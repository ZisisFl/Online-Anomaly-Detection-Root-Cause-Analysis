import json

from pandas import DataFrame, read_sql
from typing import Iterator, Dict, Optional

from sqlalchemy.engine import Engine
from sqlalchemy import create_engine

from dotenv import load_dotenv
from os import getenv

from tqdm import tqdm

from confluent_kafka import SerializingProducer
from json_serializer import JSONSerializer

class PostgresToDataFrameGenerator:
    def __init__(self, table_name: str, batch_size: int) -> None:
        self.table_name = table_name
        self.batch_size = batch_size

        self.query = self._construct_query()
        self.engine = self._create_engine()
        self.chunks_count = self._count_chunks()

    def _create_engine(self) -> Engine:
        load_dotenv()

        db_host = getenv('DB_HOST')
        db_port = getenv('DB_PORT')
        db_user = getenv('DB_USER')
        db_pass = getenv('DB_PASS')
        db_name = getenv('DB_NAME')

        connection_url = f'postgresql://{db_user}:{db_pass}@{db_host}:{db_port}/{db_name}'

        return create_engine(connection_url, pool_pre_ping=True)
    
    def _construct_query(self) -> str:
        query = F'''SELECT * FROM {self.table_name}'''

        return query

    def _count_chunks(self) -> int:
        query = f'SELECT COUNT(*)/{float(self.batch_size)} FROM ({self.query}) t'
        try:
            result = self.engine.execute(query)
        except Exception as err:
            raise err
        
        return int(result.fetchone()[0])
    
    def get_pandas_generator(self) -> Iterator[DataFrame]:
        

        print(f'Retrieving data from table {self.table_name} in {self.chunks_count} chunks')

        try:
            return read_sql(self.query, self.engine, chunksize=self.batch_size)
        except Exception as err:
            raise err

class KafkaProducerConf:
    def __init__(self) -> None:
        self.kafka_conf = self._construct_kafka_conf()

    def _construct_kafka_conf(self) -> Dict[str, Optional[str]]:
        load_dotenv()

        kafka_producer_conf = {'bootstrap.servers': getenv('KAFKA_BROKERS')}

        kafka_producer_conf.update({'value.serializer': JSONSerializer()})

        return kafka_producer_conf

    def create_producer(self) -> SerializingProducer:
        return SerializingProducer(self.kafka_conf)


class DataFrameToJsonProduce:
    def __init__(
        self,
        topic_name: str,
        producer: SerializingProducer,
        df_generator: Iterator[DataFrame]
        ) -> None:

        self.topic_name = topic_name
        self.producer = producer
        self.df_generator = df_generator

    def _df_to_dict(self, input_df: DataFrame): #-> Dict[str, Any]:
        return input_df.to_dict(orient='records')

    def produce(self):
        for chunk_index, chunk in enumerate(self.df_generator):
            print(f'Producing records of chunk {chunk_index}')
            for record in tqdm(self._df_to_dict(chunk)):
                self.producer.produce(
                    topic=self.topic_name,
                    value=record)
            
            self.producer.flush()


if __name__=='__main__':
    postgres_df_generator = PostgresToDataFrameGenerator('store_sales', 5000)

    kafka_producer = KafkaProducerConf().create_producer()

    DataFrameToJsonProduce(
        topic_name='test1',
        producer=kafka_producer,
        df_generator=postgres_df_generator.get_pandas_generator()).produce()
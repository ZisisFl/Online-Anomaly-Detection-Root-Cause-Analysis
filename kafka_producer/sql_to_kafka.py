from pandas import DataFrame, read_sql
from sqlalchemy.engine import Engine
from sqlalchemy import create_engine
from numpy import nan

from confluent_kafka import SerializingProducer
from json_serializer import JSONSerializer

from typing import Iterator, Dict, List, Optional, Any

from dotenv import load_dotenv
from os import getenv
from tqdm import tqdm

import argparse


def parse_command_line_arguments() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description='SQL to Kafka producer')

    parser.add_argument(
        '--table_name',
        help='SQL table name to fetch data from',
        type=str,
        required=True
        )
    parser.add_argument(
        '--topic_name',
        help='Kafka topic name to produce records to',
        type=str,
        required=True
        )
    parser.add_argument(
        '--batch_size',
        help='Size of each batch to be sent to Kafka',
        type=int,
        required=True
        )
    parser.add_argument(
        '--limit',
        help='Limit number of records to sent to Kafka (over all batches)',
        type=int,
        required=True
        )
    
    args = parser.parse_args()

    return args

class SQLToDataFrameGenerator:
    def __init__(self, table_name: str, batch_size: int, limit: Optional[int]) -> None:
        self.table_name = table_name
        self.batch_size = batch_size
        self.limit = limit

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
        # TODO generalize this
        if self.limit is None:
            query = F'''SELECT * FROM {self.table_name} ORDER BY sale_at'''
        else:
            query = F'''SELECT * FROM {self.table_name} ORDER BY sale_at LIMIT {self.limit}'''

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
        df: DataFrame
        ) -> None:

        self.topic_name = topic_name
        self.producer = producer
        self.df = df

    def _df_to_dict(self, input_df: DataFrame) -> List[Dict[str, Any]]:
        input_df = input_df.replace({nan: None})
        return input_df.to_dict(orient='records')

    def produce(self):
        for record in tqdm(self._df_to_dict(self.df)):
            self.producer.produce(
                topic=self.topic_name,
                value=record)
        
        self.producer.flush()

def main():
    args = parse_command_line_arguments()

    sql_df_generator = SQLToDataFrameGenerator(
        args.table_name,
        args.batch_size,
        args.limit
        ).get_pandas_generator()

    kafka_producer = KafkaProducerConf().create_producer()

    for chunk_index, chunk in enumerate(sql_df_generator):
        print(f'Producing records of chunk {chunk_index}')

        DataFrameToJsonProduce(
            topic_name=args.topic_name,
            producer=kafka_producer,
            df=chunk
            ).produce()

if __name__=='__main__':
    main()
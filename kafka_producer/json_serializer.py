import json
from io import BytesIO
from datetime import datetime
from confluent_kafka.serialization import Serializer


class DateTimeEncoder(json.JSONEncoder):
    def default(self, o):
        if isinstance(o, datetime):
            return o.isoformat()

        return super().default(self, o)


class _ContextStringIO(BytesIO):
    """
    Wrapper to allow use of StringIO via 'with' constructs.

    """

    def __enter__(self):
        return self

    def __exit__(self, *args):
        self.close()
        return False


class JSONSerializer(Serializer):
    """Simplified version of JSONSerializer
    """
    
    __slots__ = ['_to_dict']

    def __init__(self, to_dict=None):

        if to_dict is not None and not callable(to_dict):
            raise ValueError("to_dict must be callable with the signature"
                             " to_dict(object, SerializationContext)->dict")

        self._to_dict = to_dict

    def __call__(self, obj, ctx):
        """
        Serializes an object to the Confluent Schema Registry's JSON binary
        format.

        Args:
            obj (object): object instance to serialize.

            ctx (SerializationContext): Metadata pertaining to the serialization
                operation.

        Note:
            None objects are represented as Kafka Null.

        Raises:
            SerializerError if any error occurs serializing obj

        Returns:
            bytes: Confluent Schema Registry formatted JSON bytes

        """
        if obj is None:
            return None

        if self._to_dict is not None:
            value = self._to_dict(obj, ctx)
        else:
            value = obj

        with _ContextStringIO() as fo:
            # JSON dump always writes a str never bytes
            # https://docs.python.org/3/library/json.html
            fo.write(json.dumps(value, cls=DateTimeEncoder).encode('utf8'))

            return fo.getvalue()
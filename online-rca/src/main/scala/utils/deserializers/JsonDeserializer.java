package utils.deserializers;

import models.SaleRecord;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

import static org.apache.flink.api.java.typeutils.TypeExtractor.getForClass;

public class JsonDeserializer implements DeserializationSchema<SaleRecord> {

    private static final long serialVersionUID = 1509391548173891955L;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public JsonDeserializer() {}


    @Override
    public TypeInformation<SaleRecord> getProducedType() {
        return getForClass(SaleRecord.class);
    }

    @Override
    public SaleRecord deserialize(byte[] message) throws IOException {
        if (message != null) {
            return objectMapper.readValue(message, SaleRecord.class);
        }
        else {
            return null;
        }
    }

    @Override
    public boolean isEndOfStream(SaleRecord nextElement) {
        return false;
    }
}

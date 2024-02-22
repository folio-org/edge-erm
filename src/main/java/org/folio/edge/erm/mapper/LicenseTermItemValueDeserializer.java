package org.folio.edge.erm.mapper;


import org.folio.edge.erm.mapper.model.LicenseTermItemValueWrapper;
import org.folio.erm.domain.dto.LicenseTermItemValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;

public class LicenseTermItemValueDeserializer extends JsonDeserializer<LicenseTermItemValue> {
  
  @Override
  public LicenseTermItemValue deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    ObjectCodec oc = p.getCodec();
    JsonNode node = oc.readTree(p);
    var objectMapper = (ObjectMapper) oc;

    if (node.isTextual()) {
      return new LicenseTermItemValueWrapper<>(node.asText());
    }

    if (node.isObject()) {
      var obj = objectMapper.convertValue(node, Object.class);
      return new LicenseTermItemValueWrapper<>(obj);
    }

    if (node.isArray()) {
      var list = objectMapper.convertValue(node, List.class);
      return new LicenseTermItemValueWrapper<>(list);
    }

    if (node.isDouble()) {
      return new LicenseTermItemValueWrapper<>(node.asDouble());
    }

    if (node.isNumber()) {
      return new LicenseTermItemValueWrapper<>(node.asInt());
    }

    return null;
  }

}

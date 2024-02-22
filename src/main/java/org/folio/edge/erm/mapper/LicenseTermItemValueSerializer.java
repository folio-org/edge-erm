package org.folio.edge.erm.mapper;

import org.folio.edge.erm.mapper.model.LicenseTermItemValueWrapper;
import org.folio.erm.domain.dto.LicenseTermItemValue;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.List;

public class LicenseTermItemValueSerializer extends JsonSerializer<LicenseTermItemValue> {

  @Override
  public void serialize(LicenseTermItemValue bean, JsonGenerator gen,
      SerializerProvider serializers) throws IOException {
    if (bean instanceof LicenseTermItemValueWrapper) {
      var wrapper = (LicenseTermItemValueWrapper<?>) bean;
      Object value = wrapper.getValue();
      
      if (value instanceof String) {
        gen.writeString((String) value);
        return;
      }
      
      if (value instanceof List) {
        var list = (List) value; 
        gen.writeStartArray();
        for (Object item : list) {
          gen.writeObject(item);
        }
        gen.writeEndArray();
        return;
      }
      
      if (value != null) {
        gen.writeObject(value);
      }
    }
  }
}

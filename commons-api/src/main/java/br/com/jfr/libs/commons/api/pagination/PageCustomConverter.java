package br.com.jfr.libs.commons.api.pagination;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

@JsonComponent
public class PageCustomConverter {

  public static class PageCustomSerializer extends JsonSerializer<Page<?>> {
    @Override
    public void serialize(Page<?> page, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
      gen.writeStartObject();
      gen.writeObjectField("items", page.getContent());
      gen.writeBooleanField("first", page.isFirst());
      gen.writeBooleanField("last", page.isLast());
      gen.writeNumberField("totalPages", page.getTotalPages());
      gen.writeNumberField("totalItems", page.getTotalElements());
      gen.writeNumberField("currentItemCount", page.getNumberOfElements());
      gen.writeNumberField("itemsPerPage", page.getSize());
      gen.writeNumberField("pageIndex", page.getNumber() + 1);

      Sort sort = page.getSort();

      gen.writeArrayFieldStart("sort");

      for (Sort.Order order : sort) {
        gen.writeStartObject();
        gen.writeStringField("property", order.getProperty());
        gen.writeStringField("direction", order.getDirection().name());
        gen.writeBooleanField("ignoreCase", order.isIgnoreCase());
        gen.writeStringField("nullHandling", order.getNullHandling().name());
        gen.writeEndObject();
      }

      gen.writeEndArray();
      gen.writeEndObject();
    }
  }

}

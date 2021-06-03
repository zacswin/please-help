package entity;

import java.util.stream.Stream;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

// https://www.baeldung.com/jpa-attribute-converters
@Converter
public class TicketStatusConverter implements AttributeConverter<TicketStatus, String> {

    @Override
    public String convertToDatabaseColumn(TicketStatus status) {
        return status.toString();
    }

    @Override
    public TicketStatus convertToEntityAttribute(String dbStatus) {

        return Stream.of(TicketStatus.values())
                .filter(c -> c.toString().equals(dbStatus))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}

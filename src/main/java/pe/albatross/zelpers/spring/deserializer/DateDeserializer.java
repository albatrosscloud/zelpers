package pe.albatross.zelpers.spring.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.util.StringUtils;

public class DateDeserializer extends StdDeserializer<Date> {

    private static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    private static SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");

    public DateDeserializer() {
        this(null);
    }

    public DateDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Date deserialize(JsonParser jsonparser, DeserializationContext context)
            throws IOException {

        String date = jsonparser.getText();
        if (StringUtils.isEmpty(date)) {
            return null;
        }

        String error = null;

        try {

            return formatter.parse(date);

        } catch (ParseException e) {
        }

        try {

            return formatter2.parse(date);

        } catch (ParseException e) {
        }

        try {

            return new Date(Long.parseLong(date));

        } catch (NumberFormatException e) {
        }

        
        throw new RuntimeException(String.format("Error DateDeserializer, %s no pudo ser parseado", date));
    }

}

package pe.albatross.zelpers.spring.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.springframework.util.StringUtils;

public class DateTimeDeserializer extends StdDeserializer<Date> {

    private static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static SimpleDateFormat formatter3 = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat formatter4 = new SimpleDateFormat("dd/MM/yyyy");

    public DateTimeDeserializer() {
        this(null);
    }

    public DateTimeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Date deserialize(
            JsonParser jsonparser, DeserializationContext context)
            throws IOException {

        String date = jsonparser.getText();

        if (StringUtils.isEmpty(date)) {
            return null;
        }

        List<SimpleDateFormat> formatos = Arrays.asList(formatter, formatter2, formatter3, formatter4);

        for (SimpleDateFormat formato : formatos) {
            try {

                return formato.parse(date);

            } catch (ParseException e) {
            }
        }

        throw new RuntimeException(String.format("Error DateTimeDeserializer, %s no pudo ser parseado", date));
    }

}

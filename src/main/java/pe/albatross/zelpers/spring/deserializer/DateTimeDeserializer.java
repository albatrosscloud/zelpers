package pe.albatross.zelpers.spring.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeDeserializer extends StdDeserializer<Date> {

    private static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
        if (org.apache.commons.lang3.StringUtils.isEmpty(date)) {
            return null;
        }

        boolean firstFormattter = true;
        String error = null;
        Date dateResult = null;

        try {
            dateResult = formatter.parse(date);
        } catch (ParseException e) {
            firstFormattter = false;
            error = e.getLocalizedMessage();
        }
        if (!firstFormattter) {
            error = null;
            try {
                dateResult = formatter2.parse(date);
            } catch (ParseException e) {
                error = e.getLocalizedMessage();
            }
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(error)) {
            throw new RuntimeException(error);
        }
        return dateResult;
    }

}

package pe.albatross.zelpers.miscelanea;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class Assert_isNull_Test {

    @Test
    public void assert_isnull_ok() {
        String data = null;
        Assert.isNull(data, "Error, el valor no es NULL");
    }

    @Test
    public void assert_isnull_error() {
        String data = "algo";
        Exception exception = assertThrows(PhobosException.class, () -> {
            Assert.isNull(data, "Error, el valor no es NULL");
        });

        String expectedMessage = "Error, el valor no es NULL";
        String actualMessage = exception.getMessage();
        log.info("Assert.isNull(data) data=[{}] message=[{}]", data, actualMessage);

        assertEquals(actualMessage, expectedMessage);
    }

}

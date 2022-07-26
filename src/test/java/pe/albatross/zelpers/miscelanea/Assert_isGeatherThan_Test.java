package pe.albatross.zelpers.miscelanea;

import java.math.BigDecimal;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import pe.albatross.zelpers.helpers.Pais;

@Slf4j
public class Assert_isGeatherThan_Test {

    @Test
    public void assert_isGeatherThan_nulls_error() {
        String data1 = null;
        String data2 = null;

        Exception exception = assertThrows(PhobosException.class, () -> {
            Assert.isGeatherThan(data1, data2, "Error, estos datos son iguales");
        });

        String expectedMessage = "Error, estos datos son iguales";
        String actualMessage = exception.getMessage();
        log.info("Assert.isGeatherThan(data1,data2) data1=[{}] data2=[{}] message=[{}]", data1, data2, actualMessage);

        assertEquals(actualMessage, expectedMessage);
    }

    @Test
    public void assert_isGeatherThan_notnulls_error() {
        String data1 = "algo1";
        String data2 = "algo1";

        Exception exception = assertThrows(PhobosException.class, () -> {
            Assert.isGeatherThan(data1, data2, "Error, estos datos son iguales");
        });

        String expectedMessage = "Error, estos datos son iguales";
        String actualMessage = exception.getMessage();
        log.info("Assert.isGeatherThan(data1,data2) data1=[{}] data2=[{}] message=[{}]", data1, data2, actualMessage);

        assertEquals(actualMessage, expectedMessage);
    }

    @Test
    public void assert_isGeatherThan_notnulls_numbers1_error() {
        Integer data1 = 100;
        Long data2 = 100L;

        Exception exception = assertThrows(PhobosException.class, () -> {
            Assert.isGeatherThan(data1, data2, "Error, estos datos son iguales");
        });

        String expectedMessage = "Error, estos datos son iguales";
        String actualMessage = exception.getMessage();
        log.info("Assert.isGeatherThan(data1,data2) data1=[{}] data2=[{}] message=[{}]", data1, data2, actualMessage);

        assertEquals(actualMessage, expectedMessage);
    }

    @Test
    public void assert_isGeatherThan_notnulls_numbers2_error() {
        Integer data1 = 7878;
        BigDecimal data2 = new BigDecimal("7878");

        Exception exception = assertThrows(PhobosException.class, () -> {
            Assert.isGeatherThan(data1, data2, "Error, estos datos son iguales");
        });

        String expectedMessage = "Error, estos datos son iguales";
        String actualMessage = exception.getMessage();
        log.info("Assert.isGeatherThan(data1,data2) data1=[{}] data2=[{}] message=[{}]", data1, data2, actualMessage);

        assertEquals(actualMessage, expectedMessage);
    }

    @Test
    public void assert_isGeatherThan_notnulls_numbers3_error() {
        Double data1 = -783.56;
        BigDecimal data2 = new BigDecimal("-783.56");

        Exception exception = assertThrows(PhobosException.class, () -> {
            Assert.isGeatherThan(data1, data2, "Error, estos datos son iguales");
        });

        String expectedMessage = "Error, estos datos son iguales";
        String actualMessage = exception.getMessage();
        log.info("Assert.isGeatherThan(data1,data2) data1=[{}] data2=[{}] message=[{}]", data1, data2, actualMessage);

        assertEquals(actualMessage, expectedMessage);
    }

    @Test
    public void assert_isGeatherThan_notnulls_dates1_error() {
        Date data1 = new Date();
        Date data2 = new Date();

        Exception exception = assertThrows(PhobosException.class, () -> {
            Assert.isGeatherThan(data1, data2, "Error, estos datos son iguales");
        });

        String expectedMessage = "Error, estos datos son iguales";
        String actualMessage = exception.getMessage();
        log.info("Assert.isGeatherThan(data1,data2) data1=[{}] data2=[{}] message=[{}]", data1, data2, actualMessage);

        assertEquals(actualMessage, expectedMessage);
    }

    @Test
    public void assert_isGeatherThan_notnulls_dates2_error() {
        Date data1 = new Date();
        Date data2 = new DateTime().plusHours(6).toDate();

        Exception exception = assertThrows(PhobosException.class, () -> {
            Assert.isGeatherThan(data1, data2, "Error, estos datos son iguales");
        });

        String expectedMessage = "Error, estos datos son iguales";
        String actualMessage = exception.getMessage();
        log.info("Assert.isGeatherThan(data1,data2) data1=[{}] data2=[{}] message=[{}]", data1, data2, actualMessage);

        assertEquals(actualMessage, expectedMessage);
    }

    @Test
    public void assert_isGeatherThan_notnulls_dates3_ok() {
        Date data1 = new DateTime().plusHours(6).toDate();
        Date data2 = new Date();

        Assert.isGeatherThan(data1, data2, "Error, estos datos son iguales");
        log.info("OK :::: Assert.isGeatherThan(data1,data2) data1=[{}] data2=[{}]", data1, data2);
    }

    @Test
    public void assert_isGeatherThan_null_notNull_error() {
        String data1 = null;
        String data2 = "algo";

        Exception exception = assertThrows(PhobosException.class, () -> {
            Assert.isGeatherThan(data1, data2, "Error, estos datos son iguales");
        });

        String expectedMessage = "Error, estos datos son iguales";
        String actualMessage = exception.getMessage();
        log.info("Assert.isGeatherThan(data1,data2) data1=[{}] data2=[{}] message=[{}]", data1, data2, actualMessage);

        assertEquals(actualMessage, expectedMessage);
    }

    @Test
    public void assert_isGeatherThan_notNull_null_ok() {
        String data1 = "nada";
        String data2 = null;

        Assert.isGeatherThan(data1, data2, "Error, estos datos son iguales");
        log.info("OK :::: Assert.isGeatherThan(data1,data2) data1=[{}] data2=[{}]", data1, data2);
    }

    @Test
    public void assert_isGeatherThan_string_error() {
        String data1 = "aaaa";
        String data2 = "zzzz";

        Exception exception = assertThrows(PhobosException.class, () -> {
            Assert.isGeatherThan(data1, data2, "Error, estos datos son iguales");
        });

        String expectedMessage = "Error, estos datos son iguales";
        String actualMessage = exception.getMessage();
        log.info("Assert.isGeatherThan(data1,data2) data1=[{}] data2=[{}] message=[{}]", data1, data2, actualMessage);

        assertEquals(actualMessage, expectedMessage);
    }

    @Test
    public void assert_isGeatherThan_string_ok() {
        String data1 = "zzzz";
        String data2 = "aaaa";

        Assert.isGeatherThan(data1, data2, "Error, estos datos son iguales");
        log.info("OK :::: Assert.isGeatherThan(data1,data2) data1=[{}] data2=[{}]", data1, data2);
    }

    @Test
    @DisplayName("verificando igualdad de objetos a nivel de datos, para clases que tienen toString override")
    public void assert_isGeatherThan_dtos_error() {
        Pais data1 = new Pais();
        data1.setId(34L);

        Pais data2 = new Pais();
        data2.setId(34L);

        log.info("pais1={}", data1);
        log.info("pais2={}", data2);

        Exception exception = assertThrows(PhobosException.class, () -> {
            Assert.isGeatherThan(data1, data2, "Error, estos datos son iguales");
        });

        String actualMessage = exception.getMessage();
        log.info("Assert.isGeatherThan(data1,data2) data1=[{}] data2=[{}] message=[{}]", data1, data2, actualMessage);

        assertEquals(Assert.INCOMPATIBLE_CLASSES, actualMessage);
    }

}

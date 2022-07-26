package pe.albatross.zelpers.miscelanea;

import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import pe.albatross.zelpers.helpers.Ciudad;
import pe.albatross.zelpers.helpers.Pais;

@Slf4j
public class Assert_isNotEqual_Test {

    @Test
    public void assert_notEqual_nulls_error() {
        String data1 = null;
        String data2 = null;

        Exception exception = assertThrows(PhobosException.class, () -> {
            Assert.isNotEqual(data1, data2, "Error, estos datos son iguales");
        });

        String expectedMessage = "Error, estos datos son iguales";
        String actualMessage = exception.getMessage();
        log.info("Assert.isNotEqual(data1,data2) data1=[{}] data2=[{}] message=[{}]", data1, data2, actualMessage);

        assertEquals(actualMessage, expectedMessage);
    }

    @Test
    public void assert_notEqual_notnulls_error() {
        String data1 = "algo1";
        String data2 = "algo1";

        Exception exception = assertThrows(PhobosException.class, () -> {
            Assert.isNotEqual(data1, data2, "Error, estos datos son iguales");
        });

        String expectedMessage = "Error, estos datos son iguales";
        String actualMessage = exception.getMessage();
        log.info("Assert.isNotEqual(data1,data2) data1=[{}] data2=[{}] message=[{}]", data1, data2, actualMessage);

        assertEquals(actualMessage, expectedMessage);

    }

    @Test
    public void assert_notEqual_notnulls_numbers_error() {
        Integer data1 = 100;
        Long data2 = 100L;

        Exception exception = assertThrows(PhobosException.class, () -> {
            Assert.isNotEqual(data1, data2, "Error, estos datos son iguales");
        });

        String expectedMessage = "Error, estos datos son iguales";
        String actualMessage = exception.getMessage();
        log.info("Assert.isNotEqual(data1,data2) data1=[{}] data2=[{}] message=[{}]", data1, data2, actualMessage);

        assertEquals(actualMessage, expectedMessage);
    }

    @Test
    public void assert_notEqual_notnulls_dates_error() {
        Date data1 = new Date();
        Date data2 = new Date();

        Exception exception = assertThrows(PhobosException.class, () -> {
            Assert.isNotEqual(data1, data2, "Error, estos datos son iguales");
        });

        String expectedMessage = "Error, estos datos son iguales";
        String actualMessage = exception.getMessage();
        log.info("Assert.isNotEqual(data1,data2) data1=[{}] data2=[{}] message=[{}]", data1, data2, actualMessage);

        assertEquals(actualMessage, expectedMessage);
    }

    @Test
    public void assert_notEqual_ok1() {
        String data1 = null;
        String data2 = "algo";
        Assert.isNotEqual(data1, data2, "Error, estos datos son iguales");
        log.info("OK :::: Assert.isNotEqual(data1,data2) data1=[{}] data2=[{}]", data1, data2);
    }

    @Test
    public void assert_notEqual_ok2() {
        String data1 = "nada";
        String data2 = null;
        Assert.isNotEqual(data1, data2, "Error, estos datos son iguales");
        log.info("OK :::: Assert.isNotEqual(data1,data2) data1=[{}] data2=[{}]", data1, data2);
    }

    @Test
    public void assert_notEqual_ok3() {
        String data1 = "nada";
        String data2 = "algo";
        Assert.isNotEqual(data1, data2, "Error, estos datos son iguales");
        log.info("OK :::: Assert.isNotEqual(data1,data2) data1=[{}] data2=[{}]", data1, data2);
    }

    @Test
    @DisplayName("verificando igualdad de objetos a nivel de datos, para clases que tienen toString override")
    public void assert_notEqual_notnulls_objects_error() {
        Pais data1 = new Pais();
        data1.setId(34L);

        Pais data2 = new Pais();
        data2.setId(34L);

        log.info("pais1={}", data1);
        log.info("pais2={}", data2);

        Exception exception = assertThrows(PhobosException.class, () -> {
            Assert.isNotEqual(data1, data2, "Error, estos datos son iguales");
        });

        String expectedMessage = "Error, estos datos son iguales";
        String actualMessage = exception.getMessage();
        log.info("Assert.isNotEqual(data1,data2) data1=[{}] data2=[{}] message=[{}]", data1, data2, actualMessage);

        assertEquals(actualMessage, expectedMessage);
    }

    @Test
    @DisplayName("verificando igualdad de objetos sin considerar sus datos, para clases que NO tienen toString override")
    public void assert_notEqual_notnulls_objects_ok() {
        Ciudad data1 = new Ciudad();
        data1.setId(34L);

        Ciudad data2 = new Ciudad();
        data2.setId(34L);

        log.info("ciudad1={}", data1);
        log.info("ciudad2={}", data2);

        Assert.isNotEqual(data1, data2, "Error, estos datos son iguales");
        log.info("OK :::: Assert.isNotEqual(data1,data2) data1=[{}] data2=[{}]", data1, data2);
    }

}

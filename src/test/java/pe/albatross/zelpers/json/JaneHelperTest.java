package pe.albatross.zelpers.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.platform.runner.JUnitPlatform;
import pe.albatross.zelpers.helpers.Ciudad;
import pe.albatross.zelpers.helpers.Pais;
import pe.albatross.zelpers.helpers.Persona;
import pe.albatross.zelpers.helpers.TagsEnum;

@Slf4j
@RunWith(JUnitPlatform.class)
public class JaneHelperTest {

    private static Persona pepe = null;
    private final static Map<String, Pais> paises = new HashMap();
    private final static Map<String, Ciudad> ciudades = new HashMap();
    private final static ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void init() {

        Pais peru = new Pais(1l, "Perú");

        paises.put("peru", peru);
        paises.put("usa", new Pais(2l, "USA"));
        paises.put("brazil", new Pais(3l, "Brazil"));

        Ciudad lima = new Ciudad("Lima", peru);

        ciudades.put("lima", lima);
        ciudades.put("paris", new Ciudad("Paris"));
        ciudades.put("newyork", new Ciudad("New York"));

        pepe = new Persona("Pepe");
        pepe.setPaisVacaciones(new Pais(4l, "Francia"));
        pepe.setCiudad(lima);

    }

    @Test
    @Order(100)
    public void todo() {
        try {

            ObjectNode json = JaneHelper.from(paises.get("brazil")).json();
            log.info("todo {}", json.toString());

            Pais brazil = objectMapper.readValue(json.toString(), Pais.class);

            assertAll("pais",
                    () -> assertEquals(3, brazil.getId()),
                    () -> assertEquals("Brazil", brazil.getNombre())
            );

        } catch (JsonProcessingException e) {
            fail();
        }

    }

    @Test
    @Order(200)
    public void algunosAtributos() {
        try {
            ObjectNode json = JaneHelper.from(paises.get("usa"))
                    .only("id,nombre")
                    .json();

            log.info("algunosAtributos {}", json.toString());

            Pais usa = objectMapper.readValue(json.toString(), Pais.class);

            assertAll("pais",
                    () -> assertEquals(2, usa.getId()),
                    () -> assertEquals("USA", usa.getNombre())
            );

        } catch (JsonProcessingException e) {
            fail();
        }
    }

    @Test
    @Order(300)
    public void algunosAtributosSinNulos() {
        try {
            ObjectNode json = JaneHelper.from(ciudades.get("lima"), false)
                    .only("id, nombre")
                    .json();

            log.info("algunosAtributosSinNulos {}", json.toString());

            Ciudad lima = objectMapper.readValue(json.toString(), Ciudad.class);

            assertAll("ciudad",
                    () -> assertNull(lima.getId()),
                    () -> assertEquals("Lima", lima.getNombre())
            );

        } catch (JsonProcessingException e) {
            fail();
        }
    }

    @Test
    @Order(350)
    public void atributosListas() {

        Pais peru = paises.get("peru");
        peru.setCiudades(new ArrayList(ciudades.values()));
        peru.setSitiosTuristicos(Arrays.asList("Selva", "Costa", "Sierra"));
        peru.setCiudadesMap(ciudades);
        peru.setCapital(ciudades.get("lima"));

        Persona dua = new Persona("Dua");
        dua.setCiudad(ciudades.get("lima"));
        dua.setPaisVacaciones(peru);

        ObjectNode json = JaneHelper.from(dua)
                .only("nombres")
                .join("ciudad")
                .join("paisVacaciones")
                .join("paisVacaciones.capital")
                .include("paisVacaciones.sitiosTuristicos")
                .include("paisVacaciones.ciudadesMap ")
                .include("paisVacaciones.ciudades")
                .json();

        log.info("atributosListas {}", json.toPrettyString());

    }

    @Test
    @Order(400)
    public void incluyePadres() {
        try {
            ObjectNode json = JaneHelper.from(pepe)
                    .parents("ciudad", "paisVacaciones")
                    .json();

            log.info("incluyePadres {}", json.toString());

            Persona pepe = objectMapper.readValue(json.toString(), Persona.class);

            assertAll("pepe",
                    () -> assertNull(pepe.getCiudad().getId()),
                    () -> assertEquals(4, pepe.getPaisVacaciones().getId()),
                    () -> assertEquals("Francia", pepe.getPaisVacaciones().getNombre())
            );

        } catch (JsonProcessingException e) {
            fail();
        }
    }

    @Test
    @Order(500)
    public void joinAlgunosAtributos() {
        try {
            ObjectNode json = JaneHelper.from(pepe)
                    .join("ciudad", "id,nombre")
                    .join("ciudad.pais", "nombre")
                    .json();

            log.info("joinAlgunosAtributos {}", json.toString());

            Persona pepe = objectMapper.readValue(json.toString(), Persona.class);

            assertAll("pepe",
                    () -> assertEquals("Lima", pepe.getCiudad().getNombre()),
                    () -> assertEquals("Perú", pepe.getCiudad().getPais().getNombre())
            );

        } catch (JsonProcessingException e) {
            fail();
        }
    }

    @Test
    @Order(600)
    @Disabled("Este caso no esta implementado")
    public void joinSinNulos() {

        // Marca no nulos y al incluir parents/join
        // no debe incluir nulos del padre
    }

    @Test
    @Order(700)
    public void joinIgnoraError() {
        try {
            ObjectNode json = JaneHelper.from(pepe)
                    .join("pais", "id, nombres")
                    .json();

            log.info("joinIgnoraError {}", json.toString());

            Persona pepe = objectMapper.readValue(json.toString(), Persona.class);

            assertAll("pepe",
                    () -> assertEquals("Pepe", pepe.getNombres()),
                    () -> assertNull(pepe.getCiudad()),
                    () -> assertNull(pepe.getPaisVacaciones())
            );

        } catch (JsonProcessingException e) {
            fail();
        }

    }

    @Test
    @Disabled
    public void joinDirectoFather() {
        ObjectNode json = JaneHelper.from(pepe)
                .join("ciudad.pais", "id, nombre")
                .json();

        // {..., ciudad:{pais:{id:x, nombres:''}} ...}
        log.info(json.toPrettyString());
    }

    @Test
    @Disabled
    public void joinDirectoFatherAlias() {
        ObjectNode json = JaneHelper.from(pepe)
                .join("ciudad.pais country", "id, nombre", true)
                .json();

        // {..., ciudad:{country:{id:x, nombres:''}} ...}
        log.info(json.toPrettyString());
    }

    @Test
    @Disabled
    public void complexJoin() {
        ObjectNode json = JaneHelper.from(pepe)
                .only("nombres,paterno")
                .join("ciudad", "id, nombre")
                .join("ciudad.pais", "id,codigo")
                .json();

        //{..., ciudad:{id:x, nombres:'', pais{id:x, nombre:''}} ...}
        log.info(json.toPrettyString());

    }

    @Test
    @Disabled
    public void complexJoinTwo() {
        ObjectNode json = JaneHelper.from(pepe)
                .only("nombres,paterno")
                .join("ciudad city", "id, nombre")
                .join("ciudad.pais", "id, codigo", true) // TRUE es AttachToRoot (Por defecto false)
                .join("ciudad.pais country", "id, codigo", true) // TRUE es AttachToRoot (Por defecto false)
                .join("ciudad.pais", "id, codigo") // TRUE es AttachToRoot (Por defecto false)
                .json();

        // El JSON tendrá la siguiente composición 
        // {... pais:{id:x, codigo:''}}
        log.info(json.toPrettyString());

    }

    @Test
    @Disabled
    public void listObjects() {
        List<Persona> pepes = Arrays.asList(pepe, pepe);

        ArrayNode json1 = JaneHelper.from(pepes)
                .only("nombres,paterno")
                .join("ciudad city", "id, nombre")
                .join("ciudad.pais", "id,codigo")
                .array();

        log.info(json1.toPrettyString());
    }

    @Test
    @Disabled
    public void arrayObjects() {
        Object[] pipos = new Object[]{pepe, pepe, pepe};

        ArrayNode json2 = JaneHelper.from(pipos)
                .only("nombres,paterno")
                .join("ciudad city", "id, nombre")
                .join("ciudad.pais", "id,codigo")
                .array();

        log.info(json2.toPrettyString());

    }

    @Test
    @Disabled
    public void arraysWrappersPrimitivos() {

        List<Date> dates = Arrays.asList(new Date(), new LocalDate().toDate(), new Date(), new Date());
        ArrayNode json1 = JaneHelper.from(dates).array();
        log.info(json1.toPrettyString());

        List<String> strings = Arrays.asList("azul", "verde", "amarillo");
        ArrayNode json2 = JaneHelper.from(strings).array();
        log.info(json2.toPrettyString());

        List<Long> enteres = Arrays.asList(12L, 34L, 18L);
        ArrayNode json3 = JaneHelper.from(enteres).array();
        log.info(json3.toPrettyString());

        ArrayNode json4 = JaneHelper.from("papu:::pepe:::pipo".split(":::")).array();
        log.info(json4.toPrettyString());
    }

    @Test
    @Disabled
    public void complexMap() {
        Map pepes = new HashMap();
        pepes.put("pepe", pepe);
        pepes.put("papo", pepe);

        ObjectNode json = JaneHelper.from(pepes)
                .only("nombres,paterno")
                .join("ciudad", "id, nombre")
                .join("ciudad.pais", "id,codigo")
                .join("ciudad.pais patria", "id,codigo")
                .join("ciudad.pais country", "id,codigo", true)
                .json();

        log.info(json.toPrettyString());
    }

    @Test
    @Disabled
    public void enumToJSON() {

        ArrayNode json = JaneHelper.from(TagsEnum.values()).array();
        log.info(json.toPrettyString());
    }

}

package pe.albatross.zelpers.json;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import pe.albatross.zelpers.helpers.Ciudad;
import pe.albatross.zelpers.helpers.Pais;
import pe.albatross.zelpers.helpers.Persona;
import pe.albatross.zelpers.helpers.RecursoNatural;
import pe.albatross.zelpers.helpers.RiquezaPais;

@Slf4j
public class JaneHelper_test_functions {

    @Test
    public void from_test() {
        Persona pepe = new Persona();
        pepe.setId(34L);
        pepe.setNombres("Juan Figueroa");

        {
            ObjectNode node = JaneHelper.from(pepe).json();
            Assert.assertEquals("{\"id\":34,\"nombres\":\"Juan Figueroa\"}", node.toString());
            System.out.println(node.toString());
        }

        List<Persona> pepes = new ArrayList();
        pepes.add(pepe);

        {
            ArrayNode array = JaneHelper.from(pepes).array();
            Assert.assertEquals("[{\"id\":34,\"nombres\":\"Juan Figueroa\"}]", array.toString());
            System.out.println(array.toString());
        }
    }

    @Test
    public void from_only_test() {
        RecursoNatural pepe = new RecursoNatural();
        pepe.setId("R34ML");
        pepe.setCodigo("2333");

        ObjectNode node = JaneHelper.from(pepe).only("id,codigo").json();
        Assert.assertEquals("{\"id\":\"R34ML\",\"codigo\":\"2333\"}", node.toString());
        System.out.println(node.toString());
    }

    @Test
    public void from_only_twotimes_test() {
        RecursoNatural recurso = new RecursoNatural();
        recurso.setId("R34ML");
        recurso.setCodigo("2333");
        recurso.setNombre("Oro");
        recurso.setTipo("Minerales");

        {
            ObjectNode node = JaneHelper
                    .from(recurso)
                    .only("id,codigo")
                    .only("nombre,tipo")
                    .json();

            Assert.assertEquals("{\"codigo\":\"2333\",\"id\":\"R34ML\",\"nombre\":\"Oro\",\"tipo\":\"Minerales\"}", node.toString());
            System.out.println(node.toString());
        }

        List<RecursoNatural> recursos = new ArrayList();
        recursos.add(recurso);

        {
            ArrayNode array = JaneHelper
                    .from(recursos)
                    .only("id,codigo")
                    .only("nombre,tipo")
                    .array();

            Assert.assertEquals("[{\"codigo\":\"2333\",\"id\":\"R34ML\",\"nombre\":\"Oro\",\"tipo\":\"Minerales\"}]", array.toString());
            System.out.println(array.toString());
        }
    }

    @Test
    public void join_attrs_test() {
        RiquezaPais riqueza = new RiquezaPais();
        riqueza.setId(345L);
        riqueza.setCantidadMinas(23);
        riqueza.setRecursoNatural(new RecursoNatural());
        riqueza.getRecursoNatural().setId("0934835");
        riqueza.getRecursoNatural().setCodigo("AU-PURO");
        riqueza.getRecursoNatural().setNombre("Oro");
        riqueza.getRecursoNatural().setMaterial("Mineral");

        {
            ObjectNode node = JaneHelper
                    .from(riqueza)
                    .only("id")
                    .join("recursoNatural", "id,codigo,material")
                    .json();

            Assert.assertEquals("{\"id\":345,\"recursoNatural\":{\"id\":\"0934835\",\"codigo\":\"AU-PURO\",\"material\":\"Mineral\"}}", node.toString());
            System.out.println(node.toString());
        }
    }

    @Test
    public void join_attrs_twotimes_test() {
        RiquezaPais riqueza = new RiquezaPais();
        riqueza.setId(345L);
        riqueza.setCantidadMinas(23);

        riqueza.setRecursoNatural(new RecursoNatural());
        riqueza.getRecursoNatural().setId("0934835");
        riqueza.getRecursoNatural().setCodigo("AU-PURO");
        riqueza.getRecursoNatural().setNombre("Oro");
        riqueza.getRecursoNatural().setMaterial("Mineral");

        riqueza.setPais(new Pais());
        riqueza.getPais().setCapital(new Ciudad("Lima"));
        riqueza.getPais().getCapital().setId(767L);

        {
            ObjectNode node = JaneHelper
                    .from(riqueza)
                    .only("id")
                    .join("recursoNatural", "id,material")
                    .join("recursoNatural", "codigo,nombre")
                    .json();

            Assert.assertEquals("{\"id\":345,\"recursoNatural\":{\"codigo\":\"AU-PURO\",\"id\":\"0934835\",\"material\":\"Mineral\",\"nombre\":\"Oro\"}}", node.toString());
            System.out.println(node.toString());
        }

        {
            ObjectNode node = JaneHelper
                    .from(riqueza)
                    .only("id")
                    .join("recursoNatural", "id")
                    .join("pais.capital", "id")
                    .join("pais.capital", "nombre")
                    .json();

            Assert.assertEquals("{\"id\":345,\"recursoNatural\":{\"id\":\"0934835\"},\"pais\":{\"capital\":{\"id\":767,\"nombre\":\"Lima\"}}}", node.toString());
            System.out.println(node.toString());
        }
    }

    @Test
    public void list_join_attrs_twotimes_test() {
        List<RiquezaPais> riquezas = new ArrayList();
        {
            RiquezaPais riqueza = new RiquezaPais();
            riqueza.setId(345L);
            riqueza.setCantidadMinas(23);

            riqueza.setRecursoNatural(new RecursoNatural());
            riqueza.getRecursoNatural().setId("0934835");
            riqueza.getRecursoNatural().setCodigo("AU-PURO");
            riqueza.getRecursoNatural().setNombre("Oro");
            riqueza.getRecursoNatural().setMaterial("Mineral");

            riquezas.add(riqueza);
        }

        {
            RiquezaPais riqueza = new RiquezaPais();
            riqueza.setId(89L);
            riqueza.setCantidadMinas(13);

            riqueza.setRecursoNatural(new RecursoNatural());
            riqueza.getRecursoNatural().setId("000444");
            riqueza.getRecursoNatural().setCodigo("AG-PURO");
            riqueza.getRecursoNatural().setNombre("Plata");
            riqueza.getRecursoNatural().setMaterial("Mineral");

            riqueza.setPais(new Pais());
            riqueza.getPais().setCapital(new Ciudad("Cusco"));
            riqueza.getPais().getCapital().setId(767L);

            riquezas.add(riqueza);
        }

        {
            ArrayNode array = JaneHelper
                    .from(riquezas)
                    .only("id")
                    .join("recursoNatural", "id,nombre")
                    .join("recursoNatural", "codigo")
                    .join("pais.capital", "id")
                    .join("pais.capital", "nombre")
                    .array();

            Assert.assertEquals(
                    "[{\"id\":345,\"recursoNatural\":{\"codigo\":\"AU-PURO\",\"id\":\"0934835\",\"nombre\":\"Oro\"}},"
                    + "{\"id\":89,\"recursoNatural\":{\"codigo\":\"AG-PURO\",\"id\":\"000444\",\"nombre\":\"Plata\"},\"pais\":{\"capital\":{\"id\":767,\"nombre\":\"Cusco\"}}}]",
                    array.toString());
            System.out.println(array.toString());
        }
    }
}

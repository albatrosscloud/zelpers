package pe.albatross.zelpers.miscelanea;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import pe.albatross.zelpers.helpers.CicloAcademico;
import pe.albatross.zelpers.helpers.Ciudad;
import pe.albatross.zelpers.helpers.Pais;
import pe.albatross.zelpers.helpers.RecursoNatural;

@Slf4j
public class ObjectUtilTest {

    @Test
    @Order(100)
    public void clazz_simple() {
        Ciudad ciudad = new Ciudad();
        ciudad.setId(23L);
        ciudad.setNombre("Lima");
        ciudad.setPais(new Pais(3L, "Suiza"));

        ObjectUtil.printAttr(ciudad);
        ObjectUtil.printAttr(ciudad.getPais());
    }

    @Test
    @Order(200)
    public void clazz_metodo_grandes() {
        CicloAcademico ciclo = new CicloAcademico();
        ObjectUtil.printAttr(ciclo);

        RecursoNatural recurso = new RecursoNatural();
        ObjectUtil.printAttr(recurso);
    }
}

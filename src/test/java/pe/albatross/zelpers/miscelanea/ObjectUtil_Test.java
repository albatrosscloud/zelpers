package pe.albatross.zelpers.miscelanea;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import pe.albatross.zelpers.helpers.CicloAcademico;
import pe.albatross.zelpers.helpers.Ciudad;
import pe.albatross.zelpers.helpers.Pais;
import pe.albatross.zelpers.helpers.RecursoNatural;

@Slf4j
public class ObjectUtil_Test {

    @Test
    @Order(100)
    public void printAttr_simple() {
        Ciudad ciudad = new Ciudad();
        ciudad.setId(23L);
        ciudad.setNombre("Lima");
        ciudad.setPais(new Pais(3L, "Suiza"));

        ObjectUtil.printAttr(ciudad);
        ObjectUtil.printAttr(ciudad.getPais());
    }

    @Test
    @Order(200)
    public void printAttr_metodo_grandes() {
        CicloAcademico ciclo = new CicloAcademico();
        ObjectUtil.printAttr(ciclo);

        RecursoNatural recurso = new RecursoNatural();
        ObjectUtil.printAttr(recurso);
    }

    @Test
    @Order(300)
    public void getParentTree_method_with_params() {
        CicloAcademico ciclo = new CicloAcademico();
        ciclo.setCodigo("20221");
        ciclo.setNombre("2022-I");

        org.junit.Assert.assertNull(ObjectUtil.getParentTree(ciclo, "codigoNuevo"));
    }
}

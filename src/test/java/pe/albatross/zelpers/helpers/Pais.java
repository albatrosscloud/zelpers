package pe.albatross.zelpers.helpers;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class Pais {

    private Long id;
    private String codigo;
    private String nombre;
    
    private Ciudad capital;

    private List<Ciudad> ciudades;

    private List<String> sitiosTuristicos;

    private Map<String, Ciudad> ciudadesMap;

    public Pais(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;

    }

}

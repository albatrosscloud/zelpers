package pe.albatross.zelpers.helpers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class Ciudad {

    private Long id;

    private String nombre;

    private Pais pais;

    public Ciudad(String nombre) {
        this.nombre = nombre;
    }

    public Ciudad(String nombre, Pais pais) {
        this.nombre = nombre;
        this.pais = pais;
    }

}

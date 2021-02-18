package pe.albatross.zelpers.helpers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class Persona {

    private Long id;
    private String nombres;

    private Pais paisVacaciones;
    private Ciudad ciudad;

    public Persona(String nombres) {
        this.nombres = nombres;
    }

}

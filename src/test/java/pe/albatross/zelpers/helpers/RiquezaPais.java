package pe.albatross.zelpers.helpers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RiquezaPais {

    private Long id;
    private Integer cantidadMinas;
    private Pais pais;
    private RecursoNatural recursoNatural;
}

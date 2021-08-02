package pe.albatross.zelpers.helpers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CicloAcademico {
    private String id;
    
    private String codigo;
    private String codigoAnterior;
    private String codigoAnteriorMilitar;
    private String codigoAnteriorMilitarPacifico;
    private String codigoAnteriorMilitarPacificoMitral;
    
    private String nombre;
    private String nombreCompleto;
    private String nombreCompletoSimple;
    private String nombreCompletoSimpleDescriptivo;
}

package pe.albatross.zelpers.helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TagsEnum {

    CODIGO_SEGUIMIENTO("Código Interno para Seguimiento del Documento"),
    TIPO_DOCUMENTO("Tipo de Documento"),
    FECHA_DOCUMENTO("Fecha del Documento en formato <dd/MM/yyy>"),
    FECHA_DOCUMENTO_TEXT("Fecha del Documento en formato <dd de MMMM del yyyy>"),
    SERIE_DOCUMENTO("Serie del Documento"),
    ASUNTO("Asunto del Documento"),
    OFICINA_PROCEDENCIA("Oficina de Procedencia del Documento"),
    FIRMANTE("Nombre completo del Firmante"),
    OFICINA_DESTINO("Oficina Destino del Documento"),
    DESTINATARIO("Nombre completo del Destinatario");

    private final String descripcion;

}

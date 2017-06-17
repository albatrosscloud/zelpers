package pe.albatross.zelpers.dao;

@Deprecated
public class DaoFilter {

    private String attr;
    private String tipoCondicion;
    private Object valor;
    
    public DaoFilter(String attr, String tipoCondicion, Object valor){
        this.attr = attr;
        this.tipoCondicion = tipoCondicion;
        this.valor = valor;
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public String getTipoCondicion() {
        return tipoCondicion;
    }

    public void setTipoCondicion(String tipoCondicion) {
        this.tipoCondicion = tipoCondicion;
    }

    public Object getValor() {
        return valor;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }
    
}

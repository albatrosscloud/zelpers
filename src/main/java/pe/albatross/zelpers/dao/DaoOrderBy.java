package pe.albatross.zelpers.dao;

public class DaoOrderBy {

    private Integer tipoOrden;
    private String columna;

    public DaoOrderBy(String columna, Integer tipoOrden) {
        this.tipoOrden = tipoOrden;
        this.columna = columna;
    }

    public DaoOrderBy(String columna) {
        this.tipoOrden = SqlUtil.ORDER_ASC;
        this.columna = columna;
    }

    public Integer getTipoOrden() {
        return tipoOrden;
    }

    public void setTipoOrden(Integer tipoOrden) {
        this.tipoOrden = tipoOrden;
    }

    public String getColumna() {
        return columna;
    }

    public void setColumna(String columna) {
        this.columna = columna;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.columna);
        if (this.tipoOrden == SqlUtil.ORDER_ASC) {
            sb.append(" asc");
        } else if (this.tipoOrden == SqlUtil.ORDER_DESC) {
            sb.append(" desc");
        }

        return sb.toString();
    }
}

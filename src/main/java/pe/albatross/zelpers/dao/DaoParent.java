package pe.albatross.zelpers.dao;

@Deprecated
public class DaoParent {

    private Integer tipoJoin;
    private String name;
    private String alias;
    private SqlUtil util;

    public DaoParent(Integer tipoJoin, String father, String alias) {
        this.tipoJoin = tipoJoin;
        this.name = father;
        this.alias = alias;
    }

    public Integer getTipoJoin() {
        return tipoJoin;
    }

    public void setTipoJoin(Integer tipoJoin) {
        this.tipoJoin = tipoJoin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setUtil(SqlUtil util) {
        this.util = util;
    }

    @Override
    public String toString() {
        StringBuilder joinParent = new StringBuilder();

        if (this.util.getIsCount()) {
            if (this.tipoJoin == SqlUtil.INNER_JOIN) {
                joinParent.append(" inner join ");
            } else if (this.tipoJoin == SqlUtil.LEFT_JOIN) {
                joinParent.append(" left join ");
            } else {
                throw new RuntimeException("No se asignó un tipo de JOIN correcto a la consulta");
            }
        } else {
            if (this.tipoJoin == SqlUtil.INNER_JOIN) {
                joinParent.append(" inner join fetch ");
            } else if (this.tipoJoin == SqlUtil.LEFT_JOIN) {
                joinParent.append(" left join fetch ");
            } else {
                throw new RuntimeException("No se asignó un tipo de JOIN correcto a la consulta");
            }
        }

        if (this.name.startsWith("_")) {
            joinParent.append(this.name.substring(1)).append(" ").append(this.alias).append(" ");
        } else {
            joinParent.append(this.util.getAlias()).append(".").append(this.name).append(" ").append(this.alias).append(" ");
        }

        return joinParent.toString();
    }
}

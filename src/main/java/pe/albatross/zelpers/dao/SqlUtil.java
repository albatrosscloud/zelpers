package pe.albatross.zelpers.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.StringUtils;
import pe.albatross.zelpers.miscelanea.ObjectUtil;

@Deprecated
public class SqlUtil {

    public final static Integer INNER_JOIN = 1;
    public final static Integer LEFT_JOIN = 2;
    public final static Integer ORDER_DESC = 3;
    public final static Integer ORDER_ASC = 4;

    private final List<String> comparators = Arrays.asList("=", ">=", "<=", "<>", "!=", "like");
    private final List<String> types = Arrays.asList(
            String.class.getSimpleName(),
            Long.class.getSimpleName(),
            Integer.class.getSimpleName(),
            Double.class.getSimpleName(),
            Float.class.getSimpleName(),
            BigDecimal.class.getSimpleName(),
            Date.class.getSimpleName());

    private List<DaoParent> parents;
    private List<DaoFilter> filters;
    private List<String> conditions;
    private List<DaoFilter> filtersIn;
    private List<List<DaoFilter>> filtersOr;
    private List<DaoOrderBy> orderBys;
    private String aliasMain;
    private Integer pageSize;
    private Integer firstResult;
    private Boolean isCount;
    private Boolean isFalse;

    public SqlUtil(String aliasMain) {
        this.parents = new ArrayList();
        this.filters = new ArrayList();
        this.conditions = new ArrayList();
        this.filtersIn = new ArrayList();
        this.filtersOr = new ArrayList();
        this.orderBys = new ArrayList();
        this.aliasMain = aliasMain;
        this.isCount = false;
        this.isFalse = false;
    }

    public SqlUtil(String aliasMain, boolean isCount) {
        this.parents = new ArrayList();
        this.filters = new ArrayList();
        this.conditions = new ArrayList();
        this.filtersIn = new ArrayList();
        this.filtersOr = new ArrayList();
        this.orderBys = new ArrayList();
        this.aliasMain = aliasMain;
        this.isCount = isCount;
        this.isFalse = false;
    }

    public static SqlUtil creaSqlUtil(String alias) {
        SqlUtil sqlutil = new SqlUtil(alias);
        return sqlutil;
    }

    public static SqlUtil creaCountSql(String alias) {
        SqlUtil sqlutil = new SqlUtil(alias, true);
        return sqlutil;
    }

    public SqlUtil parents(String... parents) {
        for (int i = 0; i < parents.length; i++) {
            String parent = parents[i];
            String[] partes = parents[i].split(" ");
            if (parent.startsWith("left ")) {
                String aliast = (partes.length > 2) ? partes[2] : "_tab" + (i + 1);
                DaoParent daoParent = new DaoParent(LEFT_JOIN, partes[1], aliast);
                daoParent.setUtil(this);
                this.parents.add(daoParent);
            } else {
                String alias = (partes.length > 1) ? partes[1] : "_tab" + (i + 1);
                DaoParent daoParent = new DaoParent(INNER_JOIN, partes[0], alias);
                daoParent.setUtil(this);
                this.parents.add(daoParent);
            }
        }
        return this;
    }

    public SqlUtil filter(String columna, Object valor) {
        String[] partes = columna.split(" ");
        DaoFilter filter = null;
        if (partes.length == 1) {
            filter = new DaoFilter(partes[0], "=", valor);
        } else if (partes.length > 1) {
            filter = new DaoFilter(partes[0], partes[1], valor);
        }

        if (filter != null) {
            this.filters.add(filter);
        }

        return this;
    }

    public SqlUtil condition(String condition) {
        this.conditions.add(condition);
        return this;
    }

    public SqlUtil filterIsNull(String columna) {
        DaoFilter filter = new DaoFilter(columna, "IS NULL", null);
        this.filters.add(filter);

        return this;
    }

    public SqlUtil filterIsNotNull(String columna) {
        DaoFilter filter = new DaoFilter(columna, "IS NOT NULL", null);
        this.filters.add(filter);

        return this;
    }

    public SqlUtil filterNotNull(String columna, Object valor) {
        if (valor == null) {
            return this;
        }
        return this.filter(columna, valor);
    }

    public Integer getNextOr() {
        return this.filtersOr.size();
    }

    public SqlUtil filterOr(int numOr, String columna, Object valor) {
        while (this.filtersOr.size() < numOr + 1) {
            this.filtersOr.add(new ArrayList());
        }

        List<DaoFilter> filterOr = this.filtersOr.get(numOr);

        DaoFilter filter = new DaoFilter(getColumn(columna), getOperator(columna), valor);
        filterOr.add(filter);

        return this;
    }

    public SqlUtil filterStr(String columna, String valor) {

        if (StringUtils.isEmpty(valor)) {
            return this;
        }

        DaoFilter filter = new DaoFilter(getColumn(columna), getOperator(columna), valor);
        this.filters.add(filter);

        return this;
    }

    public SqlUtil filterOrStr(int numOr, String columna, String valor) {

        if (StringUtils.isEmpty(valor)) {
            return this;
        }

        while (this.filtersOr.size() < numOr + 1) {
            this.filtersOr.add(new ArrayList());
        }

        List<DaoFilter> filterOr = this.filtersOr.get(numOr);

        DaoFilter filter = new DaoFilter(getColumn(columna), getOperator(columna), getValue(columna, valor));
        filterOr.add(filter);
        return this;
    }

    public SqlUtil filterIn(String columna, List lista) {

        if (lista.isEmpty()) {
            this.isFalse = true;
            return this;
        }
        String tipo = lista.get(0).getClass().getSimpleName();
        if (types.contains(tipo)) {
            DaoFilter filter = new DaoFilter(columna, "in", lista);
            this.filtersIn.add(filter);

            return this;
        }

        List<Long> idsList = new ArrayList();
        for (Object item : lista) {
            Long id = (Long) ObjectUtil.getParent(item, "id");
            idsList.add(id);
        }

        DaoFilter filter = new DaoFilter(columna, "in", idsList);
        this.filtersIn.add(filter);

        return this;
    }

    public SqlUtil filterNotIn(String columna, List lista) {

        if (lista.isEmpty()) {
            return this;
        }
        String tipo = lista.get(0).getClass().getSimpleName();
        if (types.contains(tipo)) {
            DaoFilter filter = new DaoFilter(columna, "not in", lista);
            this.filtersIn.add(filter);

            return this;
        }

        List<Long> idsList = new ArrayList();
        for (Object item : lista) {
            Long id = (Long) ObjectUtil.getParent(item, "id");
            idsList.add(id);
        }

        DaoFilter filter = new DaoFilter(columna, "not in", idsList);
        this.filtersIn.add(filter);

        return this;
    }

    public SqlUtil orderBy(String... columnas) {
        for (String columna : columnas) {
            if (StringUtils.isEmpty(columna)) {
                continue;
            }
            String[] partes = columna.split(" ");
            if (partes.length == 1) {
                this.orderBys.add(new DaoOrderBy(columna));
            } else if (partes.length > 1) {
                Integer tipoOrden = (partes[1].equalsIgnoreCase("DESC")) ? ORDER_DESC : ((partes[1].equalsIgnoreCase("ASC")) ? ORDER_ASC : 0);
                this.orderBys.add(new DaoOrderBy(partes[0], tipoOrden));
            }
        }

        return this;
    }

    public SqlUtil setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public SqlUtil setFirstResult(Integer firstResult) {
        this.firstResult = firstResult;
        return this;
    }

    public List<DaoParent> getParents() {
        return parents;
    }

    public List<DaoFilter> getFilters() {
        return filters;
    }

    public List<String> getConditions() {
        return conditions;
    }

    public List<DaoFilter> getFiltersIn() {
        return filtersIn;
    }

    public List<DaoOrderBy> getOrderBys() {
        return orderBys;
    }

    public String getAlias() {
        return aliasMain;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public Boolean getIsCount() {
        return isCount;
    }

    public String getAliasMain() {
        return aliasMain;
    }

    public void setAliasMain(String aliasMain) {
        this.aliasMain = aliasMain;
    }

    public Integer getFirstResult() {
        return firstResult;
    }

    public List<List<DaoFilter>> getFiltersOr() {
        return filtersOr;
    }

    public Boolean getIsFalse() {
        return isFalse;
    }

    public void setIsFalse(Boolean isFalse) {
        this.isFalse = isFalse;
    }

    public static List<Long> allIds(List lista) {
        List<Long> idsList = new ArrayList();
        lista.stream().map((item) -> (Long) ObjectUtil.getParent(item, "id")).forEach((id) -> {
            idsList.add((Long) id);
        });
        return idsList;
    }

    private String getColumn(String originalColumn) {
        String[] parts = originalColumn.split(" ");
        String column = originalColumn;
        if (parts.length > 1) {
            String[] jujuy = ArrayUtils.remove(parts, parts.length - 1);
            column = String.join(" ", jujuy);
        }
        return column;
    }

    private String getOperator(String originalColumn) {
        String[] parts = originalColumn.split(" ");
        String operator = comparators.get(0);
        if (parts.length > 1) {
            if (comparators.contains(parts[parts.length - 1].toLowerCase())) {
                operator = parts[parts.length - 1];
            }
        }
        return operator;
    }

    private String getValue(String originalColumn, String originalValue) {
        String[] parts = originalColumn.split(" ");
        boolean esLike = parts[parts.length - 1].equalsIgnoreCase("like");
        if (esLike) {
            return new StringBuilder("%")
                    .append(originalValue.replaceAll(" ", "%"))
                    .append("%").toString();
        }
        return originalValue;
    }

}

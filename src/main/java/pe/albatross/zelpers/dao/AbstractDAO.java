package pe.albatross.zelpers.dao;

import java.io.Serializable;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.base.Preconditions;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.SessionFactory;
import org.springframework.util.StringUtils;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;

@Deprecated
@SuppressWarnings("unchecked")
public abstract class AbstractDAO<T extends Serializable> implements Crud<T> {

    private Class<T> clazz;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected void setClazz(final Class<T> clazzToSet) {
        clazz = Preconditions.checkNotNull(clazzToSet);
    }

    private void setValue(Query query, String tipo, String param, Object valor) {

        if (tipo.equals(String.class.getSimpleName())) {
            query.setString(param, (String) valor);

        } else if (tipo.equals(Integer.class.getSimpleName())) {
            query.setInteger(param, (Integer) valor);

        } else if (tipo.equals(Long.class.getSimpleName())) {
            query.setLong(param, (Long) valor);

        } else if (tipo.equals(BigDecimal.class.getSimpleName())) {
            query.setBigDecimal(param, (BigDecimal) valor);

        } else if (tipo.equals(Float.class.getSimpleName())) {
            query.setFloat(param, (Float) valor);

        } else if (tipo.equals(Double.class.getSimpleName())) {
            query.setDouble(param, (Double) valor);

        } else if (tipo.equals(Timestamp.class.getSimpleName())) {
            query.setDate(param, (Date) valor);

        } else if (tipo.equals(Date.class.getSimpleName())) {
            query.setDate(param, (Date) valor);

        } else {
            query.setLong(param, (Long) ObjectUtil.getParent(valor, "id"));
        }
    }

    private StringBuilder createSelect(SqlUtil sqlUtil) {
        StringBuilder sql;
        if (sqlUtil.getIsCount()) {
            sql = new StringBuilder("select count(*) from ");
        } else {
            sql = new StringBuilder("from ");
        }

        sql.append(clazz.getName()).append(" as ").append(sqlUtil.getAlias()).append(" ");

        for (DaoParent parent : sqlUtil.getParents()) {
            sql.append(parent.toString());
        }

        return sql;
    }

    private void createWhere(SqlUtil sqlUtil, StringBuilder sql) {
        int loopFilters = 1;
        boolean whereExists = false;

        if (sqlUtil.getIsFalse()) {
            sql.append((whereExists ? " and " : " where "));
            whereExists = true;
            sql.append(" 1=2 ");
        }

        for (DaoFilter filter : sqlUtil.getFilters()) {
            sql.append((whereExists ? " and " : " where "));
            whereExists = true;

            sql.append(filter.getAttr()).append(" ").append(filter.getTipoCondicion());
            if (filter.getTipoCondicion().equalsIgnoreCase("IS NULL")) {
            } else if (filter.getTipoCondicion().equalsIgnoreCase("IS NOT NULL")) {
            } else {
                sql.append(" :param").append(loopFilters);
                loopFilters++;
            }

        }

        for (String condition : sqlUtil.getConditions()) {
            sql.append((whereExists ? " and " : " where "));
            whereExists = true;
            sql.append(" ").append(condition);

        }

        for (DaoFilter filter : sqlUtil.getFiltersIn()) {
            sql.append((whereExists ? " and " : " where "));
            whereExists = true;

            sql.append(filter.getAttr()).append(" ").append(filter.getTipoCondicion());
            sql.append(" :param").append(loopFilters);
            loopFilters++;
        }

        for (List<DaoFilter> listaOr : sqlUtil.getFiltersOr()) {
            if (listaOr.isEmpty()) {
                continue;
            }

            sql.append((whereExists ? " and " : " where "));
            whereExists = true;

            StringBuilder sqlOr = new StringBuilder("(");
            int loopFilterOr = 1;
            for (DaoFilter filter : listaOr) {
                sqlOr.append(((loopFilterOr == 1) ? " " : " or "));
                sqlOr.append(filter.getAttr()).append(" ").append(filter.getTipoCondicion());
                sqlOr.append(" :param").append(loopFilters);
                loopFilterOr++;
                loopFilters++;
            }

            sqlOr.append(") ");
            sql.append(sqlOr);
        }
    }

    private void createOrderBy(SqlUtil sqlUtil, StringBuilder sql) {
        int loopOrders = 1;
        List<DaoOrderBy> orders = sqlUtil.getOrderBys();
        for (DaoOrderBy orderBy : orders) {
            sql.append(((loopOrders == 1) ? " order by " : ", "));
            sql.append(orderBy.toString());
            loopOrders++;
        }
    }

    private void settingParameters(SqlUtil sqlUtil, StringBuilder sql, Query query) {
        int loopParams = 1;
        for (DaoFilter filter : sqlUtil.getFilters()) {
            if (filter.getTipoCondicion().equalsIgnoreCase("IS NULL")) {
            } else if (filter.getTipoCondicion().equalsIgnoreCase("IS NOT NULL")) {
            } else {

                Object valor = filter.getValor();
                String tipo = valor.getClass().getSimpleName();
                String param = "param" + loopParams;
                setValue(query, tipo, param, valor);

                loopParams++;
            }
        }

        for (DaoFilter filter : sqlUtil.getFiltersIn()) {
            List lista = (List) filter.getValor();
            String param = "param" + loopParams;
            query.setParameterList(param, lista);

            loopParams++;
        }

        for (List<DaoFilter> listaOr : sqlUtil.getFiltersOr()) {
            if (listaOr.isEmpty()) {
                continue;
            }
            for (DaoFilter filter : listaOr) {
                Object valor = filter.getValor();
                String tipo = valor.getClass().getSimpleName();
                String param = "param" + loopParams;
                setValue(query, tipo, param, valor);

                loopParams++;
            }
        }
    }

    @Override
    public T find(final long id) {
        return (T) getCurrentSession().get(clazz, id);
    }

    @Override
    public T find(SqlUtil sqlUtil) {

        if (sqlUtil.getIsCount()) {
            throw new RuntimeException("Este SqlUtil es para retornar datos, no conteos");
        }

        StringBuilder sql = this.createSelect(sqlUtil);
        this.createWhere(sqlUtil, sql);
        this.createOrderBy(sqlUtil, sql);
        Query query = getCurrentSession().createQuery(sql.toString());
        this.settingParameters(sqlUtil, sql, query);

        if (sqlUtil.getFirstResult() != null) {
            query.setFirstResult(sqlUtil.getFirstResult());
        }
        if (sqlUtil.getPageSize() != null) {
            query.setMaxResults(sqlUtil.getPageSize());
        }

        return (T) query.uniqueResult();
    }

    public List<T> dynaTable(DynatableFilter filtro) {

        filtro.setTotal(this.count(filtro));
        filtro.setFiltered(this.countByFilter(filtro));

        SqlUtil sqlUtil = SqlUtil.creaSqlUtil(filtro.getAlias());
        sqlUtil.parents(filtro.getParents());
        this.filter(sqlUtil, filtro.getFields(), filtro.getSearchValue());
        sqlUtil.setFirstResult(filtro.getOffset())
                .setPageSize(filtro.getPerPage());

        return this.all(sqlUtil);

    }

    @Override
    public Long count(SqlUtil sqlUtil) {
        if (!sqlUtil.getIsCount()) {
            throw new RuntimeException("Este SqlUtil es para retornar conteos, no datos");
        }

        StringBuilder sql = this.createSelect(sqlUtil);
        this.createWhere(sqlUtil, sql);
        Query query = getCurrentSession().createQuery(sql.toString());
        this.settingParameters(sqlUtil, sql, query);

        return (Long) query.uniqueResult();
    }

    @Override
    public List<T> all(SqlUtil sqlUtil) {
        if (sqlUtil.getIsCount()) {
            throw new RuntimeException("Este SqlUtil es para retornar datos, no conteos");
        }

        StringBuilder sql = this.createSelect(sqlUtil);
        this.createWhere(sqlUtil, sql);
        this.createOrderBy(sqlUtil, sql);
        Query query = getCurrentSession().createQuery(sql.toString());
        this.settingParameters(sqlUtil, sql, query);

        if (sqlUtil.getFirstResult() != null) {
            query.setFirstResult(sqlUtil.getFirstResult());
        }
        if (sqlUtil.getPageSize() != null) {
            query.setMaxResults(sqlUtil.getPageSize());
        }

        return query.list();
    }

    private String getColumnType(Class clazs, String column) {
        Field[] fields = clazs.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(column)) {
                Annotation[] annotations = field.getDeclaredAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation instanceof Column) {
                        return field.getType().getName();
                    }
                    if (annotation instanceof OneToOne) {
                        throw new PhobosException("La columna " + column + " esta definida como tabla");
                    }
                    if (annotation instanceof JoinColumn) {
                        throw new PhobosException("La columna " + column + " esta definida como tabla");
                    }
                }
            }
        }
        throw new PhobosException("La columna " + column + " no esta definida en la clase " + clazs.getName());
    }

    private Class getClassColumn(Class clazs, String column) {
        Field[] fields = clazs.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(column)) {
                Annotation[] annotations = field.getDeclaredAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation instanceof Column) {
                        throw new PhobosException("La columna " + column + " no está definida como tabla");
                    }
                    if (annotation instanceof JoinColumn || annotation instanceof OneToOne) {
                        String subClazz = field.getType().getCanonicalName();
                        try {
                            return Class.forName(subClazz);
                        } catch (ClassNotFoundException ex) {
                            //java.util.logging.Logger.getLogger(Laboratory.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
        throw new PhobosException("La columna " + column + " no esta definida en la clase " + clazs.getName());
    }

    private void getSequenceParent(List<DaoParent> sequence, String alias, List<DaoParent> parents) {
        for (DaoParent parent : parents) {
            if (alias.equals(parent.getAlias())) {
                sequence.add(parent);

                int pos = parent.getName().indexOf(".");
                if (pos < 0) {
                    return;
                }
                String subAlias = parent.getName().substring(1, pos);
                getSequenceParent(sequence, subAlias, parents);
                return;
            }
        }
        throw new PhobosException("Error en busqueda de secuencia de parents: No se halló <<" + alias + ">>");
    }

    private Class getClassAlias(Class clazs, String alias, List<DaoParent> parents) {
        List<DaoParent> sequence = new ArrayList();
        getSequenceParent(sequence, alias, parents);
        Collections.reverse(sequence);

        Class clazze = clazs;
        for (DaoParent parent : sequence) {
            int pos = parent.getName().indexOf(".");
            clazze = getClassColumn(clazze, pos > -1 ? parent.getName().substring(pos + 1) : parent.getName());
        }
        return clazze;

    }

    public void filter(SqlUtil sqlUtil, List<String> columns, String searchMain) {
        filter(sqlUtil, columns, searchMain, new ArrayList());
    }

    public void filter(SqlUtil sqlUtil, List<String> columns, String searchMain, List<String> othersQuerys) {
        if (StringUtils.isEmpty(searchMain)) {
            return;
        }

        String[] searchs = searchMain.split(",");
        for (String search : searchs) {
            Integer nextQueryOR = sqlUtil.getNextOr();
            for (String column : columns) {
                if ("NADA".equals(column)) {
                    continue;
                }

                String[] subColumns = column.split(",");
                loopColumns:
                for (String subColumn : subColumns) {
                    String alias = sqlUtil.getAlias() + ".";
                    if (subColumn.startsWith(alias)) {
                        String type = getColumnType(clazz, subColumn.substring(alias.length()));
                        if (Date.class.getName().equals(type)) {
                            sqlUtil.filterOrStr(nextQueryOR, "date_format(" + subColumn + ",'%d/%m/%Y') LIKE", search);
                        } else {
                            sqlUtil.filterOrStr(nextQueryOR, subColumn + " LIKE", search);
                        }
                        continue;
                    }

                    List<DaoParent> parents = sqlUtil.getParents();
                    for (DaoParent parent : parents) {
                        String aliasParent = parent.getAlias() + ".";
                        if (subColumn.startsWith(aliasParent)) {
                            Class clazzColumn = getClassAlias(clazz, parent.getAlias(), parents);
                            String type = getColumnType(clazzColumn, subColumn.substring(parent.getAlias().length() + 1));

                            if (Date.class.getName().equals(type)) {
                                sqlUtil.filterOrStr(nextQueryOR, "date_format(" + subColumn + ",'%d/%m/%Y') LIKE", search);
                            } else {
                                sqlUtil.filterOrStr(nextQueryOR, subColumn + " LIKE", search);
                            }
                            continue loopColumns;
                        }
                    }
                    throw new PhobosException("La columna " + subColumn + " no se encuentra enlazada a ninguna tabla");
                }
            }

            for (String otherQuery : othersQuerys) {
                sqlUtil.filterOrStr(nextQueryOR, otherQuery + " LIKE", search);
            }
        }
    }

    public void filterFixed(SqlUtil sqlUtil, String column, Object searchMain) {

        if ("NADA".equals(column)) {
            return;
        }

        String col = column.split(" ").length == 1 ? column : column.split(" ")[0];
        String alias = sqlUtil.getAlias() + ".";
        if (searchMain instanceof String) {
            String[] searchs = ((String) searchMain).split(",");
            Integer busqueda = sqlUtil.getNextOr();
            for (String search : searchs) {
                filterFixedZZZ(search, col, column, alias, sqlUtil, busqueda);
            }

        } else {
            filterFixedZZZ(searchMain, col, column, alias, sqlUtil, -1);
        }

    }

    private void filterFixedZZZ(Object search, String col, String column, String alias, SqlUtil sqlUtil, Integer busqueda) {

        if (col.startsWith(alias)) {
            String type = getColumnType(clazz, col.substring(alias.length()));
            if (Date.class.getName().equals(type)) {
                if (column.split(" ").length == 1) {
                    if (busqueda == -1) {
                        sqlUtil.filter("date_format(" + col + ",'%d/%m/%Y')", search);
                    } else {
                        sqlUtil.filterOr(busqueda, "date_format(" + col + ",'%d/%m/%Y')", search);
                    }
                } else if (busqueda == -1) {
                    sqlUtil.filter("date_format(" + col + ",'%d/%m/%Y') " + column.split(" ")[1], search);
                } else {
                    sqlUtil.filterOr(busqueda, "date_format(" + col + ",'%d/%m/%Y') " + column.split(" ")[1], search);
                }
            } else if (busqueda == -1) {
                sqlUtil.filter(column, search);
            } else {
                sqlUtil.filterOr(busqueda, column, search);
            }
            return;
        }

        List<DaoParent> parents = sqlUtil.getParents();
        for (DaoParent parent : parents) {
            String aliasParent = parent.getAlias() + ".";
            if (col.startsWith(aliasParent)) {
                Class clazzColumn = getClassAlias(clazz, parent.getAlias(), parents);
                String type = getColumnType(clazzColumn, col.substring(parent.getAlias().length() + 1));

                if (Date.class.getName().equals(type)) {
                    if (column.split(" ").length == 1) {
                        if (busqueda == -1) {
                            sqlUtil.filter("date_format(" + col + ",'%d/%m/%Y')", search);
                        } else {
                            sqlUtil.filterOr(busqueda, "date_format(" + col + ",'%d/%m/%Y')", search);
                        }
                    } else if (busqueda == -1) {
                        sqlUtil.filter("date_format(" + col + ",'%d/%m/%Y') " + column.split(" ")[1], search);
                    } else {
                        sqlUtil.filterOr(busqueda, "date_format(" + col + ",'%d/%m/%Y') " + column.split(" ")[1], search);
                    }
                } else if (busqueda == -1) {
                    sqlUtil.filter(column, search);
                } else {
                    sqlUtil.filterOr(busqueda, column, search);
                }
                return;
            }
        }
        throw new PhobosException("La columna " + col + " no se encuentra enlazada a ninguna tabla");
    }

    public void filterInFixed(SqlUtil sqlUtil, String column, List lista) {

        if ("NADA".equals(column)) {
            return;
        }

        String alias = sqlUtil.getAlias() + ".";
        if (column.startsWith(alias)) {
            String type = getColumnType(clazz, column.substring(alias.length()));
            if (Date.class.getName().equals(type)) {
                sqlUtil.filterIn("date_format(" + column + ",'%d/%m/%Y')", lista);
            } else {
                sqlUtil.filterIn(column, lista);
            }
            return;
        }

        List<DaoParent> parents = sqlUtil.getParents();
        for (DaoParent parent : parents) {
            String aliasParent = parent.getAlias() + ".";
            if (column.startsWith(aliasParent)) {
                Class clazzColumn = getClassAlias(clazz, parent.getAlias(), parents);
                String type = getColumnType(clazzColumn, column.substring(parent.getAlias().length() + 1));

                if (Date.class.getName().equals(type)) {
                    sqlUtil.filterIn("date_format(" + column + ",'%d/%m/%Y')", lista);
                } else {
                    sqlUtil.filterIn(column, lista);
                }
                return;
            }
        }
        throw new PhobosException("La columna " + column + " no se encuentra enlazada a ninguna tabla");
    }

    public Integer count(DynatableFilter filtro) {
        SqlUtil sqlUtil = SqlUtil.creaCountSql(filtro.getAlias());
        if (filtro.getParents() != null) {
            sqlUtil.parents(filtro.getParents());
        }

        Map filtersFixed = filtro.getFiltersFixed();
        for (Object key : filtersFixed.keySet()) {
            this.filterFixed(sqlUtil, (String) key, filtersFixed.get(key));
        }

        Map filtersInFixed = filtro.getFiltersInFixed();
        for (Object key : filtersInFixed.keySet()) {
            this.filterInFixed(sqlUtil, (String) key, (List) filtersInFixed.get(key));
        }

        return this.count(sqlUtil).intValue();
    }

    public Integer countByFilter(DynatableFilter filtro) {
        SqlUtil sqlUtil = SqlUtil.creaCountSql(filtro.getAlias());
        if (filtro.getParents() != null) {
            sqlUtil.parents(filtro.getParents());
        }
        this.filter(sqlUtil, filtro.getFields(), filtro.getSearchValue());

        Map filtersFixed = filtro.getFiltersFixed();
        for (Object key : filtersFixed.keySet()) {
            this.filterFixed(sqlUtil, (String) key, filtersFixed.get(key));
        }

        Map filtersInFixed = filtro.getFiltersInFixed();
        for (Object key : filtersInFixed.keySet()) {
            this.filterInFixed(sqlUtil, (String) key, (List) filtersInFixed.get(key));
        }

        Map queries = filtro.getQueries();
        if (queries != null) {
            for (Object key : queries.keySet()) {
                if (!((String) key).equals("search")) {
                    this.filterFixed(sqlUtil, (String) key, queries.get(key));
                }
            }
        }

        return this.count(sqlUtil).intValue();
    }

    @Override
    public List<T> all() {

        String query = "from " + clazz.getName();
        query += (this.getFilterField() != null) ? " order by " + this.getFilterField() : " ";

        return getCurrentSession().createQuery(query).list();
    }

    private String getFilterField() {

        Set<String> attributos = new HashSet<>();

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            attributos.add(field.getName());
        }

        if (attributos.contains("orden")) {
            return "orden";

        } else if (attributos.contains("nombre")) {
            return "nombre";

        } else if (attributos.contains("nombres")) {
            return "nombres";

        } else if (attributos.contains("paterno")) {
            return "paterno";

        } else if (attributos.contains("descripcion")) {
            return "descripcion";
        }

        return null;

    }

    @Override
    public List<T> all(List<Long> ids) {
        Query query = getCurrentSession().createQuery("FROM " + clazz.getName() + " c WHERE c.id IN :ids");
        query.setParameterList("ids", ids);
        return query.list();
    }

    @Override
    public void save(final T entity) {
        Preconditions.checkNotNull(entity);
        getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public void update(final T entity) {
        Preconditions.checkNotNull(entity);
        getCurrentSession().merge(entity);
    }

    @Override
    public void delete(final T entity) {
        Preconditions.checkNotNull(entity);
        getCurrentSession().delete(entity);
    }

    @Override
    public void delete(final long entityId) {
        final T entity = find(entityId);
        Preconditions.checkState(entity != null);
        delete(entity);
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
}

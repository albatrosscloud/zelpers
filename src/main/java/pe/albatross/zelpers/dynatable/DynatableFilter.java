package pe.albatross.zelpers.dynatable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynatableFilter {

    private Integer page;
    private Integer perPage;
    private Integer offset;
    private Map queries;
    private String orden;

    private List<String> fields;
    private List<String> complexFields;
    private List<Map> order;
    private String[] parents;
    private String alias;
    private Integer total;
    private Integer filtered;
    private Map filtersFixed;
    private Map filtersInFixed;
    private String searchValue;

    public DynatableFilter() {
        total = 0;
        filtered = 0;
        filtersFixed = new HashMap();
        filtersInFixed = new HashMap();
        complexFields = new ArrayList();
    }

    public void complexField(String column) {
        complexFields.add(column);
    }

    public void filterFix(String column, Object value) {
        if (value == null) {
            return;
        }
        filtersFixed.put(column, value);
    }

    public void filterInFix(String column, List value) {
        if (value == null) {
            return;
        }
        filtersInFixed.put(column, value);
    }

    public String getSearchValue() {
        String search = "";
        if (queries != null) {
            if (queries.get("search") == null) {
                return search;
            }
            search = queries.get("search").toString();
        }
        return search;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPerPage() {
        return perPage;
    }

    public void setPerPage(Integer perPage) {
        this.perPage = perPage;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Map getQueries() {
        return queries;
    }

    public void setQueries(Map queries) {
        this.queries = queries;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public List<Map> getOrder() {
        return order;
    }

    public void setOrder(List<Map> order) {
        this.order = order;
    }

    public String[] getParents() {
        return parents;
    }

    public void setParents(String... parents) {
        this.parents = parents;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getFiltered() {
        return filtered;
    }

    public void setFiltered(Integer filtered) {
        this.filtered = filtered;
    }

    public Map getFiltersFixed() {
        return filtersFixed;
    }

    public void setFiltersFixed(Map filtersFixed) {
        this.filtersFixed = filtersFixed;
    }

    public Map getFiltersInFixed() {
        return filtersInFixed;
    }

    public void setFiltersInFixed(Map filtersInFixed) {
        this.filtersInFixed = filtersInFixed;
    }

    public List<String> getComplexFields() {
        return complexFields;
    }

    public void setComplexFields(List<String> complexFields) {
        this.complexFields = complexFields;
    }

    public String getOrden() {
        return orden;
    }

    public void setOrden(String orden) {
        this.orden = orden;
    }

}

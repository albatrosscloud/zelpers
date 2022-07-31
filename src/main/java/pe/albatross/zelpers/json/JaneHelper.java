package pe.albatross.zelpers.json;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.opensagres.xdocreport.utils.StringUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;

@Slf4j
public class JaneHelper {

    private Set<String> fields;
    private Map<String, Set<String>> parentsFields;

    private Object objectOrigin;

    private ObjectNode rootObjectNode;
    private ArrayNode rootArrayNode;

    private Map<Integer, ObjectNode> mapNode;
    private boolean allowNullsBlanks;

    private static List<Class> TIPOS_DATOS = Arrays.asList(
            String.class, Character.class, Boolean.class,
            Integer.class, Long.class, Float.class, Double.class,
            BigDecimal.class, Date.class, Timestamp.class, Enum.class,
            boolean.class, int.class, long.class, float.class, double.class);

    public JaneHelper() {
        throw new PhobosException("Utilizar el método 'from()'");
    }

    public JaneHelper(Object object, boolean allowNulls) {
        Assert.isNull(this.objectOrigin, "El objeto inicial ya fue creado");

        this.objectOrigin = object;
        this.allowNullsBlanks = allowNulls;

        if (object instanceof String[]) {

            List list = Arrays.asList((String[]) object);
            this.generateArrayNode(list);

        } else if (object instanceof Object[]) {

            List list = Arrays.asList((Object[]) object);
            this.generateArrayNode(list);

        } else if (object instanceof List) {
            List list = (List) object;
            this.generateArrayNode(list);

        } else if (object instanceof Set) {
            Set list = (Set) object;
            this.generateArrayNode(list);

        } else if (object instanceof Map) {
            Map map = (Map) object;
            this.generateMapNode(map);

        } else {
            this.rootObjectNode = JsonHelper.createJson(object, JsonNodeFactory.instance, allowNullsBlanks, new String[]{"*"});
        }
    }

    public ObjectNode json() {
        return rootObjectNode;
    }

    public ArrayNode array() {
        return rootArrayNode;
    }

    public static JaneHelper from(Object object) {
        return JaneHelper.from(object, true);
    }

    public static JaneHelper from(Object object, boolean allowNulls) {
        return new JaneHelper(object, allowNulls);

    }

    private void generateMapNode(Map map) {
        this.rootObjectNode = new ObjectNode(JsonNodeFactory.instance);
        this.mapNode = new HashMap();

        int loop = 0;
        for (Object keyy : map.keySet()) {

            Object value = map.get(keyy);
            String key = keyy.toString();

            if (TIPOS_DATOS.contains(value.getClass()) || (value instanceof Enum)) {

                this.generateNativesForMap(value, key, this.rootObjectNode);

            } else {
                ObjectNode node = JsonHelper.createJson(value, JsonNodeFactory.instance, allowNullsBlanks, new String[]{"*"});
                this.rootObjectNode.set(key, node);
                this.mapNode.put(loop, node);
            }

            loop++;
        }

    }

    private void generateArrayNode(Collection collection) {
        this.rootArrayNode = new ArrayNode(JsonNodeFactory.instance);
        this.mapNode = new HashMap();

        int loop = 0;

        for (Object value : collection) {

            Class clazz = value.getClass();

            if (TIPOS_DATOS.contains(clazz) || (value instanceof Enum)) {
                this.generateNativesForArray(value, this.rootArrayNode);

            } else {

                ObjectNode node = JsonHelper.createJson(value, JsonNodeFactory.instance, allowNullsBlanks, new String[]{"*"});
                this.rootArrayNode.add(node);
                this.mapNode.put(loop, node);
            }

            loop++;
        }

    }

    public JaneHelper only(String attrs) {

        Assert.isNotNull(attrs, "Ha ingresado datos nulos en el método ONLY");
        boolean isEmpty = false;
        if (fields == null) {
            fields = new TreeSet();
            isEmpty = true;
        }
        fields.addAll(Arrays.asList(this.split(attrs)));
        String[] columns = isEmpty ? this.split(attrs) : new String[fields.size()];
        if (!isEmpty) {
            fields.toArray(columns);
        }

        if (this.objectOrigin instanceof Map) {
            int loop = 0;
            Map list = (Map) objectOrigin;

            for (Object key : list.keySet()) {
                Object obj = list.get(key);
                ObjectNode node = JsonHelper.createJson(obj, JsonNodeFactory.instance, allowNullsBlanks, columns);
                this.rootObjectNode.set(key.toString(), node);
                this.mapNode.put(loop, node);
                loop++;
            }

        } else if (this.rootObjectNode != null) {
            this.rootObjectNode = JsonHelper.createJson(objectOrigin, JsonNodeFactory.instance, allowNullsBlanks, columns);

        } else {
            this.rootArrayNode = new ArrayNode(JsonNodeFactory.instance);

            int loop = 0;

            if (this.objectOrigin instanceof String[]) {
                String[] list = (String[]) objectOrigin;
                for (Object obj : list) {
                    ObjectNode node = JsonHelper.createJson(obj, JsonNodeFactory.instance, allowNullsBlanks, columns);
                    this.mapNode.put(loop, node);
                    this.rootArrayNode.add(node);
                    loop++;
                }

            } else if (this.objectOrigin instanceof Object[]) {

                Object[] list = (Object[]) objectOrigin;

                for (Object obj : list) {
                    ObjectNode node = JsonHelper.createJson(obj, JsonNodeFactory.instance, allowNullsBlanks, columns);
                    this.mapNode.put(loop, node);
                    this.rootArrayNode.add(node);
                    loop++;
                }

            } else if (this.objectOrigin instanceof List) {

                List list = (List) objectOrigin;

                for (Object obj : list) {
                    ObjectNode node = JsonHelper.createJson(obj, JsonNodeFactory.instance, allowNullsBlanks, columns);
                    this.mapNode.put(loop, node);
                    this.rootArrayNode.add(node);
                    loop++;
                }

            } else if (this.objectOrigin instanceof Set) {
                Set list = (Set) objectOrigin;
                for (Object obj : list) {
                    ObjectNode node = JsonHelper.createJson(obj, JsonNodeFactory.instance, allowNullsBlanks, columns);
                    this.mapNode.put(loop, node);
                    this.rootArrayNode.add(node);
                    loop++;
                }

            }
        }
        return this;
    }

    public JaneHelper parents(String... parents) {

        for (String parent : parents) {
            join(parent, "*", false);
        }

        return this;
    }

    public JaneHelper include(String subList) {
        return join(subList, "*", false);
    }

    public JaneHelper include(String subList, String attributes) {
        return join(subList, attributes, false);
    }

    public JaneHelper join(String parent) {
        return join(parent, "*", false);
    }

    public JaneHelper join(String parent, String attributes) {
        return join(parent, attributes, false);
    }

    public JaneHelper join(String parent, boolean attachRoot) {
        return join(parent, "*", attachRoot);
    }

    public JaneHelper join(String parent, String attributes, boolean attachRoot) {
        if (this.objectOrigin == null) {
            return this;
        }

        parent = parent.replaceAll("\\s{2,}", " ").trim();
        int countSpace = parent.length() - parent.replace(" ", "").length();
        Assert.isTrue(countSpace < 2, "Mal ingreso de alias en " + parent);

        if (this.objectOrigin instanceof Map) {
            int loop = 0;
            Map map = (Map) objectOrigin;

            for (Object key : map.keySet()) {
                Object obj = map.get(key);
                ObjectNode node = mapNode.get(loop);
                this.joinOneObject(obj, node, parent, attributes, attachRoot);
                loop++;
            }

        } else if (this.rootObjectNode != null) {

            this.joinOneObject(this.objectOrigin, this.rootObjectNode, parent, attributes, attachRoot);

        } else {
            int loop = 0;

            if (this.objectOrigin instanceof String[]) {
                String[] list = (String[]) objectOrigin;
                for (Object obj : list) {
                    ObjectNode node = mapNode.get(loop);
                    this.joinOneObject(obj, node, parent, attributes, attachRoot);
                    loop++;
                }

            } else if (this.objectOrigin instanceof Object[]) {
                Object[] list = (Object[]) objectOrigin;
                for (Object obj : list) {
                    ObjectNode node = mapNode.get(loop);
                    this.joinOneObject(obj, node, parent, attributes, attachRoot);
                    loop++;
                }

            } else if (this.objectOrigin instanceof List) {
                List list = (List) objectOrigin;
                for (Object obj : list) {
                    ObjectNode node = mapNode.get(loop);
                    this.joinOneObject(obj, node, parent, attributes, attachRoot);
                    loop++;
                }

            } else if (this.objectOrigin instanceof Set) {
                Set list = (Set) objectOrigin;
                for (Object obj : list) {
                    ObjectNode node = mapNode.get(loop);
                    this.joinOneObject(obj, node, parent, attributes, attachRoot);
                    loop++;
                }

            }
        }

        return this;
    }

    private void joinOneObject(Object mother, ObjectNode parentNode, String parentName, String attributes, boolean attachRoot) {

        int countSpace = parentName.length() - parentName.replace(" ", "").length();
        Assert.isTrue(countSpace < 2, "Mal ingreso de alias en " + parentName);

        Object object;

        String alias = null;
        if (countSpace == 0) {
            object = ObjectUtil.getParentTree(mother, parentName);

        } else {
            alias = parentName.split(" ")[1].trim();
            parentName = parentName.split(" ")[0];
            object = ObjectUtil.getParentTree(mother, parentName);
        }

        if (object == null) {
            return;
        }

        int posPoint = parentName.indexOf(".");

        if (posPoint < 0) {
            String aliasFinal = StringUtils.isEmpty(alias) ? parentName : alias;
            this.generateParentJson(parentNode, object, parentName, aliasFinal, attributes);

        } else {
            this.generateAncestrosJson(mother, parentNode, parentName, object, alias, attributes, attachRoot);
        }

    }

    private void generateAncestrosJson(Object mother, ObjectNode parentNode, String parentName, Object objectTransform, String alias, String attributes, boolean attachRoot) {

        String[] attrs = parentName.split("\\.");
        String acum = "";

        if (parentsFields == null) {
            parentsFields = new HashMap();
        }

        boolean isEmpty = false;
        Set<String> parentFields = parentsFields.get(parentName);
        if (parentFields == null) {
            parentFields = new TreeSet();
            isEmpty = true;
            parentsFields.put(parentName, parentFields);
        }
        parentFields.addAll(Arrays.asList(this.split(attributes)));
        String[] columns = isEmpty ? this.split(attributes) : new String[parentFields.size()];
        if (!isEmpty) {
            parentFields.toArray(columns);
        }

        for (int i = 0; i < attrs.length; i++) {

            acum += acum.equals("") ? "" : ".";
            acum += attrs[i];

            if (i == attrs.length - 1) {

                String subAlias = StringUtils.isEmpty(alias) ? attrs[i] : alias;

                Object parentTree = ObjectUtil.getParentTree(mother, acum);

                if (parentTree instanceof List) {

                    List list = (List) parentTree;
                    parentNode.set(subAlias, this.createParentArrayNode(list, columns));

                } else if (parentTree instanceof Set) {

                    Set list = (Set) parentTree;
                    parentNode.set(subAlias, this.createParentArrayNode(list, columns));

                } else if (parentTree instanceof Map) {

                    Map map = (Map) parentTree;
                    parentNode.set(subAlias, this.createParentMapNode(map, columns));

                } else {
                    parentNode.set(subAlias, JsonHelper.createJson(objectTransform, JsonNodeFactory.instance, allowNullsBlanks, columns));
                }

            } else {
                if (!attachRoot) {
                    parentNode = parentNode.with(attrs[i]);
                }
            }
        }

    }

    private void generateParentJson(ObjectNode parentNode, Object object, String parentName, String aliasFinal, String attributes) {

        if (parentsFields == null) {
            parentsFields = new HashMap();
        }

        boolean isEmpty = false;
        Set<String> parentFields = parentsFields.get(parentName);
        if (parentFields == null) {
            parentFields = new TreeSet();
            isEmpty = true;
            parentsFields.put(parentName, parentFields);
        }
        parentFields.addAll(Arrays.asList(this.split(attributes)));
        String[] columns = isEmpty ? this.split(attributes) : new String[parentFields.size()];
        if (!isEmpty) {
            parentFields.toArray(columns);
        }

        if (object instanceof List) {

            List list = (List) object;
            parentNode.set(aliasFinal, this.createParentArrayNode(list, columns));

        } else if (object instanceof Set) {

            Set list = (Set) object;
            parentNode.set(aliasFinal, this.createParentArrayNode(list, columns));

        } else if (object instanceof Map) {

            Map map = (Map) object;
            parentNode.set(aliasFinal, this.createParentMapNode(map, columns));

        } else {

            parentNode.set(aliasFinal, JsonHelper.createJson(object, JsonNodeFactory.instance, this.allowNullsBlanks, columns));
        }

    }

    private ObjectNode createParentMapNode(Map map, String[] attributes) {

        ObjectNode objectNodeMap = new ObjectNode(JsonNodeFactory.instance);

        for (Object keyy : map.keySet()) {

            Object value = map.get(keyy);
            String key = keyy.toString();

            if (TIPOS_DATOS.contains(value.getClass()) || (value instanceof Enum)) {

                this.generateNativesForMap(value, key, objectNodeMap);

            } else {

                ObjectNode json = JsonHelper.createJson(value, JsonNodeFactory.instance, allowNullsBlanks, attributes);
                objectNodeMap.set(key, json);

            }
        }

        return objectNodeMap;
    }

    private ArrayNode createParentArrayNode(Collection list, String[] attributes) {

        ArrayNode parentArray = new ArrayNode(JsonNodeFactory.instance);

        for (Object value : list) {
            Class clazz = value.getClass();

            if (TIPOS_DATOS.contains(clazz) || (value instanceof Enum)) {

                this.generateNativesForArray(value, parentArray);

            } else {

                ObjectNode node = JsonHelper.createJson(value, JsonNodeFactory.instance, allowNullsBlanks, attributes);
                parentArray.add(node);
            }

        }
        return parentArray;
    }

    private String[] split(String attrs) {
        return attrs.replace(" ", "").split(",");
    }

    private static ObjectNode createJsonEnum(Enum enumValue, boolean allowNullsBlanks) {
        ObjectNode jsonEnum = new ObjectNode(JsonNodeFactory.instance);
        if (enumValue == null) {
            return jsonEnum;
        }

        boolean error = false;
        Class clazz = enumValue.getClass();
        Method methodName = null;
        try {
            methodName = clazz.getMethod("name");
        } catch (Exception ex) {
            error = true;
        }
        if (!error) {
            String name = "";
            try {
                name = (String) methodName.invoke(enumValue);
            } catch (Exception ex) {
                error = true;
            }
            if (!error) {
                jsonEnum.put("name", name);
            }
        }

        Method[] mths = clazz.getDeclaredMethods();
        for (Method m : mths) {
            if (!(!m.isSynthetic() && m.getName().startsWith("get") && m.getName().length() > 3)) {
                continue;
            }

            if (!TIPOS_DATOS.contains(m.getReturnType())) {
                continue;
            }

            String attrEnum = getFieldEnum(m.getName(), clazz);
            if (attrEnum == null) {
                continue;
            }

            Object value = null;
            try {
                value = m.invoke(enumValue);
            } catch (Exception ex) {
                continue;
            }

            if (value == null) {
                if (allowNullsBlanks) {
                    jsonEnum.put(attrEnum, "");
                }
            } else if (value instanceof Date) {
                jsonEnum.put(attrEnum, new DateTime((Date) value).toString("dd/MM/yyyy"));
            } else if (value instanceof Time) {
                jsonEnum.put(attrEnum, ((Time) value).getTime());
            } else if (value instanceof Timestamp) {
                jsonEnum.put(attrEnum, new DateTime((Date) value).toString("dd/MM/yyyy HH:mm:ss"));
            } else if (value instanceof Integer) {
                jsonEnum.put(attrEnum, (Integer) value);
            } else if (value instanceof Double) {
                jsonEnum.put(attrEnum, (Double) value);
            } else if (value instanceof Float) {
                jsonEnum.put(attrEnum, (Float) value);
            } else if (value instanceof Long) {
                jsonEnum.put(attrEnum, (Long) value);
            } else if (value instanceof BigDecimal) {
                jsonEnum.put(attrEnum, (BigDecimal) value);
            } else if (value instanceof Character) {
                jsonEnum.put(attrEnum, (Character) value);
            } else if (value instanceof String) {
                jsonEnum.put(attrEnum, (String) value);
            } else if (value instanceof Boolean) {
                jsonEnum.put(attrEnum, (Boolean) value);
            } else {
                jsonEnum.put(attrEnum, value.toString());
            }

        }

        return jsonEnum;
    }

    private static String getFieldEnum(String method, Class<?> c) {
        Field[] flds = c.getDeclaredFields();
        for (Field f : flds) {
            if (f.isEnumConstant()) {
            } else {
                String field = f.getName();
                String met = "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
                if (met.equals(method)) {
                    return f.getName();
                }
            }
        }
        return null;
    }

    private void generateNativesForMap(Object value, String key, ObjectNode objectNode) {
        if (value instanceof Date) {
            Date d1 = new LocalDate((Date) value).toDate();
            Date d2 = new DateTime((Date) value).toDate();

            if (d1.getTime() == d2.getTime()) {
                objectNode.put(key, new DateTime((Date) value).toString("dd/MM/yyyy"));
            } else {
                objectNode.put(key, new DateTime((Date) value).toString("dd/MM/yyyy HH:mm:ss"));
            }

        } else if (value instanceof Timestamp) {
            objectNode.put(key, new DateTime((Date) value).toString("dd/MM/yyyy HH:mm:ss"));

        } else if (value instanceof Time) {
            objectNode.put(key, ((Time) value).getTime());

        } else if (value instanceof Integer) {
            objectNode.put(key, (Integer) value);

        } else if (value instanceof Double) {
            objectNode.put(key, (Double) value);

        } else if (value instanceof Float) {
            objectNode.put(key, (Float) value);

        } else if (value instanceof Long) {
            objectNode.put(key, (Long) value);

        } else if (value instanceof BigDecimal) {
            objectNode.put(key, (BigDecimal) value);

        } else if (value instanceof Character) {
            objectNode.put(key, (Character) value);

        } else if (value instanceof String) {
            objectNode.put(key, (String) value);

        } else if (value instanceof Boolean) {
            objectNode.put(key, (Boolean) value);

        } else if (value instanceof Enum) {
            objectNode.set(key, this.createJsonEnum((Enum) value, allowNullsBlanks));

        } else {
            objectNode.put(key, value.toString());
        }

    }

    private void generateNativesForArray(Object value, ArrayNode node) {

        if (value instanceof Date) {
            Date d1 = new LocalDate((Date) value).toDate();
            Date d2 = new DateTime((Date) value).toDate();

            if (d1.getTime() == d2.getTime()) {
                node.add(new DateTime((Date) value).toString("dd/MM/yyyy"));
            } else {
                node.add(new DateTime((Date) value).toString("dd/MM/yyyy HH:mm:ss"));
            }

        } else if (value instanceof Timestamp) {
            node.add(new DateTime((Date) value).toString("dd/MM/yyyy HH:mm:ss"));

        } else if (value instanceof Time) {
            node.add(((Time) value).getTime());

        } else if (value instanceof Integer) {
            node.add((Integer) value);

        } else if (value instanceof Double) {
            node.add((Double) value);

        } else if (value instanceof Float) {
            node.add((Float) value);

        } else if (value instanceof Long) {
            node.add((Long) value);

        } else if (value instanceof BigDecimal) {
            node.add((BigDecimal) value);

        } else if (value instanceof Character) {
            node.add((Character) value);

        } else if (value instanceof String) {
            node.add((String) value);

        } else if (value instanceof Boolean) {
            node.add((Boolean) value);

        } else if (value instanceof Enum) {
            node.add(createJsonEnum((Enum) value, allowNullsBlanks));

        } else {
            node.add(value.toString());
        }

    }

}

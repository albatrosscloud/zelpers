package pe.albatross.zelpers.json;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;

public class JaneHelper {

    private Object object;
    private ObjectNode rootObject;
    private ArrayNode rootArray;
    private Map<Integer, ObjectNode> mapNode;
    private boolean allowNullsBlanks;

    private static List<Class> TIPOS_DATOS
            = Arrays.asList(String.class, Integer.class, Long.class, Float.class, Double.class,
                    BigDecimal.class, Character.class, Date.class, Timestamp.class, Boolean.class, Enum.class,
                    boolean.class, int.class, float.class, double.class);

    public JaneHelper() {
        throw new PhobosException("Utilizar el método 'from()'");
    }

    public JaneHelper(Object object, boolean allowNulls) {
        Assert.isNull(this.object, "El objeto inicial ya fue creado");
        this.object = object;
        this.allowNullsBlanks = allowNulls;

        if (object instanceof String[]) {
            this.rootArray = new ArrayNode(JsonNodeFactory.instance);
            this.mapNode = new HashMap();

            int loop = 0;
            String[] list = (String[]) object;
            for (Object value : list) {
                createItemList(value, loop);
                loop++;
            }

        } else if (object instanceof Object[]) {
            this.rootArray = new ArrayNode(JsonNodeFactory.instance);
            this.mapNode = new HashMap();

            int loop = 0;
            Object[] list = (Object[]) object;
            for (Object value : list) {
                createItemList(value, loop);
                loop++;
            }

        } else if (object instanceof List) {
            this.rootArray = new ArrayNode(JsonNodeFactory.instance);
            this.mapNode = new HashMap();

            int loop = 0;
            List list = (List) object;
            for (Object value : list) {
                createItemList(value, loop);
                loop++;
            }

        } else if (object instanceof Set) {
            this.rootArray = new ArrayNode(JsonNodeFactory.instance);
            this.mapNode = new HashMap();

            int loop = 0;
            Set list = (Set) object;
            for (Object value : list) {
                createItemList(value, loop);
                loop++;
            }

        } else if (object instanceof Map) {
            this.rootObject = new ObjectNode(JsonNodeFactory.instance);
            this.mapNode = new HashMap();

            int loop = 0;
            Map list = (Map) object;
            for (Object key : list.keySet()) {
                ObjectNode node = JsonHelper.createJson(list.get(key), JsonNodeFactory.instance, allowNullsBlanks, new String[]{"*"});
                this.rootObject.set(key.toString(), node);
                this.mapNode.put(loop, node);
                loop++;
            }

        } else {
            this.rootObject = JsonHelper.createJson(object, JsonNodeFactory.instance, allowNullsBlanks, new String[]{"*"});
        }
    }

    private void createItemList(Object value, int loop) {
        Class clazz = value.getClass();

        if (TIPOS_DATOS.contains(clazz) || (value instanceof Enum)) {

            if (value instanceof Date) {
                Date d1 = new LocalDate((Date) value).toDate();
                Date d2 = new DateTime((Date) value).toDate();

                if (d1.getTime() == d2.getTime()) {
                    this.rootArray.add(new DateTime((Date) value).toString("dd/MM/yyyy"));
                } else {
                    this.rootArray.add(new DateTime((Date) value).toString("dd/MM/yyyy HH:mm:ss"));
                }

            } else if (value instanceof Time) {
                this.rootArray.add(((Time) value).getTime());
            } else if (value instanceof Timestamp) {
                this.rootArray.add(new DateTime((Date) value).toString("dd/MM/yyyy HH:mm:ss"));
            } else if (value instanceof Integer) {
                this.rootArray.add((Integer) value);
            } else if (value instanceof Double) {
                this.rootArray.add((Double) value);
            } else if (value instanceof Float) {
                this.rootArray.add((Float) value);
            } else if (value instanceof Long) {
                this.rootArray.add((Long) value);
            } else if (value instanceof BigDecimal) {
                this.rootArray.add((BigDecimal) value);
            } else if (value instanceof Character) {
                this.rootArray.add((Character) value);
            } else if (value instanceof String) {
                this.rootArray.add((String) value);
            } else if (value instanceof Boolean) {
                this.rootArray.add((Boolean) value);
            } else if (value instanceof Enum) {
                this.rootArray.add(createJsonEnum((Enum) value, allowNullsBlanks));
            } else {
                this.rootArray.add(value.toString());
            }

        } else {
            ObjectNode node = JsonHelper.createJson(value, JsonNodeFactory.instance, allowNullsBlanks, new String[]{"*"});
            this.mapNode.put(loop, node);
            this.rootArray.add(node);
        }
    }

    public ObjectNode json() {
        return rootObject;
    }

    public ArrayNode array() {
        return rootArray;
    }

    public static JaneHelper from(Object object) {
        return JaneHelper.from(object, true);
    }

    public static JaneHelper from(Object object, boolean allowNulls) {
        return new JaneHelper(object, allowNulls);

    }

    public JaneHelper only(String attrs) {
        if (this.object instanceof Map) {
            int loop = 0;
            Map list = (Map) object;
            for (Object key : list.keySet()) {
                Object obj = list.get(key);
                ObjectNode node = JsonHelper.createJson(obj, JsonNodeFactory.instance, allowNullsBlanks, this.split(attrs));
                this.rootObject.set(key.toString(), node);
                this.mapNode.put(loop, node);
                loop++;
            }

        } else if (this.rootObject != null) {
            this.rootObject = JsonHelper.createJson(object, JsonNodeFactory.instance, allowNullsBlanks, this.split(attrs));

        } else {
            this.rootArray = new ArrayNode(JsonNodeFactory.instance);

            int loop = 0;
            if (this.object instanceof String[]) {
                String[] list = (String[]) object;
                for (Object obj : list) {
                    ObjectNode node = JsonHelper.createJson(obj, JsonNodeFactory.instance, allowNullsBlanks, this.split(attrs));
                    this.mapNode.put(loop, node);
                    this.rootArray.add(node);
                    loop++;
                }

            } else if (this.object instanceof Object[]) {
                Object[] list = (Object[]) object;
                for (Object obj : list) {
                    ObjectNode node = JsonHelper.createJson(obj, JsonNodeFactory.instance, allowNullsBlanks, this.split(attrs));
                    this.mapNode.put(loop, node);
                    this.rootArray.add(node);
                    loop++;
                }

            } else if (this.object instanceof List) {
                List list = (List) object;
                for (Object obj : list) {
                    ObjectNode node = JsonHelper.createJson(obj, JsonNodeFactory.instance, allowNullsBlanks, this.split(attrs));
                    this.mapNode.put(loop, node);
                    this.rootArray.add(node);
                    loop++;
                }

            } else if (this.object instanceof Set) {
                Set list = (Set) object;
                for (Object obj : list) {
                    ObjectNode node = JsonHelper.createJson(obj, JsonNodeFactory.instance, allowNullsBlanks, this.split(attrs));
                    this.mapNode.put(loop, node);
                    this.rootArray.add(node);
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

    public JaneHelper join(String parent) {
        return join(parent, "*", false);
    }

    public JaneHelper join(String parent, String fields) {
        return join(parent, fields, false);
    }

    public JaneHelper join(String parent, boolean attachRoot) {
        return join(parent, "*", attachRoot);
    }

    public JaneHelper join(String parentString, String fields, boolean attachRoot) {
        if (this.object == null) {
            return this;
        }

        parentString = parentString.replaceAll("\\s{2,}", " ").trim();
        int countSpace = parentString.length() - parentString.replace(" ", "").length();
        Assert.isTrue(countSpace < 2, "Mal ingreso de alias en " + parentString);

        if (this.object instanceof Map) {
            int loop = 0;
            Map list = (Map) object;
            for (Object key : list.keySet()) {
                Object obj = list.get(key);
                ObjectNode node = mapNode.get(loop);
                this.joinOneObject(obj, node, parentString, fields, attachRoot);
                loop++;
            }

        } else if (this.rootObject != null) {
            this.joinOneObject(this.object, this.rootObject, parentString, fields, attachRoot);

        } else {
            int loop = 0;
            if (this.object instanceof String[]) {
                String[] list = (String[]) object;
                for (Object obj : list) {
                    ObjectNode node = mapNode.get(loop);
                    this.joinOneObject(obj, node, parentString, fields, attachRoot);
                    loop++;
                }

            } else if (this.object instanceof Object[]) {
                Object[] list = (Object[]) object;
                for (Object obj : list) {
                    ObjectNode node = mapNode.get(loop);
                    this.joinOneObject(obj, node, parentString, fields, attachRoot);
                    loop++;
                }

            } else if (this.object instanceof List) {
                List list = (List) object;
                for (Object obj : list) {
                    ObjectNode node = mapNode.get(loop);
                    this.joinOneObject(obj, node, parentString, fields, attachRoot);
                    loop++;
                }

            } else if (this.object instanceof Set) {
                Set list = (Set) object;
                for (Object obj : list) {
                    ObjectNode node = mapNode.get(loop);
                    this.joinOneObject(obj, node, parentString, fields, attachRoot);
                    loop++;
                }

            }
        }

        return this;
    }

    private void joinOneObject(Object matter, ObjectNode nodeRoot, String parentString, String fields, boolean attachRoot) {
        int countSpace = parentString.length() - parentString.replace(" ", "").length();
        Assert.isTrue(countSpace < 2, "Mal ingreso de alias en " + parentString);

        Object parent;
        String alias = null;
        if (countSpace == 0) {
            parent = ObjectUtil.getParentTree(matter, parentString);
        } else {
            alias = parentString.split(" ")[1];
            parentString = parentString.split(" ")[0];
            parent = ObjectUtil.getParentTree(matter, parentString);
        }

        if (parent == null) {
            return;
        }

        int posPoint = parentString.indexOf(".");
        if (posPoint < 0) {
            if (parent instanceof List) {
                if (alias == null) {
                    nodeRoot.set(parentString, createArrayNode(parent, this.split(fields)));
                } else {
                    nodeRoot.set(alias, createArrayNode(parent, this.split(fields)));
                }

            } else {
                if (alias == null) {
                    nodeRoot.set(parentString, JsonHelper.createJson(parent, JsonNodeFactory.instance, this.allowNullsBlanks, this.split(fields)));
                } else {
                    nodeRoot.set(alias, JsonHelper.createJson(parent, JsonNodeFactory.instance, this.allowNullsBlanks, this.split(fields)));
                }
            }
            return;
        }

        String acum = "";
        ObjectNode nodo = nodeRoot;

        String[] attrs = parentString.split("\\.");
        for (int i = 0; i < attrs.length; i++) {
            acum += acum.equals("") ? "" : ".";
            acum += attrs[i];

            Object parentTree = ObjectUtil.getParentTree(matter, acum);
            if (parentTree instanceof List) {
                Assert.isTrue(i == attrs.length - 1, "No puede ingresar al array " + acum);
                if (alias == null) {
                    nodo.set(parentString, createArrayNode(parentTree, this.split(fields)));
                } else {
                    nodo.set(alias, createArrayNode(parentTree, this.split(fields)));
                }
                return;

            } else {
                if (i == attrs.length - 1) {
                    if (alias == null) {
                        nodo.set(attrs[i], JsonHelper.createJson(parent, JsonNodeFactory.instance, allowNullsBlanks, this.split(fields)));
                    } else {
                        nodo.set(alias, JsonHelper.createJson(parent, JsonNodeFactory.instance, allowNullsBlanks, this.split(fields)));
                    }
                } else {
                    if (!attachRoot) {
                        nodo = nodo.with(attrs[i]);
                    }
                }
            }
        }
    }

    private ArrayNode createArrayNode(Object object, String[] fields) {
        List list = (List) object;
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (Object itemList : list) {
            ObjectNode node = JsonHelper.createJson(itemList, JsonNodeFactory.instance, allowNullsBlanks, fields);
            array.add(node);
        }
        return array;
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

}

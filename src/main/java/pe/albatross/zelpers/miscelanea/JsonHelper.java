package pe.albatross.zelpers.miscelanea;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.Introspector;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

public class JsonHelper {

    public static String TIPO_DATOS = "String_Integer_Long_BigDecimal_Float_Double_Char_Date";
    public static String TIPO_LISTAS = "List";
    private static final Logger logger = getLogger(JsonHelper.class);
    private String separador = ".";

    private static List<Class> TIPOS_DATOS
            = Arrays.asList(String.class, Integer.class, Long.class, Float.class, Double.class,
                    BigDecimal.class, Character.class, Date.class, Timestamp.class, Boolean.class,
                    boolean.class, int.class, float.class, double.class);

    public JsonHelper(ObjectNode json, Object obj) {
        loadPrefijo(null, json, obj);
    }

    public JsonHelper(ObjectNode json, Object obj, String separador) {
        this.separador = separador;
        loadPrefijo(null, json, obj);
    }

    public Object getAtributoObjeto(Object obj, String atributo) {
        Method metodo = null;
        Object objAttr = null;

        Class clase = obj.getClass();
        Method[] metodos = clase.getMethods();

        StringBuilder sbMetodo = new StringBuilder("get");
        sbMetodo.append(WordUtils.capitalize(atributo));

        for (int i = 0; i < metodos.length; i++) {
            Method metodoTmp = metodos[i];
            if (sbMetodo.toString().equals(metodoTmp.getName())) {
                metodo = metodoTmp;
                break;
            }
        }

        try {
            objAttr = metodo.invoke(obj);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        if (objAttr == null) {
            try {
                String[] tipoReturnList = metodo.getReturnType().toString().split(" ");
                Class clasePadre = Class.forName(tipoReturnList[tipoReturnList.length - 1]);
                Constructor constructor = clasePadre.getConstructor();
                objAttr = constructor.newInstance();
            } catch (Exception ex) {
                logger.error(ex.getLocalizedMessage());
            }

        }

        return objAttr;
    }

    public void load(ObjectNode json, Object obj) {
        loadPrefijo(null, json, obj);
    }

    public void loadId(ObjectNode json, Object obj) {
        loadPrefijoId(null, json, obj);
    }

    public void loadPrefijo(String prefijo, ObjectNode json, Object obj) {
        prefijo = (prefijo == null) ? "" : prefijo;
        Class clase = obj.getClass();
        Method[] metodos = clase.getMethods();

        for (int i = 0; i < metodos.length; i++) {
            Method metodo = metodos[i];
            String[] tipos = metodo.getReturnType().toString().split("\\.");
            String tipo = tipos[tipos.length - 1];

            if (metodo.getName().startsWith("get") && TIPO_DATOS.contains(tipo)) {
                StringBuilder sb = new StringBuilder(WordUtils.uncapitalize(metodo.getName().substring(3)));
                StringBuilder tmp = new StringBuilder(prefijo);
                if ("".equals(tmp.toString())) {
                    tmp.append(sb);
                } else {
                    tmp.append(this.separador).append(sb);
                }

                try {
                    Object rpta = metodo.invoke(obj);
                    if (rpta != null) {
                        if (rpta instanceof Date) {
                            json.put(tmp.toString(), ((Date) rpta).getTime());
                        } else if (rpta instanceof Time) {
                            json.put(tmp.toString(), ((Time) rpta).getTime());
                        } else if (rpta instanceof Timestamp) {
                            json.put(tmp.toString(), ((Timestamp) rpta).getTime());
                        } else if (rpta instanceof Integer) {
                            json.put(tmp.toString(), (Integer) rpta);
                        } else if (rpta instanceof Double) {
                            json.put(tmp.toString(), (Double) rpta);
                        } else if (rpta instanceof Float) {
                            json.put(tmp.toString(), (Float) rpta);
                        } else if (rpta instanceof Long) {
                            json.put(tmp.toString(), (Long) rpta);
                        } else if (rpta instanceof BigDecimal) {
                            json.put(tmp.toString(), (BigDecimal) rpta);
                        } else if (rpta instanceof Character) {
                            json.put(tmp.toString(), (Character) rpta);
                        } else if (rpta instanceof String) {
                            json.put(tmp.toString(), (String) rpta);
                        } else {
                            json.put(tmp.toString(), rpta.toString());
                        }
                    }
                } catch (Exception ex) {
                    logger.error(ex.getMessage());
                }
            }
        }
    }

    public void loadPrefijoId(String prefijo, ObjectNode json, Object obj) {
        prefijo = (prefijo == null) ? "" : prefijo;
        Class clase = obj.getClass();
        Method[] metodos = clase.getMethods();

        for (int i = 0; i < metodos.length; i++) {
            Method metodo = metodos[i];
            String[] tipos = metodo.getReturnType().toString().split("\\.");
            String tipo = tipos[tipos.length - 1];

            if (metodo.getName().equals("getId") && TIPO_DATOS.contains(tipo)) {
                StringBuilder sb = new StringBuilder(WordUtils.uncapitalize(metodo.getName().substring(3)));
                StringBuilder tmp = new StringBuilder(prefijo);
                if ("".equals(tmp.toString())) {
                    tmp.append(sb);
                } else {
                    tmp.append(this.separador).append(sb);
                }

                try {
                    Object rpta = metodo.invoke(obj);
                    if (rpta != null) {
                        json.put(tmp.toString(), rpta.toString());
                    }
                } catch (Exception ex) {
                    logger.error(ex.getMessage());
                }
            }
        }
    }

    public void load(ObjectNode json, Object obj, String padres) {
        String[] padreLista = padres.split("\\.");
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        Object objPadre = getAtributoObjeto(obj, padreLista[0]);

        ObjectNode jsonSub = new ObjectNode(jsonFactory);
        JsonNode json1 = json.get(padreLista[0]);
        if (json1 != null) {
            Iterator<Map.Entry<String, JsonNode>> items = json1.fields();
            while (items.hasNext()) {
                Map.Entry<String, JsonNode> item = items.next();
                jsonSub.set(item.getKey(), item.getValue());
            }
        }

        load(jsonSub, objPadre);

        if (padreLista.length > 1) {
            String abuelos = padres.substring(padreLista[0].length() + 1);
            load(jsonSub, objPadre, abuelos);
        }

        json.set(padreLista[0], jsonSub);
    }

    public void loadId(ObjectNode json, Object obj, String padres) {
        String[] padreLista = padres.split("\\.");
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        Object objPadre = getAtributoObjeto(obj, padreLista[0]);

        ObjectNode jsonSub = new ObjectNode(jsonFactory);
        JsonNode json1 = json.get(padreLista[0]);
        if (json1 != null) {
            Iterator<Map.Entry<String, JsonNode>> items = json1.fields();
            while (items.hasNext()) {
                Map.Entry<String, JsonNode> item = items.next();
                jsonSub.set(item.getKey(), item.getValue());
            }
        }

        loadId(jsonSub, objPadre);

        if (padreLista.length > 1) {
            String abuelos = padres.substring(padreLista[0].length() + 1);
            loadId(jsonSub, objPadre, abuelos);
        }

        json.set(padreLista[0], jsonSub);
    }

    public void loadPrefijo(ObjectNode json, Object obj, String padres) {
        loadPrefijo(null, json, obj, padres);
    }

    public void loadPrefijoId(ObjectNode json, Object obj, String padres) {
        loadPrefijoId(null, json, obj, padres);
    }

    public void loadPrefijo(String prefijo, ObjectNode json, Object obj, String padres) {
        prefijo = (prefijo == null) ? "" : prefijo;
        String[] padreLista = padres.split("\\.");

        StringBuilder tmp = new StringBuilder(prefijo);
        if ("".equals(tmp.toString())) {
            tmp.append(padreLista[0]);
        } else {
            tmp.append(this.separador).append(padreLista[0]);
        }

        Object objPadre = getAtributoObjeto(obj, padreLista[0]);
        loadPrefijo(tmp.toString(), json, objPadre);

        if (padreLista.length > 1) {
            String abuelos = padres.substring(padreLista[0].length() + 1);
            loadPrefijo(tmp.toString(), json, objPadre, abuelos);
        }

    }

    public void loadPrefijoId(String prefijo, ObjectNode json, Object obj, String padres) {
        prefijo = (prefijo == null) ? "" : prefijo;
        String[] padreLista = padres.split("\\.");

        StringBuilder tmp = new StringBuilder(prefijo);
        if ("".equals(tmp.toString())) {
            tmp.append(padreLista[0]);
        } else {
            tmp.append(this.separador).append(padreLista[0]);
        }

        Object objPadre = getAtributoObjeto(obj, padreLista[0]);
        loadPrefijoId(tmp.toString(), json, objPadre);

        if (padreLista.length > 1) {
            String abuelos = padres.substring(padreLista[0].length() + 1);
            loadPrefijoId(tmp.toString(), json, objPadre, abuelos);
        }

    }

    public static String toJson(Object object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return mapper.writeValueAsString(object);

        } catch (Exception e) {
            logger.debug("Error al Serializar/Marshall");
            return "";
        }

    }

    public static Object fromJson(String json, Class clazz) {
        try {

            ObjectMapper mapper = new ObjectMapper();
            Object object = (Object) mapper.readValue(json, clazz);
            return object;

        } catch (IOException ex) {
            logger.debug("Error al Deserializar/Unmarshall");
            return null;
        }

    }

    public static ObjectNode createJson(Object obj, JsonNodeFactory jsonFactory) {
        return JsonHelper.createJson(obj, jsonFactory, false);
    }

    public static ObjectNode createJson(Object obj, JsonNodeFactory jsonFactory, boolean allowNullsBlanks) {
        ObjectNode json = new ObjectNode(jsonFactory);

        if (obj == null) {
            return null;
        }

        Class clazz = obj.getClass();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            Class claxx = method.getReturnType();

            if (!(method.getName().startsWith("get") || method.getName().startsWith("is"))) {
                continue;
            }

            try {
                Object value = method.invoke(obj);
                if (!allowNullsBlanks && value == null) {
                    continue;
                }

                if (!TIPOS_DATOS.contains(claxx) && !(value instanceof Enum)) {
                    continue;
                }

                String methodName = method.getName().startsWith("get")
                        ? method.getName().substring(3)
                        : method.getName().substring(0);
                String attr = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);

                if (value == null) {
                    json.put(attr, "");
                } else if (value instanceof Date) {
                    json.put(attr, ((Date) value).getTime());
                } else if (value instanceof Time) {
                    json.put(attr, ((Time) value).getTime());
                } else if (value instanceof Timestamp) {
                    json.put(attr, ((Timestamp) value).getTime());
                } else if (value instanceof Integer) {
                    json.put(attr, (Integer) value);
                } else if (value instanceof Double) {
                    json.put(attr, (Double) value);
                } else if (value instanceof Float) {
                    json.put(attr, (Float) value);
                } else if (value instanceof Long) {
                    json.put(attr, (Long) value);
                } else if (value instanceof BigDecimal) {
                    json.put(attr, (BigDecimal) value);
                } else if (value instanceof Character) {
                    json.put(attr, (Character) value);
                } else if (value instanceof String) {
                    json.put(attr, (String) value);
                } else if (value instanceof Boolean) {
                    json.put(attr, (Boolean) value);
                } else if (value instanceof Enum) {
                    json.put(attr, ((Enum) value).name());
                } else {
                    json.put(attr, value.toString());
                }

            } catch (Exception ex) {
                //logger.error(ex.getMessage());
            }
        }

        return json;
    }

    public static ObjectNode createJson(Object obj, JsonNodeFactory jsonFactory, String[] attrs) {
        return createJson(obj, jsonFactory, false, attrs);
    }

    public static ObjectNode createJson(Object obj, JsonNodeFactory jsonFactory, boolean allowNullsBlanks, String[] attrs) {
        ObjectNode json = new ObjectNode(jsonFactory);

        if (obj == null) {
            return null;
        }

        Class objectClass = obj.getClass();

        Method[] methods = objectClass.getMethods();

        Map<String, List<String>> mapAttr = new LinkedHashMap();

        for (String attr : attrs) {
            int cant = StringUtils.countMatches(attr, ".");
            if (cant == 0) {
                if (attr.equals("*")) {
                    putAllAttr(json, obj, methods, objectClass, allowNullsBlanks);
                } else {
                    putOneAttr(json, obj, attr, objectClass, allowNullsBlanks);
                }
            } else {
                String attrKey = attr.substring(0, attr.indexOf("."));
                String attrValue = attr.substring(attr.indexOf(".") + 1);
                List<String> attrList = mapAttr.get(attrKey);
                if (attrList == null) {
                    attrList = new ArrayList();
                    mapAttr.put(attrKey, attrList);
                }
                attrList.add(attrValue);
            }
        }

        for (Map.Entry<String, List<String>> entry : mapAttr.entrySet()) {
            String attr = entry.getKey();
            List<String> listSubAttr = entry.getValue();

            Method method = ObjectUtil.getMethod(obj, attr);
            if (method == null) {
                throw new PhobosException("No existe el metodo GET o el metodo IS para el atributo: " + attr);
            }

            Class methodClass = method.getReturnType();
            if (methodClass == List.class) {
                List listObjAttr = null;
                try {
                    listObjAttr = (List) method.invoke(obj);
                } catch (Exception ex) {
                }
                if (listObjAttr == null) {
                    listObjAttr = new ArrayList();
                }

                ArrayNode array = new ArrayNode(jsonFactory);
                for (Object objAttr : listObjAttr) {
                    String[] attrObj = listSubAttr.toArray(new String[listSubAttr.size()]);
                    ObjectNode node = createJson(objAttr, jsonFactory, allowNullsBlanks, attrObj);
                    array.add(node);
                }

                json.set(attr, array);

            } else {
                Object objAttr = null;
                try {
                    objAttr = method.invoke(obj);
                } catch (Exception ex) {
                }
                if (objAttr == null) {
                    try {
                        Constructor konst = methodClass.getConstructor();
                        objAttr = konst.newInstance();
                    } catch (Exception ex) {
                        throw new PhobosException("El atributo " + attr + " no puede ser instanciado por un constructor de la forma: new Object()");
                    }
                }
                String[] attrObj = listSubAttr.toArray(new String[listSubAttr.size()]);
                ObjectNode jsonAttr = createJson(objAttr, jsonFactory, allowNullsBlanks, attrObj);
                json.set(attr, jsonAttr);
            }
        }

        return json;
    }

    private static void putAllAttr(ObjectNode json, Object obj, Method[] methods, Class objectClass, boolean allowNullsBlanks) {
        for (Method method : methods) {
            if (!(method.getName().startsWith("get") || method.getName().startsWith("is"))) {
                continue;
            }

            Object value = null;
            try {
                value = method.invoke(obj);
            } catch (Exception ex) {
                // logger.error(ex.getMessage());
            }

            if (!allowNullsBlanks && value == null) {
                continue;
            }

            Class methodClass = method.getReturnType();
            if (!TIPOS_DATOS.contains(methodClass) && !(value instanceof Enum)) {
                continue;
            }

            String methodName = method.getName().startsWith("get")
                    ? method.getName().substring(3)
                    : method.getName();
            String attr = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);

            if (value == null) {
                json.put(attr, "");
            } else if (value instanceof Date) {
                json.put(attr, getDateValue(objectClass, attr, (Date) value));
            } else if (value instanceof Time) {
                json.put(attr, ((Time) value).getTime());
            } else if (value instanceof Timestamp) {
                json.put(attr, getDateValue(objectClass, attr, new Date(((Timestamp) value).getTime())));
            } else if (value instanceof Integer) {
                json.put(attr, (Integer) value);
            } else if (value instanceof Double) {
                json.put(attr, (Double) value);
            } else if (value instanceof Float) {
                json.put(attr, (Float) value);
            } else if (value instanceof Long) {
                json.put(attr, (Long) value);
            } else if (value instanceof BigDecimal) {
                json.put(attr, (BigDecimal) value);
            } else if (value instanceof Character) {
                json.put(attr, (Character) value);
            } else if (value instanceof String) {
                json.put(attr, (String) value);
            } else if (value instanceof Boolean) {
                json.put(attr, (Boolean) value);
            } else if (value instanceof Enum) {
                setJsonEnum(json, attr, (Enum) value, allowNullsBlanks);
            } else {
                json.put(attr, value.toString());
            }
        }
    }

    private static void putOneAttr(ObjectNode json, Object obj, String attr, Class objectClass, boolean allowNullsBlanks) {
        Method method = ObjectUtil.getMethod(obj, attr);
        if (method == null) {
            throw new PhobosException("No existe el metodo GET o el metodo IS para el atributo: " + attr);
        }

        Class methodClass = method.getReturnType();
        Object value = null;

        try {
            value = method.invoke(obj);
        } catch (Exception ex) {
        }

        if (!allowNullsBlanks && value == null) {
            return;
        }

        if (!TIPOS_DATOS.contains(methodClass) && !(value instanceof Enum)) {
            return;
        }

        if (value == null) {
            json.put(attr, "");
        } else if (value instanceof Date) {
            json.put(attr, getDateValue(objectClass, attr, (Date) value));
        } else if (value instanceof Time) {
            json.put(attr, ((Time) value).getTime());
        } else if (value instanceof Timestamp) {
            json.put(attr, getDateValue(objectClass, attr, new Date(((Timestamp) value).getTime())));
        } else if (value instanceof Integer) {
            json.put(attr, (Integer) value);
        } else if (value instanceof Double) {
            json.put(attr, (Double) value);
        } else if (value instanceof Float) {
            json.put(attr, (Float) value);
        } else if (value instanceof Long) {
            json.put(attr, (Long) value);
        } else if (value instanceof BigDecimal) {
            json.put(attr, (BigDecimal) value);
        } else if (value instanceof Character) {
            json.put(attr, (Character) value);
        } else if (value instanceof String) {
            json.put(attr, (String) value);
        } else if (value instanceof Boolean) {
            json.put(attr, (Boolean) value);
        } else if (value instanceof Enum) {
            setJsonEnum(json, attr, (Enum) value, allowNullsBlanks);
        } else {
            json.put(attr, value.toString());
        }
    }

    private static void setJsonEnum(ObjectNode json, String attr, Enum enumValue, boolean allowNullsBlanks) {
        ObjectNode jsonEnum = new ObjectNode(JsonNodeFactory.instance);
        if (enumValue == null) {
            return;
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

        json.set(attr, jsonEnum);
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

    private static String getDateValue(Class objectClazz, String attr, Date date) {
        if (date == null) {
            return "";
        }

        Field ff = null;
        try {
            ff = objectClazz.getDeclaredField(attr);
        } catch (Exception ex) {
        }

        DateTime dt = new DateTime(date);
        if (ff != null && ff.isAnnotationPresent(Temporal.class)) {
            Temporal tt = (Temporal) ff.getAnnotation(Temporal.class);
            if (tt.value() == TemporalType.TIME) {
                return dt.toString("HH:mm:ss");
            }
        }

        String fecha = dt.toString("dd/MM/yyyy HH:mm:ss");
        return fecha.replaceAll("00:00:00", "").trim();
    }

    public static ObjectNode enumToJson(Object[] objects) {

        ObjectNode jsonEnum = new ObjectNode(JsonNodeFactory.instance);

        for (Object obj : objects) {

            ObjectNode jsonAttr = new ObjectNode(JsonNodeFactory.instance);
            jsonAttr.put("name", obj.toString());
            System.out.println(">>> " + obj.toString());

            for (Method method : obj.getClass().getDeclaredMethods()) {
                try {
                    System.out.println(method.getName());
                    if (!(method.getName().startsWith("get") && method.getGenericParameterTypes().length == 0)) {
                        System.out.println("\tNo se considera");
                        continue;
                    }
                    System.out.println("\tSacando su return value");
                    Object value = method.invoke(obj);
                    String attrEnum = Introspector.decapitalize(method.getName().substring(method.getName().startsWith("is") ? 2 : 3));

                    if (value == null) {
                        jsonAttr.put(attrEnum, "");
                    } else if (value instanceof Date) {
                        jsonAttr.put(attrEnum, new DateTime((Date) value).toString("dd/MM/yyyy"));
                    } else if (value instanceof Time) {
                        jsonAttr.put(attrEnum, ((Time) value).getTime());
                    } else if (value instanceof Timestamp) {
                        jsonAttr.put(attrEnum, new DateTime((Date) value).toString("dd/MM/yyyy HH:mm:ss"));
                    } else if (value instanceof Integer) {
                        jsonAttr.put(attrEnum, (Integer) value);
                    } else if (value instanceof Double) {
                        jsonAttr.put(attrEnum, (Double) value);
                    } else if (value instanceof Float) {
                        jsonAttr.put(attrEnum, (Float) value);
                    } else if (value instanceof Long) {
                        jsonAttr.put(attrEnum, (Long) value);
                    } else if (value instanceof BigDecimal) {
                        jsonAttr.put(attrEnum, (BigDecimal) value);
                    } else if (value instanceof Character) {
                        jsonAttr.put(attrEnum, (Character) value);
                    } else if (value instanceof String) {
                        jsonAttr.put(attrEnum, (String) value);
                    } else if (value instanceof Boolean) {
                        jsonAttr.put(attrEnum, (Boolean) value);
                    } else {
                        jsonAttr.put(attrEnum, value.toString());
                    }

                } catch (IllegalAccessException
                        | IllegalArgumentException
                        | InvocationTargetException e) {
                    logger.info(e.getLocalizedMessage());

                    logger.debug("Error", e);
                }
            }

            jsonEnum.set(obj.toString(), jsonAttr);
        }

        return jsonEnum;
    }

}

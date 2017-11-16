package pe.albatross.zelpers.miscelanea;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

public class JsonHelper {

    public static String TIPO_DATOS = "String_Integer_Long_BigDecimal_Float_Double_Char_Date";
    public static String TIPO_LISTAS = "List";
    private static final Logger logger = getLogger(JsonHelper.class);
    private String separador = ".";

    private static List<Class> TIPOS_DATOS
            = Arrays.asList(String.class, Integer.class, Long.class, Float.class, Double.class,
                    BigDecimal.class, Character.class, Date.class, Timestamp.class);

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
        ObjectNode json = new ObjectNode(jsonFactory);

        if (obj == null) {
            return null;
        }

        Class clazz = obj.getClass();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            Class claxx = method.getReturnType();
            if (!TIPOS_DATOS.contains(claxx)) {
                continue;
            }

            if (!method.getName().startsWith("get")) {
                continue;
            }

            try {
                Object value = method.invoke(obj);
                if (value == null) {
                    continue;
                }

                String methodName = method.getName().substring(3);
                String attr = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);

                if (value instanceof Date) {
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
                } else {
                    json.put(attr, value.toString());
                }

            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }
        }

        return json;
    }
}

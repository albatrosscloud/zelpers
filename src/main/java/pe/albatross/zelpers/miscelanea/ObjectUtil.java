package pe.albatross.zelpers.miscelanea;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectUtil {

    private static final Logger logger = LoggerFactory.getLogger(ObjectUtil.class);

    public static List<Class> TYPICAL_CLASSES = Arrays.asList(
            String.class, Integer.class, Long.class, BigDecimal.class, Float.class, Double.class, Timestamp.class, Date.class
    );

    public static Object completarAtributoObjeto(Object obj, String atributo) {
        Object objAttr = null;
        Method metodo = null;
        objAttr = getParent(obj, atributo);
        metodo = getMethod(obj, atributo);

        if (objAttr != null) {
            return objAttr;
        }

        try {
            String[] tipoReturnList = metodo.getReturnType().toString().split(" ");
            Class clasePadre = Class.forName(tipoReturnList[tipoReturnList.length - 1]);
            Constructor constructor = clasePadre.getConstructor();
            objAttr = constructor.newInstance();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        StringBuilder sbMetodo = new StringBuilder("set");
        sbMetodo.append(WordUtils.capitalize(atributo));
        Method[] metodos = obj.getClass().getMethods();

        for (Method metodoTmp : metodos) {
            if (sbMetodo.toString().equals(metodoTmp.getName())) {
                metodo = metodoTmp;
                break;
            }
        }

        try {
            metodo.invoke(obj, objAttr);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return objAttr;
    }

    public static void completarParents(Object obj, String parents) {
        Object padre = obj;
        String[] padreLista = parents.split("\\.");
        for (int i = 0; i < padreLista.length; i++) {
            String parent = padreLista[i];
            padre = completarAtributoObjeto(padre, parent);
        }

    }

    public static Object getParent(Object obj, String atributo) {
        Object parent = null;
        Method metodo = null;
        StringBuilder sbMetodo = new StringBuilder("get");
        sbMetodo.append(WordUtils.capitalize(atributo));

        for (Method metodoTmp : obj.getClass().getMethods()) {
            if (sbMetodo.toString().equals(metodoTmp.getName())) {
                metodo = metodoTmp;
                break;
            }
        }
        try {
            parent = metodo.invoke(obj);
        } catch (InvocationTargetException ex) {
        } catch (Exception ex) {
            logger.error("InvocationTargetException ::: " + ex.getLocalizedMessage());
        }

        return parent;
    }

    public static Object getParentTree(Object obj, String atributo) {
        Object objPadre = null;
        Object objHijo = obj;
        String[] attrs = atributo.split("\\.");

        for (String attr : attrs) {
            objPadre = getParent(objHijo, attr);
            if (objPadre == null) {
                return null;
            }
            objHijo = objPadre;
        }

        return objPadre;
    }

    public static Method getMethod(Object obj, String atributo) {
        Method metodo = null;
        StringBuilder sbMetodoGet = new StringBuilder("get");
        sbMetodoGet.append(WordUtils.capitalize(atributo));
        StringBuilder sbMetodoIs = new StringBuilder("is");
        sbMetodoIs.append(WordUtils.capitalize(atributo));
        StringBuilder sbMetodoEs = new StringBuilder("es");
        sbMetodoEs.append(WordUtils.capitalize(atributo));

        for (Method metodoTmp : obj.getClass().getMethods()) {
            if (sbMetodoGet.toString().equals(metodoTmp.getName())) {
                metodo = metodoTmp;
                break;
            }
            if (sbMetodoIs.toString().equals(metodoTmp.getName())) {
                metodo = metodoTmp;
                break;
            }
        }

        return metodo;
    }

    public static void eliminarAttrSinId(Object obj, String atributo) {
        Object objNieto = null;
        Object objPadre = null;
        Object objHijo = obj;

        String[] attrs = atributo.split("\\.");

        for (String attr : attrs) {
            objPadre = getParent(objHijo, attr);
            objNieto = objHijo;
            objHijo = objPadre;
            if (objPadre == null) {
                break;
            }
        }

        if (objPadre == null) {
            return;
        }

        Method metodo = null;
        for (Method metodoTmp : objHijo.getClass().getMethods()) {
            if ("getId".equals(metodoTmp.getName())) {
                metodo = metodoTmp;
                break;
            }
        }

        Object objId = null;

        try {
            objId = metodo.invoke(objHijo);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        if (objId == null) {
            String attr = attrs[attrs.length - 1];
            StringBuilder sbMetodo = new StringBuilder("set");
            sbMetodo.append(WordUtils.capitalize(attr));
            for (Method metodoTmp : objNieto.getClass().getMethods()) {
                if (sbMetodo.toString().equals(metodoTmp.getName())) {
                    metodo = metodoTmp;
                    break;
                }
            }

            try {
                metodo.invoke(objNieto, new Object[]{null});
            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }

        }

    }

    public static void eliminarAttrSinId(Object obj) {
        if (obj == null) {
            return;
        }

        for (Method metodo : obj.getClass().getMethods()) {
            if (metodo.getName().startsWith("get")) {
                Object attr = null;
                try {
                    attr = metodo.invoke(obj);
                } catch (Exception ex) {
                    logger.error(ex.getMessage());
                }

                if (attr == null) {
                    continue;
                }

                if (attr.getClass().equals(Class.class)) {
                    continue;
                }
                if (TYPICAL_CLASSES.contains(attr.getClass())) {
                    continue;
                }
                if (!tieneAttrId(attr)) {
                    continue;
                }
                if (tieneIdNull(attr)) {
                    String metodoSet = "set" + metodo.getName().substring(3);
                    setValorNull(obj, metodoSet);
                    continue;
                }
                eliminarAttrSinId(attr);
            }
        }

    }

    private static void setValorNull(Object obj, String attr) {
        for (Method metodo : obj.getClass().getMethods()) {
            if (metodo.getName().equals(attr)) {
                try {
                    metodo.invoke(obj, new Object[]{null});
                } catch (Exception ex) {
                    logger.error(ex.getMessage());
                }
            }
        }
    }

    private static boolean tieneIdNull(Object obj) {
        for (Method metodo : obj.getClass().getMethods()) {
            if (metodo.getName().equals("getId")) {
                Object attr = null;
                try {
                    attr = metodo.invoke(obj);
                } catch (Exception ex) {
                    logger.error(ex.getMessage());
                }
                if (attr == null) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    private static boolean tieneAttrId(Object obj) {
        for (Method metodo : obj.getClass().getMethods()) {
            if (metodo.getName().equals("getId")) {
                return true;
            }
        }
        return false;
    }

    public static void printAttr(Object obj) {
        if (obj == null) {
            System.out.println("Object no existe para imprimir sus atributos");
            return;
        }

        System.out.println("/======================================================================\\");
        Method[] methods = obj.getClass().getMethods();
        System.out.println("| class: " + obj.getClass().getName());
        System.out.println("|======================================================================|");

        for (Method method : methods) {
            if (method.getName().startsWith("get") && method.getGenericParameterTypes().length == 0) {
                try {
                    Object returnObject = method.invoke(obj);
                    System.out.println("|\t" + method.getName() + " - " + returnObject);
                } catch (Exception ex) {
                    logger.error(method.getName() + ": " + ex.getMessage());
                }
            }

        }
        System.out.println("\\======================================================================/");
    }

    public static boolean verificarIgualdad(Object obj1, Object obj2, List<String> atributos) {
        if (atributos.size() == 0) {
            throw new PhobosException("Lista de parametros vacio");
        }

        int i = 0;
        for (String atributo : atributos) {
            try {
                i += compararAtributos(getParentTree(obj1, atributo), getParentTree(obj2, atributo));
            } catch (Exception ex) {
                logger.debug(ex.getLocalizedMessage());
            }
        }

        if (i > 0) {
            return false;
        } else {
            return true;
        }
    }

    public static Integer compararAtributos(Object attr1, Object attr2) {

        if (attr1 == null && attr2 == null) {
            return 0;
        }
        if (attr1 == null && attr2 != null) {
            return 1;
        }
        if (attr1 != null && attr2 == null) {
            return 1;
        }

        if (attr1.getClass() != attr2.getClass()) {
            if (!attr1.getClass().getSimpleName().equals(attr2.getClass().getSimpleName())) {
                return 1;
            }
        }

        if (!TYPICAL_CLASSES.contains(attr1.getClass())) {
            throw new PhobosException("Solo se puede comparar los atributos básicos");
        }

        if (!TYPICAL_CLASSES.contains(attr2.getClass())) {
            throw new PhobosException("Solo se puede comparar los atributos básicos");
        }

        if (attr1 instanceof BigDecimal) {
            BigDecimal d1 = (BigDecimal) attr1;
            BigDecimal d2 = (BigDecimal) attr2;
            int iguales = (d1.compareTo(d2) == 0) ? 0 : 1;
            return iguales;
        }
        if (attr1 instanceof Double) {
            return (((Double) attr1).compareTo((Double) attr2) == 0) ? 0 : 1;
        }
        if (attr1 instanceof Float) {
            return (((Float) attr1).compareTo((Float) attr2) == 0) ? 0 : 1;
        }

        if (attr1.getClass().getSimpleName().equals("Date")) {
            Date fecha1 = (Date) attr1;
            Date fecha2 = (Date) attr2;
            return fecha1.getTime() == fecha2.getTime() ? 0 : 1;
        }

        return Math.abs(attr1.toString().compareTo(attr2.toString()));
    }

    public static boolean equalsAttrs(Object obj1, Object obj2, List<String> atributos) {
        if (atributos.size() == 0) {
            throw new PhobosException("Lista de parametros vacio");
        }

        int i = 0;
        for (String atributo : atributos) {
            try {
                i += equalAttr(getParentTree(obj1, atributo), getParentTree(obj2, atributo));
            } catch (Exception ex) {
                logger.debug(ex.getLocalizedMessage());
            }
        }
        return i <= 0;
    }

    public static Integer equalAttr(Object attr1, Object attr2) {

        if (attr1 == null && attr2 == null) {
            return 0;
        }
        if (attr1 == null && attr2 != null) {
            return 1;
        }
        if (attr1 != null && attr2 == null) {
            return 1;
        }

        if (attr1.getClass() != attr2.getClass()) {
            if (!attr1.getClass().getSimpleName().equals(attr2.getClass().getSimpleName())) {
                return 1;
            }
        }

        if (attr1 instanceof BigDecimal) {
            BigDecimal d1 = (BigDecimal) attr1;
            BigDecimal d2 = (BigDecimal) attr2;
            int iguales = (d1.compareTo(d2) == 0) ? 0 : 1;
            return iguales;
        }
        if (attr1 instanceof Double) {
            return (((Double) attr1).compareTo((Double) attr2) == 0) ? 0 : 1;
        }
        if (attr1 instanceof Float) {
            return (((Float) attr1).compareTo((Float) attr2) == 0) ? 0 : 1;
        }

        if (attr1.getClass().getSimpleName().equals("Date")) {
            Date fecha1 = (Date) attr1;
            Date fecha2 = (Date) attr2;
            return fecha1.getTime() == fecha2.getTime() ? 0 : 1;
        }

        return Math.abs(attr1.toString().compareTo(attr2.toString()));
    }

    public static Object obtenerCampo(Object obj, String nombre) throws Exception {
        Field f = obj.getClass().getDeclaredField(nombre);
        f.setAccessible(true);
        Object iWantThis = f.get(obj);
        return iWantThis;
    }
}

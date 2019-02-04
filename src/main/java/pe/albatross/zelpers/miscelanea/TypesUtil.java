package pe.albatross.zelpers.miscelanea;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.util.DigestUtils;

public class TypesUtil {

    public static Integer getInt(Object objValue) {
        try {
            return Integer.valueOf(objValue.toString());

        } catch (Exception e) {
            return null;
        }
    }

    public static Integer getInt(Object objValue, Integer defaultValue) {
        try {

            return Integer.valueOf(objValue.toString());

        } catch (Exception e) {
            return defaultValue;
        }

    }

    public static Long getLong(Object objValue) {
        try {
            if (objValue instanceof java.lang.Long) {
                return (Long) objValue;
            } else {
                return Long.valueOf(objValue.toString());
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static Long getLong(BigDecimal bigDecimalValue) {
        try {
            return bigDecimalValue.longValue();
        } catch (Exception e) {
            return 0L;
        }

    }

    public static Long getLong(Object objValue, Long defaultValue) {
        try {
            return Long.valueOf(objValue.toString());

        } catch (Exception e) {
            return defaultValue;
        }

    }

    public static BigDecimal getBigDecimal(Object objValue) {
        try {
            return BigDecimal.valueOf(Double.valueOf(objValue.toString()));

        } catch (Exception e) {

            return null;
        }
    }

    public static BigDecimal getBigDecimal(Object objValue, double defaultValue) {
        try {

            return BigDecimal.valueOf(Double.valueOf(objValue.toString()));

        } catch (Exception e) {
            return new BigDecimal(defaultValue);
        }

    }

    public static Float getFloat(Object objValue, Float defaultValue) {

        try {

            return Float.parseFloat(objValue.toString());

        } catch (Exception e) {

            return defaultValue;
        }

    }

    public static Double getDouble(BigDecimal value) {
        try {
            return value.doubleValue();

        } catch (Exception e) {
            return null;
        }

    }

    public static double getDouble(Object objValue, double defaultValue) {
        try {

            if (objValue instanceof BigDecimal) {
                return ((BigDecimal) objValue).doubleValue();
            } else {
                return Double.parseDouble(objValue.toString());
            }

        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static boolean getBoolean(Object value) {
        return ((Integer) value == 1);
    }

    public static String getString(Object objValue) {
        try {
            return objValue.toString();

        } catch (Exception e) {
            return null;
        }

    }

    public static String getStringDate(Date fecha, String formato) {
        if (fecha == null) {
            return null;
        }
        DateTime hoy = new DateTime(fecha);
        DateTimeFormatter fmt = DateTimeFormat.forPattern(formato);
        return fmt.print(hoy);
    }

    public static String getStringDate(Date fecha, String formato, String language) {
        if (fecha == null) {
            return null;
        }
        DateTime hoy = new DateTime(fecha);
        DateTimeFormatter fmt = DateTimeFormat.forPattern(formato);
        return fmt.withLocale(new Locale(language)).print(hoy);
    }

    public static String getStringDate(Date fecha, String formato, String language, String country) {
        if (fecha == null) {
            return null;
        }
        DateTime hoy = new DateTime(fecha);
        DateTimeFormatter fmt = DateTimeFormat.forPattern(formato);
        return fmt.withLocale(new Locale(language, country)).print(hoy);
    }

    public static String getCurrentStringDate(String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = new Date();
        return sdf.format(date);
    }

    public static String getStringDate(Timestamp fecha, String format) {
        if (fecha == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, new Locale("ES"));
        return sdf.format(fecha);
    }

    public static Long getTimestamp(Date fecha, String hora) throws PhobosException {

        try {
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            DateFormat dfh = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            if (hora.equals("")) {
                hora = "00:00";
            }
            String fechaTexto = df.format(fecha) + " ";
            return dfh.parse(fechaTexto + hora).getTime();
        } catch (Exception e) {
            throw new PhobosException(e.getMessage());
        }

    }

    public static Integer getRandom() {

        return new Random().nextInt(900000) + 100000;

    }

    public static String toMD5(String pass) throws PhobosException {

        try {

            byte[] bytesOfMessage = pass.getBytes("UTF-8");
            return DigestUtils.md5DigestAsHex(bytesOfMessage);

        } catch (Exception e) {
            throw new PhobosException(e.getMessage());
        }

    }

    public static String getClean(String string) {
        return StringUtils.stripAccents(string).trim();
    }

    public static Long getUnixTime() {
        return System.currentTimeMillis();
    }

    public static String getStringDateDayMonthFormat(Date date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd 'de' MMMMM", new Locale("es", "ES"));
            String retorno = sdf.format(date);
            return retorno;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getStringDateDayMonthAltFormat(Date date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMMM", new Locale("es", "ES"));
            String retorno = sdf.format(date);
            return retorno;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getStringDateLongFormat(Date date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMMM 'del' yyyy", new Locale("es", "ES"));
            String retorno = sdf.format(date);
            return retorno;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getStringDateTimeLongFormat(Date date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd 'de' MMMMM 'del' yyyy 'a las' hh:mm a", new Locale("es", "ES"));
            String retorno = sdf.format(date);
            return retorno;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getStringDateDayMonthNsFormat(Date date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", new Locale("es", "ES"));
            String retorno = sdf.format(date);
            return retorno;
        } catch (Exception e) {
            return null;
        }
    }

    public static Map convertListToMap(String attr, List items) {
        Map map = new LinkedHashMap();
        items.forEach((item) -> {
            Object key = ObjectUtil.getParentTree(item, attr);
            if (!(key == null)) {
                map.put(key, item);
            }
        });
        return map;
    }

    public static Map convertListToMap(String attrKey, String attrValue, List items) {
        Map map = new LinkedHashMap();
        items.forEach((item) -> {
            Object key = ObjectUtil.getParentTree(item, attrKey);
            if (!(key == null)) {
                Object val = ObjectUtil.getParentTree(item, attrValue);
                map.put(key, val);
            }
        });
        return map;
    }

    public static Map convertListToMapList(String attr, List items) {
        Map map = new LinkedHashMap();
        items.forEach((item) -> {
            Object key = ObjectUtil.getParentTree(item, attr);
            if (!(key == null)) {
                List lista = (List) map.get(key);
                if (lista == null) {
                    lista = new ArrayList();
                    map.put(key, lista);
                }
                lista.add(item);
            }
        });
        return map;
    }

    public static Map convertListToMapList(String attrKey, String attrValue, List items) {
        Map map = new LinkedHashMap();
        items.forEach((item) -> {
            Object key = ObjectUtil.getParentTree(item, attrKey);
            if (!(key == null)) {
                Object val = ObjectUtil.getParentTree(item, attrValue);
                List lista = (List) map.get(key);
                if (lista == null) {
                    lista = new ArrayList();
                    map.put(key, lista);
                }
                lista.add(val);
            }
        });
        return map;
    }

    public static List extractListByAttr(String attr, List items) {
        List list = new ArrayList();
        items.forEach((item) -> {
            Object obj = ObjectUtil.getParentTree(item, attr);
            list.add(obj);
        });
        return list;
    }

    public static ListsInspector analizeLists(List listDB, List listForm, String attr) {
        if (listDB == null) {
            listDB = new ArrayList();
        }
        if (listForm == null) {
            listForm = new ArrayList();
        }

        Map mapListDB = TypesUtil.convertListToMap(attr, listDB);
        Map mapListForm = TypesUtil.convertListToMap(attr, listForm);

        List newList = new ArrayList();
        List deadList = new ArrayList();
        List oldListDB = new ArrayList();
        List oldListForm = new ArrayList();

        for (Object itemDB : listDB) {
            Object idDB = ObjectUtil.getParentTree(itemDB, attr);
            Object itemForm = mapListForm.get(idDB);
            if (itemForm == null) {
                deadList.add(itemDB);
                continue;
            }
            oldListDB.add(itemDB);
            oldListForm.add(itemForm);
        }

        for (Object itemForm : listForm) {
            Object idForm = ObjectUtil.getParentTree(itemForm, attr);
            Object itemDB = mapListDB.get(idForm);
            if (itemDB == null) {
                newList.add(itemForm);
            }
        }

        ListsInspector inspector = new ListsInspector();
        inspector.setDeadList(deadList);
        inspector.setListDB(listDB);
        inspector.setListForm(listForm);
        inspector.setNewList(newList);
        inspector.setOldListDB(oldListDB);
        inspector.setOldListForm(oldListForm);

        return inspector;
    }

    public static void delay(long mseg) {
        long t1 = System.currentTimeMillis();
        for (;;) {
            long t2 = System.currentTimeMillis();
            if (t2 - t1 > mseg) {
                break;
            }
        }
    }

    public String capitalizeWord(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        str = str.trim().replaceAll("\\s{2,}", " ").trim();
        String words[] = str.split("\\s");
        String capitalizeWord = "";
        for (String w : words) {
            String first = w.substring(0, 1);
            String afterfirst = w.substring(1);
            capitalizeWord += first.toUpperCase() + afterfirst.toLowerCase() + " ";
        }
        return capitalizeWord.trim();
    }

}

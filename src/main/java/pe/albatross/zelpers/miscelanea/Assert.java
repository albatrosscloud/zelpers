package pe.albatross.zelpers.miscelanea;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Assert {

    private final static List<Class> BASIC_CLASS = Arrays.asList(
            String.class, Long.class, Double.class, Integer.class, Float.class, BigDecimal.class,
            Date.class, java.sql.Date.class, Boolean.class);

    private final static List<Class> NUMBER_CLASS = Arrays.asList(
            Long.class, Double.class, Integer.class, Float.class, BigDecimal.class);

    private final static List<Class> DATE_CLASS = Arrays.asList(Date.class, java.sql.Date.class);

    public final static String INCOMPATIBLE_CLASSES = "Tipo de datos incompatibles para comparar";

    public static void isNull(Object obj, String msg) {
        if (obj != null) {
            throw new PhobosException(msg);
        }
    }

    public static void isNotNull(Object obj, String msg) {
        isNotNull(obj, msg, true);
    }

    public static void isNotNull(Object obj, String msg, boolean checkString) {
        if (obj == null) {
            throw new PhobosException(msg);
        }

        if (checkString) {
            if (obj instanceof String) {
                if (((String) obj).trim().equals("")) {
                    throw new PhobosException(msg);
                }
            }
        }
    }

    public static void isNotBlank(String string, String msg) {
        if (string == null) {
            throw new PhobosException(msg);
        }
        if (string.trim().equals("")) {
            throw new PhobosException(msg);
        }
    }

    public static void isBlank(String string, String msg) {
        if (string == null) {
            return;
        }
        if (!string.trim().equals("")) {
            throw new PhobosException(msg);
        }
    }

    public static void isFalse(Boolean condition, String msg) {
        if (condition) {
            throw new PhobosException(msg);
        }
    }

    public static void isTrue(Boolean condition, String msg) {
        if (!condition) {
            throw new PhobosException(msg);
        }
    }

    private static boolean isEqual(Object input1, Object input2) {
        if (input1 == null && input2 == null) {
            return true;
        } else if (input1 != null && input2 == null) {
            return false;
        } else if (input1 == null && input2 != null) {
            return false;
        }

        if (BASIC_CLASS.contains(input1.getClass()) && BASIC_CLASS.contains(input2.getClass())) {
            return input1.toString().equals(input2.toString());
        }

        if (input1.equals(input2)) {
            return true;
        }

        if (input1.toString().equals(input2.toString())) {
            return true;
        }

        return false;
    }

    public static void isEqual(Object input1, Object input2, String msg) {
        if (isEqual(input1, input2)) {
            return;
        }
        throw new PhobosException(msg);
    }

    public static void isNotEqual(Object input1, Object input2, String msg) {
        if (!isEqual(input1, input2)) {
            return;
        }
        throw new PhobosException(msg);
    }

    public static void isGeatherThan(Object input1, Object input2, String msg) {
        if (input1 == null && input2 == null) {
            throw new PhobosException(msg);
        } else if (input1 != null && input2 == null) {
            return;
        } else if (input1 == null && input2 != null) {
            throw new PhobosException(msg);
        }

        if (NUMBER_CLASS.contains(input1.getClass()) && NUMBER_CLASS.contains(input2.getClass())) {
            BigDecimal bd1 = new BigDecimal(input1.toString());
            BigDecimal bd2 = new BigDecimal(input2.toString());
            if (bd1.compareTo(bd2) > 0) {
                return;
            }
            throw new PhobosException(msg);
        }

        if (DATE_CLASS.contains(input1.getClass()) && DATE_CLASS.contains(input2.getClass())) {
            Date date1 = (Date) input1;
            Date date2 = (Date) input2;
            if (date1.compareTo(date2) > 0) {
                return;
            }
            throw new PhobosException(msg);
        }

        if (input1 instanceof String && input2 instanceof String) {
            String s1 = (String) input1;
            String s2 = (String) input2;
            if (s1.compareTo(s2) > 0) {
                return;
            }
            throw new PhobosException(msg);
        }

        throw new PhobosException(INCOMPATIBLE_CLASSES);
    }

    public static void isLessThan(Object input1, Object input2, String msg) {
        if (input1 == null && input2 == null) {
            throw new PhobosException(msg);
        } else if (input1 != null && input2 == null) {
            throw new PhobosException(msg);
        } else if (input1 == null && input2 != null) {
            return;
        }

        if (NUMBER_CLASS.contains(input1.getClass()) && NUMBER_CLASS.contains(input2.getClass())) {
            BigDecimal bd1 = new BigDecimal(input1.toString());
            BigDecimal bd2 = new BigDecimal(input2.toString());
            if (bd1.compareTo(bd2) < 0) {
                return;
            }
            throw new PhobosException(msg);
        }

        if (DATE_CLASS.contains(input1.getClass()) && DATE_CLASS.contains(input2.getClass())) {
            Date date1 = (Date) input1;
            Date date2 = (Date) input2;
            if (date1.compareTo(date2) < 0) {
                return;
            }
            throw new PhobosException(msg);
        }

        if (input1 instanceof String && input2 instanceof String) {
            String s1 = (String) input1;
            String s2 = (String) input2;
            if (s1.compareTo(s2) < 0) {
                return;
            }
            throw new PhobosException(msg);
        }

        throw new PhobosException(INCOMPATIBLE_CLASSES);
    }

}

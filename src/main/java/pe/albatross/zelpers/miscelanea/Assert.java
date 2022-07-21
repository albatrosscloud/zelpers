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

    public static void isEquals(Object input1, Object input2, String msg) {
        if (input1 == null && input2 == null) {
            return;

        } else if (input1 != null && input2 == null) {
            throw new PhobosException(msg);

        } else if (input1 == null && input2 != null) {
            throw new PhobosException(msg);
        }

        if (BASIC_CLASS.contains(input1.getClass()) && BASIC_CLASS.contains(input2.getClass())) {
            if (input1.toString().equals(input2.toString())) {
                return;
            }
            throw new PhobosException(msg);
        }

        if (input1.equals(input2)) {
            return;
        }

        if (input1.toString().equals(input2.toString())) {
            return;
        }

        throw new PhobosException(msg);
    }

}

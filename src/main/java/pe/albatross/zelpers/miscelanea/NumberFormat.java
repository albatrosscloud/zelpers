package pe.albatross.zelpers.miscelanea;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NumberFormat {

    public static String dec(Object value) {
        if (value == null) {
            return null;
        }

        DecimalFormat myFormatter = new DecimalFormat("#####0.######", new DecimalFormatSymbols(new Locale("pe", "PE")));
        return myFormatter.format(value);
    }

    

    public static String notaDecimal(Object value) {
        return NumberFormat.notaDecimalXDecimals(value, 2);
    }

    public static String notaDecimal4Decimals(Object value) {
        return NumberFormat.notaDecimalXDecimals(value, 4);
    }

    public static String notaDecimal10Decimals(Object value) {
        return NumberFormat.notaDecimalXDecimals(value, 10);
    }

    public static String notaDecimalXDecimals(Object value, int decimales) {
        if (value == null) {
            return null;
        }

        String cod = NumberFormat.codigo(0, decimales);

        DecimalFormat myFormatter = new DecimalFormat("00." + cod, new DecimalFormatSymbols(new Locale("pe", "PE")));
        return myFormatter.format(value);
    }

    public static String nota(Object value) {
        if (value == null) {
            return null;
        }

        DecimalFormat myFormatter = new DecimalFormat("00", new DecimalFormatSymbols(new Locale("pe", "PE")));
        return myFormatter.format(value);
    }
    
    public static String precio(Object value) {
        return NumberFormat.precioMini(value, 2);
    }

    public static String precioMini(Object value, int ancho) {
        if (value == null) {
            return null;
        }

        StringBuilder frmt = new StringBuilder();
        for (int i = 0; i < ancho; i++) {
            frmt.append('0');
        }

        DecimalFormat myFormatter = new DecimalFormat("###,##0." + frmt, new DecimalFormatSymbols(new Locale("pe", "PE")));
        return myFormatter.format(value);
    }

    public static String medida(Object value) {
        return NumberFormat.precioMini(value, 8);
    }

    public static String codigo(Object value, int ancho) {
        return codigo(value, ancho, '0');
    }

    public static String codigo(Object value, int ancho, char relleno) {
        if (value == null) {
            return null;
        }

        if (relleno == '0') {
            StringBuilder cod = new StringBuilder();
            for (int i = 0; i < ancho; i++) {
                cod.append(relleno);
            }
            DecimalFormat myFormatter = new DecimalFormat(cod.toString(), new DecimalFormatSymbols(new Locale("pe", "PE")));
            return myFormatter.format(value);

        } else {
            String val = String.valueOf(value);
            while (val.length() < ancho) {
                val = relleno + val;
            }
            return val;
        }

    }

}

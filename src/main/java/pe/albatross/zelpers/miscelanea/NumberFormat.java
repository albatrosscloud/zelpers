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

    public static String precio(Object value) {
        if (value == null) {
            return null;
        }

        DecimalFormat myFormatter = new DecimalFormat("###,##0.00", new DecimalFormatSymbols(new Locale("pe", "PE")));
        return myFormatter.format(value);
    }

    public static String notaDecimal(Object value) {
        if (value == null) {
            return null;
        }

        DecimalFormat myFormatter = new DecimalFormat("00.00", new DecimalFormatSymbols(new Locale("pe", "PE")));
        return myFormatter.format(value);
    }

    public static String notaDecimal4Decimals(Object value) {
        if (value == null) {
            return null;
        }

        DecimalFormat myFormatter = new DecimalFormat("00.0000", new DecimalFormatSymbols(new Locale("pe", "PE")));
        return myFormatter.format(value);
    }

    public static String notaDecimal10Decimals(Object value) {
        if (value == null) {
            return null;
        }

        DecimalFormat myFormatter = new DecimalFormat("00.0000000000", new DecimalFormatSymbols(new Locale("pe", "PE")));
        return myFormatter.format(value);
    }

    public static String nota(Object value) {
        if (value == null) {
            return null;
        }

        DecimalFormat myFormatter = new DecimalFormat("00", new DecimalFormatSymbols(new Locale("pe", "PE")));
        return myFormatter.format(value);
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
        if (value == null) {
            return null;
        }

        DecimalFormat myFormatter = new DecimalFormat("###,##0.########", new DecimalFormatSymbols(new Locale("pe", "PE")));
        return myFormatter.format(value);
    }

    public static String codigo(Object value, int ancho) {
        if (value == null) {
            return null;
        }
        StringBuilder cod = new StringBuilder();
        for (int i = 0; i < ancho; i++) {
            cod.append('0');
        }
        DecimalFormat myFormatter = new DecimalFormat(cod.toString(), new DecimalFormatSymbols(new Locale("pe", "PE")));
        return myFormatter.format(value);

    }

}

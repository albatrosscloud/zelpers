package pe.albatross.zelpers.miscelanea;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.TreeMap;

public class NumberFormat {

    private final static TreeMap<Integer, String> mapRomans = new TreeMap();

    static {

        mapRomans.put(1000, "M");
        mapRomans.put(900, "CM");
        mapRomans.put(500, "D");
        mapRomans.put(400, "CD");
        mapRomans.put(100, "C");
        mapRomans.put(90, "XC");
        mapRomans.put(50, "L");
        mapRomans.put(40, "XL");
        mapRomans.put(10, "X");
        mapRomans.put(9, "IX");
        mapRomans.put(5, "V");
        mapRomans.put(4, "IV");
        mapRomans.put(1, "I");

    }

    public final static String roman(int number) {
        int floor = mapRomans.floorKey(number);
        if (number == floor) {
            return mapRomans.get(number);
        }

        return mapRomans.get(floor) + roman(number - floor);
    }

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
        if (value == null) {
            return null;
        }

        DecimalFormat myFormatter = new DecimalFormat("###,##0.########", new DecimalFormatSymbols(new Locale("pe", "PE")));
        return myFormatter.format(value);
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

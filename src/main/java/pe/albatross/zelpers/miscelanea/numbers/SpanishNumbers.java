package pe.albatross.zelpers.miscelanea.numbers;

import java.text.DecimalFormat;

public class SpanishNumbers {

    private static final String[] decenas = {
        "", " diez", " veinte", " treinta", " cuarenta", " cincuenta", " sesenta", " setenta", " ochenta", " noventa"
    };

    private static final String[] numeros = {
        "", " uno", " dos", " tres", " cuatro", " cinco", " seis", " siete", " ocho", " nueve", " diez", " once", " doce", " trece", " catorce", " quince", " dieciséis", " diecisiete", " dieciocho", " diecinueve"
    };

    private static String convertLessThanOneThousand(int number) {
        String soFar;

        if (number % 100 < 20) {
            soFar = numeros[number % 100];
            number /= 100;
        } else {
            soFar = numeros[number % 10];
            number /= 10;

            if (number % 10 == 2) {
                soFar = " veinti" + soFar.trim();

            } else {
                soFar = decenas[number % 10] + " y" + soFar;
            }
            number /= 10;
        }

        if (number == 0) {
            return soFar;
        }

        if (number == 1 && soFar.equals("")) {
            return " cien" + soFar;

        } else if (number == 1 && !soFar.equals("")) {
            return " ciento" + soFar;
            
        } else if (number == 5 && !soFar.equals("")) {
            return " quinientos" + soFar;
            
        } else if (number == 7 && !soFar.equals("")) {
            return " setecientos" + soFar;
            
        } else if (number == 9 && !soFar.equals("")) {
            return " novecientos" + soFar;

        } else {
            return numeros[number] + "cientos" + soFar;
        }
    }

    public static String convert(long number) {
        // 0 to 999 999 999 999
        if (number == 0) {
            return "cero";
        }

        String snumber = new DecimalFormat("000000000000").format(number);

        // XXXnnnnnnnnn
        int billions = Integer.parseInt(snumber.substring(0, 3));

        // nnnXXXnnnnnn
        int millions = Integer.parseInt(snumber.substring(3, 6));

        // nnnnnnXXXnnn
        int hundredThousands = Integer.parseInt(snumber.substring(6, 9));

        // nnnnnnnnnXXX
        int thousands = Integer.parseInt(snumber.substring(9, 12));

        String tradBillions;
        switch (billions) {
            case 0:
                tradBillions = "";
                break;
            case 1:
                tradBillions = convertLessThanOneThousand(billions)
                        + " mil millones ";
                break;
            default:
                tradBillions = convertLessThanOneThousand(billions)
                        + " mil millones ";
        }
        String result = tradBillions;

        String tradMillions;
        switch (millions) {
            case 0:
                tradMillions = "";
                break;
            case 1:
                tradMillions = "un millon ";
                break;
            default:
                tradMillions = convertLessThanOneThousand(millions)
                        + " millones ";
        }
        result = result + tradMillions;

        String tradHundredThousands;
        switch (hundredThousands) {
            case 0:
                tradHundredThousands = "";
                break;
            case 1:
                tradHundredThousands = "mil ";
                break;
            default:
                tradHundredThousands = convertLessThanOneThousand(hundredThousands)
                        + " mil ";
        }
        result = result + tradHundredThousands;

        String tradThousand;
        tradThousand = convertLessThanOneThousand(thousands);
        result = result + tradThousand;

        return result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");
    }
}

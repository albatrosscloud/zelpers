package pe.albatross.zelpers.miscelanea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CodeGenerator {

    public static String getNextCode(List<String> codes, int inicio) {
        List<String> codex = new ArrayList(codes);
        Collections.sort(codex);
        List<String> codez = getCodes(inicio, codex.size() + 1);

        for (int i = 0; i < codex.size(); i++) {
            String c1 = codex.get(i);
            String c2 = codez.get(i);
            if (!c1.equals(c2)) {
                return c2;
            }
        }
        return codez.get(codez.size() - 1);
    }

    public static String getNextCode4(List<String> codes, String codeGroup) {
        List<String> codex = new ArrayList(codes);
        Collections.sort(codex);
        List<String> codez = getCodes4(codeGroup);

        for (int i = 0; i < codex.size(); i++) {
            String c1 = codex.get(i);
            String c2 = codez.get(i);
            if (!c1.equals(c2)) {
                return c2;
            }
        }
        return codez.get(codex.size());
    }

    public static List<String> getCodes(int inicio, int cantidad) {
        List<String> codes = new ArrayList();
        for (int i = inicio; i < cantidad + inicio; i++) {
            codes.add(getCode(i));
        }
        return codes;
    }

    public static List<String> getCodes4(String codeGroup) {
        List<String> codes = new ArrayList();
        for (int i = 0; i < 10; i++) {
            codes.add(codeGroup + i);
        }
        return codes;
    }

    private static String getCode(int i) {
        if (i < 1000) {
            return NumberFormat.codigo(i, 3, '0');
        }
        int r = i % 100;
        int q = cociente(0, i -1000, 100);
        String le = Character.toString((char) (65 + q));
        return le + NumberFormat.codigo(r, 2, '0');
    }

    private static int cociente(int acumulador, int dvdo, int dvsor) {
        if (dvdo > dvsor) {
            return cociente(acumulador + 1, dvdo - dvsor, dvsor);
        } else {
            return acumulador;
        }
    }

}

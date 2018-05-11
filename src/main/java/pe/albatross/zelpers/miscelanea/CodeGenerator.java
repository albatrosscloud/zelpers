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

    public static List<String> getCodes(int inicio, int cantidad) {
        List<String> codes = new ArrayList();
        for (int i = inicio; i < cantidad + inicio; i++) {
            codes.add(getCode(i));
        }
        return codes;
    }

    private static String getCode(int i) {
        if (i < 100) {
            return NumberFormat.codigo(i, 3, '0');
        }
        int r = i % 100;
        int q = cociente(0, i + 1, 100);
        String le = Character.toString((char) (64 + q));
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

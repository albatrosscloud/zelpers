package pe.albatross.zelpers.miscelanea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Commutator {

    public static List<Map<Long, Object>> create(Map<Long, Object> mapDias) {
        Map<Long, Object> mapTempo = new LinkedHashMap();
        Map<String, String> existentes = new LinkedHashMap();
        List<Map<Long, Object>> busquedas = new ArrayList();

        for (int i = 0; i < mapDias.size(); i++) {
            findConmutation(mapDias, mapTempo, i + 1, busquedas, existentes);
        }
        return busquedas;
    }

    public static void findConmutation(Map<Long, Object> mapDias, Map<Long, Object> mapTempo, int cant,
            List<Map<Long, Object>> busquedas, Map<String, String> existentes) {

        Map<Long, Object> mapCortado = cloneMap(mapDias);
        for (Map.Entry<Long, Object> entry : mapDias.entrySet()) {
            mapCortado = cutFirstMap(mapDias);
            Long dia = entry.getKey();
            Object horasDia = mapTempo.get(dia);
            if (horasDia != null) {
                continue;
            }
            if (mapTempo.size() + 1 == cant) {
                addItem(dia, entry.getValue(), mapTempo, busquedas, existentes);
            } else {
                if (mapDias.size() > 1) {
                    Map<Long, Object> mapTempo2 = cloneMap(mapTempo);
                    mapTempo2.put(dia, entry.getValue());
                    findConmutation(mapCortado, mapTempo2, cant, busquedas, existentes);
                }
            }
        }
    }

    private static void addItem(Long dia, Object horas, Map<Long, Object> mapTempo,
            List<Map<Long, Object>> busquedas, Map<String, String> existentes) {

        List<Long> keys = new ArrayList(mapTempo.keySet());
        keys.add(dia);
        Collections.sort(keys);
        String dias = "";
        for (Long key : keys) {
            dias += dias.equals("") ? "" : "-";
            dias += key;
        }
        String hay = existentes.get(dias);
        if (hay != null) {
            return;
        }

        Map<Long, Object> mapHallado = cloneMap(mapTempo);
        mapHallado.put(dia, horas);
        busquedas.add(mapHallado);
        existentes.put(dias, dias);
    }

    private static Map<Long, Object> cloneMap(Map<Long, Object> mapTempo) {
        Map<Long, Object> mapClone = new LinkedHashMap();
        for (Map.Entry<Long, Object> entry : mapTempo.entrySet()) {
            mapClone.put(entry.getKey(), entry.getValue());
        }
        return mapClone;
    }

    private static Map<Long, Object> cutFirstMap(Map<Long, Object> mapTempo) {
        int loop = 0;
        Map<Long, Object> mapClone = new LinkedHashMap();
        for (Map.Entry<Long, Object> entry : mapTempo.entrySet()) {
            if (loop > 0) {
                mapClone.put(entry.getKey(), entry.getValue());
            }
            loop++;
        }
        return mapClone;
    }

}

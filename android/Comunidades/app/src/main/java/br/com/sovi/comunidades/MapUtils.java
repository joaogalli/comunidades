package br.com.sovi.comunidades;

import java.util.HashMap;
import java.util.Map;

public abstract class MapUtils {

    public static <T, R> Map<T, R> buildSingleMap(T t, R r) {
        Map<T, R> map = new HashMap<>();
        map.put(t, r);
        return map;
    }

}

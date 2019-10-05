package no.capraconsulting.siren.internal.util;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ListUtil {

    public static <T, R> List<R> map(List<T> list, Function<T, R> map) {
        return list.stream().map(map).collect(Collectors.toList());
    }
}

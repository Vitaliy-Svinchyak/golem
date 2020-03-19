package com.e33.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Helper {

    public static <M> List<M> concatLists(List<M> list1, List<M> list2) {
        return Stream.concat(list1.stream(), list2.stream())
                .collect(Collectors.toList());
    }
}

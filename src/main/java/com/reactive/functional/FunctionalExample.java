package com.reactive.functional;

import java.util.List;
import java.util.stream.Collectors;

public class FunctionalExample {
    public static void main(String[] args) {
        var names = List.of("alex", "ben", "chole", "afam");
        var newNames = namesGreaterTanSize(names, 3);
        System.out.println("new names list: "+newNames);
    }

    private static List<String> namesGreaterTanSize(List<String> names, int i) {
        return names.parallelStream()
                .filter(n -> n.length() > 3)
                .map(String::toUpperCase)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}

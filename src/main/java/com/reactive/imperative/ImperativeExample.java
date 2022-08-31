package com.reactive.imperative;

import java.util.ArrayList;
import java.util.List;

public class ImperativeExample {
    public static void main(String[] args) {
        var names = List.of("alex", "ben", "chole", "afam", "afam");
        var newNames = namesGreaterTanSize(names, 3);
        System.out.println("new names list: "+newNames);
    }

    private static List<String> namesGreaterTanSize(List<String> names, int i) {
        var newList = new ArrayList<String>();

        for (String name : names) {
            if (name.length() > 3 && !newList.contains(name)) {
                newList.add(name.toUpperCase());
            }
        }
        return newList;
    }
}

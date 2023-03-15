package me.study.effectivejava.week12;

import java.util.*;

public class PlantMain {

    private static final List<Plant> list = new ArrayList<>();

    public static void setList() {
        Plant plant = new Plant("plantA", Plant.LifeCycle.ANNUAL);
        Plant plant2 = new Plant("plantB", Plant.LifeCycle.PERENNIAL);
        Plant plant3 = new Plant("plantC", Plant.LifeCycle.BIENNIAL);

        list.add(plant2);
        list.add(plant);
        list.add(plant3);
    }

    public static void useOrdinal() {

        setList();

        Set<Plant>[] plantsByLifeCycle = (Set<Plant>[]) new Set[Plant.LifeCycle.values().length];
        for (int i = 0; i < plantsByLifeCycle.length; i++) {
            plantsByLifeCycle[i] = new HashSet<>();
        }

        for (Plant p : list) {
            plantsByLifeCycle[p.lifeCycle.ordinal()].add(p);
        }

        for (int i = 0; i < plantsByLifeCycle.length; i++) {
            System.out.println(Plant.LifeCycle.values()[i]);
            System.out.println(plantsByLifeCycle[i]);
        }
    }

    public static void useEnumMap() {
        setList();

        Map<Plant.LifeCycle, Set<Plant>> plantsByLifeCycle =
                new EnumMap<>(Plant.LifeCycle.class);
        for (Plant.LifeCycle lc : Plant.LifeCycle.values()) {
            plantsByLifeCycle.put(lc, new HashSet<>());
        }

        for (Plant p : list) {
            plantsByLifeCycle.get(p.lifeCycle).add(p);
        }
        System.out.println(plantsByLifeCycle);

    }
}

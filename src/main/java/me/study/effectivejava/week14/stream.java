package me.study.effectivejava.week14;

public class stream {
    private int a = 123;
    public static void main(String[] args) {
        final int b = 123;
        int c = 123;
        int d = 123;

        final Runnable r = () -> {
            System.out.println(b);
            System.out.println(c);
            System.out.println(d);

//            d = 1234;
//
//            System.out.println(d);
        };
    }
}

package com.example.gitscorer.constants;

public class ScoreCalculatorConstants {
     private static final double HALF_LIFE_IN_DAYS = 365;
     private static final double MOST_STARS = 500_000;
     private static final double MOST_FORKS = 250_000;

     public static final double MAX_STARS_NORMALIZED = Math.log10(MOST_STARS + 1);
     public static final double MAX_FORKS_NORMALIZED = Math.log10(MOST_FORKS + 1);
     public static final double LAMBDA = -1 * (Math.log(2) / HALF_LIFE_IN_DAYS);
}

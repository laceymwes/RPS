package edu.cnm.deepdive.rps.model;

import java.util.Comparator;
import java.util.Random;

/**
 * <code>Breed</code> encapsulates the Rock, Paper, and Scissors "breeds" as an
 * <code>enum</code>. It also implements (in the <code>public static</code>
 * member {@link #REFERREE}) a {@link Comparator Comparator&lt;Breed&gt;} for
 * determining the victor in a conflict between 2 <code>Breed</code> instances.
 *
 * @author Nick Bennett &amp; Deep Dive Coding Java+Android+Salesforce Cohort 4
 */
public enum Breed {

  ROCK, PAPER, SCISSORS, LIZZARD, SPOCK;

  private static int[][] COMBAT_OUTCOMES = {
      { 0, -1,  1,  1,  1},
      { 1,  0, -1, -1,  1},
      {-1,  1,  0,  1, -1},
      {-1,  1, -1,  0,  1},
      { 1, -1,  1, -1,  0}
  };

  /** Determines the victor in a conflict between 2 {@link Breed} instances. */
  public static final Comparator<Breed> REFERREE =
      new Comparator<Breed>() {
        @Override
        public int compare(Breed breed1, Breed breed2) {
          return COMBAT_OUTCOMES[breed1.ordinal()][breed2.ordinal()];
        }
      };

  /** Returns a randomly selected instance of the {@link Breed}. */
  public static Breed random(Random rng) {
    Breed[] choices = Breed.values();
    return choices[rng.nextInt(choices.length)];
  }

}

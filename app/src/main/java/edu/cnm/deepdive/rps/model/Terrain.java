package edu.cnm.deepdive.rps.model;

import java.util.Random;

/**
 * <code>Terrain</code> implements a Rock-Paper-Scissors (cyclic dominance)
 * ecosystem on a square, toroidal matrix/lattice. Methods are provided to
 * initialize ({@link #reset()}); perform iterations of opponent selection &amp;
 * combat ({@link #iterate(int)}); and mix the lattice contents by swapping a
 * specified number of pairs of individuals ({@link #mix(int)}).
 *
 * @author Nick Bennett &amp; Deep Dive Coding Java+Android+Salesforce Cohort 4
 */
public class Terrain {

  /** Default size (width and height) of the terrain. */
  public static final int DEFAULT_SIZE = 50;

  private static final int[][] NEIGHBOR_OFFSETS = {
      {-1, 0},
      {0, -1}, {0, 1},
      {1, 0}
  };

  private Breed[][] cells;
  private Random rng;
  private long iterations;
  private int[] counts;
  private int totalCount;

  /**
   * Initialize a <code>Terrain</code> instance of size
   * <code>DEFAULT_SIZE</code>, with the specified source of randomness (used
   * for placing random {@link Breed} instances on the lattice, selecting
   * individuals for conflict in each iteration, and selecting pairs of
   * individuals for mixing via swapping). Invoking this constructor is
   * equivalent to invoking {@link #Terrain(int, Random)
   * Terrain(DEFAULT_SIZE, rng)}.
   *
   * @param rng   source of randomness.
   */
  public Terrain(Random rng) {
    this(DEFAULT_SIZE, rng);
  }

  /**
   * Initialize a <code>Terrain</code> instance of the specified size, with the
   * specified source of randomness (used for placing random {@link Breed}
   * instances on the lattice, and selecting individuals for conflict in each
   * iteration).
   *
   * @param size
   * @param rng   source of randomness.
   */
  public Terrain(int size, Random rng) {
    this.rng = rng;
    cells = new Breed[size][size];
    totalCount = size * size;
    reset();
  }

  /**
   * Re-initializes the lattice by populating each location with a
   * randomly-selected instance of {@link Breed} and resetting the iterations
   * counter to 0.
   */
  public void reset() {
    counts = new int[Breed.values().length];
    for (Breed[] row : cells) {
      for (int i = 0; i < row.length; i++) {
        Breed breed = Breed.random(this.rng);
        counts[breed.ordinal()]++;
        row[i] = breed;
      }
    }
    iterations = 0;
  }

  /**
   * Executes the specified number of conflict iterations. Each iteration
   * consists of the random selection of a lattice point, followed by the random
   * selection of a neighboring lattice point, and evaluation of the conflict
   * outcome between the {@link Breed} instance(s) at those 2 points. The last
   * step is performed by consulting the {@link Breed#REFERREE} {@link
   * java.util.Comparator} &ndash; after first checking to see that the 2
   * lattice points don't contain the same {@link Breed} (in which case, no
   * change results from conflict between them).
   *
   * @param steps   number of simple iterations to perform.
   */
  public void iterate(int steps) {
    for (int i = 0; i < steps; i++) {
      Location playerLocation = randomLocation();
      Breed player = cells[playerLocation.row][playerLocation.column];
      Location opponentLocation = randomNeighbor(playerLocation);
      Breed opponent = cells[opponentLocation.row][opponentLocation.column];
      if (player != opponent) {
        if (Breed.REFERREE.compare(player, opponent) > 0) {
          cells[opponentLocation.row][opponentLocation.column] = player;
          counts[opponent.ordinal()]--;
          counts[player.ordinal()]++;
        } else {
          cells[playerLocation.row][playerLocation.column] = opponent;
          counts[opponent.ordinal()]++;
          counts[player.ordinal()]--;
        }
      }
    }
    iterations += steps;
  }

  /**
   * Randomly selects and swaps the {@link Breed} instances at each of
   * <code>numPairs</code> lattice point pairs.
   *
   * @param numPairs  number of random lattice point pairs to select for swapping.
   */
  public void mix(int numPairs) {
    for (int i = 0; i < numPairs; i++) {
      Location location1 = randomLocation();
      Location location2 = randomLocation();
      while (location1.equals(location2)) {
        location2 = randomLocation();
      }
      Breed temp = cells[location1.row][location1.column];
      cells[location1.row][location1.column] = cells[location2.row][location2.column];
      cells[location2.row][location2.column] = temp;
    }
  }

  /**
   * Returns a reference to the {@link Breed Breed[][]} lattice (matrix).
   * <em>Warning:</em> For performance reasons, the returned reference is not
   * safe in any significant sense: modifing the array contents can give
   * unpredictable results, and the array contents may be modified while it is
   * being read. Thus, use of the returned array reference for any significant
   * operation (e.g. rendering a display of its contents), while {@link
   * #iterate(int)} or {@link #mix(int)} is being invoked by another thread,
   * should be synchronized; also, the contents should not be written (except by
   * <code>Terrain</code> class methods) under any circumstances.
   *
   * @return  reference to the 2-dimensional array containing the lattice
   *          contents.
   */
  public Breed[][] getCells() {
    return cells;
  }

  /**
   * Returns the simple iterations performed since this instance was
   * initialized, or since the last invocation of {@link #reset()}.
   *
   * @return  number of simple iterations performed.
   */
  public long getIterations() {
    return iterations;
  }

  /**
   * Returns a <code>boolean</code> flag indicating whether the system has
   * reached the absorption state, in which one {@link Breed} has taken over the
   * entire lattice.
   *
   * @return  flag indicating that the system is in the absorption state.
   */
  public boolean isAbsorbed() {
    for (int count : counts) {
      if (count >= totalCount) {
        return true;
      }
    }
    return false;
  }

  private Location randomLocation() {
    int row = rng.nextInt(cells.length);
    return new Location(row, rng.nextInt(cells[row].length));
  }

  private Location randomNeighbor(Location location) {
    int[] offsets = NEIGHBOR_OFFSETS[rng.nextInt(NEIGHBOR_OFFSETS.length)];
    int opponentRow = (location.row + offsets[0] + cells.length) % cells.length;
    int opponentCol =
        (location.column + offsets[1] + cells[opponentRow].length) % cells[opponentRow].length;
    return new Location(opponentRow, opponentCol);
  }

  private static class Location {

    public final int row;
    public final int column;

    private Location(int row, int column) {
      this.row = row;
      this.column = column;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof Location) {
        Location other = (Location) obj;
        return (this.row == other.row) && (this.column == other.column);
      }
      return false;
    }

  }

}

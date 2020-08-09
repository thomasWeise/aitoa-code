package aitoa.utils.graph;

import java.util.Arrays;
import java.util.Random;

import aitoa.utils.RandomUtils;

/**
 * This is an utility class that can keep track on a set of
 * integer values from {@code 0..L-1}. You can check in O(1) if a
 * given integer is in the set / available and you can add and
 * delete integers in O(1).
 * <p>
 * This class is suitable to keep track of which nodes in a graph
 * have been visited and which can be visited next. Whenever a
 * node was visited, it can be removed from the set. If the graph
 * is not fully-connected, then at the same time, you can add the
 * new nodes that become reachable.
 */
public final class IntSet {

  /** the set of values */
  private final int[] mValues;

  /** the value positions */
  private final int[] mPositions;

  /** the number of current values present in the set */
  private int mSize;

  /**
   * instantiate the integer set
   *
   * @param pLength
   *          the number of value
   */
  public IntSet(final int pLength) {
    super();
    this.mValues = new int[pLength];
    this.mPositions = new int[pLength];
    this.clear();
  }

  /** Add all the values to the set. */
  public void fill() {
    for (int i = this.mSize = this.mValues.length; (--i) >= 0;) {
      this.mValues[i] = i;
    }
  }

  /** Clear the value set. */
  public void clear() {
    this.mSize = 0;
    Arrays.fill(this.mPositions, -1);
  }

  /**
   * Fill in all the values into the node set and make sure that
   * they are in random order.
   *
   * @param random
   *          a random number generator
   */
  public void shuffle(final Random random) {
    RandomUtils.shuffle(random, this.mValues, 0, this.mSize);
    for (int i = this.mSize; (--i) >= 0;) {
      this.mPositions[this.mValues[i]] = i;
    }
  }

  /**
   * Delete a value from the set. If the value is not present in
   * the set, an {@link IllegalStateException} will be thrown.
   *
   * @param value
   *          the value
   */
  public void delete(final int value) {
    final int[] positions = this.mPositions;
    final int[] nodes = this.mValues;
    final int size = (--this.mSize);
    final int pos = positions[value];
    if (pos < 0) {
      throw new IllegalStateException(//
          "Node " + pos + //$NON-NLS-1$
              " not in the set.");//$NON-NLS-1$
    }

    if (size > pos) { // must switch
      final int replace = nodes[size];
      nodes[pos] = replace;
      positions[replace] = pos;
    }
    positions[value] = -1;
  }

  /**
   * Add a value to the set. If the value is already present in
   * the set, an {@link IllegalStateException} will be thrown.
   *
   * @param value
   *          the value to be added
   */
  public void add(final int value) {
    final int[] positions = this.mPositions;
    final int pos = positions[value];
    if (pos >= 0) {
      throw new IllegalStateException(//
          "Node " + pos + //$NON-NLS-1$
              " already in the set.");//$NON-NLS-1$
    }
    final int[] nodes = this.mValues;
    nodes[positions[value] = this.mSize++] = value;
  }

  /**
   * Delete a the last value and return it. This method depends
   * on the potentially random state of the set. If the set is
   * empty, an {@link ArrayIndexOutOfBoundsException} may be
   * thrown.
   *
   * @return the deleted value
   */
  public int deleteLast() {
    final int id = this.mValues[--this.mSize];
    this.mPositions[id] = -1;
    return id;
  }

  /**
   * Check if the given value is present in the set
   *
   * @param value
   *          the value
   * @return {@code true} if the node has not yet been deleted
   */
  public boolean has(final int value) {
    return (this.mPositions[value] >= 0);
  }

  /**
   * Choose a value randomly and delete and return it.
   *
   * @param r
   *          the randomizer
   * @return the randomly chosen (and deleted) value
   */
  public int deleteRandom(final Random r) {
    int size = this.mSize;
    final int pos = r.nextInt(size);
    this.mSize = (--size);

    final int[] positions = this.mPositions;
    final int[] nodes = this.mValues;
    final int id = nodes[pos];
    if (size > pos) {
      final int replace = nodes[size];
      nodes[pos] = replace;
      positions[replace] = pos;
    }
    positions[id] = -1;
    return id;
  }

  /**
   * get the number of values in the set
   *
   * @return the number of values in the set
   */
  public int size() {
    return this.mSize;
  }

  /**
   * Is the set empty?
   *
   * @return {@code true} if no more values can be extracted,
   *         {@code false} if there are still values that can be
   *         extracted
   */
  public boolean isEmpty() {
    return (this.mSize <= 0);
  }

  /**
   * Get the value at the given index. If {@code index>=size()},
   * the result is unspecified.
   *
   * @param i
   *          the index
   * @return the value at that index
   */
  public int get(final int i) {
    return this.mValues[i];
  }
}

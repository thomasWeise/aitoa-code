package aitoa.algorithms;

import java.util.Arrays;

import aitoa.structure.Individual;

/**
 * A utility class with frequently needed routines.
 */
public final class Utils {

  /**
   * Put the {@code max} best individuals with unique quality to
   * the front of an individual array {@code array}. First,
   * {@code array} is sorted. Thus, the best individuals are at
   * the front. However, some of them may have the same quality
   * and we only want one individual of each quality level at
   * front. We therefore process the sorted {@code array} from
   * the front and whenever we encounter a quality repeatedly, we
   * swap these repeated individuals towards higher indices.
   * Since there may be less than {@code max} individuals with
   * unique quality, we return the number of unique qualities.
   *
   * @param array
   *          the array to process
   * @param max
   *          the maximum number of best individuals to make
   *          unique
   * @return the number {@code u} of unique-quality individuals
   *         retained, will be {@code 1<=u<=max}
   */
// start qualityClearing
  public static int qualityBasedClearing(
      final Individual<?>[] array, final int max) {

    Arrays.sort(array, Individual.BY_QUALITY);// best -> first

    int unique = 0;
    double lastQuality = Double.NEGATIVE_INFINITY; // impossible

    for (int index = 0; index < array.length; index++) {
      final Individual<?> current = array[index];
      final double currentQuality = current.quality;
      if (currentQuality > lastQuality) { // unique so-far
        if (index > unique) { // need to move forward?
          final Individual<?> other = array[unique];
          array[unique] = current; // swap with first non-unique
          array[index] = other;
        }
        lastQuality = currentQuality; // update new quality
        if ((++unique) >= max) { // are we finished?
          return unique; // then quit: unique == max
        }
      }
    }

    return unique; // return number of unique: 1<=unique<=max
  }
// end qualityClearing

  /** forbidden */
  private Utils() {
    throw new UnsupportedOperationException();
  }
}

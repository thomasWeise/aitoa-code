package aitoa.bookExamples;

/**
 * this is an example for the implementation of a randomization
 * test. it is used to verify the results of the example in the
 * book
 */
public class RandomizationTestExample {

  /**
   * the main routine
   *
   * @param args
   *          the command line arguments are ignored
   */
  public static void main(final String[] args) {
// start relevant
// how often did we find a mean <= 4?
    int meanLowerOrEqualTo4 = 0;
// total number of tested combinations
    int totalCombinations = 0;
// enumerate all sets of four different numbers from 1..10
    for (int i = 10; i > 0; i--) { // as O = numbers from 1 to 10
      for (int j = (i - 1); j > 0; j--) { // we can iterate over
        for (int k = (j - 1); k > 0; k--) { // the sets of size 4
          for (int l = (k - 1); l > 0; l--) { // with 4 loops
            if (((i + j + k + l) / 4.0) <= 4) {
              meanLowerOrEqualTo4++;// yes, found an extreme case
            } // count the extreme case
            totalCombinations++; // add up combos, to verify
          }
        }
      }
    }
// print the result: 27 210
    System.out.println(//
        meanLowerOrEqualTo4 + " " + totalCombinations); //$NON-NLS-1$
// end relevant
  }
}

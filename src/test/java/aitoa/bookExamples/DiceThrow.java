package aitoa.bookExamples;

import java.util.Random;

/** An example for throwing dice */
public class DiceThrow {

  /**
   * print a number
   *
   * @param v
   *          the number
   * @param step
   *          the step
   */
  private static final void __print(final long v,
      final long step) {
    System.out.printf("%.4f", //$NON-NLS-1$
        Double.valueOf(((double) v) / step));
  }

  /**
   * perform a dice throw experiment
   *
   * @param args
   *          the command line arguments
   */
  public static final void main(final String[] args) {
    final Random random = new Random(1212L);
    final int[] count = new int[6];
    final int max_steps = 12;
    final int max_total = 1_000_000_000;

    System.out.println(
        "|\\#&nbsp;throws|number|$f_1$|$f_2$|$f_3$|$f_4$|$f_5$|$f_6$|"); //$NON-NLS-1$
    System.out.println("|--:|:-:|--:|--:|--:|--:|--:|--:|"); //$NON-NLS-1$
    int step = 1;
    for (; step <= max_steps; step++) {
      System.out.print('|');
      Tools.printLongNumber(step);
      final int n = random.nextInt(6) + 1;
      System.out.print('|');
      System.out.print(n);
      ++count[n - 1];
      for (final int element : count) {
        System.out.print('|');
        DiceThrow.__print(element, step);
      }
      System.out.println('|');
    }

    int next = ((int) (Math.round(
        Math.pow(10, 1L + Math.floor(Math.log10(step))))));
    for (; step <= max_total; step++) {
      ++count[random.nextInt(6)];
      if (step >= next) {
        System.out.print('|');
        Tools.printLongNumber(step);
        System.out.print('|');
        System.out.print("&hellip;"); //$NON-NLS-1$
        for (final int element : count) {
          System.out.print('|');
          DiceThrow.__print(element, step);
        }
        System.out.println('|');
        next *= 10;
      }
    }
  }
}

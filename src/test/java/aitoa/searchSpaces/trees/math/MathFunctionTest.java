package aitoa.searchSpaces.trees.math;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import aitoa.searchSpaces.trees.TestNode;

/** test a math function */
@Ignore
public abstract class MathFunctionTest extends TestNode {
  /**
   * test a function
   *
   * @param node
   *          the node
   */
  protected MathFunctionTest(final MathFunction<double[]> node) {
    super(node);
  }

  /** {@inheritDoc} */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  protected MathFunction<double[]> getInstance() {
    return ((MathFunction) (super.getInstance()));
  }

  /**
   * create an instance for a given set of {@code double}
   * arguments
   *
   * @param args
   *          the arguments
   * @return the return value
   */
  protected abstract MathFunction<double[]>
      getInstance(double... args);

  /**
   * create an instance for a given set of {@code long} arguments
   *
   * @param args
   *          the arguments
   * @return the return value
   */
  protected abstract MathFunction<double[]>
      getInstance(long... args);

  /**
   * create an instance for a given set of {@code int} arguments
   *
   * @param args
   *          the arguments
   * @return the return value
   */
  protected MathFunction<double[]>
      getInstance(final int... args) {
    final long[] l = new long[args.length];
    for (int i = l.length; (--i) >= 0;) {
      l[i] = args[i];
    }
    return this.getInstance(l);
  }

  /**
   * get an array of double test cases, where each case includes
   * the arguments and the expected result
   *
   * @return the test cases
   */
  protected abstract double[][] getDoubleTestCases();

  /**
   * get an array of long test cases, where each case includes
   * the arguments and the expected result
   *
   * @return the test cases
   */
  protected abstract long[][] getLongTestCases();

  /**
   * get an array of int test cases, where each case includes the
   * arguments and the expected result
   *
   * @return the test cases
   */
  protected abstract int[][] getIntTestCases();

  /** test the double test cases */
  @Test(timeout = 3600000)
  public void testDoubleTestCases() {
    for (final double[] caze : this.getDoubleTestCases()) {
      Assert.assertEquals(caze[caze.length - 1],
          this.getInstance(caze).applyAsDouble(null),
          Double.MIN_VALUE);
    }
  }

  /** test the long test cases */
  @Test(timeout = 3600000)
  public void testLongTestCases() {
    for (final long[] caze : this.getLongTestCases()) {
      Assert.assertEquals(caze[caze.length - 1],
          this.getInstance(caze).applyAsLong(null));
    }
  }

  /** test the int test cases */
  @Test(timeout = 3600000)
  public void testIntTestCases() {
    for (final int[] caze : this.getIntTestCases()) {
      Assert.assertEquals(caze[caze.length - 1],
          this.getInstance(caze).applyAsInt(null));
    }
  }
}

package aitoa.examples.bitstrings;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import aitoa.TestTools;
import aitoa.structure.IObjectiveFunction;
import aitoa.structure.IObjectiveFunctionTest;

/** A Test for the linear harmonic function */
public class TestLinearHarmonicObjectiveFunction
    extends IObjectiveFunctionTest<boolean[]> {

  /** the linear harmonic problem */
  public static final LinearHarmonicObjectiveFunction F =
      new LinearHarmonicObjectiveFunction(20);

  /** {@inheritDoc} */
  @Override
  protected IObjectiveFunction<boolean[]> getInstance() {
    return TestLinearHarmonicObjectiveFunction.F;
  }

  /** {@inheritDoc} */
  @Override
  protected boolean[] createValid() {
    final boolean[] x =
        new boolean[TestLinearHarmonicObjectiveFunction.F.n];
    final Random r = ThreadLocalRandom.current();
    for (int i = x.length; (--i) >= 0;) {
      x[i] = r.nextBoolean();
    }
    return x;
  }

  /** test the correctness */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testCorrectness() {
    final boolean[] x =
        new boolean[TestLinearHarmonicObjectiveFunction.F.n];

    for (int i = 0; i <= x.length; i++) {
      int exp = 0;
      for (int j = 0; j < i; j++) {
        exp += (j + 1);
      }
      exp = ((x.length * (x.length + 1)) >>> 1) - exp;

      Assert.assertEquals(
          TestLinearHarmonicObjectiveFunction.F.evaluate(x), exp,
          0);
      TestTools.assertGreaterOrEqual(exp,
          TestLinearHarmonicObjectiveFunction.F.lowerBound());
      TestTools.assertLessOrEqual(exp,
          TestLinearHarmonicObjectiveFunction.F.upperBound());
      if (i >= x.length) {
        break;
      }
      x[i] = true;
    }
  }
}

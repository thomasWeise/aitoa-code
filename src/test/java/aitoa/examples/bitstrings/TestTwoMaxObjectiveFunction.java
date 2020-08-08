package aitoa.examples.bitstrings;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import aitoa.TestTools;
import aitoa.structure.IObjectiveFunction;
import aitoa.structure.IObjectiveFunctionTest;

/** A Test for the TwoMax Objective Function */
public class TestTwoMaxObjectiveFunction
    extends IObjectiveFunctionTest<boolean[]> {

  /** the twomax */
  public static final TwoMaxObjectiveFunction F =
      new TwoMaxObjectiveFunction(20);

  /** {@inheritDoc} */
  @Override
  protected IObjectiveFunction<boolean[]> getInstance() {
    return TestTwoMaxObjectiveFunction.F;
  }

  /** {@inheritDoc} */
  @Override
  protected boolean[] createValid() {
    final boolean[] x =
        new boolean[TestTwoMaxObjectiveFunction.F.n];
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
        new boolean[TestTwoMaxObjectiveFunction.F.n];

    for (int i = 0; i <= x.length; i++) {
      final int exp = (i == x.length) ? 0
          : ((1 + x.length) - Math.max(i, x.length - i));
      Assert.assertEquals(
          TestTwoMaxObjectiveFunction.F.evaluate(x), exp, 0);
      TestTools.assertGreaterOrEqual(exp,
          TestTwoMaxObjectiveFunction.F.lowerBound());
      TestTools.assertLessOrEqual(exp,
          TestTwoMaxObjectiveFunction.F.upperBound());

      if (i >= x.length) {
        break;
      }
      x[i] = true;
    }
  }
}

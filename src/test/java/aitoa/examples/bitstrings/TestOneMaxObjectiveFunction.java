package aitoa.examples.bitstrings;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import aitoa.TestTools;
import aitoa.structure.IObjectiveFunction;
import aitoa.structure.IObjectiveFunctionTest;

/** A Test for the OneMax Objective Function */
public class TestOneMaxObjectiveFunction
    extends IObjectiveFunctionTest<boolean[]> {

  /** the onemax */
  public static final OneMaxObjectiveFunction F =
      new OneMaxObjectiveFunction(20);

  /** {@inheritDoc} */
  @Override
  protected IObjectiveFunction<boolean[]> getInstance() {
    return TestOneMaxObjectiveFunction.F;
  }

  /** {@inheritDoc} */
  @Override
  protected boolean[] createValid() {
    final boolean[] x =
        new boolean[TestOneMaxObjectiveFunction.F.n];
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
        new boolean[TestOneMaxObjectiveFunction.F.n];

    for (int i = 0; i <= x.length; i++) {
      final int exp = x.length - i;
      Assert.assertEquals(
          TestOneMaxObjectiveFunction.F.evaluate(x), exp, 0);
      TestTools.assertGreaterOrEqual(exp,
          TestOneMaxObjectiveFunction.F.lowerBound());
      TestTools.assertLessOrEqual(exp,
          TestOneMaxObjectiveFunction.F.upperBound());
      if (i >= x.length) {
        break;
      }
      x[i] = true;
    }
  }
}

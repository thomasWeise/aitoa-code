package aitoa.examples.bitstrings;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import aitoa.TestTools;
import aitoa.structure.IObjectiveFunction;
import aitoa.structure.IObjectiveFunctionTest;

/** A Test for the Jump Objective Function */
public class TestJump_10_3_ObjectiveFunction
    extends IObjectiveFunctionTest<boolean[]> {

  /** the Jump */
  public static final JumpObjectiveFunction F =
      new JumpObjectiveFunction(10, 3);

  /** {@inheritDoc} */
  @Override
  protected IObjectiveFunction<boolean[]> getInstance() {
    return TestJump_10_3_ObjectiveFunction.F;
  }

  /** {@inheritDoc} */
  @Override
  protected boolean[] createValid() {
    final boolean[] x =
        new boolean[TestJump_10_3_ObjectiveFunction.F.n];
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
        new boolean[TestJump_10_3_ObjectiveFunction.F.n];

    for (int i = 0; i <= x.length; i++) {
      final double res =
          TestJump_10_3_ObjectiveFunction.F.evaluate(x);

      int exp = 0;
      if ((i <= (F.n - F.k)) || (i == F.n)) {
        exp = F.k + i;
      } else {
        exp = F.n - i;
      }

      exp = F.n + F.k - exp;
      Assert.assertEquals(res, exp, 0);
      TestTools.assertGreaterOrEqual(exp, F.lowerBound());
      TestTools.assertLessOrEqual(exp, F.upperBound());
      if (i >= x.length) {
        break;
      }
      x[i] = true;
    }
  }
}
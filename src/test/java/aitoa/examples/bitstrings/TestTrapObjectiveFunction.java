package aitoa.examples.bitstrings;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import aitoa.TestTools;
import aitoa.structure.IObjectiveFunction;
import aitoa.structure.IObjectiveFunctionTest;

/** A Test for the Trap Objective Function */
public class TestTrapObjectiveFunction
    extends IObjectiveFunctionTest<boolean[]> {

  /** the trap */
  public static final TrapObjectiveFunction F =
      new TrapObjectiveFunction(20);

  /** {@inheritDoc} */
  @Override
  protected IObjectiveFunction<boolean[]> getInstance() {
    return TestTrapObjectiveFunction.F;
  }

  /** {@inheritDoc} */
  @Override
  protected boolean[] createValid() {
    final boolean[] x =
        new boolean[TestTrapObjectiveFunction.F.n];
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
        new boolean[TestTrapObjectiveFunction.F.n];

    for (int i = 0; i <= x.length; i++) {
      final int exp = (i == 0) ? 0 : ((x.length + 1) - i);

      Assert.assertEquals(
          TestTrapObjectiveFunction.F.evaluate(x), exp, 0);
      TestTools.assertGreaterOrEqual(exp,
          TestTrapObjectiveFunction.F.lowerBound());
      TestTools.assertLessOrEqual(exp,
          TestTrapObjectiveFunction.F.upperBound());
      if (i >= x.length) {
        break;
      }
      x[i] = true;
    }
  }
}

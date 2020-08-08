package aitoa.examples.bitstrings;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import aitoa.TestTools;
import aitoa.structure.IObjectiveFunction;
import aitoa.structure.IObjectiveFunctionTest;

/** A Test for the Plateau Objective Function */
public class TestPlateau10x3ObjectiveFunction
    extends IObjectiveFunctionTest<boolean[]> {

  /** the Plateau */
  public static final PlateauObjectiveFunction F =
      new PlateauObjectiveFunction(10, 3);

  /** {@inheritDoc} */
  @Override
  protected IObjectiveFunction<boolean[]> getInstance() {
    return TestPlateau10x3ObjectiveFunction.F;
  }

  /** {@inheritDoc} */
  @Override
  protected boolean[] createValid() {
    final boolean[] x =
        new boolean[TestPlateau10x3ObjectiveFunction.F.n];
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
        new boolean[TestPlateau10x3ObjectiveFunction.F.n];

    for (int i = 0; i <= x.length; i++) {
      final double res =
          TestPlateau10x3ObjectiveFunction.F.evaluate(x);

      int exp = 0;
      if ((i <= (TestPlateau10x3ObjectiveFunction.F.n
          - TestPlateau10x3ObjectiveFunction.F.k))
          || (i == TestPlateau10x3ObjectiveFunction.F.n)) {
        exp = i;
      } else {
        exp = TestPlateau10x3ObjectiveFunction.F.n
            - TestPlateau10x3ObjectiveFunction.F.k;
      }
      exp = x.length - exp;
      Assert.assertEquals(res, exp, 0);
      TestTools.assertGreaterOrEqual(exp,
          TestPlateau10x3ObjectiveFunction.F.lowerBound());
      TestTools.assertLessOrEqual(exp,
          TestPlateau10x3ObjectiveFunction.F.upperBound());
      if (i >= x.length) {
        break;
      }
      x[i] = true;
    }
  }
}

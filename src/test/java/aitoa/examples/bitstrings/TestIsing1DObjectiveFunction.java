package aitoa.examples.bitstrings;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import aitoa.structure.IObjectiveFunction;
import aitoa.structure.IObjectiveFunctionTest;

/** A Test for the Ising model on a 1D torus */
public class TestIsing1DObjectiveFunction
    extends IObjectiveFunctionTest<boolean[]> {

  /** the Ising1D */
  public static final Ising1DObjectiveFunction F =
      new Ising1DObjectiveFunction(25);

  /** {@inheritDoc} */
  @Override
  protected IObjectiveFunction<boolean[]> getInstance() {
    return TestIsing1DObjectiveFunction.F;
  }

  /** {@inheritDoc} */
  @Override
  protected boolean[] createValid() {
    final boolean[] x =
        new boolean[TestIsing1DObjectiveFunction.F.n];
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
        new boolean[TestIsing1DObjectiveFunction.F.n];

    Assert.assertEquals(
        TestIsing1DObjectiveFunction.F.evaluate(x), 0, 0);
    Arrays.fill(x, true);
    Assert.assertEquals(
        TestIsing1DObjectiveFunction.F.evaluate(x), 0, 0);

    x[5] ^= true;
    Assert.assertEquals(2d,
        TestIsing1DObjectiveFunction.F.evaluate(x), 0);
    x[6] ^= true;
    Assert.assertEquals(2d,
        TestIsing1DObjectiveFunction.F.evaluate(x), 0);
    x[7] ^= true;
    Assert.assertEquals(2d,
        TestIsing1DObjectiveFunction.F.evaluate(x), 0);
    x[6] ^= true;
    Assert.assertEquals(4d,
        TestIsing1DObjectiveFunction.F.evaluate(x), 0);

    x[0] ^= true;
    Assert.assertEquals(6d,
        TestIsing1DObjectiveFunction.F.evaluate(x), 0);
    x[24] ^= true;
    Assert.assertEquals(6d,
        TestIsing1DObjectiveFunction.F.evaluate(x), 0);
    x[23] ^= true;
    Assert.assertEquals(6d,
        TestIsing1DObjectiveFunction.F.evaluate(x), 0);
    x[24] ^= true;
    Assert.assertEquals(8d,
        TestIsing1DObjectiveFunction.F.evaluate(x), 0);
  }
}

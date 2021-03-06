package aitoa.examples.bitstrings;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import aitoa.structure.IObjectiveFunction;
import aitoa.structure.IObjectiveFunctionTest;

/** A Test Ising model on a 2D torus */
public class TestIsing2DObjectiveFunction
    extends IObjectiveFunctionTest<boolean[]> {

  /** the Ising2D */
  public static final Ising2DObjectiveFunction F =
      new Ising2DObjectiveFunction(25);

  /** {@inheritDoc} */
  @Override
  protected IObjectiveFunction<boolean[]> getInstance() {
    return TestIsing2DObjectiveFunction.F;
  }

  /** {@inheritDoc} */
  @Override
  protected boolean[] createValid() {
    final boolean[] x =
        new boolean[TestIsing2DObjectiveFunction.F.n];
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
        new boolean[TestIsing2DObjectiveFunction.F.n];

    Assert.assertEquals(
        TestIsing2DObjectiveFunction.F.evaluate(x), 0, 0);
    Arrays.fill(x, true);
    Assert.assertEquals(
        TestIsing2DObjectiveFunction.F.evaluate(x), 0, 0);

    x[5] ^= true;
    Assert.assertEquals(4,
        TestIsing2DObjectiveFunction.F.evaluate(x), 0);
    x[6] ^= true;
    Assert.assertEquals(6,
        TestIsing2DObjectiveFunction.F.evaluate(x), 0);
    x[7] ^= true;
    Assert.assertEquals(8,
        TestIsing2DObjectiveFunction.F.evaluate(x), 0);
    x[6] ^= true;
    Assert.assertEquals(8,
        TestIsing2DObjectiveFunction.F.evaluate(x), 0);
    x[10] ^= true;
    Assert.assertEquals(10,
        TestIsing2DObjectiveFunction.F.evaluate(x), 0);
    x[12] ^= true;
    Assert.assertEquals(12,
        TestIsing2DObjectiveFunction.F.evaluate(x), 0);
    x[11] ^= true;
    Assert.assertEquals(12,
        TestIsing2DObjectiveFunction.F.evaluate(x), 0);
    x[6] ^= true;
    Assert.assertEquals(10,
        TestIsing2DObjectiveFunction.F.evaluate(x), 0);
  }
}

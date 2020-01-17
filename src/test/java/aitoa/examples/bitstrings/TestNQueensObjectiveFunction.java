package aitoa.examples.bitstrings;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import aitoa.structure.IObjectiveFunction;
import aitoa.structure.IObjectiveFunctionTest;

/** A Test for the N-Queens problem */
public class TestNQueensObjectiveFunction
    extends IObjectiveFunctionTest<boolean[]> {

  /** the NQueens */
  public static final NQueensObjectiveFunction F =
      new NQueensObjectiveFunction(4 * 4);

  /** {@inheritDoc} */
  @Override
  protected IObjectiveFunction<boolean[]> getInstance() {
    return TestNQueensObjectiveFunction.F;
  }

  /** {@inheritDoc} */
  @Override
  protected boolean[] createValid() {
    final boolean[] x =
        new boolean[TestNQueensObjectiveFunction.F.n];
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
    final boolean[] opt1 = { //
        false, true, false, false, //
        false, false, false, true, //
        true, false, false, false, //
        false, false, true, false,//
    };
    final boolean[] opt2 = { //
        false, false, true, false, //
        true, false, false, false, //
        false, false, false, true, false, true, false, false };

    Assert.assertEquals(0,
        TestNQueensObjectiveFunction.F.evaluate(opt1), 0);
    Assert.assertEquals(0,
        TestNQueensObjectiveFunction.F.evaluate(opt2), 0);
  }
}
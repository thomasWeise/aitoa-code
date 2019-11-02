package aitoa.examples.bitstrings;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import aitoa.structure.IObjectiveFunction;
import aitoa.structure.IObjectiveFunctionTest;

/** A Test for the JSSP MakeSpan Objective Function */
public class TestLeadingOnesObjectiveFunction
    extends IObjectiveFunctionTest<boolean[]> {

  /** the LeadingOnes */
  public static final LeadingOnesObjectiveFunction F =
      new LeadingOnesObjectiveFunction(20);

  /** {@inheritDoc} */
  @Override
  protected IObjectiveFunction<boolean[]> getInstance() {
    return TestLeadingOnesObjectiveFunction.F;
  }

  /** {@inheritDoc} */
  @Override
  protected boolean[] createValid() {
    final boolean[] x =
        new boolean[TestLeadingOnesObjectiveFunction.F.n];
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
        new boolean[TestLeadingOnesObjectiveFunction.F.n];

    for (int i = 0; i < x.length; i++) {
      Assert.assertEquals(
          TestLeadingOnesObjectiveFunction.F.evaluate(x),
          x.length - i, 0);
      x[i] = true;
    }
  }
}
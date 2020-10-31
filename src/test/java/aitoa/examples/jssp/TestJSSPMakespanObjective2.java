package aitoa.examples.jssp;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import aitoa.structure.IObjectiveFunction;
import aitoa.structure.IObjectiveFunctionTest;

/** A Test for the JSSP MakeSpan Objective Function */
public class TestJSSPMakespanObjective2
    extends IObjectiveFunctionTest<int[]> {

  /** the example instance */
  public static final JSSPInstance PROBLEM =
      new JSSPInstance("abz7"); //$NON-NLS-1$

  /** the example instance */
  public static final JSSPMakespanObjectiveFunction2 F =
      new JSSPMakespanObjectiveFunction2(
          TestJSSPMakespanObjective2.PROBLEM);

  /** {@inheritDoc} */
  @Override
  protected IObjectiveFunction<int[]> getInstance() {
    return TestJSSPMakespanObjective2.F;
  }

  /** the example instance */
  public static final JSSPMakespanObjectiveFunction F2 =
      new JSSPMakespanObjectiveFunction(
          TestJSSPMakespanObjective2.PROBLEM);

  /** the internal mapping */
  private static final JSSPRepresentationMapping MAPPING =
      new JSSPRepresentationMapping(
          TestJSSPMakespanObjective2.PROBLEM);

  /** {@inheritDoc} */
  @Override
  protected int[] createValid() {
    return JSSPTestUtils
        .createValidX(TestJSSPMakespanObjective2.PROBLEM);
  }

  /** test the makespan */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void testObjectiveCompatibility() {
    final ThreadLocalRandom r = ThreadLocalRandom.current();
    final JSSPCandidateSolution y = new JSSPCandidateSolution(
        TestJSSPMakespanObjective2.PROBLEM.m,
        TestJSSPMakespanObjective2.PROBLEM.n);
    for (int i = 100; (--i) >= 0;) {
      final int[] x = JSSPTestUtils
          .createValidX(TestJSSPMakespanObjective2.PROBLEM);
      TestJSSPMakespanObjective2.MAPPING.map(r, x, y);
      Assert.assertEquals(
          TestJSSPMakespanObjective2.F2.evaluate(y),
          TestJSSPMakespanObjective2.F.evaluate(x), 0d);
    }
  }
}

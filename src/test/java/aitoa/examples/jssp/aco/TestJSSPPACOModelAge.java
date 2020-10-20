package aitoa.examples.jssp.aco;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import aitoa.examples.jssp.JSSPInstance;
import aitoa.examples.jssp.JSSPMakespanObjectiveFunction;
import aitoa.examples.jssp.JSSPRepresentationMapping;
import aitoa.examples.jssp.JSSPTestUtils;
import aitoa.structure.IModel;
import aitoa.structure.IModelTest;
import aitoa.structure.ISpace;

/** test the univariate model for the JSSP */
public class TestJSSPPACOModelAge
    extends IModelTest<JSSPACORecord> {

  /** the space we use */
  private static final JSSPInstance PROBLEM =
      new JSSPInstance("swv18"); //$NON-NLS-1$

  /** the space we use */
  private static final JSSPACOSpace SPACE =
      new JSSPACOSpace(TestJSSPPACOModelAge.PROBLEM);

  /** the operator we use */
  private static final IModel<JSSPACORecord> OP =
      new JSSPPACOModelAge(TestJSSPPACOModelAge.PROBLEM, 3, 0.9,
          5d, 1d);
  /** the internal representation mapping */
  private static final JSSPRepresentationMapping G =
      new JSSPRepresentationMapping(
          TestJSSPPACOModelAge.PROBLEM);

  /** the raw objective */
  private static final JSSPMakespanObjectiveFunction F =
      new JSSPMakespanObjectiveFunction(
          TestJSSPPACOModelAge.PROBLEM);

  /** {@inheritDoc} */
  @Override
  protected ISpace<JSSPACORecord> getSpace() {
    return TestJSSPPACOModelAge.SPACE;
  }

  /** {@inheritDoc} */
  @Override
  protected IModel<JSSPACORecord>
      getModel(final ISpace<JSSPACORecord> space) {
    return TestJSSPPACOModelAge.OP;
  }

  /**
   * fill with random data
   *
   * @param dest
   *          the destination
   */
  private static final void
      fillWithRandomData(final JSSPACORecord dest) {

    JSSPTestUtils.randomX(dest.permutation,
        TestJSSPPACOModelAge.PROBLEM);
    TestJSSPPACOModelAge.G.map(ThreadLocalRandom.current(),
        dest.permutation, dest.solution);

    final int[] count = new int[TestJSSPPACOModelAge.PROBLEM.n];
    for (int i = 0; i < dest.permutation.length; i++) {
      final int p = dest.permutation[i];
      dest.permutation[i] =
          (p * TestJSSPPACOModelAge.PROBLEM.m) + (count[p]++);
    }

    dest.makespan =
        (int) (TestJSSPPACOModelAge.F.evaluate(dest.solution));
  }

  /** {@inheritDoc} */
  @Override
  protected JSSPACORecord createValid() {
    final JSSPACORecord dest =
        new JSSPACORecord(TestJSSPPACOModelAge.PROBLEM.m,
            TestJSSPPACOModelAge.PROBLEM.n);
    TestJSSPPACOModelAge.fillWithRandomData(dest);
    return dest;
  }

  /** {@inheritDoc} */
  @Override
  protected boolean equals(final JSSPACORecord a,
      final JSSPACORecord b) {
    return (a.makespan == b.makespan)
        && Arrays.equals(a.permutation, b.permutation)
        && Arrays.deepEquals(a.solution.schedule,
            b.solution.schedule);
  }
}

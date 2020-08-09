package aitoa.algorithms.bitstrings;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import aitoa.TestTools;
import aitoa.algorithms.TestMetaheuristic;
import aitoa.examples.bitstrings.BitStringObjectiveFunction;
import aitoa.examples.bitstrings.LeadingOnesObjectiveFunction;
import aitoa.examples.bitstrings.OneMaxObjectiveFunction;
import aitoa.examples.bitstrings.TrapObjectiveFunction;
import aitoa.examples.bitstrings.TwoMaxObjectiveFunction;
import aitoa.searchSpaces.bitstrings.BitStringBinaryOperatorUniform;
import aitoa.searchSpaces.bitstrings.BitStringNullaryOperator;
import aitoa.searchSpaces.bitstrings.BitStringUnaryOperator1Flip;
import aitoa.structure.BlackBoxProcessBuilder;
import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;

/** Test a bit-string based metaheuristic */
@Ignore
public abstract class TestBitStringMetaheuristic
    extends TestMetaheuristic<boolean[], boolean[]> {

  /** the default instance size */
  private static final int INST_SIZE = 32;

  /**
   * Create the metaheuristic
   *
   * @param pN
   *          the number of bits
   * @param pUB
   *          the upper bound for objective values
   * @return the instance
   */
  protected abstract IMetaheuristic<boolean[], boolean[]>
      createMetaheuristic(final int pN, final int pUB);

  /** {@inheritDoc} */
  @Override
  protected final IMetaheuristic<boolean[], boolean[]>
      getInstance() {
    return this.createMetaheuristic(
        TestBitStringMetaheuristic.INST_SIZE,
        TestBitStringMetaheuristic.INST_SIZE
            * TestBitStringMetaheuristic.INST_SIZE);
  }

  /**
   * Test the metaheuristic on a specific objective function
   *
   * @param f
   *          the bit string objective function
   * @throws IOException
   *           if i/o fails
   */
  protected final void testOnObjective(
      final BitStringObjectiveFunction f) throws IOException {

    final BlackBoxProcessBuilder<boolean[], boolean[]> builder =
        new BlackBoxProcessBuilder<>();

    final double UB = f.upperBound();
    final int UBi = ((int) UB);
    builder.setMaxFEs(f.n * f.n * 4);
    builder.setSearchSpace(f.createSpace());
    builder.setNullarySearchOperator(
        new BitStringNullaryOperator());
    builder.setUnarySearchOperator(
        new BitStringUnaryOperator1Flip(f.n));
    builder.setBinarySearchOperator(
        new BitStringBinaryOperatorUniform());
    builder.setObjectiveFunction(f);
    try (final IBlackBoxProcess<boolean[], boolean[]> process =
        builder.get()) {
      this.createMetaheuristic(f.n, UBi).solve(process);
      final double bestF = process.getBestF();
      Assert.assertTrue(Double.isFinite(bestF));
      TestTools.assertGreaterOrEqual(bestF, 0d);
      TestTools.assertLessOrEqual(bestF, Math.max(1, 0.75 * UB));
    }
  }

  /**
   * test on the one max objective function
   *
   * @throws IOException
   *           if i/o fails
   */
  @Test(timeout = 3600000)
  public final void testOneMax() throws IOException {
    for (int i = 100; (--i) >= 10;) {
      this.testOnObjective(new OneMaxObjectiveFunction(i));
    }
  }

  /**
   * test on the leading ones problem
   *
   * @throws IOException
   *           if i/o fails
   */
  @Test(timeout = 3600000)
  public final void testLeadingOnes() throws IOException {
    for (int i = 30; (--i) >= 10;) {
      this.testOnObjective(new LeadingOnesObjectiveFunction(i));
    }
  }

  /**
   * test on the twomax problem
   *
   * @throws IOException
   *           if i/o fails
   */
  @Test(timeout = 3600000)
  public final void testTwoMax() throws IOException {
    for (int i = 30; (--i) >= 10;) {
      this.testOnObjective(new TwoMaxObjectiveFunction(i));
    }
  }

  /**
   * test on the trap problem
   *
   * @throws IOException
   *           if i/o fails
   */
  @Test(timeout = 3600000)
  public final void testTrap() throws IOException {
    for (int i = 70; (--i) >= 10;) {
      this.testOnObjective(new TrapObjectiveFunction(i));
    }
  }
}

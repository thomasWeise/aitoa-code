package aitoa.examples.bitstrings;

import java.io.IOException;

import org.junit.Test;

import aitoa.TestTools;
import aitoa.algorithms.bitstrings.Greedy2p1GAmod;
import aitoa.searchSpaces.bitstrings.BitStringNullaryOperator;
import aitoa.structure.BlackBoxProcessBuilder;
import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.ISpace;

/**
 * A Test for the strange long runs of Greedy(2+1)GAmod on OneMax
 * Objective Function
 */
public class TestOneMaxObjectiveFunctionGreedyGAmodLongRuns {

  /** test the run */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testOneMax19seed0x43a2d6a4159ead3d() {
    final OneMaxObjectiveFunction f =
        new OneMaxObjectiveFunction(19);
    final ISpace<boolean[]> X = f.createSpace();
    final INullarySearchOperator<boolean[]> op0 =
        new BitStringNullaryOperator();
    final Greedy2p1GAmod<boolean[]> algo =
        new Greedy2p1GAmod<>();

    final BlackBoxProcessBuilder<boolean[], boolean[]> builder =
        new BlackBoxProcessBuilder<>();
    builder.setSearchSpace(X);
    builder.setNullarySearchOperator(op0);
    builder.setGoalF(0d);
    builder.setObjectiveFunction(f);
    builder.setRandSeed(0x43a2d6a4159ead3dL);

    try (final IBlackBoxProcess<boolean[], boolean[]> process =
        builder.get()) {
      algo.solve(process);
      TestTools.assertLessOrEqual(process.getBestF(), 0d);
      TestTools.assertLessOrEqual(process.getConsumedFEs(), 30);
    } catch (final IOException ioe) {
      throw new AssertionError(ioe);
    }
  }

  /** test the run */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testOneMax21seed0x98fb1396311d442c() {
    final OneMaxObjectiveFunction f =
        new OneMaxObjectiveFunction(21);
    final ISpace<boolean[]> X = f.createSpace();
    final INullarySearchOperator<boolean[]> op0 =
        new BitStringNullaryOperator();
    final Greedy2p1GAmod<boolean[]> algo =
        new Greedy2p1GAmod<>();

    final BlackBoxProcessBuilder<boolean[], boolean[]> builder =
        new BlackBoxProcessBuilder<>();
    builder.setSearchSpace(X);
    builder.setNullarySearchOperator(op0);
    builder.setGoalF(0d);
    builder.setObjectiveFunction(f);
    builder.setRandSeed(0x98fb1396311d442cL);

    try (final IBlackBoxProcess<boolean[], boolean[]> process =
        builder.get()) {
      algo.solve(process);
      TestTools.assertLessOrEqual(process.getBestF(), 0d);
      TestTools.assertLessOrEqual(process.getConsumedFEs(), 50L);
    } catch (final IOException ioe) {
      throw new AssertionError(ioe);
    }
  }
}

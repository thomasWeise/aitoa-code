package aitoa.algorithms.bitstrings;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import aitoa.TestTools;
import aitoa.examples.bitstrings.OneMaxObjectiveFunction;
import aitoa.searchSpaces.bitstrings.BitStringNullaryOperator;
import aitoa.searchSpaces.bitstrings.BitStringSpace;
import aitoa.structure.BlackBoxProcessBuilder;
import aitoa.structure.IBlackBoxProcess;

/** Test a the Greedy2p1GAmod algorithm */
public class TestGreedy2p1GAmodLongRuns {

  /**
   * test the temperature schedule
   *
   * @throws IOException
   *           if i/o fails
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void testGreedyGAmodLongRunsOnOneMax()
      throws IOException {
    final BlackBoxProcessBuilder<boolean[], boolean[]> bb =
        new BlackBoxProcessBuilder<>();

    final int scale = 128;

    bb.setGoalF(0);
    bb.setNullarySearchOperator(new BitStringNullaryOperator());
    bb.setObjectiveFunction(new OneMaxObjectiveFunction(scale));

    final BitStringSpace space = new BitStringSpace(scale);
    bb.setSearchSpace(space);
    bb.setSolutionSpace(space);

    bb.setRandSeed(0x17aaebd80b6d6997L);
    final Greedy2p1GAmod<boolean[]> algo =
        new Greedy2p1GAmod<>();

    try (final IBlackBoxProcess<boolean[], boolean[]> process =
        bb.get()) {
      algo.solve(process);
      Assert.assertEquals(0, process.getBestF(), 0);
      final boolean[] x = new boolean[scale];
      process.getBestX(x);
      for (final boolean xx : x) {
        Assert.assertTrue(xx);
      }
      TestTools.assertLess(process.getConsumedFEs(), 2000);
    }
  }
}

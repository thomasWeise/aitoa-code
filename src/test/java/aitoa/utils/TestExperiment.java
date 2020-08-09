package aitoa.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import aitoa.algorithms.EA1p1;
import aitoa.algorithms.RandomSampling;
import aitoa.algorithms.bitstrings.Greedy2p1GAmod;
import aitoa.algorithms.bitstrings.Greedy2p1GAmodFFA;
import aitoa.examples.bitstrings.BitStringObjectiveFunction;
import aitoa.examples.bitstrings.LeadingOnesObjectiveFunction;
import aitoa.examples.bitstrings.OneMaxObjectiveFunction;
import aitoa.searchSpaces.bitstrings.BitStringNullaryOperator;
import aitoa.searchSpaces.bitstrings.BitStringUnaryOperatorMOverNFlip;
import aitoa.structure.BlackBoxProcessBuilder;
import aitoa.structure.IMetaheuristic;
import aitoa.utils.Experiment.IExperimentStage;
import aitoa.utils.logs.EndResults;

/** Test the utilities for the experimenting */
public class TestExperiment {

  /**
   * test the name part processing
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 100000)
  public void testNameStringPrepare() {
    for (final String[] inOut : new String[][] { //
        { "a", "a" }, //$NON-NLS-1$//$NON-NLS-2$
        { " a", "a" }, //$NON-NLS-1$//$NON-NLS-2$
        { "a ", "a" }, //$NON-NLS-1$//$NON-NLS-2$
        { " a_ \n", "a" }, //$NON-NLS-1$//$NON-NLS-2$
        { "0.1", "0d1" }, //$NON-NLS-1$//$NON-NLS-2$
        { "_a0.1__b ", "a0d1_b" }, //$NON-NLS-1$//$NON-NLS-2$
        { "_a'\\0.1__b ", "a_0d1_b" }, //$NON-NLS-1$//$NON-NLS-2$
        { "_a'\\0.1__b\" ", "a_0d1_b" }, //$NON-NLS-1$//$NON-NLS-2$
    }) {
      final String in = inOut[0];
      final String out = inOut[1];
      final String res = Experiment.nameStringPrepare(in);
      final String res2 = Experiment.nameFromObjectPrepare(in);
      Assert.assertEquals(out, res);
      Assert.assertEquals(out, res2);
      if (out.equals(in)) {
        Assert.assertSame(in, res);
        Assert.assertSame(in, res2);
      }
      Assert.assertSame(res, Experiment.nameStringPrepare(res));
      Assert.assertSame(res2,
          Experiment.nameStringPrepare(res2));
      Assert.assertEquals(res,
          Experiment.nameStringPrepare(res2));
      Assert.assertEquals(res2,
          Experiment.nameStringPrepare(res));
    }
  }

  /**
   * test the name part merging
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 100000)
  public void testNameStringsMerge() {
    for (final String[] inOut : new String[][] { //
        { "a", "a" }, //$NON-NLS-1$//$NON-NLS-2$
        { " a", "a" }, //$NON-NLS-1$//$NON-NLS-2$
        { "a ", "a" }, //$NON-NLS-1$//$NON-NLS-2$
        { " a_ \n", "a" }, //$NON-NLS-1$//$NON-NLS-2$
        { "0.1", "0d1" }, //$NON-NLS-1$//$NON-NLS-2$
        { "_a0.1__b ", "a0d1_b" }, //$NON-NLS-1$//$NON-NLS-2$
        { "_a'\\0.1__b ", "a_0d1_b" }, //$NON-NLS-1$//$NON-NLS-2$
        { "_a'\\0.1__b\" ", "a_0d1_b" }, //$NON-NLS-1$//$NON-NLS-2$
        { "_a'\\0.1__b\" ", "b", "a_0d1_b_b" }, //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        { "_a'\\0.1__b\" ", "_b", "a_0d1_b_b" }, //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        { "_a'\\0.1__b\" ", "_b .", "a_0d1_b_b_d" }, //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        { "_a'\\0.1__b\" ", "_b .", "x", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
            "a_0d1_b_b_d_x" }, //$NON-NLS-1$
    }) {
      final String res = Experiment.nameStringsMerge(
          Arrays.copyOf(inOut, inOut.length - 1));
      Assert.assertEquals(inOut[inOut.length - 1], res);
      Assert.assertSame(res, Experiment.nameStringPrepare(res));
      Assert.assertSame(res,
          Experiment.nameFromObjectPrepare(res));
    }
  }

  /**
   * test the name part merging
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 100000)
  public void testNameFromObjectsMerge() {
    for (final Object[] inOut : new Object[][] { //
        { "a", null, "a" }, //$NON-NLS-1$//$NON-NLS-2$
        { null, " a", "a" }, //$NON-NLS-1$//$NON-NLS-2$
        { null, "a ", null, "a" }, //$NON-NLS-1$//$NON-NLS-2$
        { " a_ \n", "a" }, //$NON-NLS-1$//$NON-NLS-2$
        { "0.1", "0d1" }, //$NON-NLS-1$//$NON-NLS-2$
        { null, "_a0.1__b ", "a0d1_b" }, //$NON-NLS-1$//$NON-NLS-2$
        { "_a'\\0.1__b ", "a_0d1_b" }, //$NON-NLS-1$//$NON-NLS-2$
        { "_a'\\0.1__b\" ", "a_0d1_b" }, //$NON-NLS-1$//$NON-NLS-2$
        { "_a'\\0.1__b\" ", null, "b", "a_0d1_b_b" }, //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        { null, "_a'\\0.1__b\" ", null, "_b", null, //$NON-NLS-1$//$NON-NLS-2$
            "a_0d1_b_b" }, //$NON-NLS-1$
        { "_a'\\0.1__b\" ", "_b .", null, "a_0d1_b_b_d" }, //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        { null, null, null, "_a'\\0.1__b\" ", "_b .", null, "x", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
            "a_0d1_b_b_d_x" }, //$NON-NLS-1$
    }) {
      final String res = Experiment.nameFromObjectsMerge(
          Arrays.copyOf(inOut, inOut.length - 1));
      Assert.assertEquals(inOut[inOut.length - 1], res);
      Assert.assertSame(res, Experiment.nameStringPrepare(res));
      Assert.assertSame(res,
          Experiment.nameFromObjectPrepare(res));
    }
  }

  /**
   * test the log file path creation
   *
   * @throws IOException
   *           if i/o fails
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 100000)
  public void testLogFile() throws IOException {
    final Path root = IOUtils.canonicalizePath(
        Paths.get(System.getProperty("java.io.tmpdir"))); //$NON-NLS-1$
    Assert.assertNotNull(root);

    final Path dir = Files.createTempDirectory(root, null);
    Assert.assertTrue(Files.exists(dir));
    Assert.assertTrue(Files.isDirectory(dir));

    final Path test = Experiment.logFile(dir, "/2.3", //$NON-NLS-1$
        " sdfsf.g_ _", //$NON-NLS-1$
        1L, false);

    Assert.assertTrue(test.startsWith(dir));
    Assert.assertTrue(Files.exists(test));
    Assert.assertTrue(Files.isRegularFile(test));
    Assert.assertEquals(test.getFileName().toString(),
        "2d3_sdfsfdg_0x0000000000000001.txt");//$NON-NLS-1$

    final Path inst = test.getParent();
    Assert.assertTrue(Files.exists(inst));
    Assert.assertTrue(Files.isDirectory(inst));
    Assert.assertEquals(inst.getFileName().toString(),
        "sdfsfdg");//$NON-NLS-1$

    final Path algo = inst.getParent();
    Assert.assertTrue(Files.exists(algo));
    Assert.assertTrue(Files.isDirectory(algo));
    Assert.assertEquals(algo.getFileName().toString(), "2d3");//$NON-NLS-1$

    final Path r = algo.getParent();
    Assert.assertEquals(dir, r);

    Assert.assertNull(Experiment.logFile(dir, "2.3", //$NON-NLS-1$
        " sdfsfdg_ _", //$NON-NLS-1$
        1L, false));

    Files.delete(test);
    Assert.assertFalse(Files.exists(test));

    final Path test2 = Experiment.logFile(dir, "2d3", //$NON-NLS-1$
        "_sdfsf.g", //$NON-NLS-1$
        1L, false);
    Assert.assertEquals(test, test2);
    Files.delete(test);
    Assert.assertFalse(Files.exists(test));
    Assert.assertFalse(Files.exists(test2));

    Files.delete(inst);
    Assert.assertFalse(Files.exists(inst));

    Files.delete(algo);
    Assert.assertFalse(Files.exists(algo));

    Files.delete(dir);
    Assert.assertFalse(Files.exists(dir));
  }

  /**
   * test the experiment execution
   *
   * @throws IOException
   *           if i/o fails
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 1000000)
  public void testExecuteExperiment() throws IOException {
    try (final TempDir dir = new TempDir()) {

      final IExperimentStage<boolean[], boolean[],
          BitStringObjectiveFunction,
          IMetaheuristic<boolean[], boolean[]>> stage_1 =
              new IExperimentStage<boolean[], boolean[],
                  BitStringObjectiveFunction,
                  IMetaheuristic<boolean[], boolean[]>>() {
                @Override
                public
                    Stream<Supplier<BitStringObjectiveFunction>>
                    getProblems() {
                  return Stream
                      .of(() -> new OneMaxObjectiveFunction(8));
                }

                @Override
                public int getRuns(
                    final BitStringObjectiveFunction problem) {
                  return 3;
                }

                @Override
                public
                    Stream<Supplier<
                        IMetaheuristic<boolean[], boolean[]>>>
                    getAlgorithms(
                        final BitStringObjectiveFunction problem) {
                  return Stream.of(EA1p1::new,
                      RandomSampling::new);
                }

                @Override
                public void configureBuilder(
                    final BlackBoxProcessBuilder<boolean[],
                        boolean[]> builder) {
                  builder.setGoalF(0);
                  builder.setMaxFEs(100);
                  builder.setNullarySearchOperator(
                      new BitStringNullaryOperator());
                  builder.setUnarySearchOperator(
                      new BitStringUnaryOperatorMOverNFlip(1));
                }

                @Override
                public void configureBuilderForProblem(
                    final BlackBoxProcessBuilder<boolean[],
                        boolean[]> builder,
                    final BitStringObjectiveFunction problem) {
                  builder.setSearchSpace(problem.createSpace());
                }
              };

      final IExperimentStage<boolean[], boolean[],
          BitStringObjectiveFunction,
          IMetaheuristic<boolean[], boolean[]>> stage_2 =
              new IExperimentStage<boolean[], boolean[],
                  BitStringObjectiveFunction,
                  IMetaheuristic<boolean[], boolean[]>>() {
                @Override
                public
                    Stream<Supplier<BitStringObjectiveFunction>>
                    getProblems() {
                  return Stream.of(
                      () -> new OneMaxObjectiveFunction(8),
                      () -> new LeadingOnesObjectiveFunction(8));
                }

                @Override
                public int getRuns(
                    final BitStringObjectiveFunction problem) {
                  return 4;
                }

                @Override
                public
                    Stream<Supplier<
                        IMetaheuristic<boolean[], boolean[]>>>
                    getAlgorithms(
                        final BitStringObjectiveFunction problem) {
                  return Stream.of(EA1p1::new,
                      RandomSampling::new);
                }

                @Override
                public void configureBuilder(
                    final BlackBoxProcessBuilder<boolean[],
                        boolean[]> builder) {
                  builder.setGoalF(0);
                  builder.setMaxFEs(100);
                  builder.setNullarySearchOperator(
                      new BitStringNullaryOperator());
                  builder.setUnarySearchOperator(
                      new BitStringUnaryOperatorMOverNFlip(1));
                }

                @Override
                public void configureBuilderForProblem(
                    final BlackBoxProcessBuilder<boolean[],
                        boolean[]> builder,
                    final BitStringObjectiveFunction problem) {
                  builder.setSearchSpace(problem.createSpace());
                }
              };

      Experiment.executeExperiment(
          Stream.of(() -> stage_1, () -> stage_2), dir.getPath(),
          false, false, false, false);

      try (TempDir dir2 = new TempDir()) {
        final Path endResults = EndResults.makeEndResultsTable(
            dir.getPath(), dir2.getPath(), false, false);
        Assert.assertNotNull(endResults);
        Assert.assertTrue(Files.size(endResults) > 10L);
      }
    }
  }

  /**
   * test the experiment execution
   *
   * @throws IOException
   *           if i/o fails
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 1000000)
  public void testExecuteExperimentInParallel()
      throws IOException {
    try (final TempDir dir = new TempDir()) {

      final IExperimentStage<boolean[], boolean[],
          BitStringObjectiveFunction,
          IMetaheuristic<boolean[], boolean[]>> stage_1 =
              new IExperimentStage<boolean[], boolean[],
                  BitStringObjectiveFunction,
                  IMetaheuristic<boolean[], boolean[]>>() {
                @Override
                public
                    Stream<Supplier<BitStringObjectiveFunction>>
                    getProblems() {
                  return Stream
                      .of(() -> new OneMaxObjectiveFunction(8));
                }

                @Override
                public int getRuns(
                    final BitStringObjectiveFunction problem) {
                  return 3;
                }

                @Override
                public
                    Stream<Supplier<
                        IMetaheuristic<boolean[], boolean[]>>>
                    getAlgorithms(
                        final BitStringObjectiveFunction problem) {
                  return Stream.of(EA1p1::new,
                      RandomSampling::new,
                      () -> new Greedy2p1GAmodFFA<>(
                          ((int) (problem.upperBound()))),
                      Greedy2p1GAmod::new);
                }

                @Override
                public void configureBuilder(
                    final BlackBoxProcessBuilder<boolean[],
                        boolean[]> builder) {
                  builder.setGoalF(0);
                  builder.setMaxFEs(100);
                  builder.setNullarySearchOperator(
                      new BitStringNullaryOperator());
                  builder.setUnarySearchOperator(
                      new BitStringUnaryOperatorMOverNFlip(1));
                }

                @Override
                public void configureBuilderForProblem(
                    final BlackBoxProcessBuilder<boolean[],
                        boolean[]> builder,
                    final BitStringObjectiveFunction problem) {
                  builder.setSearchSpace(problem.createSpace());
                }
              };

      final IExperimentStage<boolean[], boolean[],
          BitStringObjectiveFunction,
          IMetaheuristic<boolean[], boolean[]>> stage_2 =
              new IExperimentStage<boolean[], boolean[],
                  BitStringObjectiveFunction,
                  IMetaheuristic<boolean[], boolean[]>>() {
                @Override
                public
                    Stream<Supplier<BitStringObjectiveFunction>>
                    getProblems() {
                  return Stream.of(
                      () -> new OneMaxObjectiveFunction(8),
                      () -> new LeadingOnesObjectiveFunction(8));
                }

                @Override
                public int getRuns(
                    final BitStringObjectiveFunction problem) {
                  return 4;
                }

                @Override
                public
                    Stream<Supplier<
                        IMetaheuristic<boolean[], boolean[]>>>
                    getAlgorithms(
                        final BitStringObjectiveFunction problem) {
                  return Stream.of(EA1p1::new,
                      RandomSampling::new);
                }

                @Override
                public void configureBuilder(
                    final BlackBoxProcessBuilder<boolean[],
                        boolean[]> builder) {
                  builder.setGoalF(0);
                  builder.setMaxFEs(100);
                  builder.setNullarySearchOperator(
                      new BitStringNullaryOperator());
                  builder.setUnarySearchOperator(
                      new BitStringUnaryOperatorMOverNFlip(1));
                }

                @Override
                public void configureBuilderForProblem(
                    final BlackBoxProcessBuilder<boolean[],
                        boolean[]> builder,
                    final BitStringObjectiveFunction problem) {
                  builder.setSearchSpace(problem.createSpace());
                }
              };

      Experiment.executeExperimentInParallel(
          Stream.of(() -> stage_1, () -> stage_2), dir.getPath(),
          2, false, false, false, false);

      try (TempDir dir2 = new TempDir()) {
        final Path endResults = EndResults.makeEndResultsTable(
            dir.getPath(), dir2.getPath(), false, false);
        Assert.assertNotNull(endResults);
        Assert.assertTrue(Files.size(endResults) > 10L);
      }
    }
  }
}

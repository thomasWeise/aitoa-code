package aitoa.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

/** Test the utilities for the experimenting */
public class ExperimentTest {

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
        1L);

    Assert.assertTrue(test.startsWith(dir));
    Assert.assertTrue(Files.exists(test));
    Assert.assertTrue(Files.isRegularFile(test));
    Assert.assertEquals(test.getFileName().toString(),
        "2d3_sdfsfdg_0000000000000001.txt");//$NON-NLS-1$

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
        1L));

    Files.delete(test);
    Assert.assertFalse(Files.exists(test));

    final Path test2 = Experiment.logFile(dir, "2d3", //$NON-NLS-1$
        "_sdfsf.g", //$NON-NLS-1$
        1L);
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

}

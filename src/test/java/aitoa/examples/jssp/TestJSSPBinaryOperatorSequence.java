package aitoa.examples.jssp;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

/** test the binary sequence crossover search operator */
public class TestJSSPBinaryOperatorSequence {

  /** test the application to the canonical instance */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testCanonical() {
    final Random random = ThreadLocalRandom.current();
    for (final JSSPInstance inst : CheckJSSPOperatorUtils.INSTANCS) {
      final JSSPOperatorBinarySequence op =
          new JSSPOperatorBinarySequence(inst);
      final int[] x = new int[inst.m * inst.n];
      final int[] c = new int[inst.m * inst.n];
      CheckJSSPOperatorUtils.canonicalX(c, inst);
      final int[] c2 = c.clone();
      CheckJSSPOperatorUtils.assertX(c2, inst);
      for (int i = 1000; (--i) >= 0;) {
        op.apply(c, c, x, random);
        CheckJSSPOperatorUtils.assertX(x, inst);
        Assert.assertArrayEquals(c, c2);
      }
    }
  }

  /** test the application to random instances */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testRandom() {
    final Random random = ThreadLocalRandom.current();
    for (final JSSPInstance inst : CheckJSSPOperatorUtils.INSTANCS) {
      final JSSPOperatorBinarySequence op =
          new JSSPOperatorBinarySequence(inst);
      final int[] x = new int[inst.m * inst.n];
      final int[] c1 = new int[inst.m * inst.n];
      final int[] c2 = new int[inst.m * inst.n];

      for (int i = 1000; (--i) >= 0;) {
        CheckJSSPOperatorUtils.randomX(c1, inst);
        CheckJSSPOperatorUtils.assertX(c1, inst);
        CheckJSSPOperatorUtils.randomX(c2, inst);
        CheckJSSPOperatorUtils.assertX(c2, inst);
        op.apply(c1, c2, x, random);
        CheckJSSPOperatorUtils.assertX(x, inst);
      }
    }
  }
}

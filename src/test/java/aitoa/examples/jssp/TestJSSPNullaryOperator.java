package aitoa.examples.jssp;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

/** test the nullary search operator */
public class TestJSSPNullaryOperator {

  /** test the application to the canonical instance operator */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testNullaryOperator() {
    final Random random = ThreadLocalRandom.current();
    for (final JSSPInstance inst : CheckJSSPOperatorUtils.INSTANCS) {
      final JSSPNullaryOperator op =
          new JSSPNullaryOperator(inst);
      final int[] x = new int[inst.m * inst.n];
      for (int i = 100; (--i) >= 0;) {
        op.apply(x, random);
        CheckJSSPOperatorUtils.assertX(x, inst);
      }
    }
  }
}

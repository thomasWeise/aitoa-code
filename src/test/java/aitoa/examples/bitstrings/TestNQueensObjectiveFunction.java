package aitoa.examples.bitstrings;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import aitoa.TestTools;
import aitoa.structure.IObjectiveFunction;
import aitoa.structure.IObjectiveFunctionTest;

/** A Test for the N-Queens problem */
public class TestNQueensObjectiveFunction
    extends IObjectiveFunctionTest<boolean[]> {

  /** the NQueens */
  public static final NQueensObjectiveFunction F =
      new NQueensObjectiveFunction(4 * 4);

  /** {@inheritDoc} */
  @Override
  protected IObjectiveFunction<boolean[]> getInstance() {
    return TestNQueensObjectiveFunction.F;
  }

  /** {@inheritDoc} */
  @Override
  protected boolean[] createValid() {
    final boolean[] x =
        new boolean[TestNQueensObjectiveFunction.F.n];
    final Random r = ThreadLocalRandom.current();
    for (int i = x.length; (--i) >= 0;) {
      x[i] = r.nextBoolean();
    }
    return x;
  }

  /** test the correctness */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testCorrectnessF4() {
    final boolean[] opt1 = { //
        false, true, false, false, //
        false, false, false, true, //
        true, false, false, false, //
        false, false, true, false,//
    };
    final boolean[] opt2 = { //
        false, false, true, false, //
        true, false, false, false, //
        false, false, false, true, false, true, false, false };

    final BitStringObjectiveFunction f =
        new NQueensObjectiveFunction(opt1.length);

    Assert.assertEquals(0, f.evaluate(opt1), 0);
    Assert.assertEquals(0, f.evaluate(opt2), 0);

    final int upper = ((int) (f.upperBound()));
    for (int i = opt1.length; (--i) >= 0;) {
      final boolean b1 = opt1[i];
      final boolean b2 = opt2[i];

      opt1[i] = !b1;
      opt2[i] = !b2;

      int v = (int) (f.evaluate(opt1));
      TestTools.assertGreater(v, 0);
      TestTools.assertLessOrEqual(v, upper);
      v = (int) (f.evaluate(opt2));
      TestTools.assertGreater(v, 0);
      TestTools.assertLessOrEqual(v, upper);

      for (int j = i; (--j) >= 0;) {
        final boolean bb1 = opt1[j];
        final boolean bb2 = opt2[j];

        opt1[j] = !bb1;
        opt2[j] = !bb2;

        v = (int) (f.evaluate(opt1));
        TestTools.assertGreater(v, 0);
        TestTools.assertLessOrEqual(v, upper);
        v = (int) (f.evaluate(opt2));
        TestTools.assertGreater(v, 0);
        TestTools.assertLessOrEqual(v, upper);

        opt1[j] = bb1;
        opt2[j] = bb2;
      }

      opt1[i] = b1;
      opt2[i] = b2;
    }
  }

  /** test the correctness */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testCorrectnessF8() {
    final boolean[] opt = { //
        false, false, false, true, false, false, false, false, //
        false, false, false, false, false, true, false, false, //
        false, false, false, false, false, false, false, true, //
        false, true, false, false, false, false, false, false, //
        false, false, false, false, false, false, true, false, //
        true, false, false, false, false, false, false, false, //
        false, false, true, false, false, false, false, false, //
        false, false, false, false, true, false, false, false, //
    };

    final BitStringObjectiveFunction f =
        new NQueensObjectiveFunction(opt.length);

    Assert.assertEquals(0, f.evaluate(opt), 0);

    final int upper = ((int) (f.upperBound()));
    for (int i = opt.length; (--i) >= 0;) {
      final boolean b1 = opt[i];

      opt[i] = !b1;

      int v = (int) (f.evaluate(opt));
      TestTools.assertGreater(v, 0);
      TestTools.assertLessOrEqual(v, upper);

      for (int j = i; (--j) >= 0;) {
        final boolean bb1 = opt[j];

        opt[j] = !bb1;

        v = (int) (f.evaluate(opt));
        TestTools.assertGreater(v, 0);
        TestTools.assertLessOrEqual(v, upper);

        for (int k = j; (--k) >= 0;) {
          final boolean bbb1 = opt[k];

          opt[k] = !bbb1;

          v = (int) (f.evaluate(opt));
          TestTools.assertGreater(v, 0);
          TestTools.assertLessOrEqual(v, upper);

          for (int l = j; (--l) >= 0;) {
            final boolean bbbb1 = opt[l];

            opt[l] = !bbbb1;

            v = (int) (f.evaluate(opt));
            TestTools.assertGreater(v, 0);
            TestTools.assertLessOrEqual(v, upper);

            opt[l] = bbbb1;
          }

          opt[k] = bbb1;
        }

        opt[j] = bb1;
      }

      opt[i] = b1;
    }
  }

  /** test the correctness */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testCorrectnessF4B() {
    final boolean[] x = { //
        false, false, false, false, //
        false, false, false, false, //
        false, false, false, false, //
        false, false, false, false };
    final BitStringObjectiveFunction f =
        new NQueensObjectiveFunction(x.length);

    Assert.assertEquals(4, f.evaluate(x), 0d);
    x[0] = true;
    Assert.assertEquals(3, f.evaluate(x), 0d);

// two queens, but in the same row
    x[1] = true;
    Assert.assertEquals(2 + 4, f.evaluate(x), 0d);

// three queens, but 2 in the same row, 2 in the same column, and
// 2 in one diagonal
    x[4] = true;
    Assert.assertEquals(1 + 4 + 4 + 4, f.evaluate(x), 0d);

// four queens, but 3 in the same row, 2 in the same column, and
// 2 in one diagonal
    x[2] = true;
    Assert.assertEquals(0 + 8 + 4 + 4, f.evaluate(x), 0d);

// five queens, but 4 in the same row, 2 in the same column, and
// 2 in one diagonal
    x[3] = true;
    Assert.assertEquals(-1 + 12 + 4 + 4, f.evaluate(x), 0d);
  }
}

package aitoa.examples.jssp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import aitoa.structure.IModel;
import aitoa.structure.IModelTest;
import aitoa.structure.ISpace;

/** test the univariate model for the JSSP */
public class TestJSSPUMDAModel extends IModelTest<int[]> {

  /** the space we use */
  private static final JSSPInstance PROBLEM =
      new JSSPInstance("swv18"); //$NON-NLS-1$

  /** the space we use */
  private static final JSSPSearchSpace SPACE =
      new JSSPSearchSpace(TestJSSPUMDAModel.PROBLEM);

  /** the operator we use */
  private static final IModel<int[]> OP =
      new JSSPUMDAModel(TestJSSPUMDAModel.PROBLEM, 1L);

  /** {@inheritDoc} */
  @Override
  protected ISpace<int[]> getSpace() {
    return TestJSSPUMDAModel.SPACE;
  }

  /** {@inheritDoc} */
  @Override
  protected IModel<int[]> getModel(final ISpace<int[]> space) {
    return TestJSSPUMDAModel.OP;
  }

  /** {@inheritDoc} */
  @Override
  protected boolean equals(final int[] a, final int[] b) {
    return Arrays.equals(a, b);
  }

  /** {@inheritDoc} */
  @Override
  protected int[] createValid() {
    return JSSPTestUtils.createValidX(TestJSSPUMDAModel.PROBLEM);
  }

  /**
   * Test the model
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testModelTraining() {
    final JSSPInstance demo = new JSSPInstance("demo"); //$NON-NLS-1$
    final JSSPUMDAModel model = new JSSPUMDAModel(demo, 1000L);

    for (int z = 100; (--z) >= 0;) {
      model.initialize();
      final int[] template = JSSPTestUtils.createValidX(demo);
      final ArrayList<int[]> list = new ArrayList<>();
      list.add(template);
      model.update(list);

      final int[] dest = new int[demo.m * demo.n];
      checker: {
        for (int i = 10; (--i) >= 0;) {
          model.sample(dest, ThreadLocalRandom.current());
          if (Arrays.equals(template, dest)) {
            break checker;
          }
        }
        Assert.fail("never sampled right result."); //$NON-NLS-1$
      }
    }
  }

  /**
   * Test whether the internal find function works correct
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testFind() {
    long[] array = new long[] { 1, 2 };
    Assert.assertEquals(0, JSSPUMDAModel.find(0, array, 2));
    Assert.assertEquals(1, JSSPUMDAModel.find(1, array, 2));

    array = new long[] { 1, 3 };
    Assert.assertEquals(0, JSSPUMDAModel.find(0, array, 2));
    Assert.assertEquals(1, JSSPUMDAModel.find(1, array, 2));
    Assert.assertEquals(1, JSSPUMDAModel.find(2, array, 2));

    array = new long[] { 2, 3 };
    Assert.assertEquals(0, JSSPUMDAModel.find(0, array, 2));
    Assert.assertEquals(0, JSSPUMDAModel.find(1, array, 2));
    Assert.assertEquals(1, JSSPUMDAModel.find(2, array, 2));

    array = new long[] { 2, 4 };
    Assert.assertEquals(0, JSSPUMDAModel.find(0, array, 2));
    Assert.assertEquals(0, JSSPUMDAModel.find(1, array, 2));
    Assert.assertEquals(1, JSSPUMDAModel.find(2, array, 2));
    Assert.assertEquals(1, JSSPUMDAModel.find(3, array, 2));

    array = new long[] { 1, 2, 3 };
    Assert.assertEquals(0, JSSPUMDAModel.find(0, array, 3));
    Assert.assertEquals(1, JSSPUMDAModel.find(1, array, 3));
    Assert.assertEquals(2, JSSPUMDAModel.find(2, array, 3));

    array = new long[] { 1, 2, 4 };
    Assert.assertEquals(0, JSSPUMDAModel.find(0, array, 3));
    Assert.assertEquals(1, JSSPUMDAModel.find(1, array, 3));
    Assert.assertEquals(2, JSSPUMDAModel.find(2, array, 3));
    Assert.assertEquals(2, JSSPUMDAModel.find(3, array, 3));

    array = new long[] { 3, 4, 5, 8 };
    Assert.assertEquals(0, JSSPUMDAModel.find(0, array, 4));
    Assert.assertEquals(0, JSSPUMDAModel.find(1, array, 4));
    Assert.assertEquals(0, JSSPUMDAModel.find(2, array, 4));
    Assert.assertEquals(1, JSSPUMDAModel.find(3, array, 4));
    Assert.assertEquals(2, JSSPUMDAModel.find(4, array, 4));
    Assert.assertEquals(3, JSSPUMDAModel.find(5, array, 4));
    Assert.assertEquals(3, JSSPUMDAModel.find(6, array, 4));
    Assert.assertEquals(3, JSSPUMDAModel.find(7, array, 4));
  }

// The tests below are only relevant in cases where an index
// could have zero probability. This is not possible in the
// current implementation.
// /**
// * Test whether the internal find function works correct with
// * 0s
// */
// @SuppressWarnings("static-method")
// @Test(timeout = 3600000)
// public final void testFind2() {
// final long[] data = { 0, 0, 1, 2, 3 };
// final long[] array = new long[data.length];
//
// long sum = 0L;
// for (int i = 0; i < array.length; i++) {
// sum += data[i];
// array[i] = sum;
// }
// Assert.assertEquals(6, sum);
//
// Assert.assertEquals(2,
// JSSPUMDAModel.find(0, array, array.length));
// Assert.assertEquals(3,
// JSSPUMDAModel.find(1, array, array.length));
// Assert.assertEquals(3,
// JSSPUMDAModel.find(2, array, array.length));
// Assert.assertEquals(4,
// JSSPUMDAModel.find(3, array, array.length));
// Assert.assertEquals(4,
// JSSPUMDAModel.find(4, array, array.length));
// Assert.assertEquals(4,
// JSSPUMDAModel.find(5, array, array.length));
// }
//
// /**
// * Test whether the internal find function works correct with
// * 0s
// */
// @SuppressWarnings("static-method")
// @Test(timeout = 3600000)
// public final void testFind3() {
// final long[] data = { 0, 0, 1, 0, 1, 0, 1 };
// final long[] array = new long[data.length];
//
// long sum = 0L;
// for (int i = 0; i < array.length; i++) {
// sum += data[i];
// array[i] = sum;
// }
// Assert.assertEquals(3, sum);
//
// Assert.assertEquals(2,
// JSSPUMDAModel.find(0, array, array.length));
// Assert.assertEquals(4,
// JSSPUMDAModel.find(1, array, array.length));
// Assert.assertEquals(6,
// JSSPUMDAModel.find(2, array, array.length));
// }
//
// /**
// * Test whether the internal find function works correct with
// * 0s
// */
// @SuppressWarnings("static-method")
// @Test(timeout = 3600000)
// public final void testFind4() {
// final long[] data = { 0, 0, 1, 0, 0, 0, 3, 0, 1, 0, 2, 0 };
// final long[] array = new long[data.length];
//
// long sum = 0L;
// for (int i = 0; i < array.length; i++) {
// sum += data[i];
// array[i] = sum;
// }
// Assert.assertEquals(7, sum);
//
// Assert.assertEquals(2,
// JSSPUMDAModel.find(0, array, array.length));
// Assert.assertEquals(6,
// JSSPUMDAModel.find(1, array, array.length));
// Assert.assertEquals(6,
// JSSPUMDAModel.find(2, array, array.length));
// Assert.assertEquals(6,
// JSSPUMDAModel.find(3, array, array.length));
// Assert.assertEquals(8,
// JSSPUMDAModel.find(4, array, array.length));
// Assert.assertEquals(10,
// JSSPUMDAModel.find(5, array, array.length));
// Assert.assertEquals(10,
// JSSPUMDAModel.find(6, array, array.length));
// }
}

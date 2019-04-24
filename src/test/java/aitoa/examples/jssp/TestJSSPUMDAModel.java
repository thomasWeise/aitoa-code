package aitoa.examples.jssp;

import java.util.Arrays;

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
      new JSSPUMDAModel(TestJSSPUMDAModel.PROBLEM);

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
}

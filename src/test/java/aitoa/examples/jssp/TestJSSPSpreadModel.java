package aitoa.examples.jssp;

import java.util.Arrays;

import aitoa.structure.IModel;
import aitoa.structure.IModelTest;
import aitoa.structure.ISpace;

/** test the univariate model for the JSSP */
public class TestJSSPSpreadModel extends IModelTest<int[]> {

  /** the space we use */
  private static final JSSPInstance PROBLEM =
      new JSSPInstance("swv18"); //$NON-NLS-1$

  /** the space we use */
  private static final JSSPSearchSpace SPACE =
      new JSSPSearchSpace(TestJSSPSpreadModel.PROBLEM);

  /** the operator we use */
  private static final IModel<int[]> OP =
      new JSSPSpreadModel(TestJSSPSpreadModel.PROBLEM, 16);

  /** {@inheritDoc} */
  @Override
  protected ISpace<int[]> getSpace() {
    return TestJSSPSpreadModel.SPACE;
  }

  /** {@inheritDoc} */
  @Override
  protected IModel<int[]> getModel(final ISpace<int[]> space) {
    return TestJSSPSpreadModel.OP;
  }

  /** {@inheritDoc} */
  @Override
  protected boolean equals(final int[] a, final int[] b) {
    return Arrays.equals(a, b);
  }

  /** {@inheritDoc} */
  @Override
  protected int[] createValid() {
    return JSSPTestUtils
        .createValidX(TestJSSPSpreadModel.PROBLEM);
  }
}

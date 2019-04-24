package aitoa.searchSpaces.trees.math;

import org.junit.Assert;
import org.junit.Test;

import aitoa.searchSpaces.trees.Node;
import aitoa.searchSpaces.trees.NodeType;

/** Test the atan2 function */
public class TestAtan2 extends MathFunctionTest {

  /** test an subtraction */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public TestAtan2() {
    super(new ATan2(NodeType.dummy(),
        new Node[] { TestDoubleConstant.constant(27.25),
            TestDoubleConstant.constant(5.5) }));
  }

  /** test the to-string value */
  @Test(timeout = 3600000)
  public void testToStringValue() {
    Assert.assertEquals("atan2(27.25,5.5)", //$NON-NLS-1$
        this.getInstance().toString());
  }

  /** {@inheritDoc} */
  @Override
  protected final MathFunction<double[]>
      getInstance(final double... args) {
    return new ATan2<>(NodeType.dummy(),
        new Node[] { TestDoubleConstant.constant(args[0]),
            TestDoubleConstant.constant(args[1]) });
  }

  /** {@inheritDoc} */
  @Override
  protected final MathFunction<double[]>
      getInstance(final long... args) {
    return new ATan2<>(NodeType.dummy(),
        new Node[] { TestLongConstant.constant(args[0]),
            TestLongConstant.constant(args[1]) });
  }

  /** {@inheritDoc} */
  @Override
  protected final double[][] getDoubleTestCases() {
    return new double[][] { //
        { 0d, 0d, 0d }, //
        { 0d, 1d, 0d }, //
        { 1, 0, Math.PI / 2 }, //
        { -1, 0, -Math.PI / 2 }, //
        { 1d, 1d, Math.PI / 4d }, //
        { -1d, 1d, -Math.PI / 4d }, //
        { -1d, -1d, (-3d * Math.PI) / 4 }, //
        { 1d, -1d, (3d * Math.PI) / 4d },//
    };
  }

  /** {@inheritDoc} */
  @Override
  protected final long[][] getLongTestCases() {
    return new long[][] { //
        { 0, 0, 0 }, //
        { 0, 1, 0 }, //
        { 1, 0, 90 }, //
        { -1, 0, -90 }, //
        { 1, 1, 45 }, //
        { -1, 1, -45 }, //
        { -1, -1, -135 }, //
        { 1, -1, 135 },//
    };
  }

  /** {@inheritDoc} */
  @Override
  protected final int[][] getIntTestCases() {
    return new int[][] { //
        { 0, 0, 0 }, //
        { 0, 1, 0 }, //
        { 1, 0, 90 }, //
        { -1, 0, -90 }, //
        { 1, 1, 45 }, //
        { -1, 1, -45 }, //
        { -1, -1, -135 }, //
        { 1, -1, 135 },//
    };
  }
}

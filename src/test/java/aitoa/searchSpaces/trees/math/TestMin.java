package aitoa.searchSpaces.trees.math;

import org.junit.Assert;
import org.junit.Test;

import aitoa.searchSpaces.trees.Node;
import aitoa.searchSpaces.trees.NodeType;

/** Test the minimum function */
public class TestMin extends MathFunctionTest {

  /** test a minimum */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public TestMin() {
    super(new Min(NodeType.dummy(),
        new Node[] { TestDoubleConstant.constant(27.25),
            TestDoubleConstant.constant(5.5) }));
  }

  /** test the to-string value */
  @Test(timeout = 3600000)
  public void testToStringValue() {
    Assert.assertEquals("min(27.25,5.5)", //$NON-NLS-1$
        this.getInstance().toString());
  }

  /** {@inheritDoc} */
  @Override
  protected final MathFunction<double[]>
      getInstance(final double... args) {
    return new Min<>(NodeType.dummy(),
        new Node[] { TestDoubleConstant.constant(args[0]),
            TestDoubleConstant.constant(args[1]) });
  }

  /** {@inheritDoc} */
  @Override
  protected final MathFunction<double[]>
      getInstance(final long... args) {
    return new Min<>(NodeType.dummy(),
        new Node[] { TestLongConstant.constant(args[0]),
            TestLongConstant.constant(args[1]) });
  }

  /** {@inheritDoc} */
  @Override
  protected final double[][] getDoubleTestCases() {
    return new double[][] { //
        { 0d, 0d, 0d }, //
        { 0d, 1d, 0d }, //
        { -1d, 0d, -1d }, //
        { -1d, -2d, -2d }, //
        { -10d, 11d, -10d }, //
    };
  }

  /** {@inheritDoc} */
  @Override
  protected final long[][] getLongTestCases() {
    return new long[][] { //
        { 0L, 0L, 0L }, //
        { 0L, 1L, 0L }, //
        { -1L, 0L, -1L }, //
        { -1L, -2L, -2L }, //
        { -10L, 11L, -10L }, //
    };
  }

  /** {@inheritDoc} */
  @Override
  protected final int[][] getIntTestCases() {
    return new int[][] { //
        { 0, 0, 0 }, //
        { 0, 1, 0 }, //
        { -1, 0, -1 }, //
        { -1, -2, -2 }, //
        { -10, 11, -10 }, //
    };
  }
}

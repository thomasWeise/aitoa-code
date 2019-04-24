package aitoa.searchSpaces.trees.math;

import org.junit.Assert;
import org.junit.Test;

import aitoa.searchSpaces.trees.Node;
import aitoa.searchSpaces.trees.NodeType;

/** Test the division function */
public class TestDivide extends MathFunctionTest {

  /** test a division */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public TestDivide() {
    super(new Divide(NodeType.dummy(),
        new Node[] { TestDoubleConstant.constant(27.25),
            TestDoubleConstant.constant(5.5) }));
  }

  /** test the to-string value */
  @Test(timeout = 3600000)
  public void testToStringValue() {
    Assert.assertEquals("(27.25/5.5)", //$NON-NLS-1$
        this.getInstance().toString());
  }

  /** {@inheritDoc} */
  @Override
  protected final MathFunction<double[]>
      getInstance(final double... args) {
    return new Divide<>(NodeType.dummy(),
        new Node[] { TestDoubleConstant.constant(args[0]),
            TestDoubleConstant.constant(args[1]) });
  }

  /** {@inheritDoc} */
  @Override
  protected final MathFunction<double[]>
      getInstance(final long... args) {
    return new Divide<>(NodeType.dummy(),
        new Node[] { TestLongConstant.constant(args[0]),
            TestLongConstant.constant(args[1]) });
  }

  /** {@inheritDoc} */
  @Override
  protected final double[][] getDoubleTestCases() {
    return new double[][] { //
        { 0d, 0d, 1d }, //
        { 0d, 1d, 0d }, //
        { -1d, 0d, Double.NEGATIVE_INFINITY }, //
        { 1d, 0d, Double.POSITIVE_INFINITY }, //
        { Double.MAX_VALUE, Double.MAX_VALUE, 1d }, //
        { Double.MAX_VALUE, -Double.MAX_VALUE, -1d }, //
        { Double.NEGATIVE_INFINITY, 0d,
            Double.NEGATIVE_INFINITY }, //
        { 1d, Double.POSITIVE_INFINITY, 0d }, //
        { Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
            1d }, //
        { Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
            1d }, //
    };
  }

  /** {@inheritDoc} */
  @Override
  protected final long[][] getLongTestCases() {
    return new long[][] { //
        { 0L, 0L, 1L }, //
        { 12L, 1L, 12L }, //
        { Long.MAX_VALUE, -1L, Long.MIN_VALUE + 1L }, //
        { Long.MAX_VALUE, 1L, Long.MAX_VALUE }, //
        { Long.MAX_VALUE / 5L, 7L, (Long.MAX_VALUE / 5L) / 7L }, //
        { Long.MIN_VALUE, 2L, Long.MIN_VALUE / 2L }, };
  }

  /** {@inheritDoc} */
  @Override
  protected final int[][] getIntTestCases() {
    return new int[][] { //
        { 0, 0, 1 }, //
        { 12, 1, 12 }, //
        { Integer.MAX_VALUE, -1, Integer.MIN_VALUE + 1 }, //
        { Integer.MAX_VALUE, 1, Integer.MAX_VALUE }, //
        { Integer.MIN_VALUE, 2, Integer.MIN_VALUE / 2 }, //
        { Integer.MIN_VALUE / 12, -22,
            (Integer.MIN_VALUE / 12) / (-22) }, };
  }
}

package aitoa.searchSpaces.trees.math;

import org.junit.Assert;
import org.junit.Test;

import aitoa.searchSpaces.trees.Node;
import aitoa.searchSpaces.trees.NodeType;

/** Test the subtract function */
public class TestSubtract extends MathFunctionTest {

  /** test an subtraction */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public TestSubtract() {
    super(new Subtract(NodeType.dummy(),
        new Node[] { TestDoubleConstant.constant(27.25),
            TestDoubleConstant.constant(5.5) }));
  }

  /** test the node value */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Test(timeout = 3600000)
  public void testValue() {
    final Subtract c = ((Subtract) (this.getInstance()));
    Assert.assertEquals(21.75, c.applyAsDouble(null), 0d);
    Assert.assertEquals(22, c.applyAsInt(null));
    Assert.assertEquals(22L, c.applyAsLong(null));
  }

  /** test the to-string value */
  @Test(timeout = 3600000)
  public void testToStringValue() {
    Assert.assertEquals("(27.25-5.5)", //$NON-NLS-1$
        this.getInstance().toString());
  }

  /** {@inheritDoc} */
  @Override
  protected final MathFunction<double[]>
      getInstance(final double... args) {
    return new Subtract<>(NodeType.dummy(),
        new Node[] { TestDoubleConstant.constant(args[0]),
            TestDoubleConstant.constant(args[1]) });
  }

  /** {@inheritDoc} */
  @Override
  protected final MathFunction<double[]>
      getInstance(final long... args) {
    return new Subtract<>(NodeType.dummy(),
        new Node[] { TestLongConstant.constant(args[0]),
            TestLongConstant.constant(args[1]) });
  }

  /** {@inheritDoc} */
  @Override
  protected final double[][] getDoubleTestCases() {
    return new double[][] { //
        { 0d, 0d, 0d }, //
        { 0d, 1d, -1d }, //
        { -1d, 0d, -1d }, //
        { Double.MAX_VALUE, -Double.MAX_VALUE,
            Double.POSITIVE_INFINITY }, //
        { Double.MAX_VALUE, Double.MAX_VALUE, 0 }, //
        { -Double.MAX_VALUE, Double.MAX_VALUE,
            Double.NEGATIVE_INFINITY },//
    };
  }

  /** {@inheritDoc} */
  @Override
  protected final long[][] getLongTestCases() {
    return new long[][] { //
        { 0L, 0L, 0L }, //
        { 12L, 1L, 11L }, //
        { Long.MAX_VALUE, -1L, Long.MAX_VALUE }, //
        { Long.MIN_VALUE, 1L, Long.MIN_VALUE }, };
  }

  /** {@inheritDoc} */
  @Override
  protected final int[][] getIntTestCases() {
    return new int[][] { //
        { 0, 0, 0 }, //
        { 12, 1, 11 }, //
        { Integer.MAX_VALUE, -1, Integer.MAX_VALUE }, //
        { Integer.MIN_VALUE, 1, Integer.MIN_VALUE }, };
  }
}

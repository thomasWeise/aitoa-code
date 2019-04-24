package aitoa.searchSpaces.trees.math;

import org.junit.Assert;
import org.junit.Test;

import aitoa.searchSpaces.trees.Node;
import aitoa.searchSpaces.trees.NodeType;

/** Test the Add function */
public class TestAdd extends MathFunctionTest {

  /** test an addition */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public TestAdd() {
    super(new Add(NodeType.dummy(),
        new Node[] { TestDoubleConstant.constant(3.35),
            TestDoubleConstant.constant(-5) }));
  }

  /** test the node value */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Test(timeout = 3600000)
  public void testValue() {
    final Add c = ((Add) (this.getInstance()));
    Assert.assertEquals(-1.65d, c.applyAsDouble(null), 0d);
    Assert.assertEquals(-2, c.applyAsInt(null));
    Assert.assertEquals(-2L, c.applyAsLong(null));
  }

  /** test the to-string value */
  @Test(timeout = 3600000)
  public void testToStringValue() {
    Assert.assertEquals("(3.35+-5)", //$NON-NLS-1$
        this.getInstance().toString());
  }

  /** {@inheritDoc} */
  @Override
  protected final MathFunction<double[]>
      getInstance(final double... args) {
    return new Add<>(NodeType.dummy(),
        new Node[] { TestDoubleConstant.constant(args[0]),
            TestDoubleConstant.constant(args[1]) });
  }

  /** {@inheritDoc} */
  @Override
  protected final MathFunction<double[]>
      getInstance(final long... args) {
    return new Add<>(NodeType.dummy(),
        new Node[] { TestLongConstant.constant(args[0]),
            TestLongConstant.constant(args[1]) });
  }

  /** {@inheritDoc} */
  @Override
  protected final double[][] getDoubleTestCases() {
    return new double[][] { //
        { 0d, 0d, 0d }, //
        { 0d, 1d, 1d }, //
        { -1d, 0d, -1d }, //
        { Double.MAX_VALUE, Double.MAX_VALUE,
            Double.POSITIVE_INFINITY }, //
        { -Double.MAX_VALUE, -Double.MAX_VALUE,
            Double.NEGATIVE_INFINITY },//
    };
  }

  /** {@inheritDoc} */
  @Override
  protected final long[][] getLongTestCases() {
    return new long[][] { //
        { 0L, 0L, 0L }, //
        { 12L, 1L, 13L }, //
        { Long.MAX_VALUE, 1L, Long.MAX_VALUE }, //
        { Long.MIN_VALUE, -1L, Long.MIN_VALUE }, };
  }

  /** {@inheritDoc} */
  @Override
  protected final int[][] getIntTestCases() {
    return new int[][] { //
        { 0, 0, 0 }, //
        { 12, 1, 13 }, //
        { Integer.MAX_VALUE, 1, Integer.MAX_VALUE }, //
        { Integer.MIN_VALUE, -1, Integer.MIN_VALUE }, };
  }
}

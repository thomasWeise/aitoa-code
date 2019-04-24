package aitoa.searchSpaces.trees.math;

import org.junit.Assert;
import org.junit.Test;

import aitoa.searchSpaces.trees.Node;
import aitoa.searchSpaces.trees.NodeType;

/** Test the if-greater-then-else function */
public class TestIfGreaterThenElse extends MathFunctionTest {

  /** test a if-greater-then-else */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public TestIfGreaterThenElse() {
    super(new IfGreaterThenElse(NodeType.dummy(),
        new Node[] { TestDoubleConstant.constant(27.25),
            TestDoubleConstant.constant(5.5),
            TestDoubleConstant.constant(3),
            TestDoubleConstant.constant(7) }));
  }

  /** test the to-string value */
  @Test(timeout = 3600000)
  public void testToStringValue() {
    Assert.assertEquals("((27.25>5.5)?3:7)", //$NON-NLS-1$
        this.getInstance().toString());
  }

  /** {@inheritDoc} */
  @Override
  protected final MathFunction<double[]>
      getInstance(final double... args) {
    return new IfGreaterThenElse<>(NodeType.dummy(),
        new Node[] { TestDoubleConstant.constant(args[0]),
            TestDoubleConstant.constant(args[1]),
            TestDoubleConstant.constant(args[2]),
            TestDoubleConstant.constant(args[3]) });
  }

  /** {@inheritDoc} */
  @Override
  protected final MathFunction<double[]>
      getInstance(final long... args) {
    return new IfGreaterThenElse<>(NodeType.dummy(),
        new Node[] { TestLongConstant.constant(args[0]),
            TestDoubleConstant.constant(args[1]),
            TestDoubleConstant.constant(args[2]),
            TestDoubleConstant.constant(args[3]) });
  }

  /** {@inheritDoc} */
  @Override
  protected final double[][] getDoubleTestCases() {
    return new double[][] { //
        { 0, 0, 0, 0, 0 }, //
        { 0, 0, 1, 0, 0 }, //
        { 0, 0, 0, 1, 1 }, //
        { 0, 0, 1, 1, 1 }, //
        { -1, 0, 0, 0, 0 }, //
        { -1, 0, 1, 0, 0 }, //
        { -1, 0, 0, 1, 1 }, //
        { 1, 0, 0, 0, 0 }, //
        { 1, 0, 1, 0, 1 }, //
        { 1, 0, 0, 1, 0 }, //
    };
  }

  /** {@inheritDoc} */
  @Override
  protected final long[][] getLongTestCases() {
    return new long[][] { //
        { 0, 0, 0, 0, 0 }, //
        { 0, 0, 1, 0, 0 }, //
        { 0, 0, 0, 1, 1 }, //
        { 0, 0, 1, 1, 1 }, //
        { -1, 0, 0, 0, 0 }, //
        { -1, 0, 1, 0, 0 }, //
        { -1, 0, 0, 1, 1 }, //
        { 1, 0, 0, 0, 0 }, //
        { 1, 0, 1, 0, 1 }, //
        { 1, 0, 0, 1, 0 }, //
    };
  }

  /** {@inheritDoc} */
  @Override
  protected final int[][] getIntTestCases() {
    return new int[][] { //
        { 0, 0, 0, 0, 0 }, //
        { 0, 0, 1, 0, 0 }, //
        { 0, 0, 0, 1, 1 }, //
        { 0, 0, 1, 1, 1 }, //
        { -1, 0, 0, 0, 0 }, //
        { -1, 0, 1, 0, 0 }, //
        { -1, 0, 0, 1, 1 }, //
        { 1, 0, 0, 0, 0 }, //
        { 1, 0, 1, 0, 1 }, //
        { 1, 0, 0, 1, 0 }, //
    };
  }
}

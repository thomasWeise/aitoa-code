package aitoa.searchSpaces.trees.math;

import org.junit.Assert;
import org.junit.Test;

import aitoa.searchSpaces.trees.NodeTest;
import aitoa.searchSpaces.trees.NodeType;

/** Test the double constant function */
public class TestDoubleConstant extends NodeTest {

  /** create */
  public TestDoubleConstant() {
    super(TestDoubleConstant.constant(-7.5d));
  }

  /** test the node value */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Test(timeout = 3600000)
  public void testValue() {
    final DoubleConstant c =
        ((DoubleConstant) (this.getInstance()));
    Assert.assertEquals(-7.5d, c.applyAsDouble(null), 0d);
    Assert.assertEquals(-7, c.applyAsInt(null));
    Assert.assertEquals(-7L, c.applyAsLong(null));
  }

  /** test the to-string value */
  @Test(timeout = 3600000)
  public void testToStringValue() {
    Assert.assertEquals("-7.5", //$NON-NLS-1$
        this.getInstance().toString());
  }

  /**
   * create a double constant
   *
   * @param value
   *          the value
   * @return the constant
   */
  static final DoubleConstant<double[]>
      constant(final double value) {
    return new DoubleConstant<>(NodeType.dummy(), value);
  }
}

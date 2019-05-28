package aitoa.searchSpaces.trees.math;

import org.junit.Assert;
import org.junit.Test;

import aitoa.searchSpaces.trees.NodeType;
import aitoa.searchSpaces.trees.TestNode;

/** Test the long constant function */
public class TestLongConstant extends TestNode {

  /** create */
  public TestLongConstant() {
    super(TestLongConstant.constant(23));
  }

  /** test the node value */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Test(timeout = 3600000)
  public void testValue() {
    final LongConstant c = ((LongConstant) (this.getInstance()));
    Assert.assertEquals(23d, c.applyAsDouble(null), 0d);
    Assert.assertEquals(23, c.applyAsInt(null));
    Assert.assertEquals(23L, c.applyAsLong(null));
  }

  /** test the to-string value */
  @Test(timeout = 3600000)
  public void testToStringValue() {
    Assert.assertEquals("23", //$NON-NLS-1$
        this.getInstance().toString());
  }

  /**
   * create a long constant
   *
   * @param value
   *          the value
   * @return the constant
   */
  static final LongConstant<double[]>
      constant(final long value) {
    return new LongConstant<>(NodeType.dummy(), value);
  }
}

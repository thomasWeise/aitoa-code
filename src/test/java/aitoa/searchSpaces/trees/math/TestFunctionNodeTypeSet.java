package aitoa.searchSpaces.trees.math;

import org.junit.Assert;
import org.junit.Test;

import aitoa.searchSpaces.trees.NodeType;
import aitoa.searchSpaces.trees.NodeTypeSet;
import aitoa.searchSpaces.trees.NodeTypeSetBuilder;
import aitoa.searchSpaces.trees.NodeTypeSetBuilder.Builder;
import aitoa.searchSpaces.trees.TestNodeTypeSet;

/** test a function node type set */
public class TestFunctionNodeTypeSet
    extends TestNodeTypeSet<MathFunction<?>> {

  /** create */
  public TestFunctionNodeTypeSet() {
    super(TestFunctionNodeTypeSet.makeMathNodeTypeSet());
  }

  /**
   * make the node type set
   *
   * @return the node type set
   */
  public static final NodeTypeSet<MathFunction<?>>
      makeMathNodeTypeSet() {
    return TestFunctionNodeTypeSet.makeMathNodeTypeSet(false);
  }

  /** test the node types in this node type set for classes */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Test(timeout = 3600000)
  public void testNodeTypesClasses() {
    final NodeTypeSet<MathFunction<?>> nts = this.getInstance();

    Assert.assertNotNull(
        nts.getTypeForClass(Add.class, false, true));
    Assert.assertNotNull(
        nts.getTypeForClass(Subtract.class, false, true));
    Assert.assertNotNull(
        nts.getTypeForClass(Divide.class, false, true));
    Assert.assertNotNull(
        nts.getTypeForClass(ATan2.class, false, true));
    Assert.assertNotNull(
        nts.getTypeForClass(Multiply.class, false, true));
    Assert.assertNotNull(
        nts.getTypeForClass(Min.class, false, true));
    Assert.assertNotNull(
        nts.getTypeForClass(Max.class, false, true));
    Assert.assertNotNull(nts
        .getTypeForClass(IfGreaterThenElse.class, false, true));

    NodeType<LongConstant> lct =
        nts.getTypeForClass(LongConstant.class, true, false);
    Assert.assertNotNull(lct);
    Assert.assertSame(lct,
        nts.getTypeOfClass(lct.getClass(), true, false));
    NodeType<DoubleConstant> dct =
        nts.getTypeForClass(DoubleConstant.class, true, false);
    Assert.assertNotNull(dct);
    Assert.assertSame(dct,
        nts.getTypeOfClass(dct.getClass(), true, false));

    Assert.assertNotNull(
        nts.getTypeForClass(Add.class, true, true));
    Assert.assertNotNull(
        nts.getTypeForClass(Subtract.class, true, true));
    Assert.assertNotNull(
        nts.getTypeForClass(Divide.class, true, true));
    Assert.assertNotNull(
        nts.getTypeForClass(ATan2.class, true, true));
    Assert.assertNotNull(
        nts.getTypeForClass(Multiply.class, true, true));
    Assert.assertNotNull(
        nts.getTypeForClass(Min.class, true, true));
    Assert.assertNotNull(
        nts.getTypeForClass(Max.class, true, true));
    Assert.assertNotNull(nts
        .getTypeForClass(IfGreaterThenElse.class, true, true));

    lct = nts.getTypeForClass(LongConstant.class, true, true);
    Assert.assertNotNull(lct);
    Assert.assertSame(lct,
        nts.getTypeOfClass(lct.getClass(), true, true));
    dct = nts.getTypeForClass(DoubleConstant.class, true, true);
    Assert.assertNotNull(dct);
    Assert.assertSame(dct,
        nts.getTypeOfClass(dct.getClass(), true, true));
  }

  /**
   * make the node type set
   *
   * @param constantsMustBeUnique
   *          must constants be unique nodes?
   * @return the node type set
   */
  public static final NodeTypeSet<MathFunction<?>>
      makeMathNodeTypeSet(final boolean constantsMustBeUnique) {
    final NodeTypeSetBuilder builder = new NodeTypeSetBuilder();

    final Builder root = builder.rootNodeTypeSet();
    if (!constantsMustBeUnique) {
      root.add(LongConstant.type());
    }
    root.add(Add.class, root, root);
    root.add(Subtract.class, root, root);
    root.add(Divide.class, root, root);
    if (constantsMustBeUnique) {
      root.add(DoubleConstant.type(-1d, 1d, new double[0]));
    } else {
      root.add(DoubleConstant.type());
    }
    root.add(ATan2.class, root, root);
    root.add(Multiply.class, root, root);
    root.add(Min.class, root, root);
    root.add(Max.class, root, root);
    root.add(IfGreaterThenElse.class, root, root, root, root);

    return builder.build();
  }
}

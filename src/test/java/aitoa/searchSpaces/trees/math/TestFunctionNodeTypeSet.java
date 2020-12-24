package aitoa.searchSpaces.trees.math;

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

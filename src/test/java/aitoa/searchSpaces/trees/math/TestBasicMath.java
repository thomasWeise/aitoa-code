package aitoa.searchSpaces.trees.math;

import org.junit.Assert;
import org.junit.Test;

import aitoa.searchSpaces.trees.Node;
import aitoa.searchSpaces.trees.NodeTest;
import aitoa.searchSpaces.trees.NodeType;
import aitoa.searchSpaces.trees.NodeTypeSet;
import aitoa.searchSpaces.trees.NodeTypeSetBuilder;
import aitoa.searchSpaces.trees.NodeTypeSetBuilder.Builder;

/** Test basic math functions */
public class TestBasicMath {

  /** test the creation of an addition */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Test(timeout = 3600000)
  public void testAdd2Typed() {
    final NodeTypeSetBuilder nts = new NodeTypeSetBuilder();

    final Builder b1 = nts.rootNodeTypeSet();
    b1.add(Add.class, b1, b1);
    b1.add(DoubleConstant.type());
    final NodeTypeSet<?> nt = nts.build();

    final DoubleConstant<double[]> c1 = new DoubleConstant<>(
        (NodeType) (nt.getTerminalType(0)), 1);
    final DoubleConstant<double[]> c2 = new DoubleConstant<>(
        (NodeType) (nt.getTerminalType(0)), 7);
    final DoubleConstant<double[]> c3 = new DoubleConstant<>(
        (NodeType) (nt.getTerminalType(0)), 9);
    final Add<double[]> a1 =
        new Add<>((NodeType) (nt.getNonTerminalType(0)),
            new Node[] { c1, c2 });
    final Add<double[]> a2 =
        new Add<>((NodeType) (nt.getNonTerminalType(0)),
            new Node[] { a1, c3 });
    Assert.assertSame(a1.getChild(0), c1);
    Assert.assertSame(a1.getChild(1), c2);
    Assert.assertSame(a2.getChild(0), a1);
    Assert.assertSame(a2.getChild(1), c3);
    Assert.assertEquals(2, a1.getChildCount());
    Assert.assertEquals(2, a2.getChildCount());
    Assert.assertEquals(17, a2.applyAsDouble(null), 0);
    NodeTest.testNode(a2);
  }
}

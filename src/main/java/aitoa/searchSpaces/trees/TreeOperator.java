package aitoa.searchSpaces.trees;

import java.util.Random;

/** A base class for tree-based search operations */
class TreeOperator {

  /** the maximum number of trials before failing */
  static final int MAX_TRIALS = 1000;

  /** the maximum depth */
  final int mMaxDepth;

  /**
   * Create a new tree operation
   *
   * @param pMaxDepth
   *          the maximum tree depth
   */
  TreeOperator(final int pMaxDepth) {
    super();
    this.mMaxDepth = TreeSpace.checkMaxDepth(pMaxDepth);
  }

  /**
   * Create a sub-tree of the specified maximum depth. It can be
   * used to create trees during the random population
   * initialization phase or during mutation steps.
   *
   * @param types
   *          the node types available for creating the tree
   * @param maxDepth
   *          the maximum depth of the tree
   * @param rand
   *          the random number generator
   * @return the new tree
   */
  static final Node createTree(final NodeTypeSet<?> types,
      final int maxDepth, final Random rand) {

    final NodeType<?> t = ((maxDepth <= 1)//
        ? types.getRandomTerminalType(rand)//
        : types.getRandomType(rand));
    if (t == null) {
      return null;
    }

    if (t.isTerminal()) {
      return t.instantiate(null, rand);
    }

    int i = t.mChildTypes.length;
    final Node[] x = new Node[i];
    for (; (--i) >= 0;) {
      if ((x[i] = TreeOperator.createTree(t.mChildTypes[i],
          maxDepth - 1, rand)) == null) {
        return null;
      }
    }
    return t.instantiate(x, rand);
  }
}

package aitoa.searchSpaces.trees;

import java.util.Random;

/** A base class for tree-based search operations */
class _TreeOperator {

  /** the maximum number of trials before failing */
  static final int MAX_TRIALS = 1000;

  /** the maximum depth */
  final int m_maxDepth;

  /**
   * Create a new tree operation
   *
   * @param maxDepth
   *          the maximum tree depth
   */
  _TreeOperator(final int maxDepth) {
    super();
    this.m_maxDepth = TreeSpace._checkMaxDepth(maxDepth);
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
  static final Node _createTree(final NodeTypeSet<?> types,
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

    int i = t.m_childTypes.length;
    final Node[] x = new Node[i];
    for (; (--i) >= 0;) {
      if ((x[i] = _TreeOperator._createTree(t.m_childTypes[i],
          maxDepth - 1, rand)) == null) {
        return null;
      }
    }
    return t.instantiate(x, rand);
  }
}
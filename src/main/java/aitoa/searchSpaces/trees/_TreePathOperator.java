package aitoa.searchSpaces.trees;

import java.util.Objects;
import java.util.Random;

/**
 * A base class for tree-based search operations that work on
 * tree paths
 */
class _TreePathOperator extends _TreeOperator {

  /** the path */
  private final Node[] m_path;

  /** the path indexes */
  private final int[] m_indexInParent;

  /** the length */
  private int m_length;

  /**
   * Create a new tree operation
   *
   * @param md
   *          the maximum tree depth
   */
  _TreePathOperator(final int md) {
    super(md);
    this.m_path = new Node[md];
    this.m_indexInParent = new int[md];
  }

  /**
   * Get the end element of the path
   *
   * @return the element
   */
  final Node _getEnd() {
    return this.m_path[this.m_length - 1];
  }

  /**
   * Get the choices for the element of the path
   *
   * @return the choices for the end element of the path
   */
  final NodeTypeSet<?> _getEndChoices() {
    return this.m_path[this.m_length - 1].m_type.m_typeSet;
  }

  /**
   * Try to modify the end node of the path. If a
   * non-{@code null} value is returned, the path becomes invalid
   * after this operation! If {@code null} is returned, the path
   * remains valid.
   *
   * @param random
   *          the random number generator
   * @return new head node of the path, if the end node could be
   *         modified, or {@code null} if the modification failed
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  final Node _tryModifyEnd(final Random random) {
    final Node end = this.m_path[this.m_length - 1];
    final Node newEnd = ((NodeType) (end.m_type))
        .createModifiedCopy(end, random);
    if ((newEnd != null) && (!Objects.equals(end, newEnd))) {
      return this._replaceEnd(newEnd);
    }
    return null;
  }

  /**
   * Create a random path through the tree. Each node in the tree
   * is selected with exactly the same probability.
   *
   * @param start
   *          the start node
   * @param random
   *          the randomizer
   * @return the path length
   */
  final int _randomPath(final Node start, final Random random) {
    int length = 0;
    int parentIndex = -1;
    final Node[] path = this.m_path;
    final int[] parentIndexes = this.m_indexInParent;
    Node cur = start;

    for (;;) {
      path[length] = cur;
      parentIndexes[length] = parentIndex;
      ++length;

      final int size = cur.getChildCount();
      parentIndex = random.nextInt(size + 1);
      if (parentIndex >= size) {
        break;
      }
      cur = cur.getChild(parentIndex);
    }

    return (this.m_length = length);
  }

  /**
   * Replace the end of this path with the new node. Since tree
   * nodes are immutable, this will result in the creation of a
   * completely new tree and a new root node.<br/>
   * The path becomes invalid after this operation.
   *
   * @param newNode
   *          the new node
   * @return the result new root node of the path, or
   *         {@code null} if the end replacement would lead to no
   *         change
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  final Node _replaceEnd(final Node newNode) {
    int i = this.m_length;
    final Node[] path = this.m_path;
    final int[] parentIndexes = this.m_indexInParent;

    Node x = newNode;
    if (Objects.equals(x, path[--i])) {
      this.m_length = -1; // invalidate path
      return null;
    }
    for (; (--i) >= 0;) {
      final Node current = path[i];
      x = ((NodeType) (current.m_type)).replaceChild(current, x,
          parentIndexes[i + 1]);
    }
    this.m_length = -1; // invalidate path
    return x;
  }
}
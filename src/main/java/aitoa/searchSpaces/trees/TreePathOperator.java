package aitoa.searchSpaces.trees;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

/**
 * A base class for tree-based search operations that work on
 * tree paths
 */
class TreePathOperator extends TreeOperator {

  /** the path */
  private final Node[] mPath;

  /** the path indexes */
  private final int[] mIndexInParent;

  /** the length */
  private int mLength;

  /** the weight-proportional set */
  private int[] mWeightSum;

  /**
   * Create a new tree operation
   *
   * @param pMd
   *          the maximum tree depth
   */
  TreePathOperator(final int pMd) {
    super(pMd);
    this.mPath = new Node[pMd];
    this.mIndexInParent = new int[pMd];
    this.mWeightSum = new int[16];
  }

  /**
   * Get the end element of the path
   *
   * @return the element
   */
  final Node getEnd() {
    return this.mPath[this.mLength - 1];
  }

  /**
   * Get the choices for the element of the path
   *
   * @return the choices for the end element of the path
   */
  final NodeTypeSet<?> getEndChoices() {
    return this.mPath[this.mLength - 1].mType.mTypeSet;
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
  final Node tryModifyEnd(final Random random) {
    final Node end = this.mPath[this.mLength - 1];
    final Node newEnd =
        ((NodeType) (end.mType)).createModifiedCopy(end, random);
    if ((newEnd != null) && (!Objects.equals(end, newEnd))) {
      return this.replaceEnd(newEnd);
    }
    return null;
  }

  /**
   * Create a random path through the tree. Each node in the tree
   * is selected with exactly the same probability as end of the
   * path.
   *
   * @param start
   *          the start node
   * @param random
   *          the randomizer
   * @return the path length
   */
  final int randomPath(final Node start, final Random random) {
    int length = 0;
    int parentIndex = -1;
    final Node[] path = this.mPath;
    final int[] parentIndexes = this.mIndexInParent;
    Node cur = start;

    for (;;) {
// add the step to the path
      path[length] = cur;
      parentIndexes[length] = parentIndex;
      ++length;

// arrived in a terminal node?
      final int size = cur.getChildCount();
      if (size <= 0) {
        break;
      }

// get the weight sum array
      int[] weightSum = this.mWeightSum;
      if (weightSum.length < size) {
        this.mWeightSum = weightSum = new int[size << 1];
      }

// fill the weight sum array with the cumulative weight sum
      int weightTotal = 0;
      for (int i = 0; i < size; i++) {
        weightTotal += cur.getChild(i).weight();
        weightSum[i] = weightTotal;
      }

      final int choice = random.nextInt(weightTotal + 1);
      if (choice >= weightTotal) {
        break; // current node selected as end
      }

// pick next branch based on its weight
      parentIndex =
          Arrays.binarySearch(weightSum, 0, size, choice);
      if (parentIndex < 0) {
        parentIndex = (-(parentIndex + 1));
      } else {
        ++parentIndex;
      }
// pick the selected child
      cur = cur.getChild(parentIndex);
    }

    return (this.mLength = length);
  }

  /**
   * Replace the end of this path with the new node. Since tree
   * nodes are immutable, this will result in the creation of a
   * completely new tree and a new root node.<br>
   * The path becomes invalid after this operation.
   *
   * @param newNode
   *          the new node
   * @return the result new root node of the path, or
   *         {@code null} if the end replacement would lead to no
   *         change
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  final Node replaceEnd(final Node newNode) {
    int i = this.mLength;
    final Node[] path = this.mPath;
    final int[] parentIndexes = this.mIndexInParent;

    Node x = newNode;
    if (Objects.equals(x, path[--i])) {
      this.mLength = -1; // invalidate path
      return null;
    }
    for (; (--i) >= 0;) {
      final Node current = path[i];
      x = ((NodeType) (current.mType)).replaceChild(current, x,
          parentIndexes[i + 1]);
    }
    this.mLength = -1; // invalidate path
    return x;
  }
}

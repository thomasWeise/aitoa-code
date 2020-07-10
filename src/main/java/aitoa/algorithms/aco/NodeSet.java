package aitoa.algorithms.aco;

import java.util.Random;

import aitoa.utils.RandomUtils;

/**
 * This is an utility class that can keep track on nodes that
 * have been used in a permutation. A {@link NodeSet} manages a
 * set of nodes. You can check in O(1) if a given node is in the
 * set / available and you can delete nodes in O(1).
 */
final class NodeSet {

  /** the remaining node ids */
  private final int[] m_nodes;

  /** the node positions */
  private final int[] m_positions;

  /** the number of nodes */
  private int m_n;

  /**
   * instantiate the node manager
   *
   * @param _L
   *          the number of nodes
   */
  NodeSet(final int _L) {
    super();
    this.m_nodes = new int[_L];
    this.m_positions = new int[_L];
  }

  /**
   * Fill in all the nodes into the node set and make sure that
   * they are in random order.
   *
   * @param random
   *          a random number generator
   */
  void fill(final Random random) {
    final int L = this.m_nodes.length;
    for (int i = L; (--i) >= 0;) {
      this.m_nodes[i] = i;
    }
    RandomUtils.shuffle(random, this.m_nodes, 0,
        this.m_nodes.length);
    for (int i = L; (--i) >= 0;) {
      this.m_positions[this.m_nodes[i]] = i;
    }
    this.m_n = L;
  }

  /**
   * Delete a node by its id.
   *
   * @param id
   *          the node's id
   */
  void deleteNode(final int id) {
    final int[] positions = this.m_positions;
    final int[] nodes = this.m_nodes;
    final int size = (--this.m_n);
    final int pos = positions[id];

    if (size > pos) { // must switch
      final int replace = nodes[size];
      nodes[pos] = replace;
      positions[replace] = pos;
    }
    positions[id] = -1;
  }

  /**
   * Delete a the last node and return its id. This method
   * depends on the potentially random state of the node manager.
   * It should best only be used if only a single node is left in
   * the node manager.
   *
   * @return the id of the deleted node
   */
  int deleteLast() {
    final int id = this.m_nodes[--this.m_n];
    this.m_positions[id] = -1;
    return id;
  }

  /**
   * Check if the node has not yet been deleted
   *
   * @param n
   *          the node's id
   * @return {@code true} if the node has not yet been deleted
   */
  boolean isNodeAvailable(final int n) {
    return (this.m_positions[n] >= 0);
  }

  /**
   * Choose a node randomly and delete and return it.
   *
   * @param r
   *          the randomizer
   * @return the randomly chosen (and deleted) node
   */
  int deleteRandom(final Random r) {
    int size = this.m_n;
    final int pos = r.nextInt(size);
    this.m_n = (--size);

    final int[] positions = this.m_positions;
    final int[] nodes = this.m_nodes;
    final int id = nodes[pos];
    if (size > pos) {
      final int replace = nodes[size];
      nodes[pos] = replace;
      positions[replace] = pos;
    }
    positions[id] = -1;
    return id;
  }

  /**
   * get the number of remaining nodes
   *
   * @return the number of remaining nodes
   */
  int size() {
    return this.m_n;
  }

  /**
   * Is the node manager empty?
   *
   * @return {@code true} if no more nodes can be obtained,
   *         {@code false} if there are still nodes
   */
  boolean isEmpty() {
    return (this.m_n <= 0);
  }

  /**
   * get the remaining node at the given index
   *
   * @param i
   *          the node index
   * @return the node id
   */
  int getNodeAt(final int i) {
    return this.m_nodes[i];
  }
}

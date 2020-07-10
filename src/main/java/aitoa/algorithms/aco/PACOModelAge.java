package aitoa.algorithms.aco;

import java.util.Arrays;
import java.util.Random;

/**
 * The population-based Ant Colony Optimization (PACO),
 * implemented as an EDA model.
 *
 * @param <X>
 *          the search space
 */
public class PACOModelAge<X> extends ACOModel<X> {
  /**
   * the fraction of edges to be chosen directly based on the
   * heuristic
   */
  public final double q0;

  /** the power to be applied to the heuristic value */
  public final double beta;

  /** the minimal tau value */
  public final double tau0;

  /** the maximum pheromone that can be assigned to any edge */
  public final double tauMax;

  /** the size of the population */
  public final int K;

  /** the edge matrix used for pheromones */
  private final EdgeMultiSetLK m_matrix;
  /** the node set managing the nodes */
  private final NodeSet m_nodes;

  /** the pheromone multiplier */
  private final double m_pheroMultiplier;

  /** the population of size of at most {@link #K} */
  private final int[][] m_population;

  /** the actual size of the population */
  private int m_popSize;
  /**
   * the index where the next permutation can be stored in the
   * population
   */
  private int m_popIndex;

  /** the values */
  private final double[] m_vs;

  /**
   * Create the PACO model.
   *
   * @param _L
   *          the length of the permutation
   * @param _K
   *          the size of the population
   * @param _q0
   *          the fraction of edges to be chosen directly based
   *          on the heuristic
   * @param _beta
   *          the power to be applied to the heuristic value
   * @param _tauMax
   *          the maximum pheromone that can be assigned to any
   *          edge
   */
  protected PACOModelAge(final int _L, final int _K,
      final double _q0, final double _beta,
      final double _tauMax) {
    super(_L);

    if (_K <= 0) {
      throw new IllegalArgumentException("K must be > 0, but is "//$NON-NLS-1$
          + _K);
    }
    this.K = _K;

    if (Double.isFinite(_q0) && (_q0 >= 0d) && (_q0 <= 1)) {
      this.q0 = _q0;
    } else {
      throw new IllegalArgumentException(
          "q0 must be fron [0,1], but is " //$NON-NLS-1$
              + _q0);
    }

    if (Double.isFinite(_beta) && (_beta >= 0d)) {
      this.beta = _beta;
    } else {
      throw new IllegalArgumentException(
          "beta must be >= 0, but is "//$NON-NLS-1$
              + _beta);
    }

    this.tau0 = (1d / (this.L - 1));
    if ((!Double.isFinite(this.tau0)) || (this.tau0 <= 0d)) {
      throw new IllegalArgumentException(//
          "Huh? tau0=" + this.tau0 //$NON-NLS-1$
              + " for L=" + this.L);//$NON-NLS-1$
    }

    if (Double.isFinite(_tauMax)
        && (_tauMax >= (1d / (this.L - 1)))) {
      this.tauMax = _tauMax;
    } else {
      throw new IllegalArgumentException(//
          "tauMax must be >= 1/(L-1), i.e., >= 1/"//$NON-NLS-1$
              + (this.L - 1) + ", i.e., >= "//$NON-NLS-1$
              + this.tau0 + ", but is "//$NON-NLS-1$
              + _tauMax);
    }

    this.m_pheroMultiplier = (this.tauMax - this.tau0) / this.K;
    if ((!Double.isFinite(this.m_pheroMultiplier))
        || (this.m_pheroMultiplier <= 0d)) {
      throw new IllegalArgumentException(
          "Invalid pheromone multiplier " //$NON-NLS-1$
              + this.m_pheroMultiplier
              + " resulting from tauMax=" + //$NON-NLS-1$
              this.tauMax + " and K=" //$NON-NLS-1$
              + this.K + " at L=" + this.L); //$NON-NLS-1$
    }

    this.m_nodes = new NodeSet(this.L);
    this.m_matrix = new EdgeMultiSetLK(this.L, this.K);

    this.m_population = new int[this.K][this.L];
    this.m_vs = new double[this.L];
  }

  /** {@inheritDoc} */
  @Override
  public void initialize() {
    this.m_matrix.clear();
    this.m_popSize = 0;
    this.m_popIndex = 0;
  }

  /**
   * Get the pheromone value for the edge from {@code a} to
   * {@code b}. {@code b} must be from {@code 0..L-1} whereas
   * {@code a} can also take on the special value {@code -1}, in
   * which case we refer to the "start" of the permutation.
   *
   * @param a
   *          the first node index
   * @param b
   *          the second node index
   * @return the pheromone value
   */
  final double getPheromone(final int a, final int b) {
    return this.tau0 + (this.m_matrix.getEdgeCount(a, b)
        * this.m_pheroMultiplier);
  }

  /** {@inheritDoc} */
  @Override
  public final void update(final Iterable<X> selected) {
    for (final X x : selected) {
      final int[] pi = this.permutationFromX(x);
      final int size = this.m_popSize;
      final int index = this.m_popIndex;
      final int[] dest = this.m_population[index];
      if (size >= this.K) {
        this.m_matrix.removePermutation(dest);
      }
      System.arraycopy(pi, 0, dest, 0, this.L);
      this.m_matrix.addPermutation(pi);
      this.m_popSize = Math.min(this.K, size + 1);
      this.m_popIndex = (index + 1) % this.K;
    }
  }

  /** {@inheritDoc} */
  @Override
  public void apply(final X dest, final Random random) {
    this.m_nodes.fill(random);

// Build one new candidate solution by simulating the behavior of
// one ant moving through the graph.
    int i = 0;
    final int[] x = this.permutationFromX(dest);
    int bestNode = -1;
    final double[] vs = this.m_vs;

// Visit the nodes, after starting at a random node (skipping the
// last node as there is no choice for the last node).
    for (int nodesLeft = this.L; nodesLeft > 1; --nodesLeft) {
      final int lastNode = bestNode;

// With probability q0, always choose best node directly.
      final boolean decideRandomly =
          (random.nextDouble() >= this.q0);

// Ok, calculate the pheromones and heuristic values.
// First: setup the best values.
      double vBest = Double.NEGATIVE_INFINITY;
      double vSum = 0d;

// Then: for each node which is not yet assigned...
      for (int j = 0; j < nodesLeft; j++) {
        final int curNode = this.m_nodes.getNodeAt(j);

// Get the cost of adding the node: Must be >= 0
        final double cost =
            this.getCostOfAppending(curNode, dest);

// Compute the value v = [pheromone^1 * (1/cost)^beta].
        final double v = (this.tau0
            + (this.m_matrix.getEdgeCount(lastNode, curNode)
                * this.m_pheroMultiplier)) // compute pheromone
            * Math.pow(cost, -this.beta); // compute cost impact

// Is v the best pheromone/heuristic value?
        if (v >= vBest) { // Then remember it.
          vBest = v;
          bestNode = curNode;
        }

        if (decideRandomly) {
// Only if we actually are going to use the table we need to add
// up the pheromone/heuristic values and remember them.
// This is needed to later make a value-proportional choice.
// Otherwise, if we decide deterministically anyway, we don't do
// this to save runtime.
          vSum += v;
          vs[j] = vSum;
        }
      }

// Ok, by now we have either found the best node to add (in case
// of !decideRandomly) or built the complete heuristic/pheromone
// decision table (in case of decideRandomly). After this,
// bestNode is the selected node.
      if (decideRandomly) {
        vs[nodesLeft - 1] = Double.POSITIVE_INFINITY;
        int j = Arrays.binarySearch(vs, 0, nodesLeft, //
            random.nextDouble() * vSum);
        if (j < 0) {
          j = (-(j + 1));
        }
        bestNode = this.m_nodes.getNodeAt(j);
      } // else: No random decision: choose the best.

// Visit the chosen node by adding it to the permutation.
      x[i++] = bestNode;// Store node in solution.
      this.append(bestNode, dest); // potential internal update
      this.m_nodes.deleteNode(bestNode);// bestNode done
    }

// Add the last node: There only is one choice.
    this.append(x[i] = this.m_nodes.deleteLast(), dest);
  }
}

package aitoa.algorithms;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Random;

import aitoa.structure.LogFormat;
import aitoa.utils.ReflectionUtils;
import aitoa.utils.graph.DirectedEdgeMultiSet;
import aitoa.utils.graph.IntSet;

/**
 * The population-based Ant Colony Optimization (PACO),
 * implemented as an EDA model.
 * <p>
 * The population of the ants is managed in an age-based manner.
 * The population will hold at most {@link #popSize} "ants"
 * (permutations). Initially, it is empty. In each iteration, new
 * ants may enter the population (via a call of
 * {@link #update(Iterable)}). If adding a new ant (permutation)
 * would lead to exceeding the maximum population size
 * {@link #popSize}, the "oldest" ant, i.e., the ant in the
 * population which entered the population at the earliest time,
 * will be removed. The population is basically a ring buffer of
 * size {@link #popSize}.
 * <p>
 * The amount of pheromone on an sub-sequence {@code (a, b)}
 * (called edge) is determined by how often {@code b} directly
 * follows {@code a} in the ants in the population. If the
 * sequence {@code (a, b)} does not occur in any permutation in
 * the population, the pheromone is a very small value
 * {@link #tau0} (where {@code tau0=1/(L-1)}, with
 * {@link #length} being the length of the permutations). If
 * {@code (a, b)} occurs {@code t} times, the pheromone is
 * {@code tau0 + t(tauMax-tauMin)/K}, where {@link #tauMax} is
 * the maximum pheromone.
 * <p>
 * In this model implementation, the choice of the starting node
 * is also modeled. This means that if the model is sampled, new
 * ants will start their paths more likely at good starting
 * points of the previous ants. This makes sense in problems such
 * as the Job Shop Scheduling Problem (JSSP) or in vehicle
 * routing problems with time windows, where both the choice of
 * the first node and the "direction" of tours that visit nodes
 * is important. In symmetric Traveling Salesman Problems, on the
 * other hand, you could perform a tour forward or backwards, and
 * it would not matter. In that case, the initial starting node
 * would not need to be modeled. Anyway, here we also model the
 * starting node.
 *
 * @param <X>
 *          the search space
 */
public class PACOModelAge<X> extends ACOModel<X> {
  /**
   * the fraction of edges to be chosen greedily, i.e., directly
   * based on the best pheromone-cost combination (instead of
   * randomly but proportionally on the pheromone-cost)
   */
  public final double q0;

  /** the power to be applied to the cost value */
  public final double beta;

  /** the minimal pheromone value */
  public final double tau0;
  /** the maximum pheromone that can be assigned to any edge */
  public final double tauMax;
  /** the pheromone multiplier */
  private final double mPheroMultiplier;

  /** the maximum size of the population */
  public final int popSize;

  /** the edge matrix used for pheromones */
  private final DirectedEdgeMultiSet mMatrix;
  /** the node set managing the nodes */
  protected final IntSet mNodes;

  /** the population of size of at most {@link #popSize} */
  private final int[][] mPopulation;

  /**
   * the actual size of the population: will initially be
   * {@code 0}, the increase every time an ant reaches the
   * population, until it eventually remains fixed at
   * {@link #popSize} once the population is full
   */
  private int mCurPopSize;
  /**
   * the index where the next permutation can be stored in the
   * population: The population is a ring buffer, where the
   * oldest ant is overwritten with new ants coming in.
   * {@link #mCurPopIndex} therefore increases by {@code 1} and
   * is modulo-divided by {@link #popSize} every time an ant
   * enters the population.
   */
  private int mCurPopIndex;

  /**
   * the temporary storage of the edge values, used when
   * random-proportional node choices are done
   */
  private final double[] mVs;

  /**
   * Create the PACO model.
   *
   * @param pLength
   *          the length of the permutation
   * @param pPopSize
   *          the size of the population
   * @param pQ0
   *          the fraction of edges to be chosen greedily based
   *          on the pheromone-cost combination
   * @param pBeta
   *          the power to be applied to the heuristic value
   * @param pTauMax
   *          the maximum pheromone that can be assigned to any
   *          edge
   */
  protected PACOModelAge(final int pLength, final int pPopSize,
      final double pQ0, final double pBeta,
      final double pTauMax) {
    super(pLength);

    if (pPopSize <= 0) {
      throw new IllegalArgumentException(//
          "K must be > 0, but is " + pPopSize);//$NON-NLS-1$
    }
    this.popSize = pPopSize;

    if (Double.isFinite(pQ0) && (pQ0 >= 0d) && (pQ0 <= 1d)) {
      this.q0 = pQ0;
    } else {
      throw new IllegalArgumentException(
          "q0 must be fron [0,1], but is " //$NON-NLS-1$
              + pQ0);
    }

    if (Double.isFinite(pBeta) && (pBeta >= 0d)) {
      this.beta = pBeta;
    } else {
      throw new IllegalArgumentException(
          "beta must be >= 0, but is "//$NON-NLS-1$
              + pBeta);
    }

    this.tau0 = (1d / (this.length - 1));
    if ((!Double.isFinite(this.tau0)) || (this.tau0 <= 0d)) {
      throw new IllegalArgumentException(//
          "Huh? tau0=" + this.tau0 //$NON-NLS-1$
              + " for L=" + this.length);//$NON-NLS-1$
    }

    if (Double.isFinite(pTauMax)
        && (pTauMax > (1d / (this.length - 1)))) {
      this.tauMax = pTauMax;
    } else {
      throw new IllegalArgumentException(((//
      "tauMax must be > 1/(L-1), i.e., > 1/"//$NON-NLS-1$
          + (this.length - 1)) + ", i.e., > "//$NON-NLS-1$
          + this.tau0) + ", but is "//$NON-NLS-1$
          + pTauMax);
    }

    this.mPheroMultiplier =
        (this.tauMax - this.tau0) / this.popSize;
    if ((!Double.isFinite(this.mPheroMultiplier))
        || (this.mPheroMultiplier <= 0d)) {
      throw new IllegalArgumentException(
          "Invalid pheromone multiplier " //$NON-NLS-1$
              + this.mPheroMultiplier + " resulting from tauMax=" //$NON-NLS-1$
              + this.tauMax + " and K=" //$NON-NLS-1$
              + this.popSize + " at L=" + this.length); //$NON-NLS-1$
    }

    this.mNodes = new IntSet(this.length);
    this.mMatrix =
        DirectedEdgeMultiSet.create(this.length, this.popSize);

    this.mPopulation = new int[this.popSize][this.length];
    this.mVs = new double[this.length];
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return (((((("paco_age_" + this.popSize) + '_') //$NON-NLS-1$
        + this.q0) + '_') + this.beta) + '_') + this.tauMax;
  }

  /** {@inheritDoc} */
  @Override
  public void initialize() {
    this.mMatrix.clear();
    this.mCurPopSize = 0;
    this.mCurPopIndex = 0;
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
    return this.tau0 + (this.mMatrix.getEdgeCount(a, b)
        * this.mPheroMultiplier);
  }

  /** {@inheritDoc} */
  @Override
  public final void update(final Iterable<X> selected) {
    for (final X x : selected) { // for each ant to be added
      final int[] pi = this.permutationFromX(x);
      final int size = this.mCurPopSize;
      final int index = this.mCurPopIndex;
      final int[] dest = this.mPopulation[index];
      if (size >= this.popSize) { // pop full: remove oldest
        this.mMatrix.removePermutation(dest);
      }
      System.arraycopy(pi, 0, dest, 0, this.length); // copy
      this.mMatrix.addPermutation(pi); // add edges to pheros
      this.mCurPopSize = Math.min(this.popSize, size + 1);
      this.mCurPopIndex = (index + 1) % this.popSize; // move idx
    }
  }

  /**
   * initialize the node set: This method fills all the node IDs
   * that can be appended to the permutation in the first step
   * into the set.
   * <p>
   * In this default implementation, all nodes in {@code 0..L-1}
   * are added, but you could overwrite this method to only add a
   * reachable subset.
   *
   * @param random
   *          the random number generator
   */
  protected void initNodeSet(final Random random) {
    this.mNodes.fill();
  }

  /**
   * Build one new candidate solution by simulating the behavior
   * of one ant moving through the graph.
   *
   * @param dest
   *          {@inheritDoc}
   * @param random
   *          {@inheritDoc}
   */
  @Override
  public void apply(final X dest, final Random random) {
    this.initNodeSet(random); // get all potential first nodes
    this.mNodes.shuffle(random); // shuffle them

    int i = 0;
    final int[] x = this.permutationFromX(dest);
    final double[] vs = this.mVs;
    int nodesLeft = -1;

    int bestNode = -1; // we start at virtual node -1
    while ((nodesLeft = this.mNodes.size()) > 0) {
      final int lastNode = bestNode; // previously chosen node

      if (nodesLeft <= 1) {
// Only one node can be chosen: Pick it directly
        bestNode = this.mNodes.get(0);
      } else { // multiple choices: compute costs and pheromones

// With probability q0, always choose best node directly.
        final boolean decideRandomly =
            (random.nextDouble() >= this.q0);

// Ok, calculate the pheromones and heuristic values.
// First: setup the best values.
        double vBest = Double.NEGATIVE_INFINITY;
        double vSum = 0d;

// Then: for each node which is not yet assigned...
        for (int j = 0; j < nodesLeft; j++) {
          final int curNode = this.mNodes.get(j);

// Get the cost of adding the node: Must be >= 0
          final double cost =
              this.getCostOfAppending(curNode, dest);

// Compute the value v = [pheromone^1 * (1/cost)^beta].
          final double v = (this.tau0
              + (this.mMatrix.getEdgeCount(lastNode, curNode)
                  * this.mPheroMultiplier)) // compute pheromone
              * Math.pow(cost, -this.beta); // compute cost

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
            vSum = Math.nextUp(vSum + v); // ensure increase
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
          bestNode = this.mNodes.get(j);
        } // else: No random decision: keep the best.
      } // bestNode is either only possible node or chosen node

// Visit the chosen node by adding it to the permutation.
      x[i++] = bestNode;// Store node in solution.
      this.mNodes.delete(bestNode);// bestNode done
      this.append(bestNode, dest); // potential internal update
    }
  }

  /** {@inheritDoc} */
  @Override
  public void printSetup(final Writer output)
      throws IOException {
    super.printSetup(output);
    output.write(LogFormat.mapEntry("K", this.popSize)); //$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("tau0", this.tau0)); //$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("tauMax", this.tauMax)); //$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("beta", this.beta)); //$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("q0", this.q0)); //$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("pruningStrategy", //$NON-NLS-1$
        "age")); //$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry(//
        LogFormat.classKey("edgeSet"), //$NON-NLS-1$
        ReflectionUtils.className(this.mMatrix)));
    output.write(System.lineSeparator());
  }
}

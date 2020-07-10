package aitoa.examples.jssp.aco;

import aitoa.examples.jssp.JSSPCandidateSolution;

/**
 * An individual holding a permutation and a JSSP candidate
 * solution
 */
public final class JSSPACOIndividual {

  /** the permutation */
  public final int[] permutation;

  /** the solution */
  public final JSSPCandidateSolution solution;

  /** the makespan */
  public int makespan;

  /**
   * create the individual
   *
   * @param m
   *          the number of machines
   * @param n
   *          the number of jobs
   */
  public JSSPACOIndividual(final int m, final int n) {
    super();
    this.permutation = new int[m * n];
    this.solution = new JSSPCandidateSolution(m, n);
  }
}

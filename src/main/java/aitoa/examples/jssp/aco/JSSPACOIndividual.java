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
   * @param pM
   *          the number of machines
   * @param pN
   *          the number of jobs
   */
  public JSSPACOIndividual(final int pM, final int pN) {
    super();
    this.permutation = new int[pM * pN];
    this.solution = new JSSPCandidateSolution(pM, pN);
  }
}

package aitoa.examples.jssp.aco;

import aitoa.examples.jssp.JSSPCandidateSolution;

/**
 * A record holding both a permutation and a JSSP candidate
 * solution, since we can construct them together at once in our
 * model sampling procedure.
 */
public final class JSSPACORecord {

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
  public JSSPACORecord(final int pM, final int pN) {
    super();
    this.permutation = new int[pM * pN];
    this.solution = new JSSPCandidateSolution(pM, pN);
  }
}

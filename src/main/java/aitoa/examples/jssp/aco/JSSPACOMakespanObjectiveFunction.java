package aitoa.examples.jssp.aco;

import aitoa.examples.jssp.JSSPInstance;
import aitoa.examples.jssp.JSSPMakespanObjectiveFunction;
import aitoa.structure.IObjectiveFunction;

/**
 * The makespan as objective function for a candidate solution to
 * the Job Shop Scheduling Problem, subject to minimization
 */
public final class JSSPACOMakespanObjectiveFunction
    implements IObjectiveFunction<JSSPACOIndividual> {

  /**
   * the inner makespan objective function, used only for
   * computing the bounds
   */
  private final JSSPMakespanObjectiveFunction mInner;

  /**
   * create
   *
   * @param pInstance
   *          the instance
   */
  public JSSPACOMakespanObjectiveFunction(
      final JSSPInstance pInstance) {
    super();
    this.mInner = new JSSPMakespanObjectiveFunction(pInstance);
  }

  /**
   * Get the JSSP instance
   *
   * @return the JSSP instance
   */
  public JSSPInstance getInstance() {
    return this.mInner.instance;
  }

  /**
   * create
   *
   * @param pInstance
   *          the instance
   */
  public JSSPACOMakespanObjectiveFunction(
      final String pInstance) {
    this(new JSSPInstance(pInstance));
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return this.mInner.toString();
  }

  /** {@inheritDoc} */
  @Override
  public double evaluate(final JSSPACOIndividual y) {
    return y.makespan;
  }

  /** {@inheritDoc} */
  @Override
  public double lowerBound() {
    return this.mInner.lowerBound();
  }

  /** {@inheritDoc} */
  @Override
  public double upperBound() {
    return this.mInner.upperBound();
  }
}

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
  private final JSSPMakespanObjectiveFunction m_inner;

  /**
   * create
   *
   * @param _instance
   *          the instance
   */
  public JSSPACOMakespanObjectiveFunction(
      final JSSPInstance _instance) {
    super();
    this.m_inner = new JSSPMakespanObjectiveFunction(_instance);
  }

  /**
   * Get the JSSP instance
   *
   * @return the JSSP instance
   */
  public JSSPInstance getInstance() {
    return this.m_inner.instance;
  }

  /**
   * create
   *
   * @param _instance
   *          the instance
   */
  public JSSPACOMakespanObjectiveFunction(
      final String _instance) {
    this(new JSSPInstance(_instance));
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return this.m_inner.toString();
  }

  /** {@inheritDoc} */
  @Override
  public double evaluate(final JSSPACOIndividual y) {
    return y.makespan;
  }

  /** {@inheritDoc} */
  @Override
  public double lowerBound() {
    return this.m_inner.lowerBound();
  }

  /** {@inheritDoc} */
  @Override
  public double upperBound() {
    return this.m_inner.upperBound();
  }
}

package aitoa.algorithms;

import java.io.IOException;
import java.io.Writer;

import aitoa.structure.LogFormat;
import aitoa.utils.Experiment;

/**
 * A temperature schedule is used in
 * {@linkplain aitoa.algorithms.SimulatedAnnealing simulated
 * annealing} to compute the temperature that should be used at
 * each iteration.
 */
// start main
public abstract class TemperatureSchedule {
  /** the start temperature */
  public final double startTemperature;

// end main
  /**
   * create
   *
   * @param pStartTemperature
   *          the start temperature
   */
  protected TemperatureSchedule(final double pStartTemperature) {
    super();
    if ((pStartTemperature <= 0d)
        || (!(Double.isFinite(pStartTemperature)))) {
      throw new IllegalArgumentException(
          "start temperature must be positive, but is " //$NON-NLS-1$
              + pStartTemperature);
    }
    this.startTemperature = pStartTemperature;
  }

  /**
   * Compute the temperature at the given time step tau
   *
   * @param tau
   *          the time step
   * @return the temperature
   */
// start main
  public abstract double temperature(final long tau);
// end main

  /**
   * print the setup of this class to the given output writer
   *
   * @param output
   *          the output writer
   * @throws IOException
   *           if i/o fails
   */
  public void printSetup(final Writer output)
      throws IOException {
    output.write(LogFormat.mapEntry("temperatureSchedule", //$NON-NLS-1$
        this));
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("startTemperature", ///$NON-NLS-1$
        this.startTemperature));
    output.write(System.lineSeparator());
  }

  /**
   * The exponential temperature schedule: Here, the temperature
   * at algorithm iteration index {@code tau} equals to
   * {@code Ts*(1-e)^tau}, where {@code TS} is the start
   * temperature and {@code e} is an epsilon value which should
   * be close to zero but not zero, say, 0.01.
   */
// start exponential
  public static final class Exponential
      extends TemperatureSchedule {

    /** the epsilon */
    public final double epsilon;

// end exponential
    /**
     * create
     *
     * @param pStartTemperature
     *          the start temperature
     * @param pEpsilon
     *          the epsilon
     */
    public Exponential(final double pStartTemperature,
        final double pEpsilon) {
      super(pStartTemperature);
      if ((pEpsilon <= 0d) || (pEpsilon >= 1d)
          || (!(Double.isFinite(pEpsilon)))) {
        throw new IllegalArgumentException(
            "epsilon must be in (0,1), but is "//$NON-NLS-1$
                + pEpsilon);
      }
      this.epsilon = pEpsilon;
    }

    /** {@inheritDoc} */
    @Override
// start exponential
    public double temperature(final long tau) {
      return (this.startTemperature
          * Math.pow((1d - this.epsilon), (tau - 1L)));
    }
// end exponential

    /** {@inheritDoc} */
    @Override
    public String toString() {
      return ((("exp_" + //$NON-NLS-1$
          Experiment
              .doubleToStringForName(this.startTemperature))
          + '_')//
          + Experiment.doubleToStringForName(this.epsilon));
    }

    /** {@inheritDoc} */
    @Override
    public void printSetup(final Writer output)
        throws IOException {
      super.printSetup(output);
      output.write(LogFormat.mapEntry("epsilon", ///$NON-NLS-1$
          this.epsilon));
      output.write(System.lineSeparator());
    }
// start exponential
  }
// end exponential

  /**
   * The logarithmic temperature schedule: Here, the temperature
   * equals {@code Ts/log(tau+1)}, where {@code Ts} is the start
   * temperature and {@code tau} is the algorithm iteration
   * index.
   */
// start logarithmic
  public static final class Logarithmic
      extends TemperatureSchedule {

    /** the epsilon */
    public final double epsilon;
// end logarithmic

    /**
     * create
     *
     * @param pStartTemperature
     *          the start temperature
     * @param pEpsilon
     *          the epsilon
     */
    public Logarithmic(final double pStartTemperature,
        final double pEpsilon) {
      super(pStartTemperature);
      if ((pEpsilon <= 0d) || (!(Double.isFinite(pEpsilon)))) {
        throw new IllegalArgumentException(
            "epsilon must be in greater than 0, but is "//$NON-NLS-1$
                + pEpsilon);
      }
      this.epsilon = pEpsilon;
    }

    /** {@inheritDoc} */
    @Override
// start logarithmic
    public double temperature(final long tau) {
      if (tau >= Long.MAX_VALUE) {
        return 0d;
      }
      return (this.startTemperature
          / Math.log(((tau - 1L) * this.epsilon) + Math.E));
    }
// end logarithmic

    /** {@inheritDoc} */
    @Override
    public String toString() {
      return ((("log_" + //$NON-NLS-1$
          Experiment
              .doubleToStringForName(this.startTemperature))
          + '_')
          + Experiment.doubleToStringForName(this.epsilon));
    }

    /** {@inheritDoc} */
    @Override
    public void printSetup(final Writer output)
        throws IOException {
      super.printSetup(output);
      output.write(LogFormat.mapEntry("epsilon", ///$NON-NLS-1$
          this.epsilon));
      output.write(System.lineSeparator());
    }
// start logarithmic
  }
// end logarithmic

// start main
}
// end main

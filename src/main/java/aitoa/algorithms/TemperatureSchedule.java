package aitoa.algorithms;

import java.io.BufferedWriter;
import java.io.IOException;

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
   * @param _startTemperature
   *          the start temperature
   */
  protected TemperatureSchedule(final double _startTemperature) {
    super();
    if ((_startTemperature <= 0d)
        || (!(Double.isFinite(_startTemperature)))) {
      throw new IllegalArgumentException(
          "start temperature must be positive, but is " //$NON-NLS-1$
              + _startTemperature);
    }
    this.startTemperature = _startTemperature;
  }

  /**
   * Compute the temperature at the given time step t
   *
   * @param t
   *          the time step
   * @return the temperature
   */
// start main
  public abstract double temperature(final long t);
// end main

  /**
   * print the setup of this class to the given output writer
   *
   * @param output
   *          the output writer
   * @throws IOException
   *           if i/o fails
   */
  public void printSetup(final BufferedWriter output)
      throws IOException {
    output.write("temperatureSchedule: "); //$NON-NLS-1$
    output.write(this.toString());
    output.newLine();
    output.write("temperatureScheduleClass: "); //$NON-NLS-1$
    output.write(this.getClass().getCanonicalName());
    output.newLine();
    output.write("startTemperature: ");//$NON-NLS-1$
    output.write(Double.toString(this.startTemperature));
    output.newLine();
    output.write("startTemperature(inhex): ");//$NON-NLS-1$
    output.write(Double.toHexString(this.startTemperature));
    output.newLine();
  }

  /**
   * The exponential temperature schedule: Here, the temperature
   * at algorithm iteration index {@code t} equals to
   * {@code Ts*(1-e)^t}, where {@code TS} is the start
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
     * @param _startTemperature
     *          the start temperature
     * @param _epsilon
     *          the epsilon
     */
    public Exponential(final double _startTemperature,
        final double _epsilon) {
      super(_startTemperature);
      if ((_epsilon <= 0d) || (_epsilon >= 1d)
          || (!(Double.isFinite(_epsilon)))) {
        throw new IllegalArgumentException(
            "epsilon must be in (0,1), but is "//$NON-NLS-1$
                + _epsilon);
      }
      this.epsilon = _epsilon;
    }

    /** {@inheritDoc} */
    @Override
// start exponential
    public double temperature(final long t) {
      return (this.startTemperature
          * Math.pow((1d - this.epsilon), t));
    }
// end exponential

    /** {@inheritDoc} */
    @Override
    public final String toString() {
      return ((("exp_" + //$NON-NLS-1$
          this.startTemperature) + '_')//
          + this.epsilon);
    }

    /** {@inheritDoc} */
    @Override
    public final void printSetup(final BufferedWriter output)
        throws IOException {
      super.printSetup(output);
      output.write("epsilon: ");//$NON-NLS-1$
      output.write(Double.toString(this.epsilon));
      output.newLine();
      output.write("epsilon(inhex): ");//$NON-NLS-1$
      output.write(Double.toHexString(this.epsilon));
      output.newLine();
    }
// start exponential
  }
// end exponential

  /**
   * The logarithmic temperature schedule: Here, the temperature
   * equals {@code Ts/log(t+1)}, where {@code Ts} is the start
   * temperature and {@code t} is the algorithm iteration index.
   */
// start logarithmic
  public static final class Logarithmic
      extends TemperatureSchedule {
// end logarithmic
    
    /**
     * create
     *
     * @param _startTemperature
     *          the start temperature
     */
    public Logarithmic(final double _startTemperature) {
      super(_startTemperature);
    }

    /** {@inheritDoc} */
    @Override
// start logarithmic
    public double temperature(final long t) {
      if (t >= Long.MAX_VALUE) {
        return 0d;
      }
      return (this.startTemperature / Math.log(t + 1L));
    }    
// end logarithmic
    
    /** {@inheritDoc} */
    @Override
    public final String toString() {
      return "log"; //$NON-NLS-1$
    }
// start logarithmic
  }
// end logarithmic
  
// start main
}
// end main

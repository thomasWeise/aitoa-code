package aitoa.structure;

/**
 * The termination criterion tells the optimization process when
 * to stop.
 */
@FunctionalInterface
public interface ITerminationCriterion {
  /**
   * If this function becomes {@code true}, the optimization
   * process should stop.
   *
   * @return {@code true} if the optimization process should stop
   *         now, {@code false} if it can continue
   */
  public abstract boolean shouldTerminate();
}

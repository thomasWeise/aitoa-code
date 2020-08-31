package aitoa.structure;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

/**
 * A base class implementing the {@link IMetaheuristic} interface
 * and storing a {@linkplain INullarySearchOperator nullary
 * search operator}.
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
public abstract class Metaheuristic0<X, Y>
    implements IMetaheuristic<X, Y> {

  /** the nullary search operator */
  public final INullarySearchOperator<X> nullary;

  /**
   * Create the metaheuristic
   *
   * @param pNullary
   *          the nullary search operator.
   */
  protected Metaheuristic0(
      final INullarySearchOperator<X> pNullary) {
    super();
    this.nullary = Objects.requireNonNull(pNullary);
  }

  /** {@inheritDoc} */
  @Override
  public void printSetup(final Writer output)
      throws IOException {
    IMetaheuristic.super.printSetup(output);
    output.write(LogFormat.mapEntry(LogFormat.SETUP_NULLARY_OP,
        this.nullary));
    output.write(System.lineSeparator());
    this.nullary.printSetup(output);
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return this.getClass().getSimpleName();
  }
}

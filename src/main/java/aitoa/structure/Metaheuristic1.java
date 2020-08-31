package aitoa.structure;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

/**
 * A base class implementing the {@link IMetaheuristic} interface
 * and storing a {@linkplain INullarySearchOperator nullary
 * search operator} and a {@linkplain IUnarySearchOperator unary
 * search operator}.
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
public abstract class Metaheuristic1<X, Y>
    extends Metaheuristic0<X, Y> {

  /** the unary search operator */
  public final IUnarySearchOperator<X> unary;

  /**
   * Create the metaheuristic
   *
   * @param pNullary
   *          the nullary search operator.
   * @param pUnary
   *          the unary search operator
   */
  protected Metaheuristic1(
      final INullarySearchOperator<X> pNullary,
      final IUnarySearchOperator<X> pUnary) {
    super(pNullary);
    this.unary = Objects.requireNonNull(pUnary);
  }

  /** {@inheritDoc} */
  @Override
  public void printSetup(final Writer output)
      throws IOException {
    super.printSetup(output);
    output.write(LogFormat.mapEntry(LogFormat.SETUP_UNARY_OP,
        this.unary));
    output.write(System.lineSeparator());
    if (this.unary != this.nullary) {
      this.unary.printSetup(output);
    }
  }
}

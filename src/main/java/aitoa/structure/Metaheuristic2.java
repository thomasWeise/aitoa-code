package aitoa.structure;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

/**
 * A base class implementing the {@link IMetaheuristic} interface
 * and storing a {@linkplain INullarySearchOperator nullary
 * search operator}, a {@linkplain IUnarySearchOperator unary
 * search operator}, and a {@linkplain IBinarySearchOperator
 * binary search operator}.
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
public abstract class Metaheuristic2<X, Y>
    extends Metaheuristic1<X, Y> {

  /** the binary search operator */
  public final IBinarySearchOperator<X> binary;

  /**
   * Create the metaheuristic
   *
   * @param pNullary
   *          the nullary search operator.
   * @param pUnary
   *          the unary search operator
   * @param pBinary
   *          the binary search operator
   */
  protected Metaheuristic2(
      final INullarySearchOperator<X> pNullary,
      final IUnarySearchOperator<X> pUnary,
      final IBinarySearchOperator<X> pBinary) {
    super(pNullary, pUnary);
    this.binary = Objects.requireNonNull(pBinary);
  }

  /** {@inheritDoc} */
  @Override
  public void printSetup(final Writer output)
      throws IOException {
    super.printSetup(output);
    output.write(LogFormat.mapEntry(LogFormat.SETUP_BINARY_OP,
        this.binary));
    output.write(System.lineSeparator());
    if ((this.binary != this.unary)
        && (this.binary != this.nullary)) {
      this.binary.printSetup(output);
    }
  }
}

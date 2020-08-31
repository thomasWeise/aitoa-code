package aitoa.utils.logs;

import aitoa.algorithms.EA1p1;
import aitoa.examples.bitstrings.BitStringObjectiveFunction;
import aitoa.examples.bitstrings.OneMaxObjectiveFunction;
import aitoa.searchSpaces.bitstrings.BitStringNullaryOperator;
import aitoa.searchSpaces.bitstrings.BitStringSpace;
import aitoa.searchSpaces.bitstrings.BitStringUnaryOperatorMOverNFlip;
import aitoa.structure.BlackBoxProcessBuilder;
import aitoa.structure.IMetaheuristic;

/** the example problem and configuration */
final class Example {

  /**
   * create the example problem
   *
   * @return the builder
   */
  static BlackBoxProcessBuilder<boolean[], boolean[]> problem() {
    final BlackBoxProcessBuilder<boolean[], boolean[]> builder =
        new BlackBoxProcessBuilder<>();

    final BitStringObjectiveFunction problem =
        new OneMaxObjectiveFunction(11);

    final BitStringSpace searchSpace = problem.createSpace();

    builder.setSearchSpace(searchSpace);
    builder.setObjectiveFunction(problem);
    builder.setGoalF(0d);
    return builder;
  }

  /**
   * create the example algorithm
   *
   * @return the example algorithm
   */
  static IMetaheuristic<boolean[], boolean[]> algorithm() {
    return new EA1p1<>(new BitStringNullaryOperator(),
        new BitStringUnaryOperatorMOverNFlip(1));
  }

  /** forbidden */
  private Example() {
    throw new UnsupportedOperationException();
  }
}

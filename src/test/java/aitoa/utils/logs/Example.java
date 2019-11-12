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
  static final BlackBoxProcessBuilder<boolean[], boolean[]>
      problem() {
    final BlackBoxProcessBuilder<boolean[], boolean[]> builder =
        new BlackBoxProcessBuilder<>();

    final BitStringObjectiveFunction problem =
        new OneMaxObjectiveFunction(11);

    final BitStringSpace searchSpace = problem.createSpace();
    final BitStringUnaryOperatorMOverNFlip unary =
        new BitStringUnaryOperatorMOverNFlip(1);
    final BitStringNullaryOperator nullary =
        new BitStringNullaryOperator();

    builder.setSearchSpace(searchSpace);
    builder.setObjectiveFunction(problem);
    builder.setNullarySearchOperator(nullary);
    builder.setUnarySearchOperator(unary);
    builder.setGoalF(0d);
    return builder;
  }

  /**
   * create the example algorithm
   *
   * @return the example algorithm
   */
  static final IMetaheuristic<boolean[], boolean[]> algorithm() {
    return new EA1p1<>();
  }
}

package aitoa.examples.jssp;

import java.util.stream.Stream;

import aitoa.structure.IMetaheuristic;
import aitoa.utils.Experiment.IExperimentStage;
import aitoa.utils.TestExperimentStages;

/** Test the {@link EJSSPExperimentStage} */
public class TestEJSSPExperimentStage extends
    TestExperimentStages<int[], JSSPCandidateSolution,
        JSSPMakespanObjectiveFunction,
        IMetaheuristic<int[], JSSPCandidateSolution>,
        IExperimentStage<int[], JSSPCandidateSolution,
            JSSPMakespanObjectiveFunction,
            IMetaheuristic<int[], JSSPCandidateSolution>>> {

  /** create */
  public TestEJSSPExperimentStage() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected
      Stream<IExperimentStage<int[], JSSPCandidateSolution,
          JSSPMakespanObjectiveFunction,
          IMetaheuristic<int[], JSSPCandidateSolution>>>
      getInstance() {
    return Stream.of(EJSSPExperimentStage.values());
  }
}

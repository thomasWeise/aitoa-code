package aitoa.examples.jssp;

import java.util.stream.Stream;

import aitoa.examples.jssp.aco.JSSPACOMakespanObjectiveFunction;
import aitoa.examples.jssp.aco.JSSPACORecord;
import aitoa.structure.IMetaheuristic;
import aitoa.utils.Experiment.IExperimentStage;
import aitoa.utils.TestExperimentStages;

/** Test the {@link EJSSPExperimentStageACO} */
public class TestEJSSPExperimentStageACO extends
    TestExperimentStages<JSSPACORecord, JSSPACORecord,
        JSSPACOMakespanObjectiveFunction,
        IMetaheuristic<JSSPACORecord, JSSPACORecord>,
        IExperimentStage<JSSPACORecord, JSSPACORecord,
            JSSPACOMakespanObjectiveFunction,
            IMetaheuristic<JSSPACORecord, JSSPACORecord>>> {

  /** create */
  public TestEJSSPExperimentStageACO() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected
      Stream<IExperimentStage<JSSPACORecord, JSSPACORecord,
          JSSPACOMakespanObjectiveFunction,
          IMetaheuristic<JSSPACORecord, JSSPACORecord>>>
      getInstance() {
    return Stream.of(EJSSPExperimentStageACO.values());
  }
}

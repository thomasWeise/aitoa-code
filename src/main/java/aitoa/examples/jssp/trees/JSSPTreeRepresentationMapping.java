package aitoa.examples.jssp.trees;

import java.util.Arrays;
import java.util.Random;

import aitoa.examples.jssp.JSSPCandidateSolution;
import aitoa.examples.jssp.JSSPInstance;
import aitoa.searchSpaces.trees.Node;
import aitoa.searchSpaces.trees.math.MathFunction;
import aitoa.structure.IRepresentationMapping;

/**
 * The representation mapping for scheduling formulas to Gantt
 * charts. The idea is that the input of this mapping is a
 * formula which is used to step-by-step construct a Gantt chart.
 * In each step, the formula receives information about the
 * current construction state of the Gantt chart. It then
 * computes one rating value for each job that may be scheduled.
 * The job with the smallest rating value is then scheduled. The
 * Gantt chart is updated. This is repeated again and again,
 * until the Gantt chart has been filled and no job is left that
 * might be scheduled. If more than one job receive the smallest
 * rating, we randomly choose one of them.
 */
// start relevant
public final class JSSPTreeRepresentationMapping implements
    IRepresentationMapping<Node[], JSSPCandidateSolution> {
// end relevant

  /** the job id */
  static final int JOB_ID = 0;
  /** the next sub-job of the job */
  static final int JOB_COMPLETED_SUBJOBS =
      (JSSPTreeRepresentationMapping.JOB_ID + 1);
  /** the index of the working time of the next sub job */
  static final int JOB_NEXT_SUBJOB_WORK_TIME =
      JSSPTreeRepresentationMapping.JOB_COMPLETED_SUBJOBS + 1;
  /**
   * the time index when the last sub-job of the current job was
   * finished
   */
  static final int JOB_LAST_SUBJOB_FINISHED_TIME =
      JSSPTreeRepresentationMapping.JOB_NEXT_SUBJOB_WORK_TIME
          + 1;
  /** the work time already invested into the job */
  static final int JOB_FINISHED_WORKTIME =
      JSSPTreeRepresentationMapping.JOB_LAST_SUBJOB_FINISHED_TIME
          + 1;
  /** the total work time that needs to be invested in the job */
  static final int JOB_TOTAL_WORKTIME =
      JSSPTreeRepresentationMapping.JOB_FINISHED_WORKTIME + 1;
  /**
   * the machine id
   */
  static final int MACHINE_ID =
      JSSPTreeRepresentationMapping.JOB_TOTAL_WORKTIME + 1;
  /**
   * the time index when the last sub-job was finished on the
   * current machine
   */
  static final int MACHINE_LAST_SUBJOB_FINISHED_TIME =
      JSSPTreeRepresentationMapping.MACHINE_ID + 1;
  /** the number of sub-jobs a machine has completed */
  static final int MACHINE_COMPLETED_SUBJOBS =
      JSSPTreeRepresentationMapping.MACHINE_LAST_SUBJOB_FINISHED_TIME
          + 1;
  /** the work time already performed by the machine */
  static final int MACHINE_FINISHED_WORKTIME =
      JSSPTreeRepresentationMapping.MACHINE_COMPLETED_SUBJOBS
          + 1;
  /**
   * the total work time that needs to be spent on this machine
   */
  static final int MACHINE_TOTAL_WORKTIME =
      JSSPTreeRepresentationMapping.MACHINE_FINISHED_WORKTIME
          + 1;

  /** the values dimensions */
  static final int DIM_VALUES =
      JSSPTreeRepresentationMapping.MACHINE_TOTAL_WORKTIME + 1;

  /** the state used by the mathematical functions */
  final double[] mState;

  /** the jobs data from the instance */
  final int[][] mJobs;
  /** the total work time required by the job */
  final int[] mJobTotalTime;
  /** the total work time required by the machine */
  final int[] mMachineTotalTime;

  /** the job ids */
  final int[] mJobIDs;
  /** the best jobs ids */
  final int[] mBestJobIDs;
  /** the best jobs indexes */
  final int[] mBestJobIndexes;
  /** the steo if the job */
  final int[] mJobCompletedSubjobs;
  /** the next job machine */
  final int[] mJobNextMachine;
  /** the last time a sub-job of this job was finished */
  final int[] mJobLastSubjobFinishedTime;
  /** the the work time already performed by the job */
  final int[] mJobFinishedWorkTime;

  /** the time the machine has spent on processing jobs */
  final int[] mMachineFinishedWorkTime;
  /** the index of the machine into the solution */
  final int[] mMachineLastSubjobFinishedTime;
  /** the number of subjobs processed by the machine */
  final int[] mMachineCompletedSubjobs;

  /**
   * create the representation
   *
   * @param pInstance
   *          the problem instance
   */
  public JSSPTreeRepresentationMapping(
      final JSSPInstance pInstance) {
    super();

    this.mJobs = pInstance.jobs;
    this.mState =
        new double[JSSPTreeRepresentationMapping.DIM_VALUES];

    this.mJobTotalTime = new int[pInstance.n];
    this.mMachineTotalTime = new int[pInstance.m];
    for (int i = pInstance.n; (--i) >= 0;) {
      final int[] job = this.mJobs[i];
      for (int k = 0; k < pInstance.m;) {
        final int machine = job[k++];
        final int time = job[k++];
        this.mJobTotalTime[i] += time;
        this.mMachineTotalTime[machine] += time;
      }
    }

    this.mJobIDs = new int[pInstance.n];
    this.mBestJobIDs = new int[pInstance.n];
    this.mBestJobIndexes = new int[pInstance.n];
    this.mJobCompletedSubjobs = new int[pInstance.n];
    this.mJobNextMachine = new int[pInstance.n];
    this.mJobLastSubjobFinishedTime = new int[pInstance.n];
    this.mJobFinishedWorkTime = new int[pInstance.n];

    this.mMachineFinishedWorkTime = new int[pInstance.m];
    this.mMachineLastSubjobFinishedTime = new int[pInstance.m];
    this.mMachineCompletedSubjobs = new int[pInstance.m];
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return this.getClass().getCanonicalName();
  }

// start relevant
  /**
   * Map a point {@code x} from the search space (here a formula
   * describing how job should be prioritized based on the
   * current scheduling state) to a candidate solution {@code y}
   * in the solution space (here a Gantt chart).
   *
   * @param random
   *          a random number generator
   * @param x
   *          the point in the search space
   * @param y
   *          the solution record, i.e., the Gantt chart
   */
  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void map(final Random random, final Node[] x,
      final JSSPCandidateSolution y) {
    final MathFunction<double[]> func = ((MathFunction) (x[0]));

    final int n = this.mJobIDs.length;
    final int m = this.mMachineLastSubjobFinishedTime.length;

    for (int i = n; (--i) >= 0;) {
      this.mJobIDs[i] = i;
    }
    Arrays.fill(this.mJobCompletedSubjobs, 0);
    Arrays.fill(this.mJobNextMachine, 0);
    Arrays.fill(this.mJobLastSubjobFinishedTime, 0);
    Arrays.fill(this.mJobFinishedWorkTime, 0);
    Arrays.fill(this.mMachineFinishedWorkTime, 0);
    Arrays.fill(this.mMachineLastSubjobFinishedTime, 0);
    Arrays.fill(this.mMachineCompletedSubjobs, 0);
    final double[] state = this.mState;

    for (int count = n; count > 0;) {
// now we have computed all necessary statistics, so we can
// actually apply the formula and select the job to schedule next
      double bestScore = Double.POSITIVE_INFINITY;
      int bestCount = 0;

      if (count > 1) {
        // now we need to find the job index to execute
        for (int i = count; (--i) >= 0;) {
          final int job = this.mJobIDs[i];
          final int next = this.mJobCompletedSubjobs[job];
          final int machine = this.mJobs[job][next << 1];
          final int time = this.mJobs[job][1 + (next << 1)];

          state[JSSPTreeRepresentationMapping.JOB_ID] = job;
          state[JSSPTreeRepresentationMapping.JOB_COMPLETED_SUBJOBS] =
              next;
          state[JSSPTreeRepresentationMapping.JOB_NEXT_SUBJOB_WORK_TIME] =
              time;
          state[JSSPTreeRepresentationMapping.JOB_LAST_SUBJOB_FINISHED_TIME] =
              this.mJobLastSubjobFinishedTime[job];
          state[JSSPTreeRepresentationMapping.JOB_FINISHED_WORKTIME] =
              this.mJobFinishedWorkTime[job];
          state[JSSPTreeRepresentationMapping.JOB_TOTAL_WORKTIME] =
              this.mJobTotalTime[job];
          state[JSSPTreeRepresentationMapping.MACHINE_ID] =
              machine;
          state[JSSPTreeRepresentationMapping.MACHINE_LAST_SUBJOB_FINISHED_TIME] =
              this.mMachineLastSubjobFinishedTime[machine];
          state[JSSPTreeRepresentationMapping.MACHINE_COMPLETED_SUBJOBS] =
              this.mMachineCompletedSubjobs[machine];
          state[JSSPTreeRepresentationMapping.MACHINE_FINISHED_WORKTIME] =
              this.mMachineFinishedWorkTime[machine];
          state[JSSPTreeRepresentationMapping.MACHINE_TOTAL_WORKTIME] =
              this.mMachineTotalTime[machine];

          // compute the score for the job
          final double score = func.applyAsDouble(this.mState);
          if (score < bestScore) {
            bestCount = 1;
            this.mBestJobIDs[0] = job;
            this.mBestJobIndexes[0] = i;
            bestScore = score;
          } else {
            if (score == bestScore) {
              this.mBestJobIDs[bestCount] = job;
              this.mBestJobIndexes[bestCount] = i;
              ++bestCount;
            }
          }
        }
      }

      if (bestCount == 0) {
        this.mBestJobIndexes[0] = random.nextInt(count);
        this.mBestJobIDs[0] =
            this.mJobIDs[this.mBestJobIndexes[0]];
      } else {
        bestCount = random.nextInt(bestCount);
      }

      // ok, we have selected a job to execute, now we need to
      // execute it and update the data
      final int bestJob = this.mBestJobIDs[bestCount];
      int next = this.mJobCompletedSubjobs[bestJob];
      final int machine = this.mJobs[bestJob][next << 1];
      final int time = this.mJobs[bestJob][1 + (next << 1)];

      this.mJobCompletedSubjobs[bestJob] = (++next);
      if (next >= m) {
        final int bestJobIndex = this.mBestJobIndexes[bestCount];
        this.mJobIDs[bestJobIndex] = this.mJobIDs[--count];
        this.mJobIDs[count] = -1;
      }

      final int beginTime =
          Math.max(this.mJobLastSubjobFinishedTime[bestJob],
              this.mMachineLastSubjobFinishedTime[machine]);
      final int endTime = beginTime + time;
      this.mMachineLastSubjobFinishedTime[machine] = endTime;
      this.mJobLastSubjobFinishedTime[bestJob] = endTime;
      this.mJobFinishedWorkTime[bestJob] += time;
      int machineNext = this.mMachineCompletedSubjobs[machine]++;
      this.mMachineFinishedWorkTime[machine] += time;

      // store everything in the schedule
      machineNext *= 3;
      y.schedule[machine][machineNext++] = bestJob;
      y.schedule[machine][machineNext++] = beginTime;
      y.schedule[machine][machineNext++] = endTime;
    }
  }
}
// end relevant

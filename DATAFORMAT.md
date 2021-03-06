# 1. Experiment Structure and Execution

Running experiments with `aitoa-code` means to apply algorithm setups (which we will refer to as "algorithms") to problem instances.
For each algorithm-instance pair, a pre-determined fixed number of independent runs is performed.
A convenient API for experiment execution is provided by the class [Experiment](src/main/java/aitoa/utils/Experiment.java).

## 1.1. Key Ideas behind Storage and Data Structure
The key ideas behind the structure are as follows:

1. Every single independent run of an algorithm on a problem goes into one single log file.
2. The log file stores all information needed to fully understand what was done, including the algorithm setup information, the problem instance information, the termination criteria, the progress the algorithm made over time, the final solution, the random seed used, and the static information about the system on which the algorithm was executed.
3. Each log file has a unique name derived from the unique ID of the algorithm setup, the problem instance ID, and the random seed.
   These three pieces of information must be sufficient to replicate the run.

## 1.2. Replicable Distributed Experiment Execution

Another important point of our experiment framework is that *random seeds are generated by a random generator number seeded with the problem instance name*.
In other words, given a problem instance name, it is possible to generate, in a deterministic and reproducible fashion, random seeds for the random number generators used in experimental runs.

Together with point&nbsp;3 above, this allows for a simple method to parallelize and even distribute the runs.
A thread in the experiment engine will, in a random order, instantiate algorithm setups, problem instances, and random seeds.
From such a tuple, it creates the unique file name that would correspond to the run if it was executed.
Then, it attempts to create the file.
Since file creation is an atomic operation of the file system, regardless how many threads attempt it at the same time, only one will succeed to create the file.
If the file already exists, all threads fail.
If the thread failed to create the file, it will directly move on to the next algorithm-instance-seed pair.
If it succeeded, it will execute the run.

All log information will be stored in memory and only written to the file after the run completed successfully.
If the experiment crashes for some reason, all we have to do is to delete all files of size zero.
Then the experiment can be started again and will perform exactly and only the runs that were not performed or crashed.

Interestingly, this also works with distributed experiments.
All what is necessary is a shared folder in the network.
Since the file creation is atomic on the host computer of the shared folder, it is also atomic for the shared folder.
In other words:
We can run an experiment using arbitrarily many computers which execute the runs in parallel, as long as their output folder is the same shared folder.
Since the algorithm-instance pairs are defined by the experiment setup and the random seeds are generated deterministically from the instance names, all parallel executions will perform the same experiment.

# 2. Result Folder Structure

The folder structure looks as follows is composed of three folder levels under the root folder (usually named `results`).
There will be one folder for each algorithm setup (here referred to as `algorithm`).
Inside, there will be one folder for each problem instance.
In these folders, there are log files for the runs of the algorithm on the instance.
Each run goes into one log file whose name is composed of the algorithm name, instance name, and the random seed used for the run.
In other words: Each run of each algorithm/instance combination is uniquely identified by a log file.

```
results
  |- algorithm1
  |      |- instance1
  |      |      |- algorithm1_instance1_randomseedA.txt
  |      |      |- algorithm1_instance1_randomseedB.txt
  |      |      |- ....
  |      |- instance2
  |      |      |- algorithm1_instance2_randomseedC.txt
  |      |      |- algorithm1_instance2_randomseedD.txt
  |      |      |- ....
  |      |- instance3
  |      |      |- algorithm1_instance3_randomseedE.txt
  |      |      |- algorithm1_instance3_randomseedF.txt
  |      |      |- ....
  |      |- ...
  |
  |- algorithm2
  |      |- instance1
  |      |      |- algorithm2_instance1_randomseedA.txt
  |      |      |- algorithm2_instance1_randomseedB.txt
  |      |      |- ....
  |      |- instance2
  |      |      |- algorithm2_instance2_randomseedC.txt
  |      |      |- algorithm2_instance2_randomseedD.txt
  |      |      |- ....
  |      |- instance3
  |      |      |- algorithm2_instance3_randomseedE.txt
  |      |      |- algorithm2_instance3_randomseedF.txt
  |      |      |- ....
  |      |- ...
  |
  |- algorithm3
  |      |- instance1
  |      |      |- algorithm3_instance1_randomseedA.txt
  |      |      |- algorithm3_instance1_randomseedB.txt
  |      |      |- ....
  |      |- instance2
  |      |      |- algorithm3_instance2_randomseedC.txt
  |      |      |- algorithm3_instance2_randomseedD.txt
  |      |      |- ....
  |      |- instance3
  |      |      |- algorithm3_instance3_randomseedE.txt
  |      |      |- algorithm3_instance3_randomseedF.txt
  |      |      |- ....
  |      |- ...
  |
  ...
```

# 3. Log File Structure

Below, you can find an example for a log generated for one run of the aitoa experimenter.
Each log file holds the complete information needed to replicate the run.
The log file consists of sections, which each are started with the a fixed string of either the form `# BEGIN_SECTION_NAME` or `# SECTION_NAME` (yes, I was not fully consistent here) and end with a string of the form `# END_SECTION_NAME`.

## 3.1. Example for a Log File 

```
# ALGORITHM_SETUP
# base_algorithm: sa
# algorithm: sa_exp_20_0.0000008
# algorithm(class): aitoa.algorithms.SimulatedAnnealing
# temperatureSchedule: exp_20_0.0000008
# temperatureSchedule(class): aitoa.algorithms.TemperatureSchedule.Exponential
# startTemperature: 20
# startTemperature(inhex): 0x1.4p4
# epsilon: 8.0E-7
# epsilon(inhex): 0x1.ad7f29abcaf48p-21
# nullaryOperator: uniform
# nullaryOperator(class): aitoa.examples.jssp.JSSPNullaryOperator
# unaryOperator: 1swap
# unaryOperator(class): aitoa.examples.jssp.JSSPUnaryOperator1Swap
# END_ALGORITHM_SETUP
# BEGIN_LOG
# fbest;consumedFEs;consumedTimeMS
1736;1;0
1677;8;0
1665;10;0
1525;12;0
...
950;27288112;74581
# END_OF_LOG
# BEGIN_SETUP
# SEARCH_SPACE: jssp:int[150]:la24:aitoa.examples.jssp.JSSPSearchSpace
# SEARCH_SPACE(class): aitoa.examples.jssp.JSSPSearchSpace
# SOLUTION_SPACE: jssp:gantt:la24:aitoa.examples.jssp.JSSPSolutionSpace
# SOLUTION_SPACE(class): aitoa.examples.jssp.JSSPSolutionSpace
# REPRESENTATION_MAPPING: jssp:int[]-to-Gantt:aitoa.examples.jssp.JSSPRepresentationMapping
# REPRESENTATION_MAPPING(class): aitoa.examples.jssp.JSSPRepresentationMapping
# OBJECTIVE_FUNCTION: la24
# OBJECTIVE_FUNCTION(class): aitoa.examples.jssp.JSSPMakespanObjectiveFunction
# MAX_FES: 9223372036854775807
# MAX_TIME: 180000
# GOAL_F: -Infinity
# RANDOM_SEED: 0x1a369a5e836c08bf
# END_SETUP
# BEGIN_SYSTEM
# COMPUTER_MANUFACTURER: Dell Inc.
# COMPUTER_MODEL: Vostro 3480
# CPU_FREQUENCY_HZ: 1800000000
# CPU_IDENTIFIER: Intel64 Family 6 Model 142 Stepping 11
# CPU_IS_64_BIT: true
# CPU_LOGICAL_CORES: 8
# CPU_NAME: Intel(R) Core(TM) i7-8565U CPU @ 1.80GHz
# CPU_PHYSICAL_CORES: 4
# GPU_NAME: Advanced Micro Devices, Inc. [AMD/ATI] Jet PRO [Radeon R5 M230 / R7 M260DX / Radeon 520 Mobile]
# JAVA_VERSION: 13
# JAVA_VM_NAME: OpenJDK 64-Bit Server VM
# JAVA_VM_VERSION: 13+33-Ubuntu-1
# MAINBOARD_MANUFACTURER: Dell Inc.
# MAINBOARD_MODEL: 0KTK89
# MAINBOARD_VERSION: A00
# MEMORY_PAGE_SIZE: 4096
# MEMORY_TOTAL_BYTES: 16662446080
# OS_BITS: 64
# OS_BUILD: 5.3.0-29-generic
# OS_CODENAME: Eoan Ermine
# OS_FAMILY: Ubuntu
# OS_VERSION: 19.10
# SESSION_START_DATE_TIME: 2020-02-16T13:06:23.806423Z
# version.aitoa: 0.8.29
# version.aitoa.oshi: 4.3.0
...
# END_SYSTEM
# BEGIN_STATE
# CONSUMED_FES: 65834170
# LAST_IMPROVEMENT_FE: 27288112
# CONSUMED_TIME: 180001
# LAST_IMPROVEMENT_TIME: 74581
# BEST_F: 950
# END_STATE
# BEST_X
...
# END_BEST_X
# BEST_Y
...
# END_BEST_Y
```

## 3.2. Description of the Log File Sections

The following sections are included:

- The section `ALGORITHM_SETUP` stores information about all parameters and operators used in the algorithm.
  This can include parameters such as population sizes or temperature schedules, but also the nullary and unary search operators.
  It holds key-value pairs of the form `# KEY: VALUE`.
  The only pre-defined key that always must be stored is `algorithm` which should be a unique identifier of the algorithm setup within the context of the overarching experiment.
  From this identifier, the first log file name part and a base directory name are derived (by stripping whitespace and replacing `.` with `d`).
- The section `LOG` contains a semicolon-separated-values list of log points.
  Each log point holds three values:
  + `fbest`: the best-so-far objective value
  + `consumedFEs`: the number of objective function evaluations performed since the beginning of the run
  + `consumedTimeMS`: the number of milliseconds elapsed since the beginning of the run
  In the default setup, one log point is stored for each improving move.
- The section `SETUP` contains the black-box setup of the optimization process.
  This is the static setup from the perspective of the building blocks handed to a metaheuristic and the termination criteria.
  It holds key-value pairs of the form `# KEY: VALUE` with the following information:
  + `SEARCH_SPACE`: a short name of the search space.
    The search space is the representation used only internally in the algorithm and may be different from the data structure fed to the objective function.
    The optimization algorithm can access the operators provided by the search space (such as allocation and copying of points). 
  + `SEARCH_SPACE(class)`: the canonical class name of the Java class implementing the search space
  + `SOLUTION_SPACE`: a short name of the solution space.
    The solution space represents the type of data structure fed to the objective function. 
    The optimization algorithm will not receive any information about this.
  + `SOLUTION_SPACE(class)`: the canonical class name of the Java class implementing the solution space
  + `REPRESENTATION_MAPPING`: a short name of a mapping function between search and solution space, or `null` if search and solution space are the same.
    The representation mapping is also hidden from the optimization algorithm and internally performed by the experimentation environment.
  + `REPRESENTATION_MAPPING(class)`: the canonical class name of the Java class implementing the representation, omitted if no mapping is used
  + `OBJECTIVE_FUNCTION`: a short name describing the objective function and/or problem instance.
     From this identifier, the second log file name part and a second-level directory name are derived (by stripping whitespace and replacing `.` with `d`).
  + `OBJECTIVE_FUNCTION(class)`: the canonical class name implementing the objective function.
    Ideally, this class can be instantiated with a constructor that accepts a single String as argument and as value of the string, we would use the one provided in key `OBJECTIVE_FUNCTION`.
    This should then create the instance-based objective function that can evaluate `BEST_Y` (see below) and return exactly `BEST_F` (see below).  
  + `MAX_FES`: the computational budget in terms of the maximum permitted number of FEs or the value `9223372036854775807` (`Long.MAX_VALUE`) if no limit was specified
  + `MAX_TIME`: the computational budget in terms of the maximum runtime in milliseconds or the value `9223372036854775807` (`Long.MAX_VALUE`) if no limit was specified
  + `GOAL_F`: the goal objective value.
    If `GOAL_F` is reached, the run will be terminated.
    `-Infinity` indicates that no goal is specified, as all problems are subject to minimization.
  + `RANDOM_SEED`: the random seed used for this run.
    The optimization process will receive access to an instance of `java.util.Random` seeded with this seed.
    The optimization process is not permitted to use any other source of randomness besides this.
    From this seed, the last log file name part is derived.  
- The section `SYSTEM` holds static system information in the form of key-value pairs, providing comprehensive data about:
  + the computer model
  + the CPU type and speed
  + the GPU version
  + the java version
  + the mainboard version and model
  + the available memory
  + information about the operating system
  + the command line with which the Java program was executed
  + the start time of the session
  + the version of the aitoa software the libraries it uses internally. 
  All key-value pairs are of the format `# KEY: VALUE`.
- The section `STATE` holds the end-of-run state, with the following key-value pairs:
  + `CONSUMED_FES`: The total number of consumed FEs.
    This value can be higher than the number of FEs in the last log point, as in the default setup only improving moves are logged.
  + `LAST_IMPROVEMENT_FE`: the index of the FE where the last improvement was made by the optimization process.
  + `CONSUMED_TIME`: The total runtime consumed in milliseconds.
    This includes the tear-down time of the algorithm and may thus be (slightly) more than the time budget limit (if any was specified).
    It can also be higher than the value in the last log point, as in the default setup only improving moves are logged.
  + `LAST_IMPROVEMENT_TIME`: The last time in milliseconds from the start of the run when an improvement was made.
  + `BEST_F`: The objective value of the best solution encountered. Must be consistent with log section. 
  All key-value pairs are of the format `# KEY: VALUE`.
- The section `BEST_X` contains the best point in the search space in an arbitrary, problem-specific format.
- If search and solution space are different, then the section `BEST_Y` holds the best point in the solution space in arbitrary, problem-specific format.

Notice that our experiment execution facility automatically fills in the log file with all of these information.
The only information that needs to be filled in programmatically by the algorithm developer are the contents of the `ALGORITHM_SETUP` section.

# 4. Tools

Besides the [experiment executor](src/main/java/aitoa/utils/Experiment.java), a set of tools for this experiment data structure are provided in package [`aitoa.utils`](src/main/java/aitoa/utils/logs).
These can transform and aggregate the log output.

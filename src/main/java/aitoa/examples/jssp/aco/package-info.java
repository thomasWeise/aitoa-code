/**
 * An adaptation of ACO to the Job-Shop Scheduling Problem.
 * <p>
 * Here, the idea is that while the ants travel through the
 * "network" of jobs, it automatically not just constructs the
 * permutation but also the Gantt chart at the same time. The
 * cost of adding a certain node is 1 + the amount in which it
 * increases the makespan. For knowing such a cost, we need the
 * partial Gantt chart anyway. Thus, once the ant reaches the end
 * node, it also has constructed one complete solution and
 * computed the makespan as well. In other words, we then do
 * neither need a representation mapping nor do we need the
 * objective function to compute the makespan.
 */
package aitoa.examples.jssp.aco;

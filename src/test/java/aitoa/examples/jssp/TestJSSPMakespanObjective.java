package aitoa.examples.jssp;

import org.junit.Assert;
import org.junit.Test;

/** A Test for the JSSP MakeSpan Objective Function */
public class TestJSSPMakespanObjective {

  /** test the makespan */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void test_objective() {
    final JSSPMakespanObjectiveFunction f = new JSSPMakespanObjectiveFunction();
    JSSPCandidateSolution x;

    x = new JSSPCandidateSolution(0, 0);
    Assert.assertEquals(0d, f.evaluate(x), 0d);

    x = new JSSPCandidateSolution(1, 1);
    Assert.assertEquals(0d, f.evaluate(x), 0d);
    x.schedule[0][2] = 5;
    Assert.assertEquals(5d, f.evaluate(x), 0d);

    x = new JSSPCandidateSolution(2, 1);
    Assert.assertEquals(0d, f.evaluate(x), 0d);
    x.schedule[0][2] = 5;
    x.schedule[1][2] = 4;
    Assert.assertEquals(5d, f.evaluate(x), 0d);
    x.schedule[1][2] = 5;
    x.schedule[0][2] = 4;
    Assert.assertEquals(5d, f.evaluate(x), 0d);
    x.schedule[0][2] = 3;
    x.schedule[1][2] = 3;
    Assert.assertEquals(3d, f.evaluate(x), 0d);

    x = new JSSPCandidateSolution(2, 3);
    Assert.assertEquals(0d, f.evaluate(x), 0d);
    x.schedule[0][8] = 5;
    x.schedule[1][8] = 4;
    Assert.assertEquals(5d, f.evaluate(x), 0d);
    x.schedule[1][8] = 5;
    x.schedule[0][8] = 4;
    Assert.assertEquals(5d, f.evaluate(x), 0d);
    x.schedule[0][8] = 3;
    x.schedule[1][8] = 3;
    Assert.assertEquals(3d, f.evaluate(x), 0d);

    x = new JSSPCandidateSolution(4, 3);
    Assert.assertEquals(0d, f.evaluate(x), 0d);
    x.schedule[0][8] = 5;
    x.schedule[1][8] = 4;
    x.schedule[2][8] = 2;
    x.schedule[3][8] = 3;
    Assert.assertEquals(5d, f.evaluate(x), 0d);
    x.schedule[0][8] = 4;
    x.schedule[1][8] = 5;
    x.schedule[2][8] = 2;
    x.schedule[3][8] = 3;
    Assert.assertEquals(5d, f.evaluate(x), 0d);
    x.schedule[0][8] = 2;
    x.schedule[1][8] = 4;
    x.schedule[2][8] = 5;
    x.schedule[3][8] = 3;
    Assert.assertEquals(5d, f.evaluate(x), 0d);
  }
}
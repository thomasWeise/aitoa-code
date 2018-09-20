package aitoa.examples.jssp;

import org.junit.Assert;
import org.junit.Test;

/** A Test for the JSSP representation mapping */
public class TestJSSPRepresentationMapping {

  /** test the creation */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void test_JSSPRepresentationMappingExample() {
    final JSSPInstance instance =
        JSSPRepresentationMappingExample.INSTANCE;
    Assert.assertNotNull(instance);
    final int[] point = JSSPRepresentationMappingExample.POINT;
    Assert.assertNotNull(point);
    final JSSPRepresentationMapping mapping =
        JSSPRepresentationMappingExample.MAPPING;
    Assert.assertNotNull(mapping);
    final JSSPCandidateSolution solution =
        JSSPRepresentationMappingExample.SOLUTION;
    Assert.assertNotNull(solution);
    final JSSPMakespanObjectiveFunction f =
        JSSPRepresentationMappingExample.F;
    Assert.assertNotNull(f);

    Assert.assertEquals(f.evaluate(solution), 180, 0);
    Assert.assertArrayEquals(new int[] { 0, 0, 10, 1, 20, 30, 2,
        130, 140, 3, 140, 160 }, solution.schedule[0]);
    Assert.assertArrayEquals(new int[] { 1, 0, 20, 0, 20, 40, 2,
        40, 60, 3, 160, 175 }, solution.schedule[1]);
    Assert.assertArrayEquals(new int[] { 2, 0, 30, 0, 40, 60, 1,
        60, 110, 3, 110, 125 }, solution.schedule[2]);
    Assert.assertArrayEquals(new int[] { 1, 30, 60, 3, 60, 90, 2,
        90, 130, 0, 130, 170 }, solution.schedule[3]);
    Assert.assertArrayEquals(new int[] { 3, 0, 50, 2, 60, 72, 1,
        110, 140, 0, 170, 180 }, solution.schedule[4]);
  }
}
package aitoa.examples.jssp;

import org.junit.Assert;
import org.junit.Test;

/** A Test for the JSSP Instance */
public class TestJSSPInstance {

  /** test the demo instance */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void test_load_demo() {
    final JSSPInstance inst = new JSSPInstance("demo"); //$NON-NLS-1$
    Assert.assertEquals(4, inst.n);
    Assert.assertEquals(5, inst.m);

    Assert.assertArrayEquals(
        new int[] { 0, 10, 1, 20, 2, 20, 3, 40, 4, 10 }, inst.jobs[0]);

    Assert.assertArrayEquals(
        new int[] { 1, 20, 0, 10, 3, 30, 2, 50, 4, 30 }, inst.jobs[1]);

    Assert.assertArrayEquals(
        new int[] { 2, 30, 1, 20, 4, 12, 3, 40, 0, 10 }, inst.jobs[2]);

    Assert.assertArrayEquals(
        new int[] { 4, 50, 3, 30, 2, 15, 0, 20, 1, 15 }, inst.jobs[3]);
  }

  /** test the abz5 instance */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void test_load_abz5() {
    final JSSPInstance inst = new JSSPInstance("abz5"); //$NON-NLS-1$
    Assert.assertEquals(10, inst.n);
    Assert.assertEquals(10, inst.m);

    Assert.assertEquals(4, inst.jobs[0][0]);
    Assert.assertEquals(88, inst.jobs[0][1]);
    Assert.assertEquals(8, inst.jobs[0][2]);
    Assert.assertEquals(68, inst.jobs[0][3]);
    Assert.assertEquals(6, inst.jobs[0][4]);
    Assert.assertEquals(94, inst.jobs[0][5]);
    Assert.assertEquals(0, inst.jobs[0][16]);
    Assert.assertEquals(86, inst.jobs[0][17]);
    Assert.assertEquals(3, inst.jobs[0][18]);
    Assert.assertEquals(92, inst.jobs[0][19]);

    Assert.assertEquals(5, inst.jobs[1][0]);
    Assert.assertEquals(72, inst.jobs[1][1]);
    Assert.assertEquals(3, inst.jobs[1][2]);
    Assert.assertEquals(50, inst.jobs[1][3]);
    Assert.assertEquals(6, inst.jobs[1][4]);
    Assert.assertEquals(69, inst.jobs[1][5]);
    Assert.assertEquals(7, inst.jobs[1][16]);
    Assert.assertEquals(94, inst.jobs[1][17]);
    Assert.assertEquals(9, inst.jobs[1][18]);
    Assert.assertEquals(63, inst.jobs[1][19]);

    Assert.assertEquals(3, inst.jobs[9][0]);
    Assert.assertEquals(50, inst.jobs[9][1]);
    Assert.assertEquals(0, inst.jobs[9][2]);
    Assert.assertEquals(59, inst.jobs[9][3]);
    Assert.assertEquals(1, inst.jobs[9][4]);
    Assert.assertEquals(82, inst.jobs[9][5]);
    Assert.assertEquals(5, inst.jobs[9][16]);
    Assert.assertEquals(59, inst.jobs[9][17]);
    Assert.assertEquals(2, inst.jobs[9][18]);
    Assert.assertEquals(96, inst.jobs[9][19]);
  }

  /** test the swv20 instance */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void test_load_swv20() {
    final JSSPInstance inst = new JSSPInstance("swv20"); //$NON-NLS-1$
    Assert.assertEquals(inst.m, 10);
    Assert.assertEquals(inst.n, 50);

    Assert.assertArrayEquals(new int[] { 8, 100, 7, 30, 4, 42, 9, 11, 2,
        31, 1, 71, 5, 41, 0, 1, 3, 55, 6, 94 }, inst.jobs[0]);

    Assert.assertArrayEquals(new int[] { 4, 81, 6, 20, 3, 96, 7, 39, 8, 29,
        0, 90, 9, 61, 2, 64, 1, 86, 5, 47 }, inst.jobs[1]);

    Assert.assertArrayEquals(new int[] { 5, 80, 0, 56, 1, 88, 7, 19, 2, 68,
        8, 95, 3, 44, 4, 22, 9, 60, 6, 80 }, inst.jobs[2]);

    Assert.assertArrayEquals(new int[] { 6, 23, 7, 9, 1, 90, 0, 51, 2, 52,
        9, 14, 5, 30, 4, 1, 8, 25, 3, 83 }, inst.jobs[5]);

    Assert.assertArrayEquals(new int[] { 4, 49, 6, 27, 7, 17, 5, 64, 2, 30,
        8, 56, 0, 42, 3, 97, 9, 82, 1, 34 }, inst.jobs[49]);
  }
}
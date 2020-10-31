package aitoa.examples.jssp;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import aitoa.TestTools;

/** A Test for the JSSP Instance */
public class TestJSSPInstance {

  /** test the demo instance */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void testLoadDemo() {
    final JSSPInstance inst = new JSSPInstance("demo"); //$NON-NLS-1$
    Assert.assertEquals(4, inst.n);
    Assert.assertEquals(5, inst.m);

    Assert.assertArrayEquals(
        new int[] { 0, 10, 1, 20, 2, 20, 3, 40, 4, 10 },
        inst.jobs[0]);

    Assert.assertArrayEquals(
        new int[] { 1, 20, 0, 10, 3, 30, 2, 50, 4, 30 },
        inst.jobs[1]);

    Assert.assertArrayEquals(
        new int[] { 2, 30, 1, 20, 4, 12, 3, 40, 0, 10 },
        inst.jobs[2]);

    Assert.assertArrayEquals(
        new int[] { 4, 50, 3, 30, 2, 15, 0, 20, 1, 15 },
        inst.jobs[3]);

    Assert.assertTrue(new JSSPMakespanObjectiveFunction(inst)
        .lowerBound() > 0);
  }

  /** test the abz5 instance */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void testLoadAbz5() {
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

    Assert.assertTrue(new JSSPMakespanObjectiveFunction(inst)
        .lowerBound() > 0);
  }

  /** test the swv20 instance */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void testLoadSwv20() {
    final JSSPInstance inst = new JSSPInstance("swv20"); //$NON-NLS-1$
    Assert.assertEquals(inst.m, 10);
    Assert.assertEquals(inst.n, 50);

    Assert.assertArrayEquals(new int[] { 8, 100, 7, 30, 4, 42, 9,
        11, 2, 31, 1, 71, 5, 41, 0, 1, 3, 55, 6, 94 },
        inst.jobs[0]);

    Assert.assertArrayEquals(new int[] { 4, 81, 6, 20, 3, 96, 7,
        39, 8, 29, 0, 90, 9, 61, 2, 64, 1, 86, 5, 47 },
        inst.jobs[1]);

    Assert.assertArrayEquals(new int[] { 5, 80, 0, 56, 1, 88, 7,
        19, 2, 68, 8, 95, 3, 44, 4, 22, 9, 60, 6, 80 },
        inst.jobs[2]);

    Assert.assertArrayEquals(new int[] { 6, 23, 7, 9, 1, 90, 0,
        51, 2, 52, 9, 14, 5, 30, 4, 1, 8, 25, 3, 83 },
        inst.jobs[5]);

    Assert.assertArrayEquals(new int[] { 4, 49, 6, 27, 7, 17, 5,
        64, 2, 30, 8, 56, 0, 42, 3, 97, 9, 82, 1, 34 },
        inst.jobs[49]);

    Assert.assertTrue(new JSSPMakespanObjectiveFunction(inst)
        .lowerBound() > 0);
  }

  /** test the all the instances */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void testLoadAllInstances() {
    for (final String s : new String[] { //
        "abz5", //$NON-NLS-1$
        "abz6", //$NON-NLS-1$
        "abz7", //$NON-NLS-1$
        "abz8", //$NON-NLS-1$
        "abz9", //$NON-NLS-1$
        "dmu01", //$NON-NLS-1$
        "dmu02", //$NON-NLS-1$
        "dmu03", //$NON-NLS-1$
        "dmu04", //$NON-NLS-1$
        "dmu05", //$NON-NLS-1$
        "dmu06", //$NON-NLS-1$
        "dmu07", //$NON-NLS-1$
        "dmu08", //$NON-NLS-1$
        "dmu09", //$NON-NLS-1$
        "dmu10", //$NON-NLS-1$
        "dmu11", //$NON-NLS-1$
        "dmu12", //$NON-NLS-1$
        "dmu13", //$NON-NLS-1$
        "dmu14", //$NON-NLS-1$
        "dmu15", //$NON-NLS-1$
        "dmu16", //$NON-NLS-1$
        "dmu17", //$NON-NLS-1$
        "dmu18", //$NON-NLS-1$
        "dmu19", //$NON-NLS-1$
        "dmu20", //$NON-NLS-1$
        "dmu21", //$NON-NLS-1$
        "dmu22", //$NON-NLS-1$
        "dmu23", //$NON-NLS-1$
        "dmu24", //$NON-NLS-1$
        "dmu25", //$NON-NLS-1$
        "dmu26", //$NON-NLS-1$
        "dmu27", //$NON-NLS-1$
        "dmu28", //$NON-NLS-1$
        "dmu29", //$NON-NLS-1$
        "dmu30", //$NON-NLS-1$
        "dmu31", //$NON-NLS-1$
        "dmu32", //$NON-NLS-1$
        "dmu33", //$NON-NLS-1$
        "dmu34", //$NON-NLS-1$
        "dmu35", //$NON-NLS-1$
        "dmu36", //$NON-NLS-1$
        "dmu37", //$NON-NLS-1$
        "dmu38", //$NON-NLS-1$
        "dmu39", //$NON-NLS-1$
        "dmu40", //$NON-NLS-1$
        "dmu41", //$NON-NLS-1$
        "dmu42", //$NON-NLS-1$
        "dmu43", //$NON-NLS-1$
        "dmu44", //$NON-NLS-1$
        "dmu45", //$NON-NLS-1$
        "dmu46", //$NON-NLS-1$
        "dmu47", //$NON-NLS-1$
        "dmu48", //$NON-NLS-1$
        "dmu49", //$NON-NLS-1$
        "dmu50", //$NON-NLS-1$
        "dmu51", //$NON-NLS-1$
        "dmu52", //$NON-NLS-1$
        "dmu53", //$NON-NLS-1$
        "dmu54", //$NON-NLS-1$
        "dmu55", //$NON-NLS-1$
        "dmu56", //$NON-NLS-1$
        "dmu57", //$NON-NLS-1$
        "dmu58", //$NON-NLS-1$
        "dmu59", //$NON-NLS-1$
        "dmu60", //$NON-NLS-1$
        "dmu61", //$NON-NLS-1$
        "dmu62", //$NON-NLS-1$
        "dmu63", //$NON-NLS-1$
        "dmu64", //$NON-NLS-1$
        "dmu65", //$NON-NLS-1$
        "dmu66", //$NON-NLS-1$
        "dmu67", //$NON-NLS-1$
        "dmu68", //$NON-NLS-1$
        "dmu69", //$NON-NLS-1$
        "dmu70", //$NON-NLS-1$
        "dmu71", //$NON-NLS-1$
        "dmu72", //$NON-NLS-1$
        "dmu73", //$NON-NLS-1$
        "dmu74", //$NON-NLS-1$
        "dmu75", //$NON-NLS-1$
        "dmu76", //$NON-NLS-1$
        "dmu77", //$NON-NLS-1$
        "dmu78", //$NON-NLS-1$
        "dmu79", //$NON-NLS-1$
        "dmu80", //$NON-NLS-1$
        "ft06", //$NON-NLS-1$
        "ft10", //$NON-NLS-1$
        "ft20", //$NON-NLS-1$
        "la01", //$NON-NLS-1$
        "la02", //$NON-NLS-1$
        "la03", //$NON-NLS-1$
        "la04", //$NON-NLS-1$
        "la05", //$NON-NLS-1$
        "la06", //$NON-NLS-1$
        "la07", //$NON-NLS-1$
        "la08", //$NON-NLS-1$
        "la09", //$NON-NLS-1$
        "la10", //$NON-NLS-1$
        "la11", //$NON-NLS-1$
        "la12", //$NON-NLS-1$
        "la13", //$NON-NLS-1$
        "la14", //$NON-NLS-1$
        "la15", //$NON-NLS-1$
        "la16", //$NON-NLS-1$
        "la17", //$NON-NLS-1$
        "la18", //$NON-NLS-1$
        "la19", //$NON-NLS-1$
        "la20", //$NON-NLS-1$
        "la21", //$NON-NLS-1$
        "la22", //$NON-NLS-1$
        "la23", //$NON-NLS-1$
        "la24", //$NON-NLS-1$
        "la25", //$NON-NLS-1$
        "la26", //$NON-NLS-1$
        "la27", //$NON-NLS-1$
        "la28", //$NON-NLS-1$
        "la29", //$NON-NLS-1$
        "la30", //$NON-NLS-1$
        "la31", //$NON-NLS-1$
        "la32", //$NON-NLS-1$
        "la33", //$NON-NLS-1$
        "la34", //$NON-NLS-1$
        "la35", //$NON-NLS-1$
        "la36", //$NON-NLS-1$
        "la37", //$NON-NLS-1$
        "la38", //$NON-NLS-1$
        "la39", //$NON-NLS-1$
        "la40", //$NON-NLS-1$
        "orb01", //$NON-NLS-1$
        "orb02", //$NON-NLS-1$
        "orb03", //$NON-NLS-1$
        "orb04", //$NON-NLS-1$
        "orb05", //$NON-NLS-1$
        "orb06", //$NON-NLS-1$
        "orb07", //$NON-NLS-1$
        "orb08", //$NON-NLS-1$
        "orb09", //$NON-NLS-1$
        "orb10", //$NON-NLS-1$
        "swv01", //$NON-NLS-1$
        "swv02", //$NON-NLS-1$
        "swv03", //$NON-NLS-1$
        "swv04", //$NON-NLS-1$
        "swv05", //$NON-NLS-1$
        "swv06", //$NON-NLS-1$
        "swv07", //$NON-NLS-1$
        "swv08", //$NON-NLS-1$
        "swv09", //$NON-NLS-1$
        "swv10", //$NON-NLS-1$
        "swv11", //$NON-NLS-1$
        "swv12", //$NON-NLS-1$
        "swv13", //$NON-NLS-1$
        "swv14", //$NON-NLS-1$
        "swv15", //$NON-NLS-1$
        "swv16", //$NON-NLS-1$
        "swv17", //$NON-NLS-1$
        "swv18", //$NON-NLS-1$
        "swv19", //$NON-NLS-1$
        "swv20", //$NON-NLS-1$
        "ta01", //$NON-NLS-1$
        "ta02", //$NON-NLS-1$
        "ta03", //$NON-NLS-1$
        "ta04", //$NON-NLS-1$
        "ta05", //$NON-NLS-1$
        "ta06", //$NON-NLS-1$
        "ta07", //$NON-NLS-1$
        "ta08", //$NON-NLS-1$
        "ta09", //$NON-NLS-1$
        "ta10", //$NON-NLS-1$
        "ta11", //$NON-NLS-1$
        "ta12", //$NON-NLS-1$
        "ta13", //$NON-NLS-1$
        "ta14", //$NON-NLS-1$
        "ta15", //$NON-NLS-1$
        "ta16", //$NON-NLS-1$
        "ta17", //$NON-NLS-1$
        "ta18", //$NON-NLS-1$
        "ta19", //$NON-NLS-1$
        "ta20", //$NON-NLS-1$
        "ta21", //$NON-NLS-1$
        "ta22", //$NON-NLS-1$
        "ta23", //$NON-NLS-1$
        "ta24", //$NON-NLS-1$
        "ta25", //$NON-NLS-1$
        "ta26", //$NON-NLS-1$
        "ta27", //$NON-NLS-1$
        "ta28", //$NON-NLS-1$
        "ta29", //$NON-NLS-1$
        "ta30", //$NON-NLS-1$
        "ta31", //$NON-NLS-1$
        "ta32", //$NON-NLS-1$
        "ta33", //$NON-NLS-1$
        "ta34", //$NON-NLS-1$
        "ta35", //$NON-NLS-1$
        "ta36", //$NON-NLS-1$
        "ta37", //$NON-NLS-1$
        "ta38", //$NON-NLS-1$
        "ta39", //$NON-NLS-1$
        "ta40", //$NON-NLS-1$
        "ta41", //$NON-NLS-1$
        "ta42", //$NON-NLS-1$
        "ta43", //$NON-NLS-1$
        "ta44", //$NON-NLS-1$
        "ta45", //$NON-NLS-1$
        "ta46", //$NON-NLS-1$
        "ta47", //$NON-NLS-1$
        "ta48", //$NON-NLS-1$
        "ta49", //$NON-NLS-1$
        "ta50", //$NON-NLS-1$
        "ta51", //$NON-NLS-1$
        "ta52", //$NON-NLS-1$
        "ta53", //$NON-NLS-1$
        "ta54", //$NON-NLS-1$
        "ta55", //$NON-NLS-1$
        "ta56", //$NON-NLS-1$
        "ta57", //$NON-NLS-1$
        "ta58", //$NON-NLS-1$
        "ta59", //$NON-NLS-1$
        "ta60", //$NON-NLS-1$
        "ta61", //$NON-NLS-1$
        "ta62", //$NON-NLS-1$
        "ta63", //$NON-NLS-1$
        "ta64", //$NON-NLS-1$
        "ta65", //$NON-NLS-1$
        "ta66", //$NON-NLS-1$
        "ta67", //$NON-NLS-1$
        "ta68", //$NON-NLS-1$
        "ta69", //$NON-NLS-1$
        "ta70", //$NON-NLS-1$
        "ta71", //$NON-NLS-1$
        "ta72", //$NON-NLS-1$
        "ta73", //$NON-NLS-1$
        "ta74", //$NON-NLS-1$
        "ta75", //$NON-NLS-1$
        "ta76", //$NON-NLS-1$
        "ta77", //$NON-NLS-1$
        "ta78", //$NON-NLS-1$
        "ta79", //$NON-NLS-1$
        "ta80", //$NON-NLS-1$
        "yn1", //$NON-NLS-1$
        "yn2", //$NON-NLS-1$
        "yn3", //$NON-NLS-1$
        "yn4", //$NON-NLS-1$
    }) {
      final JSSPInstance inst = new JSSPInstance(s);
      Assert.assertEquals(s, inst.id);
      TestTools.assertGreater(inst.n, 0);
      TestTools.assertGreater(inst.m, 0);
      final JSSPMakespanObjectiveFunction f =
          new JSSPMakespanObjectiveFunction(inst);
      TestTools.assertGreater(f.lowerBound(), 0d);
    }
  }

  /** test all instances */
  @SuppressWarnings({ "static-method", "unused" })
  @Test(timeout = 3600000)
  public void testAllInstances() {
    final Collection<String> all =
        JSSPInstance.getAllInstances();
    Assert.assertNotNull(all);
    int size = all.size();
    TestTools.assertGreater(size, 0);
    for (final String n : all) {
      new JSSPInstance(n);
      --size;
    }
    Assert.assertEquals(0, size);
  }
}

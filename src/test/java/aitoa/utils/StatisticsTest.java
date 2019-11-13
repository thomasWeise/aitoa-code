package aitoa.utils;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

/** Test the utilities for the computing statistics */
public class StatisticsTest {

  /**
   * test the quantiles
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 100000)
  public void testQuantiles_Longs() {
    long[] data = { 1, 2, 3, 4, 4, 5, 5, 5, 5, 7, 8, 9 };
    Arrays.sort(data);

    Assert.assertEquals(data[0],
        Statistics.quantile(0, data).doubleValue(), 0);
    Assert.assertEquals(data[data.length - 1],
        Statistics.quantile(1, data).doubleValue(), 0);

    Assert.assertEquals(1.5666666666666666666666666666d,
        Statistics.quantile(0.1, data).doubleValue(), 0);

    Assert.assertEquals(2.8d,
        Statistics.quantile(0.2, data).doubleValue(), 1e-15d);

    Assert.assertEquals(4d,
        Statistics.quantile(0.3, data).doubleValue(), 0);

    Assert.assertEquals(4.26666666666666666666666d,
        Statistics.quantile(0.4, data).doubleValue(), 0);

    Assert.assertEquals(5d,
        Statistics.quantile(0.5, data).doubleValue(), 0);

    Assert.assertEquals(5d,
        Statistics.quantile(0.6, data).doubleValue(), 0);

    Assert.assertEquals(5d,
        Statistics.quantile(0.7, data).doubleValue(), 0);

    Assert.assertEquals(7.2d,
        Statistics.quantile(0.8, data).doubleValue(), 1e-15d);

    Assert.assertEquals(8.433333333333333333333333333d,
        Statistics.quantile(0.9, data).doubleValue(), 2e-15d);

    data = new long[] { 1, 2, 3, 4, 4, 5, 5, 5, 7, 8, 9 };
    Arrays.sort(data);

    Assert.assertEquals(data[0],
        Statistics.quantile(0, data).doubleValue(), 0);
    Assert.assertEquals(data[data.length - 1],
        Statistics.quantile(1, data).doubleValue(), 0);

    Assert.assertEquals(1.4666666666666666666666666d,
        Statistics.quantile(0.1, data).doubleValue(), 2e-15d);

    Assert.assertEquals(2.6d,
        Statistics.quantile(0.2, data).doubleValue(), 1e-15d);

    Assert.assertEquals(3.7333333333333333333333333333333333d,
        Statistics.quantile(0.3, data).doubleValue(), 0);

    Assert.assertEquals(4d,
        Statistics.quantile(0.4, data).doubleValue(), 0);

    Assert.assertEquals(5d,
        Statistics.quantile(0.5, data).doubleValue(), 0);

    Assert.assertEquals(5d,
        Statistics.quantile(0.6, data).doubleValue(), 0);

    Assert.assertEquals(5.53333333333333333333333d,
        Statistics.quantile(0.7, data).doubleValue(), 2e-15d);

    Assert.assertEquals(7.4d,
        Statistics.quantile(0.8, data).doubleValue(), 2e-15d);

    Assert.assertEquals(8.533333333333333333333333333d,
        Statistics.quantile(0.9, data).doubleValue(), 2e-15d);
  }

  /**
   * test the quantiles
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 100000)
  public void testQuantiles_Doubles() {
    double[] data = { 1, 2, 3, 4, 4, 5, 5, 5, 5, 7, 8, 9 };
    Arrays.sort(data);

    Assert.assertEquals(data[0],
        Statistics.quantile(0, data).doubleValue(), 0);
    Assert.assertEquals(data[data.length - 1],
        Statistics.quantile(1, data).doubleValue(), 0);

    Assert.assertEquals(1.5666666666666666666666666666d,
        Statistics.quantile(0.1, data).doubleValue(), 0);

    Assert.assertEquals(2.8d,
        Statistics.quantile(0.2, data).doubleValue(), 1e-15d);

    Assert.assertEquals(4d,
        Statistics.quantile(0.3, data).doubleValue(), 0);

    Assert.assertEquals(4.26666666666666666666666d,
        Statistics.quantile(0.4, data).doubleValue(), 0);

    Assert.assertEquals(5d,
        Statistics.quantile(0.5, data).doubleValue(), 0);

    Assert.assertEquals(5d,
        Statistics.quantile(0.6, data).doubleValue(), 0);

    Assert.assertEquals(5d,
        Statistics.quantile(0.7, data).doubleValue(), 0);

    Assert.assertEquals(7.2d,
        Statistics.quantile(0.8, data).doubleValue(), 1e-15d);

    Assert.assertEquals(8.433333333333333333333333333d,
        Statistics.quantile(0.9, data).doubleValue(), 2e-15d);

    data = new double[] { 1, 2, 3, 4, 4, 5, 5, 5, 7, 8, 9 };
    Arrays.sort(data);

    Assert.assertEquals(data[0],
        Statistics.quantile(0, data).doubleValue(), 0);
    Assert.assertEquals(data[data.length - 1],
        Statistics.quantile(1, data).doubleValue(), 0);

    Assert.assertEquals(1.4666666666666666666666666d,
        Statistics.quantile(0.1, data).doubleValue(), 2e-15d);

    Assert.assertEquals(2.6d,
        Statistics.quantile(0.2, data).doubleValue(), 1e-15d);

    Assert.assertEquals(3.7333333333333333333333333333333333d,
        Statistics.quantile(0.3, data).doubleValue(), 0);

    Assert.assertEquals(4d,
        Statistics.quantile(0.4, data).doubleValue(), 0);

    Assert.assertEquals(5d,
        Statistics.quantile(0.5, data).doubleValue(), 0);

    Assert.assertEquals(5d,
        Statistics.quantile(0.6, data).doubleValue(), 0);

    Assert.assertEquals(5.53333333333333333333333d,
        Statistics.quantile(0.7, data).doubleValue(), 2e-15d);

    Assert.assertEquals(7.4d,
        Statistics.quantile(0.8, data).doubleValue(), 2e-15d);

    Assert.assertEquals(8.533333333333333333333333333d,
        Statistics.quantile(0.9, data).doubleValue(), 2e-15d);
  }

  /**
   * test the mean and standard deviation
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 100000)
  public void testMeanAndSD_Longs() {

    Number[] msd =
        Statistics.sampleMeanAndStandardDeviation(new long[] { //
            1, 2, 3, 4, 5, 6, 7 });
    Assert.assertEquals(4, msd[0].doubleValue(), 0);
    Assert.assertEquals(2.160247, msd[1].doubleValue(), 1e-5);

    msd = Statistics.sampleMeanAndStandardDeviation(new long[] { //
        Long.MAX_VALUE - 1, Long.MAX_VALUE - 1,
        Long.MAX_VALUE - 1, Long.MAX_VALUE - 1, 0, 0, 0, 0 });
    Assert.assertEquals(0.5 * (Long.MAX_VALUE - 1),
        msd[0].doubleValue(), 0);
    Assert.assertEquals((Long.MAX_VALUE - 1) >> 1,
        msd[0].longValue());
    Assert.assertEquals(4930099730380269568d,
        msd[1].doubleValue(), 1e-5);

    msd = Statistics.sampleMeanAndStandardDeviation(new long[] { //
        985361758, -389670390, -951856600, -1204820760,
        -1931393426, -1548653277, 675332070, -947861662,
        53322114, -7098300, 59217847, -19447570, 1124001815,
        -1343059882, -1713104313, -2058574202, -1469095185,
        -1959795045, -479535009, -1070852488, -414955765,
        1436336606, -998194872, 618043725, -2010767242,
        1686979142, 359625800, -1295077009, -35028359, 523734664,
        893282033, 1374032129, -413955854, -1144040057,
        2141141260, 425714759, 1901129336, -1164606514,
        -635368809, 2108308124, -2058212920, -1597072713,
        -948523359, -341727516, 1421910802, -1763930493,
        938151131, -1822083429, 1561133826, -1078383140,
        -279791102, 542752516, -592697136, -2133819796,
        -1571167441, -571706829, -65110815, 1431008289,
        2107824381, 1730160489, -1408141407, 1801972892,
        -1374179269, 1152124184, -933342943, 1214828265,
        568665514, 818050240, -1231885980, -718415098,
        1306654947, 1615920226, -1587187869, 883929987,
        -651936831, -1586198575, -801626789, -661845924,
        -1758653039, 1000850329, -1523453453, -1626869178,
        -219092547, -645752824, -1782595270, -919354679,
        1072839226, -763887224, 1677660622, -766615869,
        -1634226508, -1350726225, 1614748166, 1948271804,
        -2011799199, 678044646, 437272321, -723873608,
        1921021526, 1857353974 });
    Assert.assertEquals(-190439861, msd[0].doubleValue(), 0.05);
    Assert.assertEquals(1279203887, msd[1].doubleValue(), 0.5);
  }

  /**
   * test the mean and standard deviation
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 100000)
  public void testMeanAndSD_Doubles() {

    Number[] msd =
        Statistics.sampleMeanAndStandardDeviation(new double[] { //
            1, 2, 3, 4, 5, 6, 7 });
    Assert.assertEquals(4, msd[0].doubleValue(), 0);
    Assert.assertEquals(2.160247, msd[1].doubleValue(), 1e-5);

    msd =
        Statistics.sampleMeanAndStandardDeviation(new double[] { //
            985361758, -389670390, -951856600, -1204820760,
            -1931393426, -1548653277, 675332070, -947861662,
            53322114, -7098300, 59217847, -19447570, 1124001815,
            -1343059882, -1713104313, -2058574202, -1469095185,
            -1959795045, -479535009, -1070852488, -414955765,
            1436336606, -998194872, 618043725, -2010767242,
            1686979142, 359625800, -1295077009, -35028359,
            523734664, 893282033, 1374032129, -413955854,
            -1144040057, 2141141260, 425714759, 1901129336,
            -1164606514, -635368809, 2108308124, -2058212920,
            -1597072713, -948523359, -341727516, 1421910802,
            -1763930493, 938151131, -1822083429, 1561133826,
            -1078383140, -279791102, 542752516, -592697136,
            -2133819796, -1571167441, -571706829, -65110815,
            1431008289, 2107824381, 1730160489, -1408141407,
            1801972892, -1374179269, 1152124184, -933342943,
            1214828265, 568665514, 818050240, -1231885980,
            -718415098, 1306654947, 1615920226, -1587187869,
            883929987, -651936831, -1586198575, -801626789,
            -661845924, -1758653039, 1000850329, -1523453453,
            -1626869178, -219092547, -645752824, -1782595270,
            -919354679, 1072839226, -763887224, 1677660622,
            -766615869, -1634226508, -1350726225, 1614748166,
            1948271804, -2011799199, 678044646, 437272321,
            -723873608, 1921021526, 1857353974 });
    Assert.assertEquals(-190439861, msd[0].doubleValue(), 0.05);
    Assert.assertEquals(1279203887, msd[1].doubleValue(), 0.5);
  }
}

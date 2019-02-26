package aitoa.bookExamples;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

/**
 * Computing the probability to win at least k times in n coin
 * flips
 */
public class CoinFlip {

  /**
   * compute n over k
   *
   * @param n
   *          n
   * @param k
   *          k
   * @return the value
   */
  private static final BigInteger nChooseK(final int n,
      final int k) {

    if (k == 1) {
      return BigInteger.valueOf(n);
    }
    if (k == n) {
      return BigInteger.ONE;
    }

    BigInteger top = BigInteger.ONE;
    BigInteger bottom = BigInteger.ONE;

    for (int i = 1; i <= k; i++) {
      top = top.multiply(BigInteger.valueOf((n + 1) - i));
      bottom = bottom.multiply(BigInteger.valueOf(i));
    }
    return top.divide(bottom);
  }

  /** the internal cache */
  private static BigInteger[] POW2 = new BigInteger[128];

  /**
   * compute the probability to toss k heads our of n tosses in
   * form of a fraction res[0]/res[1]
   *
   * @param n
   *          the n
   * @param k
   *          the k
   * @return the probability
   */
  private static final BigInteger[]
      kHeadsFromNTosses(final int n, final int k) {
    if (n > CoinFlip.POW2.length) {
      CoinFlip.POW2 = Arrays.copyOf(CoinFlip.POW2, Math.max(
          n + 1,
          Math.max(n + (n >>> 1), CoinFlip.POW2.length * 2)));
    }
    if (CoinFlip.POW2[n] == null) {
      CoinFlip.POW2[n] = BigInteger.valueOf(2).pow(n);
    }
    return new BigInteger[] { CoinFlip.nChooseK(n, k),
        CoinFlip.POW2[n] };
  }

  /**
   * compute the probability to toss at least k heads our of n
   * tosses in form of a fraction res[0]/res[1]
   *
   * @param n
   *          the n
   * @param k
   *          the k
   * @return the probability
   */
  private static final BigInteger[]
      atLeastKHeadsFromNTosses(final int n, final int k) {
    final BigInteger[] result =
        new BigInteger[] { BigInteger.ZERO, BigInteger.ONE };

    for (int i = k; i <= n; i++) {
      final BigInteger[] add = CoinFlip.kHeadsFromNTosses(n, i);

      final BigInteger bottom = result[1].multiply(add[1]);
      final BigInteger top = result[0].multiply(add[1])
          .add(result[1].multiply(add[0]));
      final BigInteger gcd = bottom.gcd(top);
      result[0] = top.divide(gcd);
      result[1] = bottom.divide(gcd);
    }
    return (result);
  }

  /** test the demo instance */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void test_probabilities() {
    BigInteger[] results;
    double d;

    results = CoinFlip.atLeastKHeadsFromNTosses(3, 2);
    d = new BigDecimal(results[0])
        .divide(new BigDecimal(results[1])).doubleValue();
    Assert.assertEquals(0.5, d, 1e-10);

    results = CoinFlip.atLeastKHeadsFromNTosses(6, 3);
    d = new BigDecimal(results[0])
        .divide(new BigDecimal(results[1])).doubleValue();
    Assert.assertEquals(0.65625, d, 1e-10);

    results = CoinFlip.atLeastKHeadsFromNTosses(18, 12);
    d = new BigDecimal(results[0])
        .divide(new BigDecimal(results[1])).doubleValue();
    Assert.assertEquals(0.118942, d, 1e-6);

    results = CoinFlip.atLeastKHeadsFromNTosses(1000, 500);
    d = new BigDecimal(results[0])
        .divide(new BigDecimal(results[1])).doubleValue();
    Assert.assertEquals(0.512613, d, 1e-6);
  }

  /** test the demo instance */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void test_nChooseK() {
    BigInteger result;

    result = CoinFlip.nChooseK(1, 1);
    Assert.assertEquals(1, result.intValue());
    result = CoinFlip.nChooseK(2, 1);
    Assert.assertEquals(2, result.intValue());
    result = CoinFlip.nChooseK(2, 2);
    Assert.assertEquals(1, result.intValue());
    result = CoinFlip.nChooseK(3, 1);
    Assert.assertEquals(3, result.intValue());
    result = CoinFlip.nChooseK(3, 2);
    Assert.assertEquals(3, result.intValue());
    result = CoinFlip.nChooseK(3, 3);
    Assert.assertEquals(1, result.intValue());
    result = CoinFlip.nChooseK(4, 1);
    Assert.assertEquals(4, result.intValue());
    result = CoinFlip.nChooseK(4, 2);
    Assert.assertEquals(6, result.intValue());
    result = CoinFlip.nChooseK(4, 3);
    Assert.assertEquals(4, result.intValue());
    result = CoinFlip.nChooseK(4, 4);
    Assert.assertEquals(1, result.intValue());

    result = CoinFlip.nChooseK(5, 3);
    Assert.assertEquals(10, result.intValue());

    result = CoinFlip.nChooseK(152, 122);
    Assert.assertEquals(
        new BigInteger("50062763930025152588146122462400"), //$NON-NLS-1$
        result);
  }

  /**
   * perform a dice throw experiment
   *
   * @param args
   *          the command line arguments
   */
  public static final void main(final String[] args) {
    final BigInteger[] res =
        CoinFlip.atLeastKHeadsFromNTosses(200, 180);

    Tools.printLongNumber(res[0]);
    System.out.print('/');
    Tools.printLongNumber(res[1]);
    System.out.println();
    Tools.printLongNumber(res[0], 4);
    System.out.print('/');
    Tools.printLongNumber(res[1], 4);
    System.out.println();
    System.out.println(
        new BigDecimal(res[0]).divide(new BigDecimal(res[1])));
    Tools.printLongNumber(
        new BigDecimal(res[0]).divide(new BigDecimal(res[1])));
  }
}

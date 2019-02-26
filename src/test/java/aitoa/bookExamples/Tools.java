package aitoa.bookExamples;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;

import org.junit.Assert;
import org.junit.Test;

/**
 * Computing the probability to win at least k times in n coin
 * flips
 */
public class Tools {

  /**
   * format a long number string
   *
   * @param num
   *          the string
   * @param maxDigits
   *          the maximally allowed digits
   * @return the string
   */
  public static final String formatLongNumber(final long num,
      final int maxDigits) {
    return Tools.formatLongNumber(Long.toString(num), maxDigits);
  }

  /**
   * format a long number string
   *
   * @param num
   *          the string
   * @param maxDigits
   *          the maximally allowed digits
   * @return the string
   */
  @SuppressWarnings("unused")
  public static final String formatLongNumber(final Object num,
      final int maxDigits) {
    if (num == null) {
      return "";//$NON-NLS-1$
    }

    final String s = num.toString().trim();
    if (s == null) {
      return "";//$NON-NLS-1$
    }
    final int length = s.length();
    if (length <= 0) {
      return "";//$NON-NLS-1$
    }

    if (s.charAt(0) == '-') {
      return ('-'
          + Tools.formatLongNumber(s.substring(1), maxDigits));
    }

    boolean needsBD = (length > maxDigits);
    if (!needsBD) {
      try {// check if it is integer
        new BigInteger(s);
      } catch (final Throwable error) {
        needsBD = true;
      }
    }

    if (needsBD) {
      final BigDecimal bd = new BigDecimal(s);
      final String vv[] = new DecimalFormat("0.000E00")//$NON-NLS-1$
          .format(bd).split("E"); //$NON-NLS-1$

      return ("$\\approx$&nbsp;" + //$NON-NLS-1$
          vv[0] + "*10^" + //$NON-NLS-1$
          Integer.parseInt(vv[1]) + "^");//$NON-NLS-1$
    }

    final StringBuilder sb = new StringBuilder();
    for (int i = length, j = 0; (--i) >= 0;) {
      sb.insert(0, s.charAt(i));
      if (((++j) % 3) == 0) {
        if (j < length) {
          sb.insert(0, '\'');
        }
      }
    }

    return sb.toString();
  }

  /**
   * format a long number string
   *
   * @param num
   *          the string
   * @return the string
   */
  public static final String formatLongNumber(final Object num) {
    return (Tools.formatLongNumber(num, Integer.MAX_VALUE));
  }

  /**
   * format a long number string
   *
   * @param num
   *          the string
   * @return the string
   */
  public static final String formatLongNumber(final long num) {
    return (Tools.formatLongNumber(Long.valueOf(num),
        Integer.MAX_VALUE));
  }

  /**
   * print a long number string
   *
   * @param num
   *          the string
   * @param maxDigits
   *          the maximally allowed digits
   */
  public static final void printLongNumber(final Object num,
      final int maxDigits) {
    System.out.print(Tools.formatLongNumber(num, maxDigits));
  }

  /**
   * print a long number string
   *
   * @param num
   *          the string
   * @param maxDigits
   *          the maximally allowed digits
   */
  public static final void printLongNumber(final long num,
      final int maxDigits) {
    System.out.print(Tools.formatLongNumber(num, maxDigits));
  }

  /**
   * print a long number string
   *
   * @param num
   *          the string
   */
  public static final void printLongNumber(final Object num) {
    System.out.print(Tools.formatLongNumber(num));
  }

  /**
   * print a long number string
   *
   * @param num
   *          the string
   */
  public static final void printLongNumber(final long num) {
    System.out.print(Tools.formatLongNumber(num));
  }

  /** test the demo instance */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void test_formatLongNumber() {
    Assert.assertEquals(Tools.formatLongNumber("123"), //$NON-NLS-1$
        "123"); //$NON-NLS-1$
    Assert.assertEquals(Tools.formatLongNumber(123), "123"); //$NON-NLS-1$

    Assert.assertEquals(Tools.formatLongNumber("1234"), //$NON-NLS-1$
        "1'234"); //$NON-NLS-1$
    Assert.assertEquals(Tools.formatLongNumber("12345"), //$NON-NLS-1$
        "12'345"); //$NON-NLS-1$
    Assert.assertEquals(Tools.formatLongNumber("123456"), //$NON-NLS-1$
        "123'456"); //$NON-NLS-1$
    Assert.assertEquals(Tools.formatLongNumber("1234567"), //$NON-NLS-1$
        "1'234'567"); //$NON-NLS-1$

  }
}

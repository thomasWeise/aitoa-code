package aitoa;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;

/** Some tools for testing */
public class TestTools {

  /**
   * Compare two objects
   *
   * @param a
   *          the first object
   * @param b
   *          the second object
   */
  public static final void assertEquals(final Object a,
      final Object b) {
    if (a instanceof byte[]) {
      Assert.assertArrayEquals((byte[]) a, (byte[]) b);
    } else {
      if (a instanceof int[]) {
        Assert.assertArrayEquals((int[]) a, (int[]) b);
      } else {
        if (a instanceof long[]) {
          Assert.assertArrayEquals((long[]) a, (long[]) b);
        } else {
          if (a instanceof double[]) {
            Assert.assertArrayEquals((double[]) a, (double[]) b,
                0d);
          } else {
            if (a instanceof boolean[]) {
              Assert.assertArrayEquals((boolean[]) a,
                  (boolean[]) b);
            } else {
              if (a instanceof float[]) {
                Assert.assertArrayEquals((float[]) a,
                    (float[]) b, 0f);
              } else {
                if (a instanceof short[]) {
                  Assert.assertArrayEquals((short[]) a,
                      (short[]) b);
                } else {
                  if (a instanceof char[]) {
                    Assert.assertArrayEquals((char[]) a,
                        (char[]) b);
                  } else {
                    if (a instanceof Object[]) {
                      Assert.assertArrayEquals((Object[]) a,
                          (Object[]) b);
                    } else {
                      Assert.assertEquals(a, b);
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  /**
   * Fill an object with random data
   *
   * @param a
   *          the first object
   */
  public static final void fillWithRandomData(final Object a) {
    final ThreadLocalRandom random = ThreadLocalRandom.current();
    if (a instanceof byte[]) {
      random.nextBytes((byte[]) a);
    } else {
      if (a instanceof int[]) {
        final int[] b = (int[]) a;
        for (int i = b.length; (--i) >= 0;) {
          b[i] = random.nextInt();
        }
      } else {
        if (a instanceof long[]) {
          final long[] b = (long[]) a;
          for (int i = b.length; (--i) >= 0;) {
            b[i] = random.nextLong();
          }
        } else {
          if (a instanceof double[]) {
            final double[] b = (double[]) a;
            for (int i = b.length; (--i) >= 0;) {
              b[i] = (random.nextBoolean() ? random.nextDouble()
                  : random.nextLong());
            }
          } else {
            if (a instanceof boolean[]) {
              final boolean[] b = (boolean[]) a;
              for (int i = b.length; (--i) >= 0;) {
                b[i] = random.nextBoolean();
              }
            } else {
              if (a instanceof float[]) {
                final float[] b = (float[]) a;
                for (int i = b.length; (--i) >= 0;) {
                  b[i] = (random.nextBoolean()
                      ? random.nextFloat() : random.nextLong());
                }
              } else {
                if (a instanceof short[]) {
                  final short[] b = (short[]) a;
                  for (int i = b.length; (--i) >= 0;) {
                    b[i] =
                        ((short) (random.nextInt() & (0xffff)));
                  }
                } else {
                  if (a instanceof char[]) {
                    final char[] b = (char[]) a;
                    for (int i = b.length; (--i) >= 0;) {
                      for (;;) {
                        final char ch =
                            (char) (random.nextInt() & 0xffff);
                        if (Character.isDefined(ch)) {
                          b[i] = ch;
                          break;
                        }
                      }
                    }
                  } else {
                    throw new UnsupportedOperationException();
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  /**
   * Assert whether one integer value is less or equal to another
   * one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   */
  public static final void assertLessOrEqual(final int a,
      final int b) {
    if (a > b) {
      Assert.fail("Value " + //$NON-NLS-1$
          a + " should be less or equal than " //$NON-NLS-1$
          + b + ", but is not.");//$NON-NLS-1$
    }
  }

  /**
   * Assert whether one integer value is less than another one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   */
  public static final void assertLess(final int a, final int b) {
    if (a >= b) {
      Assert.fail("Value " + //$NON-NLS-1$
          a + " should be less than " //$NON-NLS-1$
          + b + ", but is not.");//$NON-NLS-1$
    }
  }

  /**
   * Assert whether one integer value is greater or equal to
   * another one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   */
  public static final void assertGreaterOrEqual(final int a,
      final int b) {
    if (a < b) {
      Assert.fail("Value " + //$NON-NLS-1$
          a + " should be greater or equal than " //$NON-NLS-1$
          + b + ", but is not.");//$NON-NLS-1$
    }
  }

  /**
   * Assert whether one long value is in a range
   *
   * @param a
   *          the a value
   * @param min
   *          the minimum of the range
   * @param max
   *          the maximum of the range
   */
  public static final void assertInRange(final long a,
      final long min, final long max) {
    if ((a < min) || (a > max)) {
      Assert.fail("Value " + //$NON-NLS-1$
          a + " must be in range " //$NON-NLS-1$
          + min + "..." + max//$NON-NLS-1$
          + ", but is not.");//$NON-NLS-1$
    }
  }

  /**
   * Assert whether one integer value is in a range
   *
   * @param a
   *          the a value
   * @param min
   *          the minimum of the range
   * @param max
   *          the maximum of the range
   */
  public static final void assertInRange(final int a,
      final int min, final int max) {
    if ((a < min) || (a > max)) {
      Assert.fail("Value " + //$NON-NLS-1$
          a + " must be in range " //$NON-NLS-1$
          + min + "..." + max//$NON-NLS-1$
          + ", but is not.");//$NON-NLS-1$
    }
  }

  /**
   * Assert whether one integer value is a valid index into an
   * array or list of length {@code length}
   *
   * @param a
   *          the a value
   * @param length
   *          the length of the array or list
   */
  public static final void assertValidIndex(final int a,
      final int length) {
    if (length <= 0) {
      Assert.fail(//
          "No index can be valid in an array of length 0, and neither can " //$NON-NLS-1$
              + a);
    }
    TestTools.assertInRange(a, 0, length - 1);
  }

  /**
   * Assert whether one integer value is greater than another one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   */
  public static final void assertGreater(final int a,
      final int b) {
    if (a <= b) {
      Assert.fail("Value " + //$NON-NLS-1$
          a + " should be greater than " //$NON-NLS-1$
          + b + ", but is not.");//$NON-NLS-1$
    }
  }

  /**
   * Assert whether one integer value is less or equal to another
   * one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   */
  public static final void assertLessOrEqual(final long a,
      final long b) {
    if (a > b) {
      Assert.fail("Value " + //$NON-NLS-1$
          a + " should be less or equal than " //$NON-NLS-1$
          + b + ", but is not.");//$NON-NLS-1$
    }
  }

  /**
   * Assert whether one integer value is less than another one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   */
  public static final void assertLess(final long a,
      final long b) {
    if (a >= b) {
      Assert.fail("Value " + //$NON-NLS-1$
          a + " should be less than " //$NON-NLS-1$
          + b + ", but is not.");//$NON-NLS-1$
    }
  }

  /**
   * Assert whether one integer value is greater or equal to
   * another one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   */
  public static final void assertGreaterOrEqual(final long a,
      final long b) {
    if (a < b) {
      Assert.fail("Value " + //$NON-NLS-1$
          a + " should be greater or equal than " //$NON-NLS-1$
          + b + ", but is not.");//$NON-NLS-1$
    }
  }

  /**
   * Assert whether one integer value is greater than another one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   */
  public static final void assertGreater(final long a,
      final long b) {
    if (a <= b) {
      Assert.fail("Value " + //$NON-NLS-1$
          a + " should be greater than " //$NON-NLS-1$
          + b + ", but is not.");//$NON-NLS-1$
    }
  }

  /**
   * check the values
   *
   * @param a
   *          the first value
   * @param b
   *          the second value
   * @param epsilon
   *          the epsilon
   */
  private static final void check(final double a, final double b,
      final double epsilon) {
    if (Double.isNaN(a) || Double.isNaN(b)
        || Double.isNaN(epsilon) || (epsilon < 0d)
        || Double.isFinite(epsilon)) {
      Assert.fail("Invalid test (" //$NON-NLS-1$
          + a + ", " + //$NON-NLS-1$
          b + ", " + //$NON-NLS-1$
          epsilon + ")."); //$NON-NLS-1$
    }
  }

  /**
   * Assert whether one floating point value is less or equal to
   * another one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   * @param epsilon
   *          the permissible tolerance
   */
  public static final void assertLessOrEqual(final double a,
      final double b, final double epsilon) {
    TestTools.check(a, b, epsilon);
    if (a > b) {
      final double border = b + epsilon;
      if (a > border) {
        Assert.fail("Value " + //$NON-NLS-1$
            a + " should be less or equal than " //$NON-NLS-1$
            + b + ", but is not - it exceeds the tolerance "//$NON-NLS-1$
            + epsilon + ", which has led to border value "//$NON-NLS-1$
            + border);
      }
    }
  }

  /**
   * Assert whether one floating point value is less than another
   * one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   * @param epsilon
   *          the permissible tolerance
   */
  public static final void assertLess(final double a,
      final double b, final double epsilon) {
    TestTools.check(a, b, epsilon);
    if (a >= b) {
      final double border = b + epsilon;
      if (a >= border) {
        Assert.fail("Value " + //$NON-NLS-1$
            a + " should be less than " //$NON-NLS-1$
            + b + ", but is not - it exceeds the tolerance "//$NON-NLS-1$
            + epsilon + ", which has led to border value "//$NON-NLS-1$
            + border);
      }
    }
  }

  /**
   * Assert whether one floating point value is greater or equal
   * to another one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   * @param epsilon
   *          the permissible tolerance
   */
  public static final void assertGreaterOrEqual(final double a,
      final double b, final double epsilon) {
    TestTools.check(a, b, epsilon);
    if (a < b) {
      final double border = b - epsilon;
      if (a < border) {
        Assert.fail("Value " + //$NON-NLS-1$
            a + " should be greater or equal than " //$NON-NLS-1$
            + b + " - it exceeds the tolerance "//$NON-NLS-1$
            + epsilon + ", which has led to border value "//$NON-NLS-1$
            + border);
      }
    }
  }

  /**
   * Assert whether one floating point value is greater than
   * another one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   * @param epsilon
   *          the permissible tolerance
   */
  public static final void assertGreater(final double a,
      final double b, final double epsilon) {
    TestTools.check(a, b, epsilon);
    if (a <= b) {
      final double border = b - epsilon;
      if (a <= border) {
        Assert.fail("Value " + //$NON-NLS-1$
            a + " should be greater than " //$NON-NLS-1$
            + b + ", but is not - it exceeds the tolerance "//$NON-NLS-1$
            + epsilon + ", which has led to border value "//$NON-NLS-1$
            + border);
      }
    }
  }

  /**
   * check the values
   *
   * @param a
   *          the first value
   * @param b
   *          the second value
   */
  private static final void check(final double a,
      final double b) {
    if (Double.isNaN(a) || Double.isNaN(b)) {
      Assert.fail("Invalid test (" //$NON-NLS-1$
          + a + ", " + //$NON-NLS-1$
          b + ")."); //$NON-NLS-1$
    }
  }

  /**
   * Assert whether one floating point value is less or equal to
   * another one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   */
  public static final void assertLessOrEqual(final double a,
      final double b) {
    TestTools.check(a, b);
    if (a > b) {
      Assert.fail("Value " + //$NON-NLS-1$
          a + " should be less or equal than " //$NON-NLS-1$
          + b + ", but is not.");//$NON-NLS-1$
    }
  }

  /**
   * Assert whether one floating point value is less than another
   * one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   */
  public static final void assertLess(final double a,
      final double b) {
    TestTools.check(a, b);
    if (a >= b) {
      Assert.fail("Value " + //$NON-NLS-1$
          a + " should be less than " //$NON-NLS-1$
          + b + ", but is not.");//$NON-NLS-1$
    }
  }

  /**
   * Assert whether one floating point value is greater or equal
   * to another one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   */
  public static final void assertGreaterOrEqual(final double a,
      final double b) {
    TestTools.check(a, b);
    if (a < b) {
      Assert.fail("Value " + //$NON-NLS-1$
          a + " should be greater or equal than " //$NON-NLS-1$
          + b + ", but is not.");//$NON-NLS-1$
    }
  }

  /**
   * Assert whether one floating point value is greater than
   * another one
   *
   * @param a
   *          the a value
   * @param b
   *          the b value
   */
  public static final void assertGreater(final double a,
      final double b) {
    TestTools.check(a, b);
    if (a <= b) {
      Assert.fail("Value " + //$NON-NLS-1$
          a + " should be greater than " //$NON-NLS-1$
          + b + ", but is not.");//$NON-NLS-1$
    }
  }

  /**
   * assert that all elements in a {@code boolean[]} array are
   * {@code v}.
   *
   * @param v
   *          the value to compare with
   * @param a
   *          the array
   */
  public static final void assertAllEquals(final boolean v,
      final boolean[] a) {
    ok: {
      for (final boolean z : a) {
        if (z != v) {
          break ok;
        }
      }
      return;
    }
    Assert.fail("some elements in " + //$NON-NLS-1$
        Arrays.toString(a) + " are not "//$NON-NLS-1$ s
        + v);
  }

  /**
   * assert that all elements in a {@code int[]} array are
   * {@code v}.
   *
   * @param v
   *          the value
   * @param a
   *          the array
   */
  public static final void assertAllEquals(final int v,
      final int[] a) {
    ok: {
      for (final int z : a) {
        if (z != v) {
          break ok;
        }
      }
      return;
    }
    Assert.fail("some elements in " + //$NON-NLS-1$
        Arrays.toString(a) + " are not " + v);//$NON-NLS-1$ s
  }

  /**
   * check whether a value is finite
   *
   * @param d
   *          the value that should be finite
   */
  public static final void assertFinite(final double d) {
    if (!(Double.isFinite(d))) {
      Assert.fail(d + " is not finite."); //$NON-NLS-1$
    }
  }
}

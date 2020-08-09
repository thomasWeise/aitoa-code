package aitoa.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import aitoa.TestTools;

/**
 * A test the utility methods in {@link RandomUtils}
 */
public class RandomUtilsTest {
  /** create */
  public RandomUtilsTest() {
    super();
  }

  /**
   * test the method
   * {@link RandomUtils#uniqueRandomSeeds(String, int)}
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 100000)
  public void testUniqueRandomSeeds() {
    final Random random = ThreadLocalRandom.current();
    final HashSet<Long> oldSeeds = new HashSet<>();
    final HashSet<Long> newSeeds = new HashSet<>();

    String base = ""; //$NON-NLS-1$

    for (int i = 0; i < 100; i++) {
      base += (char) ('A' + random.nextInt(26));
      oldSeeds.clear();
      newSeeds.clear();

      for (int j = 1; j < 200; j++) {
        final long[] seeds =
            RandomUtils.uniqueRandomSeeds(base, j);
        Assert.assertEquals(j, seeds.length);
// same result for two calls
        Assert.assertArrayEquals(seeds,
            RandomUtils.uniqueRandomSeeds(base, j));

        newSeeds.clear();
        for (final long l : seeds) {
          newSeeds.add(Long.valueOf(l));
        }
        Assert.assertEquals(j, newSeeds.size());
        Assert.assertEquals(oldSeeds.size(),
            newSeeds.size() - 1);

        newSeeds.removeAll(oldSeeds);
        Assert.assertEquals(1, newSeeds.size());
        oldSeeds.addAll(newSeeds);
      }
    }
  }

  /**
   * test the method
   * {@link RandomUtils#shuffle(Random, int[], int, int)}
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 100000)
  public void testIntShuffle() {
    final Random random = ThreadLocalRandom.current();
    long total = 0L;
    long diff = 0L;

    for (int length = 1; length < 100; length++) {
      final int[] array = new int[length];
      for (int j = array.length; (--j) >= 0;) {
        array[j] = j;
      }
      final int[] array2 = new int[length];
      final boolean[] done = new boolean[length];

      for (int start = 0; start < length; start++) {
        for (int count = 0; count <= (length - start); count++) {
          System.arraycopy(array, 0, array2, 0, length);
          RandomUtils.shuffle(random, array, start, count);
          if (count > 0) {
            total++;
          }

          Arrays.fill(done, false);
          int i = 0;

          for (; i < start; i++) {
            final int x = array[i];
            Assert.assertEquals(array2[i], x);
            Assert.assertFalse(done[x]);
            done[x] = true;
          }
          final int end = start + count;
          boolean diffi = false;
          for (; i < end; i++) {
            final int x = array[i];
            if (array2[i] != x) {
              diffi = true;
            }
            Assert.assertFalse(done[x]);
            done[x] = true;
          }
          if (diffi) {
            diff++;
          }

          for (; i < length; i++) {
            final int x = array[i];
            Assert.assertEquals(array2[i], x);
            Assert.assertFalse(done[x]);
            done[x] = true;
          }

        }
      }
    }

    TestTools.assertGreater(total, 10);
    TestTools.assertGreater(diff, total - (total >>> 4L));
  }

  /**
   * test the method
   * {@link RandomUtils#shuffle(Random, int[], int, int)}
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 100000)
  public void testIntShuffle2() {
    RandomUtilsTest.permShuffleTest(2);
  }

  /**
   * test the method
   * {@link RandomUtils#shuffle(Random, int[], int, int)}
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 100000)
  public void testIntShuffle3() {
    RandomUtilsTest.permShuffleTest(3);
  }

  /**
   * test the method
   * {@link RandomUtils#shuffle(Random, int[], int, int)}
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 100000)
  public void testIntShuffle4() {
    RandomUtilsTest.permShuffleTest(4);
  }

  /**
   * test the method
   * {@link RandomUtils#shuffle(Random, int[], int, int)}
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 100000)
  public void testIntShuffle5() {
    RandomUtilsTest.permShuffleTest(5);
  }

  /**
   * test the method
   * {@link RandomUtils#shuffle(Random, int[], int, int)}
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 100000)
  public void testIntShuffle6() {
    RandomUtilsTest.permShuffleTest(6);
  }

  /**
   * test the method
   * {@link RandomUtils#shuffle(Random, int[], int, int)}
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 100000)
  public void testIntShuffle7() {
    RandomUtilsTest.permShuffleTest(7);
  }

  /**
   * test the method
   * {@link RandomUtils#shuffle(Random, int[], int, int)}
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 100000)
  public void testIntShuffle8() {
    RandomUtilsTest.permShuffleTest(8);
  }

  /**
   * test the shuffling of arrays of length {@code n}
   *
   * @param pN
   *          the array length
   */
  private static final void permShuffleTest(final int pN) {
    final int[] perm = new int[pN];
    final int[] temp1 = new int[pN];
    final int[] temp2 = new int[pN];
    int count = 1;
    for (int i = pN; i >= 1; i--) {
      count = Math.multiplyExact(count, i);
    }
    final Random random = ThreadLocalRandom.current();

    final int[] counters = new int[count];

    for (int k = Math.multiplyExact(64, count); (--k) >= 0;) {
      for (int i = perm.length; (--i) >= 0;) {
        perm[i] = i;
      }
      RandomUtils.shuffle(random, perm, 0, perm.length);
      ++counters[RandomUtilsTest.permToInt(perm, temp1, temp2)];
    }

    for (final int v : counters) {
      TestTools.assertGreater(v, 8);
    }
  }

  /**
   * convert a permutation to a number
   *
   * @param pPerm
   *          the permutation
   * @param pTemp1
   *          the first temporary array (same length as
   *          {@code perm})
   * @param pTemp2
   *          the second temporary array (same length as
   *          {@code perm})
   * @return the number representation see
   *         https://stackoverflow.com/a/24689277
   */
  private static final int permToInt(final int[] pPerm,
      final int[] pTemp1, final int[] pTemp2) {
    int i, k = 0, m = 1;
    final int n = pPerm.length;

    for (i = 0; i < n; i++) {
      pTemp1[i] = i;
      pTemp2[i] = i;
    }

    for (i = 0; i < (n - 1); i++) {
      k += m * pTemp1[pPerm[i]];
      m = m * (n - i);
      pTemp1[pTemp2[n - i - 1]] = pTemp1[pPerm[i]];
      pTemp2[pTemp1[pPerm[i]]] = pTemp2[n - i - 1];
    }

    return k;
  }

  /**
   * test the method
   * {@link RandomUtils#shuffle(Random, Object[], int, int)}
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 100000)
  public void testObjectShuffle() {
    final Random random = ThreadLocalRandom.current();
    long total = 0L;
    long diff = 0L;

    for (int length = 1; length < 100; length++) {
      final Integer[] array = new Integer[length];
      for (int j = array.length; (--j) >= 0;) {
        array[j] = Integer.valueOf(j);
      }
      final Integer[] array2 = new Integer[length];
      final boolean[] done = new boolean[length];

      for (int start = 0; start < length; start++) {
        for (int count = 0; count <= (length - start); count++) {
          System.arraycopy(array, 0, array2, 0, length);
          RandomUtils.shuffle(random, array, start, count);
          if (count > 0) {
            total++;
          }

          Arrays.fill(done, false);
          int i = 0;

          for (; i < start; i++) {
            final int x = array[i].intValue();
            Assert.assertEquals(array2[i].intValue(), x);
            Assert.assertFalse(done[x]);
            done[x] = true;
          }
          final int end = start + count;
          boolean diffi = false;
          for (; i < end; i++) {
            final int x = array[i].intValue();
            if (array2[i].intValue() != x) {
              diffi = true;
            }
            Assert.assertFalse(done[x]);
            done[x] = true;
          }
          if (diffi) {
            diff++;
          }

          for (; i < length; i++) {
            final int x = array[i].intValue();
            Assert.assertEquals(array2[i].intValue(), x);
            Assert.assertFalse(done[x]);
            done[x] = true;
          }

        }
      }
    }

    TestTools.assertGreater(total, 10);
    TestTools.assertGreater(diff, total - (total >>> 4L));
  }

  /**
   * Test whether the internal uniformFrom0ToNminus1 function
   * works correct
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testUniformFrom0ToNminus1() {
    final Random r = ThreadLocalRandom.current();
    for (int N = 1; N < 100; N++) {
      final int[] count = new int[N];
      for (int i = Math.max(10000, N * 1000); (--i) >= 0;) {
        final long l = RandomUtils.uniformFrom0ToNminus1(r, N);
        TestTools.assertInRange(l, 0, N - 1L);
        ++count[(int) l];
      }

      int min = Integer.MAX_VALUE;
      int max = Integer.MIN_VALUE;
      for (final int i : count) {
        if (i < min) {
          min = i;
        }
        if (i > max) {
          max = i;
        }
      }
      TestTools.assertGreater(min * 5, max << 1);
    }
  }

  /**
   * Test whether the internal uniformFromMtoN function works
   * correct
   *
   * @param pM
   *          the lower bound
   * @param pN
   *          the upper bound
   */
  private static final void
      testUniformFromMtoNSmall(final int pM, final int pN) {
    final Random r = ThreadLocalRandom.current();
    final int[] count = new int[(pN - pM) + 1];
    for (int i = Math.max(10000, pN * 1000); (--i) >= 0;) {
      final long l = RandomUtils.uniformFromMtoN(r, pM, pN);
      TestTools.assertInRange(l, pM, pN);
      ++count[(int) (l - pM)];
    }

    int min = Integer.MAX_VALUE;
    int max = Integer.MIN_VALUE;
    for (final int i : count) {
      if (i < min) {
        min = i;
      }
      if (i > max) {
        max = i;
      }
    }
    TestTools.assertGreater(min * 5, max << 1);
  }

  /**
   * test uniformFromMtoN with 0 to 10
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testUniformFrom0To10() {
    RandomUtilsTest.testUniformFromMtoNSmall(0, 10);
  }

  /**
   * test uniformFromMtoN with -10 to 10
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testUniformFromMinus10To10() {
    RandomUtilsTest.testUniformFromMtoNSmall(-10, 10);
  }

  /**
   * test uniformFromMtoN with -10 to 0
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testUniformFromMinus10To0() {
    RandomUtilsTest.testUniformFromMtoNSmall(-10, 0);
  }

  /**
   * test uniformFromMtoN with 0 to 1
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testUniformFromMinus0To1() {
    RandomUtilsTest.testUniformFromMtoNSmall(0, 1);
  }

  /**
   * test uniformFromMtoN with 20 to 31
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testUniformFromMinus20To31() {
    RandomUtilsTest.testUniformFromMtoNSmall(20, 31);
  }

  /**
   * test uniformFromMtoN with 0 to 0
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testUniformFromMinus0To0() {
    final Random r = ThreadLocalRandom.current();
    for (long l = -10; l <= 10; l++) {
      for (int i = 0; i < 100; i++) {
        Assert.assertEquals(l,
            RandomUtils.uniformFromMtoN(r, l, l));
      }
    }
  }

  /**
   * Test whether the internal uniformFromMtoN function works
   * correct
   *
   * @param pM
   *          the lower bound
   * @param pN
   *          the upper bound
   */
  private static final void testUniformFromMtoNBig(final long pM,
      final long pN) {
    final Random r = ThreadLocalRandom.current();
    for (int i = 10000; (--i) >= 0;) {
      TestTools.assertInRange(
          RandomUtils.uniformFromMtoN(r, pM, pN), pM, pN);
    }
  }

  /**
   * test uniformFromMtoN with Long.MIN_VALUE to 0
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testUniformFromMinusMinTo0() {
    RandomUtilsTest.testUniformFromMtoNBig(Long.MIN_VALUE, 0);
  }

  /**
   * test uniformFromMtoN with Long.MIN_VALUE to Long.MAX_VALUE
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testUniformFromMinusMinToMax() {
    RandomUtilsTest.testUniformFromMtoNBig(Long.MIN_VALUE,
        Long.MAX_VALUE);
  }

  /**
   * test uniformFromMtoN with Long.MIN_VALUE/2 to
   * Long.MAX_VALUE/2
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testUniformFromMinusMin2ToMax2() {
    RandomUtilsTest.testUniformFromMtoNBig(Long.MIN_VALUE / 2,
        Long.MAX_VALUE / 2);
  }

  /**
   * test uniformFromMtoN with Long.MIN_VALUE to 0
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testUniformFromMinusMinTo0Big() {
    final boolean[] found = new boolean[10];
    final long mi = Integer.MAX_VALUE + 1L;
    final long bound = mi + found.length;
    int total = found.length;
    for (;;) {
      final long l = RandomUtils.uniformFrom0ToNminus1(
          ThreadLocalRandom.current(), bound) - mi;
      if (l >= 0L) {
        final int i = (int) l;
        if (found[i]) {
          continue;
        }
        found[i] = true;
        if ((--total) <= 0) {
          return;
        }
      }
    }
  }
}

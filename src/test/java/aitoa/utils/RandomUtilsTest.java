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
}

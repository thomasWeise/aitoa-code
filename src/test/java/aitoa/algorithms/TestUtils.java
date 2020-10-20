package aitoa.algorithms;

import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import aitoa.TestTools;
import aitoa.structure.Record;

/** Test the algorithm utils. */
public class TestUtils {

  /** test the quality based clearing */
  @SuppressWarnings({ "static-method", "rawtypes", "unchecked" })
  @Test(timeout = 3600000)
  public final void testQualityBasedClearing() {
    final ThreadLocalRandom random = ThreadLocalRandom.current();
    for (int test = 10000; (--test) >= 0;) {

      final Record[] source =
          new Record[random.nextInt(1, 1000)];
      for (int i = source.length; (--i) >= 0;) {
        source[i] = new Record(random, random.nextInt(0, 20));
      }
      final Record[] compare = source.clone();
      final int mu = (source.length > 1)
          ? random.nextInt(1, source.length) : 1;
      final int u = Utils.qualityBasedClearing(source, mu);
      TestTools.assertGreaterOrEqual(u, 1);
      TestTools.assertLessOrEqual(u, mu);

      final HashSet<Integer> quality = new HashSet<>(u);
      int lastQuality = Integer.MIN_VALUE;
      for (int i = 0; i < u; i++) {
        final int currentQuality = (int) (source[i].quality);
        TestTools.assertGreater(currentQuality, lastQuality);
        lastQuality = currentQuality;
        Assert.assertTrue(quality.add(//
            Integer.valueOf(currentQuality)));
      }
      Assert.assertEquals(u, quality.size());
      for (int i = compare.length; (--i) >= u;) {
        final int currentQuality = (int) (source[i].quality);
        if (quality.add(Integer.valueOf(currentQuality))) {
          TestTools.assertGreater(currentQuality, lastQuality);
        }
      }

      for (int i = compare.length; (--i) >= 0;) {
        int count = 0;
        final Record c = compare[i];
        for (int j = source.length; (--j) >= 0;) {
          if (c == source[j]) {
            ++count;
          }
        }
        Assert.assertEquals(1, count);
      }
    }
  }
}

package aitoa.bookExamples.bitStrings;

import java.util.Random;

/**
 * a performance comparison of the nullary bit string operator
 */
public class PerformanceComparisonBitStringNullary {

  /**
   * do the original version
   *
   * @param dest
   *          the destination
   * @param random
   *          the random number generator
   */
  private static final void __apply_orig(final boolean[] dest,
      final Random random) {
    for (int i = dest.length; (--i) >= 0;) {
      dest[i] = random.nextBoolean();
    }
  }

  /**
   * do the improved version
   *
   * @param dest
   *          the destination
   * @param random
   *          the random number generator
   */
  private static final void __apply_new(final boolean[] dest,
      final Random random) {
    int i = dest.length - 1;
    for (;;) {
      final long bits = random.nextLong();
      dest[i] = ((bits & 0x1L) != 0L);
      if (i == 0) {
        return;
      }
      dest[i - 1] = ((bits & 0x2L) != 0L);
      if (i == 1) {
        return;
      }
      dest[i - 2] = ((bits & 0x4L) != 0L);
      if (i == 2) {
        return;
      }
      dest[i - 3] = ((bits & 0x8L) != 0L);
      if (i == 3) {
        return;
      }
      dest[i - 4] = ((bits & 0x10L) != 0L);
      if (i == 4) {
        return;
      }
      dest[i - 5] = ((bits & 0x20L) != 0L);
      if (i == 5) {
        return;
      }
      dest[i - 6] = ((bits & 0x40L) != 0L);
      if (i == 6) {
        return;
      }
      dest[i - 7] = ((bits & 0x80L) != 0L);
      if (i == 7) {
        return;
      }
      dest[i - 8] = ((bits & 0x100L) != 0L);
      if (i == 8) {
        return;
      }
      dest[i - 9] = ((bits & 0x200L) != 0L);
      if (i == 9) {
        return;
      }
      dest[i - 10] = ((bits & 0x400L) != 0L);
      if (i == 10) {
        return;
      }
      dest[i - 11] = ((bits & 0x800L) != 0L);
      if (i == 11) {
        return;
      }
      dest[i - 12] = ((bits & 0x1000L) != 0L);
      if (i == 12) {
        return;
      }
      dest[i - 13] = ((bits & 0x2000L) != 0L);
      if (i == 13) {
        return;
      }
      dest[i - 14] = ((bits & 0x4000L) != 0L);
      if (i == 14) {
        return;
      }
      dest[i - 15] = ((bits & 0x8000L) != 0L);
      if (i == 15) {
        return;
      }
      dest[i - 16] = ((bits & 0x10000L) != 0L);
      if (i == 16) {
        return;
      }
      dest[i - 17] = ((bits & 0x20000L) != 0L);
      if (i == 17) {
        return;
      }
      dest[i - 18] = ((bits & 0x40000L) != 0L);
      if (i == 18) {
        return;
      }
      dest[i - 19] = ((bits & 0x80000L) != 0L);
      if (i == 19) {
        return;
      }
      dest[i - 20] = ((bits & 0x100000L) != 0L);
      if (i == 20) {
        return;
      }
      dest[i - 21] = ((bits & 0x200000L) != 0L);
      if (i == 21) {
        return;
      }
      dest[i - 22] = ((bits & 0x400000L) != 0L);
      if (i == 22) {
        return;
      }
      dest[i - 23] = ((bits & 0x800000L) != 0L);
      if (i == 23) {
        return;
      }
      dest[i - 24] = ((bits & 0x1000000L) != 0L);
      if (i == 24) {
        return;
      }
      dest[i - 25] = ((bits & 0x2000000L) != 0L);
      if (i == 25) {
        return;
      }
      dest[i - 26] = ((bits & 0x4000000L) != 0L);
      if (i == 26) {
        return;
      }
      dest[i - 27] = ((bits & 0x8000000L) != 0L);
      if (i == 27) {
        return;
      }
      dest[i - 28] = ((bits & 0x10000000L) != 0L);
      if (i == 28) {
        return;
      }
      dest[i - 29] = ((bits & 0x20000000L) != 0L);
      if (i == 29) {
        return;
      }
      dest[i - 30] = ((bits & 0x40000000L) != 0L);
      if (i == 30) {
        return;
      }
      dest[i - 31] = ((bits & 0x80000000L) != 0L);
      if (i == 31) {
        return;
      }
      dest[i - 32] = ((bits & 0x100000000L) != 0L);
      if (i == 32) {
        return;
      }
      dest[i - 33] = ((bits & 0x200000000L) != 0L);
      if (i == 33) {
        return;
      }
      dest[i - 34] = ((bits & 0x400000000L) != 0L);
      if (i == 34) {
        return;
      }
      dest[i - 35] = ((bits & 0x800000000L) != 0L);
      if (i == 35) {
        return;
      }
      dest[i - 36] = ((bits & 0x1000000000L) != 0L);
      if (i == 36) {
        return;
      }
      dest[i - 37] = ((bits & 0x2000000000L) != 0L);
      if (i == 37) {
        return;
      }
      dest[i - 38] = ((bits & 0x4000000000L) != 0L);
      if (i == 38) {
        return;
      }
      dest[i - 39] = ((bits & 0x8000000000L) != 0L);
      if (i == 39) {
        return;
      }
      dest[i - 40] = ((bits & 0x10000000000L) != 0L);
      if (i == 40) {
        return;
      }
      dest[i - 41] = ((bits & 0x20000000000L) != 0L);
      if (i == 41) {
        return;
      }
      dest[i - 42] = ((bits & 0x40000000000L) != 0L);
      if (i == 42) {
        return;
      }
      dest[i - 43] = ((bits & 0x80000000000L) != 0L);
      if (i == 43) {
        return;
      }
      dest[i - 44] = ((bits & 0x100000000000L) != 0L);
      if (i == 44) {
        return;
      }
      dest[i - 45] = ((bits & 0x200000000000L) != 0L);
      if (i == 45) {
        return;
      }
      dest[i - 46] = ((bits & 0x400000000000L) != 0L);
      if (i == 46) {
        return;
      }
      dest[i - 47] = ((bits & 0x800000000000L) != 0L);
      if (i == 47) {
        return;
      }
      dest[i - 48] = ((bits & 0x1000000000000L) != 0L);
      if (i == 48) {
        return;
      }
      dest[i - 49] = ((bits & 0x2000000000000L) != 0L);
      if (i == 49) {
        return;
      }
      dest[i - 50] = ((bits & 0x4000000000000L) != 0L);
      if (i == 50) {
        return;
      }
      dest[i - 51] = ((bits & 0x8000000000000L) != 0L);
      if (i == 51) {
        return;
      }
      dest[i - 52] = ((bits & 0x10000000000000L) != 0L);
      if (i == 52) {
        return;
      }
      dest[i - 53] = ((bits & 0x20000000000000L) != 0L);
      if (i == 53) {
        return;
      }
      dest[i - 54] = ((bits & 0x40000000000000L) != 0L);
      if (i == 54) {
        return;
      }
      dest[i - 55] = ((bits & 0x80000000000000L) != 0L);
      if (i == 55) {
        return;
      }
      dest[i - 56] = ((bits & 0x100000000000000L) != 0L);
      if (i == 56) {
        return;
      }
      dest[i - 57] = ((bits & 0x200000000000000L) != 0L);
      if (i == 57) {
        return;
      }
      dest[i - 58] = ((bits & 0x400000000000000L) != 0L);
      if (i == 58) {
        return;
      }
      dest[i - 59] = ((bits & 0x800000000000000L) != 0L);
      if (i == 59) {
        return;
      }
      dest[i - 60] = ((bits & 0x1000000000000000L) != 0L);
      if (i == 60) {
        return;
      }
      dest[i - 61] = ((bits & 0x2000000000000000L) != 0L);
      if (i == 61) {
        return;
      }
      dest[i - 62] = ((bits & 0x4000000000000000L) != 0L);
      if (i == 62) {
        return;
      }
      dest[i - 63] = ((bits & 0x8000000000000000L) != 0L);
      if (i == 63) {
        return;
      }
      i -= 64;
    }
  }

  /**
   * the main routine
   *
   * @param args
   *          ignored
   */
  public static final void main(final String[] args) {
    final boolean[] array = new boolean[2013];
    final Random random = new Random();

    final int times = 1000000;

    for (int i = times; (--i) >= 0;) {
      PerformanceComparisonBitStringNullary.__apply_orig(array,
          random);
      PerformanceComparisonBitStringNullary.__apply_new(array,
          random);
    }

    final long t1 = System.nanoTime();
    for (int i = times; (--i) >= 0;) {
      PerformanceComparisonBitStringNullary.__apply_orig(array,
          random);
    }
    final long t2 = System.nanoTime();
    for (int i = times; (--i) >= 0;) {
      PerformanceComparisonBitStringNullary.__apply_new(array,
          random);
    }
    final long t3 = System.nanoTime();
    for (int i = times; (--i) >= 0;) {
      PerformanceComparisonBitStringNullary.__apply_orig(array,
          random);
    }
    final long t4 = System.nanoTime();
    for (int i = times; (--i) >= 0;) {
      PerformanceComparisonBitStringNullary.__apply_new(array,
          random);
    }
    final long t5 = System.nanoTime();

    final long t_orig = Math.min(t4 - t3, t2 - t1);
    final long t_new = Math.min(t5 - t4, t3 - t2);

    System.out.println("orig: " + t_orig); //$NON-NLS-1$
    System.out.println(" new: " + t_new); //$NON-NLS-1$

    System.out.println();
    System.out.println();
    System.out.println();
    System.out.println("int i = dest.length - 1;");//$NON-NLS-1$
    System.out.println("for(;;) {");//$NON-NLS-1$
    System.out.println("final long bits = random.nextLong();");//$NON-NLS-1$
    int i = 0;
    long flag = 1;
    for (;;) {
      System.out.print("dest[i");//$NON-NLS-1$
      if (i > 0) {
        System.out.print('-');
        System.out.print(i);
      }
      System.out.print("] = ((bits & 0x");//$NON-NLS-1$
      System.out.print(Long.toHexString(flag));
      System.out.println("L) != 0L);");//$NON-NLS-1$
      System.out.print("if(i == ");//$NON-NLS-1$
      System.out.print(i);
      System.out.println(") { return; }");//$NON-NLS-1$
      i = i + 1;
      flag <<= 1L;
      if (i == 64) {
        break;
      }
    }

    System.out.println("i -= 64;");//$NON-NLS-1$
    System.out.println('}');// $NON-NLS-1$
  }

}

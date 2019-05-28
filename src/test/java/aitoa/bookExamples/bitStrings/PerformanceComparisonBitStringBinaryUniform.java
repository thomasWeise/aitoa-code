package aitoa.bookExamples.bitStrings;

import java.util.Random;

/**
 * a performance comparison of the nullary bit string operator
 */
public class PerformanceComparisonBitStringBinaryUniform {

  /**
   * do the original version
   *
   * @param x0
   *          the first bit string
   * @param x1
   *          the second bit string
   * @param dest
   *          the destination
   * @param random
   *          the random number generator
   */
  private static final void __apply_orig(final boolean[] x0,
      final boolean[] x1, final boolean[] dest,
      final Random random) {
    for (int i = dest.length; (--i) >= 0;) {
      dest[i] = random.nextBoolean() ? x0[i] : x1[i];
    }
  }

  /**
   * do the improved version
   *
   * @param x0
   *          the first bit string
   * @param x1
   *          the second bit string
   * @param dest
   *          the destination
   * @param random
   *          the random number generator
   */
  private static final void __apply_new_1(final boolean[] x0,
      final boolean[] x1, final boolean[] dest,
      final Random random) {

    int i = dest.length - 1;
    for (;;) {
      final long bits = random.nextLong();
      dest[i] = (((bits & 0x1L) != 0L) ? x0[i] : x1[i]);
      if (i == 0) {
        return;
      }
      dest[i - 1] =
          (((bits & 0x2L) != 0L) ? x0[i - 1] : x1[i - 1]);
      if (i == 1) {
        return;
      }
      dest[i - 2] =
          (((bits & 0x4L) != 0L) ? x0[i - 2] : x1[i - 2]);
      if (i == 2) {
        return;
      }
      dest[i - 3] =
          (((bits & 0x8L) != 0L) ? x0[i - 3] : x1[i - 3]);
      if (i == 3) {
        return;
      }
      dest[i - 4] =
          (((bits & 0x10L) != 0L) ? x0[i - 4] : x1[i - 4]);
      if (i == 4) {
        return;
      }
      dest[i - 5] =
          (((bits & 0x20L) != 0L) ? x0[i - 5] : x1[i - 5]);
      if (i == 5) {
        return;
      }
      dest[i - 6] =
          (((bits & 0x40L) != 0L) ? x0[i - 6] : x1[i - 6]);
      if (i == 6) {
        return;
      }
      dest[i - 7] =
          (((bits & 0x80L) != 0L) ? x0[i - 7] : x1[i - 7]);
      if (i == 7) {
        return;
      }
      dest[i - 8] =
          (((bits & 0x100L) != 0L) ? x0[i - 8] : x1[i - 8]);
      if (i == 8) {
        return;
      }
      dest[i - 9] =
          (((bits & 0x200L) != 0L) ? x0[i - 9] : x1[i - 9]);
      if (i == 9) {
        return;
      }
      dest[i - 10] =
          (((bits & 0x400L) != 0L) ? x0[i - 10] : x1[i - 10]);
      if (i == 10) {
        return;
      }
      dest[i - 11] =
          (((bits & 0x800L) != 0L) ? x0[i - 11] : x1[i - 11]);
      if (i == 11) {
        return;
      }
      dest[i - 12] =
          (((bits & 0x1000L) != 0L) ? x0[i - 12] : x1[i - 12]);
      if (i == 12) {
        return;
      }
      dest[i - 13] =
          (((bits & 0x2000L) != 0L) ? x0[i - 13] : x1[i - 13]);
      if (i == 13) {
        return;
      }
      dest[i - 14] =
          (((bits & 0x4000L) != 0L) ? x0[i - 14] : x1[i - 14]);
      if (i == 14) {
        return;
      }
      dest[i - 15] =
          (((bits & 0x8000L) != 0L) ? x0[i - 15] : x1[i - 15]);
      if (i == 15) {
        return;
      }
      dest[i - 16] =
          (((bits & 0x10000L) != 0L) ? x0[i - 16] : x1[i - 16]);
      if (i == 16) {
        return;
      }
      dest[i - 17] =
          (((bits & 0x20000L) != 0L) ? x0[i - 17] : x1[i - 17]);
      if (i == 17) {
        return;
      }
      dest[i - 18] =
          (((bits & 0x40000L) != 0L) ? x0[i - 18] : x1[i - 18]);
      if (i == 18) {
        return;
      }
      dest[i - 19] =
          (((bits & 0x80000L) != 0L) ? x0[i - 19] : x1[i - 19]);
      if (i == 19) {
        return;
      }
      dest[i - 20] =
          (((bits & 0x100000L) != 0L) ? x0[i - 20] : x1[i - 20]);
      if (i == 20) {
        return;
      }
      dest[i - 21] =
          (((bits & 0x200000L) != 0L) ? x0[i - 21] : x1[i - 21]);
      if (i == 21) {
        return;
      }
      dest[i - 22] =
          (((bits & 0x400000L) != 0L) ? x0[i - 22] : x1[i - 22]);
      if (i == 22) {
        return;
      }
      dest[i - 23] =
          (((bits & 0x800000L) != 0L) ? x0[i - 23] : x1[i - 23]);
      if (i == 23) {
        return;
      }
      dest[i - 24] = (((bits & 0x1000000L) != 0L) ? x0[i - 24]
          : x1[i - 24]);
      if (i == 24) {
        return;
      }
      dest[i - 25] = (((bits & 0x2000000L) != 0L) ? x0[i - 25]
          : x1[i - 25]);
      if (i == 25) {
        return;
      }
      dest[i - 26] = (((bits & 0x4000000L) != 0L) ? x0[i - 26]
          : x1[i - 26]);
      if (i == 26) {
        return;
      }
      dest[i - 27] = (((bits & 0x8000000L) != 0L) ? x0[i - 27]
          : x1[i - 27]);
      if (i == 27) {
        return;
      }
      dest[i - 28] = (((bits & 0x10000000L) != 0L) ? x0[i - 28]
          : x1[i - 28]);
      if (i == 28) {
        return;
      }
      dest[i - 29] = (((bits & 0x20000000L) != 0L) ? x0[i - 29]
          : x1[i - 29]);
      if (i == 29) {
        return;
      }
      dest[i - 30] = (((bits & 0x40000000L) != 0L) ? x0[i - 30]
          : x1[i - 30]);
      if (i == 30) {
        return;
      }
      dest[i - 31] = (((bits & 0x80000000L) != 0L) ? x0[i - 31]
          : x1[i - 31]);
      if (i == 31) {
        return;
      }
      dest[i - 32] = (((bits & 0x100000000L) != 0L) ? x0[i - 32]
          : x1[i - 32]);
      if (i == 32) {
        return;
      }
      dest[i - 33] = (((bits & 0x200000000L) != 0L) ? x0[i - 33]
          : x1[i - 33]);
      if (i == 33) {
        return;
      }
      dest[i - 34] = (((bits & 0x400000000L) != 0L) ? x0[i - 34]
          : x1[i - 34]);
      if (i == 34) {
        return;
      }
      dest[i - 35] = (((bits & 0x800000000L) != 0L) ? x0[i - 35]
          : x1[i - 35]);
      if (i == 35) {
        return;
      }
      dest[i - 36] = (((bits & 0x1000000000L) != 0L) ? x0[i - 36]
          : x1[i - 36]);
      if (i == 36) {
        return;
      }
      dest[i - 37] = (((bits & 0x2000000000L) != 0L) ? x0[i - 37]
          : x1[i - 37]);
      if (i == 37) {
        return;
      }
      dest[i - 38] = (((bits & 0x4000000000L) != 0L) ? x0[i - 38]
          : x1[i - 38]);
      if (i == 38) {
        return;
      }
      dest[i - 39] = (((bits & 0x8000000000L) != 0L) ? x0[i - 39]
          : x1[i - 39]);
      if (i == 39) {
        return;
      }
      dest[i - 40] = (((bits & 0x10000000000L) != 0L)
          ? x0[i - 40] : x1[i - 40]);
      if (i == 40) {
        return;
      }
      dest[i - 41] = (((bits & 0x20000000000L) != 0L)
          ? x0[i - 41] : x1[i - 41]);
      if (i == 41) {
        return;
      }
      dest[i - 42] = (((bits & 0x40000000000L) != 0L)
          ? x0[i - 42] : x1[i - 42]);
      if (i == 42) {
        return;
      }
      dest[i - 43] = (((bits & 0x80000000000L) != 0L)
          ? x0[i - 43] : x1[i - 43]);
      if (i == 43) {
        return;
      }
      dest[i - 44] = (((bits & 0x100000000000L) != 0L)
          ? x0[i - 44] : x1[i - 44]);
      if (i == 44) {
        return;
      }
      dest[i - 45] = (((bits & 0x200000000000L) != 0L)
          ? x0[i - 45] : x1[i - 45]);
      if (i == 45) {
        return;
      }
      dest[i - 46] = (((bits & 0x400000000000L) != 0L)
          ? x0[i - 46] : x1[i - 46]);
      if (i == 46) {
        return;
      }
      dest[i - 47] = (((bits & 0x800000000000L) != 0L)
          ? x0[i - 47] : x1[i - 47]);
      if (i == 47) {
        return;
      }
      dest[i - 48] = (((bits & 0x1000000000000L) != 0L)
          ? x0[i - 48] : x1[i - 48]);
      if (i == 48) {
        return;
      }
      dest[i - 49] = (((bits & 0x2000000000000L) != 0L)
          ? x0[i - 49] : x1[i - 49]);
      if (i == 49) {
        return;
      }
      dest[i - 50] = (((bits & 0x4000000000000L) != 0L)
          ? x0[i - 50] : x1[i - 50]);
      if (i == 50) {
        return;
      }
      dest[i - 51] = (((bits & 0x8000000000000L) != 0L)
          ? x0[i - 51] : x1[i - 51]);
      if (i == 51) {
        return;
      }
      dest[i - 52] = (((bits & 0x10000000000000L) != 0L)
          ? x0[i - 52] : x1[i - 52]);
      if (i == 52) {
        return;
      }
      dest[i - 53] = (((bits & 0x20000000000000L) != 0L)
          ? x0[i - 53] : x1[i - 53]);
      if (i == 53) {
        return;
      }
      dest[i - 54] = (((bits & 0x40000000000000L) != 0L)
          ? x0[i - 54] : x1[i - 54]);
      if (i == 54) {
        return;
      }
      dest[i - 55] = (((bits & 0x80000000000000L) != 0L)
          ? x0[i - 55] : x1[i - 55]);
      if (i == 55) {
        return;
      }
      dest[i - 56] = (((bits & 0x100000000000000L) != 0L)
          ? x0[i - 56] : x1[i - 56]);
      if (i == 56) {
        return;
      }
      dest[i - 57] = (((bits & 0x200000000000000L) != 0L)
          ? x0[i - 57] : x1[i - 57]);
      if (i == 57) {
        return;
      }
      dest[i - 58] = (((bits & 0x400000000000000L) != 0L)
          ? x0[i - 58] : x1[i - 58]);
      if (i == 58) {
        return;
      }
      dest[i - 59] = (((bits & 0x800000000000000L) != 0L)
          ? x0[i - 59] : x1[i - 59]);
      if (i == 59) {
        return;
      }
      dest[i - 60] = (((bits & 0x1000000000000000L) != 0L)
          ? x0[i - 60] : x1[i - 60]);
      if (i == 60) {
        return;
      }
      dest[i - 61] = (((bits & 0x2000000000000000L) != 0L)
          ? x0[i - 61] : x1[i - 61]);
      if (i == 61) {
        return;
      }
      dest[i - 62] = (((bits & 0x4000000000000000L) != 0L)
          ? x0[i - 62] : x1[i - 62]);
      if (i == 62) {
        return;
      }
      dest[i - 63] = (((bits & 0x8000000000000000L) != 0L)
          ? x0[i - 63] : x1[i - 63]);
      if (i == 63) {
        return;
      }
      i -= 64;
    }
  }

  /**
   * do the improved version
   *
   * @param x0
   *          the first bit string
   * @param x1
   *          the second bit string
   * @param dest
   *          the destination
   * @param random
   *          the random number generator
   */
  private static final void __apply_new_2(final boolean[] x0,
      final boolean[] x1, final boolean[] dest,
      final Random random) {

    int i = dest.length;
    System.arraycopy(x0, 0, dest, 0, i--);
    for (;;) {
      final long bits = random.nextLong();
      if ((bits & 0x1L) != 0L) {
        dest[i] = x1[i];
      }
      if (i == 0) {
        return;
      }
      if ((bits & 0x2L) != 0L) {
        dest[i - 1] = x1[i - 1];
      }
      if (i == 1) {
        return;
      }
      if ((bits & 0x4L) != 0L) {
        dest[i - 2] = x1[i - 2];
      }
      if (i == 2) {
        return;
      }
      if ((bits & 0x8L) != 0L) {
        dest[i - 3] = x1[i - 3];
      }
      if (i == 3) {
        return;
      }
      if ((bits & 0x10L) != 0L) {
        dest[i - 4] = x1[i - 4];
      }
      if (i == 4) {
        return;
      }
      if ((bits & 0x20L) != 0L) {
        dest[i - 5] = x1[i - 5];
      }
      if (i == 5) {
        return;
      }
      if ((bits & 0x40L) != 0L) {
        dest[i - 6] = x1[i - 6];
      }
      if (i == 6) {
        return;
      }
      if ((bits & 0x80L) != 0L) {
        dest[i - 7] = x1[i - 7];
      }
      if (i == 7) {
        return;
      }
      if ((bits & 0x100L) != 0L) {
        dest[i - 8] = x1[i - 8];
      }
      if (i == 8) {
        return;
      }
      if ((bits & 0x200L) != 0L) {
        dest[i - 9] = x1[i - 9];
      }
      if (i == 9) {
        return;
      }
      if ((bits & 0x400L) != 0L) {
        dest[i - 10] = x1[i - 10];
      }
      if (i == 10) {
        return;
      }
      if ((bits & 0x800L) != 0L) {
        dest[i - 11] = x1[i - 11];
      }
      if (i == 11) {
        return;
      }
      if ((bits & 0x1000L) != 0L) {
        dest[i - 12] = x1[i - 12];
      }
      if (i == 12) {
        return;
      }
      if ((bits & 0x2000L) != 0L) {
        dest[i - 13] = x1[i - 13];
      }
      if (i == 13) {
        return;
      }
      if ((bits & 0x4000L) != 0L) {
        dest[i - 14] = x1[i - 14];
      }
      if (i == 14) {
        return;
      }
      if ((bits & 0x8000L) != 0L) {
        dest[i - 15] = x1[i - 15];
      }
      if (i == 15) {
        return;
      }
      if ((bits & 0x10000L) != 0L) {
        dest[i - 16] = x1[i - 16];
      }
      if (i == 16) {
        return;
      }
      if ((bits & 0x20000L) != 0L) {
        dest[i - 17] = x1[i - 17];
      }
      if (i == 17) {
        return;
      }
      if ((bits & 0x40000L) != 0L) {
        dest[i - 18] = x1[i - 18];
      }
      if (i == 18) {
        return;
      }
      if ((bits & 0x80000L) != 0L) {
        dest[i - 19] = x1[i - 19];
      }
      if (i == 19) {
        return;
      }
      if ((bits & 0x100000L) != 0L) {
        dest[i - 20] = x1[i - 20];
      }
      if (i == 20) {
        return;
      }
      if ((bits & 0x200000L) != 0L) {
        dest[i - 21] = x1[i - 21];
      }
      if (i == 21) {
        return;
      }
      if ((bits & 0x400000L) != 0L) {
        dest[i - 22] = x1[i - 22];
      }
      if (i == 22) {
        return;
      }
      if ((bits & 0x800000L) != 0L) {
        dest[i - 23] = x1[i - 23];
      }
      if (i == 23) {
        return;
      }
      if ((bits & 0x1000000L) != 0L) {
        dest[i - 24] = x1[i - 24];
      }
      if (i == 24) {
        return;
      }
      if ((bits & 0x2000000L) != 0L) {
        dest[i - 25] = x1[i - 25];
      }
      if (i == 25) {
        return;
      }
      if ((bits & 0x4000000L) != 0L) {
        dest[i - 26] = x1[i - 26];
      }
      if (i == 26) {
        return;
      }
      if ((bits & 0x8000000L) != 0L) {
        dest[i - 27] = x1[i - 27];
      }
      if (i == 27) {
        return;
      }
      if ((bits & 0x10000000L) != 0L) {
        dest[i - 28] = x1[i - 28];
      }
      if (i == 28) {
        return;
      }
      if ((bits & 0x20000000L) != 0L) {
        dest[i - 29] = x1[i - 29];
      }
      if (i == 29) {
        return;
      }
      if ((bits & 0x40000000L) != 0L) {
        dest[i - 30] = x1[i - 30];
      }
      if (i == 30) {
        return;
      }
      if ((bits & 0x80000000L) != 0L) {
        dest[i - 31] = x1[i - 31];
      }
      if (i == 31) {
        return;
      }
      if ((bits & 0x100000000L) != 0L) {
        dest[i - 32] = x1[i - 32];
      }
      if (i == 32) {
        return;
      }
      if ((bits & 0x200000000L) != 0L) {
        dest[i - 33] = x1[i - 33];
      }
      if (i == 33) {
        return;
      }
      if ((bits & 0x400000000L) != 0L) {
        dest[i - 34] = x1[i - 34];
      }
      if (i == 34) {
        return;
      }
      if ((bits & 0x800000000L) != 0L) {
        dest[i - 35] = x1[i - 35];
      }
      if (i == 35) {
        return;
      }
      if ((bits & 0x1000000000L) != 0L) {
        dest[i - 36] = x1[i - 36];
      }
      if (i == 36) {
        return;
      }
      if ((bits & 0x2000000000L) != 0L) {
        dest[i - 37] = x1[i - 37];
      }
      if (i == 37) {
        return;
      }
      if ((bits & 0x4000000000L) != 0L) {
        dest[i - 38] = x1[i - 38];
      }
      if (i == 38) {
        return;
      }
      if ((bits & 0x8000000000L) != 0L) {
        dest[i - 39] = x1[i - 39];
      }
      if (i == 39) {
        return;
      }
      if ((bits & 0x10000000000L) != 0L) {
        dest[i - 40] = x1[i - 40];
      }
      if (i == 40) {
        return;
      }
      if ((bits & 0x20000000000L) != 0L) {
        dest[i - 41] = x1[i - 41];
      }
      if (i == 41) {
        return;
      }
      if ((bits & 0x40000000000L) != 0L) {
        dest[i - 42] = x1[i - 42];
      }
      if (i == 42) {
        return;
      }
      if ((bits & 0x80000000000L) != 0L) {
        dest[i - 43] = x1[i - 43];
      }
      if (i == 43) {
        return;
      }
      if ((bits & 0x100000000000L) != 0L) {
        dest[i - 44] = x1[i - 44];
      }
      if (i == 44) {
        return;
      }
      if ((bits & 0x200000000000L) != 0L) {
        dest[i - 45] = x1[i - 45];
      }
      if (i == 45) {
        return;
      }
      if ((bits & 0x400000000000L) != 0L) {
        dest[i - 46] = x1[i - 46];
      }
      if (i == 46) {
        return;
      }
      if ((bits & 0x800000000000L) != 0L) {
        dest[i - 47] = x1[i - 47];
      }
      if (i == 47) {
        return;
      }
      if ((bits & 0x1000000000000L) != 0L) {
        dest[i - 48] = x1[i - 48];
      }
      if (i == 48) {
        return;
      }
      if ((bits & 0x2000000000000L) != 0L) {
        dest[i - 49] = x1[i - 49];
      }
      if (i == 49) {
        return;
      }
      if ((bits & 0x4000000000000L) != 0L) {
        dest[i - 50] = x1[i - 50];
      }
      if (i == 50) {
        return;
      }
      if ((bits & 0x8000000000000L) != 0L) {
        dest[i - 51] = x1[i - 51];
      }
      if (i == 51) {
        return;
      }
      if ((bits & 0x10000000000000L) != 0L) {
        dest[i - 52] = x1[i - 52];
      }
      if (i == 52) {
        return;
      }
      if ((bits & 0x20000000000000L) != 0L) {
        dest[i - 53] = x1[i - 53];
      }
      if (i == 53) {
        return;
      }
      if ((bits & 0x40000000000000L) != 0L) {
        dest[i - 54] = x1[i - 54];
      }
      if (i == 54) {
        return;
      }
      if ((bits & 0x80000000000000L) != 0L) {
        dest[i - 55] = x1[i - 55];
      }
      if (i == 55) {
        return;
      }
      if ((bits & 0x100000000000000L) != 0L) {
        dest[i - 56] = x1[i - 56];
      }
      if (i == 56) {
        return;
      }
      if ((bits & 0x200000000000000L) != 0L) {
        dest[i - 57] = x1[i - 57];
      }
      if (i == 57) {
        return;
      }
      if ((bits & 0x400000000000000L) != 0L) {
        dest[i - 58] = x1[i - 58];
      }
      if (i == 58) {
        return;
      }
      if ((bits & 0x800000000000000L) != 0L) {
        dest[i - 59] = x1[i - 59];
      }
      if (i == 59) {
        return;
      }
      if ((bits & 0x1000000000000000L) != 0L) {
        dest[i - 60] = x1[i - 60];
      }
      if (i == 60) {
        return;
      }
      if ((bits & 0x2000000000000000L) != 0L) {
        dest[i - 61] = x1[i - 61];
      }
      if (i == 61) {
        return;
      }
      if ((bits & 0x4000000000000000L) != 0L) {
        dest[i - 62] = x1[i - 62];
      }
      if (i == 62) {
        return;
      }
      if ((bits & 0x8000000000000000L) != 0L) {
        dest[i - 63] = x1[i - 63];
      }
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
    final boolean[] x0 = new boolean[array.length];
    final boolean[] x1 = new boolean[array.length];
    final Random random = new Random();

    final int times = 1000000;

    for (int i = times; (--i) >= 0;) {
      PerformanceComparisonBitStringBinaryUniform
          .__apply_orig(x0, x1, array, random);
      PerformanceComparisonBitStringBinaryUniform
          .__apply_new_1(x0, x1, array, random);
      PerformanceComparisonBitStringBinaryUniform
          .__apply_new_2(x0, x1, array, random);
    }

    final long t1 = System.nanoTime();
    for (int i = times; (--i) >= 0;) {
      PerformanceComparisonBitStringBinaryUniform
          .__apply_orig(x0, x1, array, random);
    }
    final long t2 = System.nanoTime();
    for (int i = times; (--i) >= 0;) {
      PerformanceComparisonBitStringBinaryUniform
          .__apply_new_1(x0, x1, array, random);
    }
    final long t3 = System.nanoTime();
    for (int i = times; (--i) >= 0;) {
      PerformanceComparisonBitStringBinaryUniform
          .__apply_new_2(x0, x1, array, random);
    }
    final long t4 = System.nanoTime();

    for (int i = times; (--i) >= 0;) {
      PerformanceComparisonBitStringBinaryUniform
          .__apply_orig(x0, x1, array, random);
    }
    final long t5 = System.nanoTime();
    for (int i = times; (--i) >= 0;) {
      PerformanceComparisonBitStringBinaryUniform
          .__apply_new_1(x0, x1, array, random);
    }
    final long t6 = System.nanoTime();
    for (int i = times; (--i) >= 0;) {
      PerformanceComparisonBitStringBinaryUniform
          .__apply_new_2(x0, x1, array, random);
    }
    final long t7 = System.nanoTime();

    final long t_orig = Math.min(t2 - t1, t5 - t4);
    final long t_new_1 = Math.min(t3 - t2, t6 - t5);
    final long t_new_2 = Math.min(t4 - t3, t7 - t6);

    System.out.println(" orig: " + t_orig); //$NON-NLS-1$
    System.out.println("new_1: " + t_new_1); //$NON-NLS-1$
    System.out.println("new_2: " + t_new_2); //$NON-NLS-1$

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
      System.out.print("] = (((bits & 0x");//$NON-NLS-1$
      System.out.print(Long.toHexString(flag));
      System.out.print("L) != 0L) ? x0[i");//$NON-NLS-1$
      if (i > 0) {
        System.out.print('-');
        System.out.print(i);
      }
      System.out.print("] : x1[i");//$NON-NLS-1$
      if (i > 0) {
        System.out.print('-');
        System.out.print(i);
      }
      System.out.println("]);");//$NON-NLS-1$

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

    System.out.println();
    System.out.println();
    System.out.println();
    System.out.println("int i = dest.length;");//$NON-NLS-1$
    System.out.println("System.arraycopy(x0, 0, dest, 0, i--);");//$NON-NLS-1$
    System.out.println("for(;;) {");//$NON-NLS-1$
    System.out.println("final long bits = random.nextLong();");//$NON-NLS-1$
    i = 0;
    flag = 1;
    for (;;) {
      System.out.print("if((bits & 0x");//$NON-NLS-1$
      System.out.print(Long.toHexString(flag));
      System.out.print("L) != 0L) { ");//$NON-NLS-1$
      System.out.print("dest[i");//$NON-NLS-1$
      if (i > 0) {
        System.out.print('-');
        System.out.print(i);
      }
      System.out.print("] = x1[i");//$NON-NLS-1$
      if (i > 0) {
        System.out.print('-');
        System.out.print(i);
      }
      System.out.println("]; }");//$NON-NLS-1$

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

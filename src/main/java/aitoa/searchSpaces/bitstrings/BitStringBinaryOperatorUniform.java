package aitoa.searchSpaces.bitstrings;

import java.util.Random;

import aitoa.structure.IBinarySearchOperator;

/**
 * The uniform crossover operator for bit strings.
 */
public final class BitStringBinaryOperatorUniform
    implements IBinarySearchOperator<boolean[]> {

  /** create the uniform crossover operator */
  public BitStringBinaryOperatorUniform() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "uniform"; //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public void apply(final boolean[] x0, final boolean[] x1,
      final boolean[] dest, final Random random) {
// The code of this procedure is equivalent to the following
// code:
//
// // for (int i = dest.length; (--i) >= 0;) {
// // dest[i] = random.nextBoolean() ? x0[i] : x1[i];
// // }
//
// However, we perform loop unwinding and hence also safe calls
// to the random number generator, so it can be more than twice
// as fast.

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
}

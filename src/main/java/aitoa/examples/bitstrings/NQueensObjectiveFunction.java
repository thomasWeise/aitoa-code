package aitoa.examples.bitstrings;

import java.util.Arrays;

/**
 * The N-Queens problem. This objective function is a
 * re-implementation from scratch of the IOHprofiler version, but
 * as minimization problem. See
 * https://github.com/IOHprofiler/IOHexperimenter/blob/master/src/Problems/f_N_queens.hpp
 */
public final class NQueensObjectiveFunction
    extends BitStringObjectiveFunction {

  /** the name prefix */
  public static final String NAME_PREFIX = "NQueens"; //$NON-NLS-1$

  /** the number of queens */
  public final int k;

  /** the internal upper bound */
  private long upper;

  /**
   * create
   *
   * @param _n
   *          the length of the bit string
   */
  public NQueensObjectiveFunction(final int _n) {
    super(_n);

    if (_n <= 15) {
      throw new IllegalArgumentException(
          "The N-Queens problem can only be defined for n>=16, i.e., for at least four queens, but you specified "//$NON-NLS-1$
              + _n);
    }

    this.k = ((int) (Math.sqrt(_n) + 0.5d));
    if ((this.k * this.k) != _n) {
      throw new IllegalArgumentException((((((((//
      "Cannot have N-Queens problem with n=" //$NON-NLS-1$
          + this.n) + ", because n must be a square number but ")//$NON-NLS-1$
          + this.n) + "!=") + this.k) + '*') + this.k) + '.');//$NON-NLS-1$
    }

    this.upper = -1L;
  }

  /**
   * Create the objective function based on the instance name
   *
   * @param s
   *          the name
   */
  public NQueensObjectiveFunction(final String s) {
    this(BitStringObjectiveFunction
        ._parse_n(NQueensObjectiveFunction.NAME_PREFIX, s));
  }

  /** {@inheritDoc} */
  @Override
  public double evaluate(final boolean[] y) {
    final int kk = this.k;
    final int nn = this.n;

// The chess board is stored row-by-row.

// Count the total number of queens on the chess board. It should
// be k.
    int queensTotal = 0;
// Since we need to go through the board row by row anyway, we
// can also check how many queens are in each row while we are at
// it.
    int mustBeOne1 = 0; // queens per row
    int penalty = 0;
// The last row goes from index n-1 to index n-k, the row before
// from n-k-1 to n-2k, ..., the first row goes from k-1 to 0.
    for (int i = nn, nextRowReset = (i - kk); (--i) >= 0;) {
      if (y[i]) {
        ++queensTotal;
        ++mustBeOne1;
      }

      if (i <= nextRowReset) {
// We reached the end of a row.
        nextRowReset -= kk;

// If there is at most one queen in the row, the penalty is zero.
// Otherwise, the penalty for the row is number of queens in it
// minus 1.
        if (mustBeOne1 > 1) {
          penalty += (mustBeOne1 - 1);
        }
        mustBeOne1 = 0;
      }
    }

// Count the number of queens in the columns and check if there
// is more than one queen in a column.
// j be the column index, it goes from 1 to k.
    for (int j = kk; j > 0; --j) {
      mustBeOne1 = 0;
// The cells in column j have indices k-j, 2k-j, 3k-j, ...,
// k*k-j=n-j in y and we iterate them from the back.
      for (int i = (nn - j); i >= 0; i -= kk) {
        if (y[i]) {
          ++mustBeOne1;
        }
      }
// If there is at most one queen in the column, the penalty is
// zero.
// Otherwise, the penalty for the column is number of queens in
// it minus 1.
      if (mustBeOne1 > 1) {
        penalty += (mustBeOne1 - 1);
      }
    }

// There are 1 + 2*(k-2) = 2*k-3 diagonals of any kind.
// The diagonal in the "middle" is unique, the others can be
// mirrored.

// We have two types of diagonals.
// One goes from top-left to bottom-right, for which we use index
// i1 and count collisions in mustBeOne1 and mustBeOne2 and whose
// indices step in k-1 increments.
// The other one goes from bottom-left to top-right, for which we
// use index i2 and count collisions in mustBeOne3 and mustBeOne4
// and whose indices step in k+1 increments.
// Both have the central, non-mirrored version and the others
// which are mirrored around the central diagonal.

    final int diagonalStep1 = kk - 1;
    final int diagonalStep2 = kk + 1;
    final int otherDiagonalStart = nn - 1;

// First process unique center diagonal.
    mustBeOne1 = 0;
    int mustBeOne3 = 0;
    int d = kk - 1;
    for (int i1 = (kk * d), i2 = i1 + diagonalStep1; i1 > 0;
        i1 -= diagonalStep1, i2 -= diagonalStep2) {
      if (y[i1]) {
        ++mustBeOne1;
      }
      if (y[i2]) {
        ++mustBeOne3;
      }
    }
    if (mustBeOne1 > 1) {
      penalty += (mustBeOne1 - 1);
    }
    if (mustBeOne3 > 1) {
      penalty += (mustBeOne3 - 1);
    }

// Now process the mirrored diagonals
    for (; (--d) >= 1;) {
      mustBeOne1 = 0;
      mustBeOne3 = 0;
      int mustBeOne2 = 0;
      int mustBeOne4 = 0;

      int i1 = (kk * d);
      if (i1 > otherDiagonalStart) {
        i1 -= diagonalStep1
            * ((i1 - otherDiagonalStart) / diagonalStep1);
      }
      int i2 = i1 + diagonalStep1;

      for (; i1 > 0; i1 -= diagonalStep1, i2 -= diagonalStep2) {
        if (y[i1]) {
          ++mustBeOne1;
        }
        if (y[otherDiagonalStart - i1]) {
          ++mustBeOne2;
        }

        if (y[i2]) {
          ++mustBeOne3;
        }
        if (y[otherDiagonalStart - i2]) {
          ++mustBeOne4;
        }
      }
      if (mustBeOne1 > 1) {
        penalty += (mustBeOne1 - 1);
      }
      if (mustBeOne2 > 1) {
        penalty += (mustBeOne2 - 1);
      }

      if (mustBeOne3 > 1) {
        penalty += (mustBeOne3 - 1);
      }
      if (mustBeOne4 > 1) {
        penalty += (mustBeOne4 - 1);
      }
    }

// penalty now holds the total number of collisions in the rows,
// columns, and all diagonals.
// queensTotal is the number of queens.
// The minimization version of the IOHprofiler N queens problem
// is then:

    return (kk - queensTotal) + (kk * penalty);
  }

  /** {@inheritDoc} */
  @Override
  public double lowerBound() {
    return 0;
  }

  /** {@inheritDoc} */
  @Override
  public double upperBound() {
    if (this.upper <= 0L) {
      final boolean[] test = new boolean[this.n];
      Arrays.fill(test, true);
      this.upper = Math.round(Math.ceil(this.evaluate(test)));
    }
    return this.upper;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return BitStringObjectiveFunction._make_name_n(
        NQueensObjectiveFunction.NAME_PREFIX, this.n);
  }

// A program for showing and testing that we hit all the
// diagonals properly.
// /**
// * @param args
// * the args
// */
// public static final void main(final String[] args) {
// final TreeMap<String, ArrayList<Integer>> diags =
// new TreeMap<>();
//
// final int kk = 5;
// final int nn = kk * kk;
//
// final int diagonalStep1 = kk - 1;
// final int diagonalStep2 = kk + 1;
// final int otherDiagonalStart = nn - 1;
//
//// First process unique center diagonal.
// String mustBeOne1 = "a" + kk; //$NON-NLS-1$
// String mustBeOne3 = "b" + kk; //$NON-NLS-1$
// int d = kk - 1;
// for (int i1 = (kk * d), i2 = i1 + diagonalStep1; i1 > 0;
// i1 -= diagonalStep1, i2 -= diagonalStep2) {
// // if (i1 <= otherDiagonalStart) {
// diags.computeIfAbsent(mustBeOne1, (x) -> new ArrayList<>())
// .add(Integer.valueOf(i1));
// diags.computeIfAbsent(mustBeOne3, (x) -> new ArrayList<>())
// .add(Integer.valueOf(i2));
// // }
// }
//
//// Now process the mirrored diagonals
// for (; (--d) >= 1;) {
// mustBeOne1 = "a" + d + "-1";//$NON-NLS-1$//$NON-NLS-2$
// mustBeOne3 = "b" + d + "-1";//$NON-NLS-1$//$NON-NLS-2$
// String mustBeOne2 = "a" + d + "-2";//$NON-NLS-1$//$NON-NLS-2$
// String mustBeOne4 = "b" + d + "-2";//$NON-NLS-1$//$NON-NLS-2$
//
// int i1 = (kk * d);
// if (i1 > otherDiagonalStart) {
// i1 -= diagonalStep1
// * ((i1 - otherDiagonalStart) / diagonalStep1);
// }
// int i2 = i1 + diagonalStep1;
// for (; i1 > 0; i1 -= diagonalStep1, i2 -= diagonalStep2) {
// diags
// .computeIfAbsent(mustBeOne1,
// (x) -> new ArrayList<>())
// .add(Integer.valueOf(i1));
// diags
// .computeIfAbsent(mustBeOne2,
// (x) -> new ArrayList<>())
// .add(Integer.valueOf(otherDiagonalStart - i1));
//
// diags
// .computeIfAbsent(mustBeOne3,
// (x) -> new ArrayList<>())
// .add(Integer.valueOf(i2));
// diags
// .computeIfAbsent(mustBeOne4,
// (x) -> new ArrayList<>())
// .add(Integer.valueOf(otherDiagonalStart - i2));
// }
// }
//
// for (Map.Entry<String, ArrayList<Integer>> x : diags
// .entrySet()) {
// System.out.print(x.getKey());
// System.out.print(':');
// x.getValue().stream().sorted().forEach((a) -> {
// System.out.print(' ');
// System.out.print(a);
// });
// System.out.println();
// }
//
// }
}

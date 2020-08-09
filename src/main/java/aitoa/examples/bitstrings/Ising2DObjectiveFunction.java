package aitoa.examples.bitstrings;

/**
 * The Ising model on the 2-dimensional Torus.
 */
public final class Ising2DObjectiveFunction
    extends BitStringObjectiveFunction {

  /** the name prefix */
  public static final String NAME_PREFIX = "Ising2d"; //$NON-NLS-1$

  /** the torus width = sqrt(n) */
  public final int k;

  /** the upper bound */
  public final int upperBound;

  /**
   * create
   *
   * @param pN
   *          the length of the bit string
   */
  public Ising2DObjectiveFunction(final int pN) {
    super(pN);
    this.k = ((int) (Math.sqrt(pN) + 0.5d));
    if ((this.k * this.k) != pN) {
      throw new IllegalArgumentException((((((((//
      "Cannot have Ising model on two-dimensional torus with n=" //$NON-NLS-1$
          + this.n) + ", because n must be a square number but ")//$NON-NLS-1$
          + this.n) + "!=") + this.k) + '*') + this.k) + '.');//$NON-NLS-1$
    }

    this.upperBound = pN + pN;
    if (((this.upperBound / this.k) != (this.k + this.k))
        || (pN >= (Integer.MAX_VALUE >>> 1))) {
      throw new IllegalArgumentException(((//
      "Cannot have Ising model on two-dimensional torus with n=" //$NON-NLS-1$
          + this.n)
          + ", because n must less than half of Integer.MAX_VALUE (")//$NON-NLS-1$
          + Integer.MAX_VALUE + ").");//$NON-NLS-1$
    }
  }

  /**
   * Create the objective function based on the instance name
   *
   * @param s
   *          the name
   */
  public Ising2DObjectiveFunction(final String s) {
    this(BitStringObjectiveFunction
        .parseN(Ising2DObjectiveFunction.NAME_PREFIX, s));
  }

  /** {@inheritDoc} */
  @Override
  public double evaluate(final boolean[] y) {
    final int kk = this.k;
    int s = this.upperBound;

    int lastitimesk = 0;
    for (int i = kk; (--i) >= 0;) {
      final int itimesk = (i * kk);
      int lastj = 0;
      for (int j = kk; (--j) >= 0;) {
        final boolean center = y[itimesk + j];

        if (center == (y[(lastitimesk) + j])) {
          --s;
        }
        if (center == y[(itimesk) + lastj]) {
          --s;
        }
        lastj = j;
      }
      lastitimesk = itimesk;
    }

    return s;
  }

  /** {@inheritDoc} */
  @Override
  public double lowerBound() {
    return 0;
  }

  /** {@inheritDoc} */
  @Override
  public double upperBound() {
    return this.upperBound;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return BitStringObjectiveFunction
        .makeNameN(Ising2DObjectiveFunction.NAME_PREFIX, this.n);
  }

// Below, we provide code that shows how the pair-generation in
// our objective function is equivalent to the enumeration of all
// unique pairs.
// There should be in total 2*n=2*k*k pairs, so 2*n is the upper
// bound for the objective value and 0 is the lower bound.
// Each pair can contribute -1 to the objective value if the
// neighboring elements are the same and zero otherwise.

// /** x */
// private static final class __Pair
// implements Comparable<__Pair> {
// /** a */
// final int a;
// /** b */
// final int b;
//
// /**
// * @param k
// * x
// * @param i1
// * x
// * @param j1
// * x
// * @param i2
// * x
// * @param j2
// * x
// */
// __Pair(int k, int i1, int j1, int i2, int j2) {
// this((((i1 + k) % k) * k) + ((j1 + k) % k),
// (((i2 + k) % k) * k) + ((j2 + k) % k));
// }
//
// /**
// * @param i
// * x
// * @param j
// * x
// */
// __Pair(int i, int j) {
// super();
// if (i < j) {
// this.a = i;
// this.b = j;
// } else {
// this.a = j;
// this.b = i;
// }
// }
//
// /** {@inheritDoc} */
// @Override
// public final int hashCode() {
// return this.a * 31 + this.b;
// }
//
// /** {@inheritDoc} */
// @Override
// public final boolean equals(final Object o) {
// if (o instanceof __Pair) {
// final __Pair p = (__Pair) o;
// return ((p.a == this.a) && (p.b == this.b));
// }
// return false;
// }
//
// /** {@inheritDoc} */
// @Override
// public final int compareTo(final __Pair p) {
// if (this.a < p.a) {
// return -1;
// }
// if (this.a > p.a) {
// return 1;
// }
// if (this.b < p.b) {
// return -1;
// }
// if (this.b > p.b) {
// return 1;
// }
// return 0;
// }
//
// /** {@inheritDoc} */
// @Override
// public final String toString() {
// return ((('(' + Integer.toString(this.a)) + ',') + this.b
// + ')');
// }
// }
//
// /**
// * ignore
// *
// * @param args
// * ignore
// */
// public static final void main(final String[] args) {
// final int k = 17;
//// final int n = k*k;
//
// final HashSet<__Pair> all = new HashSet<>();
//
// for (int i = k; (--i) >= 0;) {
// for (int j = k; (--j) >= 0;) {
// all.add(new __Pair(k, i, j, i - 1, j));
// all.add(new __Pair(k, i, j, i + 1, j));
// all.add(new __Pair(k, i, j, i, j - 1));
// all.add(new __Pair(k, i, j, i, j + 1));
// }
// }
//
// all.stream().sorted().forEach((p) -> System.out.println(p));
//
// System.out.println();
// System.out.println();
//
// int lastitimesk = 0;
// for (int i = k; (--i) >= 0;) {
// int itimesk = (i * k);
// int lastj = 0;
// for (int j = k; (--j) >= 0;) {
//
// int center = itimesk + j;
// int top = (lastitimesk) + j;
// int left = (itimesk) + lastj;
//
// if (center < top) {
// System.out.println("(" + //$NON-NLS-1$
// center + "," + //$NON-NLS-1$
// top + ")"); //$NON-NLS-1$
// } else {
// System.out.println("(" + //$NON-NLS-1$
// top + "," + //$NON-NLS-1$
// center + ")"); //$NON-NLS-1$
// }
// if (center < left) {
// System.out.println("(" + //$NON-NLS-1$
// center + "," + //$NON-NLS-1$
// left + ")"); //$NON-NLS-1$
// } else {
// System.out.println("(" + //$NON-NLS-1$
// left + "," + //$NON-NLS-1$
// center + ")"); //$NON-NLS-1$
// }
//
// lastj = j;
// }
// lastitimesk = itimesk;
// }
// }
}

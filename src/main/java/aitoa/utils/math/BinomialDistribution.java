package aitoa.utils.math;

import java.util.Random;

/**
 * An class for sampling binomial distributions. Much of the code
 * in here is taken from the colt library, for which the
 * following statement holds:
 * <p>
 * Copyright © 1999 CERN - European Organization for Nuclear
 * Research. Permission to use, copy, modify, distribute and sell
 * this software and its documentation for any purpose is hereby
 * granted without fee, provided that the above copyright notice
 * appear in all copies and that both that copyright notice and
 * this permission notice appear in supporting documentation.
 * CERN makes no representations about the suitability of this
 * software for any purpose. It is provided "as is" without
 * expressed or implied warranty.
 * </p>
 */
public final class BinomialDistribution
    extends DiscreteRandomDistribution {

  /** the probability */
  public final double p;
  /** the number of values */
  public final int n;

  /** internal variable */
  private final double mPar;
  /** internal variable */
  private final double mNp;
  /** internal variable */
  private final double mP0;
  /** internal variable */
  private final double mQ;
  /** internal variable */
  private final int mB;
  /** internal variable */
  private final int mM;
  /** internal variable */
  private final int mNm;
  /** internal variable */
  private final double mPq;
  /** internal variable */
  private final double mRc;
  /** internal variable */
  private final double mSs;
  /** internal variable */
  private final double mXm;
  /** internal variable */
  private final double mXl;
  /** internal variable */
  private final double mXr;
  /** internal variable */
  private final double mLl;
  /** internal variable */
  private final double mLr;
  /** internal variable */
  private final double mC;
  /** internal variable */
  private final double mP1;
  /** internal variable */
  private final double mP2;
  /** internal variable */
  private final double mP3;
  /** internal variable */
  private final double mP4;
  /** internal variable */
  private final double mCh;

  /** internal constant for 1/3 */
  private static final double C1_3 = 1d / 3d;
  /** internal constant for 5/8 */
  private static final double C5_8 = 5d / 8d;
  /** internal constant for 1/6 */
  private static final double C1_6 = 1d / 6d;
  /** internal constants */
  private static final int DMAX_KM = 20;
  /** internal constant for 1/12 */
  private static final double C1 = 1d / 12d;
  /** internal constant for -1/360 */
  private static final double C3 = -1d / 360d;
  /** internal constant for 1/1260 */
  private static final double C5 = 1d / 1260d;
  /** internal constant for -1/1680 */
  private static final double C7 = -1d / 1680d;

  /** constants used for the stirling correction */
  private static final double[] STIRLING_CORRECTION = { //
      0.0d, //
      8.106146679532726e-02d, 4.134069595540929e-02d,
      2.767792568499834e-02d, 2.079067210376509e-02d,
      1.664469118982119e-02d, 1.387612882307075e-02d,
      1.189670994589177e-02d, 1.041126526197209e-02d,
      9.255462182712733e-03d, 8.330563433362871e-03d,
      7.573675487951841e-03d, 6.942840107209530e-03d,
      6.408994188004207e-03d, 5.951370112758848e-03d,
      5.554733551962801e-03d, 5.207655919609640e-03d,
      4.901395948434738e-03d, 4.629153749334029e-03d,
      4.385560249232324e-03d, 4.166319691996922e-03d,
      3.967954218640860e-03d, 3.787618068444430e-03d,
      3.622960224683090e-03d, 3.472021382978770e-03d,
      3.333155636728090e-03d, 3.204970228055040e-03d,
      3.086278682608780e-03d, 2.976063983550410e-03d,
      2.873449362352470e-03d, 2.777674929752690e-03d };

  /**
   * create the distribution
   *
   * @param pN
   *          the maximum number
   * @param pP
   *          the probability
   */
  public BinomialDistribution(final int pN, final double pP) {
    super();

    if (pN <= 0) {
      throw new IllegalArgumentException("n must be > 0 but is " //$NON-NLS-1$
          + pN);
    }
    this.n = pN;

    if ((pP <= 0d) || (!Double.isFinite(pP))) {
      throw new IllegalArgumentException(
          "Probability must be > 0, but is " //$NON-NLS-1$
              + pP);
    }
    this.p = pP;

    this.mPar = Math.min(this.p, 1.0d - this.p);
    this.mQ = 1.0d - this.mPar;
    this.mNp = this.n * this.mPar;

    // Check for invalid input values

    if (this.mNp <= 0.0d) {
      throw new IllegalArgumentException("Cannot have n="//$NON-NLS-1$
          + pN + " and p=" + pP);//$NON-NLS-1$
    }

    final double rm = this.mNp + this.mPar;
    this.mM = (int) rm; // mode, integer
    this.mXm = this.mM + 0.5d;
    this.mPq = this.mPar / this.mQ;

    this.mP0 = Math.exp(this.n * Math.log(this.mQ)); // Chop-down
    final int bh = (int) (this.mNp
        + (10.0d * Math.sqrt(this.mNp * this.mQ)));
    this.mB = Math.min(this.n, bh);

    this.mRc = (this.n + 1.0d) * this.mPq; // recurr.

    this.mSs = this.mNp * this.mQ; // variance
    final int i = (int) ((2.195d * Math.sqrt(this.mSs))
        - (4.6d * this.mQ));
    this.mXl = this.mM - i; // limit left
    this.mXr = this.mM + i + 1L; // limit right
    double f = (rm - this.mXl) / (rm - (this.mXl * this.mPar));
    this.mLl = f * (1.0d + (0.5d * f));
    f = (this.mXr - rm) / (this.mXr * this.mQ);
    this.mLr = f * (1.0d + (0.5d * f));
    this.mC = 0.134d + (20.5d / (15.3d + this.mM)); // parallelogram
    // height
    this.mP1 = i + 0.5d;
    this.mP2 = this.mP1 * (1.0d + this.mC + this.mC); // probabilities
    this.mP3 = this.mP2 + (this.mC / this.mLl); // of regions
                                                // 1-4
    this.mP4 = this.mP3 + (this.mC / this.mLr);

    this.mNm = (this.n - this.mM) + 1;
    this.mCh = (this.mXm
        * Math.log((this.mM + 1.0d) / (this.mPq * this.mNm)))
        + BinomialDistribution.stirlingCorrection(this.mM + 1)
        + BinomialDistribution.stirlingCorrection(this.mNm);
  }

  /**
   * Returns the Stirling Correction, based on (i.e., stolen
   * from) cern.jet.math.Arithmetic of the colt library.
   *
   * @param k
   *          the k
   * @return the stirling correction
   */
  private static double stirlingCorrection(final int k) {
    if (k > 30) {
      final double r = 1.0d / k;
      final double rr = r * r;
      return r * (BinomialDistribution.C1
          + (rr * (BinomialDistribution.C3
              + (rr * (BinomialDistribution.C5
                  + (rr * BinomialDistribution.C7))))));
    }
    return BinomialDistribution.STIRLING_CORRECTION[k];
  }

  /**
   * compute a random number in (0,1), as taken from
   * cern.jet.random.engine.RandomEngine.
   *
   * @param random
   *          the random number generator
   * @return the random number
   */
  private static double raw(final Random random) {
    int nextInt;
    do { // accept anything but zero
      nextInt = random.nextInt();
// in [Integer.MIN_VALUE,Integer.MAX_VALUE]-interval
    } while (nextInt == 0);

// transform to (0.0,1.0)-interval 2.3283064365386963E-10 == 1.0
// / Math.pow(2,32)
    return (nextInt & 0xFFFFFFFFL) * 2.3283064365386963E-10;

/*
 * nextInt == Integer.MAX_VALUE --> 0.49999999976716936 nextInt
 * == Integer.MIN_VALUE --> 0.5 nextInt == Integer.MAX_VALUE-1
 * --> 0.4999999995343387 nextInt == Integer.MIN_VALUE+1 -->
 * 0.5000000002328306 nextInt == 1 --> 2.3283064365386963E-10
 * nextInt == -1 --> 0.9999999997671694 nextInt == 2 -->
 * 4.6566128730773926E-10 nextInt == -2 --> 0.9999999995343387
 */
  }

  /**
   * Compute a random number binomially distributed in [0, n], as
   * taken from cern.jet.random.Binomial
   *
   * @param random
   *          the random number generator
   * @return the next integer
   */
  @Override
  public int nextInt(final Random random) {
    int K = 0;
    int Km = -1;
    double U = Double.NaN;
    double V = Double.NaN;
    double X = Double.NaN;
    if (this.mNp < 10) { // Inversion Chop-down

      double pk = this.mP0;
      U = BinomialDistribution.raw(random);
      while (U > pk) {
        ++K;
        if (K > this.mB) {
          U = BinomialDistribution.raw(random);
          K = 0;
          pk = this.mP0;
        } else {
          U -= pk;
          pk = (((this.n - K) + 1) * this.mPar * pk)
              / (K * this.mQ);
        }
      }
      return ((this.p > 0.5d) ? (this.n - K) : K);
    }

    for (;;) {
      V = BinomialDistribution.raw(random);
      if ((U = BinomialDistribution.raw(random)
          * this.mP4) <= this.mP1) { // triangular
        // region
        K = (int) ((this.mXm - U) + (this.mP1 * V));
        return (this.p > 0.5d) ? (this.n - K) : K; // immediate
                                                   // accept
      }
      if (U <= this.mP2) { // parallelogram
        X = this.mXl + ((U - this.mP1) / this.mC);
        if ((V = ((V * this.mC) + 1.0d)
            - (Math.abs(this.mXm - X) / this.mP1)) >= 1.0d) {
          continue;
        }
        K = (int) X;
      } else if (U <= this.mP3) { // left tail
        if ((X = this.mXl + (Math.log(V) / this.mLl)) < 0.0d) {
          continue;
        }
        K = (int) X;
        V *= (U - this.mP2) * this.mLl;
      } else { // right tail
        if ((K = (int) (this.mXr
            - (Math.log(V) / this.mLr))) > this.n) {
          continue;
        }
        V *= (U - this.mP3) * this.mLr;
      }

      // acceptance test : two cases, depending on |K - m_m|
      if (((Km =
          Math.abs(K - this.mM)) <= BinomialDistribution.DMAX_KM)
          || ((Km + Km + 2L) >= this.mSs)) {

        // computation of p(K) via recurrence relationship from
        // the mode
        double f = 1.0d; // f(m_m)
        if (this.mM < K) {
          for (int i = this.mM; i < K;) {
            if ((f *= ((this.mRc / ++i) - this.mPq)) < V) {
              break; // multiply f
            }
          }
        } else {
          for (int i = K; i < this.mM;) {
            if ((V *= ((this.mRc / ++i) - this.mPq)) > f) {
              break; // multiply V
            }
          }
        }
        if (V <= f) {
          break; // acceptance test
        }
      } else {
        // lower and upper squeeze tests, based on lower bounds
        // for log p(K)
        V = Math.log(V);
        final double T = (-Km * Km) / (this.mSs + this.mSs);
        final double E = (Km / this.mSs)
            * ((((Km * ((Km * BinomialDistribution.C1_3)
                + BinomialDistribution.C5_8))
                + BinomialDistribution.C1_6) / this.mSs) + 0.5d);
        if (V <= (T - E)) {
          break;
        }
        if (V <= (T + E)) {
          final int nK = (this.n - K) + 1;

          // computation of log f(K) via Stirling's formula
          // final acceptance-rejection test
          if (V <= ((this.mCh
              + ((this.n + 1.0d)
                  * Math.log((double) this.mNm / (double) nK))
              + ((K + 0.5d)
                  * Math.log((nK * this.mPq) / (K + 1.0d))))
              - BinomialDistribution.stirlingCorrection(K + 1)
              - BinomialDistribution.stirlingCorrection(nK))) {
            break;
          }
        }
      }
    }
    return (this.p > 0.5d) ? (this.n - K) : K;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return (((("Bin(" + this.n) + ',') + this.p) + ')'); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return (31 * Integer.hashCode(this.n))
        + Double.hashCode(this.p);
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object o) {
    if (o instanceof BinomialDistribution) {
      final BinomialDistribution b = ((BinomialDistribution) o);
      return (this.n == b.n)
          && (Double.compare(this.p, b.p) == 0);
    }
    return false;
  }
}

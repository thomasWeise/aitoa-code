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
  private final double m_par;
  /** internal variable */
  private final double m_np;
  /** internal variable */
  private final double m_p0;
  /** internal variable */
  private final double m_q;
  /** internal variable */
  private final int m_b;
  /** internal variable */
  private final int m_m;
  /** internal variable */
  private final int m_nm;
  /** internal variable */
  private final double m_pq;
  /** internal variable */
  private final double m_rc;
  /** internal variable */
  private final double m_ss;
  /** internal variable */
  private final double m_xm;
  /** internal variable */
  private final double m_xl;
  /** internal variable */
  private final double m_xr;
  /** internal variable */
  private final double m_ll;
  /** internal variable */
  private final double m_lr;
  /** internal variable */
  private final double m_c;
  /** internal variable */
  private final double m_p1;
  /** internal variable */
  private final double m_p2;
  /** internal variable */
  private final double m_p3;
  /** internal variable */
  private final double m_p4;
  /** internal variable */
  private final double m_ch;

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
   * @param _n
   *          the maximum number
   * @param _p
   *          the probability
   */
  public BinomialDistribution(final int _n, final double _p) {
    super();

    if (_n <= 0) {
      throw new IllegalArgumentException("n must be > 0 but is " //$NON-NLS-1$
          + _n);
    }
    this.n = _n;

    if ((_p <= 0d) || (!Double.isFinite(_p))) {
      throw new IllegalArgumentException(
          "Probability must be > 0, but is " //$NON-NLS-1$
              + _p);
    }
    this.p = _p;

    int bh, i;
    double f, rm;

    this.m_par = Math.min(this.p, 1.0d - this.p);
    this.m_q = 1.0d - this.m_par;
    this.m_np = this.n * this.m_par;

    // Check for invalid input values

    if (this.m_np <= 0.0d) {
      throw new IllegalArgumentException("Cannot have n="//$NON-NLS-1$
          + _n + " and p=" + _p);//$NON-NLS-1$
    }

    rm = this.m_np + this.m_par;
    this.m_m = (int) rm; // mode, integer
    this.m_xm = this.m_m + 0.5d;
    this.m_pq = this.m_par / this.m_q;

    this.m_p0 = Math.exp(this.n * Math.log(this.m_q)); // Chop-down
    bh = (int) (this.m_np
        + (10.0d * Math.sqrt(this.m_np * this.m_q)));
    this.m_b = Math.min(this.n, bh);

    this.m_rc = (this.n + 1.0d) * this.m_pq; // recurr.

    this.m_ss = this.m_np * this.m_q; // variance
    i = (int) ((2.195d * Math.sqrt(this.m_ss))
        - (4.6d * this.m_q));
    this.m_xl = this.m_m - i; // limit left
    this.m_xr = this.m_m + i + 1L; // limit right
    f = (rm - this.m_xl) / (rm - (this.m_xl * this.m_par));
    this.m_ll = f * (1.0d + (0.5d * f));
    f = (this.m_xr - rm) / (this.m_xr * this.m_q);
    this.m_lr = f * (1.0d + (0.5d * f));
    this.m_c = 0.134d + (20.5d / (15.3d + this.m_m)); // parallelogram
    // height
    this.m_p1 = i + 0.5d;
    this.m_p2 = this.m_p1 * (1.0d + this.m_c + this.m_c); // probabilities
    this.m_p3 = this.m_p2 + (this.m_c / this.m_ll); // of regions
                                                    // 1-4
    this.m_p4 = this.m_p3 + (this.m_c / this.m_lr);

    this.m_nm = (this.n - this.m_m) + 1;
    this.m_ch = (this.m_xm
        * Math.log((this.m_m + 1.0d) / (this.m_pq * this.m_nm)))
        + BinomialDistribution.__stirlingCorrection(this.m_m + 1)
        + BinomialDistribution.__stirlingCorrection(this.m_nm);
  }

  /**
   * Returns the Stirling Correction, based on (i.e., stolen
   * from) cern.jet.math.Arithmetic of the colt library.
   *
   * @param k
   *          the k
   * @return the stirling correction
   */
  private static double __stirlingCorrection(final int k) {

    double r, rr;

    if (k > 30) {
      r = 1.0d / k;
      rr = r * r;
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
  private static double __raw(final Random random) {
    int nextInt;
    do { // accept anything but zero
      nextInt = random.nextInt(); // in
                                  // [Integer.MIN_VALUE,Integer.MAX_VALUE]-interval
    } while (nextInt == 0);

    // transform to (0.0,1.0)-interval
    // 2.3283064365386963E-10 == 1.0 / Math.pow(2,32)
    return (nextInt & 0xFFFFFFFFL) * 2.3283064365386963E-10;

    /*
     * nextInt == Integer.MAX_VALUE --> 0.49999999976716936
     * nextInt == Integer.MIN_VALUE --> 0.5 nextInt ==
     * Integer.MAX_VALUE-1 --> 0.4999999995343387 nextInt ==
     * Integer.MIN_VALUE+1 --> 0.5000000002328306 nextInt == 1
     * --> 2.3283064365386963E-10 nextInt == -1 -->
     * 0.9999999997671694 nextInt == 2 --> 4.6566128730773926E-10
     * nextInt == -2 --> 0.9999999995343387
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

    int i, K, Km, nK;
    double f;
    double U, V, X, T, E;

    if (this.m_np < 10) { // Inversion Chop-down
      double pk;

      K = 0;
      pk = this.m_p0;
      U = BinomialDistribution.__raw(random);
      while (U > pk) {
        ++K;
        if (K > this.m_b) {
          U = BinomialDistribution.__raw(random);
          K = 0;
          pk = this.m_p0;
        } else {
          U -= pk;
          pk = (((this.n - K) + 1) * this.m_par * pk)
              / (K * this.m_q);
        }
      }
      return ((this.p > 0.5d) ? (this.n - K) : K);
    }

    for (;;) {
      V = BinomialDistribution.__raw(random);
      if ((U = BinomialDistribution.__raw(random)
          * this.m_p4) <= this.m_p1) { // triangular
        // region
        K = (int) ((this.m_xm - U) + (this.m_p1 * V));
        return (this.p > 0.5d) ? (this.n - K) : K; // immediate
                                                   // accept
      }
      if (U <= this.m_p2) { // parallelogram
        X = this.m_xl + ((U - this.m_p1) / this.m_c);
        if ((V = ((V * this.m_c) + 1.0d)
            - (Math.abs(this.m_xm - X) / this.m_p1)) >= 1.0d) {
          continue;
        }
        K = (int) X;
      } else if (U <= this.m_p3) { // left tail
        if ((X = this.m_xl + (Math.log(V) / this.m_ll)) < 0.0d) {
          continue;
        }
        K = (int) X;
        V *= (U - this.m_p2) * this.m_ll;
      } else { // right tail
        if ((K = (int) (this.m_xr
            - (Math.log(V) / this.m_lr))) > this.n) {
          continue;
        }
        V *= (U - this.m_p3) * this.m_lr;
      }

      // acceptance test : two cases, depending on |K - m_m|
      if (((Km = Math
          .abs(K - this.m_m)) <= BinomialDistribution.DMAX_KM)
          || ((Km + Km + 2L) >= this.m_ss)) {

        // computation of p(K) via recurrence relationship from
        // the mode
        f = 1.0d; // f(m_m)
        if (this.m_m < K) {
          for (i = this.m_m; i < K;) {
            if ((f *= ((this.m_rc / ++i) - this.m_pq)) < V) {
              break; // multiply f
            }
          }
        } else {
          for (i = K; i < this.m_m;) {
            if ((V *= ((this.m_rc / ++i) - this.m_pq)) > f) {
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
        T = (-Km * Km) / (this.m_ss + this.m_ss);
        E = (Km / this.m_ss)
            * ((((Km * ((Km * BinomialDistribution.C1_3)
                + BinomialDistribution.C5_8))
                + BinomialDistribution.C1_6) / this.m_ss)
                + 0.5d);
        if (V <= (T - E)) {
          break;
        }
        if (V <= (T + E)) {
          nK = (this.n - K) + 1;

          // computation of log f(K) via Stirling's formula
          // final acceptance-rejection test
          if (V <= ((this.m_ch
              + ((this.n + 1.0d)
                  * Math.log((double) this.m_nm / (double) nK))
              + ((K + 0.5d)
                  * Math.log((nK * this.m_pq) / (K + 1.0d))))
              - BinomialDistribution.__stirlingCorrection(K + 1)
              - BinomialDistribution.__stirlingCorrection(nK))) {
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

package aitoa.utils.logs;

/** A single ert-ecd point */
public final class ErtEcdfPoint
    implements Comparable<ErtEcdfPoint> {
  /** the time measure value */
  public final double ert;
  /** the relative ecdf value */
  public final double ecdfRel;
  /** the absolute number of successfully solved instances */
  public final int ecdfAbs;
  /** the number of instances we aggregate over */
  public final int instances;

  /**
   * create
   *
   * @param _ert
   *          the time measure value
   * @param _ecdfRel
   *          the relative ecdf value
   * @param _ecdfAbs
   *          the absolute number of successfully solved
   *          instances
   * @param _instances
   *          the number of instances we aggregate over
   */
  ErtEcdfPoint(final double _ert, final double _ecdfRel,
      final int _ecdfAbs, final int _instances) {
    super();
    this.ert = _ert;
    this.ecdfRel = _ecdfRel;
    this.ecdfAbs = _ecdfAbs;
    this.instances = _instances;

    if (!Double.isFinite(this.ert)) {
      if (!(this.ert >= Double.POSITIVE_INFINITY)) {
        throw new IllegalArgumentException(
            "Non-finite ERT must be positive infinite, but is " //$NON-NLS-1$
                + this.ert);
      }
    }
    if ((!Double.isFinite(this.ecdfRel)) || (this.ecdfRel < 0d)
        || (this.ecdfRel > 1d)) {
      throw new IllegalArgumentException(
          "Relative ECDF must be in [0,1], but is " //$NON-NLS-1$
              + this.ecdfRel);
    }
    if (this.ecdfAbs < 0) {
      throw new IllegalArgumentException(
          "Absolute ECDF must be in >=0, but is " //$NON-NLS-1$
              + this.ecdfAbs);
    }
    if ((this.ecdfAbs <= 0) ^ (this.ecdfRel <= 0d)) {
      throw new IllegalArgumentException(
          "Absolute and relative ECDF must either be both 0 or both>0, but are be " //$NON-NLS-1$
              + this.ecdfAbs + " and " + this.ecdfRel);//$NON-NLS-1$
    }

    if (this.ecdfAbs > 0) {
      final double x = (this.ecdfAbs / this.ecdfRel);
      if (Math.abs(x - Math.round(x)) > (3d * Math.ulp(x))) {
        throw new IllegalArgumentException(
            "Ecdf absolute and relative values do not fit: "//$NON-NLS-1$
                + this.ecdfAbs + " and " + this.ecdfRel //$NON-NLS-1$
                + " would correspond to " + x + //$NON-NLS-1$
                " runs.");//$NON-NLS-1$
      }
    }
    if (this.instances <= 0) {
      throw new IllegalArgumentException(
          "Number of instances must be > 0, but is " //$NON-NLS-1$
              + this.instances);
    }
    if (this.instances < this.ecdfAbs) {
      throw new IllegalArgumentException("Number of instances " //$NON-NLS-1$
          + this.instances
          + " must be greater or equal to ecdf.abs "//$NON-NLS-1$
          + this.ecdfAbs);
    }

    if (this.ecdfRel != (this.ecdfAbs
        / ((double) this.instances))) {
      throw new IllegalArgumentException(
          "Invalid ecdf.rel " + this.ecdfRel//$NON-NLS-1$
              + " for ecdf.abs=" + this.ecdfAbs + //$NON-NLS-1$
              " on " + this.instances//$NON-NLS-1$
              + " instances.");//$NON-NLS-1$
    }
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    int hc = Double.hashCode(this.ert);
    hc = (31 * hc) + Double.hashCode(this.ecdfRel);
    hc = (31 * hc) + Integer.hashCode(this.ecdfAbs);
    hc = (31 * hc) + Integer.hashCode(this.instances);
    return hc;
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ErtEcdfPoint) {
      final ErtEcdfPoint e = ((ErtEcdfPoint) o);
      return ((Double.compare(this.ert, e.ert) == 0) && //
          (Double.compare(this.ecdfRel, e.ecdfRel) == 0) && //
          (this.instances == e.instances) && //
          (this.ecdfAbs == e.ecdfAbs));
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public int compareTo(final ErtEcdfPoint o) {
    int r = Double.compare(this.ert, o.ert);
    if (r != 0) {
      return r;
    }
    r = Double.compare(o.ecdfRel, this.ecdfRel);
    if (r != 0) {
      return r;
    }
    r = Integer.compare(o.ecdfAbs, this.ecdfAbs);
    if (r != 0) {
      return r;
    }
    return Integer.compare(o.instances, this.instances);
  }
}

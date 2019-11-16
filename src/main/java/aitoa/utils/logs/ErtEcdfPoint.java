package aitoa.utils.logs;

/** A single ert-ecd point */
public final class ErtEcdfPoint
    implements Cloneable, Comparable<ErtEcdfPoint> {
  /** the time measure value */
  public double ert;
  /** the relative ecdf value */
  public double ecdfRel;
  /** the absolute number of successfully solved instances */
  public int ecdfAbs;

  /** create */
  ErtEcdfPoint() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public final ErtEcdfPoint clone() {
    try {
      return ((ErtEcdfPoint) (super.clone()));
    } catch (final Throwable error) {
      throw new RuntimeException(error);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    int hc = Double.hashCode(this.ert);
    hc = (31 * hc) + Double.hashCode(this.ecdfRel);
    hc = (31 * hc) + Integer.hashCode(this.ecdfAbs);
    return hc;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ErtEcdfPoint) {
      final ErtEcdfPoint e = ((ErtEcdfPoint) o);
      return ((Double.compare(this.ert, e.ert) == 0) && //
          (Double.compare(this.ecdfRel, e.ecdfRel) == 0) && //
          (this.ecdfAbs == e.ecdfAbs));
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final int compareTo(final ErtEcdfPoint o) {
    int r = Double.compare(this.ert, o.ert);
    if (r != 0) {
      return r;
    }
    r = Double.compare(o.ecdfRel, this.ecdfRel);
    if (r != 0) {
      return r;
    }
    return Integer.compare(o.ecdfAbs, this.ecdfAbs);
  }
}

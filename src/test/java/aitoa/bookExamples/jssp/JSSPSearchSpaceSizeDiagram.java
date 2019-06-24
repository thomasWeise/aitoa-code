package aitoa.bookExamples.jssp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Print the search space sizes for the JSSP instances in form of
 * a diagram
 */
public class JSSPSearchSpaceSizeDiagram {

  /**
   * The main routine
   *
   * @param args
   *          ignore
   */
  public static final void main(final String[] args) {
    final ArrayList<__row> all = new ArrayList<>();
    final ArrayList<__row> pending = new ArrayList<>();

    loop: for (int max = 1;; max++) {
      pending.clear();
      for (int m = 1; m <= max; m++) {
        for (int n = 1; n <= max; n++) {
          final BigInteger size =
              JSSPSearchSpaceSize.searchSpaceSize(m, n);
          if (Double.isFinite(size.doubleValue())) {
            pending.add(new __row(m, n, size));
          } else {
            break loop;
          }
        }
      }
      all.clear();
      all.addAll(pending);
    }

    pending.clear();
    System.out.print('"');
    System.out.print("m,n,size"); //$NON-NLS-1$
    System.out.print('"');

    for (final __row row : all) {
      System.out.println(',');
      System.out.print('"');
      System.out.print(row.m);
      System.out.print(',');
      System.out.print(row.n);
      System.out.print(',');
      System.out.print(row.size);
      System.out.print('"');
    }
  }

  /** one row of the diagram */
  private static final class __row {
    /** the m */
    final int m;
    /** the n */
    final int n;
    /** the size */
    final BigInteger size;

    /**
     * create a new row
     *
     * @param _m
     *          the m
     * @param _n
     *          the n
     * @param _size
     *          the size
     */
    __row(final int _m, final int _n, final BigInteger _size) {
      super();
      this.m = _m;
      this.n = _n;
      this.size = Objects.requireNonNull(_size);
    }
  }
}

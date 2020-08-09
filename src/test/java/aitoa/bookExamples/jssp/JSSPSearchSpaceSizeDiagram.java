package aitoa.bookExamples.jssp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Print the search space sizes for the JSSP instances in form of
 * a diagram
 */
public final class JSSPSearchSpaceSizeDiagram {

  /**
   * The main routine
   *
   * @param args
   *          ignore
   */
  public static void main(final String[] args) {
    final ArrayList<Row> all = new ArrayList<>();
    final ArrayList<Row> pending = new ArrayList<>();

    loop: for (int max = 1;; max++) {
      pending.clear();
      for (int m = 1; m <= max; m++) {
        for (int n = 1; n <= max; n++) {
          final BigInteger size =
              JSSPSearchSpaceSize.searchSpaceSize(m, n);
          if (Double.isFinite(size.doubleValue())) {
            pending.add(new Row(m, n, size));
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

    for (final Row row : all) {
      System.out.println(',');
      System.out.print('"');
      System.out.print(row.mM);
      System.out.print(',');
      System.out.print(row.mN);
      System.out.print(',');
      System.out.print(row.mSize);
      System.out.print('"');
    }
  }

  /** one row of the diagram */
  private static final class Row {
    /** the m */
    final int mM;
    /** the n */
    final int mN;
    /** the size */
    final BigInteger mSize;

    /**
     * create a new row
     *
     * @param pM
     *          the m
     * @param pN
     *          the n
     * @param pSize
     *          the size
     */
    Row(final int pM, final int pN, final BigInteger pSize) {
      super();
      this.mM = pM;
      this.mN = pN;
      this.mSize = Objects.requireNonNull(pSize);
    }
  }

  /** forbidden */
  private JSSPSearchSpaceSizeDiagram() {
    throw new UnsupportedOperationException();}
}

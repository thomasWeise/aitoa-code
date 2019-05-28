package aitoa.bookExamples.jssp;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.LongConsumer;

import aitoa.examples.jssp.JSSPCandidateSolution;
import aitoa.examples.jssp.JSSPExperiment;
import aitoa.examples.jssp.JSSPInstance;
import aitoa.examples.jssp.JSSPMakespanObjectiveFunction;
import aitoa.examples.jssp.JSSPNullaryOperator;
import aitoa.examples.jssp.JSSPRepresentationMapping;

/** Try to estimate the epistasis in our JSSP representation */
public class JSSPVariableImpact {

  /**
   * The main routine
   *
   * @param args
   *          ignore
   */
  public static final void main(final String[] args) {
    final HashSet<String> list = new HashSet<>();
    list.addAll(Arrays.asList(JSSPExperiment.INSTANCES));
    list.add("ft20"); //$NON-NLS-1$
    list.add("orb10"); //$NON-NLS-1$
    final String[] instances =
        list.toArray(new String[list.size()]);
    Arrays.sort(instances);

    for (final String inst : instances) {
      System.out.println("Now processing instance " + inst); //$NON-NLS-1$
      final JSSPInstance instance = new JSSPInstance(inst);
      final int n = instance.n;
      final int nm = n * instance.m;
      final __Stddev[][] stddevs = new __Stddev[nm][n];
      final __Mean[][] means = new __Mean[nm][n];
      for (final __Stddev[] x : stddevs) {
        for (int i = x.length; (--i) >= 0;) {
          x[i] = new __Stddev();
        }
      }
      for (final __Mean[] x : means) {
        for (int i = x.length; (--i) >= 0;) {
          x[i] = new __Mean();
        }
      }

      final JSSPNullaryOperator create =
          new JSSPNullaryOperator(instance);
      final int[] x = new int[nm];
      final JSSPRepresentationMapping map =
          new JSSPRepresentationMapping(instance);
      final JSSPCandidateSolution y =
          new JSSPCandidateSolution(instance.m, instance.n);
      final JSSPMakespanObjectiveFunction f =
          new JSSPMakespanObjectiveFunction(instance);

      final Random random = new Random(-334945236459211234L);

      for (int i = 1024 * 1024 * 1024; (--i) >= 0;) {
        create.apply(x, random);
        map.map(x, y);
        final int q = ((int) (f.evaluate(y)));
        for (int j = nm; (--j) >= 0;) {
          final int k = x[j];
          stddevs[j][k].accept(q);
          means[j][k].accept(q);
        }
      }

      final double[] stddevOfStddevs = new double[nm];
      final double[] stddevOfMeans = new double[nm];
      final __Stddev stddev = new __Stddev();
      for (int i = nm; (--i) >= 0;) {
        stddev._reset();
        for (final __Stddev sd : stddevs[i]) {
          stddev.accept(sd.getAsDouble());
        }
        stddevOfStddevs[i] = stddev.getAsDouble();
        stddev._reset();

        for (final __Mean m : means[i]) {
          stddev.accept(m.getAsDouble());
        }
        stddevOfMeans[i] = stddev.getAsDouble();
      }

      try (final FileWriter w = new FileWriter(inst + ".txt"); //$NON-NLS-1$
          final PrintWriter p = new PrintWriter(w)) {
        p.println("i,stddevOfStddevs,stddevOfMeans");//$NON-NLS-1$

        for (int i = 0; i < nm; i++) {
          p.print(i + 1);
          p.print(',');
          p.print(stddevOfStddevs[i]);
          p.print(',');
          p.println(stddevOfMeans[i]);
        }
      } catch (final Throwable error) {
        error.printStackTrace();
      }
    }
  }

  /** an internal Kahan Sum implementation */
  private static final class __StableSum
      implements DoubleConsumer, DoubleSupplier {
    /** the item sum */
    private double m_sum;
    /** the sum of squares */
    private double m_carry;

    /** create the stable sum */
    __StableSum() {
      super();
    }

    /** {@inheritDoc} */
    @Override
    public double getAsDouble() {
      return this.m_sum;
    }

    /** {@inheritDoc} */
    @Override
    public final void accept(final double value) {
      final double y = value - this.m_carry;
      final double t = this.m_sum + y;
      this.m_carry = (t - this.m_sum) - y;
      this.m_sum = t;
    }

    /** reset the stable sum */
    final void _reset() {
      this.m_carry = 0d;
      this.m_sum = 0d;
    }
  }

  /** the mean */
  private static final class __Mean
      implements LongConsumer, DoubleSupplier {
    /** the counter */
    private long m_count;
    /** the item sum */
    private long m_sum;

    /** create */
    __Mean() {
      super();
    }

    /** {@inheritDoc} */
    @Override
    public final void accept(final long value) {
      this.m_sum = Math.addExact(this.m_sum, value);
      ++this.m_count;
    }

    /** {@inheritDoc} */
    @Override
    public final double getAsDouble() {
      if (this.m_count <= 0L) {
        return Double.NaN;
      }
      final long gcd = __Mean.__gcd(this.m_sum, this.m_count);
      return ((double) (this.m_sum / gcd))
          / (this.m_count / gcd);
    }

    /**
     * compute the gcd
     *
     * @param a
     *          the a
     * @param b
     *          the b
     * @return the gcd
     */
    private static final long __gcd(final long a, final long b) {
      return b == 0L ? a : __Mean.__gcd(b, a % b);
    }
  }

  /** the standard deviation */
  private static final class __Stddev
      implements LongConsumer, DoubleConsumer, DoubleSupplier {
    /** the counter */
    private long m_count;
    /** the item sum */
    private final __StableSum m_M;
    /** the sum of squares */
    private final __StableSum m_S;

    /** create */
    __Stddev() {
      super();
      this.m_M = new __StableSum();
      this.m_S = new __StableSum();
    }

    /** {@inheritDoc} */
    @Override
    public final void accept(final long value) {
      final double xMinusOldM = (value - this.m_M.getAsDouble());
      this.m_M.accept((xMinusOldM) / (++this.m_count));
      this.m_S.accept(//
          (value - this.m_M.getAsDouble()) * xMinusOldM);
    }

    /** {@inheritDoc} */
    @Override
    public final void accept(final double value) {
      final double xMinusOldM = (value - this.m_M.getAsDouble());
      this.m_M.accept((xMinusOldM) / (++this.m_count));
      this.m_S.accept(//
          (value - this.m_M.getAsDouble()) * xMinusOldM);
    }

    /** {@inheritDoc} */
    @Override
    public final double getAsDouble() {
      if (this.m_count <= 0L) {
        return Double.NaN;
      }
      return Math.sqrt(this.m_S.getAsDouble() / this.m_count);
    }

    /** reset */
    final void _reset() {
      this.m_count = 0L;
      this.m_M._reset();
      this.m_S._reset();
    }
  }
}

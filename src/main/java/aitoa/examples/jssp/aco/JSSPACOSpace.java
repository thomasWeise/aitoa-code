package aitoa.examples.jssp.aco;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Objects;

import aitoa.examples.jssp.JSSPInstance;
import aitoa.examples.jssp.JSSPSolutionSpace;
import aitoa.structure.ISpace;
import aitoa.utils.math.BigMath;

/** The space for the PACO individuals */
public class JSSPACOSpace implements ISpace<JSSPACOIndividual> {
  /** the internal {@link JSSPSolutionSpace} */
  private final JSSPSolutionSpace mY;

  /**
   * create
   *
   * @param pInstance
   *          the problem instance
   */
  public JSSPACOSpace(final JSSPInstance pInstance) {
    super();
    this.mY = new JSSPSolutionSpace(pInstance);
  }

  /** {@inheritDoc} */
  @Override
  public JSSPACOIndividual create() {
    return new JSSPACOIndividual(this.mY.instance.m,
        this.mY.instance.n);
  }

  /** {@inheritDoc} */
  @Override
  public void copy(final JSSPACOIndividual from,
      final JSSPACOIndividual to) {
    System.arraycopy(from.permutation, 0, to.permutation, 0,
        to.permutation.length);
    this.mY.copy(from.solution, to.solution);
    to.makespan = from.makespan;
  }

  /** {@inheritDoc} */
  @Override
  public void print(final JSSPACOIndividual z,
      final Appendable out) throws IOException {
    out.append("new int[] "); //$NON-NLS-1$
    char ch = '{';
    for (final int i : z.permutation) {
      out.append(ch);
      ch = ',';
      out.append(Integer.toString(i));
    }
    out.append('}');
    out.append(System.lineSeparator());
    out.append(System.lineSeparator());
    this.mY.print(z.solution, out);
    out.append(System.lineSeparator());
    out.append(System.lineSeparator());
    out.append("makespan: "); //$NON-NLS-1$
    out.append(Integer.toString(z.makespan));
  }

  /** {@inheritDoc} */
  @Override
  public void check(final JSSPACOIndividual z) {
    Objects.requireNonNull(z);

    this.mY.check(z.solution);

    if (z.makespan <= 0) {
      throw new IllegalArgumentException(
          "Invalid makespan: " + z.makespan);//$NON-NLS-1$
    }

    Objects.requireNonNull(z.permutation);
    final int l = this.mY.instance.m * this.mY.instance.n;
    if (z.permutation.length != l) {
      throw new IllegalArgumentException(
          "Invalid length " + z.permutation.length//$NON-NLS-1$
              + " of permutation, must be " + l);//$NON-NLS-1$
    }

    final boolean[] visited = new boolean[l];
    for (final int i : z.permutation) {
      if (!(visited[i] ^= true)) {
        throw new IllegalArgumentException(//
            "Value " + i + //$NON-NLS-1$
                " encountered twice in permutation.");//$NON-NLS-1$
      }
    }

    for (int i = visited.length; (--i) >= 0;) {
      if (!visited[i]) {
        throw new IllegalArgumentException(//
            "Value " + i + //$NON-NLS-1$
                " not encountered in permutation.");//$NON-NLS-1$
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public final double getScale() {
    return BigMath.ld(BigMath.factorial(BigInteger.valueOf(//
        this.mY.instance.m * this.mY.instance.n)));
  }
}

package aitoa.examples.jssp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

/**
 * Obtain the best-known makespan of any solution for an JSSP
 * instance
 */
public class JSSPMakespanBKS
    implements ToDoubleFunction<String>, ToIntFunction<String> {

  /** create */
  public JSSPMakespanBKS() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public final int applyAsInt(final String value) {
    final Integer i = Holder.DATA.get(value);
    if (i != null) {
      return i.intValue();
    }
    throw new IllegalArgumentException(
        "Unknown instance '" + value + //$NON-NLS-1$
            "'."); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public final double applyAsDouble(final String value) {
    return this.applyAsInt(value);
  }

  /** the data holder */
  private static final class Holder {

    /** the data */
    static final HashMap<String, Integer> DATA =
        Holder.loadData();

    /**
     * Load the data
     *
     * @return the data
     */
    private static HashMap<String, Integer> loadData() {
      final String resource = "instances_with_bks.txt";//$NON-NLS-1$

      try {
        final HashMap<String, Integer> result = new HashMap<>();

        try (
            final InputStream is = JSSPMakespanBKS.class
                .getResourceAsStream(resource);
            final InputStreamReader isr =
                new InputStreamReader(is);
            final BufferedReader br = new BufferedReader(isr)) {
          if (br.readLine() == null) {
            throw new IllegalStateException("Header missing.");//$NON-NLS-1$
          }

          int line = 1;
          String s = null;
          while ((s = br.readLine()) != null) {
            s = s.trim();
            if (s.isEmpty()) {
              throw new IllegalStateException(
                  "Blank line: " + line);//$NON-NLS-1$
            }

            ++line;
            final String instance;
            final int bks;

            load: {
              int i = s.indexOf(',');
              if (i > 0) {
                instance = s.substring(0, i).trim();
                if (instance.isEmpty()) {
                  throw new IllegalArgumentException(
                      ("Empty instance id in line '" + //$NON-NLS-1$
                          s + "' (") + line + ')'); //$NON-NLS-1$
                }
                i = s.indexOf(',', i + 1);
                if (i > 0) {
                  i = s.indexOf(',', i + 1);
                  if (i > 0) {
                    i = s.indexOf(',', i + 1);
                    if (i > 0) {
                      int j = i + 1;
                      i = s.indexOf(',', j);
                      if (i > j) {
                        final int lb;
                        try {
                          lb = Integer.parseInt(
                              s.substring(j, i).trim());
                          if (lb <= 0) {
                            throw new IllegalArgumentException(
                                "lower bound must be > 0, but is "//$NON-NLS-1$
                                    + lb);
                          }
                        } catch (final Throwable error) {
                          throw new IllegalStateException(
                              "Invalid lb in string '"//$NON-NLS-1$
                                  + s + "' in line " + line, //$NON-NLS-1$
                              error);
                        }
                        i = s.indexOf(',', i + 1);
                        if (i > 0) {
                          j = i + 1;
                          i = s.indexOf(',', j);
                          if (i > j) {
                            try {
                              bks = Integer.parseInt(
                                  s.substring(j, i).trim());
                              if (bks < lb) {
                                throw new IllegalArgumentException(
                                    "BKS " + bks //$NON-NLS-1$
                                        + " must be >= lower bound " //$NON-NLS-1$
                                        + lb);
                              }
                              break load;
                            } catch (final Throwable error) {
                              throw new IllegalStateException(
                                  "Invalid bks in string '"//$NON-NLS-1$
                                      + s + "' in line " + line, //$NON-NLS-1$
                                  error);
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
              throw new IllegalStateException(
                  "Not enough columns in string '"//$NON-NLS-1$
                      + s + "' in line " + line);//$NON-NLS-1$
            }

            if (result.put(instance,
                Integer.valueOf(bks)) != null) {
              throw new IllegalStateException(
                  ("Instance id '" + instance//$NON-NLS-1$
                      + "' occurs twice (2nd time in line "//$NON-NLS-1$
                      + line) + ')');
            }
          }
        }

        if (result.isEmpty()) {
          throw new IllegalStateException("No BKS found!"); //$NON-NLS-1$
        }
        return result;
      } catch (final Throwable error) {
        throw new IllegalStateException(
            "Could not correctly load JSSP makespak BKS data from resource '" //$NON-NLS-1$
                + resource + "'.", //$NON-NLS-1$
            error);
      }
    }
  }
}

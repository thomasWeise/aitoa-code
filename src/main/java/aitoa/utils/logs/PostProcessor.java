package aitoa.utils.logs;

import aitoa.utils.Configuration;
import aitoa.utils.ConsoleIO;

/** The entry point for post-processing the data */
public class PostProcessor {

  /**
   * The main routine
   *
   * @param args
   *          the command line arguments
   */
  public static final void main(final String[] args) {
    final Class<?>[] classes = { //
        EndResults.class, //
        EndResultStatistics.class, //
        ErtEcdf.class, //
        IOHProfiler.class,//
    };

    final String[] choices = new String[classes.length];
    for (int i = classes.length; (--i) >= 0;) {
      choices[i] = classes[i].getSimpleName();
    }

    final String choice = "choice";//$NON-NLS-1$

    ConsoleIO.stdout((s) -> {
      s.println("Welcome to the AITOA Result Post-Processor"); //$NON-NLS-1$
      for (final String c : choices) {
        s.print(" -choice=");//$NON-NLS-1$
        s.print(c);
        s.print(": execute the ");//$NON-NLS-1$
        s.print(c);
        s.println(" utility.");//$NON-NLS-1$
      }
    });

    Configuration.putCommandLine(args);
    final String selected = Configuration.getString(choice);
    if (selected != null) {
      Class<?> taken = null;
      for (int i = choices.length; (--i) >= 0;) {
        if (choices[i].equalsIgnoreCase(selected)) {
          taken = classes[i];
          break;
        }
      }

      if (taken == null) {
        ConsoleIO.stdout(
            '\'' + selected + "' is not a valid choice.");//$NON-NLS-1$
        return;
      }

      try {
        taken.getMethod("main", String[].class)//$NON-NLS-1$
            .invoke(null, ((Object) (args)));
      } catch (final Throwable error) {
        ConsoleIO.stderr("Error when invoking the '"//$NON-NLS-1$
            + selected + "' tool.", //$NON-NLS-1$
            error);
      }
    }
  }
}

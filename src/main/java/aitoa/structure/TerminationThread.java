package aitoa.structure;

/**
 * <p>
 * This class is an internal class. Please do not instantiate it,
 * use it by yourself, or otherwise meddle with it.
 * <p>
 * This internal {@link java.lang.Thread thread} takes care of
 * end times. We could also check whether a runtime limit is
 * reached by calling
 * {@link java.lang.System#currentTimeMillis()}, but this would
 * be costly (system calls!) and waste time during optimization.
 * Thus, instead, we have this thread which
 * {@link java.lang.Object#wait(long) sleeps} until the time
 * limit of an objective function is reached and then updates its
 * {@link BlackBoxProcessBase#shouldTerminate() termination
 * criterion}. As one thread is shared for all instances of
 * {@link BlackBoxProcessBase}, this method is very
 * resource-friendly and saves runtime.
 * <p>
 * The thread works as a very simple, sorted queue. It is assumed
 * that there will never be too many problems running in
 * parallel, so we can simply use methods with linear complexity
 * to update and work on the queue. The elements in the queue are
 * sorted according to their termination time.
 */
final class TerminationThread extends Thread {

  /** the synchronizer */
  private static final Object SYNC = new Object();

  /** the queue */
  private static volatile BlackBoxProcessBase<?, ?> sQueue =
      null;
  /** the instance */
  private static volatile TerminationThread sInstance = null;

  /** create */
  private TerminationThread() {
    super();
    this.setDaemon(true);
  }

  /**
   * enqueue the objective function
   *
   * @param f
   *          the function
   */
  static void enqueue(final BlackBoxProcessBase<?, ?> f) {
    final long t;
    BlackBoxProcessBase<?, ?> prev, next;

    t = f.mEndTime; // throw NullPointerException if null
    if ((t >= Long.MAX_VALUE) || (t <= 0L)) {
      throw new IllegalArgumentException(
          "Invalid end time for enquing: " //$NON-NLS-1$
              + t);
    }

    // find right place for insertion
    synchronized (TerminationThread.SYNC) {
      prev = null;
      next = TerminationThread.sQueue;

      while (next != null) {
        if (next == f) {
          throw new IllegalArgumentException(
              "Attempt to enqueue problem twice!"); //$NON-NLS-1$
        }
        if (next.mEndTime > f.mEndTime) {
          break; // found it
        }
        prev = next;
        next = next.mNext;
      }

      f.mNext = next;
      if (prev != null) {
        // no need to wake up thread, as there is already a
        // pending sleep
        prev.mNext = f;
        return;
      }

      TerminationThread.sQueue = f;

      if (TerminationThread.sInstance == null) {
        // create thread if necessary
        TerminationThread.sInstance = new TerminationThread();
        TerminationThread.sInstance.start();
        return;
      }

      // we have new first element, wake up thread to check
      TerminationThread.SYNC.notifyAll();
    }
  }

  /**
   * dequeue an objective function
   *
   * @param f
   *          the function
   */
  static void dequeue(final BlackBoxProcessBase<?, ?> f) {
    BlackBoxProcessBase<?, ?> cur, next;

    if (f == null) {
      throw new NullPointerException(//
          "null function?"); //$NON-NLS-1$
    }

    synchronized (TerminationThread.SYNC) {
      cur = null;
      next = TerminationThread.sQueue;

      // find element in queue
      while (next != null) {
        if (next == f) {
          if (cur == null) {
            // delete first element
            TerminationThread.sQueue = next.mNext;
            TerminationThread.SYNC.notifyAll();
            return;
          }
          // delete element which is not the first one
          cur.mNext = next.mNext;
          return;
        }
        cur = next;
        next = next.mNext;
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public void run() {
    // main loop
    for (;;) {
      final long time = System.currentTimeMillis();

      synchronized (TerminationThread.SYNC) {

        inner: for (;;) {
          if (TerminationThread.sQueue == null) {
            // nothing pending anymore: quit thread
            TerminationThread.sInstance = null;
            return;
          }

          if (TerminationThread.sQueue.mEndTime <= time) {
            // terminate one element from queue
            TerminationThread.sQueue.mTerminated = true;
            TerminationThread.sQueue =
                TerminationThread.sQueue.mNext;
          } else {
            break inner;
          }
        }

        try {
          TerminationThread.SYNC.wait(Math.max(0L,
              (TerminationThread.sQueue.mEndTime - time)));
        } catch (@SuppressWarnings("unused") //
        final InterruptedException ie) {
          continue;
        }
      }
    }
  }
}

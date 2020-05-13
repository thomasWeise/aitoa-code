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
 * {@link _BlackBoxProcessBase#shouldTerminate() termination
 * criterion}. As one thread is shared for all instances of
 * {@link _BlackBoxProcessBase}, this method is very
 * resource-friendly and saves runtime.
 * <p>
 * The thread works as a very simple, sorted queue. It is assumed
 * that there will never be too many problems running in
 * parallel, so we can simply use methods with linear complexity
 * to update and work on the queue. The elements in the queue are
 * sorted according to their termination time.
 */
final class _TerminationThread extends Thread {

  /** the synchronizer */
  private static final Object SYNC = new Object();

  /** the queue */
  private static volatile _BlackBoxProcessBase<?, ?> s_queue =
      null;
  /** the instance */
  private static volatile _TerminationThread s_instance = null;

  /** create */
  private _TerminationThread() {
    super();
    this.setDaemon(true);
  }

  /**
   * enqueue the objective function
   *
   * @param f
   *          the function
   */
  static void _enqueue(final _BlackBoxProcessBase<?, ?> f) {
    final long t;
    _BlackBoxProcessBase<?, ?> prev, next;

    t = f.m_endTime; // throw NullPointerException if null
    if ((t >= Long.MAX_VALUE) || (t <= 0L)) {
      throw new IllegalArgumentException(
          "Invalid end time for enquing: " //$NON-NLS-1$
              + t);
    }

    // find right place for insertion
    synchronized (_TerminationThread.SYNC) {
      prev = null;
      next = _TerminationThread.s_queue;

      while (next != null) {
        if (next == f) {
          throw new IllegalArgumentException(
              "Attempt to enqueue problem twice!"); //$NON-NLS-1$
        }
        if (next.m_endTime > f.m_endTime) {
          break; // found it
        }
        prev = next;
        next = next.m_next;
      }

      f.m_next = next;
      if (prev != null) {
        // no need to wake up thread, as there is already a
        // pending sleep
        prev.m_next = f;
        return;
      }

      _TerminationThread.s_queue = f;

      if (_TerminationThread.s_instance == null) {
        // create thread if necessary
        _TerminationThread.s_instance = new _TerminationThread();
        _TerminationThread.s_instance.start();
        return;
      }

      // we have new first element, wake up thread to check
      _TerminationThread.SYNC.notifyAll();
    }
  }

  /**
   * dequeue an objective function
   *
   * @param f
   *          the function
   */
  static void _dequeue(final _BlackBoxProcessBase<?, ?> f) {
    _BlackBoxProcessBase<?, ?> cur, next;

    if (f == null) {
      throw new NullPointerException(//
          "null function?"); //$NON-NLS-1$
    }

    synchronized (_TerminationThread.SYNC) {
      cur = null;
      next = _TerminationThread.s_queue;

      // find element in queue
      while (next != null) {
        if (next == f) {
          if (cur == null) {
            // delete first element
            _TerminationThread.s_queue = next.m_next;
            _TerminationThread.SYNC.notifyAll();
            return;
          }
          // delete element which is not the first one
          cur.m_next = next.m_next;
          return;
        }
        cur = next;
        next = next.m_next;
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public void run() {
    // main loop
    for (;;) {
      final long time = System.currentTimeMillis();

      synchronized (_TerminationThread.SYNC) {

        inner: for (;;) {
          if (_TerminationThread.s_queue == null) {
            // nothing pending anymore: quit thread
            _TerminationThread.s_instance = null;
            return;
          }

          if (_TerminationThread.s_queue.m_endTime <= time) {
            // terminate one element from queue
            _TerminationThread.s_queue.m_terminated = true;
            _TerminationThread.s_queue =
                _TerminationThread.s_queue.m_next;
          } else {
            break inner;
          }
        }

        try {
          _TerminationThread.SYNC.wait(Math.max(0L,
              (_TerminationThread.s_queue.m_endTime - time)));
        } catch (@SuppressWarnings("unused") //
        final InterruptedException ie) {
          continue;
        }
      }
    }
  }
}

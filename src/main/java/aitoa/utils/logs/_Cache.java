package aitoa.utils.logs;

import java.util.HashMap;

/** a quick and dirty internal cache */
final class _Cache extends HashMap<String, String> {

  /** ignore */
  private static final long serialVersionUID = 1L;

  /** create */
  _Cache() {
    super();
  }

  /**
   * cache a string
   *
   * @param s
   *          the string
   * @return the cached version of the string
   */
  String _string(final String s) {
    if (s.isEmpty()) {
      throw new IllegalArgumentException(
          "Cached strings must not be empty."); //$NON-NLS-1$
    }
    final String ret = this.get(s);
    if (ret != null) {
      return ret;
    }
    this.put(s, s);
    return s;
  }
}

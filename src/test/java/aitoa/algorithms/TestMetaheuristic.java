package aitoa.algorithms;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import aitoa.ObjectTest;
import aitoa.TestTools;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.LogFormat;

/**
 * Test a metaheuristic
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
@Ignore
public abstract class TestMetaheuristic<X, Y>
    extends ObjectTest<IMetaheuristic<X, Y>> {
  /**
   * test printing the setup
   *
   * @throws IOException
   *           if the method fails
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testPrintSetup() throws IOException {
    char[] text = null;
    try (CharArrayWriter caw = new CharArrayWriter()) {
      this.getInstance().printSetup(caw);
      text = caw.toCharArray();
    }
    Assert.assertNotNull(text);
    TestTools.assertGreater(text.length, 0);
    int count = 0;
    try (CharArrayReader car = new CharArrayReader(text);
        BufferedReader br = new BufferedReader(car)) {
      String s = null;
      for (;;) {
        s = br.readLine();
        if (s == null) {
          break;
        }
        count++;
        TestTools.assertGreaterOrEqual(s.length(), 6);
        Assert.assertEquals(s.charAt(0), LogFormat.COMMENT_CHAR);
        s = s.substring(1).trim();
        Assert.assertEquals(s.length(), s.trim().length());
        int i = s.indexOf(LogFormat.MAP_SEPARATOR_CHAR);
        TestTools.assertInRange(i, 1, s.length() - 3);
        Assert.assertEquals(' ', s.charAt(i + 1));
        Assert.assertEquals(-1,
            s.indexOf(LogFormat.MAP_SEPARATOR_CHAR, i + 1));
        Assert.assertEquals(-1, s.indexOf('\t'));
        boolean flip = false;
        for (i = s.length(); (--i) >= 0;) {
          final char ch = s.charAt(i);
          if (((ch >= 'a') && (ch <= 'z'))
              || ((ch >= 'A') && (ch <= 'Z'))
              || ((ch >= '0') && (ch <= '9')) || (ch == ':')
              || (ch == '.') || (ch == '@') || (ch == '_')
              || (ch == '-') || (ch == ',') || (ch == '+')
              || (ch == '(') || (ch == ')')) {
            flip = false;
            continue;
          }
          if (ch == ' ') {
            if (flip) {
              Assert.fail("double spaces not allowed");//$NON-NLS-1$
            }
            flip = true;
            continue;
          }
          Assert.fail(("invalid character '" + //$NON-NLS-1$
              ch) + '\'');
        }
      }
      TestTools.assertGreater(count, 0);
    }
  }
}

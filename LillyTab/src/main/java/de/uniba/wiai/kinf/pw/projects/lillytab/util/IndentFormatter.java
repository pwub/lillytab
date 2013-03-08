/**
 * (c) 2009-2012 Otto-Friedrich-University Bamberg
 *
 * $Id$
 *
 * Use, modification and restribution of this file are covered by the terms of the Artistic License 2.0.
 *
 * You should have received a copy of the license terms in a file named "LICENSE" together with this software package.
 *
 * Disclaimer of Warranty: THE PACKAGE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS "AS IS' AND WITHOUT ANY
 * EXPRESS OR IMPLIED WARRANTIES. THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT ARE DISCLAIMED TO THE EXTENT PERMITTED BY YOUR LOCAL LAW. UNLESS REQUIRED BY LAW, NO COPYRIGHT
 * HOLDER OR CONTRIBUTOR WILL BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING IN ANY
 * WAY OUT OF THE USE OF THE PACKAGE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.util;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class IndentFormatter
	extends java.util.logging.Formatter {

	public static final String INDENT_SEVERE = "!!!!!";
	public static final String INDENT_WARNING = "!!!";
	public static final String INDENT_INFO = "";
	public static final String INDENT_FINE = "####";
	public static final String INDENT_FINER = "########";
	public static final String INDENT_FINEST = "############";
	public static final String UNKNOWN_INDENT_STRING = "???";


	public IndentFormatter()
	{
	}


	public static String multiplyString(final String item, final int count)
	{
		final StringBuilder sb = new StringBuilder(item.length() * count);
		for (int i = 0; i < count; ++i) {
			sb.append(item);
		}
		final String value = sb.toString();
		return value.intern();
	}


	private static String levelToIndent(final Level level)
	{
		if (level == Level.SEVERE) {
			return INDENT_SEVERE;
		} else if (level == Level.WARNING) {
			return INDENT_WARNING;
		} else if (level == Level.INFO) {
			return INDENT_INFO;
		} else if (level == Level.FINE) {
			return INDENT_FINE;
		} else if (level == Level.FINER) {
			return INDENT_FINER;
		} else if (level == Level.FINEST) {
			return INDENT_FINEST;
		} else {
			return "???";
		}
	}


	public static String formatRight(String str, final int width)
	{
		final StringBuilder sb = new StringBuilder(width);
		final int offset = str.length() - width;
		if (offset > 0) {
			sb.append(str.substring(offset));
		} else {
			sb.append(str);
			while (sb.length() < width) {
				sb.append(' ');
			}
		}
		return sb.toString();
	}
	private Map<String, String> _nameCache = new HashMap<>();


	public String formatLoggerName(final String longName, final int width)
	{
		if (_nameCache.containsKey(longName)) {
			return _nameCache.get(longName);
		} else {
			final StringBuilder sb = new StringBuilder(width);

			String[] parts = longName.split("\\.");
			int nonShortPartCount = 0;
			for (String part : parts) {
				if (part.length() > 1) {
					++nonShortPartCount;
				}
			};
			sb.append(longName);
			while ((sb.length() > width) && (nonShortPartCount > 1)) {
				sb.delete(0, sb.length());
				/* do not shorten the last part */
				for (int i = 0; i < parts.length - 1; ++i) {
					if (parts[i].length() > 1) {
						parts[i] = parts[i].substring(0, 1);
						--nonShortPartCount;
					}
				}
				for (int i = 0; i < parts.length; ++i) {
					if (i != 0) {
						sb.append(".");
					}
					sb.append(parts[i]);
				}
			}
			final String shortName = formatRight(sb.toString(), width);
			_nameCache.put(longName, shortName);
			return shortName;
		}
	}


	@Override
	public String format(LogRecord record)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(String.format("%10d", record.getMillis()));
		sb.append(levelToIndent(record.getLevel()));
		sb.append("[");
		sb.append(formatLoggerName(record.getLoggerName(), 30));
		sb.append("] ");
		sb.append(record.getMessage());
		sb.append("\n");
		return sb.toString();
	}
}

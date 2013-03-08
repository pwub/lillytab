/**
 * (c) 2009-2012 Peter Wullinger
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
package de.dhke.projects.lutil;

import java.util.Locale;

/**
 * <p>
 * Lazy formatter provides a method to pass on a format string and its parameter objects and delay formatting until the
 * formatter's {
 *
 * @see #toString()} method is called.
 * </p><p>
 * Use with care: There are side effects when the parameter objects are modified after they lazy formatter has been
 * constructed. In this case the the modified versions of the parameter objects will be used. This can also result in
 * multiple calls to {
 * @see #toString()} yielding different results.
 * <p>
 *
 * @author Peter Wullinger <java@dhke.de>
 *
 */
public class LazyFormatter {

	private final Locale _locale;
	private final String _format;
	private final Object[] _params;


	protected LazyFormatter(final String format, final Object... params)
	{
		this(Locale.getDefault(), format, params);
	}


	protected LazyFormatter(final Object obj)
	{
		this(Locale.getDefault(), "%s", obj);
	}


	protected LazyFormatter(final Locale locale, final String format, final Object... params)
	{
		_locale = locale;
		_format = format;
		_params = params;
	}


	public static LazyFormatter decorate(final String format, final Object... params)
	{
		return new LazyFormatter(format, params);
	}


	public static LazyFormatter decorate(final Object obj)
	{
		return new LazyFormatter(obj);
	}


	public static LazyFormatter decorate(final Locale locale, final String format, final Object... params)
	{
		return new LazyFormatter(locale, format, params);
	}


	@Override
	public String toString()
	{
		return String.format(_locale, _format, _params);
	}
}

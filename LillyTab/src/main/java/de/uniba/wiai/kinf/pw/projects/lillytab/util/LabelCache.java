/**
 * (c) 2009-2012 Otto-Friedrich-University Bamberg
 *
 * $Id$
 *
 * Use, modification and restribution of this file are covered by the
 * terms of the Artistic License 2.0.
 *
 * You should have received a copy of the license terms in a file named
 * "LICENSE" together with this software package.
 *
 * Disclaimer of Warranty: THE PACKAGE IS PROVIDED BY THE COPYRIGHT
 * HOLDER AND CONTRIBUTORS "AS IS' AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES. THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
 * A PARTICULAR PURPOSE, OR NON-INFRINGEMENT ARE DISCLAIMED TO THE
 * EXTENT PERMITTED BY YOUR LOCAL LAW. UNLESS REQUIRED BY LAW, NO
 * COPYRIGHT HOLDER OR CONTRIBUTOR WILL BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING IN ANY WAY OUT
 * OF THE USE OF THE PACKAGE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 **/
package de.uniba.wiai.kinf.pw.projects.lillytab.util;

import java.util.WeakHashMap;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class LabelCache
	implements IToStringFormatter {


	public static LabelCache decorate(final IToStringFormatter baseFormatter)
	{
		return new LabelCache(baseFormatter);
	}

	private final IToStringFormatter _baseFormatter;
	private final WeakHashMap<Object, String> _labelCache = new WeakHashMap<>();


	@Deprecated
	public LabelCache(final IToStringFormatter baseFormatter)
	{
		_baseFormatter = baseFormatter;
	}


	@Override
	public String toString(Object obj)
	{
		String label = _labelCache.get(obj);
		if (label == null) {
			label = _baseFormatter.toString(obj);
			_labelCache.put(obj, label);
		}
		return label;
	}


	@Override
	public void append(StringBuilder sb, Object obj)
	{
		final String label = toString(obj);
		sb.append(label);
	}
}

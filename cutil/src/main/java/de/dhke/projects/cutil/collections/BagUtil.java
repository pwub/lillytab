/**
 * (c) 2009-2014 Peter Wullinger
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
package de.dhke.projects.cutil.collections;

import org.apache.commons.collections15.Bag;


/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class BagUtil
{
	public static <T> Bag<T> unionBags(final Bag<T> b0, final Bag<T> b1, final Bag<T> targetBag)
	{
		targetBag.clear();
		for (T item : b0) {
			final int count0 = b0.getCount(item);
			final int count1 = b1.getCount(item);
			targetBag.add(item, Math.max(count0, count1));
		}
		for (T item : b1) {
			if (!b0.contains(item)) {
				final int count1 = b1.getCount(item);
				targetBag.add(item, count1);
			}
		}
		return targetBag;
	}

	public static <T> Bag<T>
		intersectBags(final Bag<T> b0, final Bag<T> b1, final Bag<T> targetBag)
	{
		targetBag.clear();
		for (T item : b0) {
			final int count0 = b0.getCount(item);
			final int count1 = b1.getCount(item);
			if ((count0 > 0) && (count1 > 0)) {
				targetBag.add(item, Math.min(count0, count1));
			}
		}
		return targetBag;
	}
}

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
package de.dhke.projects.cutil.collections.aspect;

import java.util.Collection;
import java.util.Iterator;
import org.junit.Assert;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public final class TestHelper {
	private TestHelper()
	{
	}

	public static <E> boolean eventListContains(E item, Collection<? extends CollectionItemEvent<E, ?>> eventList)
	{
		for (CollectionItemEvent<E, ?> event: eventList) {
			if (item.equals(event.getItem()))
				return true;
		}
		return false;
	}

	public static <E> void assertSequenceEquals(Iterable<E> seq1, Iterable<E> seq2)
	{
		int idx = 0;
		Iterator<E> iter1 = seq1.iterator();
		Iterator<E> iter2 = seq2.iterator();

		while (iter1.hasNext() && iter2.hasNext()) {
			if (! iter1.next().equals(iter2.next()))
				Assert.fail(seq1.toString() + " and " + seq2.toString() + " differ at position " + idx);
			++idx;
		}
		if (iter1.hasNext() != iter2.hasNext())
			Assert.fail(seq1.toString() + " and " + seq2.toString() + " are of different size");
	}
}

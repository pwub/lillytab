/**
 * (c) 2009-2012 Peter Wullinger
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
package de.dhke.projects.cutil.collections.set;

import java.util.*;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class SortedListSet<T extends Comparable<? super T>>
	extends AbstractList<T>
	implements Set<T> {

	private List<T> _backingList;


	public SortedListSet(final Collection<? extends T> collection)
	{
		_backingList = new ArrayList<T>(collection);
		Collections.sort(_backingList);
		removeDuplicates();
	}


	private void removeDuplicates()
	{
		final Iterator<T> iter = _backingList.iterator();
		T lastObj = null;		
		/* remove duplicates */
		while (iter.hasNext()) {
			final T obj = iter.next();
			if ((lastObj == obj) || ((obj != null) && obj.equals(lastObj)))
				iter.remove();
			lastObj = obj;				
		}
	}


	@Override
	public T get(int index)
	{
		return _backingList.get(index);
	}


	@Override
	public int size()
	{
		return _backingList.size();
	}


	@Override
	public boolean add(final T e)
	{
		final boolean added = _backingList.add(e);
		if (added)
			Collections.sort(_backingList);
		return added;
	}


	@Override
	public boolean addAll(Collection<? extends T> c)
	{
		final boolean added = _backingList.addAll(c);
		if (added)
			Collections.sort(_backingList);
		return added;
	}
}

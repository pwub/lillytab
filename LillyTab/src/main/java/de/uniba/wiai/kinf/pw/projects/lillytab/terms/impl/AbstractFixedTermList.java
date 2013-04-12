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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl;

import de.dhke.projects.cutil.Pair;
import de.dhke.projects.cutil.collections.iterator.PairIterable;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.ITerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.ITermList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.list.FixedSizeList;
import org.apache.commons.collections15.list.TransformedList;

/**
 *
 * Basic implmementation {@link ITerm} list with a fixed number of subterms.
 *
 *
 * @param <Term> The type of the terms
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class AbstractFixedTermList<Term extends ITerm>
	implements ITermList<Term> {

	private static final long serialVersionUID = 7141058164156591514L;
	private final List<Term> _modifiableBackend;
	private final List<Term> _backend;
	/**
	 * Hashcode is cached for term lists, this should improve performance.
	 *
	 */
	private int _hashCode = 0;


	/**
	 *
	 * Construct a new {@link AbstractFixedTermList} using {@literal backend} as the backend list.
	 * <p /> {@literal backend} Must already be of the desired size.
	 *
	 *
	 * @param backend The backend list to use. Must be of the desired size.
	 */
	protected AbstractFixedTermList(final List<Term> backend)
	{
		_modifiableBackend = FixedSizeList.decorate(backend);
		_backend = Collections.unmodifiableList(_modifiableBackend);
	}


	/**
	 *
	 * Construct a new {@link AbstractFixedTermList} using an {@link ArrayList} for backend storage. The list will be
	 * filled with {@literal null}s up to the desired number of elements.
	 *
	 *
	 * @param size The desired size of the term list.
	 */
	protected AbstractFixedTermList(final int size)
	{
		ArrayList<Term> backendList = new ArrayList<>(size);
		for (int i = 0; i < size; ++i) {
			backendList.add(null);
		}
		_modifiableBackend = FixedSizeList.decorate(backendList);
		_backend = Collections.unmodifiableList(_modifiableBackend);
	}


	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj) {
			return true;
		}
		return equals(this, obj);
	}

	/// <editor-fold defaultstate="collapsed" desc="ITermList<Term>">

	@Override
	public List<ITerm> getTermList()
	{
		return TransformedList.decorate(this, new Transformer<Term, ITerm>() {
			@Override
			public ITerm transform(Term input)
			{
				return input;
			}
		});
	}


	public List<Term> getModifiableTermList()
	{
		return _modifiableBackend;
	}


	@Override
	public int size()
	{
		return _backend.size();
	}


	@Override
	public boolean isEmpty()
	{
		return _backend.isEmpty();
	}


	@Override
	public boolean contains(final Object o)
	{
		return _backend.contains(o);
	}


	@Override
	public Iterator<Term> iterator()
	{
		return _backend.iterator();
	}


	@Override
	public Object[] toArray()
	{
		return _backend.toArray();
	}


	@Override
	public <T> T[] toArray(final T[] a)
	{
		return _backend.toArray(a);
	}


	@Override
	public boolean add(final Term e)
	{
		return _backend.add(e);
	}


	@Override
	public boolean remove(final Object o)
	{
		return _backend.remove(o);
	}


	@Override
	public boolean containsAll(final Collection<?> c)
	{
		return _backend.containsAll(c);
	}


	@Override
	public boolean addAll(final Collection<? extends Term> c)
	{
		return _backend.addAll(c);
	}


	@Override
	public boolean addAll(final int index, final Collection<? extends Term> c)
	{
		return _backend.addAll(index, c);
	}


	@Override
	public boolean removeAll(final Collection<?> c)
	{
		return _backend.removeAll(c);
	}


	@Override
	public boolean retainAll(final Collection<?> c)
	{
		return _backend.retainAll(c);
	}


	@Override
	public void clear()
	{
		_backend.clear();
	}


	@Override
	public Term get(final int index)
	{
		return _backend.get(index);
	}


	@Override
	public Term set(final int index, final Term element)
	{
		return _backend.set(index, element);
	}


	@Override
	public void add(final int index, final Term element)
	{
		_backend.add(index, element);
	}


	@Override
	public Term remove(final int index)
	{
		return _backend.remove(index);
	}


	@Override
	public int indexOf(final Object o)
	{
		return _backend.indexOf(o);
	}


	@Override
	public int lastIndexOf(final Object o)
	{
		return _backend.lastIndexOf(o);
	}


	@Override
	public ListIterator<Term> listIterator()
	{
		return _backend.listIterator();
	}


	@Override
	public ListIterator<Term> listIterator(final int index)
	{
		return _backend.listIterator(index);
	}


	@Override
	public int hashCode()
	{
		if (_hashCode == 0) {
			int hCode = 31;
			for (ITerm subTerm : this) {
				hCode += subTerm.hashCode();
			}

			_hashCode = hCode;
		}
		return _hashCode;
	}


	/**
	 *
	 * Compare the the {@link ITermList} {@literal tl0} to the object {@literal o1}.
	 *
	 *
	 * @param <T> The type of the {@link ITermList}'s elements.
	 * @param tl0 The {@link ITermList} to compare.
	 * @param o1 The object to compare.
	 * @return {@literal true} if {@literal o1} is equal to {@literal tl0}
	 *
	 */
	public static <T extends ITerm> boolean equals(ITermList<T> tl0, Object o1)
	{
		if ((o1 instanceof ITermList) && (tl0 != null) && (o1 != null)) {
			@SuppressWarnings("unchecked")
			ITermList<T> tl1 = (ITermList<T>) o1;
			if (tl0.size() == tl1.size()) {
				for (Pair<T, T> pair : PairIterable.wrap(tl0, tl1)) {
					if (!pair.getFirst().equals(pair.getSecond())) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}


	@Override
	public List<Term> subList(final int fromIndex, final int toIndex)
	{
		return _backend.subList(fromIndex, toIndex);
	}
	/// </editor-fold>
}

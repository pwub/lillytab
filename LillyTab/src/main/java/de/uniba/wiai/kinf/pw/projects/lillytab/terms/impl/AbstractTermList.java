/**
 * (c) 2009-2014 Otto-Friedrich-University Bamberg
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

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.ITerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.ITermList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.list.FixedSizeList;
import org.apache.commons.collections15.list.TransformedList;

/**
 *
 * @param <Term> The type of the terms
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public class AbstractTermList<Term extends ITerm>
	implements ITermList<Term> {

	private static final long serialVersionUID = 7141058164156591514L;
	private final List<Term> _backend;


	protected AbstractTermList(final int size)
	{
		ArrayList<Term> backendList = new ArrayList<>(size);
		for (int i = 0; i < size; ++i) {
			backendList.add(null);
		}
		_backend = FixedSizeList.decorate(backendList);
	}

	/// <editor-fold defaultstate="collapsed" desc="ITermList<Term>">

	@Override
	public List<ITerm> getTermList()
	{
		return TransformedList.decorate(this,
										new Transformer<Term, ITerm>() {
			@Override
			public ITerm transform(Term input)
			{
				return input;
			}
		});
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
	public boolean addAll(final int index,
						  final Collection<? extends Term> c)
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
	public List<Term> subList(final int fromIndex, final int toIndex)
	{
		return _backend.subList(fromIndex, toIndex);
	}
	/// </editor-fold>
}

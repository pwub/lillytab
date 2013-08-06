/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.tbox;

import de.dhke.projects.cutil.collections.immutable.ImmutableIterator;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IAssertedRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.ITBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;


/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class ImmutableTBox<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	implements ITBox<I, L, K, R>
{
	private final ITBox<I, L, K, R> _baseTBox;
	private final IRBox<I, L, K, R> _rbox;

	public ImmutableTBox(final ITBox<I, L, K, R> tbox)
	{
		_baseTBox = tbox;
		_rbox = tbox.getRBox();
	}

	@Override
	public IRBox<I, L, K, R> getRBox()
	{
		return _rbox;
	}

	@Override
	public IAssertedRBox<I, L, K, R> getAssertedRBox()
	{
		return _rbox.getAssertedRBox();
	}

	@Override
	public Set<IDLClassExpression<I, L, K, R>> getGlobalDescriptions()
	{
		return Collections.unmodifiableSet(_baseTBox.getGlobalDescriptions());
	}

	@Override
	public Collection<IDLClassExpression<I, L, K, R>> getUnfolding(
		IDLClassExpression<I, L, K, R> unfoldee)
	{
		return _baseTBox.getUnfolding(unfoldee);
	}

	@Override
	public String toString(String prefix)
	{
		return _baseTBox.toString(prefix);
	}

	@Override
	public ITBox<I, L, K, R> clone()
	{
		return _baseTBox.clone();
	}

	@Override
	public ITBox<I, L, K, R> getImmutable()
	{
		return this;
	}

	@Override
	public <T extends IDLTerm<I, L, K, R>> Iterator<T> iterator(
		Class<? extends T> klass)
	{
		return ImmutableIterator.decorate(iterator(klass));
	}

	@Override
	public <T extends IDLTerm<I, L, K, R>> Iterator<T> iterator(DLTermOrder termType,
																Class<? extends T> klass)
	{
		return ImmutableIterator.decorate(iterator(termType, klass));
	}

	@Override
	public SortedSet<IDLTerm<I, L, K, R>> subSet(DLTermOrder termType)
	{
		return Collections.unmodifiableSortedSet(_baseTBox.subSet(termType));
	}

	@Override
	public Comparator<? super IDLTerm<I, L, K, R>> comparator()
	{
		return _baseTBox.comparator();
	}

	@Override
	public SortedSet<IDLTerm<I, L, K, R>> subSet(
		IDLTerm<I, L, K, R> fromElement,
		IDLTerm<I, L, K, R> toElement)
	{
		return Collections.unmodifiableSortedSet(_baseTBox.subSet(fromElement, toElement));
	}

	@Override
	public SortedSet<IDLTerm<I, L, K, R>> headSet(
		IDLTerm<I, L, K, R> toElement)
	{
		return Collections.unmodifiableSortedSet(_baseTBox.headSet(toElement));
	}

	@Override
	public SortedSet<IDLTerm<I, L, K, R>> tailSet(
		IDLTerm<I, L, K, R> fromElement)
	{
		return Collections.unmodifiableSortedSet(_baseTBox.tailSet(fromElement));
	}

	@Override
	public IDLTerm<I, L, K, R> first()
	{
		return _baseTBox.first();
	}

	@Override
	public IDLTerm<I, L, K, R> last()
	{
		return _baseTBox.last();
	}

	@Override
	public int size()
	{
		return _baseTBox.size();
	}

	@Override
	public boolean isEmpty()
	{
		return _baseTBox.isEmpty();
	}

	@Override
	public boolean contains(Object o)
	{
		return _baseTBox.contains(o);
	}

	@Override
	public Iterator<IDLTerm<I, L, K, R>> iterator()
	{
		return ImmutableIterator.decorate(iterator());
	}

	@Override
	public Object[] toArray()
	{
		return _baseTBox.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a)
	{
		return _baseTBox.toArray(a);
	}

	@Override
	public boolean add(
		IDLTerm<I, L, K, R> e)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableTBox");
	}

	@Override
	public boolean remove(Object o)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableTBox");
	}

	@Override
	public boolean containsAll(
		Collection<?> c)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableTBox");
	}

	@Override
	public boolean addAll(
		Collection<? extends IDLTerm<I, L, K, R>> c)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableTBox");
	}

	@Override
	public boolean retainAll(
		Collection<?> c)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableTBox");
	}

	@Override
	public boolean removeAll(
		Collection<?> c)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableTBox");
	}

	@Override
	public void clear()
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableTBox");
	}
}

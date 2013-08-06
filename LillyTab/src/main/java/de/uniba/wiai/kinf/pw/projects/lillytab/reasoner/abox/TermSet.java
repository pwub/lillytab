/**
 * (c) 2009-2013 Otto-Friedrich-University Bamberg
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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox;

import de.dhke.projects.cutil.collections.aspect.AspectSortedSet;
import de.dhke.projects.cutil.collections.aspect.CollectionItemEvent;
import de.dhke.projects.cutil.collections.aspect.CollectionItemReplacedEvent;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ITermSet;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.immutable.ImmutableTermSet;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLDummyTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataRange;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.commons.collections15.IteratorUtils;
import org.apache.commons.collections15.Predicate;

/**
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class TermSet<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends AspectSortedSet<IDLTerm<I, L, K, R>>
	implements ITermSet<I, L, K, R> {

	private final TermTypes _types;


	public TermSet(final TermTypes types)
	{
		super(new TreeSet<IDLTerm<I, L, K, R>>());
		_types = types;
	}


	public TermSet(final TermTypes types, final Object sender)
	{
		this(types, new TreeSet<IDLTerm<I, L, K, R>>(), sender);
	}


	public TermSet(final TermTypes types, final SortedSet<IDLTerm<I, L, K, R>> baseSet, final Object sender)
	{
		super(baseSet, sender);
		_types = types;
	}


	@Override
	protected void notifyBeforeElementAdded(
		CollectionItemEvent<IDLTerm<I, L, K, R>, Collection<IDLTerm<I, L, K, R>>> ev)
	{
		checkTermType(ev.getItem());
		super.notifyBeforeElementAdded(ev); //To change body of generated methods, choose Tools | Templates.
	}


	@Override
	protected void notifyBeforeElementReplaced(
		CollectionItemReplacedEvent<IDLTerm<I, L, K, R>, Collection<IDLTerm<I, L, K, R>>> ev)
	{
		checkTermType(ev.getNewItem());
		super.notifyBeforeElementReplaced(ev);
	}

	@Override
	public ITermSet<I, L, K, R> getImmutable()
	{
		return new ImmutableTermSet<>(this);
	}
		

	/// <editor-fold defaultstate="collapsed" desc="interface ITermSet">

	@Override
	public SortedSet<IDLTerm<I, L, K, R>> subSet(DLTermOrder termType)
	{
		final DLTermOrder[] values = DLTermOrder.values();
		final int typeIndex = Arrays.binarySearch(values, termType);
		assert (typeIndex > 0) && (typeIndex < values.length - 1);
		final DLTermOrder before = values[typeIndex - 1];
		final DLTermOrder after = values[typeIndex + 1];
		final DLDummyTerm<I, L, K, R> beforeTerm = new DLDummyTerm<>(before);
		final DLDummyTerm<I, L, K, R> afterTerm = new DLDummyTerm<>(after);

		return subSet(beforeTerm, afterTerm);
	}

	@Override
	public <T extends IDLTerm<I, L, K, R>> Iterator<T> iterator(final Class<? extends T> klass)
	{
		return new TermsOfTypeIterator<>(klass, this);
	}


	@Override
	public <T extends IDLTerm<I, L, K, R>> Iterator<T> iterator(DLTermOrder termType,
																Class<? extends T> klass)
	{
		return new TermsOfTypeIterator<>(klass, subSet(termType));
	}


	public TermTypes getAllowedTermTypes()
	{
		return _types;
	}


	protected void checkTermType(
		IDLTerm<I, L, K, R> term)
		throws EIllegalTermTypeException
	{
		switch (getAllowedTermTypes()) {
			case ANY:
				return;
			case DATATYPE_ONLY:
				if (!(term instanceof IDLDataRange))
					throw new EIllegalTermTypeException(term);
				break;
			case CLASS_ONLY:
				if (!(term instanceof IDLClassExpression))
					throw new EIllegalTermTypeException(term);
				break;
		}
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="enum TermTypes">
	public enum TermTypes {

		ANY,
		DATATYPE_ONLY,
		CLASS_ONLY
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="class TermsOfTypeIterator" >
	final class TermsOfTypeIterator<T>
		implements Iterator<T> {

		private final Class<? extends T> _klass;
		private final Iterator<IDLTerm<I, L, K, R>> _termIter;
		private final Predicate<IDLTerm<I, L, K, R>> _pred = new Predicate<IDLTerm<I, L, K, R>>() {
			@Override
			public boolean evaluate(IDLTerm<I, L, K, R> object)
			{
				return _klass.isInstance(object);
			}
		};


		TermsOfTypeIterator(final Class<? extends T> klass, final Iterable<IDLTerm<I, L, K, R>> baseIterable)
		{
			_klass = klass;
			final Iterator<IDLTerm<I, L, K, R>> iter = baseIterable.iterator();
			_termIter = IteratorUtils.filteredIterator(iter, _pred);
		}


		@Override
		public boolean hasNext()
		{
			return _termIter.hasNext();
		}


		@SuppressWarnings("unchecked")
		@Override
		public T next()
		{
			return (T) _termIter.next();
		}


		@Override
		public void remove()
		{
			_termIter.remove();
		}
	}
	/// </editor-fold>
}

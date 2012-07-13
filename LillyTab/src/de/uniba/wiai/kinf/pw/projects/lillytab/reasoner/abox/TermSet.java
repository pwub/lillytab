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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox;

import de.dhke.projects.cutil.collections.aspect.AspectSortedSet;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ITermSet;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLDummyDescription;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import java.util.Arrays;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.commons.collections15.IteratorUtils;
import org.apache.commons.collections15.Predicate;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class TermSet<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends AspectSortedSet<IDLTerm<Name, Klass, Role>>
	implements ITermSet<Name, Klass, Role>
{
	public TermSet()
	{
		super(new TreeSet<IDLTerm<Name, Klass, Role>>());
	}

	public TermSet(final SortedSet<IDLTerm<Name, Klass, Role>> baseSet)
	{
		super(baseSet);
	}

	public TermSet(final Object sender)
	{
		this(new TreeSet<IDLTerm<Name, Klass, Role>>(), sender);
	}

	public TermSet(final SortedSet<IDLTerm<Name, Klass, Role>> baseSet, final Object sender)
	{
		super(baseSet, sender);
	}

	public SortedSet<IDLTerm<Name, Klass, Role>> subSet(DLTermOrder termType)
	{
		final DLTermOrder[] values = DLTermOrder.values();
		final int typeIndex = Arrays.binarySearch(values, termType);
		assert (typeIndex > 0) && (typeIndex < values.length - 1);
		final DLTermOrder before = values[typeIndex - 1];
		final DLTermOrder after = values[typeIndex + 1];
		final DLDummyDescription<Name, Klass, Role> beforeTerm = new DLDummyDescription<Name, Klass, Role>(before);
		final DLDummyDescription<Name, Klass, Role> afterTerm = new DLDummyDescription<Name, Klass, Role>(after);

		return subSet(beforeTerm, afterTerm);
	}

	public ITermSet<Name, Klass, Role> getImmutable()
	{
		return ImmutableTermSet.decorate(this);
	}

	final class TermsOfTypeIterator<T>
		implements Iterator<T>
	{
		private final Class<? extends T> _klass;
		private final Iterator<IDLTerm<Name, Klass, Role>> _termIter;
		private final Predicate<IDLTerm<Name, Klass, Role>> _pred = new Predicate<IDLTerm<Name, Klass, Role>>() {
			public boolean evaluate(IDLTerm<Name, Klass, Role> object)
			{
				return _klass.isInstance(object);
			}
		};

		public TermsOfTypeIterator(final Class<? extends T> klass, final Iterable<IDLTerm<Name, Klass, Role>> baseIterable)
		{
			_klass = klass;
			final Iterator<IDLTerm<Name, Klass, Role>> iter = baseIterable.iterator();
			_termIter = IteratorUtils.filteredIterator(iter, _pred);
		}


		public boolean hasNext()
		{
			return _termIter.hasNext();
		}


		@SuppressWarnings("unchecked")
		public T next()
		{
			return (T)_termIter.next();
		}


		public void remove()
		{
			_termIter.remove();
		}
	}

	public <T extends IDLTerm<Name, Klass, Role>> Iterator<T> iterator(final Class<? extends T> klass)
	{
		return new TermsOfTypeIterator<T>(klass, this);
	}


	public <T extends IDLTerm<Name, Klass, Role>> Iterator<T> iterator(DLTermOrder termType,
																	   Class<? extends T> klass)
	{
		return new TermsOfTypeIterator<T>(klass, subSet(termType));
	}
}

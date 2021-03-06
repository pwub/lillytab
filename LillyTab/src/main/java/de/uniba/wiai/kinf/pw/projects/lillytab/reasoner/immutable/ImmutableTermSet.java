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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.immutable;

import de.dhke.projects.cutil.IDecorator;
import de.dhke.projects.cutil.collections.immutable.GenericImmutableSet;
import de.dhke.projects.cutil.collections.immutable.GenericImmutableSortedSet;
import de.dhke.projects.cutil.collections.immutable.ImmutableIterator;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ITermSet;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;


/**
 * /
 **
 * *
 * A proxy object to an {@link ITermSet} that forbids changes to the underlying set.
 * <p />
 * If an immutable is first created and the underlying map is modified, afterwards, behaviour of the immutable is
 * undefined.
 * <p/>
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public class ImmutableTermSet<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends GenericImmutableSet<IDLTerm<I, L, K, R>, ITermSet<I, L, K, R>>
	implements ITermSet<I, L, K, R>, IDecorator<ITermSet<I, L, K, R>> {

	public ImmutableTermSet(final ITermSet<I, L, K, R> baseSet)
	{
		super(baseSet, null);
	}

	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> ImmutableTermSet<I, L, K, R> decorate(
		final ITermSet<I, L, K, R> baseSet)
	{
		return new ImmutableTermSet<>(baseSet);
	}

	@Override
	public SortedSet<IDLTerm<I, L, K, R>> subSet(DLTermOrder termType)
	{
		return GenericImmutableSortedSet.decorate(getDecoratee().subSet(termType));
	}

	@Override
	public <T extends IDLTerm<I, L, K, R>> Iterator<T> iterator(Class<? extends T> klass)
	{
		return ImmutableIterator.decorate(getDecoratee().iterator(klass));
	}

	@Override
	public <T extends IDLTerm<I, L, K, R>> Iterator<T> iterator(DLTermOrder termType,
																Class<? extends T> klass)
	{
		return ImmutableIterator.decorate(getDecoratee().iterator(termType, klass));
	}

	@Override
	public Comparator<? super IDLTerm<I, L, K, R>> comparator()
	{
		return getDecoratee().comparator();
	}

	@Override
	public SortedSet<IDLTerm<I, L, K, R>> subSet(IDLTerm<I, L, K, R> fromElement,
												 IDLTerm<I, L, K, R> toElement)
	{
		return Collections.unmodifiableSortedSet(getDecoratee().subSet(fromElement, toElement));
	}

	@Override
	public SortedSet<IDLTerm<I, L, K, R>> headSet(IDLTerm<I, L, K, R> toElement)
	{
		return Collections.unmodifiableSortedSet(getDecoratee().headSet(toElement));
	}

	@Override
	public SortedSet<IDLTerm<I, L, K, R>> tailSet(IDLTerm<I, L, K, R> fromElement)
	{
		return Collections.unmodifiableSortedSet(getDecoratee().tailSet(fromElement));
	}

	@Override
	public IDLTerm<I, L, K, R> first()
	{
		return getDecoratee().first();
	}

	@Override
	public IDLTerm<I, L, K, R> last()
	{
		return getDecoratee().last();
	}

	@Override
	public ITermSet<I, L, K, R> getImmutable()
	{
		return this;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		} else if (obj instanceof ImmutableTermSet) {
			final ImmutableTermSet<?, ?, ?, ?> other = (ImmutableTermSet<?, ?, ?, ?>) obj;
			return getDecoratee().equals(other.getDecoratee());
		} else {
			return getDecoratee().equals(obj);
		}
	}

	@Override
	public int hashCode()
	{
		return getDecoratee().hashCode();
	}

}

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
package de.uniba.wiai.kinf.pw.projects.lillytab.abox;

import de.dhke.projects.cutil.collections.immutable.IImmutable;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import java.util.Iterator;
import java.util.SortedSet;


/**
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public interface ITermSet<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends SortedSet<IDLTerm<I, L, K, R>>, IImmutable<ITermSet<I, L, K, R>>
{
	/**
	 * Create an iterator over all elements of the current term set which are derivates of {@literal klass}.
	 *
	 * @param <T> The type of the elements to look for.
	 * @param klass The class of the elements to look for.
	 * @return An iterator of the filtered elements of type {@literal klass}.
	 */
	<T extends IDLTerm<I, L, K, R>> Iterator<T> iterator(final Class<? extends T> klass);

	/**
	 * Create an iterator over all elements of the current term set which are derivates of {@literal klass} and feature
	 * the term type {@literal termType}.
	 *
	 * @param <T> The type of the elements to look for.
	 * @param termType The {
	 * @see DLTermType} of the terms to look for.
	 * @param klass The class of the elements to look for.
	 * @return An iterator of the filtered elements of type {@literal klass}.
	 */
	<T extends IDLTerm<I, L, K, R>> Iterator<T> iterator(final DLTermOrder termType,
														 final Class<? extends T> klass);

	/**
	 *  Return the subset of the current {@link ITermSet} that contains only the terms of the specified type. <p />
	 * Modifications to the subset modify the underlying termset. 
	 *
	 * @param termType The types of the terms to return.
	 * @return The subset of the current termset containing only terms of the specified term type.
	 * @see DLTermOrder
	 */
	SortedSet<IDLTerm<I, L, K, R>> subSet(final DLTermOrder termType);

	/**
	 * 
	 * @return an immutable version of this {@link ITermSet}
	 */
	@Override
	ITermSet<I, L, K, R> getImmutable();
}

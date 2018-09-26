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

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLDummyTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLIndividualReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.visitor.IDLTermVisitor;


/**
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public class DLIndividualReference<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	implements IDLIndividualReference<I, L, K, R>
{
	private final I _individual;
	
	protected DLIndividualReference(final I individual)
	{
		_individual = individual;
	}
	
	@Override
	public I getIndividual()
	{
		return _individual;
	}
	
	@Override
	public DLTermOrder getDLTermOrder()
	{
		return DLTermOrder.DL_INDIVIDUAL_REFERENCE;
	}
	
	@Override
	public DLIndividualReference<I, L, K, R> clone()
	{
		return this;
	}
	
	@Override
	public int compareTo(final IDLTerm<I, L, K, R> o)
	{
		int compare = getDLTermOrder().compareTo(o);
		if (compare == 0) {
			assert o instanceof IDLIndividualReference;
			IDLIndividualReference<I, L, K, R> otherNRef = (IDLIndividualReference<I, L, K, R>) o;
			compare = getIndividual().compareTo(otherNRef.getIndividual());
		}
		return compare;
	}
	
	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(_individual.toString());
		sb.append("}");
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		} else {
			return ((obj instanceof IDLIndividualReference)
				&& getIndividual().equals(((IDLIndividualReference) obj).getIndividual()));
		}
	}
	
	@Override
	public int hashCode()
	{
		return getIndividual().hashCode();
	}
	
	@Override
	public IDLTerm<I, L, K, R> getBefore()
	{
		return new DLDummyTerm<>(DLTermOrder.DL_BEFORE_INDIVIDUAL_REFERENCE);
	}
	
	@Override
	public IDLTerm<I, L, K, R> getAfter()
	{
		return new DLDummyTerm<>(DLTermOrder.DL_AFTER_INDIVIDUAL_REFERENCE);
	}
	
	@Override
	public void accept(
		IDLTermVisitor<I, L, K, R> visitor)
	{
		visitor.visit(this);
	}
}

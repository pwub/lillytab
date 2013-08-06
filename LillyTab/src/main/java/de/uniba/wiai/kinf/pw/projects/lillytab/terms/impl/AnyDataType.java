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
 *
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLDummyTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDatatype;
import de.uniba.wiai.kinf.pw.projects.lillytab.util.IToStringFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class AnyDataType<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	implements IDLDatatype<I, L, K, R> {

	public AnyDataType()
	{
	}


	@Override
	public boolean isValidValue(L literal)
	{
		return true;
	}


	@SafeVarargs
	@Override
	public final boolean isCompatibleValue(L lit1, L lit2, L... otherLits)
	{
		return true;
	}


	@Override
	public Set<Set<L>> getIncompatibleValues(
		Collection<? extends L> lits)
	{
		return Collections.emptySet();
	}


	@Override
	public IDLTerm<I, L, K, R> getBefore()
	{
		return new DLDummyTerm<>(DLTermOrder.DL_BEFORE_DATATYPE_EXPRESSION);
	}


	@Override
	public IDLTerm<I, L, K, R> getAfter()
	{
		return new DLDummyTerm<>(DLTermOrder.DL_AFTER_DATATYPE_EXPRESSION);
	}


	@Override
	public DLTermOrder getDLTermOrder()
	{
		return DLTermOrder.DL_DATATYPE_EXPRESSION;
	}


	@Override
	public String toString(IToStringFormatter entityFormatter)
	{
		return toString();
	}


	@Override
	public String toString()
	{
		return "xsd:anyType";
	}


	@Override
	public AnyDataType<I, L, K, R> clone()
	{
		return this;
	}


	@Override
	public int compareTo(
		IDLTerm<I, L, K, R> o)
	{
		int compare = getDLTermOrder().compareTo(o);
		if (compare == 0) {
			assert o instanceof IDLDatatype;
			/* any sorts before all other types */
			if (o instanceof AnyDataType)
				return 0;
			else
				return -1;
		} else
			return compare;
	}
}

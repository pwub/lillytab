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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.impl;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.ITerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl.AbstractFixedTermList;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLArgument;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLIArgument;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLRoleAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.TermUtil;
import de.uniba.wiai.kinf.pw.projects.lillytab.util.IToStringFormatter;

/**
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public abstract class SWRLRoleAtom<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends AbstractFixedTermList<ISWRLArgument<I, L, K, R>>
	implements ISWRLRoleAtom<I, L, K, R> {

	private final R _role;


	protected SWRLRoleAtom(R role, ISWRLIArgument<I, L, K, R> first,
						   ISWRLArgument<I, L, K, R> second)
	{
		super(2);
		_role = role;
		getModifiableTermList().set(0, first);
		getModifiableTermList().set(1, second);
	}


	@Override
	public R getRole()
	{
		return _role;
	}


	@Override
	public ISWRLIArgument<I, L, K, R> getFirstIndividual()
	{
		return (ISWRLIArgument<I, L, K, R>) get(0);
	}


	@Override
	public ISWRLArgument<I, L, K, R> getSecondIndividual()
	{
		return get(1);
	}


	@Override
	public ITerm clone()
	{
		return this;
	}


	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		else if (obj instanceof ISWRLRoleAtom) {
			final ISWRLRoleAtom<?, ?, ?, ?> other = (ISWRLRoleAtom<?, ?, ?, ?>) obj;
			return getSWRLTermOrder().equals(other.getSWRLTermOrder()) && getRole().equals(other.getRole()) && super.equals(
				obj);
		} else
			return false;
	}


	@Override
	public int compareTo(ISWRLTerm<I, L, K, R> o)
	{
		int compare = getSWRLTermOrder().compareTo(o);
		if (compare == 0) {
			assert o instanceof ISWRLRoleAtom;
			final ISWRLRoleAtom<I, L, K, R> other = (ISWRLRoleAtom<I, L, K, R>) o;
			compare = TermUtil.compareTermList(this, other);
		}
		return compare;
	}


	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("(");

		sb.append(getRole());

		for (ISWRLArgument<I, L, K, R> individual : this) {
			sb.append(" ");
			sb.append(individual.toString());
		}
		sb.append(")");
		return sb.toString();
	}


	@Override
	public String toString(IToStringFormatter formatter)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("(");

		/* workaround hack around OWLapis 3.0s new behaviour to output full IRIs for OWLNamedObjects */
		/* Cast: netbeans/compiler workaround */
		sb.append(formatter.toString(getRole()));
		for (ISWRLArgument<I, L, K, R> individual : this) {
			sb.append(" ");
			sb.append(individual);
		}
		sb.append(")");
		return sb.toString();
	}
}

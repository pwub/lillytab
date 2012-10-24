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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.impl;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.ITerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.util.IToStringFormatter;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.TermUtil;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl.AbstractFixedTermList;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLIndividual;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLRoleAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.SWRLTermOrder;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class SWRLRoleAtom<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends AbstractFixedTermList<ISWRLIndividual<Name, Klass, Role>>
	implements ISWRLRoleAtom<Name, Klass, Role> {

	private final Role _role;


	protected SWRLRoleAtom(Role role, ISWRLIndividual<Name, Klass, Role> first,
						   ISWRLIndividual<Name, Klass, Role> second)
	{
		super(2);
		_role = role;
		getModifiableTermList().set(0, first);
		getModifiableTermList().set(1, second);
	}


	public Role getRole()
	{
		return _role;
	}


	public ISWRLIndividual<Name, Klass, Role> getFirstIndividual()
	{
		return get(0);
	}


	public ISWRLIndividual<Name, Klass, Role> getSecondIndividual()
	{
		return get(1);
	}


	public SWRLTermOrder getSWRLTermOrder()
	{
		return SWRLTermOrder.SWRL_ROLE_ATOM;
	}


	@Override
	public ITerm clone()
	{
		return this;
	}


	public int compareTo(ISWRLTerm<Name, Klass, Role> o)
	{
		int compare = getSWRLTermOrder().compareTo(o);
		if (compare == 0) {
			assert o instanceof ISWRLRoleAtom;
			final ISWRLRoleAtom<Name, Klass, Role> other = (ISWRLRoleAtom<Name, Klass, Role>) o;
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

		for (ISWRLIndividual<Name, Klass, Role> individual : this) {
			sb.append(" ");
			sb.append(individual.toString());
		}
		sb.append(")");
		return sb.toString();
	}


	public String toString(IToStringFormatter formatter)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("(");

		/* workaround hack around OWLapis 3.0s new behaviour to output full IRIs for OWLNamedObjects */
		/* Cast: netbeans/compiler workaround */
		sb.append(formatter.toString(getRole()));

		for (ISWRLIndividual<Name, Klass, Role> individual : this) {
			sb.append(" ");
			sb.append(individual);
		}
		sb.append(")");
		return sb.toString();
	}
}

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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRoleOperator;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IUnaryOperator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections15.list.FixedSizeList;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 * @param <I> The type for nominals and values
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 */
public abstract class AbstractDLRoleRestriction
<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>, ResTerm extends IDLTerm<I, L, K, R>>
	extends AbstractUnaryOperator<ResTerm>
	implements IDLRoleOperator<I, L, K, R> {

	private final List<R> _roles;


	protected AbstractDLRoleRestriction(final DLTermOrder termOrder, final String operatorName, final R role,
										final ResTerm d)
	{
		super(termOrder, operatorName, d);
		List<R> roles = new ArrayList<>(1);
		roles.add(role);
		_roles = FixedSizeList.decorate(roles);
	}


	protected AbstractDLRoleRestriction(final DLTermOrder termOrder, final String operatorName, final R role)
	{
		super(termOrder, operatorName);
		List<R> roles = new ArrayList<>(1);
		roles.add(role);
		_roles = FixedSizeList.decorate(roles);
	}


	public R getRole()
	{
		return _roles.get(0);
	}


	@Override
	public List<R> getRoles()
	{
		return Collections.unmodifiableList(_roles);
	}


	public R getElement()
	{
		return _roles.get(0);
	}


	@Override
	public int hashCode()
	{
		int hCode = super.hashCode();
		for (R role : getRoles()) {
			hCode += role.hashCode();
		}
		return hCode;
	}


	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj) {
			return true;
		}
		if ((obj instanceof IDLRoleOperator)
			&& (obj instanceof IUnaryOperator)) {
			@SuppressWarnings("unchecked")
			IDLRoleOperator<I, L, K, R> otherRoleOp = (IDLRoleOperator<I, L, K, R>) obj;

			return ((getRoles().size() == otherRoleOp.getRoles().size())
				&& getRoles().containsAll(otherRoleOp.getRoles())
				&& super.equals(obj));
		} else {
			return false;
		}
	}


	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(getOperatorName());
		sb.append(" ");
		sb.append(getRole());

		for (ResTerm d : this) {
			sb.append(" ");
			sb.append(d);
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public abstract AbstractDLRoleRestriction<I, L, K, R, ResTerm> clone();
}

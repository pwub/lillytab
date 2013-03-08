/**
 * (c) 2009-2012 Otto-Friedrich-University Bamberg
 *
 * $Id$
 *
 * Use, modification and restribution of this file are covered by the terms of the Artistic License 2.0.
 *
 * You should have received a copy of the license terms in a file named "LICENSE" together with this software package.
 *
 * Disclaimer of Warranty: THE PACKAGE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS "AS IS' AND WITHOUT ANY
 * EXPRESS OR IMPLIED WARRANTIES. THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT ARE DISCLAIMED TO THE EXTENT PERMITTED BY YOUR LOCAL LAW. UNLESS REQUIRED BY LAW, NO COPYRIGHT
 * HOLDER OR CONTRIBUTOR WILL BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING IN ANY
 * WAY OUT OF THE USE OF THE PACKAGE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRoleOperator;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IUnaryOperator;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections15.list.FixedSizeList;
import org.semanticweb.owlapi.model.OWLProperty;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 * @param <Name> The type for nominals and values
 * @param <Klass> The type for DL classes
 * @param <Role> The type for properties (roles)
 */
public abstract class AbstractDLRoleRestriction<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends AbstractUnaryOperator<IDLRestriction<Name, Klass, Role>>
	implements IDLRoleOperator<Name, Klass, Role> {

	private final List<Role> _roles;


	protected AbstractDLRoleRestriction(final DLTermOrder termOrder, final String operatorName, final Role role,
										final IDLRestriction<Name, Klass, Role> d)
	{
		super(termOrder, operatorName, d);
		List<Role> roles = new ArrayList<>(1);
		roles.add(role);
		_roles = FixedSizeList.decorate(roles);
	}


	protected AbstractDLRoleRestriction(final DLTermOrder termOrder, final String operatorName, final Role role)
	{
		super(termOrder, operatorName);
		List<Role> roles = new ArrayList<>(1);
		roles.add(role);
		_roles = FixedSizeList.decorate(roles);
	}


	public Role getRole()
	{
		return _roles.get(0);
	}


	public List<Role> getRoles()
	{
		return _roles;
	}


	public Role getElement()
	{
		return _roles.get(0);
	}


	@Override
	public int hashCode()
	{
		int hCode = super.hashCode();
		for (Role role : getRoles()) {
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
			IDLRoleOperator<Name, Klass, Role> otherRoleOp = (IDLRoleOperator<Name, Klass, Role>) obj;

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

		for (IDLRestriction<Name, Klass, Role> d : this) {
			sb.append(" ");
			sb.append(d);
		}
		sb.append(")");
		return sb.toString();
	}


	@Override
	public abstract AbstractDLRoleRestriction<Name, Klass, Role> clone();
}

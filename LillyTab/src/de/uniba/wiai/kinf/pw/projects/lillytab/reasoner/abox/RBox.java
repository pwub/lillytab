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

import de.dhke.projects.cutil.collections.MultiTreeSetHashMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ITBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.RoleProperty;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;


/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 * @param <Name>
 * @param <Klass>
 * @param <Role> Role name type.
 */
public class RBox<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends MultiHashMap<Role, RoleProperty>
	implements IRBox<Name, Klass, Role>
{
	static final long serialVersionUID = 2484966322395859950L;

	private final ITBox<Name, Klass, Role> _tbox;
	private final MultiMap<Role, IDLRestriction<Name, Klass, Role>> _roleDomains = new MultiTreeSetHashMap<Role, IDLRestriction<Name, Klass, Role>>();
	private final MultiMap<Role, IDLRestriction<Name, Klass, Role>> _roleRanges = new MultiTreeSetHashMap<Role, IDLRestriction<Name, Klass, Role>>();

	public RBox(ITBox<Name, Klass, Role> tbox)
	{
		_tbox = tbox;
	}

	public ITBox<Name, Klass, Role> getTBox()
	{
		return _tbox;
	}

	/// <editor-fold defaultstate="collapsed" desc="Role Property Management">
	public Set<RoleProperty> getRoleProperties(Role role)
	{
		return (Set<RoleProperty>)get(role);
	}

	public boolean setRoleProperty(Role role, RoleProperty property)
	{
		if (! containsValue(role, property)) {
			put(role, property);
			return true;
		} else {
			return false;
		}

	}

	public boolean clearRoleProperty(Role role, RoleProperty property)
	{
		if (containsValue(role, property)) {
			remove(role, property);
			return true;
		} else {
			return false;
		}
	}

	public boolean hasRoleProperty(Role role, RoleProperty property)
	{
		return containsValue(role, property);
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="Domain and Range management">
	public MultiMap<Role, IDLRestriction<Name, Klass, Role>> getRoleDomains()
	{
		return _roleDomains;
	}

	public MultiMap<Role, IDLRestriction<Name, Klass, Role>> getRoleRanges()
	{
		return _roleRanges;
	}
	/// </editor-fold>


	@Override
	protected Collection<RoleProperty> createCollection(Collection<? extends RoleProperty> coll)
	{
		/* override MultiHashMap core collection to EnumSet */
		Set<RoleProperty> newCollection = EnumSet.noneOf(RoleProperty.class);
		if (coll != null) {
			for (RoleProperty prop : coll)
				newCollection.add(prop);
		}
		return newCollection;
	}

	@Override
	public String toString()
	{
		return toString("");
	}

	public String toString(String prefix)
	{
		StringBuilder sb = new StringBuilder();
		Set<Role> roles = new TreeSet<Role>();
		roles.addAll(keySet());;
		roles.addAll(getRoleDomains().keySet());
		roles.addAll(getRoleRanges().keySet());

		for (Role role: roles) {
			sb.append(role);
			sb.append(": Properties: ");
			sb.append(getRoleProperties(role));
			sb.append(", Domains: ");
			sb.append(getRoleDomains().get(role));
			sb.append(", Ranges: ");
			sb.append(getRoleRanges().get(role));
			sb.append("\n");
			sb.append(prefix);
		}
		return sb.toString();
	}

	public Set<Role> getInverseRoles(Role role)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Set<Role> getEquivalentRoles(Role role)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Set<Role> getSuperRoles(Role role)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Set<Role> getSubRoles(Role role)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
}

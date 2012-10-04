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

import de.dhke.projects.cutil.collections.iterator.MultiMapItemIterator;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentRBoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.ITBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleType;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IAssertedRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleProperty;
import de.dhke.projects.cutil.collections.map.MultiEnumSetHashMap;
import de.dhke.projects.cutil.collections.map.MultiSortedListSetHashMap;
import de.dhke.projects.cutil.collections.map.MultiTreeSetHashMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.*;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import java.lang.ref.WeakReference;
import java.util.*;
import org.apache.commons.collections15.MultiMap;


/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 * @param <Name>
 * @param <Klass>
 * @param <Role> Role name type.
 */
public class AssertedRBox<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	implements IAssertedRBox<Name, Klass, Role>
{
	static final long serialVersionUID = 2484966322395859950L;
	// <editor-fold defaultstate="collapsed" desc="private variables">
	private final WeakReference<ITBox<Name, Klass, Role>> _tboxRef;
	private final RBox<Name, Klass, Role> _rbox;
	private final MultiMap<Role, IDLRestriction<Name, Klass, Role>> _roleDomains = new MultiTreeSetHashMap<Role, IDLRestriction<Name, Klass, Role>>();
	private final MultiMap<Role, IDLRestriction<Name, Klass, Role>> _roleRanges = new MultiTreeSetHashMap<Role, IDLRestriction<Name, Klass, Role>>();
	private final MultiMap<Role, Role> _inverseRoles;
	private final MultiMap<Role, Role> _equivalentRoles;
	private final MultiMap<Role, Role> _subRoles;
	private final MultiMap<Role, Role> _superRoles;
	private final MultiMap<RoleProperty, Role> _propertyRoleMap;
	private final MultiMap<Role, RoleProperty> _rolePropertyMap;
	private final Map<Role, RoleType> _roleTypeMap;
	private final MultiMap<RoleType, Role> _typeRoleMap;
	// </editor-fold>

	public AssertedRBox(ITBox<Name, Klass, Role> tbox)
	{
		super();
		_tboxRef = new WeakReference<ITBox<Name, Klass, Role>>(tbox);

		_roleTypeMap = new HashMap<Role, RoleType>();
		_typeRoleMap = new MultiSortedListSetHashMap<RoleType, Role>();

		_rolePropertyMap = new MultiEnumSetHashMap<Role, RoleProperty>(RoleProperty.class);
		_propertyRoleMap = new MultiTreeSetHashMap<RoleProperty, Role>();

		_inverseRoles = new MultiSortedListSetHashMap<Role, Role>();
		_equivalentRoles = new MultiSortedListSetHashMap<Role, Role>();

		_subRoles = new MultiSortedListSetHashMap<Role, Role>();
		_superRoles = new MultiSortedListSetHashMap<Role, Role>();

		_rbox = new RBox<Name, Klass, Role>(this);
	}

	protected MultiMap<Role, Role> getEquivalentRoles()
	{
		return _equivalentRoles;
	}

	protected MultiMap<Role, Role> getSubRoles()
	{
		return _subRoles;
	}

	protected MultiMap<Role, Role> getSuperRoles()
	{
		return _superRoles;
	}

	protected MultiMap<Role, Role> getInverseRoles()
	{
		return _inverseRoles;
	}

	protected MultiMap<Role, RoleProperty> getRoleProperties()
	{
		return _rolePropertyMap;
	}

	protected MultiMap<RoleProperty, Role> getPropertyRoles()
	{
		return _propertyRoleMap;
	}

	public ITBox<Name, Klass, Role> getTBox()
	{
		return _tboxRef.get();
	}

	public boolean addRole(Role role, RoleType roleType)
		throws EInconsistentRBoxException
	{
		if (roleType == null)
			throw new IllegalArgumentException("roleType cannot be null");
		else {
			final RoleType existingRoleType = _roleTypeMap.get(role);
			if (existingRoleType == null) {
				_roleTypeMap.put(role, roleType);
				_typeRoleMap.put(roleType, role);

				_equivalentRoles.put(role, role);
				_subRoles.put(role, role);
				_superRoles.put(role, role);
				try {
					recalculate();
				} catch (EInconsistentRBoxException ex) {
					removeRole(role);
					throw ex;
				}
				return true;
			} else if (existingRoleType == roleType)
				return false;
			else
				throw new EInconsistentRBoxException(this, String.format(
					"Cannot add role `%s' with different role type `%s' (has type `%s')", role, roleType,
					existingRoleType));
		}
	}

	public boolean removeRole(Role role)
	{
		final RoleType roleType = _roleTypeMap.remove(role);
		if (roleType != null) {
			_typeRoleMap.remove(roleType, role);

			_roleDomains.remove(role);
			_roleRanges.remove(role);

			if ((_equivalentRoles.get(role) != null) && (!_equivalentRoles.isEmpty())) {
				final List<Role> eqRoles = new ArrayList<Role>(_equivalentRoles.get(role));
				for (Role eqRole : eqRoles)
					_equivalentRoles.remove(eqRole, role);
			}
			_equivalentRoles.remove(role);

			if ((_inverseRoles.get(role) != null) && (!_inverseRoles.get(role).isEmpty())) {
				final List<Role> invRoles = new ArrayList<Role>(_inverseRoles.get(role));
				for (Role invRole : invRoles)
					_inverseRoles.remove(invRole, role);
			}
			_inverseRoles.remove(role);

			final Iterator<Map.Entry<Role, Role>> subIter = MultiMapItemIterator.decorate(_subRoles);
			while (subIter.hasNext()) {
				final Map.Entry<Role, Role> subEntry = subIter.next();
				if (role.equals(subEntry.getKey()) || role.equals(subEntry.getValue()))
					subIter.remove();
			}

			final Iterator<Map.Entry<Role, Role>> superIter = MultiMapItemIterator.decorate(_superRoles);
			while (subIter.hasNext()) {
				final Map.Entry<Role, Role> supEntry = superIter.next();
				if (role.equals(supEntry.getKey()) || role.equals(supEntry.getValue()))
					subIter.remove();
			}


			return true;
		} else
			return false;
	}

	public Collection<Role> getRoles()
	{
		return Collections.unmodifiableCollection(_roleTypeMap.keySet());
	}

	public Collection<Role> getRoles(final RoleProperty property)
	{
		return Collections.unmodifiableCollection(_propertyRoleMap.get(property));
	}

	// <editor-fold defaultstate="collapsed" desc="Role Type Management">
	public Collection<Role> getRoles(RoleType type)
	{
		return Collections.unmodifiableCollection(_typeRoleMap.get(type));
	}

	public void setRoleType(Role role, RoleType roleType)
		throws EInconsistentRBoxException
	{
		final RoleType preRoleType = _roleTypeMap.get(role);
		final IRBox<Name, Klass, Role> rbox = getRBox();

		if ((roleType == RoleType.DATA_PROPERTY) && (rbox.getInverseRoles(role) != null) && (!rbox.getInverseRoles(role).
			isEmpty())) {
			throw new EInconsistentRBoxException(this, String.format(
				"Error switching role `%s' to datatype role, role has inverses", role));
		}

		if (preRoleType != roleType) {
			if ((rbox.getEquivalentRoles(role) != null) && (!rbox.getEquivalentRoles(role).isEmpty()))
				throw new EInconsistentRBoxException(this, String.format(
					"Cannot change type of role `%s' to `%s', has equivalent roles", role, roleType));
			if ((rbox.getSubRoles(role) != null) && (!rbox.getSubRoles(role).isEmpty()))
				throw new EInconsistentRBoxException(this, String.format(
					"Cannot change type of role `%s' to `%s', has subroles", role, roleType));
			if ((rbox.getSuperRoles(role) != null) && (!rbox.getSuperRoles(role).isEmpty()))
				throw new EInconsistentRBoxException(this, String.format(
					"Cannot change type of role `%s' to `%s', has superroles", role, roleType));

			_typeRoleMap.remove(role, preRoleType);
			_typeRoleMap.put(roleType, role);
			_roleTypeMap.put(role, roleType);
			try {
				recalculate();
			} catch (EInconsistentRBoxException ex) {
				_typeRoleMap.remove(roleType, role);
				_typeRoleMap.put(preRoleType, role);
				_roleTypeMap.put(role, preRoleType);
				recalculate();
				throw ex;
			}

		}
	}

	public boolean hasRoleType(Role role, RoleType roleType)
	{
		assert roleType != null;
		return roleType == _roleTypeMap.get(role);
	}

	public RoleType getRoleType(Role role)
	{
		final RoleType roleType = _roleTypeMap.get(role);
		if (roleType == null)
			throw new IllegalArgumentException(String.format("Role type not known for role `%s'", role));
		else
			return roleType;
	}

	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Role Property Management">
	public boolean setRoleProperty(Role role, RoleProperty property) throws EInconsistentRBoxException
	{
		if (!_roleTypeMap.containsKey(role))
			throw new IllegalArgumentException(String.format("Must set role type for role `%s', first", role));
		else if (property == null)
			throw new IllegalArgumentException("Role property cannot be null");
		else if (_rolePropertyMap.containsValue(role, property))
			/*
			 * role type already set
			 */
			return false;
		else {
			if (property == RoleProperty.TOP) {
				if (_propertyRoleMap.containsKey(RoleProperty.TOP)) {
					for (Role topRole : _propertyRoleMap.get(RoleProperty.TOP)) {
						if ((!topRole.equals(role)) && (getRoleType(topRole) == getRoleType(role)))
							throw new EInconsistentRBoxException(this, String.format(
								"Cannot add another top role of this role type (existing top role: `%s')", topRole));
					}
				}
			} else if ((property == RoleProperty.TRANSITIVE) && (_roleTypeMap.get(role) != RoleType.OBJECT_PROPERTY)) {
				throw new EInconsistentRBoxException(this, String.format(
					"role `%s': only object properties can be transitive", role));
			} else if ((property == RoleProperty.SYMMETRIC) && (_roleTypeMap.get(role) != RoleType.OBJECT_PROPERTY)) {
				throw new EInconsistentRBoxException(this, String.format(
					"role `%s': only object properties can be symmetric", role));
			}
			/* XXX - add reflexiveness */
			/*
			 * update maps
			 */
			_propertyRoleMap.put(property, role);
			_rolePropertyMap.put(role, property);
			try {
				recalculate();
			} catch (EInconsistentRBoxException ex) {
				_propertyRoleMap.remove(property, role);
				_rolePropertyMap.remove(role, property);
				throw ex;
			}
			return true;
		}
	}

	public boolean clearRoleProperty(Role role, RoleProperty property)
	{
		if (_rolePropertyMap.containsValue(role, property)) {
			_rolePropertyMap.remove(role, property);
			_propertyRoleMap.remove(property, role);
			return true;
		} else
			return false;
	}

	public boolean hasRoleProperty(Role role, RoleProperty property)
	{
		return _rolePropertyMap.containsValue(role, property);
	}

	public Collection<RoleProperty> getRoleProperties(Role role)
	{
		final Collection<RoleProperty> properties = _rolePropertyMap.get(role);
		if (properties != null)
			return Collections.unmodifiableCollection(properties);
		else
			return null;
	}
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="Domain and Range management">
	public MultiMap<Role, IDLRestriction<Name, Klass, Role>> getRoleDomains()
	{
		return _roleDomains;
	}

	public MultiMap<Role, IDLRestriction<Name, Klass, Role>> getRoleRanges()
	{
		return _roleRanges;
	}
	/// </editor-fold>

	public boolean removeEquivalentRole(Role first, Role second)
	{
		if (_equivalentRoles.containsValue(first, second)) {
			_equivalentRoles.remove(first, second);
			_equivalentRoles.remove(second, first);
			try {
				recalculate();
			} catch (EInconsistentRBoxException ex) {
				/*
				 * this should never happen ...
				 */
				_equivalentRoles.put(first, second);
				_equivalentRoles.put(second, first);
				throw new RuntimeException(ex);
			}
			return true;
		} else
			return false;
	}

	public boolean addEquivalentRole(Role first, Role second)
		throws EInconsistentRBoxException
	{
		final IRBox<Name, Klass, Role> rbox = getRBox();
		if (rbox.isInverseRole(first, second))
			throw new EInconsistentRBoxException(this, String.format("`%s' is inverse of `%s', cannot be equivalent",
																	 first,
																	 second));
		else if (getRoleType(first) != getRoleType(second))
			throw new EInconsistentRBoxException(this, String.format(
				"`%s' and `%s' have different types and cannot be equal", first, second));
		else {
			if (!_equivalentRoles.containsValue(first, second)) {
				_equivalentRoles.put(first, second);
				_equivalentRoles.put(second, first);
				try {
					recalculate();
				} catch (EInconsistentRBoxException ex) {
					_equivalentRoles.remove(first, second);
					_equivalentRoles.remove(second, first);
					recalculate();
				}
				return true;
			} else
				return false;
		}
	}

	public Set<Role> getEquivalentRoles(Role role)
	{
		Set<Role> eqRoles = (Set<Role>) _equivalentRoles.get(role);
		if (eqRoles == null)
			throw new NoSuchElementException("Unknown role:" + role);

		assert eqRoles != null;
		return Collections.unmodifiableSet(eqRoles);
	}

	public boolean isEquivalentRole(Role first, Role second)
	{
		return _equivalentRoles.containsValue(first, second);
	}

	public boolean removeInverseRole(Role first, Role second)
	{
		if (_inverseRoles.containsValue(first, second)) {
			_inverseRoles.remove(first, second);
			_inverseRoles.remove(second, first);
			try {
				recalculate();
			} catch (EInconsistentRBoxException ex) {
				throw new RuntimeException(ex);
			}
			return true;
		} else
			return false;
	}

	public boolean addInverseRole(final Role first, final Role second)
		throws EInconsistentRBoxException
	{
		final IRBox<Name, Klass, Role> rbox = getRBox();
		if (first.equals(second))
			throw new EInconsistentRBoxException(this, String.format("Role `%s' cannot be its own inverse", first));
		else if (rbox.hasRoleType(first, RoleType.DATA_PROPERTY))
			throw new EInconsistentRBoxException(this, String.format("Datatype role `%s' cannot have inverses", first));
		else if (rbox.hasRoleType(second, RoleType.DATA_PROPERTY))
			throw new EInconsistentRBoxException(this, String.format("Datatype role `%s' cannot have inverses", second));
		else if (rbox.isSubRole(first, second))
			throw new EInconsistentRBoxException(this, String.format("Inverse role `%s' is a subrole of `%s'", first,
																	 second));
		else if (rbox.isSuperRole(first, second))
			throw new EInconsistentRBoxException(this, String.format("Inverse role `%s' is a superrole of `%s'", first,
																	 second));
		else if (rbox.isEquivalentRole(first, second))
			throw new EInconsistentRBoxException(this, String.format(
				"`%s' and `%s' are equivalent and cannot be inverses", first, second));
		else if (_inverseRoles.containsValue(first, second))
			return false;
		else {
			_inverseRoles.put(second, first);
			_inverseRoles.put(first, second);
			try {
				recalculate();
			} catch (EInconsistentRBoxException ex) {
				/*
				 * if this happens, the checks above are incomplete
				 */
				_inverseRoles.remove(first, second);
				_inverseRoles.remove(second, first);
				throw ex;
			}
			return true;
		}
	}

	public Set<Role> getInverseRoles(Role role)
	{
		final Set<Role> invRoles = (Set<Role>) _inverseRoles.get(role);
		if (invRoles == null)
			throw new NoSuchElementException("Unknown role: " + role);
		else
			return Collections.unmodifiableSet(invRoles);
	}

	public boolean isInverseRole(Role first, Role second)
	{
		return _inverseRoles.containsValue(first, second);
	}

	public boolean hasInverseRoles()
	{
		return !_inverseRoles.isEmpty();
	}		

	public Set<Role> getSuperRoles(Role role)
	{
		final Set<Role> superRoles = (Set<Role>) _superRoles.get(role);
		if (superRoles == null)
			throw new NoSuchElementException("Unknown role:" + role);

		assert superRoles != null;
		return Collections.unmodifiableSet(superRoles);
	}

	public Set<Role> getSubRoles(Role role)
	{
		final Set<Role> subRoles = (Set<Role>) _subRoles.get(role);
		if (subRoles == null)
			throw new NoSuchElementException("Unknown role:" + role);
		assert subRoles != null;
		return Collections.unmodifiableSet(subRoles);
	}

	public boolean removeSubRole(Role sup, Role sub)
	{
		if (_subRoles.containsValue(sup, sub)) {
			_subRoles.remove(sup, sub);
			_superRoles.remove(sub, sup);
			try {
				recalculate();
			} catch (EInconsistentRBoxException ex) {
				/*
				 * this should not happen
				 */
				throw new RuntimeException(ex);
			}
			return true;
		} else
			return false;
	}

	public boolean addSubRole(Role sup, Role sub)
		throws EInconsistentRBoxException
	{
		if (isInverseRole(sup, sub))
			throw new EInconsistentRBoxException(this, String.format(
				"Role `%s' is inverse of `%s', cannot be subroles", sup, sup));
		else if (getRoleType(sup) != getRoleType(sub))
			throw new EInconsistentRBoxException(this, String.format(
				"Roles `%s' and `%s' have different types, cannot be subroles", sup, sub));
		else if (!_subRoles.containsValue(sup, sub)) {
			_subRoles.put(sup, sub);
			_superRoles.put(sub, sup);
			recalculate();
			return true;
		} else
			return false;
	}

	public boolean isSubRole(Role sub, Role sup)
	{
		return _subRoles.containsValue(sup, sub);
	}

	public boolean isSuperRole(Role sup, Role sub)
	{
		return _superRoles.containsValue(sub, sup);
	}

	private void recalculate()
		throws EInconsistentRBoxException
	{
		_rbox.recalculate();
	}

	public IAssertedRBox<Name, Klass, Role> getAssertedRBox()
	{
		return this;
	}

	public RBox<Name, Klass, Role> getRBox()
	{
		return _rbox;
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
		roles.addAll(_rolePropertyMap.keySet());;
		roles.addAll(getRoleDomains().keySet());
		roles.addAll(getRoleRanges().keySet());

		for (Role role : roles) {
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
}

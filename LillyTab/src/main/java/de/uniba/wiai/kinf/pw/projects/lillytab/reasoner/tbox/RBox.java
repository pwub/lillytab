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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.tbox;

import de.dhke.projects.cutil.collections.iterator.MultiMapEntryIterable;
import de.dhke.projects.cutil.collections.map.MultiEnumSetHashMap;
import de.dhke.projects.cutil.collections.map.MultiSortedListSetHashMap;
import de.dhke.projects.cutil.collections.map.MultiTreeSetHashMap;
import de.dhke.projects.cutil.collections.set.Flat3Set;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentRBoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IAssertedRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.ITBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleProperty;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleType;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import java.util.*;
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.keyvalue.DefaultMapEntry;

/**
 *
 * @param <Name> The type for nominals and values
 * @param <Klass> The type for DL classes
 * @param <Role> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class RBox<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	implements IRBox<Name, Klass, Role> {

	private final AssertedRBox<Name, Klass, Role> _assertedRBox;
	private final MultiMap<Role, Role> _inverseRoles;
	private final MultiMap<Role, Role> _equivalentRoles;
	private final MultiMap<Role, Role> _subRoles;
	private final MultiMap<Role, Role> _superRoles;
	private final MultiMap<RoleProperty, Role> _propertyRoleMap;
	private final MultiMap<Role, RoleProperty> _rolePropertyMap;


	protected RBox(final AssertedRBox<Name, Klass, Role> assertedRBox)
	{
		_assertedRBox = assertedRBox;

		_rolePropertyMap = new MultiEnumSetHashMap<>(RoleProperty.class);
		_propertyRoleMap = new MultiTreeSetHashMap<>();

		_inverseRoles = new MultiSortedListSetHashMap<>();
		_equivalentRoles = new MultiSortedListSetHashMap<>();

		_subRoles = new MultiSortedListSetHashMap<>();
		_superRoles = new MultiSortedListSetHashMap<>();
	}


	@Override
	public ITBox<Name, Klass, Role> getTBox()
	{
		return _assertedRBox.getTBox();
	}


	protected void recalculate()
		throws EInconsistentRBoxException
	{
		/*
		 * clear local collections, initialize from asserted RBox
		 */
		_propertyRoleMap.clear();
		_propertyRoleMap.putAll(_assertedRBox.getPropertyRoles());

		_rolePropertyMap.clear();
		_rolePropertyMap.putAll(_assertedRBox.getRoleProperties());

		_inverseRoles.clear();
		_inverseRoles.putAll(_assertedRBox.getInverseRoles());

		_subRoles.clear();
		_subRoles.putAll(_assertedRBox.getSubRoles());

		_superRoles.clear();
		_superRoles.putAll(_assertedRBox.getSuperRoles());

		_equivalentRoles.clear();
		_equivalentRoles.putAll(_assertedRBox.getEquivalentRoles());

		addCommutativeInverses();
		addCommutativeEqualities();
		addSubSuper();

		/**
		 * Perform a fixed point iteration to update the role box.
		 */
		boolean isChanged = true;
		while (isChanged) {
			isChanged = false;
			isChanged |= updateInverseEqualities();
			isChanged |= updateEqualitiesSubSuper();
			isChanged |= updateTopEqualities();
			isChanged |= updateSubSuperEqualities();
			isChanged |= updateTopSubSuper();
			isChanged |= propagateRoleProperties();
		}
	}


	private boolean propagateRoleProperties()
	{
		boolean isChanged = false;
		isChanged |= propagateDown(RoleProperty.FUNCTIONAL);
		isChanged |= propagateDown(RoleProperty.INVERSE_FUNCTIONAL);
		isChanged |= propagateDown(RoleProperty.TRANSITIVE);
		isChanged |= propagateDown(RoleProperty.SYMMETRIC);
		return isChanged;
	}


	private boolean updateTopSubSuper()
	{
		boolean isChanged = false;
		final Collection<Role> topRoles = getRoles(RoleProperty.TOP);
		if (topRoles != null) {
			for (Role topRole : topRoles) {
				final RoleType topRoleType = getRoleType(topRole);
				if (getRoles(topRoleType) != null) {
					for (Role otherRole : getRoles(topRoleType)) {
						if (!isSubRole(topRole, otherRole)) {
							_subRoles.put(topRole, otherRole);
							_superRoles.put(otherRole, topRole);
							isChanged = true;
						}
					}
				}
			}
		}
		return isChanged;
	}


	private boolean updateEqualitiesSubSuper()
		throws EInconsistentRBoxException
	{
		boolean isChanged = false;

		for (Map.Entry<Role, Role> subRoleEntry : MultiMapEntryIterable.decorate(_subRoles.entrySet())) {
			final Role sup = subRoleEntry.getKey();
			final Role sub = subRoleEntry.getValue();
			if (getRoleType(sub) != getRoleType(sup)) {
				throw new EInconsistentRBoxException(_assertedRBox, String.format(
					"Roles `%s' and `%s' are subroles, but not of the same type", sub, sup));
			}
			if ((isSubRole(sub, sup)) && (!isEquivalentRole(sub, sup))) {
				_equivalentRoles.put(sub, sup);
				_equivalentRoles.put(sup, sub);
				isChanged = true;
			}
		}
		return isChanged;
	}


	private boolean updateTopEqualities()
	{
		boolean isChanged = false;
		final Collection<Role> topAdds = new TreeSet<>();
		if (getRoles(RoleProperty.TOP) != null) {
			for (Role topRole : getRoles(RoleProperty.TOP)) {
				for (Role topEq : getEquivalentRoles(topRole)) {
					if (!hasRoleProperty(topEq, RoleProperty.TOP)) {
						topAdds.add(topEq);
						isChanged = true;
					}
				}
			}
		}
		if (!topAdds.isEmpty()) {
			for (Role topAdd : topAdds) {
				_propertyRoleMap.put(RoleProperty.TOP, topAdd);
				_rolePropertyMap.put(topAdd, RoleProperty.TOP);
			}
			isChanged = true;
		}
		return isChanged;
	}


	private boolean updateSubSuperEqualities()
		throws EInconsistentRBoxException
	{
		boolean isChanged = false;

		for (Map.Entry<Role, Role> eq : MultiMapEntryIterable.decorate(_equivalentRoles)) {
			if (getRoleType(eq.getKey()) != getRoleType(eq.getValue())) {
				throw new EInconsistentRBoxException(_assertedRBox, String.format(
					"Roles `%s' and `%s' are equal, but of different type", eq.getKey(), eq.getValue()));
			}
			if (isInverseRole(eq.getKey(), eq.getValue())) {
				throw new EInconsistentRBoxException(_assertedRBox, String.format(
					"Roles `%s' and `%s' are inverses and cannot be equal", eq.getKey(), eq.getValue()));
			}

			if (!isSubRole(eq.getKey(), eq.getValue())) {
				_subRoles.put(eq.getKey(), eq.getValue());
				isChanged = true;
			}
			if (!isSuperRole(eq.getKey(), eq.getValue())) {
				_superRoles.put(eq.getKey(), eq.getValue());
				isChanged = true;
			}
		}
		return isChanged;
	}


	private boolean updateInverseEqualities()
		throws EInconsistentRBoxException
	{
		final Collection<Map.Entry<Role, Role>> addList = new HashSet<>();
		boolean isChanged = false;

		/**
		 * Iterate through all inverses
		 *
		 */
		for (Map.Entry<Role, Role> invEntry : MultiMapEntryIterable.decorate(_inverseRoles.entrySet())) {
			final Role first = invEntry.getValue();
			final Role second = invEntry.getKey();

			if (!hasRoleType(first, RoleType.OBJECT_PROPERTY)) {
				throw new EInconsistentRBoxException(_assertedRBox, String.format(
					"Role `%s' has an inverse, but is not an object property", first));
			}

			if (isEquivalentRole(first, second)) {
				throw new EInconsistentRBoxException(_assertedRBox, String.format(
					"Roles `%s' and `%s' are both inverses and equal", first, second));
			}

			/**
			 * All roles equal to the second role are inverses to the first role, too.
			 *
			 */
			final Collection<Role> secondEqs = getEquivalentRoles(second);
			for (Role secondEq : secondEqs) {
				if (!isInverseRole(first, secondEq)) {
					if (isEquivalentRole(first, secondEq)) {
						if (isInverseRole(first, secondEq)) {
							throw new EInconsistentRBoxException(_assertedRBox, String.format(
								"Roles `%s' and `%s' are equal and cannot be inverses", first, secondEq));
						}
					}
					addList.add(new DefaultMapEntry<>(first, secondEq));
				}
			}

			/**
			 * Inverses of the second (= inverses of the inverses of the first role) are equal to the first role.
			 */
			final Collection<Role> invInvs = getInverseRoles(second);
			if (invInvs != null) {
				for (Role invInv : invInvs) {
					final Collection<Role> invInvEqs = getEquivalentRoles(invInv);
					for (Role invInvEq : invInvEqs) {
						if (isInverseRole(first, invInvEq)) {
							throw new EInconsistentRBoxException(_assertedRBox, String.format(
								"Roles `%s' and `%s' are inverses and cannot be equal", first, invInvEq));
						}

						if (!isEquivalentRole(first, invInvEq)) {
							_equivalentRoles.put(first, invInvEq);
							isChanged = true;
						}
					}
				}
			}
		}

		if (!addList.isEmpty()) {
			for (Map.Entry<Role, Role> invAddEntry : addList) {
				_inverseRoles.put(invAddEntry.getKey(), invAddEntry.getValue());
				_inverseRoles.put(invAddEntry.getValue(), invAddEntry.getKey());
			}
			isChanged = true;
		}
		return isChanged;
	}


	private void addCommutativeInverses()
	{
		final Collection<Map.Entry<Role, Role>> addList = new HashSet<>();
		for (Map.Entry<Role, Role> invEntry : MultiMapEntryIterable.decorate(_inverseRoles.entrySet())) {
			if (!_inverseRoles.containsValue(invEntry.getValue(), invEntry.getKey())) {
				addList.add(invEntry);
			}
		}
		for (Map.Entry<Role, Role> addItem : addList) {
			_inverseRoles.put(addItem.getKey(), addItem.getValue());
		}
	}


	private void addCommutativeEqualities()
	{
		final Collection<Map.Entry<Role, Role>> addList = new HashSet<>();
		for (Map.Entry<Role, Role> invEntry : MultiMapEntryIterable.decorate(_equivalentRoles.entrySet())) {
			if (!_equivalentRoles.containsValue(invEntry.getValue(), invEntry.getKey())) {
				addList.add(invEntry);
			}
		}
		for (Map.Entry<Role, Role> addItem : addList) {
			_equivalentRoles.put(addItem.getKey(), addItem.getValue());
		}
	}


	private void addSubSuper()
	{
		final Collection<Map.Entry<Role, Role>> addList = new HashSet<>();


		for (Map.Entry<Role, Role> invEntry : MultiMapEntryIterable.decorate(_subRoles.entrySet())) {
			if (!_superRoles.containsValue(invEntry.getValue(), invEntry.getKey())) {
				addList.add(invEntry);
			}
		}
		for (Map.Entry<Role, Role> addItem : addList) {
			_superRoles.put(addItem.getKey(), addItem.getValue());
		}

		addList.clear();
		for (Map.Entry<Role, Role> invEntry : MultiMapEntryIterable.decorate(_superRoles.entrySet())) {
			if (!_subRoles.containsValue(invEntry.getValue(), invEntry.getKey())) {
				addList.add(invEntry);
			}
		}
		for (Map.Entry<Role, Role> addItem : addList) {
			_subRoles.put(addItem.getKey(), addItem.getValue());
		}
	}


	@Override
	public String toString()
	{
		return toString("");
	}


	@Override
	public String toString(String prefix)
	{
		/* ": Properties: [], Domains: [], Ranges: []".length() = 41 */
		StringBuilder sb = new StringBuilder(_assertedRBox.getRoles().size() * (prefix.length() + 41));

		for (Role role : _assertedRBox.getRoles()) {
			sb.append(role);
			sb.append(": Properties: ");
			sb.append(getRoleProperties(role));
			sb.append(", Domains: ");
			sb.append(getRoleDomains(role));
			sb.append(", Ranges: ");
			sb.append(getRoleRanges(role));
			sb.append("\n");
			sb.append(prefix);
		}
		return sb.toString();
	}


	@Override
	public Collection<Role> getEquivalentRoles(Role role)
	{
		if (!hasRole(role)) {
			throw new IllegalArgumentException(String.format("Unknown role `%s'", role));
		}

		final Collection<Role> roles = _equivalentRoles.get(role);
		if (roles != null) {
			return Collections.unmodifiableCollection(roles);
		} else {
			return null;
		}
	}


	@Override
	public Collection<Role> getInverseRoles(Role role)
	{
		if (!hasRole(role)) {
			throw new IllegalArgumentException(String.format("Unknown role `%s'", role));
		}

		final Collection<Role> roles = _inverseRoles.get(role);
		if (roles != null) {
			return Collections.unmodifiableCollection(roles);
		} else {
			return null;
		}
	}


	@Override
	public Collection<IDLRestriction<Name, Klass, Role>> getRoleDomains(final Role role)
	{
		final Set<IDLRestriction<Name, Klass, Role>> domains = new Flat3Set<>();

		for (Role superRole : getSuperRoles(role)) {
			final Collection<IDLRestriction<Name, Klass, Role>> assertedDomains = _assertedRBox.getRoleDomains(
				superRole);
			if (assertedDomains != null) {
				domains.addAll(assertedDomains);
			}
		}
		return domains;
	}


	@Override
	public Collection<IDLRestriction<Name, Klass, Role>> getRoleRanges(final Role role)
	{
		final Set<IDLRestriction<Name, Klass, Role>> ranges = new Flat3Set<>();

		for (Role superRole : getSuperRoles(role)) {
			final Collection<IDLRestriction<Name, Klass, Role>> assertedRanges = _assertedRBox.getRoleRanges(
				superRole);
			if (assertedRanges != null) {
				ranges.addAll(assertedRanges);
			}
		}
		return ranges;

	}


	@Override
	public Collection<RoleProperty> getRoleProperties(Role role)
	{
		final Collection<RoleProperty> properties = _rolePropertyMap.get(role);
		if (properties != null) {
			return Collections.unmodifiableCollection(properties);
		} else {
			return null;
		}
	}


	@Override
	public RoleType getRoleType(Role role)
	{
		return _assertedRBox.getRoleType(role);
	}


	@Override
	public boolean hasRole(Role role)
	{
		return (getRoleType(role) != null);
	}


	@Override
	public Collection<Role> getRoles()
	{
		return _assertedRBox.getRoles();
	}


	@Override
	public Collection<Role> getRoles(RoleProperty property)
	{
		final Collection<Role> roles = _propertyRoleMap.get(property);
		if (roles != null) {
			return Collections.unmodifiableCollection(roles);
		} else {
			return null;
		}
	}


	@Override
	public Collection<Role> getRoles(RoleType type)
	{
		return _assertedRBox.getRoles(type);
	}


	@Override
	public Collection<Role> getSubRoles(Role role)
	{
		if (!hasRole(role)) {
			throw new IllegalArgumentException(String.format("Unknown role `%s'", role));
		}

		final Collection<Role> subRoles = _subRoles.get(role);
		if (subRoles != null) {
			return Collections.unmodifiableCollection(subRoles);
		} else {
			return null;
		}
	}


	@Override
	public Collection<Role> getSuperRoles(Role role)
	{
		if (!hasRole(role)) {
			throw new IllegalArgumentException(String.format("Unknown role `%s'", role));
		}

		final Collection<Role> superRoles = _superRoles.get(role);
		if (superRoles != null) {
			return Collections.unmodifiableCollection(superRoles);
		} else {
			return null;
		}
	}


	@Override
	public boolean hasRoleProperty(Role role, RoleProperty property)
	{
		return _rolePropertyMap.containsValue(role, property);
	}


	@Override
	public boolean hasRoleType(Role role, RoleType roleType)
	{
		return getAssertedRBox().hasRoleType(role, roleType);
	}


	@Override
	public boolean isEquivalentRole(Role first, Role second)
	{
		return _equivalentRoles.containsValue(first, second);
	}


	@Override
	public boolean isInverseRole(Role first, Role second)
	{
		return _inverseRoles.containsValue(first, second);
	}


	@Override
	public boolean hasInverseRoles()
	{
		return !_inverseRoles.isEmpty();
	}


	@Override
	public boolean isSubRole(Role sup, Role sub)
	{
		return _subRoles.containsValue(sup, sub);
	}


	@Override
	public boolean isSuperRole(Role sub, Role sup)
	{
		return _superRoles.containsValue(sub, sup);
	}


	@Override
	public IAssertedRBox<Name, Klass, Role> getAssertedRBox()
	{
		return _assertedRBox;
	}


	private boolean propagateDown(final RoleProperty prop)
	{
		boolean isChanged = false;

		for (Role superRole : _subRoles.keySet()) {
			if (hasRoleProperty(superRole, prop)) {
				for (Role subRole : _subRoles.get(superRole)) {
					if (!hasRoleProperty(subRole, prop)) {
						_rolePropertyMap.put(subRole, prop);
						_propertyRoleMap.put(prop, subRole);
						isChanged = true;
					}
				}
			}
		}
		return isChanged;
	}
}

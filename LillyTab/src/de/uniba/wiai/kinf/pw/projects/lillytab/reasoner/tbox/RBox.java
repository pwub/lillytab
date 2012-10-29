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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.tbox;

import de.dhke.projects.cutil.collections.immutable.ImmutableMultiMap;
import de.dhke.projects.cutil.collections.iterator.MultiMapItemIterable;
import de.dhke.projects.cutil.collections.map.MultiEnumSetHashMap;
import de.dhke.projects.cutil.collections.map.MultiSortedListSetHashMap;
import de.dhke.projects.cutil.collections.map.MultiTreeSetHashMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentRBoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.*;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IAssertedRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.ITBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleProperty;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleType;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.Map.Entry;
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.keyvalue.DefaultMapEntry;


/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class RBox<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	implements IRBox<Name, Klass, Role>
{
	private final WeakReference<AssertedRBox<Name, Klass, Role>> _assertedRBoxRef;
	private final MultiMap<Role, IDLRestriction<Name, Klass, Role>> _roleDomains = new MultiTreeSetHashMap<Role, IDLRestriction<Name, Klass, Role>>();
	private final MultiMap<Role, IDLRestriction<Name, Klass, Role>> _roleRanges = new MultiTreeSetHashMap<Role, IDLRestriction<Name, Klass, Role>>();
	private final MultiMap<Role, Role> _inverseRoles;
	private final MultiMap<Role, Role> _equivalentRoles;
	private final MultiMap<Role, Role> _subRoles;
	private final MultiMap<Role, Role> _superRoles;
	private final MultiMap<RoleProperty, Role> _propertyRoleMap;
	private final MultiMap<Role, RoleProperty> _rolePropertyMap;

	protected RBox(final AssertedRBox<Name, Klass, Role> assertedRBox)
	{
		_assertedRBoxRef = new WeakReference<AssertedRBox<Name, Klass, Role>>(assertedRBox);

		_rolePropertyMap = new MultiEnumSetHashMap<Role, RoleProperty>(RoleProperty.class);
		_propertyRoleMap = new MultiTreeSetHashMap<RoleProperty, Role>();

		_inverseRoles = new MultiSortedListSetHashMap<Role, Role>();
		_equivalentRoles = new MultiSortedListSetHashMap<Role, Role>();

		_subRoles = new MultiSortedListSetHashMap<Role, Role>();
		_superRoles = new MultiSortedListSetHashMap<Role, Role>();
	}

	@Override
	public ITBox<Name, Klass, Role> getTBox()
	{
		return _assertedRBoxRef.get().getTBox();
	}

	protected void recalculate()
		throws EInconsistentRBoxException
	{
		final AssertedRBox<Name, Klass, Role> assertedRBox = _assertedRBoxRef.get();

		/*
		 * clear local collections, initialize from asserted RBox
		 */
		_propertyRoleMap.clear();
		_propertyRoleMap.putAll(assertedRBox.getPropertyRoles());

		_rolePropertyMap.clear();
		_rolePropertyMap.putAll(assertedRBox.getRoleProperties());


		_inverseRoles.clear();
		_inverseRoles.putAll(assertedRBox.getInverseRoles());

		_subRoles.clear();
		_subRoles.putAll(assertedRBox.getSubRoles());

		_superRoles.clear();
		_superRoles.putAll(assertedRBox.getSuperRoles());

		_equivalentRoles.clear();
		_equivalentRoles.putAll(assertedRBox.getEquivalentRoles());

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
		}
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

	private boolean updateEqualitiesSubSuper() throws EInconsistentRBoxException
	{
		final AssertedRBox<Name, Klass, Role> assertedRBox = _assertedRBoxRef.get();
		boolean isChanged = false;

		for (Map.Entry<Role, Role> subRoleEntry : MultiMapItemIterable.decorate(_subRoles.entrySet())) {
			final Role sup = subRoleEntry.getKey();
			final Role sub = subRoleEntry.getValue();
			if (getRoleType(sub) != getRoleType(sup))
				throw new EInconsistentRBoxException(assertedRBox, String.format(
					"Roles `%s' and `%s' are subroles, but not of the same type", sub, sup));
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
		final Collection<Role> topAdds = new TreeSet<Role>();
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

	private boolean updateSubSuperEqualities() throws EInconsistentRBoxException
	{
		final AssertedRBox<Name, Klass, Role> assertedRBox = _assertedRBoxRef.get();		
		boolean isChanged = false;
		
		for (Map.Entry<Role, Role> eq : MultiMapItemIterable.decorate(_equivalentRoles)) {
			if (getRoleType(eq.getKey()) != getRoleType(eq.getValue()))
				throw new EInconsistentRBoxException(assertedRBox, String.format(
					"Roles `%s' and `%s' are equal, but of different type", eq.getKey(), eq.getValue()));
			if (isInverseRole(eq.getKey(), eq.getValue()))
				throw new EInconsistentRBoxException(assertedRBox, String.format("Roles `%s' and `%s' are inverses and cannot be equal", eq.getKey(), eq.getValue()));				

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

	private boolean updateInverseEqualities() throws EInconsistentRBoxException
	{
		final AssertedRBox<Name, Klass, Role> assertedRBox = _assertedRBoxRef.get();		
		final Collection<Map.Entry<Role, Role>> addList = new HashSet<Map.Entry<Role, Role>>();		
		boolean isChanged = false;
		
		/**
		 * Iterate through all inverses
		 * 
		 */
		for (Map.Entry<Role, Role> invEntry : MultiMapItemIterable.decorate(_inverseRoles.entrySet())) {
			final Role first = invEntry.getValue();
			final Role second = invEntry.getKey();

			if (!hasRoleType(first, RoleType.OBJECT_PROPERTY))
				throw new EInconsistentRBoxException(assertedRBox, String.format(
					"Role `%s' has an inverse, but is not an object property", first));

			if (isEquivalentRole(first, second))
				throw new EInconsistentRBoxException(assertedRBox, String.format(
					"Roles `%s' and `%s' are both inverses and equal", first, second));

			/**
			 * All roles equal to the second role are inverses to the first role, too.
			 *
			 */
			final Collection<Role> secondEqs = getEquivalentRoles(second);
			for (Role secondEq : secondEqs) {
				if (!isInverseRole(first, secondEq)) {
					if (isEquivalentRole(first, secondEq))
						if (isInverseRole(first, secondEq))
							throw new EInconsistentRBoxException(assertedRBox, String.format("Roles `%s' and `%s' are equal and cannot be inverses", first, secondEq));
					addList.add(new DefaultMapEntry<Role, Role>(first, secondEq));
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
						if (isInverseRole(first, invInvEq))
							throw new EInconsistentRBoxException(assertedRBox, String.format("Roles `%s' and `%s' are inverses and cannot be equal", first, invInvEq));
						
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
		final Collection<Map.Entry<Role, Role>> addList = new HashSet<Map.Entry<Role, Role>>();
		for (Map.Entry<Role, Role> invEntry : MultiMapItemIterable.decorate(_inverseRoles.entrySet())) {
			if (!_inverseRoles.containsValue(invEntry.getValue(), invEntry.getKey()))
				addList.add(invEntry);
		}
		for (Map.Entry<Role, Role> addItem : addList)
			_inverseRoles.put(addItem.getKey(), addItem.getValue());
	}

	private void addCommutativeEqualities()
	{
		final Collection<Map.Entry<Role, Role>> addList = new HashSet<Map.Entry<Role, Role>>();
		for (Map.Entry<Role, Role> invEntry : MultiMapItemIterable.decorate(_equivalentRoles.entrySet())) {
			if (!_equivalentRoles.containsValue(invEntry.getValue(), invEntry.getKey()))
				addList.add(invEntry);
		}
		for (Map.Entry<Role, Role> addItem : addList)
			_equivalentRoles.put(addItem.getKey(), addItem.getValue());
	}

	private void addSubSuper()
	{
		final Collection<Map.Entry<Role, Role>> addList = new HashSet<Map.Entry<Role, Role>>();
		for (Map.Entry<Role, Role> invEntry : MultiMapItemIterable.decorate(_subRoles.entrySet())) {
			if (!_superRoles.containsValue(invEntry.getValue(), invEntry.getKey()))
				addList.add(invEntry);
		}
		for (Map.Entry<Role, Role> addItem : addList)
			_superRoles.put(addItem.getKey(), addItem.getValue());

		addList.clear();
		for (Map.Entry<Role, Role> invEntry : MultiMapItemIterable.decorate(_superRoles.entrySet())) {
			if (!_subRoles.containsValue(invEntry.getValue(), invEntry.getKey()))
				addList.add(invEntry);
		}
		for (Map.Entry<Role, Role> addItem : addList)
			_subRoles.put(addItem.getKey(), addItem.getValue());
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

	public Collection<Role> getEquivalentRoles(Role role)
	{
		final Collection<Role> roles = _equivalentRoles.get(role);
		if (roles != null)
			return Collections.unmodifiableCollection(roles);
		else
			return null;
	}

	public Collection<Role> getInverseRoles(Role role)
	{
		final Collection<Role> roles = _inverseRoles.get(role);
		if (roles != null)
			return Collections.unmodifiableCollection(roles);
		else
			return null;
	}

	public MultiMap<Role, IDLRestriction<Name, Klass, Role>> getRoleDomains()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Collection<RoleProperty> getRoleProperties(Role role)
	{
		final Collection<RoleProperty> properties = _rolePropertyMap.get(role);
		if (properties != null)
			return Collections.unmodifiableCollection(properties);
		else
			return null;
	}

	public MultiMap<Role, IDLRestriction<Name, Klass, Role>> getRoleRanges()
	{
		return ImmutableMultiMap.decorate(_roleRanges);
	}

	public RoleType getRoleType(Role role)
	{
		return _assertedRBoxRef.get().getRoleType(role);
	}

	public Collection<Role> getRoles()
	{
		return _assertedRBoxRef.get().getRoles();
	}

	public Collection<Role> getRoles(RoleProperty property)
	{
		final Collection<Role> roles = _propertyRoleMap.get(property);
		if (roles != null)
			return Collections.unmodifiableCollection(roles);
		else
			return null;
	}

	public Collection<Role> getRoles(RoleType type)
	{
		return _assertedRBoxRef.get().getRoles(type);
	}

	public Collection<Role> getSubRoles(Role role)
	{
		final Collection<Role> subRoles = _subRoles.get(role);
		if (subRoles != null)
			return Collections.unmodifiableCollection(subRoles);
		else
			return null;
	}

	public Collection<Role> getSuperRoles(Role role)
	{
		final Collection<Role> superRoles = _superRoles.get(role);
		if (superRoles != null)
			return Collections.unmodifiableCollection(superRoles);
		else
			return null;
	}

	public boolean hasRoleProperty(Role role, RoleProperty property)
	{
		return _rolePropertyMap.containsValue(role, property);
	}

	public boolean hasRoleType(Role role, RoleType roleType)
	{
		return getAssertedRBox().hasRoleType(role, roleType);
	}

	public boolean isEquivalentRole(Role first, Role second)
	{
		return _equivalentRoles.containsValue(first, second);
	}

	public boolean isInverseRole(Role first, Role second)
	{
		return _inverseRoles.containsValue(first, second);
	}

	public boolean hasInverseRoles()
	{
		return !_inverseRoles.isEmpty();
	}

	public boolean isSubRole(Role sup, Role sub)
	{
		return _subRoles.containsValue(sup, sub);
	}

	public boolean isSuperRole(Role sub, Role sup)
	{
		return _superRoles.containsValue(sub, sup);
	}

	public IAssertedRBox<Name, Klass, Role> getAssertedRBox()
	{
		return _assertedRBoxRef.get();
	}
}

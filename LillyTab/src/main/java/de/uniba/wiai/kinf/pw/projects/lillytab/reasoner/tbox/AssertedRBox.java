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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.tbox;

import de.dhke.projects.cutil.collections.factories.EnumSetFactory;
import de.dhke.projects.cutil.collections.factories.ICollectionFactory;
import de.dhke.projects.cutil.collections.factories.SortedListSetFactory;
import de.dhke.projects.cutil.collections.factories.TreeSetFactory;
import de.dhke.projects.cutil.collections.iterator.MultiMapEntryIterator;
import de.dhke.projects.cutil.collections.map.GenericMultiHashMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentRBoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.*;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLNodeTerm;
import java.lang.ref.WeakReference;
import java.util.*;
import org.apache.commons.collections15.MultiMap;

/**
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class AssertedRBox<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	implements IAssertedRBox<I, L, K, R> {

	static final long serialVersionUID = 2484966322395859950L;
	// <editor-fold defaultstate="collapsed" desc="private variables">
	private final WeakReference<ITBox<I, L, K, R>> _tboxRef;
	private final RBox<I, L, K, R> _rbox;
	private final MultiMap<R, IDLClassExpression<I, L, K, R>> _roleDomains;
	private final MultiMap<R, IDLNodeTerm<I, L, K, R>> _roleRanges;
	private final MultiMap<R, R> _inverseRoles;
	private final MultiMap<R, R> _equivalentRoles;
	private final MultiMap<R, R> _subRoles;
	private final MultiMap<R, R> _superRoles;
	private final MultiMap<RoleProperty, R> _propertyRoleMap;
	private final MultiMap<R, RoleProperty> _rolePropertyMap;
	private final Map<R, RoleType> _roleTypeMap;
	private final MultiMap<RoleType, R> _typeRoleMap;
	// </editor-fold>


	public AssertedRBox(ITBox<I, L, K, R> tbox)
	{
		super();
		_tboxRef = new WeakReference<>(tbox);

		_roleDomains = new GenericMultiHashMap<>(new TreeSetFactory<IDLClassExpression<I, L, K, R>>());
		_roleRanges = new GenericMultiHashMap<>(new TreeSetFactory<IDLNodeTerm<I, L, K, R>>());

		final ICollectionFactory<R, ? extends Collection<R>> roleSetFactory = new SortedListSetFactory<>();

		_roleTypeMap = new HashMap<>();
		_typeRoleMap = new GenericMultiHashMap<>(roleSetFactory);

		_rolePropertyMap = new GenericMultiHashMap<>(new EnumSetFactory<>(RoleProperty.class));
		_propertyRoleMap = new GenericMultiHashMap<>(roleSetFactory);

		_inverseRoles = new GenericMultiHashMap<>(roleSetFactory);
		_equivalentRoles = new GenericMultiHashMap<>(roleSetFactory);

		_subRoles = new GenericMultiHashMap<>(roleSetFactory);
		_superRoles = new GenericMultiHashMap<>(roleSetFactory);

		_rbox = new RBox<>(this);
	}


	@Override
	public ITBox<I, L, K, R> getTBox()
	{
		return _tboxRef.get();
	}


	@Override
	public boolean addRole(R role, RoleType roleType) throws EInconsistentRBoxException
	{
		if (roleType == null) {
			throw new IllegalArgumentException("roleType cannot be null");
		} else {
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
			} else if (existingRoleType == roleType) {
				return false;
			} else {
				throw new EInconsistentRBoxException(this, String.format(
					"Cannot add role `%s' with different role type `%s' (has type `%s')", role, roleType,
					existingRoleType));
			}
		}
	}


	@Override
	public boolean removeRole(R role)
	{
		final RoleType roleType = _roleTypeMap.remove(role);
		if (roleType != null) {
			_typeRoleMap.remove(roleType, role);

			_roleDomains.remove(role);
			_roleRanges.remove(role);

			if ((_equivalentRoles.get(role) != null) && (!_equivalentRoles.isEmpty())) {
				final List<R> eqRoles = new ArrayList<>(_equivalentRoles.get(role));
				for (R eqRole : eqRoles) {
					_equivalentRoles.remove(eqRole, role);
				}
			}
			_equivalentRoles.remove(role);

			if ((_inverseRoles.get(role) != null) && (!_inverseRoles.get(role).isEmpty())) {
				final List<R> invRoles = new ArrayList<>(_inverseRoles.get(role));
				for (R invRole : invRoles) {
					_inverseRoles.remove(invRole, role);
				}
			}
			_inverseRoles.remove(role);

			final Iterator<Map.Entry<R, R>> subIter = MultiMapEntryIterator.decorate(_subRoles);
			while (subIter.hasNext()) {
				final Map.Entry<R, R> subEntry = subIter.next();
				if (role.equals(subEntry.getKey()) || role.equals(subEntry.getValue())) {
					subIter.remove();
				}
			}

			final Iterator<Map.Entry<R, R>> superIter = MultiMapEntryIterator.decorate(_superRoles);
			while (subIter.hasNext()) {
				final Map.Entry<R, R> supEntry = superIter.next();
				if (role.equals(supEntry.getKey()) || role.equals(supEntry.getValue())) {
					subIter.remove();
				}
			}


			return true;
		} else {
			return false;
		}
	}


	@Override
	public Collection<R> getRoles()
	{
		final Collection<R> value = _roleTypeMap.keySet();
		if (value != null) {
			return Collections.unmodifiableCollection(value);
		} else {
			return Collections.emptySet();
		}
	}


	@Override
	public Collection<R> getRoles(final RoleProperty property)
	{
		final Collection<R> value = _propertyRoleMap.get(property);
		if (value != null) {
			return Collections.unmodifiableCollection(value);
		} else {
			return Collections.emptySet();
		}
	}


	// <editor-fold defaultstate="collapsed" desc="R Type Management">
	@Override
public Collection<R> getRoles(RoleType type)
	{
		return Collections.unmodifiableCollection(_typeRoleMap.get(type));
	}


	@Override
	public void setRoleType(R role, RoleType roleType) throws EInconsistentRBoxException
	{
		final RoleType preRoleType = _roleTypeMap.get(role);
		final IRBox<I, L, K, R> rbox = getRBox();

		if ((roleType == RoleType.DATA_PROPERTY) && (rbox.getInverseRoles(role) != null) && (!rbox.getInverseRoles(role).
			isEmpty())) {
			throw new EInconsistentRBoxException(this, String.format(
				"Error switching role `%s' to datatype role, role has inverses", role));
		}

		if (preRoleType != roleType) {
			if ((rbox.getEquivalentRoles(role) != null) && (!rbox.getEquivalentRoles(role).isEmpty())) {
				throw new EInconsistentRBoxException(this, String.format(
					"Cannot change type of role `%s' to `%s', has equivalent roles", role, roleType));
			}
			if ((rbox.getSubRoles(role) != null) && (!rbox.getSubRoles(role).isEmpty())) {
				throw new EInconsistentRBoxException(this, String.format(
					"Cannot change type of role `%s' to `%s', has subroles", role, roleType));
			}
			if ((rbox.getSuperRoles(role) != null) && (!rbox.getSuperRoles(role).isEmpty())) {
				throw new EInconsistentRBoxException(this, String.format(
					"Cannot change type of role `%s' to `%s', has superroles", role, roleType));
			}

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


	@Override
	public boolean hasRoleType(R role, RoleType roleType)
	{
		assert roleType != null;
		return roleType == _roleTypeMap.get(role);
	}


	@Override
	public RoleType getRoleType(R role)
	{
		final RoleType roleType = _roleTypeMap.get(role);
		if (roleType == null) {
			throw new IllegalArgumentException(String.format("R type not known for role `%s'", role));
		} else {
			return roleType;
		}
	}


	// </editor-fold>
	@Override
	public boolean setRoleProperty(R role, RoleProperty property) throws EInconsistentRBoxException
	{
		if (!_roleTypeMap.containsKey(role)) {
			throw new IllegalArgumentException(String.format("Must set role type for role `%s', first", role));
		} else if (property == null) {
			throw new IllegalArgumentException("R property cannot be null");
		} else if (_rolePropertyMap.containsValue(role, property)) /*
		 * role type already set
		 */ {
			return false;
		} else {
			if (property == RoleProperty.TOP) {
				if (_propertyRoleMap.containsKey(RoleProperty.TOP)) {
					for (R topRole : _propertyRoleMap.get(RoleProperty.TOP)) {
						if ((!topRole.equals(role)) && (getRoleType(topRole) == getRoleType(role))) {
							throw new EInconsistentRBoxException(this, String.format(
								"Cannot add another top role of this role type (existing top role: `%s')", topRole));
						}
					}
				}
			} else if ((property == RoleProperty.TRANSITIVE) && (_roleTypeMap.get(role) != RoleType.OBJECT_PROPERTY)) {
				throw new EInconsistentRBoxException(this, String.format(
					"role `%s': only object properties can be transitive", role));
			} else if ((property == RoleProperty.TRANSITIVE) && (getRBox().
				hasRoleProperty(role, RoleProperty.FUNCTIONAL))) {
				throw new EInconsistentRBoxException(this, String.format(
					"role `%s': roles can only be functional or transitive, not both", role));
			} else if ((property == RoleProperty.FUNCTIONAL) && (getRBox().
				hasRoleProperty(role, RoleProperty.TRANSITIVE))) {
				throw new EInconsistentRBoxException(this, String.format(
					"role `%s': roles can only be functional or transitive, not both", role));
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


	@Override
	public boolean clearRoleProperty(R role, RoleProperty property)
	{
		if (_rolePropertyMap.containsValue(role, property)) {
			_rolePropertyMap.remove(role, property);
			_propertyRoleMap.remove(property, role);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean hasRoleProperty(R role, RoleProperty property)
	{
		return _rolePropertyMap.containsValue(role, property);
	}


	@Override
	public Collection<RoleProperty> getRoleProperties(R role)
	{
		final Collection<RoleProperty> properties = _rolePropertyMap.get(role);
		if (properties != null) {
			return Collections.unmodifiableCollection(properties);
		} else {
			return null;
		}
	}
	// </editor-fold>
	

	// <editor-fold defaultstate="collapsed" desc="Domain and Range management">
	@Override
	public MultiMap<R, IDLClassExpression<I, L, K, R>> getRoleDomains()
	{
		return _roleDomains;
	}


	@Override
	public MultiMap<R, IDLNodeTerm<I, L, K, R>> getRoleRanges()
	{
		return _roleRanges;
	}

	@Override
	public Collection<IDLNodeTerm<I, L, K, R>> getRoleRanges(R role)
	{
		return _roleRanges.get(role);
	}


	@Override
	public Collection<IDLClassExpression<I, L, K, R>> getRoleDomains(R role)
	{
		return _roleDomains.get(role);
	}


	/// </editor-fold>
	@Override
	public boolean removeEquivalentRole(R first, R second)
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
		} else {
			return false;
		}
	}


	@Override
	public boolean addEquivalentRole(R first, R second) throws EInconsistentRBoxException
	{
		final IRBox<I, L, K, R> rbox = getRBox();
		if (rbox.isInverseRole(first, second)) {
			throw new EInconsistentRBoxException(this, String.format("`%s' is inverse of `%s', cannot be equivalent",
																	 first,
																	 second));
		} else if (getRoleType(first) != getRoleType(second)) {
			throw new EInconsistentRBoxException(this, String.format(
				"`%s' and `%s' have different types and cannot be equal", first, second));
		} else {
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
			} else {
				return false;
			}
		}
	}

	@Override
	public Set<R> getEquivalentRoles(R role)
	{
		Set<R> eqRoles = (Set<R>) _equivalentRoles.get(role);
		if (eqRoles == null) {
			throw new NoSuchElementException("Unknown role:" + role);
		}

		assert eqRoles != null;
		return Collections.unmodifiableSet(eqRoles);
	}


	@Override
	public boolean isEquivalentRole(R first, R second)
	{
		return _equivalentRoles.containsValue(first, second);
	}


	@Override
	public boolean removeInverseRole(R first, R second)
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
		} else {
			return false;
		}
	}


	@Override
	public boolean addInverseRole(final R first, final R second) throws EInconsistentRBoxException
	{
		final IRBox<I, L, K, R> rbox = getRBox();
		if (first.equals(second)) {
			throw new EInconsistentRBoxException(this, String.format("R `%s' cannot be its own inverse", first));
		} else if (rbox.hasRoleType(first, RoleType.DATA_PROPERTY)) {
			throw new EInconsistentRBoxException(this, String.format("Datatype role `%s' cannot have inverses", first));
		} else if (rbox.hasRoleType(second, RoleType.DATA_PROPERTY)) {
			throw new EInconsistentRBoxException(this, String.format("Datatype role `%s' cannot have inverses", second));
		} else if (rbox.isSubRole(first, second)) {
			throw new EInconsistentRBoxException(this, String.format("Inverse role `%s' is a subrole of `%s'", first,
																	 second));
		} else if (rbox.isSuperRole(first, second)) {
			throw new EInconsistentRBoxException(this, String.format("Inverse role `%s' is a superrole of `%s'", first,
																	 second));
		} else if (rbox.isEquivalentRole(first, second)) {
			throw new EInconsistentRBoxException(this, String.format(
				"`%s' and `%s' are equivalent and cannot be inverses", first, second));
		} else if (_inverseRoles.containsValue(first, second)) {
			return false;
		} else {
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

	@Override
	public Set<R> getInverseRoles(R role)
	{
		final Set<R> invRoles = (Set<R>) _inverseRoles.get(role);
		if (invRoles == null) {
			throw new NoSuchElementException("Unknown role: " + role);
		} else {
			return Collections.unmodifiableSet(invRoles);
		}
	}


	@Override
	public boolean isInverseRole(R first, R second)
	{
		return _inverseRoles.containsValue(first, second);
	}


	@Override
	public boolean hasInverseRoles()
	{
		return !_inverseRoles.isEmpty();
	}


	@Override
	public Set<R> getSuperRoles(R role)
	{
		final Set<R> superRoles = (Set<R>) _superRoles.get(role);
		if (superRoles == null) {
			throw new NoSuchElementException("Unknown role:" + role);
		}

		assert superRoles != null;
		return Collections.unmodifiableSet(superRoles);
	}


	@Override
	public Set<R> getSubRoles(R role)
	{
		final Set<R> subRoles = (Set<R>) _subRoles.get(role);
		if (subRoles == null) {
			throw new NoSuchElementException("Unknown role:" + role);
		}
		assert subRoles != null;
		return Collections.unmodifiableSet(subRoles);
	}


	@Override
	public boolean removeSubRole(R sup, R sub)
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
		} else {
			return false;
		}
	}


	@Override
	public boolean addSubRole(R sup, R sub) throws EInconsistentRBoxException
	{
		if (isInverseRole(sup, sub)) {
			throw new EInconsistentRBoxException(this, String.format(
				"R `%s' is inverse of `%s', cannot be subroles", sup, sup));
		} else if (getRoleType(sup) != getRoleType(sub)) {
			throw new EInconsistentRBoxException(this, String.format(
				"Roles `%s' and `%s' have different types, cannot be subroles", sup, sub));
		} else if (!_subRoles.containsValue(sup, sub)) {
			_subRoles.put(sup, sub);
			_superRoles.put(sub, sup);
			recalculate();
			return true;
		} else {
			return false;
		}
	}


	@Override
	public boolean isSubRole(R sub, R sup)
	{
		return _subRoles.containsValue(sup, sub);
	}


	@Override
	public boolean isSuperRole(R sup, R sub)
	{
		return _superRoles.containsValue(sub, sup);
	}


	@Override
	public IAssertedRBox<I, L, K, R> getAssertedRBox()
	{
		return this;
	}


		public RBox<I, L, K, R> getRBox()
	{
		return _rbox;
	}


	@Override
	public String toString()
	{
		return toString("");
	}


	@Override
	public String toString(String prefix)
	{
		StringBuilder sb = new StringBuilder();
		Set<R> roles = new TreeSet<>();
		roles.addAll(_rolePropertyMap.keySet());;
		roles.addAll(getRoleDomains().keySet());
		roles.addAll(getRoleRanges().keySet());

		for (R role : roles) {
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


	@Override
	public boolean hasRole(R role)
	{
		return _roleTypeMap.get(role) != null;
	}


	@Override
	public IAssertedRBox<I, L, K, R> getImmutable()
	{
		return new ImmutableAssertedRBox<>(getTBox(), getRBox(), getAssertedRBox());
	}


	protected MultiMap<R, R> getEquivalentRoles()
	{
		return _equivalentRoles;
	}


		protected MultiMap<R, R> getSubRoles()
	{
		return _subRoles;
	}


	protected MultiMap<R, R> getSuperRoles()
	{
		return _superRoles;
	}


		protected MultiMap<R, R> getInverseRoles()
	{
		return _inverseRoles;
	}


		protected MultiMap<R, RoleProperty> getRoleProperties()
	{
		return _rolePropertyMap;
	}


		protected MultiMap<RoleProperty, R> getPropertyRoles()
	{
		return _propertyRoleMap;
	}


		private void recalculate() throws EInconsistentRBoxException
	{
		_rbox.recalculate();
	}
}

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
package de.uniba.wiai.kinf.pw.projects.lillytab.tbox;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentRBoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import org.apache.commons.collections15.MultiMap;

/**
 * <p>
 * Role box containing information about various role parameters.
 * </p><p>
 * Note that roles must be made known to the RBox prior to use. This is done by assigned a role one of either
 * {@link RoleProperty#DATA_PROPERTY} or {@link RoleProperty#OBJECT_PROPERTY}.
 * </p>
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 *
 * @param <Role> The type for properties (roles)
 */
public interface IAssertedRBox<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends IRBox<Name, Klass, Role> {

	/**
	 * <p>
	 * Add the specified role to the RBox.
	 * </p><p>
	 * To change the role type of an existing role, you must remove it, first.
	 * </p>
	 *
	 * @returns {@literal true} if the role was successfully added or {@literal false} if the role did already exist
	 * with the specified {@literal roleType}.
	 * @throws EInconsistentRBoxException The add operation would cause an inconsistency.
	 *
	 */
	boolean addRole(final Role role, final RoleType roleType)
		throws EInconsistentRBoxException;


	/**
	 * Remove the specified role from the RBox.
	 *
	 * @returns {@literal true} if the role was successfully removed or {@literal false} if the role did not exist.
	 *
	 */
	boolean removeRole(final Role role);


	/**
	 * Change the type of {@literal role} to {@literal roleType}, if possible. Inconsistencies are not resolved
	 * automatically, but rather an exception is thrown.
	 *
	 */
	void setRoleType(final Role role, final RoleType roleType)
		throws EInconsistentRBoxException;

	// <editor-fold defaultstate="collapsed" desc="role assertion management">

	/**
	 * <p>
	 * Remove the equivalence assertion between the two roles.
	 * </p><p>
	 * Note that removing the assertion need not remove the equivalence relationship, as this may still be derived by
	 * other means.
	 * </p>
	 *
	 * @param first The first role.
	 * @param second The second role.
	 * @return {@literal true}, if the equivalence assertion between {@literal first} and {@literal second} was removed.
	 * {@literal false} if the assertion could not be removed.
	 */
	boolean removeEquivalentRole(final Role first, final Role second);


	/**
	 * Add an equivalence assertion between {@literal first} and {@literal second}.
	 *
	 * @param first The first role
	 * @param second The second role
	 * @return {@literal true} if the operation was successful.
	 * @throws EInconsistentRBoxException The addition would cause an inconsistency
	 */
	boolean addEquivalentRole(final Role first, final Role second)
		throws EInconsistentRBoxException;


	/**
	 * <p>
	 * Remove the equivalence assertion between the two roles {@literal first} and {@literal second}.
	 * <p></p>
	 * Note that removing the assertion need not remove the equivalence relationship, as this may still be derived by
	 * other means.
	 * </p>
	 *
	 * @param first The first role
	 * @param second The second role
	 *
	 * @return {@literal true} if the assertion was successfully removed and {@literal false} if the assertion did not
	 * exist.
	 *
	 *
	 */
	boolean removeInverseRole(final Role first, final Role second);


	/**
	 * Add an inverse role assertion between {@literal first} and {@literal second}.
	 *
	 * @param first The first role
	 * @param second The second role
	 *
	 * @return {@literal true} if the assertion was successfully added.
	 * @throws EInconsistentRBoxException The addition would cause an inconsistency
	 *
	 */
	boolean addInverseRole(final Role first, final Role second)
		throws EInconsistentRBoxException;


	/**
	 * <p>
	 * Remove the subrole assertion between {@literal sup} and {@literal sub}.
	 * </p><p>
	 * Note that removing the assertion need not remove the subrole relationship, as this may still be derived by other
	 * means.
	 * </p>
	 *
	 * @param sup The super role
	 * @param sub The sub role.
	 * @return {@literal true} if the assertion was sucessfully removed.
	 *
	 */
	boolean removeSubRole(final Role sup, final Role sub);


	/**
	 * <p>
	 * Add a new subrole assertion between {@literal sup} and {@literal sub}
	 * </p><p>
	 * </p>
	 *
	 * @param sup The super role
	 * @param sub The sub role
	 * @return {@literal true} if the subrole assertion was added successfully.
	 * @throws EInconsistentRBoxException The addition would cause an inconsistency
	 */
	boolean addSubRole(final Role sup, final Role sub)
		throws EInconsistentRBoxException;


	/**
	 * Set the specified property for a role.
	 *
	 * @param role The role to modify
	 * @param property The property to set.
	 * @return True, if the role's properties were modified.
	 * @throws EInconsistentRBoxException The addition would cause an inconsistency
	 */
	boolean setRoleProperty(Role role, RoleProperty property)
		throws EInconsistentRBoxException;


	/**
	 * <p>
	 * Clear the specified property for a role.
	 * </p><p>
	 * Clearing an asserted role property does not affect the derived properties of a role.
	 * </p>
	 *
	 * @param role The role to modify
	 * @param property The property to clear.
	 * @return True, if the role's properties were modified.
	 *
	 */
	boolean clearRoleProperty(Role role, RoleProperty property);


	/**
	 * <p> A multimap, defining the set of domains of a role. </p><p> A domain of a role is an
	 * {@link IDLClassExpression} that the <em>source</em> end of a role link must be a subclass of. </p>
	 *
	 * @return a {@link MultiMap} defining domain restrictions for roles.
	 */
	MultiMap<Role, IDLRestriction<Name, Klass, Role>> getRoleDomains();


	/**
	 * <p> A multimap, defining the set of ranges of a role. </p><p> A range of a role is an {@link IDLClassExpression}
	 * that the <em>target</em> end of a role link must be a subclass of. </p>
	 *
	 * @return a {@link MultiMap} defining range restrictions for roles.
	 */
	MultiMap<Role, IDLRestriction<Name, Klass, Role>> getRoleRanges();
}

/**
 * (c) 2009-2014 Otto-Friedrich-University Bamberg
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
package de.uniba.wiai.kinf.pw.projects.lillytab.tbox;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentRBoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLNodeTerm;
import org.apache.commons.collections15.MultiMap;

/**
 *
 * R box containing information about various role parameters.
 * <p />
 * Note that roles must be made known to the RBox prior to use. This is done by assigned a role one of either
 * {@link RoleProperty#DATA_PROPERTY} or {@link RoleProperty#OBJECT_PROPERTY}.
 * <p/>
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public interface IAssertedRBox<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends IRBox<I, L, K, R> {

	/**
	 *
	 * Add the specified role to the RBox.
	 * <p />
	 * To change the role type of an existing role, you must remove it, first.
	 * <p/>
	 *
	 * @returns {@literal true} if the role was successfully added or {@literal false} if the role did already exist
	 * with the specified {@literal roleType}.
	 * @throws EInconsistentRBoxException The add operation would cause an inconsistency.
	 *
	 */
	boolean addRole(final R role, final RoleType roleType)
		throws EInconsistentRBoxException;


	/**
	 * Remove the specified role from the RBox.
	 *
	 * @returns {@literal true} if the role was successfully removed or {@literal false} if the role did not exist.
	 *
	 */
	boolean removeRole(final R role);


	/**
	 * Change the type of {@literal role} to {@literal roleType}, if possible. Inconsistencies are not resolved
	 * automatically, but rather an exception is thrown.
	 *
	 */
	void setRoleType(final R role, final RoleType roleType)
		throws EInconsistentRBoxException;

	// <editor-fold defaultstate="collapsed" desc="role assertion management">

	/**
	 *
	 * Remove the equivalence assertion between the two roles.
	 * <p />
	 * Note that removing the assertion need not remove the equivalence relationship, as this may still be derived by
	 * other means.
	 * <p/>
	 *
	 * @param first  The first role.
	 * @param second The second role.
	 * <p/>
	 * @return {@literal true}, if the equivalence assertion between {@literal first} and {@literal second} was removed.
	 *            {@literal false} if the assertion could not be removed.
	 */
	boolean removeEquivalentRole(final R first, final R second);


	/**
	 * Add an equivalence assertion between {@literal first} and {@literal second}.
	 *
	 * @param first  The first role
	 * @param second The second role
	 * <p/>
	 * @return {@literal true} if the operation was successful.
	 * <p/>
	 * @throws EInconsistentRBoxException The addition would cause an inconsistency
	 */
	boolean addEquivalentRole(final R first, final R second)
		throws EInconsistentRBoxException;


	/**
	 *
	 * Remove the equivalence assertion between the two roles {@literal first} and {@literal second}.
	 * <p/>
	 * Note that removing the assertion need not remove the equivalence relationship, as this may still be derived by
	 * other means.
	 * <p/>
	 *
	 * @param first  The first role
	 * @param second The second role
	 *
	 * @return {@literal true} if the assertion was successfully removed and {@literal false} if the assertion did not
	 *            exist.
	 *
	 *
	 */
	boolean removeInverseRole(final R first, final R second);


	/**
	 * Add an inverse role assertion between {@literal first} and {@literal second}.
	 *
	 * @param first  The first role
	 * @param second The second role
	 *
	 * @return {@literal true} if the assertion was successfully added.
	 * <p/>
	 * @throws EInconsistentRBoxException The addition would cause an inconsistency
	 *
	 */
	boolean addInverseRole(final R first, final R second)
		throws EInconsistentRBoxException;


	/**
	 *
	 * Remove the subrole assertion between {@literal sup} and {@literal sub}.
	 * <p />
	 * Note that removing the assertion need not remove the subrole relationship, as this may still be derived by other
	 * means.
	 * <p/>
	 *
	 * @param sup The super role
	 * @param sub The sub role.
	 * <p/>
	 * @return {@literal true} if the assertion was sucessfully removed.
	 *
	 */
	boolean removeSubRole(final R sup, final R sub);


	/**
	 *
	 * Add a new subrole assertion between {@literal sup} and {@literal sub}
	 * <p />
	 * <p/>
	 *
	 * @param sup The super role
	 * @param sub The sub role
	 * <p/>
	 * @return {@literal true} if the subrole assertion was added successfully.
	 * <p/>
	 * @throws EInconsistentRBoxException The addition would cause an inconsistency
	 */
	boolean addSubRole(final R sup, final R sub)
		throws EInconsistentRBoxException;


	/**
	 * Set the specified property for a role.
	 *
	 * @param role     The role to modify
	 * @param property The property to set.
	 * <p/>
	 * @return True, if the role's properties were modified.
	 * <p/>
	 * @throws EInconsistentRBoxException The addition would cause an inconsistency
	 */
	boolean setRoleProperty(R role, RoleProperty property)
		throws EInconsistentRBoxException;


	/**
	 *
	 * Clear the specified property for a role.
	 * <p />
	 * Clearing an asserted role property does not affect the derived properties of a role.
	 * <p/>
	 *
	 * @param role     The role to modify
	 * @param property The property to clear.
	 * <p/>
	 * @return True, if the role's properties were modified.
	 *
	 */
	boolean clearRoleProperty(R role, RoleProperty property);


	/**
	 * XXX - role domain and range handling interface should be fixed
	 * currently, it is possible to add data ranges to object properties
	 * and class expressions to data properties.
	 * <p/>
	 * This only affects getRoleRanges(), but any change should also
	 * affect getRoleDomains() to maintain interface consistency.
	 *
	 */
	/**
	 * A multimap, defining the set of domains of a role. <p /> A domain of a role is an
	 * {@link IDLClassExpression} that the <em>source</em> end of a role link must be a subclass of.
	 *
	 * @return a {@link MultiMap} defining domain restrictions for roles.
	 */
	MultiMap<R, IDLClassExpression<I, L, K, R>> getRoleDomains();


	/**
	 * A multimap, defining the set of ranges of a role. <p /> A range of a role is an {@link IDLClassExpression}
	 * that the <em>target</em> end of a role link must be a subclass of.
	 *
	 * @return a {@link MultiMap} defining range restrictions for roles.
	 */
	MultiMap<R, IDLNodeTerm<I, L, K, R>> getRoleRanges();


	@Override
	IAssertedRBox<I, L, K, R> getImmutable();
//	@Override
//	IAssertedRBox<I, L, K, R> clone();
}

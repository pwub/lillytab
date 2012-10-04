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
package de.uniba.wiai.kinf.pw.projects.lillytab.tbox;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import java.util.Collection;
import org.apache.commons.collections15.MultiMap;


/**
 * 
 * 
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 **/
public interface IRBox<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
{

	/**
	 * <p> Transitively get the equivalent roles of the specified role. </p><p> The returned set will include roles that
	 * have been specified as both subroles or superroles, which implies equivalence. </p>
	 *
	 * @param role The role to find equivalent roles for.
	 * @return the set of roles that are the equivalent to {@literal role}.
	 */
	Collection<Role> getEquivalentRoles(Role role);

	/**
	 * <p> Transitively get the inverse roles of the specified roles. </p></p> The returned set will include equivalent
	 * roles of any inverse roles. </p>
	 *
	 * @param role The role to find inverses for.
	 * @return Return the set of roles that are the inverse of {@literal role}
	 *
	 */
	Collection<Role> getInverseRoles(Role role);

	/**
	 * <p> A multimap, defining the set of domains of a role. </p><p> A domain of a role is an {@link IDLClassExpression}
	 * that the <em>source</em> end of a role link must be a subclass of. </p>
	 *
	 * @return a {@link MultiMap} defining domain restrictions for roles.
	 */
	MultiMap<Role, IDLRestriction<Name, Klass, Role>> getRoleDomains();

	/**
	 * <p> Get a set of all properties for a given role. </p><p> It is not guarantueed that the returned set is
	 * modifiable. </p>
	 *
	 * @param role The role to retrieve properties for.
	 * @return The set of properties for a role.
	 */
	Collection<RoleProperty> getRoleProperties(Role role);

	/**
	 * <p> A multimap, defining the set of ranges of a role. </p><p> A range of a role is an {@link IDLClassExpression}
	 * that the <em>target</em> end of a role link must be a subclass of. </p>
	 *
	 * @return a {@link MultiMap} defining range restrictions for roles.
	 */
	MultiMap<Role, IDLRestriction<Name, Klass, Role>> getRoleRanges();

	/**
	 * Query the role type of {@literal role}.
	 * 
	 * @return The type of {@literal role}
	 * @throws NoSuchElementException The type of {@literal role} is not known.
	 */
	RoleType getRoleType(final Role role);

	/**
	 * Get a collection of all roles known to this RBox.
	 */
	Collection<Role> getRoles();

	/**
	 * Get a collection of all roles with the given property in this RBox.
	 */
	Collection<Role> getRoles(RoleProperty property);

	/**
	 * Get a collection of all roles with the given type in this RBox.
	 */
	Collection<Role> getRoles(RoleType type);

	/**
	 * <p> Transitively get the subroles of the specified role. </p><p> Note, that this will include the equivalent
	 * roles, including {@literal role} itself. </p>
	 *
	 * @param role The role to find the sub roles for.
	 * @return the set of roles that are the sub roles {@literal role}.
	 */
	Collection<Role> getSubRoles(Role role);

	/**
	 * <p> Transitively get the superroles of the specified role. </p><p> Note, that this will include the equivalent
	 * roles, including {@literal role} itself. </p>
	 *
	 * @param role The role to find the super roles for.
	 * @return the set of roles that are the super roles {@literal role}.
	 */
	Collection<Role> getSuperRoles(final Role role);

	/**
	 * @return The {@link ITBox} associated with this {@link IAssertedRBox}
	 */
	ITBox<Name, Klass, Role> getTBox();

	/**
	 * Check, if the specified role has a property.
	 *
	 * @param role The role to check.
	 * @param property The property to check
	 * @return {@literal true} if the specified role has {literal property}.
	 */
	boolean hasRoleProperty(Role role, RoleProperty property);

	/**
	 * Determine, if the specified role is of the specified type.
	 * @return {@literal true} if {@literal role} has the specified role type.
	 */
	boolean hasRoleType(final Role role, final RoleType roleType);

	/**
	 * Determine if the two roles are equivalent (either asserted or derived).
	 * @param first The {@literal first} role
	 * @param second The {@literal second} role
	 * @return {@literal true} if {@literal first} and {@literal second} are equivalent.
	 */
	boolean isEquivalentRole(final Role first, final Role second);

	/**
	 * Determine, if the specified role is of the specified type.
	 * 
	 * @param first The {@literal first} role
	 * @param second The {@literal second} role
	 * @return {@literal true} if {@literal first} is an inverse role of {@literal second}.
	 */
	boolean isInverseRole(final Role first, final Role second);
	
	/**
	 * Feature test: Inverse roles.
	 **/
	boolean hasInverseRoles();

	/**
	 * Determine if {@literal sub} is a subclass of {@literal sup}
	 *
	 * @param sub The presumed subclass.
	 * @param sup The presumed superclass.
	 * @return {@literal true} if {@literal sub} is subrole of {@literal sup}.
	 */
	boolean isSubRole(final Role sup, final Role sub);

	/**
	 * Determine if {@literal sup} is a superclass of {@literal sub}
	 *
	 * @param sup The presumed superclass.
	 * @param sub The presumed subclass.
	 * @return {@literal true} if {@literal sup} is a super class of {@literal sub}.
	 */
	boolean isSuperRole(final Role sub, final Role sup);

	IAssertedRBox<Name, Klass, Role> getAssertedRBox();
	
	String toString(String prefix);
	
}

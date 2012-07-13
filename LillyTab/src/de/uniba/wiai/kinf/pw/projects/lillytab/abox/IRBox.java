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
package de.uniba.wiai.kinf.pw.projects.lillytab.abox;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import java.util.Set;
import org.apache.commons.collections15.MultiMap;

/**
 * <p>
 * Role box containing information about various role parameters.
 * </p>
 * <p>
 * </p>
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 *
 * @param <Role> Role name type.
 */
public interface IRBox<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends MultiMap<Role, RoleProperty>
{
	/**
	 * @return The {@link ITBox} associated with this {@link IRBox}
	 */
	ITBox<Name, Klass, Role> getTBox();

	/**
	 * @param role The role to find inverses for.
	 * @return Return the set of roles that are the inverse of {@literal role}
	 **/
	Set<Role> getInverseRoles(Role role);

	/**
	 * @param role The role to find equivalent roles for.
	 * @return the set of roles that are the equivalent to {@literal role}.
	 *	
	 */
	Set<Role> getEquivalentRoles(Role role);

	/**
	 * @param role The role to find the super roles for.
	 * @return the set of roles that are the super roles {@literal role}.
	 */
	Set<Role> getSuperRoles(Role role);

	/**
	 * @param role The role to find the sub roles for.
	 * @return the set of roles that are the sub roles {@literal role}.
	 */
	Set<Role> getSubRoles(Role role);

	/**
	 * <p>
	 * A multimap, defining the set of domains of a role.
	 * </p><p>
	 * A domain of a role is an {@link IDLClassExpression} that
	 * the <em>source</em> end of a role link must be a subclass of.
	 * </p>
	 * 
	 * @return a {@link MultiMap} defining domain restrictions for roles.
	 */
	MultiMap<Role, IDLRestriction<Name, Klass, Role>> getRoleDomains();

	/**
	 * <p>
	 * A multimap, defining the set of ranges of a role.
	 * </p><p>
	 * A range of a role is an {@link IDLClassExpression} that
	 * the <em>target</em> end of a role link must be a subclass of.
	 * </p>
	 *
	 * @return a {@link MultiMap} defining range restrictions for roles.
	 */
	MultiMap<Role, IDLRestriction<Name, Klass, Role>> getRoleRanges();

	/**
	 * Set the specified property for a role.
	 *
	 * @param role The role to modify
	 * @param property The property to set.
	 * @return True, if the role's properties were modified.
	 **/
	boolean setRoleProperty(Role role, RoleProperty property);
	
	/**
	 * Clear the specified property for a role.
	 *
	 * @param role The role to modify
	 * @param property The property to clear.
	 * @return True, if the role's properties were modified.
	 **/
	boolean clearRoleProperty(Role role, RoleProperty property);

	/**
	 * Check, if the specified role has a property.
	 * @param role The role to check.
	 * @param property The property to check
	 * @return {@literal true} if the specified role has {literal property}.
	 */
	boolean hasRoleProperty(Role role, RoleProperty property);

	/**
	 * <p>
	 * Get a set of all properties for a given role.
	 * </p><p>
	 * It is not guarantueed that the returned set is modifiable.
	 * </p>
	 *
	 * @param role The role to retrieve properties for.
	 * @return The set of properties for a role.
	 */
	Set<RoleProperty> getRoleProperties(Role role);

	String toString(String prefix);

}

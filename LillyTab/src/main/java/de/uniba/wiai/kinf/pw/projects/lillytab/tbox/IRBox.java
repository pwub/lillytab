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

import de.dhke.projects.cutil.collections.immutable.IImmutable;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLNodeTerm;
import java.util.Collection;
import java.util.NoSuchElementException;


/**
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public interface IRBox<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends IImmutable<IRBox<I, L, K, R>> {

	/**
	 * Transitively get the equivalent roles of the specified role. <p />
	 * The returned set will include roles that have been specified as both subroles or superroles, which implies
	 * equivalence.
	 *
	 * @param role The role to find equivalent roles for.
	 * <p/>
	 * @return the set of roles that are the equivalent to {@literal role}.
	 */
	Collection<R> getEquivalentRoles(R role);

	/**
	 * Transitively get the inverse roles of the specified roles.
	 * The returned set will include equivalent roles of any inverse roles.
	 *
	 * @param role The role to find inverses for.
	 * <p/>
	 * @return Return the set of roles that are the inverse of {@literal role}
	 *
	 */
	Collection<R> getInverseRoles(R role);

	/**
	 * Get a set of all properties for a given role. <p /> It is not guarantueed that the returned set is
	 * modifiable.
	 *
	 * @param role The role to retrieve properties for.
	 * <p/>
	 * @return The set of properties for a role.
	 */
	Collection<RoleProperty> getRoleProperties(R role);

	/**
	 *
	 * Retrieve the role domains associated with {@literal role}.
	 * <p />
	 * The returned value may include inferred role ranges.
	 * <p/>
	 *
	 * @param role The role to obtain the domain assertions for.
	 * <p/>
	 * @return The role domains of {@literal role}.
	 */
	Collection<IDLClassExpression<I, L, K, R>> getRoleDomains(final R role);

	/**
	 *
	 * Retrieve the role ranges associated with {@literal role}.
	 * <p />
	 * The returned value may include inferred role ranges.
	 * <p/>
	 *
	 * @param role The role to obtain the domain assertions for.
	 * <p/>
	 * @return The role domains of {@literal role}.
	 */
	Collection<IDLNodeTerm<I, L, K, R>> getRoleRanges(final R role);

	/**
	 * Query the role type of {@literal role}.
	 *
	 * @return The type of {@literal role}
	 * <p/>
	 * @throws NoSuchElementException The type of {@literal role} is not known.
	 */
	RoleType getRoleType(final R role);

	/**
	 * Get a collection of all roles known to this RBox.
	 */
	Collection<R> getRoles();

	/**
	 * Get a collection of all roles with the given property in this RBox.
	 */
	Collection<R> getRoles(RoleProperty property);

	/**
	 * Get a collection of all roles with the given type in this RBox.
	 */
	Collection<R> getRoles(RoleType type);

	/**
	 * Transitively get the subroles of the specified role. <p /> Note, that this will include the equivalent
	 * roles, including {@literal role} itself.
	 *
	 * @param role The role to find the sub roles for.
	 * <p/>
	 * @return the set of roles that are the sub roles {@literal role}.
	 */
	Collection<R> getSubRoles(R role);

	/**
	 * Transitively get the superroles of the specified role. <p /> Note, that this will include the equivalent
	 * roles, including {@literal role} itself.
	 *
	 * @param role The role to find the super roles for.
	 * <p/>
	 * @return the set of roles that are the super roles {@literal role}.
	 */
	Collection<R> getSuperRoles(final R role);

	/**
	 * @return The {@link ITBox} associated with this {@link IAssertedRBox}
	 */
	ITBox<I, L, K, R> getTBox();

	/**
	 * Check, if the specified role has a property.
	 *
	 * @param role     The role to check.
	 * @param property The property to check
	 * <p/>
	 * @return {@literal true} if the specified role has {literal property}.
	 */
	boolean hasRoleProperty(R role, RoleProperty property);

	/**
	 * Determine, if the specified role is of the specified type.
	 *
	 * @return {@literal true} if {@literal role} has the specified role type.
	 */
	boolean hasRoleType(final R role, final RoleType roleType);

	/**
	 * Check, if the given role is known to the {@link IRBox}.
	 *
	 * @param role The role to check for.
	 * <p/>
	 * @return {@literal true} if {@literal role} is known to the current {@link IRBox}.
	 */
	boolean hasRole(final R role);

	/**
	 * Determine if the two roles are equivalent (either asserted or derived).
	 *
	 * @param first  The {@literal first} role
	 * @param second The {@literal second} role
	 * <p/>
	 * @return {@literal true} if {@literal first} and {@literal second} are equivalent.
	 */
	boolean isEquivalentRole(final R first, final R second);

	/**
	 * Determine, if the specified role is of the specified type.
	 *
	 * @param first  The {@literal first} role
	 * @param second The {@literal second} role
	 * <p/>
	 * @return {@literal true} if {@literal first} is an inverse role of {@literal second}.
	 */
	boolean isInverseRole(final R first, final R second);

	/**
	 * Feature test: Inverse roles.
	 *
	 */
	boolean hasInverseRoles();

	/**
	 * Determine if {@literal sub} is a subclass of {@literal sup}
	 *
	 * @param sub The presumed subclass.
	 * @param sup The presumed superclass.
	 * <p/>
	 * @return {@literal true} if {@literal sub} is subrole of {@literal sup}.
	 */
	boolean isSubRole(final R sup, final R sub);

	/**
	 * Determine if {@literal sup} is a superclass of {@literal sub}
	 *
	 * @param sup The presumed superclass.
	 * @param sub The presumed subclass.
	 * <p/>
	 * @return {@literal true} if {@literal sup} is a super class of {@literal sub}.
	 */
	boolean isSuperRole(final R sub, final R sup);

	IAssertedRBox<I, L, K, R> getAssertedRBox();

	String toString(String prefix);
//	IRBox<I, L, K, R> clone();
}

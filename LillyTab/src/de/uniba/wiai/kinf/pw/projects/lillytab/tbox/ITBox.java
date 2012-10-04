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

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ITermSet;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import java.util.Collection;
import java.util.Set;

/**
 * <p>
 * A TBox represents a set of global description that are always valid, i.e.
 * every instance is an instance of the global descriptions
 * </p><p>
 * The global axioms are split into an unfoldable and an non-unfoldable set.
 * The non-unfoldable set is available via {@link #getGlobalDescriptions()} and
 * needs to be added to the concept set of new ABox instance (see {@link IABoxNode}).
 * The unfoldable set needs to be added only if a given description (normally a {@link IDLClassReference})
 * is already present in the concept set of a particular {@link IABoxNode}.
 * </p><p>
 * This enables the implementation of lazy unfolding as detailed in [Horrocks97].
 * </p>
 * <pre>
 * PHDTHESIS{Horrocks97,
 *  ANNOTE = {AKA: Horrocks97b},
 * AUTHOR = {Ian Horrocks},
 * DATE-MODIFIED = {2008-06-21 18:19:52 +0100},
 * SCHOOL = {University of Manchester},
 * TITLE = {Optimising Tableaux Decision Procedures for Description
 *		  Logics},
 *
 * YEAR = 1997,
 * }
 * </pre>
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public interface ITBox<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends ITermSet<Name, Klass, Role>
{
	IAssertedRBox<Name, Klass, Role> getRBox();

	/**
	 * @return A set of global description axioms
	 */
	public Set<IDLRestriction<Name, Klass, Role>> getGlobalDescriptions();
	
	/**
	 * <p>
	 * The set of DIRECT unfoldings axioms for the specified description.
	 * The direct unfolding is not recursive and does not contain the initial
	 * description itself.
	 * </p><p>
	 * The caller is responsible for handling correct recursive unfolding.
	 * </p>
	 * @param unfoldee The description to unfold
	 * @return A set of terms that is the unfolding of {@literal unfoldee}.
	 */
	Collection<IDLRestriction<Name, Klass, Role>> getUnfolding(IDLRestriction<Name, Klass, Role> unfoldee);

	String toString(String prefix);
}

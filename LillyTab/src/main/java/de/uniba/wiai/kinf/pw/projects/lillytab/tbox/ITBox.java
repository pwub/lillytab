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
 **/
package de.uniba.wiai.kinf.pw.projects.lillytab.tbox;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ITermSet;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassReference;
import java.util.Collection;
import java.util.Set;


/**
 * 
 * A TBox represents a set of global description that are always valid, i.e. every instance is an instance of the global
 * descriptions
 * <p />
 * The global axioms are split into an unfoldable and an non-unfoldable set. The non-unfoldable set is available via
 * {@link #getGlobalDescriptions()} and needs to be added to the concept set of new ABox instance (see
 * {@link IABoxNode}). The unfoldable set needs to be added only if a given description (normally a
 * {@link IDLClassReference}) is already present in the concept set of a particular {@link IABoxNode}.
 * <p />
 * This enables the implementation of lazy unfolding as detailed in [Horrocks97].
 * 
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
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public interface ITBox<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends ITermSet<I, L, K, R>
{
	IRBox<I, L, K, R> getRBox();

	IAssertedRBox<I, L, K, R> getAssertedRBox();

	/**
	 * @return A set of global description axioms
	 */
	public Set<IDLClassExpression<I, L, K, R>> getGlobalDescriptions();

	/**
	 * 
	 * The set of DIRECT unfoldings axioms for the specified description. The direct unfolding is not recursive and does
	 * not contain the initial description itself.
	 * <p />
	 * The caller is responsible for handling correct recursive unfolding.
	 * 
	 *
	 * @param unfoldee The description to unfold
	 * @return A set of terms that is the unfolding of {@literal unfoldee}.
	 */
	Collection<IDLClassExpression<I, L, K, R>> getUnfolding(IDLClassExpression<I, L, K, R> unfoldee);

	String toString(String prefix);

	ITBox<I, L, K, R> clone();

	@Override
	ITBox<I, L, K, R> getImmutable();
}

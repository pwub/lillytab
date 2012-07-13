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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentABoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import java.util.Collection;

/**
 *
 * @param <Name> 
 * @param <Klass>
 * @param <Role>
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public interface IReasoner<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
{
	/**
	 * <p>
	 * Perform tableaux expansion on a copy of the supplied ABox, testing for consistency.
	 * The input ABox will not be modified.
	 * </p><p>
	 * Returns the list of all possible consistent completions of {@literal abox}.
	 * </p>
	 * @param abox The {@link ABox} to expand.
	 * @return A collection of consistent aboxes that are expansions of the input abox.
	 * @throws EInconsistentOntologyException No consistent ABox was found.
	 * @throws EReasonerException When a reasoner error occurred.
	 */
	public Collection<? extends IReasonerResult<Name, Klass, Role>> checkConsistency(final IABox<Name, Klass, Role> abox)
		throws EReasonerException, EInconsistentABoxException;

	/**
	 * <p>
	 * Check if the specified concept description is consistent with regard to the
	 * specified {@link IABox}, i.e. if there is a model of the specified concept.
	 * </p><p>
	 *
	 * @param abox An {@link IABox} representing background knowledge
	 * @param concept The concept whose consistency should be verified.
	 * @param stopAtFirstModel Stop consistency checking at first found model.
	 * @return A list of consistent completions of {@literal abox}.
	 * @throws EInconsistentOntologyException The concept is not consistent with regard to {@literal abox}
	 * @throws EReasonerException When a reasoner error occured.
	 **/
	public Collection<? extends IReasonerResult<Name, Klass, Role>> checkConsistency(final IABox<Name, Klass, Role> abox,
																		   final IDLRestriction<Name, Klass, Role> concept,
																		   final boolean stopAtFirstModel)
		throws EReasonerException, EInconsistentABoxException;

	/**
	 * <p>
	 * Perform tableaux expansion on a copy of the supplied ABox, testing for consistency.
	 * The input ABox will not be modified.
	 * </p><p>
	 * Returns the list of possible consistent completions of {@literal abox}.
	 * </p>
	 * @param abox The {@link ABox} to expand.
	 * @param stopAtFirstModel Stop at the first encountered model?
	 * @return A collection of consistent aboxes that are expansions of the input abox.
	 * @throws EInconsistentOntologyException No consistent ABox was found.
	 * @throws EReasonerException When a reasoner error occurred.
	 */
	public Collection<? extends IReasonerResult<Name, Klass, Role>> checkConsistency(final IABox<Name, Klass, Role> abox,
																		   final boolean stopAtFirstModel)
		throws EReasonerException, EInconsistentABoxException;

	/**
	 * Determine, if presumedSub is a subclass of presumedSuper with regard
	 * to the given ABox.
	 * @param abox The abox to use as background knowledge
	 * @param presumedSub The presumed subconcept
	 * @param presumedSuper The presumed super concept
	 * @return {@literal true} if presumedSub is a proper subconcept of presumedSuper.
	 * @throws EReasonerException A reasoner error has occured.
	 */
	public boolean isSubClassOf(
		final IABox<Name, Klass, Role> abox,
		final IDLRestriction<Name, Klass, Role> presumedSub,
		final IDLRestriction<Name, Klass, Role> presumedSuper)
		throws EReasonerException, EInconsistentABoxException;

	/**
	 * Determine if the specified ABox is consistent.
	 *
	 * @param abox The ABox to check for consistency
	 * @return {@literal true} if the ABox is consistent.
	 * @throws EReasonerException
	 */
	public boolean isConsistent(final IABox<Name, Klass, Role> abox)
		throws EReasonerException;

	/**
	 * Determine, if concept is consistent wit regard to the specified ABox.
	 * @param abox The abox to use as background knowledge.
	 * @param concept The concept to check for consistency.
	 * @return {@literal true} if there is a model for concept, {@literal false}, if no possible model for concept could be found.
	 * @throws EReasonerException A reasoner error has occured.
	 **/
	public boolean isConsistent(final IABox<Name, Klass, Role> abox, final IDLRestriction<Name, Klass, Role> concept)
		throws EReasonerException;

	/**
	 * <p>
	 * Determine if a concept is consistent by itself.
	 * </p>
	 * <p>
	 * Equivalent to calling {@link #isConsistent(de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox, de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression) }
	 * with an empty abox.
	 * </p>
	 * @param concept The concept to check for consistency.
	 * @param aboxFactory The ABox factory to use for creating the ABoxes to reason on.
	 * @return {@literal true}, if concept is consistent. {@literal false}, if concept has no model.
	 * @throws EReasonerException A reasoner error has occured.
	 */
	public boolean isConsistent(final IDLRestriction<Name, Klass, Role> concept,
								final IABoxFactory<Name, Klass, Role> aboxFactory)
		throws EReasonerException;

	/**
	 * Check, if {@literal desc} may be in the domain of {@literal role},
	 * i.e. if having {@literal desc} on the source side of {@literal role}
	 * is not contradictory.
	 *
	 * @param abox The ABox to check against.
	 * @param desc The class expression to check
	 * @param role The role
	 * @return {@literal true} if {@literal desc} lies in the domain of {@literal role}
	 * @throws EReasonerException
	 */
	public boolean isInDomain(final IABox<Name, Klass, Role> abox, final IDLRestriction<Name, Klass, Role> desc,
							  final Role role)
		throws EReasonerException, EInconsistentABoxException;

	/**
	 * Check, if {@literal desc} may be in the range of {@literal role},
	 * i.e. if having {@literal desc} on the target side of {@literal role}
	 * is not contradictory.
	 *
	 * @param abox THe ABox to check against.
	 * @param desc The class expression to check
	 * @param role The role
	 * @return {@literal true} if {@literal desc} lies in the range of {@literal role}
	 **/
	
	public boolean isInRange(final IABox<Name, Klass, Role> abox, final IDLRestriction<Name, Klass, Role> desc,
							 final Role role)
		throws EReasonerException, EInconsistentABoxException;



	/**
	 * Check, if the specified restrictions are disjoint.
	 *
	 * @param abox THe ABox to check against.
	 * @param desc1 The first class expression to check
	 * @param desc2 The second class expression to check
	 * @return {@literal true} if both descriptions are strictly disjoint.
	 **/
	public boolean isDisjoint(final IABox<Name, Klass, Role> abox, final IDLRestriction<Name, Klass, Role> desc1, final IDLRestriction<Name, Klass, Role> desc2)
		throws EReasonerException, EInconsistentABoxException;
}

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
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import java.util.Collection;


/**
 * A {@link INodeConsistencyChecker} is used to locally check the
 * consistency of the concept set of a single {@link IABoxNode} or concept set.
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 * @param <Name>
 * @param <Klass>
 * @param <Role>
 */
public interface INodeConsistencyChecker<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>

{
	/**
	 * <p>
	 * Check the consistency of the concept set of {@literal node}
	 * and raise an exception if an inconsistency is detected.
	 * </p>
	 * @param node The {@link ABoxNode} to check.
	 * @throws EInconsistentOntologyException If {@literal node} a together with {@literal extraDesc} is not locally consistent.
	 * @throws EReasonerException An internal error occured.
	 **/
	public void checkConsistency(final IABoxNode<Name, Klass, Role> node)
		throws EReasonerException, EInconsistentABoxException;

	/**
	 * <p>
	 * Check the consistency of the concept set of {@literal node}
	 * when merged with the extra descriptions from {@literal extraDesc}.
	 * and raise an exception if an inconsistency is detected.
	 * </p>
	 * @param node The {@link ABoxNode} to check.
	 * @param extraDesc Extra descriptions to add to the concept set of {@literal node}.
	 * @throws EInconsistentOntologyException If {@literal node} a together with {@literal extraDesc} is not locally consistent.
	 * @throws EReasonerException An internal error occured.
	 **/
	public void checkConsistency(final IABoxNode<Name, Klass, Role> node,
								 final Collection<? extends IDLTerm<Name, Klass, Role>> extraDesc)
		throws EReasonerException, EInconsistentABoxException ;

	/**
	 * <p>
	 * Assume that the concept set of {@literal node} is consistent
	 * and verify, if it is still consistent when the
	 * the extra descriptions from {@literal extraDesc} are added.
	 * Raise an exception if an inconsistency is detected.
	 * </p>
	 * @param node The {@link ABoxNode} to check.
	 * @param extraDesc Extra descriptions to add to the concept set of {@literal node}.
	 * @throws EInconsistentOntologyException If {@literal node} a together with {@literal extraDesc} is not locally consistent.
	 * @throws EReasonerException An internal error occured.
	 **/
	public void checkExtraConsistency(final IABoxNode<Name, Klass, Role> node,
								 final Collection<? extends IDLTerm<Name, Klass, Role>> extraDesc)
		throws EReasonerException, EInconsistentABoxException;

	/**
	 * Check the consistency of a single {@link IABoxNode}.
	 *
	 * @param abox The {@link IABox} the abox to use (required to obtain the {@link IDLTermFactory}.
	 * @param descs The concept set to check for inconsistencies.
	 * @return {@literal true}, if the provided concept set does not contain any local inconsistencies.
	 */
	public boolean isConsistent(final IABox<Name, Klass, Role> abox,
								final IABoxNode<Name, Klass, Role> node);


	/**
	 * <p>
	 * Check the consistency of the concept set of {@literal node}
	 * when merged with the extra descriptions from {@literal extraDesc}.
	 * </p>
	 *
	 * @param abox The {@link IABox} the abox to use (required to obtain the {@link IDLTermFactory}.
	 * @param node The node to check for consistency
	 * @param extraDesc Extra descriptions to add to the concept set of {@literal node}.
	 * @return {@literal true}, if the provided concept set does not contain any local inconsistencies.
	 */
	public boolean isConsistent(final IABox<Name, Klass, Role> abox,
								final IABoxNode<Name, Klass, Role> node,
								final Collection<? extends IDLTerm<Name, Klass, Role>> extraDesc
								);


	/**
	 * Check the inner consistency of a concept set.
	 *
	 * @param abox The {@link IABox} the abox to use (required to obtain the {@link IDLTermFactory}.
	 * @param descs The concept set to check for inconsistencies.
	 * @return {@literal true}, if the provided concept set does not contain any local inconsistencies.
	 */
	public boolean isConsistent(final IABox<Name, Klass, Role> abox,
								final Collection<? extends IDLTerm<Name, Klass, Role>> descs);


	/**
	 * Check the inner consistency of the union of two concept sets.
	 *
	 * @param abox The {@link IABox} the abox to use (required to obtain the {@link IDLTermFactory}.
	 * @param descs The concept set to check for inconsistencies.
	 * @param extraDescs The additional set of concepts to check for inconsistencies.
	 * @return {@literal true}, if the union of the provided concept sets does not contain any local inconsistencies.
	 */
	public boolean isConsistent(final IABox<Name, Klass, Role> abox,
								final Collection<? extends IDLTerm<Name, Klass, Role>> descs,
								final Collection<? extends IDLTerm<Name, Klass, Role>> extraDescs);

	/**
	 * <p>
	 * Assume that the concept set {@literal descs} is internally consistent
	 * and check, if the union between the concept sets {@literal desc} and {@literal extraDescs}
	 * is still consistent.
	 * </p><p>
	 * This is a faster version of {@link #isConsistent(de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox, java.util.Collection, java.util.Collection) }.
	 * </p>
	 *
	 * @param abox The {@link IABox} the abox to use (required to obtain the {@link IDLTermFactory}.
	 * @param descs The concept set to check for inconsistencies.
	 * @param extraDescs The additional set of concepts to check for inconsistencies.
	 * @return {@literal true}, if the union of the provided concept sets does not contain any local inconsistencies,
	 *   assuming that {@literal descs} was initially consistent.
	 */
	public boolean isExtraConsistent(final IABox<Name, Klass, Role> abox,
								final Collection<? extends IDLTerm<Name, Klass, Role>> descs,
								final Collection<? extends IDLTerm<Name, Klass, Role>> extraDescs);


}

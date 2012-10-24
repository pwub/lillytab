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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntryFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import java.util.Collection;


/**
 * A {@link INodeConsistencyChecker} is used to locally check the consistency of the concept set of a single
 * {@link IABoxNode} or concept set.
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 * @param <Name> The type for nominals and values
 * @param <Klass> The type for DL classes
 * @param <Role> The type for properties (roles)
 */
public interface INodeConsistencyChecker<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
{
	/**
	 * Check the consistency of a single {@link IABoxNode}.
	 *
	 * @param abox The {@link IABox} the abox to use (required to obtain the {@link IDLTermFactory}.
	 * @param node The node to check for consistency
	 * @return An {@link ConsistencyInfo} describing the result of the consistency check.
	 */
	public ConsistencyInfo<Name, Klass, Role> isConsistent(final IABox<Name, Klass, Role> abox,
														   final IABoxNode<Name, Klass, Role> node);
	
	/**
	 * <p> 
	 * Check the consistency of a single {@link IABoxNode}.
	 * </p><p>
	 * This obtains the {@link IABox} via {@link IABoxNode#getABox() }. If you already
	 * have the ABox, use {@link #isConsistent(de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox, de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode, java.util.Collection)}
	 * which is slightly faster.
	 * </p>
	 *
	 * @param node The node to check for consistency
	 * @return An {@link ConsistencyInfo} describing the result of the consistency check.
	 */
	public ConsistencyInfo<Name, Klass, Role> isConsistent(final IABoxNode<Name, Klass, Role> node);

	/**
	 * <p> 
	 * Check the consistency of the concept set of {@literal node} when merged with the extra descriptions from
	 * {@literal extraDesc}. 
	 * </p>
	 *
	 * @param abox The {@link IABox} the abox to use (required to obtain the {@link IDLTermFactory}.
	 * @param node The node to check for consistency
	 * @param extraDesc Extra descriptions to add to the concept set of {@literal node}.
	 * @return An {@link ConsistencyInfo} describing the result of the consistency check.
	 */
	public ConsistencyInfo<Name, Klass, Role> isConsistent(final IABox<Name, Klass, Role> abox,
														   final IABoxNode<Name, Klass, Role> node,
														   final Collection<? extends IDLTerm<Name, Klass, Role>> extraDesc);
	
	/**
	 * <p>
	 * Assume that the provided {@literal node} is already consistent and verify,
	 * that the termset stays consistent when adding {@literal extraDescs} to it.
	 * </p><p>
	 * This obtains the {@link IABox} via {@link IABoxNode#getABox() }. If you already
	 * have the ABox, use {@link #isConsistent(de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox, de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode, java.util.Collection)}
	 * which is slightly faster.
	 * </p>
	 *
	 * @param abox The {@link IABox} the abox to use (required to obtain the {@link IDLTermFactory}.
	 * @param node The node to check for consistency
	 * @param extraDescs The additional set of concepts to check for inconsistencies.
	 * @return An {@link ConsistencyInfo} describing the result of the consistency check.
	 */
	public ConsistencyInfo<Name, Klass, Role> isExtraConsistent(
		final IABox<Name, Klass, Role> abox,
		final IABoxNode<Name, Klass, Role> node,
		final Collection<? extends IDLTerm<Name, Klass, Role>> extraDescs);	
	
	/**
	 * <p>
	 * Assume that the provided {@literal node} is already consistent and verify,
	 * that the termset stays consistent when adding {@literal extraDescs} to it.
	 * </p><p>
	 * This obtains the {@link IABox} via {@link IABoxNode#getABox() }. If you already
	 * have the ABox, use {@link #isExtraConsistent(de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox, de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode, java.util.Collection) }
	 * which is slightly faster.
	 * </p>
	 *
	 * @param node The node to check for consistency
	 * @param extraDescs The additional set of concepts to check for inconsistencies.
	 * @return An {@link ConsistencyInfo} describing the result of the consistency check.
	 */
	public ConsistencyInfo<Name, Klass, Role> isExtraConsistent(
		final IABoxNode<Name, Klass, Role> node,
		final Collection<? extends IDLTerm<Name, Klass, Role>> extraDescs);	

	/**
	 * <p>
	 * Check the consistency of the concept set of {@literal node} when merged with the extra descriptions from
	 * {@literal extraDesc}. 
	 * </p><p>
	 * This obtains the {@link IABox} via {@link IABoxNode#getABox() }. If you already
	 * have the ABox, use {@link #isConsistent(de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox, de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode, java.util.Collection)}
	 * which is slightly faster.
	 * </p>
	 * 
	 * @param node The node to check for consistency
	 * @param extraDescs The additional set of concepts to check for inconsistencies.
	 * @return An {@link ConsistencyInfo} describing the result of the consistency check.
	 */
	public ConsistencyInfo<Name, Klass, Role> isConsistent(final IABoxNode<Name, Klass, Role> node,
														   final Collection<? extends IDLTerm<Name, Klass, Role>> extraDescs);
	
	
	/**
	 * Check the inner consistency of a concept set.
	 *
	 * @param termFactory The {@link IDLTermFactory} to use.
	 * @param termEntryFactory The {@link TermEntryFactory} to use.
	 * @param descs The concept set to check for inconsistencies.
	 */
	public ConsistencyInfo<Name, Klass, Role> isConsistent(final IDLTermFactory<Name, Klass, Role> termFactory,
														   final TermEntryFactory<Name, Klass, Role> termEntryFactory,
														   final Collection<? extends IDLTerm<Name, Klass, Role>> descs);

	/**
	 * <p> Check the inner consistency of the union of two concept sets. </p><p> The therms within the returned
	 * {@link ConsistencyInfo} will be tagged with a null {@link NodeID}. </p>
	 *
	 * @param termFactory The {@link IDLTermFactory} to use.
	 * @param termEntryFactory The {@link TermEntryFactory} to use.
	 * @param extraDescs The additional set of concepts to check for inconsistencies.
	 * @return An {@link ConsistencyInfo} describing the result of the consistency check.
	 */
	public ConsistencyInfo<Name, Klass, Role> isConsistent(final IDLTermFactory<Name, Klass, Role> termFactory,
														   final TermEntryFactory<Name, Klass, Role> termEntryFactory,
														   final Collection<? extends IDLTerm<Name, Klass, Role>> descs,
														   final Collection<? extends IDLTerm<Name, Klass, Role>> extraDescs);

	/**
	 * <p> Assume that the concept set {@literal descs} is internally consistent and check, if the union between the
	 * concept sets {@literal desc} and {@literal extraDescs} is still consistent. </p><p> This is a faster version of
	 * {@link #isConsistent(de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox, java.util.Collection, java.util.Collection)}
	 * </p><p> The therms within the returned {@link ConsistencyInfo} will be tagged with a null {@link NodeID}. </p>
	 *
	 * @param termFactory The {@link IDLTermFactory} to use.
	 * @param termEntryFactory The {@link TermEntryFactory} to use.
	 * @param descs The concept set to check for inconsistencies.
	 * @param extraDescs The additional set of concepts to check for inconsistencies.
	 * @return An {@link ConsistencyInfo} describing the result of the consistency check.
	 */
	public ConsistencyInfo<Name, Klass, Role> isExtraConsistent(
		final IDLTermFactory<Name, Klass, Role> termFactory,
		final TermEntryFactory<Name, Klass, Role> termEntryFactory,
		final Collection<? extends IDLTerm<Name, Klass, Role>> descs,
		final Collection<? extends IDLTerm<Name, Klass, Role>> extraDescs);
}

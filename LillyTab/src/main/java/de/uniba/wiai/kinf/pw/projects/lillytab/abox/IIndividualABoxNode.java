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
package de.uniba.wiai.kinf.pw.projects.lillytab.abox;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import java.util.SortedSet;

/**
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 * 
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public interface IIndividualABoxNode<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends IABoxNode<I, L, K, R> {

	@Override
	SortedSet<I> getNames();


	@Override
	I getPrimaryName();


	/**
	 *
	 * Add the specified description to the current node.
	 * <p />
	 * When the description list contains nominals, the current node may have to be joined with other nodes in the ABox.
	 * The method thus returns an appropriate {@link NodeMergeInfo} object describing any eventual merge operation.
	 * <p />
	 * After a merge operation, only the target node of the returned {@link NodeMergeInfo} is a valid node of the
	 * current {@link IABox}. All other nodes have been merged away.
	 *
	 *
	 * @param desc A description to add to the current node.
	 * @return A {@link NodeMergeInfo} indicating the progress of the operation.
	 * @throws ENodeMergeException A node merge was required but failed.
	 */
	NodeMergeInfo<I, L, K, R> addClassTerm(final IDLClassExpression<I, L, K, R> desc)
		throws ENodeMergeException;


	/**
	 *
	 * Add the specified descriptions to the current node.
	 * <p />
	 * When the description list contains nominals, the current node may have to be joined with other nodes in the ABox.
	 * The method thus returns an appropriate {@link NodeMergeInfo} object describing any eventual merge operation.
	 * <p />
	 * After a merge operation, only the target node of the returned {@link NodeMergeInfo} is a valid node of the
	 * current {@link IABox}. All other nodes have been merged away.
	 *
	 *
	 * @param descs Sequence of descriptions to add to the current node.
	 * @return A {@link NodeMergeInfo} indicating the ID of the target node containing the unfoldings and information,
	 * if the target node was modified.
	 * @throws ENodeMergeException A node merge was required but failed.
	 */
	NodeMergeInfo<I, L, K, R> addClassTerm(
		final Iterable<? extends IDLClassExpression<I, L, K, R>> descs)
		throws ENodeMergeException;
}

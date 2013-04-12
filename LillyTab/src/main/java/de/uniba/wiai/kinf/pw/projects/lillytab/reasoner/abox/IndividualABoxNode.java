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

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IDependencyMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IIndividualABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.ITBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLIndividualReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.TermUtil;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class IndividualABoxNode<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends ABoxNode<I, I, L, K, R>
	implements IIndividualABoxNode<I, L, K, R> {

	public IndividualABoxNode(
		ABox<I, L, K, R> newABox,
		ABoxNode<I, I, L, K, R> klonee)
	{
		super(newABox, klonee);
	}


	public IndividualABoxNode(
		ABox<I, L, K, R> abox, int id)
	{
		super(abox, id, false);
	}


	@Override
	public NodeMergeInfo<I, L, K, R> addClassTerm(final IDLClassExpression<I, L, K, R> term)
		throws ENodeMergeException
	{
		final ABox<I, L, K, R> abox = getABox();
		final IDependencyMap<I, L, K, R> depMap = abox.getDependencyMap();
		final ITBox<I, L, K, R> tbox = abox.getTBox();
		final IDLClassExpression<I, L, K, R> nnfTerm = TermUtil.toNNF(term, abox.getCommon().getTermFactory());

		final SortedMap<IDLClassExpression<I, L, K, R>, IDLClassExpression<I, L, K, R>> addQueue = new TreeMap<>();

		addQueue.put(nnfTerm, null);
		final NodeMergeInfo<I, L, K, R> mergeInfo = new NodeMergeInfo<>(this, false);
		while (!addQueue.isEmpty()) {
			final IDLClassExpression<I, L, K, R> addTerm = addQueue.firstKey();
			final IDLClassExpression<I, L, K, R> parent = addQueue.remove(addTerm);

			IndividualABoxNode<I, L, K, R> currentNode = (IndividualABoxNode<I, L, K, R>) mergeInfo.
				getCurrentNode();
			assert abox.contains(mergeInfo.getCurrentNode());
			if (!currentNode.getTerms().contains(addTerm)) {
				if (addTerm instanceof IDLIndividualReference) {
					final IDLIndividualReference<I, L, K, R> indRef = (IDLIndividualReference<I, L, K, R>) addTerm;
					final I newInd = indRef.getIndividual();
					final IABoxNode<I, L, K, R> otherNode = abox.getIndividualNode(newInd);
					if (otherNode != null) {
						final NodeMergeInfo<I, L, K, R> newMergeInfo = abox.mergeNodes(otherNode, currentNode);
						mergeInfo.append(newMergeInfo);
						assert mergeInfo.getCurrentNode() instanceof IIndividualABoxNode;
						currentNode = (IndividualABoxNode<I, L, K, R>) mergeInfo.getCurrentNode();
						assert abox.contains(currentNode);
					}
				}
				if (currentNode._terms.add(addTerm)) {
					mergeInfo.setModified(currentNode);
				}
				for (IDLClassExpression<I, L, K, R> unfoldee : tbox.getUnfolding(addTerm)) {
					if (!currentNode.getTerms().contains(unfoldee)) {
						addQueue.put(unfoldee, addTerm);
					}
				}
				if ((parent != null) && (!depMap.containsKey(currentNode, term))) {
					depMap.addParent(currentNode, term, currentNode, parent);
				}
			}
		}

		assert abox.contains(mergeInfo.getCurrentNode());
		return mergeInfo;
	}


	@Override
	public NodeMergeInfo<I, L, K, R> addClassTerm(
		final Iterable<? extends IDLClassExpression<I, L, K, R>> descs)
		throws ENodeMergeException
	{
		final NodeMergeInfo<I, L, K, R> mergeInfo = new NodeMergeInfo<>(this, false);
		/*
		 * the local abox may go away
		 */
		IndividualABoxNode<I, L, K, R> currentNode = this;
		for (IDLClassExpression<I, L, K, R> desc : descs) {
			final NodeMergeInfo<I, L, K, R> unfoldResult = currentNode.addClassTerm(desc);
			mergeInfo.append(unfoldResult);
			currentNode = (IndividualABoxNode<I, L, K, R>) mergeInfo.getCurrentNode();
		}

		return mergeInfo;
	}

	/// </editor-fold>

	@Override
	public IndividualABoxNode<I, L, K, R> clone(
		IABox<I, L, K, R> newABox)
	{
		assert newABox != null;
		assert newABox instanceof ABox;
		final ABox<I, L, K, R> nABox = (ABox<I, L, K, R>) newABox;
		return new IndividualABoxNode<>(nABox, this);
	}
}

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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IDatatypeABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLLiteralReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataRange;

/**
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class LiteralABoxNode<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends ABoxNode<L, I, L, K, R>
	implements IDatatypeABoxNode<I, L, K, R> {

	public LiteralABoxNode(
		ABox<I, L, K, R> newABox,
		ABoxNode<L, I, L, K, R> klonee)
	{
		super(newABox, klonee);
	}


	public LiteralABoxNode(
		ABox<I, L, K, R> abox, int id)
	{
		super(abox, id, true);
	}


	@Override
	public LiteralABoxNode<I, L, K, R> clone(
		IABox<I, L, K, R> newABox)
	{
		assert newABox != null;
		assert newABox instanceof ABox;
		final ABox<I, L, K, R> nABox = (ABox<I, L, K, R>) newABox;
		return new LiteralABoxNode<>(nABox, this);
	}


	@Override
	public NodeMergeInfo<I, L, K, R> addDataTerm(
		IDLDataRange<I, L, K, R> desc)
		throws ENodeMergeException
	{
		/* the current ABox may go away when this node is merged */
		final IABox<I, L, K, R> abox = getABox();
		final NodeMergeInfo<I, L, K, R> mergeInfo = new NodeMergeInfo<>(this, false);
		if ((!getTerms().contains(desc)) && (desc instanceof IDLLiteralReference)) {
			final IDLLiteralReference<I, L, K, R> litRef = (IDLLiteralReference<I, L, K, R>) desc;
			final L newLit = litRef.getLiteral();
			final IABoxNode<I, L, K, R> otherNode = abox.getDatatypeNode(newLit);
			if (otherNode != null) {
				final NodeMergeInfo<I, L, K, R> newMergeInfo = abox.mergeNodes(otherNode, this);
				mergeInfo.append(newMergeInfo);
				return newMergeInfo;
			}
		}
		assert mergeInfo.getCurrentNode() instanceof LiteralABoxNode;
		final LiteralABoxNode<I, L, K, R> currentNode = (LiteralABoxNode<I, L, K, R>)mergeInfo.getCurrentNode();
		if (currentNode._terms.add(desc)) {
			mergeInfo.setModified(currentNode);
		}
		return mergeInfo;
	}
}

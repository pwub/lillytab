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

import de.dhke.projects.cutil.collections.aspect.AspectSortedSet;
import de.dhke.projects.cutil.collections.aspect.CollectionEvent;
import de.dhke.projects.cutil.collections.aspect.CollectionItemEvent;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import java.util.Collection;
import java.util.TreeSet;

/**
 *
 * @param <I> The type for nominals and values
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ABoxNodeSet<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> 
	extends AspectSortedSet<IABoxNode<I, L, K, R>> {

	public ABoxNodeSet(final ABox<I, L, K, R> abox)
	{
		super(new TreeSet<IABoxNode<I, L, K, R>>(), abox);
	}


	public ABoxNodeSet(final ABox<I, L, K, R> newABox, final ABoxNodeSet<I, L, K, R> klonee)
	{
		this(newABox);
		for (IABoxNode<I, L, K, R> node : klonee) {
			getDecoratee().add(node.clone(newABox));
		}
	}


	@SuppressWarnings("unchecked")
	public ABox<I, L, K, R> getABox()
	{
		return (ABox<I, L, K, R>) getSender();
	}


	@Override
	public void notifyAfterCollectionCleared(
		CollectionEvent<IABoxNode<I, L, K, R>, Collection<IABoxNode<I, L, K, R>>> e)
	{
		getABox()._nodeMap.clear();
	}


	@Override
	public void notifyBeforeElementAdded(
		CollectionItemEvent<IABoxNode<I, L, K, R>, Collection<IABoxNode<I, L, K, R>>> e)
	{
		super.notifyBeforeElementAdded(e);
		/**
		 * Check node map consistency before add.
		 */
		final IABoxNode<I, L, K, R> node = e.getItem();
		if (getABox()._nodeMap.containsKey(node.getNodeID())) {
			throw new IllegalArgumentException(
				"Node with id " + node.getNodeID().toString() + " already in node map.");
		} else {
			for (Object name : node.getNames()) {
				if (getABox()._nodeMap.containsKey(name)) {
					throw new IllegalArgumentException("Node with name " + name + " already in node map.");
				}
			}
		}
	}


	@Override
	public void notifyAfterElementAdded(
		CollectionItemEvent<IABoxNode<I, L, K, R>, Collection<IABoxNode<I, L, K, R>>> e)
	{
		assert e.getItem() instanceof IABoxNode;
		final ABoxNode<?, I, L, K, R> node = (ABoxNode<?, I, L, K, R>) e.getItem();
		node.setABox(getABox());
		getABox()._nodeMap.put(node.getNodeID(), node);
		for (Object name : node.getNames()) {
			getABox()._nodeMap.put(name, node);
		}
		super.notifyAfterElementAdded(e);
	}


	@Override
	public void notifyBeforeElementRemoved(
		CollectionItemEvent<IABoxNode<I, L, K, R>, Collection<IABoxNode<I, L, K, R>>> e)
	{
		super.notifyBeforeElementRemoved(e);
		e.getItem().getRABox().getAssertedPredecessors().clear();
		e.getItem().getRABox().getAssertedSuccessors().clear();
	}


	@Override
	public void notifyAfterElementRemoved(
		CollectionItemEvent<IABoxNode<I, L, K, R>, Collection<IABoxNode<I, L, K, R>>> e)
	{
		assert e.getItem() instanceof IABoxNode;
		final ABoxNode<?, I, L, K, R> node = (ABoxNode<?, I, L, K, R>) e.getItem();
		getABox()._nodeMap.remove(node.getNodeID());
		for (Object name : node.getNames()) {
			/* PARANOIA: remove mapping only if it pointed to the current node */
			if (getABox()._nodeMap.get(name).equals(node)) {
				getABox()._nodeMap.remove(name);
			}
		}
		node.setABox(null);
		super.notifyAfterElementRemoved(e);
	}


	public ABoxNodeSet<I, L, K, R> clone(final ABox<I, L, K, R> newABox)
	{
		return new ABoxNodeSet<>(newABox, this);
	}


	/**
	 * 
	 * Remove the {@literal node} from the node set, but to not remove its links to other nodes.
	 * <p />
	 * The backlink from the node to the Abox is kept. The node map and set are updated, however.
	 * 
	 *
	 * @param node
	 * @return
	 *
	 *
	 */
	protected boolean removeNoUnlink(final ABoxNode<?, I, L, K, R> node)
	{
		if (getDecoratee().remove(node)) {
			/* remove only the names from the node map */
			for (Object name : node.getNames()) {
				if (getABox()._nodeMap.get(name).equals(node)) {
					getABox()._nodeMap.remove(name);
				}
			}
			return true;
		} else {
			return false;
		}
	}


	/**
	 * undo the changes made by {@link #removeNoUnlink(de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxNode)
	 * }
	 * and
	 *
	 * 
	 * Companion to {@link #removeNoUnlink(de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxNode))
	 * 
	 *
	 * @param node
	 * @return
	 */
	protected boolean addNoUnlink(final ABoxNode<?, I, L, K, R> node)
	{
		if (getDecoratee().add(node)) {
			final ABox<I, L, K, R> abox = getABox();
			/* add the (possibly different) names back to the node map */
			for (Object name : node.getNames()) {
				abox._nodeMap.put(name, node);
			}
			return true;
		} else {
			return false;
		}
	}
}

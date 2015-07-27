/**
 * (c) 2009-2014 Peter Wullinger
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
package de.dhke.projects.cutil.collections.tree;

import java.util.Iterator;
import java.util.List;


/**
 * An implementation of a decision tree.
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public interface IDecisionTree<Data>
{
	Node<Data> getRoot();

	List<? extends Node<Data>> getChildren(final Node<Data> parent);

	boolean hasChildren(final Node<Data> parent);

	Node<Data> getParent(final Node<Data> child);

	boolean hasData(final Node<Data> node);

	Data getData(final Node<Data> node);

	/**
	 * 
	 * Create a new leaf node as a sibling of {@literal sibling}.
	 * <p />
	 * The node that {@literal data} is now attached to is returned.
	 * This node reference is only guaranteed to be valid until
	 * the next modification of the decision tree. The parent node
	 * reference remains valid through {@link #fork(null, data)}-operations.
	 * 
	 *
	 * @param sibling The sibling of the new node
	 * @param data Datum for the leaf node. May not be {@literal null}.
	 * @return The leaf node {@literal data} is now attached to.
	 */
	Node<Data> fork(final Node<Data> sibling, final Data data);

	/**
	 * 
	 * Create a new leaf node as a sibling of {@literal sibling}
	 * making sure that the new node and its {@literal sibling} are
	 * the only child nodes of their parent.
	 * <p />
	 * This differs from {@link #fork(final Node, final Object)}
	 * in that it will create a new parent node, if necessary to
	 * ensure that {@literal sibling} and the new node reside on their
	 * own branch.
	 * 
	 * @param sibling The sibling of the new node
	 * @param data Datum for the leaf node. May not be {@literal null}.
	 * @return The leaf node {@literal data} is now attached to.
	 **/
	Node<Data> branch(final Node<Data> sibling, final Data data);


	/**
	 * 
	 * Remove the decision at {@literal node}.
	 * <p />
	 * This is equivalent to calling {@link #remove(de.dhke.projects.cutil.collections.tree.IDecisionTree.Node, boolean) }
	 * with {@literal pullChildren = true}.
	 * 
	 *
	 * @param node The node to remove.
	 */
	void remove(final Node<Data> node);

	/**
	 * 
	 * Remove the decision at {@literal node}.
	 * <p />
	 * If {@literal pullChildren} is {@literal false}, all children of {@literal node} are simply removed.
	 * If {@literal pullChildren} is passed as {@literal true} and {@literal node} has children,
	 * the children are pulled down and attached to the parent, instead.
	 * 
	 * @param node The node to remove.
	 * @param pullChildren {@literal true} indicates that all children of {@literal node} should also be removed.
	 **/
	void remove(final Node<Data> node, boolean pullChildren);

	List<? extends Node<Data>> getPath(final Node<Data> node);

	Iterator<? extends Node<Data>> iterator();


	public interface Node<Data>
		extends de.dhke.projects.cutil.collections.Node<Data>
	{
		Node<Data> getParent();

		List<? extends Node<Data>> getChildren();

		boolean hasChildren();

		boolean hasData();

		@Override
		Data getData();

		void remove();

		List<? extends Node<Data>> getPath();

		Node<Data> fork(final Data data);
		Node<Data> branch(final Data data);
	}
}

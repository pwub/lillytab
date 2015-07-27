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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;
import org.apache.commons.collections15.list.SetUniqueList;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class DecisionTree<Data>
	implements IDecisionTree<Data> {

	private DTNode _root;


	public DecisionTree()
	{
		_root = new DTNode();


	}


	@Override
	public Node<Data> getRoot()
	{
		return _root;


	}


	@Override
	public List<? extends Node<Data>> getChildren(final Node<Data> parent)
	{
		return parent.getChildren();


	}


	@Override
	public boolean hasChildren(Node<Data> parent)
	{
		return parent.hasChildren();


	}


	@Override
	public Node<Data> getParent(Node<Data> child)
	{
		return child.getParent();


	}


	@Override
	public boolean hasData(Node<Data> node)
	{
		return node.hasData();


	}


	@Override
	public Data getData(Node<Data> node)
	{
		return node.getData();
	}


	@Override
	public Node<Data> fork(final Node<Data> sibling, Data data)
	{
		// assert checkDecisionTree(this);
		/* TODO: merge common code */
		assert sibling instanceof Node;
		final DTNode siblingNode = (DTNode) sibling;

		if (sibling.hasData()) {
			/* sibling has data, we really need to fork */
			final DTNode parentNode = siblingNode.getParent();
			if (parentNode != null) {
				final DTNode newNode = new DTNode(parentNode, data);
				parentNode.getChildren().add(newNode);
				// assert checkDecisionTree(this);
				return newNode;
			} else {
				/**
				 * parent node does not exist, i.e. our sibling is the root node
				 *
				 * - create a new parent node and make it the new root node
				 * - create a new node and attach it to the parent
				 * - attach our sibling to the new parent.
				 *
				 * This way, the sibling reference remains intact and forking with
				 * the same sibling should work as expected.
				 *
				 */
				assert parentNode == null;
				final DTNode newParent = new DTNode();
				newParent.getChildren().add(siblingNode);
				siblingNode.setParent(newParent);
				final DTNode newNode = new DTNode(newParent, data);
				newParent.getChildren().add(newNode);
				assert !newParent.hasData();
				assert newParent.getChildren().size() == 2;
				_root = newParent;
				// assert checkDecisionTree(this);
				return newNode;
			}
		} else {
			/* sibling has no data, attach and return */
			siblingNode.setData(data);
			// assert checkDecisionTree(this);
			return siblingNode;
		}
	}


	@Override
	public Node<Data> branch(final Node<Data> sibling, final Data data)
	{
		assert checkDecisionTree(this);
		/* TODO: merge common code */
		assert sibling instanceof Node;
		final DTNode siblingNode = (DTNode) sibling;
		if (sibling.hasData()) {
			/* sibling has data, we really need to fork */
			final DTNode parentNode = siblingNode.getParent();
			if (parentNode != null) {
				/* parent node exists. In this case it already has more than a single child */
				assert parentNode.getChildren().size() > 1;

				/* create new parent and attach new node and sibling */
				final DTNode newParent = new DTNode();
				parentNode.getChildren().remove(siblingNode);
				newParent.getChildren().add(siblingNode);
				siblingNode.setParent(newParent);
				final DTNode newNode = new DTNode(newParent, data);
				newParent.getChildren().add(newNode);
				/* insert new parent node into old parent */
				parentNode.getChildren().add(newParent);
				newParent.setParent(parentNode);
				assert checkDecisionTree(this);
				return newNode;
			} else {
				assert checkDecisionTree(this);
				/**
				 * sibling has data and parent node does not exist, i.e. our sibling is
				 * the root node
				 *
				 * - create a new parent node and make it the new root node
				 * - create a new node and attach it to the parent
				 * - attach our sibling to the new parent.
				 *
				 * This way, the sibling reference remains intact and forking with
				 * the same sibling should work as expected.
				 *
				 */
				assert parentNode == null;
				final DTNode newParent = new DTNode();
				newParent.getChildren().add(siblingNode);
				siblingNode.setParent(newParent);

				final DTNode newNode = new DTNode(newParent, data);
				newParent.getChildren().add(newNode);

				assert !newParent.hasData();
				assert newParent.hasChildren();
				assert newParent.getChildren().size() == 2;
				assert newParent.getChildren().contains(siblingNode);
				assert newParent.getChildren().contains(newNode);
				_root = newParent;
				// assert checkDecisionTree(this);
				return newNode;
			}
		} else {
			/* sibling has no data, attach and return */
			siblingNode.setData(data);
			// assert checkDecisionTree(this);
			return siblingNode;
		}

	}


	@Override
	public void remove(final Node<Data> node, final boolean pullChildren)
	{
		// assert checkDecisionTree(this);
		removeReal(node, pullChildren);
		// assert checkDecisionTree(this);
	}


	@Override
	public void remove(final Node<Data> node)
	{
		remove(node, true);
	}


	/**
	 * Remove the specified node from the decision tree.
	 * <p/>
	 * @param removeNode   The node to remove.
	 * @param pullChildren if {@literal true}, any children of {@literal removeNode} will be
	 *                        attached to {@literal removeNode}'s parent.
	 */
	private void removeReal(final Node<Data> removeNode, final boolean pullChildren)
	{
		if (removeNode.getParent() != null) {
			/* our parent node is not null, i.e. we are not the root */
			final DTNode rmNode = (DTNode) removeNode;
			final DTNode parent = rmNode.getParent();

			/* remove target node from tree */
			assert parent.getChildren().contains(rmNode);

			boolean removed = parent.getChildren().remove(rmNode);
			assert removed;
			rmNode.setParent(null);

			if (pullChildren && removeNode.hasChildren()) {
				/* pull down children to parent, if specified */
				for (Node<Data> childNode : removeNode.getChildren()) {
					parent.getChildren().add((DTNode) childNode);
					((DTNode) childNode).setParent(parent);
				}
			}

			if (parent.getChildren().size() == 1) {
				/**
				 * if the parent now has only one child (otherChild) left, we need to collapse
				 *
				 * (1) keep the parent
				 * (2) remove the other child
				 * (3) - if the other child is a leaf, move its data down to the parent, making it a leaf
				 * (4) - if the other child is not a leaf, pull the children down.
				 *
				 */
				final DTNode otherChild = parent.getChildren().iterator().next();

				if (otherChild.hasChildren()) {
					/* remove non-leaf node, pull down its children */
					removeReal(otherChild, true);
				} else {
					removed = parent.getChildren().remove(otherChild);
					assert removed;
					parent.setData(otherChild.getData());
					otherChild.setParent(null);

					assert !parent.hasChildren();
				}
			}
		} else if (_root.hasData()) {
			_root.setData(null);
		} else
			throw new IllegalArgumentException("Cannot remove empty root node");
	}


	@Override
	public List<? extends Node<Data>> getPath(final Node<Data> node)
	{
		final List<Node<Data>> path = new ArrayList<>();

		for (Node<Data> currentNode = node; currentNode
			!= null; currentNode = currentNode.getParent())
			path.add(currentNode);

		Collections.reverse(path);

		return path;
	}


	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(getRoot().toString());

		return sb.toString();
	}


	@Override
	public Iterator<? extends Node<Data>> iterator()
	{
		return new Itr();

	}


	public static <Data> boolean checkDecisionTree(final IDecisionTree<Data> tree, final Node<Data> node)
	{
		if (node.hasChildren()) {
			if (node.getChildren().size() < 2) {
				throw new IllegalArgumentException("node " + node + " does not have at least two children");
			} else {
				for (Node<Data> child : node.getChildren()) {
					if (child.getParent() != node)
						throw new IllegalArgumentException(
							"node " + child + " has incorrect parent pointer, should be " + node + ", but is " + child.getParent());
					checkDecisionTree(tree, child);
				}
			}
		} else if (!node.hasData())
			throw new IllegalArgumentException("node " + node + " is not a leaf but has no data");
		return true;
	}


	public static <Data> boolean checkDecisionTree(final IDecisionTree<Data> tree)
	{
		final Node<Data> root = tree.getRoot();
		if (root.hasChildren())
			checkDecisionTree(tree, root);
		return true;
	}

	/// <editor-fold defaultstate="collapsed" desc="Iterator">
	class Itr
		implements Iterator<Node<Data>> {

		private Node<Data> _next;
		private final Stack<Iterator<? extends Node<Data>>> iterStack = new Stack<>();


		private Itr()
		{
			Iterator<? extends Node<Data>> iter = getRoot().getChildren().iterator();
			if (iter.hasNext()) {
				iterStack.push(iter);
				final Node<Data> current = iter.next();
				while (current.hasChildren()) {
					iter = current.getChildren().iterator();
					iterStack.push(iter);
				}
				_next = current;
			} else if (getRoot().hasData()) {
				_next = getRoot();
			} else
				_next = null;
		}


		@Override
		public boolean hasNext()
		{
			return _next != null;
		}


		private void advance()
		{
			if (_next != null) {
				_next = null;
				while (_next == null) {
					/* find the topmost iterator on the stack that still has a successor */
					while ((!iterStack.isEmpty()) && (!iterStack.peek().hasNext()))
						iterStack.pop();

					if (iterStack.isEmpty()) {
						/* we did not find an iterator on the stack that still has */
						_next = null;
						return;
					} else {
						Iterator<? extends Node<Data>> iter = iterStack.peek();
						assert iter.hasNext();
						Node<Data> current = iter.next();
						while (current.hasChildren()) {
							iter = current.getChildren().iterator();
							iterStack.push(iter);
							current = iter.next();
						}
						_next = current;
					}
				}
			} else
				_next = null;
		}


		@Override
		public Node<Data> next()
		{
			if (_next != null) {
				final Node<Data> next = _next;
				advance();
				return next;
			} else
				throw new NoSuchElementException("End of DecisionTree reached");
		}


		@Override
		public void remove()
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="DTNode">
	class DTNode
		implements Node<Data> {

		private DTNode _parent;
		private Data _data;
		private List<DTNode> _children;


		DTNode()
		{
			_parent = null;
			_children = SetUniqueList.decorate(new ArrayList<DTNode>());
		}


		DTNode(final DTNode newParent)
		{
			_parent = newParent;
			_children = SetUniqueList.decorate(new ArrayList<DTNode>());
		}


		DTNode(final DTNode newParent, final Data data)
		{
			_parent = newParent;
			_data = data;
			_children = SetUniqueList.decorate(new ArrayList<DTNode>());
		}


		@Override
		public DTNode getParent()
		{
			return _parent;
		}


		protected void setParent(final DTNode newParent)
		{
			_parent = newParent;
		}


		@Override
		public List<DTNode> getChildren()
		{
			return _children;
		}


		@Override
		public boolean hasChildren()
		{
			return !_children.isEmpty();
		}


		@Override
		public boolean hasData()
		{
			return _data != null;
		}


		@Override
		public Data getData()
		{
			return _data;
		}


		protected void setData(final Data newData)
		{
			if (_data != newData) {
				_data = newData;
				if (_parent != null) {
					/* make sure, parent properly rehashes the child entry for this node */
					final DTNode parent = _parent;
					parent.getChildren().remove(this);
					parent.getChildren().add(this);
				}
			}
		}


		@Override
		public void remove()
		{
			DecisionTree.this.remove(this);
		}


		@Override
		public List<? extends Node<Data>> getPath()
		{
			return DecisionTree.this.getPath(this);
		}


		@Override
		public boolean equals(final Object obj)
		{
			if (this == obj)
				return true;
			if (obj instanceof Node) {
				final Node<?> other = (Node<?>) obj;
				if (hasData())
					return getData().equals(other.getData());
				else
					return !other.hasData();
			} else
				return false;
		}


		@Override
		public int hashCode()
		{
			if (_data == null)
				return System.identityHashCode(this);
			else
				return _data.hashCode();
		}


		@Override
		public String toString()
		{
			if (hasChildren()) {
				final StringBuilder sb = new StringBuilder();
				sb.append("<");
				sb.append(getChildren().toString());
				sb.append(">");
				return sb.toString();
			} else if (hasData())
				return getData().toString();
			else
				return "(null)";
		}


		@Override
		public IDecisionTree.Node<Data> fork(Data data)
		{
			return DecisionTree.this.fork(this, data);
		}


		@Override
		public IDecisionTree.Node<Data> branch(Data data)
		{
			return DecisionTree.this.branch(this, data);
		}
	}
	/// </editor-fold>
}

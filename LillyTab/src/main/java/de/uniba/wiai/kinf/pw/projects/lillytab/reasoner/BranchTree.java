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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner;

import de.dhke.projects.cutil.collections.tree.DecisionTree;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public class BranchTree<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> 
	extends DecisionTree<Branch<I, L, K, R>> {

	private static final long serialVersionUID = 3820019753050796191L;


	public String getBranchID(final Node<Branch<I, L, K, R>> node)
	{
		final String separator = ".";
		final StringBuilder branchID = new StringBuilder();
		final List<? extends Node<Branch<I, L, K, R>>> branchPath = getPath(node);
		Collections.reverse(branchPath);

		final Node<Branch<I, L, K, R>> root = getRoot();
		final Iterator<? extends Node<Branch<I, L, K, R>>> iter = branchPath.iterator();

		Node<Branch<I, L, K, R>> parent = iter.next();
		assert parent == root;
		while (iter.hasNext()) {
			final Node<Branch<I, L, K, R>> currentNode = iter.next();
			assert currentNode.getParent() == parent;

			final int branchIndex = parent.getChildren().indexOf(currentNode);
			assert branchIndex >= 0;
			if (branchID.length() > 0) {
				branchID.append(separator);
			}

			branchID.append(String.valueOf(branchIndex));

			parent = currentNode;
		}

		return branchID.toString();
	}


	public Node<Branch<I, L, K, R>> firstLeaf()
	{
		Node<Branch<I, L, K, R>> node = getRoot();
		if (node.hasChildren()) {
			while (node.hasChildren()) {
				node = node.getChildren().get(0);
			}
			assert node.hasData();
			return node;
		} else {
			if (node.hasData()) {
				return node;
			} else {
				return null;
			}
		}
	}
}

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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.blocking;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import org.apache.commons.collections15.SetUtils;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class DoubleBlockingStrategy<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> 
	extends SubsetBlockingStrategy<I, L, K, R> {

	/**
	 *  Determine, if this node is a potential blocker node for {@literal target}. <p /> We are a potential
	 * blocker, if the target's concept set is a subset of ours and if we are (strictly) in front of target (according
	 * to the natural order of nodes). Note that we may not block ourselves. 
	 *
	 * @param blocker
	 * @param target A potential blocker.
	 * @return {@literal true} if {@literal target} is a potential blocking node for {@literal this} node.
	 */
	@Override
	protected boolean isPotentialBlocker(final IABoxNode<I, L, K, R> blocker,
										 final IABoxNode<I, L, K, R> target)
	{
		/**
		 * This node is potentially blocking {@literal target}, if target's set of concept terms is a superset of ours
		 * i.e. if target is less specific that the current node.#
		 *
		 * The size check is a very simple performance trick, that helps for some ontologies (
		 *
		 */
		if ((blocker.compareTo(target) < 0) && (blocker.getTerms().size() == target.getTerms().size())
			&& SetUtils.isEqualSet(blocker.getTerms(), target.getTerms())) {
			/* blocking candidate, now search for predecessors that are also blockers 
			 *
			 * XXX - this may not correct as implemented.
			 * TODO: Re-read double blocking strategies.
			 */
			for (IABoxNode<I, L, K, R> pred : blocker.getRABox().getPredecessorNodes()) {
				if ((pred.compareTo(blocker) < 0) && (blocker.getTerms().size() == pred.getTerms().size())
					&& SetUtils.isEqualSet(blocker.getTerms(), pred.getTerms())) {
					return true;
				}
			}
		}
		return false;
	}
}

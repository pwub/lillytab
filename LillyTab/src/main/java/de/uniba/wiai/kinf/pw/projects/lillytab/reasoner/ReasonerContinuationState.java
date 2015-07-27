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

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public enum ReasonerContinuationState {

	/**
	 * an inconsistency was detected an reasoning cannot continue.
	 *
	 */
	INCONSISTENT,
	/**
	 * no modification performed, reasoning can continue as normal on the current branch.
	 *
	 */
	CONTINUE,
	/**
	 * At least one node's term set was modified, the node queue of the current branch needs to be rechecked.
	 *
	 */
	RECHECK_NODE,
	/**
	 * The branch tree was modified and the branch tree needs to be reevaluated.
	 *
	 */
	RECHECK_BRANCH,
	/**
	 * The current branch has been satisfied, i.e. reasoning on the current branch is complete. Reasoning can continue
	 * as normal.
	 *
	 */
	DONE
}

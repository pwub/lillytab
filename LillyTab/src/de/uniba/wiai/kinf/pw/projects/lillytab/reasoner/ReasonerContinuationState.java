/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner;


/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public enum ReasonerContinuationState
{

	/**
	 * an inconsistency was detected an reasoning cannot continue.
	 **/
	INCONSISTENT,
	/**
	 * no modification performed, reasoning can continue as normal on the current branch.
	 **/
	CONTINUE,
	/**
	 * At least one node's term set was modified, the node queue of the current branch needs to be rechecked.
	 **/
	RECHECK_NODE, 
	/**
	 * The branch tree was modified and the branch tree needs to be reevaluated.
	 **/
	RECHECK_BRANCH, 
	/**
	 * The current branch has been satisfied, i.e. reasoning on the current branch is complete. Reasoning can continue
	 * as normal.
	 **/
	DONE
}

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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentABoxException;
import de.dhke.projects.cutil.Pair;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IAssertedRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleProperty;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLNegation;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datatype.IDLDatatypeExpression;
import de.dhke.projects.lutil.LoggingClass;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class NodeConsistencyChecker<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends LoggingClass
	implements INodeConsistencyChecker<Name, Klass, Role> {
	/*
	 * perform tracing
	 */

	private final boolean _trace;


	public NodeConsistencyChecker(final boolean trace)
	{
		_trace = false;
	}


	public NodeConsistencyChecker()
	{
		this(false);
	}


	private static <T> ArrayList<T> makePairArrayList(final T one, final T two)
	{
		final ArrayList<T> list = new ArrayList<T>();
		list.add(one);
		list.add(two);
		return list;
	}


	/**
	 * Determine if the union of both sets of descriptions contains a direct contradiction and return the first pair of
	 * contradicting terms.
	 *
	 * @param abox The ABox whose IDLTermFactory is to be used.
	 * @param descs A set of descriptions
	 * @param extraDescs An extra set of descriptions.
	 * @return {@literal false} if a direct contradiction was found in the union set.
	 */
	// @SuppressWarnings("unchecked")
	public Collection<IDLTerm<Name, Klass, Role>> getInconsistentTerms(final IABox<Name, Klass, Role> abox,
																	   final Collection<? extends IDLTerm<Name, Klass, Role>> descs,
																	   final Collection<? extends IDLTerm<Name, Klass, Role>> extraDescs)
	{
		assert abox != null;
		assert abox.getDLTermFactory() != null;
		final IDLRestriction<Name, Klass, Role> nothing = abox.getDLTermFactory().getDLNothing();
		// final IDLClassExpression<Name, Klass, Role> thing = abox.getDLTermFactory().getDLThing();

		if (descs.contains(nothing)) {
			if (_trace) {
				logFiner("Inconsistent concept set: %s", descs);
				logFinest("\t'%s' in set", nothing);
			}
			return Collections.<IDLTerm<Name, Klass, Role>>singleton(nothing);
		} else if ((extraDescs != null) && extraDescs.contains(nothing)) {
			if (_trace) {
				logFiner("Inconsistent concept set: %s", descs);
				logFinest("\t'%s' in set", nothing);
			}
			return Collections.<IDLTerm<Name, Klass, Role>>singleton(nothing);
		} else {
			for (IDLTerm<Name, Klass, Role> desc : descs) {
				if (desc instanceof IDLNegation) {
					/*
					 * check for negated concept clash
					 */
					IDLNegation<Name, Klass, Role> neg = (IDLNegation<Name, Klass, Role>) desc;
					if (descs.contains(neg.getTerm())) {
						/*
						 * descs internal inconsistency
						 */
						if (_trace) {
							logFiner("Inconsistent concept set: %s", descs);
							logFinest("\t'%s' and its negation '%s' in set", neg.getTerm(), neg);
						}
						return makePairArrayList((IDLTerm<Name, Klass, Role>) neg.getTerm(), neg);
					} else if ((extraDescs != null) && extraDescs.contains(neg.getTerm())) {
						/*
						 * desc->extraDescs cross inconsistency
						 */
						if (_trace) {
							logFiner("Inconsistent concept sets: %s, %s", descs, extraDescs);
							logFinest("\t'%s' and its negation '%s' present.", neg.getTerm(), neg);
						}
						return makePairArrayList((IDLTerm<Name, Klass, Role>) neg.getTerm(), neg);
					}
				}
			}

			if (extraDescs != null) {
				for (IDLTerm<Name, Klass, Role> xDesc : extraDescs) {
					if (xDesc instanceof IDLNegation) {
						IDLNegation<Name, Klass, Role> neg = (IDLNegation<Name, Klass, Role>) xDesc;
						if (descs.contains(neg.getTerm())) {
							if (_trace) {
								logFiner("Inconsistent concept sets: %s, %s", extraDescs, descs);
								logFinest("\t'%s' and its negation '%s' present.", neg.getTerm(), neg);
							}
							return makePairArrayList((IDLTerm<Name, Klass, Role>) neg.getTerm(), neg);
						} else if (extraDescs.contains(neg.getTerm())) {
							/*
							 * extraDescs internal inconsistency
							 */
							if (_trace) {
								logFiner("Inconsistent concept set: %s", extraDescs);
								logFinest("\t'%s' and its negation '%s' present.", neg.getTerm(), neg);
							}
							return makePairArrayList((IDLTerm<Name, Klass, Role>) neg.getTerm(), neg);
						}
					}
				}
			}
		}
		/*
		 * no clash found
		 */
		return null;
	}


	private void checkFunctionalRoles(final IABoxNode<Name, Klass, Role> node)
		throws EReasonerException, EInconsistencyException
	{
		final IABox<Name, Klass, Role> abox = node.getABox();
		final IAssertedRBox<Name, Klass, Role> rbox = abox.getTBox().getRBox();

		for (Role role : node.getLinkMap().getOutgoingRoles()) {
			if (rbox.hasRoleProperty(role, RoleProperty.FUNCTIONAL)) {
				Iterator<NodeID> succIDiter = node.getLinkMap().getSuccessors(role).iterator();
				if (succIDiter.hasNext() && (succIDiter.next() != null) && succIDiter.hasNext()) {
					throw new ERoleCardinalityException(node, role,
														String.format("More than one successor for functional role '%s'",
																	  role));
				}
			}
		}
		for (Role role : node.getLinkMap().getIncomingRoles()) {
			if (rbox.hasRoleProperty(role, RoleProperty.INVERSE_FUNCTIONAL)) {
				final Iterator<NodeID> predIDIter = node.getLinkMap().getSuccessors(role).iterator();
				if (predIDIter.hasNext() && (predIDIter.next() != null) && predIDIter.hasNext()) {
					throw new ERoleCardinalityException(node, role,
														String.format(
						"More than one predecessor for inverse functional role '%s'", role));
				}
			}
		}
	}


	private boolean hasProperFunctionalRoles(final IABoxNode<Name, Klass, Role> node)
	{
		final IABox<Name, Klass, Role> abox = node.getABox();
		final IAssertedRBox<Name, Klass, Role> rbox = abox.getTBox().getRBox();

		for (Pair<Role, NodeID> outgoing : node.getLinkMap().getSuccessorPairs()) {
			final Role role = outgoing.getFirst();
			if (rbox.hasRoleProperty(role, RoleProperty.FUNCTIONAL)) {
				Iterator<NodeID> succIDiter = node.getLinkMap().getSuccessors(role).iterator();
				if (succIDiter.hasNext() && (succIDiter.next() != null) && succIDiter.hasNext()) {
					return false;
				}
			}
		}
		for (Pair<Role, NodeID> incoming : node.getLinkMap().getPredecessorPairs()) {
			final Role role = incoming.getFirst();
			if (rbox.hasRoleProperty(role, RoleProperty.INVERSE_FUNCTIONAL)) {
				final Iterator<NodeID> predIDIter = node.getLinkMap().getSuccessors(role).iterator();
				if (predIDIter.hasNext() && (predIDIter.next() != null) && predIDIter.hasNext()) {
					return false;
				}
			}
		}
		return true;
	}


	private void checkDatatypeNode(final IABoxNode<Name, Klass, Role> node)
		throws EReasonerException, EInconsistentABoxException
	{
		if (node.isDatatypeNode()) {
			/* This check is superfluous in the current implementation, as datatype nodes can programmatically never have successors */
			//if (! node.getLinkMap().getAssertedSuccessors().isEmpty())
			//	throw new EInconsistentABoxNodeException(node, "Datatype node cannot have successors");

			for (IDLTerm<Name, Klass, Role> term : node.getTerms().subSet(DLTermOrder.DL_DATATYPE_EXPRESSION)) {
				assert term instanceof IDLDatatypeExpression;
				final IDLDatatypeExpression<Name, Klass, Role> dtExpression = (IDLDatatypeExpression<Name, Klass, Role>) term;
				if (!node.isAnonymous()) {
					for (Name individual : node.getNames())
						if (!dtExpression.isValidValue(individual))
							throw new EInconsistentABoxNodeException(node,
																	 "'" + individual + " is not a valid value for datatype expression " + dtExpression);
					if (!dtExpression.isCompatibleValue(node.getNames()))
						throw new EInconsistentABoxNodeException(node,
																 "Incompatible datatype values: " + node.getNames());
				}
			}
		}
	}


	private boolean isProperDatatypeNode(final IABoxNode<Name, Klass, Role> node)
	{
		if (node.isDatatypeNode()) {
			/* This check is superfluous in the current implementation, as datatype nodes can programmatically never have successors */
			//if (! node.getLinkMap().getAssertedSuccessors().isEmpty())
			//	return false;

			for (IDLTerm<Name, Klass, Role> term : node.getTerms().subSet(DLTermOrder.DL_DATATYPE_EXPRESSION)) {
				assert term instanceof IDLDatatypeExpression;
				final IDLDatatypeExpression<Name, Klass, Role> dtExpression = (IDLDatatypeExpression<Name, Klass, Role>) term;
				for (Name individual : node.getNames())
					if (!dtExpression.isValidValue(individual))
						return false;
				if (!dtExpression.isCompatibleValue(node.getNames()))
					return false;
			}

		}
		return true;
	}


	public void checkConsistency(IABoxNode<Name, Klass, Role> node) throws EReasonerException, EInconsistencyException
	{
		checkConsistency(node, null);
	}


	public void checkConsistency(IABoxNode<Name, Klass, Role> node,
								 Collection<? extends IDLTerm<Name, Klass, Role>> extraDesc) throws EReasonerException, EInconsistencyException
	{
		final IABox<Name, Klass, Role> abox = node.getABox();
		final Collection<? extends IDLTerm<Name, Klass, Role>> inconsistent = getInconsistentTerms(abox, node.getTerms(),
																								   null);
		if (inconsistent != null)
			throw new ETermClashException(node, inconsistent, "Clashing terms");
		checkFunctionalRoles(node);
		checkDatatypeNode(node);
	}


	public void checkExtraConsistency(IABoxNode<Name, Klass, Role> node,
									  Collection<? extends IDLTerm<Name, Klass, Role>> extraDesc) throws EReasonerException, EInconsistencyException
	{
		final IABox<Name, Klass, Role> abox = node.getABox();
		final Collection<? extends IDLTerm<Name, Klass, Role>> inconsistent = getInconsistentTerms(abox, node.getTerms(),
																								   null);
		if (inconsistent != null)
			throw new ETermClashException(node, inconsistent, "Clashing terms");
		checkFunctionalRoles(node);
		checkDatatypeNode(node);
	}


	public boolean isConsistent(IABox<Name, Klass, Role> abox,
								Collection<? extends IDLTerm<Name, Klass, Role>> descs,
								Collection<? extends IDLTerm<Name, Klass, Role>> extraDescs)
	{
		final Collection<? extends IDLTerm<Name, Klass, Role>> inconsistent = getInconsistentTerms(abox, descs,
																								   extraDescs);
		return (inconsistent == null);
	}


	public boolean isExtraConsistent(IABox<Name, Klass, Role> abox,
									 Collection<? extends IDLTerm<Name, Klass, Role>> descs,
									 Collection<? extends IDLTerm<Name, Klass, Role>> extraDescs)
	{
		/*
		 * TODO: speedup
		 */
		final Collection<? extends IDLTerm<Name, Klass, Role>> inconsistent = getInconsistentTerms(abox, descs,
																								   extraDescs);
		return (inconsistent == null);
	}


	public boolean isConsistent(IABox<Name, Klass, Role> abox,
								IABoxNode<Name, Klass, Role> node)
	{
		return isConsistent(abox, node.getTerms()) && hasProperFunctionalRoles(node) && isProperDatatypeNode(node);

	}


	public boolean isConsistent(IABox<Name, Klass, Role> abox,
								IABoxNode<Name, Klass, Role> node,
								Collection<? extends IDLTerm<Name, Klass, Role>> extraDesc)
	{
		return isConsistent(abox, node.getTerms(), extraDesc) && hasProperFunctionalRoles(node) && isProperDatatypeNode(
			node);
	}


	public boolean isConsistent(IABox<Name, Klass, Role> abox,
								Collection<? extends IDLTerm<Name, Klass, Role>> descs)
	{
		return isConsistent(abox, descs);
	}
}

/**
 * (c) 2009-2013 Otto-Friedrich-University Bamberg
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
 *
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner;

import de.dhke.projects.cutil.collections.frozen.FrozenSet;
import de.dhke.projects.cutil.collections.iterator.ChainIterable;
import de.dhke.projects.cutil.collections.set.Flat3Set;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IDatatypeABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IRABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntryFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.ITBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleProperty;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleType;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLLiteralReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataNegation;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDatatypeExpression;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

/**
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class NodeConsistencyChecker<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	implements INodeConsistencyChecker<I, L, K, R> {
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


	public Set<Set<TermEntry<I, L, K, R>>> getInconsistentTermEntries(
		final IDLTermFactory<I, L, K, R> termFactory, final TermEntryFactory<I, L, K, R> termEntryFactory, final Collection<? extends IDLTerm<I, L, K, R>> descs, final Collection<? extends IDLTerm<I, L, K, R>> extraDescs)
	{
		final Set<Set<TermEntry<I, L, K, R>>> clashTermSets = new Flat3Set<>();

		final Collection<Iterable<? extends IDLTerm<I, L, K, R>>> termColl = new ArrayList<>();
		termColl.add(descs);
		if (extraDescs != null) {
			termColl.add(extraDescs);
		}
		for (IDLTerm<I, L, K, R> term : ChainIterable.decorate(termColl)) {
			if (term instanceof IDLClassExpression) {
				final IDLClassExpression<I, L, K, R> desc = (IDLClassExpression<I, L, K, R>) term;
				final IDLClassExpression<I, L, K, R> negDesc = termFactory.getDLObjectNegation(desc);
				if (descs.contains(negDesc)) {
					clashTermSets.add(makeTermEntryPairSet(termEntryFactory, null, desc, negDesc));
					return clashTermSets;
				}
				if ((extraDescs != null) && extraDescs.contains(negDesc)) {
					clashTermSets.add(makeTermEntryPairSet(termEntryFactory, null, desc, negDesc));
					return clashTermSets;
				}
			}
		}
		return clashTermSets;
	}


	public Set<Set<TermEntry<I, L, K, R>>> getInconsistentExtraTermEntries(
		final IDLTermFactory<I, L, K, R> termFactory, final TermEntryFactory<I, L, K, R> termEntryFactory, final Collection<? extends IDLTerm<I, L, K, R>> descs, final Collection<? extends IDLTerm<I, L, K, R>> extraDescs)
	{
		final Set<Set<TermEntry<I, L, K, R>>> clashTermSets = new Flat3Set<>();

		if (extraDescs.contains(termFactory.getDLNothing())) {
			clashTermSets.add(makeTermEntryPairSet(termEntryFactory, null, termFactory.getDLNothing(),
												   termFactory.getDLThing()));
		}

		/* it is sufficient to iterate over extraDescs, because descs are assumed to be consistent */
		for (IDLTerm<I, L, K, R> term : extraDescs) {
			if (term instanceof IDLClassExpression) {
				final IDLClassExpression<I, L, K, R> desc = (IDLClassExpression<I, L, K, R>) term;
				final IDLClassExpression<I, L, K, R> negDesc = termFactory.getDLObjectNegation(desc);
				if (descs.contains(negDesc)) {
					clashTermSets.add(makeTermEntryPairSet(termEntryFactory, null, desc, negDesc));
					return clashTermSets;
				}
				if ((extraDescs != null) && extraDescs.contains(negDesc)) {
					clashTermSets.add(makeTermEntryPairSet(termEntryFactory, null, desc, negDesc));
					return clashTermSets;
				}
			}
		}
		return clashTermSets;
	}


	public Set<Set<TermEntry<I, L, K, R>>> getInconsistentTermEntries(
		final IABox<I, L, K, R> abox, final IABoxNode<I, L, K, R> node, final Collection<? extends IDLTerm<I, L, K, R>> extraDescs)
	{
		final IDLTermFactory<I, L, K, R> termFactory = abox.getDLTermFactory();
		final TermEntryFactory<I, L, K, R> termEntryFactory = abox.getTermEntryFactory();
		return getInconsistentTermEntries(termFactory, termEntryFactory, abox.getTBox(), node, extraDescs);
	}


	public Set<Set<TermEntry<I, L, K, R>>> getInconsistentTermEntries(
		final IABoxNode<I, L, K, R> node, final Collection<? extends IDLTerm<I, L, K, R>> extraDescs)
	{
		return getInconsistentTermEntries(node.getABox(), node, extraDescs);
	}


	public Set<Set<TermEntry<I, L, K, R>>> getInconsistentTermEntries(
		final IDLTermFactory<I, L, K, R> termFactory, final TermEntryFactory<I, L, K, R> termEntryFactory, final ITBox<I, L, K, R> tbox, final IABoxNode<I, L, K, R> node, final Collection<? extends IDLTerm<I, L, K, R>> extraDescs)
	{
		final Set<Set<TermEntry<I, L, K, R>>> clashTermSets = new Flat3Set<>();
		final Collection<Iterable<? extends IDLTerm<I, L, K, R>>> termColl = new ArrayList<>();

		if (node.getTerms().contains(termFactory.getDLNothing())) {
			clashTermSets.add(makeTermEntryPairSet(termEntryFactory, node.getNodeID(), termFactory.getDLNothing(),
												   termFactory.getDLThing()));
			return clashTermSets;
		}

		if ((extraDescs != null) && extraDescs.contains(termFactory.getDLNothing())) {
			clashTermSets.add(makeTermEntryPairSet(termEntryFactory, node.getNodeID(), termFactory.getDLNothing(),
												   termFactory.getDLThing()));
			return clashTermSets;
		}

		termColl.add(node.getTerms());
		if (extraDescs != null) {
			for (IDLTerm<I, L, K, R> desc : extraDescs) {
				if (desc instanceof IDLClassExpression) {
					termColl.add(tbox.getUnfolding((IDLClassExpression<I, L, K, R>) desc));
				}
			}
		}
		final SortedSet<IDLTerm<I, L, K, R>> nodeNegations = node.getTerms().subSet(DLTermOrder.DL_OBJECT_NEGATION);

		for (IDLTerm<I, L, K, R> term : ChainIterable.decorate(termColl)) {
			if (term instanceof IDLClassExpression) {
				final IDLClassExpression<I, L, K, R> desc = (IDLClassExpression<I, L, K, R>) term;
				final IDLClassExpression<I, L, K, R> negDesc = termFactory.getDLObjectNegation(desc);
				if (nodeNegations.contains(negDesc)) {
					clashTermSets.add(makeTermEntryPairSet(termEntryFactory, node.getNodeID(), desc, negDesc));
				}
				if ((extraDescs != null) && extraDescs.contains(negDesc)) {
					clashTermSets.add(makeTermEntryPairSet(termEntryFactory, node.getNodeID(), desc, negDesc));
				}
			}
		}
		return clashTermSets;
	}


	public Set<Set<TermEntry<I, L, K, R>>> getInconsistentExtraTermEntries(
		final IABox<I, L, K, R> abox, final IABoxNode<I, L, K, R> node, final Collection<? extends IDLTerm<I, L, K, R>> extraDescs)
	{
		return getInconsistentExtraTermEntries(abox.getDLTermFactory(), abox.getTermEntryFactory(), abox.getTBox(), node,
											   extraDescs);
	}


	public Set<Set<TermEntry<I, L, K, R>>> getInconsistentExtraTermEntries(
		final IDLTermFactory<I, L, K, R> termFactory, final TermEntryFactory<I, L, K, R> termEntryFactory, final ITBox<I, L, K, R> tbox, final IABoxNode<I, L, K, R> node, final Collection<? extends IDLTerm<I, L, K, R>> extraDescs)
	{
		final Set<Set<TermEntry<I, L, K, R>>> clashTermSets = new Flat3Set<>();
		final Collection<Iterable<? extends IDLTerm<I, L, K, R>>> termColl = new ArrayList<>();
		if (extraDescs != null) {
			if (extraDescs.contains(termFactory.getDLNothing())) {
				clashTermSets.add(makeTermEntryPairSet(termEntryFactory, node.getNodeID(), termFactory.getDLNothing(),
													   termFactory.getDLThing()));
				return clashTermSets;
			}

			for (IDLTerm<I, L, K, R> desc : extraDescs) {
				if (desc instanceof IDLClassExpression) {
					termColl.add(tbox.getUnfolding((IDLClassExpression<I, L, K, R>) desc));
					return clashTermSets;
				}
			}
		}
		final SortedSet<IDLTerm<I, L, K, R>> nodeNegations = node.getTerms().subSet(DLTermOrder.DL_OBJECT_NEGATION);

		for (IDLTerm<I, L, K, R> term : ChainIterable.decorate(termColl)) {
			if (term instanceof IDLClassExpression) {
				final IDLClassExpression<I, L, K, R> desc = (IDLClassExpression<I, L, K, R>) term;
				final IDLClassExpression<I, L, K, R> negDesc = termFactory.getDLObjectNegation(desc);
				if (nodeNegations.contains(negDesc)) {
					clashTermSets.add(makeTermEntryPairSet(termEntryFactory, node.getNodeID(), desc, negDesc));
					return clashTermSets;
				}
				if ((extraDescs != null) && extraDescs.contains(negDesc)) {
					clashTermSets.add(makeTermEntryPairSet(termEntryFactory, node.getNodeID(), desc, negDesc));
					return clashTermSets;
				}
			}
		}
		return clashTermSets;
	}


	@SuppressWarnings("unchecked")
	public ConsistencyInfo<I, L, K, R> isLinkConsistent(
		final IABoxNode<I, L, K, R> node)
	{
		final IRABox<I, L, K, R> raBox = node.getRABox();
		final IRBox<I, L, K, R> rBox = node.getABox().getTBox().getRBox();
		final Collection<R> outRoles = raBox.getOutgoingRoles();
		final ConsistencyInfo<I, L, K, R> cInfo = new ConsistencyInfo<>();
		for (R outRole : outRoles) {
			final boolean isDataProperty = (rBox.getRoleType(outRole) == RoleType.DATA_PROPERTY);
			final boolean isFunctional = rBox.hasRoleProperty(outRole, RoleProperty.FUNCTIONAL);

			int nSucc = 0;
			for (IABoxNode<I, L, K, R> succ : raBox.getSuccessorNodes(outRole)) {
				++nSucc;
				if (isFunctional && (nSucc > 1)) {
					cInfo.addCulprits(node, node.getABox().getDLTermFactory().getDLThing());
					cInfo.upgradeClashType(ConsistencyInfo.ClashType.TRANSIENT);
					return cInfo;
				}
				/**
				 * datatype properties may only link to datatype nodes and object properies only to individual nodes
				 *
				 */
				if (isDataProperty != (succ instanceof IDatatypeABoxNode)) {
					cInfo.addCulprits(node, node.getABox().getDLTermFactory().getDLThing());
					cInfo.upgradeClashType(ConsistencyInfo.ClashType.FINAL);
					return cInfo;
				}
			}
		}
		return cInfo;
	}


	@Override
	public ConsistencyInfo<I, L, K, R> isConsistent(IABox<I, L, K, R> abox, IABoxNode<I, L, K, R> node)
	{
		return isConsistent(abox, node, null);
	}


	@Override
	public ConsistencyInfo<I, L, K, R> isConsistent(IABoxNode<I, L, K, R> node)
	{
		return isConsistent(node.getABox(), node);
	}


	@Override
	public ConsistencyInfo<I, L, K, R> isConsistent(IABox<I, L, K, R> abox, IABoxNode<I, L, K, R> node, Collection<? extends IDLTerm<I, L, K, R>> extraDescs)
	{
		final ConsistencyInfo<I, L, K, R> cInfo = new ConsistencyInfo<>();
		final Set<Set<TermEntry<I, L, K, R>>> inconsistent = getInconsistentTermEntries(abox, node, extraDescs);
		if (!inconsistent.isEmpty()) {
			cInfo.addCulpritEntries(node, inconsistent);
			cInfo.upgradeClashType(ConsistencyInfo.ClashType.FINAL);
		}
		if (cInfo.isFinallyInconsistent()) {
			return cInfo;
		}

		cInfo.updateFrom(isProperDatatypeNode(node));
		if (cInfo.isFinallyInconsistent()) {
			return cInfo;
		}

		cInfo.updateFrom(isLinkConsistent(node));
		return cInfo;
	}


	@Override
	public ConsistencyInfo<I, L, K, R> isExtraConsistent(IABox<I, L, K, R> abox, IABoxNode<I, L, K, R> node, Collection<? extends IDLTerm<I, L, K, R>> extraDescs)
	{
		final ConsistencyInfo<I, L, K, R> cInfo = new ConsistencyInfo<>();
		final Set<Set<TermEntry<I, L, K, R>>> inconsistent = getInconsistentExtraTermEntries(abox, node,
																							 extraDescs);
		cInfo.addCulpritEntries(node, inconsistent);
		if (cInfo.isFinallyInconsistent()) {
			return cInfo;
		}

		cInfo.updateFrom(isProperDatatypeNode(node));
		if (cInfo.isFinallyInconsistent()) {
			return cInfo;
		}

		cInfo.updateFrom(isLinkConsistent(node));
		return cInfo;
	}


	@Override
	public ConsistencyInfo<I, L, K, R> isExtraConsistent(IABoxNode<I, L, K, R> node, Collection<? extends IDLTerm<I, L, K, R>> extraDescs)
	{
		return isExtraConsistent(null, node, extraDescs);
	}


	@Override
	public ConsistencyInfo<I, L, K, R> isConsistent(IABoxNode<I, L, K, R> node, Collection<? extends IDLTerm<I, L, K, R>> extraDescs)
	{
		return isConsistent(node.getABox(), node, extraDescs);
	}


	@Override
	public ConsistencyInfo<I, L, K, R> isConsistent(IDLTermFactory<I, L, K, R> termFactory, TermEntryFactory<I, L, K, R> termEntryFactory, Collection<? extends IDLTerm<I, L, K, R>> descs)
	{
		return isConsistent(termFactory, termEntryFactory, descs, null);
	}


	@Override
	public ConsistencyInfo<I, L, K, R> isConsistent(IDLTermFactory<I, L, K, R> termFactory, TermEntryFactory<I, L, K, R> termEntryFactory, Collection<? extends IDLTerm<I, L, K, R>> descs, Collection<? extends IDLTerm<I, L, K, R>> extraDescs)
	{
		final ConsistencyInfo<I, L, K, R> cInfo = new ConsistencyInfo<>();
		final Set<Set<TermEntry<I, L, K, R>>> inconsistent = getInconsistentTermEntries(termFactory,
																						termEntryFactory, descs,
																						extraDescs);
		if (!inconsistent.isEmpty()) {
			cInfo.addCulpritEntries((IABox<I, L, K, R>) null, inconsistent);
		}

		return cInfo;

	}


	@Override
	public ConsistencyInfo<I, L, K, R> isExtraConsistent(IDLTermFactory<I, L, K, R> termFactory, TermEntryFactory<I, L, K, R> termEntryFactory, Collection<? extends IDLTerm<I, L, K, R>> descs, Collection<? extends IDLTerm<I, L, K, R>> extraDescs)
	{
		final ConsistencyInfo<I, L, K, R> cInfo = new ConsistencyInfo<>();
		final Set<Set<TermEntry<I, L, K, R>>> inconsistent = getInconsistentExtraTermEntries(termFactory,
																							 termEntryFactory,
																							 descs,
																							 extraDescs);
		if (!inconsistent.isEmpty()) {
			cInfo.addCulpritEntries((IABox<I, L, K, R>) null, inconsistent);
		}

		return cInfo;
	}


		private Set<TermEntry<I, L, K, R>> makeTermEntryPairSet(final TermEntryFactory<I, L, K, R> factory, final NodeID nodeID, final IDLTerm<I, L, K, R> one, final IDLTerm<I, L, K, R> two)
	{
		final Set<TermEntry<I, L, K, R>> set = new Flat3Set<>(2);
		set.add(factory.getEntry(nodeID, one));
		set.add(factory.getEntry(nodeID, two));
		return new FrozenSet<>(set);
	}


		private ConsistencyInfo<I, L, K, R> isProperDatatypeNode(final IABoxNode<I, L, K, R> node)
	{
		final ConsistencyInfo<I, L, K, R> cInfo = new ConsistencyInfo<>();
		if (node instanceof IDatatypeABoxNode) {
			final IDatatypeABoxNode<I, L, K, R> dtNode = (IDatatypeABoxNode<I, L, K, R>) node;
			/* This check is superfluous in the current implementation, as datatype nodes can programmatically never have successors */
			//if (! node.getRABox().getAssertedSuccessors().isEmpty())
			//	return false;

			/**
			 * XXX - Assume that datatype (literal) nodes only ever have only one name.
			 */
			IDLLiteralReference<I, L, K, R> firstTerm = null;
			for (IDLTerm<I, L, K, R> term : node.getTerms().subSet(DLTermOrder.DL_LITERAL_REFERENCE)) {
				if (firstTerm == null) {
					firstTerm = (IDLLiteralReference<I, L, K, R>) term;
				} else {
					/* XXX - actual all names clash pairwise. But we should get here after the second one was added, already */
					final IDLLiteralReference<I, L, K, R> thisTerm = (IDLLiteralReference<I, L, K, R>) term;
					cInfo.addCulprits(node, firstTerm, thisTerm);
					cInfo.upgradeClashType(ConsistencyInfo.ClashType.FINAL);
				}
			}

			if (cInfo.isFinallyInconsistent())
				return cInfo;

			for (IDLTerm<I, L, K, R> term : node.getTerms().subSet(DLTermOrder.DL_DATATYPE_EXPRESSION)) {
				assert term instanceof IDLDatatypeExpression;
				final IDLDatatypeExpression<I, L, K, R> dtExpression = (IDLDatatypeExpression<I, L, K, R>) term;
				for (L literal : dtNode.getNames()) {
					if (!dtExpression.isValidValue(literal)) {
						cInfo.addCulprits(node,
										  node.getABox().getDLTermFactory().getDLLiteralReference(literal),
										  dtExpression);
						cInfo.upgradeClashType(ConsistencyInfo.ClashType.FINAL);
						return cInfo;
					}
				}
				final Set<Set<L>> inconsistent = dtExpression.getIncompatibleValues(dtNode.getNames());
				if (!inconsistent.isEmpty()) {
					for (Set<L> clashLits : inconsistent) {
						List<IDLTerm<I, L, K, R>> clashTerms = new ArrayList<>();
						for (L lit : clashLits) {
							final IDLLiteralReference<I, L, K, R> nomRef = node.getABox().getDLTermFactory().
								getDLLiteralReference(lit);
							clashTerms.add(nomRef);
						}
						cInfo.upgradeClashType(ConsistencyInfo.ClashType.FINAL);
						cInfo.addCulprits(node, clashTerms);
					}
					return cInfo;
				}
			}

			for (IDLTerm<I, L, K, R> term : node.getTerms().subSet(DLTermOrder.DL_DATA_NEGATION)) {
				final IDLDataNegation<I, L, K, R> negTerm = (IDLDataNegation<I, L, K, R>) term;
				if (negTerm.getTerm() instanceof IDLDatatypeExpression) {
					final IDLDatatypeExpression<I, L, K, R> dtExpression = (IDLDatatypeExpression<I, L, K, R>) negTerm.getTerm();
					for (L literal : dtNode.getNames()) {
						if (dtExpression.isValidValue(literal)) {
							cInfo.addCulprits(node,
											  node.getABox().getDLTermFactory().getDLLiteralReference(literal),
											  negTerm);
							cInfo.upgradeClashType(ConsistencyInfo.ClashType.FINAL);
							return cInfo;
						}
					}
				}
			}
		}
		return cInfo;
	}
}

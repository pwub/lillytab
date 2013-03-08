/**
 * (c) 2009-2012 Otto-Friedrich-University Bamberg
 *
 * $Id$
 *
 * Use, modification and restribution of this file are covered by the terms of the Artistic License 2.0.
 *
 * You should have received a copy of the license terms in a file named "LICENSE" together with this software package.
 *
 * Disclaimer of Warranty: THE PACKAGE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS "AS IS' AND WITHOUT ANY
 * EXPRESS OR IMPLIED WARRANTIES. THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT ARE DISCLAIMED TO THE EXTENT PERMITTED BY YOUR LOCAL LAW. UNLESS REQUIRED BY LAW, NO COPYRIGHT
 * HOLDER OR CONTRIBUTOR WILL BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING IN ANY
 * WAY OUT OF THE USE OF THE PACKAGE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner;

import de.dhke.projects.cutil.collections.frozen.FrozenSet;
import de.dhke.projects.cutil.collections.iterator.ChainIterable;
import de.dhke.projects.cutil.collections.set.Flat3Set;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import de.dhke.projects.lutil.LoggingClass;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IRABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntryFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.ITBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleProperty;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLNominalReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datatype.IDLDatatypeExpression;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

/**
 *
 * @param <Name> The type for nominals and values
 * @param <Klass> The type for DL classes
 * @param <Role> The type for properties (roles)
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


	private Set<TermEntry<Name, Klass, Role>> makeTermEntryPairSet(
		final TermEntryFactory<Name, Klass, Role> factory,
		final NodeID nodeID,
		final IDLTerm<Name, Klass, Role> one, final IDLTerm<Name, Klass, Role> two)
	{
		final Set<TermEntry<Name, Klass, Role>> set = new Flat3Set<>(2);
		set.add(factory.getEntry(nodeID, one));
		set.add(factory.getEntry(nodeID, two));
		return new FrozenSet<>(set);
	}


	private boolean hasAtMostNSuccessors(final IRABox<Name, Klass, Role> raBox, final Role role, final int n)
	{
		int i = n;
		for (NodeID succID : raBox.getSuccessors(role)) {
			--i;
			if (i < 0) {
				return false;
			}
		}
		return true;
	}


	public Set<Set<TermEntry<Name, Klass, Role>>> getInconsistentTermEntries(
		final IDLTermFactory<Name, Klass, Role> termFactory,
		final TermEntryFactory<Name, Klass, Role> termEntryFactory,
		final Collection<? extends IDLTerm<Name, Klass, Role>> descs,
		final Collection<? extends IDLTerm<Name, Klass, Role>> extraDescs)
	{
		final Set<Set<TermEntry<Name, Klass, Role>>> clashTermSets = new Flat3Set<>();

		final Collection<Iterable<? extends IDLTerm<Name, Klass, Role>>> termColl = new ArrayList<>();
		termColl.add(descs);
		if (extraDescs != null) {
			termColl.add(extraDescs);
		}
		for (IDLTerm<Name, Klass, Role> term : ChainIterable.decorate(termColl)) {
			if (term instanceof IDLRestriction) {
				final IDLRestriction<Name, Klass, Role> desc = (IDLRestriction<Name, Klass, Role>) term;
				final IDLRestriction<Name, Klass, Role> negDesc = termFactory.getDLNegation(desc);
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


	public Set<Set<TermEntry<Name, Klass, Role>>> getInconsistentExtraTermEntries(
		final IDLTermFactory<Name, Klass, Role> termFactory,
		final TermEntryFactory<Name, Klass, Role> termEntryFactory,
		final Collection<? extends IDLTerm<Name, Klass, Role>> descs,
		final Collection<? extends IDLTerm<Name, Klass, Role>> extraDescs)
	{
		final Set<Set<TermEntry<Name, Klass, Role>>> clashTermSets = new Flat3Set<>();

		if (extraDescs.contains(termFactory.getDLNothing())) {
			clashTermSets.add(makeTermEntryPairSet(termEntryFactory, null, termFactory.getDLNothing(),
												   termFactory.getDLThing()));
		}

		/* it is sufficient to iterate over extraDescs, because descs are assumed to be consistent */
		for (IDLTerm<Name, Klass, Role> term : extraDescs) {
			if (term instanceof IDLRestriction) {
				final IDLRestriction<Name, Klass, Role> desc = (IDLRestriction<Name, Klass, Role>) term;
				final IDLRestriction<Name, Klass, Role> negDesc = termFactory.getDLNegation(desc);
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


	public Set<Set<TermEntry<Name, Klass, Role>>> getInconsistentTermEntries(
		final IABox<Name, Klass, Role> abox,
		final IABoxNode<Name, Klass, Role> node,
		final Collection<? extends IDLTerm<Name, Klass, Role>> extraDescs)
	{
		final IDLTermFactory<Name, Klass, Role> termFactory = abox.getDLTermFactory();
		final TermEntryFactory<Name, Klass, Role> termEntryFactory = abox.getTermEntryFactory();
		return getInconsistentTermEntries(termFactory, termEntryFactory, abox.getTBox(), node, extraDescs);
	}


	public Set<Set<TermEntry<Name, Klass, Role>>> getInconsistentTermEntries(
		final IABoxNode<Name, Klass, Role> node,
		final Collection<? extends IDLTerm<Name, Klass, Role>> extraDescs)
	{
		return getInconsistentTermEntries(node.getABox(), node, extraDescs);
	}


	public Set<Set<TermEntry<Name, Klass, Role>>> getInconsistentTermEntries(
		final IDLTermFactory<Name, Klass, Role> termFactory,
		final TermEntryFactory<Name, Klass, Role> termEntryFactory,
		final ITBox<Name, Klass, Role> tbox,
		final IABoxNode<Name, Klass, Role> node,
		final Collection<? extends IDLTerm<Name, Klass, Role>> extraDescs)
	{
		final Set<Set<TermEntry<Name, Klass, Role>>> clashTermSets = new Flat3Set<>();
		final Collection<Iterable<? extends IDLTerm<Name, Klass, Role>>> termColl = new ArrayList<>();

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
		/* XXX - test, if it is more efficient to collect all unfoldings into a set */
		if (extraDescs != null) {
			for (IDLTerm<Name, Klass, Role> desc : extraDescs) {
				if (desc instanceof IDLRestriction) {
					termColl.add(tbox.getUnfolding((IDLRestriction<Name, Klass, Role>) desc));
				}
			}
		}
		final SortedSet<IDLTerm<Name, Klass, Role>> nodeNegations = node.getTerms().subSet(DLTermOrder.DL_NEGATION);

		for (IDLTerm<Name, Klass, Role> term : ChainIterable.decorate(termColl)) {
			if (term instanceof IDLRestriction) {
				final IDLRestriction<Name, Klass, Role> desc = (IDLRestriction<Name, Klass, Role>) term;
				final IDLRestriction<Name, Klass, Role> negDesc = termFactory.getDLNegation(desc);
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


	public Set<Set<TermEntry<Name, Klass, Role>>> getInconsistentExtraTermEntries(
		final IABox<Name, Klass, Role> abox,
		final IABoxNode<Name, Klass, Role> node,
		final Collection<? extends IDLTerm<Name, Klass, Role>> extraDescs)
	{
		return getInconsistentExtraTermEntries(abox.getDLTermFactory(), abox.getTermEntryFactory(), abox.getTBox(), node,
											   extraDescs);
	}


	public Set<Set<TermEntry<Name, Klass, Role>>> getInconsistentExtraTermEntries(
		final IDLTermFactory<Name, Klass, Role> termFactory,
		final TermEntryFactory<Name, Klass, Role> termEntryFactory,
		final ITBox<Name, Klass, Role> tbox,
		final IABoxNode<Name, Klass, Role> node,
		final Collection<? extends IDLTerm<Name, Klass, Role>> extraDescs)
	{
		final Set<Set<TermEntry<Name, Klass, Role>>> clashTermSets = new Flat3Set<>();
		final Collection<Iterable<? extends IDLTerm<Name, Klass, Role>>> termColl = new ArrayList<>();
		/* XXX - test, if it is more efficient to collect all unfoldings into a set */
		if (extraDescs != null) {
			if (extraDescs.contains(termFactory.getDLNothing())) {
				clashTermSets.add(makeTermEntryPairSet(termEntryFactory, node.getNodeID(), termFactory.getDLNothing(),
													   termFactory.getDLThing()));
				return clashTermSets;
			}

			for (IDLTerm<Name, Klass, Role> desc : extraDescs) {
				if (desc instanceof IDLRestriction) {
					termColl.add(tbox.getUnfolding((IDLRestriction<Name, Klass, Role>) desc));
					return clashTermSets;
				}
			}
		}
		final SortedSet<IDLTerm<Name, Klass, Role>> nodeNegations = node.getTerms().subSet(DLTermOrder.DL_NEGATION);

		for (IDLTerm<Name, Klass, Role> term : ChainIterable.decorate(termColl)) {
			if (term instanceof IDLRestriction) {
				final IDLRestriction<Name, Klass, Role> desc = (IDLRestriction<Name, Klass, Role>) term;
				final IDLRestriction<Name, Klass, Role> negDesc = termFactory.getDLNegation(desc);
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


	private ConsistencyInfo<Name, Klass, Role> isProperDatatypeNode(final IABoxNode<Name, Klass, Role> node)
	{
		final ConsistencyInfo<Name, Klass, Role> cInfo = new ConsistencyInfo<>();
		if (node.isDatatypeNode()) {
			/* This check is superfluous in the current implementation, as datatype nodes can programmatically never have successors */
			//if (! node.getRABox().getAssertedSuccessors().isEmpty())
			//	return false;

			for (IDLTerm<Name, Klass, Role> term : node.getTerms().subSet(DLTermOrder.DL_DATATYPE_EXPRESSION)) {
				assert term instanceof IDLDatatypeExpression;
				final IDLDatatypeExpression<Name, Klass, Role> dtExpression = (IDLDatatypeExpression<Name, Klass, Role>) term;
				for (Name individual : node.getNames()) {
					if (!dtExpression.isValidValue(individual)) {
						cInfo.addCulprits(node,
										  node.getABox().getDLTermFactory().getDLNominalReference(individual),
										  dtExpression);
						cInfo.upgradeClashType(ConsistencyInfo.ClashType.FINAL);
						return cInfo;
					}
				}
				final Set<Set<Name>> inconsistent = dtExpression.getIncompatibleValues(node.getNames());
				if (!inconsistent.isEmpty()) {
					for (Set<Name> clashNames : inconsistent) {
						List<IDLTerm<Name, Klass, Role>> clashTerms = new ArrayList<>();
						for (Name name : clashNames) {
							final IDLNominalReference<Name, Klass, Role> nomRef = node.getABox().getDLTermFactory().
								getDLNominalReference(name);
							clashTerms.add(nomRef);
						}
						cInfo.upgradeClashType(ConsistencyInfo.ClashType.FINAL);
						cInfo.addCulprits(node, clashTerms);
					}
					return cInfo;
				}
			}

		}
		return cInfo;
	}


	@SuppressWarnings("unchecked")
	public ConsistencyInfo<Name, Klass, Role> isLinkConsistent(final IABoxNode<Name, Klass, Role> node)
	{
		final IRABox<Name, Klass, Role> raBox = node.getRABox();
		final IRBox<Name, Klass, Role> rBox = node.getABox().getTBox().getRBox();
		final Collection<Role> outRoles = raBox.getOutgoingRoles();
		final ConsistencyInfo<Name, Klass, Role> cInfo = new ConsistencyInfo<>();
		for (Role outRole : outRoles) {
			if (rBox.hasRoleProperty(outRole, RoleProperty.FUNCTIONAL)) {
				if (!hasAtMostNSuccessors(raBox, outRole, 1)) {
					/* XXX - this needs to be fixed to be the governing term of the node */
					cInfo.addCulprits(node, node.getABox().getDLTermFactory().getDLThing());
					cInfo.upgradeClashType(ConsistencyInfo.ClashType.TRANSIENT);
					return cInfo;
				}
			}
		}
		return cInfo;
	}


	@Override
	public ConsistencyInfo<Name, Klass, Role> isConsistent(IABox<Name, Klass, Role> abox,
														   IABoxNode<Name, Klass, Role> node)
	{
		return isConsistent(abox, node, null);
	}


	@Override
	public ConsistencyInfo<Name, Klass, Role> isConsistent(IABoxNode<Name, Klass, Role> node)
	{
		return isConsistent(node.getABox(), node);
	}


	@Override
	public ConsistencyInfo<Name, Klass, Role> isConsistent(IABox<Name, Klass, Role> abox,
														   IABoxNode<Name, Klass, Role> node,
														   Collection<? extends IDLTerm<Name, Klass, Role>> extraDescs)
	{
		final ConsistencyInfo<Name, Klass, Role> cInfo = new ConsistencyInfo<>();
		final Set<Set<TermEntry<Name, Klass, Role>>> inconsistent = getInconsistentTermEntries(abox, node, extraDescs);
		if (!inconsistent.isEmpty()) {
			cInfo.addCulpritEntries(node, inconsistent);
			cInfo.upgradeClashType(ConsistencyInfo.ClashType.FINAL);;
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
	public ConsistencyInfo<Name, Klass, Role> isExtraConsistent(IABox<Name, Klass, Role> abox,
																IABoxNode<Name, Klass, Role> node,
																Collection<? extends IDLTerm<Name, Klass, Role>> extraDescs)
	{
		final ConsistencyInfo<Name, Klass, Role> cInfo = new ConsistencyInfo<>();
		final Set<Set<TermEntry<Name, Klass, Role>>> inconsistent = getInconsistentExtraTermEntries(abox, node,
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
	public ConsistencyInfo<Name, Klass, Role> isExtraConsistent(IABoxNode<Name, Klass, Role> node,
																Collection<? extends IDLTerm<Name, Klass, Role>> extraDescs)
	{
		return isExtraConsistent(null, node, extraDescs);
	}


	@Override
	public ConsistencyInfo<Name, Klass, Role> isConsistent(IABoxNode<Name, Klass, Role> node,
														   Collection<? extends IDLTerm<Name, Klass, Role>> extraDescs)
	{
		return isConsistent(node.getABox(), node, extraDescs);
	}


	@Override
	public ConsistencyInfo<Name, Klass, Role> isConsistent(IDLTermFactory<Name, Klass, Role> termFactory,
														   TermEntryFactory<Name, Klass, Role> termEntryFactory,
														   Collection<? extends IDLTerm<Name, Klass, Role>> descs)
	{
		return isConsistent(termFactory, termEntryFactory, descs, null);
	}


	@Override
	public ConsistencyInfo<Name, Klass, Role> isConsistent(IDLTermFactory<Name, Klass, Role> termFactory,
														   TermEntryFactory<Name, Klass, Role> termEntryFactory,
														   Collection<? extends IDLTerm<Name, Klass, Role>> descs,
														   Collection<? extends IDLTerm<Name, Klass, Role>> extraDescs)
	{
		final ConsistencyInfo<Name, Klass, Role> cInfo = new ConsistencyInfo<>();
		final Set<Set<TermEntry<Name, Klass, Role>>> inconsistent = getInconsistentTermEntries(termFactory,
																							   termEntryFactory, descs,
																							   extraDescs);
		if (!inconsistent.isEmpty()) {
			cInfo.addCulpritEntries((IABox<Name, Klass, Role>) null, inconsistent);
		}

		return cInfo;

	}


	@Override
	public ConsistencyInfo<Name, Klass, Role> isExtraConsistent(IDLTermFactory<Name, Klass, Role> termFactory,
																TermEntryFactory<Name, Klass, Role> termEntryFactory,
																Collection<? extends IDLTerm<Name, Klass, Role>> descs,
																Collection<? extends IDLTerm<Name, Klass, Role>> extraDescs)
	{
		final ConsistencyInfo<Name, Klass, Role> cInfo = new ConsistencyInfo<>();
		final Set<Set<TermEntry<Name, Klass, Role>>> inconsistent = getInconsistentExtraTermEntries(termFactory,
																									termEntryFactory,
																									descs,
																									extraDescs);
		if (!inconsistent.isEmpty()) {
			cInfo.addCulpritEntries((IABox<Name, Klass, Role>) null, inconsistent);
		}

		return cInfo;
	}
}

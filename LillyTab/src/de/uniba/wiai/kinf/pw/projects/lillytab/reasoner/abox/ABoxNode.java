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

import de.dhke.projects.cutil.collections.MultiMapUtil;
import de.dhke.projects.cutil.collections.EmptyMultiMap;
import de.dhke.projects.cutil.collections.aspect.AspectMultiMap;
import de.dhke.projects.cutil.collections.cow.CopyOnWriteMultiMap;
import de.dhke.projects.cutil.collections.cow.CopyOnWriteSortedSet;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.*;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLNominalReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.TermUtil;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.SetUtils;


/**
 * <p>
 * Default in-memory implementation of {@link IABoxNode}.
 * </p>
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 *
 * @param <Name> The type of the referenced individuals
 * @param <Klass> Type for class names
 * @param <Role> Type for role names
 */
public class ABoxNode<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends AbstractAboxNode<Name, Klass, Role>
	implements Cloneable
{
	// private final Map<DLTermOrder, IDLClassExpression<Name, Klass, Role>> _smallestTerms;
	/// <editor-fold defaultstate="collapsed" desc="private fields">
	private ABox<Name, Klass, Role> _abox;
	/**
	 * The set of concept terms for this node. Not final because of copy-on-write.
	 **/
	private TermSet<Name, Klass, Role> _terms;
	/**
	 * The set of successor nodes, indexed by role.
	 **/
	private AspectMultiMap<Role, NodeID, MultiMap<Role, NodeID>> _successors;
	/**
	 * The set of predecessor nodes, indexed by role.
	 **/
	private AspectMultiMap<Role, NodeID, MultiMap<Role, NodeID>> _predecessors;
	/**
	 * The set of node names.
	 **/
	protected SortedSet<Name> _names = new TreeSet<Name>();

	public Name getPrimaryName()
	{
		if (_names.isEmpty())
			return null;
		else
			return _names.first();
	}

	/// </editor-fold>
	public void setABox(final ABox<Name, Klass, Role> abox)
	{
		if (_abox != abox) {
			if (_abox != null) {
				final boolean wasRemoved = _abox.remove(this);
				// assert wasRemoved;
			}
			_abox = abox;
			_abox.add(this);
		}
	}

	/**
	 * <p>
	 * Create a new, named ABox node referencing the specified {@literal individual}.
	 * </p>
	 *
	 * @param abox The abox of the new individual.
	 * @param id The number of the new node.
	 */
	protected ABoxNode(final ABox<Name, Klass, Role> abox, final int id, final boolean isDatatypeNode)
	{
		super(id, isDatatypeNode);
		_abox = abox;
		_terms = new TermSet<Name, Klass, Role>(this);
//		_smallestTerms = new HashMap<DLTermOrder, IDLClassExpression<Name, Klass, Role>>();
		if (isDatatypeNode())
			_successors = AspectMultiMap.decorate((MultiMap<Role, NodeID>) new EmptyMultiMap<Role, NodeID>(), this);
		else
			_successors = AspectMultiMap.decorate(_abox.getLinkMapFactory().getInstance(), this);
		_predecessors = AspectMultiMap.decorate(_abox.getLinkMapFactory().getInstance(), this);
		addCollectionListeners();
	}

	protected ABoxNode(final ABox<Name, Klass, Role> abox, final NodeID id,
					   final boolean isDatatypeNode,
					   final SortedSet<IDLTerm<Name, Klass, Role>> realTerms,
					   final SortedSet<Name> realNames,
					   final MultiMap<Role, NodeID> realSuccessors,
					   final MultiMap<Role, NodeID> realPredecessors)
	{
		super(id, isDatatypeNode);
		_abox = abox;
		_terms = new TermSet<Name, Klass, Role>(realTerms, this);
		if (isDatatypeNode()) {
			if (!realSuccessors.isEmpty())
				throw new IllegalArgumentException("Cannot create datatype node with successors");
			_successors = AspectMultiMap.decorate((MultiMap<Role, NodeID>) new EmptyMultiMap<Role, NodeID>(), this);
		} else
			_successors = AspectMultiMap.decorate(realSuccessors, this);

		_predecessors = AspectMultiMap.decorate(realPredecessors, this);
		_names = realNames;
		addCollectionListeners();
	}

	private void addCollectionListeners()
	{
		_terms.getListeners().add(new TermSetListener<Name, Klass, Role>());
		_successors.getListeners().add(_abox.getCommon().getLinkMapListener());
		_predecessors.getListeners().add(_abox.getCommon().getLinkMapListener());
	}

	public SortedSet<Name> getNames()
	{
		return Collections.unmodifiableSortedSet(_names);
	}

	public ITermSet<Name, Klass, Role> getTerms()
	{
		return _terms;
	}


	public Collection<TermEntry<Name, Klass, Role>> getTermEntries()
	{
		/* XXX - it is not necessary to re-create the collection for every call */
		return new ABoxNodeTermEntryCollection<Name, Klass, Role>(this);
	}	

	/// <editor-fold defaultstate="collapsed" desc="lazy unfolding">
	private NodeMergeInfo<Name, Klass, Role> addTerm(final IDLRestriction<Name, Klass, Role> desc)
		throws ENodeMergeException
	{
		final NodeMergeInfo<Name, Klass, Role> mergeInfo = new NodeMergeInfo<Name, Klass, Role>(this, false);
		/* the local abox may go away if we join the current node with another node */
		final ABox<Name, Klass, Role> abox = getABox();
		assert abox != null;
		IABoxNode<Name, Klass, Role> currentNode = this;
		if ((!getTerms().contains(desc)) && (desc instanceof IDLNominalReference)) {
			final IDLNominalReference<Name, Klass, Role> nRef = (IDLNominalReference<Name, Klass, Role>) desc;
			final Name newName = nRef.getIndividual();
			IABoxNode<Name, Klass, Role> otherNode = abox.getNode(newName);
			if (otherNode != null) {
				mergeInfo.append(abox.mergeNodes(otherNode, currentNode));
				currentNode = mergeInfo.getCurrentNode();
				assert abox.contains(currentNode);
			}
		}
		if (currentNode.getTerms().add(desc))
			mergeInfo.setModified(currentNode);
		return mergeInfo;
	}

	public NodeMergeInfo<Name, Klass, Role> addUnfoldedDescription(final IDLRestriction<Name, Klass, Role> term)
		throws ENodeMergeException
	{
		// XXX cleanup and code manu are needed
		final ABox<Name, Klass, Role> abox = getABox();
		assert abox != null;
		final IDLRestriction<Name, Klass, Role> nnfTerm = TermUtil.toNNF(term, abox.getCommon().getTermFactory());
		final NodeMergeInfo<Name, Klass, Role> mergeInfo = addTerm(nnfTerm);
		
		/**
		 * get direct unfoldings, perform notify for direct unfoldings and
		 * update dependency map.
		 **/
		ABoxNode<Name, Klass, Role> currentNode = (ABoxNode<Name, Klass, Role>) mergeInfo.getCurrentNode();

		final Collection<IDLRestriction<Name, Klass, Role>> directUnfolds = abox.getTBox().getUnfolding(nnfTerm);
		if (!directUnfolds.isEmpty()) {
			abox.notifyUnfoldListeners(this, nnfTerm, directUnfolds);
			if ((!abox.getDependencyMap().containsKey(nnfTerm))) {
				for (IDLRestriction<Name, Klass, Role> unfoldee : directUnfolds)
					abox.getDependencyMap().addParent(currentNode, unfoldee, currentNode, nnfTerm);
			}
		}

		/**
		 * Handle recursive unfoldings:
		 * 
		 * To avoid a recursive call, perform transitive unfolding.
		 */
		final TreeSet<IDLRestriction<Name, Klass, Role>> allUnfolds = new TreeSet<IDLRestriction<Name, Klass, Role>>(
			directUnfolds);
		Iterator<IDLRestriction<Name, Klass, Role>> unfoldIter = allUnfolds.iterator();
		while (unfoldIter.hasNext()) {
			final IDLRestriction<Name, Klass, Role> currentTerm = unfoldIter.next();
			Collection<IDLRestriction<Name, Klass, Role>> newUnfolds = abox.getTBox().getUnfolding(currentTerm);
			if (allUnfolds.addAll(newUnfolds)) {
				if (!newUnfolds.isEmpty()) {
					abox.notifyUnfoldListeners(this, currentTerm, newUnfolds);
					if (!abox.getDependencyMap().containsKey(currentTerm)) {
						for (IDLRestriction<Name, Klass, Role> unfoldee : newUnfolds)
							abox.getDependencyMap().addParent(currentNode, unfoldee, currentNode, currentTerm);
					}
				}
				/* new unfolds, restart iterator */
				unfoldIter = allUnfolds.iterator();
			}
		}

		if (!allUnfolds.isEmpty()) {
			/* dispatch notification BEFORE actual unfold. Make sure, this is the ONLY piece of code that does this */

			for (IDLRestriction<Name, Klass, Role> unfold : allUnfolds) {
				final NodeMergeInfo<Name, Klass, Role> unfoldResult = currentNode.addTerm(unfold);
				mergeInfo.append(unfoldResult);
			}
		}
		assert abox.contains(currentNode);

		return mergeInfo;
	}

	public NodeMergeInfo<Name, Klass, Role> addUnfoldedDescriptions(
		final Iterable<? extends IDLRestriction<Name, Klass, Role>> descs)
		throws ENodeMergeException
	{
		final NodeMergeInfo<Name, Klass, Role> mergeInfo = new NodeMergeInfo<Name, Klass, Role>(this, false);
		/* the local abox may go away */
		IABoxNode<Name, Klass, Role> currentNode = this;
		for (IDLRestriction<Name, Klass, Role> desc : descs) {
			final NodeMergeInfo<Name, Klass, Role> unfoldResult = currentNode.addUnfoldedDescription(desc);
			mergeInfo.append(unfoldResult);
			currentNode = mergeInfo.getCurrentNode();
		}

		return mergeInfo;
	}
	/// </editor-fold>

	/**
	 * <p>
	 * Perform concept unfolding an all concept terms of the current node.
	 * </p><p>
	 * When the unfolding produces nominals references, node joins
	 * (see {@link IABox#mergeNodes(de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode, de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode) }
	 * may take place. {@literal unfoldAll()} thus returns a {@link NodeMergeInfo}
	 * indicating the ID of the target node containing the unfoldings
	 * and information, if the target node was modified.
	 * </p
	 *
	 * @return A {@link NodeMergeInfo} indicating the ID of the target node containing the unfoldings
	 *	and information, if the target node was modified.
	 */
	public NodeMergeInfo<Name, Klass, Role> unfoldAll()
		throws ENodeMergeException
	{
		final NodeMergeInfo<Name, Klass, Role> mergeInfo = new NodeMergeInfo<Name, Klass, Role>(this, false);
		/* the local abox may go away */
		IABoxNode<Name, Klass, Role> currentNode = this;
		Iterator<IDLTerm<Name, Klass, Role>> iter = currentNode.getTerms().iterator();
		while (iter.hasNext()) {
			final IDLTerm<Name, Klass, Role> term = iter.next();
			if (term instanceof IDLRestriction) {
				final IDLRestriction<Name, Klass, Role> desc = (IDLRestriction<Name, Klass, Role>) term;
				final NodeMergeInfo<Name, Klass, Role> unfoldResult = addUnfoldedDescription(desc);
				mergeInfo.append(unfoldResult);
				assert _abox.contains(currentNode);
				if (!currentNode.equals(unfoldResult.getCurrentNode())) {
					/* a merge operation occured, switch current node, restart description iterator */
					currentNode = unfoldResult.getCurrentNode();
					iter = currentNode.getTerms().iterator();
				} else if (unfoldResult.isModified(currentNode))
					/* no merge operation, but the current node was modified, restart iterator */
					iter = currentNode.getTerms().iterator();
			}
		}


		return mergeInfo;
	}

/// <editor-fold defaultstate="collapsed" desc="Cloneable">
	private ABoxNode<Name, Klass, Role> copyOnWriteClone(final ABox<Name, Klass, Role> newABox)
	{
		ABoxNode<Name, Klass, Role> klone;

		/**
		 * We try to be smart about which set to decorate
		 * with copy on write.
		 *
		 * If the underlying collection is already a copy-on-write,
		 * we create another CopyOnWriteCollection on top of the
		 * decorated set underlying the CopyOnWriteCollection.
		 *
		 * This only works, since we really implement a branch
		 * operation during cloning: We keep the basic state before
		 * cloning and modify this node and the clone to be
		 * copy-on-write from the common base state.
		 */
		SortedSet<IDLTerm<Name, Klass, Role>> realTermSet;
		/* move past the AspectSet, first */
		realTermSet = _terms.getDecoratee();
		while (realTermSet instanceof CopyOnWriteSortedSet)
			realTermSet = ((CopyOnWriteSortedSet<IDLTerm<Name, Klass, Role>>) realTermSet).getDecoratee();
		assert !(realTermSet instanceof CopyOnWriteSortedSet);

		/**
		 * TERMS
		 **/
		final SortedSet<IDLTerm<Name, Klass, Role>> terms = CopyOnWriteSortedSet.decorate(realTermSet, _abox.
			getTermSetFactory());
		final SortedSet<IDLTerm<Name, Klass, Role>> cloneTerms = CopyOnWriteSortedSet.decorate(realTermSet,
																							   _abox.getTermSetFactory());

		/**
		 * NAMES
		 **/
		final SortedSet<Name> cloneNames = new TreeSet<Name>(_names);

		/**
		 * LINKS
		 */
		MultiMap<Role, NodeID> realSuccessors;
		/* move past the AspectMultiMap, first */
		realSuccessors =
			_successors.getDecoratee();
		while (realSuccessors instanceof CopyOnWriteMultiMap)
			realSuccessors = ((CopyOnWriteMultiMap<Role, NodeID>) realSuccessors).getDecoratee();
		assert !(realSuccessors instanceof CopyOnWriteMultiMap);

		final MultiMap<Role, NodeID> successors = CopyOnWriteMultiMap.decorate(realSuccessors, _abox.getLinkMapFactory());
		final MultiMap<Role, NodeID> cloneSuccessors = CopyOnWriteMultiMap.decorate(realSuccessors, _abox.
			getLinkMapFactory());

		MultiMap<Role, NodeID> realPredecessors;
		realPredecessors =
			_predecessors.getDecoratee();
		while (realPredecessors instanceof CopyOnWriteMultiMap)
			realPredecessors = ((CopyOnWriteMultiMap<Role, NodeID>) realPredecessors).getDecoratee();
		assert !(realPredecessors instanceof CopyOnWriteMultiMap);

		final MultiMap<Role, NodeID> predecessors = CopyOnWriteMultiMap.decorate(realPredecessors, _abox.
			getLinkMapFactory());
		final MultiMap<Role, NodeID> clonePredecessors = CopyOnWriteMultiMap.decorate(realPredecessors, _abox.
			getLinkMapFactory());

		klone =
			new ABoxNode<Name, Klass, Role>(newABox, getNodeID(), isDatatypeNode(), cloneTerms, cloneNames,
											cloneSuccessors,
											clonePredecessors);

		/* update internal state */
		_terms = new TermSet<Name, Klass, Role>(terms, this);
		_successors = AspectMultiMap.decorate(successors, this);
		_predecessors = AspectMultiMap.decorate(predecessors, this);
		addCollectionListeners();

		return klone;

	}

	private ABoxNode<Name, Klass, Role> copyClone(final ABox<Name, Klass, Role> newABox)
	{
		final SortedSet<IDLTerm<Name, Klass, Role>> cloneTerms = _abox.getTermSetFactory().getInstance(_terms);
		final MultiMap<Role, NodeID> cloneSuccessors = _abox.getLinkMapFactory().getInstance(_successors);
		final MultiMap<Role, NodeID> clonePredecessors = _abox.getLinkMapFactory().getInstance(_predecessors);
		final SortedSet<Name> cloneNames = new TreeSet<Name>(_names);
		return new ABoxNode<Name, Klass, Role>(newABox, getNodeID(), isDatatypeNode(), cloneTerms, cloneNames,
											   cloneSuccessors,
											   clonePredecessors);
	}

	public ABoxNode<Name, Klass, Role> clone(final IABox<Name, Klass, Role> newABox)
	{
		assert newABox instanceof ABox;
		final ABox<Name, Klass, Role> nAbox = (ABox<Name, Klass, Role>) newABox;
		if (StorageOptions.COPY_ON_WRITE)
			return copyOnWriteClone(nAbox);
		else
			return copyClone(nAbox);
	}
/// </editor-fold>

/// <editor-fold defaultstate="collapsed" desc="IABoxNode">
	public ABox<Name, Klass, Role> getABox()
	{
		return _abox;
	}

	public MultiMap<Role, NodeID> getSuccessors()
	{
		return _successors;
	}

	public MultiMap<Role, NodeID> getPredecessors()
	{
		return _predecessors;
	}
/// </editor-fold>

/// <editor-fold defaultstate="collapsed" desc="toString()">
	@Override
	public String toString()
	{
		return toString("");
	}

	public String toString(
		final int indent)
	{
		char[] fill = new char[indent];
		Arrays.fill(fill, ' ');
		return new String(fill);
	}

	@Override
	public int deepHashCode()
	{
		int hashcode = 147;
		hashcode += SetUtils.hashCodeForSet(getTerms());
		for (Map.Entry<Role, Collection<NodeID>> succEntry : getSuccessors().entrySet()) {
			hashcode += 5 * succEntry.getKey().hashCode();
			for (NodeID succID : succEntry.getValue())
				hashcode += 6 * succID.hashCode();
		}
		for (Map.Entry<Role, Collection<NodeID>> predEntry : getPredecessors().entrySet()) {
			hashcode += 7 * predEntry.getKey().hashCode();
			for (NodeID predID : predEntry.getValue())
				hashcode += 8 * predID.hashCode();
		}
		return hashcode;
	}

	@Override
	public boolean deepEquals(final Object obj)
	{
		if (obj instanceof IABoxNode) {
			@SuppressWarnings("unchecked")
			final IABoxNode<Name, Klass, Role> other = (IABoxNode<Name, Klass, Role>) obj;
			if (!SetUtils.isEqualSet(getTerms(), other.getTerms()))
				return false;
			if (!MultiMapUtil.deepEquals(_successors, other.getSuccessors()))
				return false;
			if (!MultiMapUtil.deepEquals(_successors, other.getSuccessors()))
				return false;
			return true;
		} else
			return false;
	}
/// </editor-fold>

	public IABoxNode<Name, Klass, Role> getImmutable()
	{
		return getABox().getImmutable().getNode(getNodeID());
	}
}

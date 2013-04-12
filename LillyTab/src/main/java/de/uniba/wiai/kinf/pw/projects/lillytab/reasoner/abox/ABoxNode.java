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

import de.dhke.projects.lutil.LoggingClass;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IDatatypeABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IIndividualABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IRABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ITermSet;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.TermSet.TermTypes;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataRange;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.commons.collections15.SetUtils;

/**
 * Default in-memory implementation of {@link IABoxNode}.
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 *
 * @param <I> The type for nominals and values
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 */
public abstract class ABoxNode<N extends Comparable<? super N>, I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends LoggingClass
	implements IABoxNode<I, L, K, R>, Cloneable {
	// private final Map<DLTermOrder, IDLClassExpression<I, L, K, R>> _smallestTerms;

	/**
	 * The (unmodifiable) node ID of this node.
	 *
	 */
	private final NodeID _id;
	/**
	 * if the current node is a datatype node
	 *
	 */
	private final boolean _isDatatypeNode;
	// private final Map<DLTermOrder, IDLClassExpression<I, L, K, R>> _smallestTerms;
	/**
	 * The link map
	 *
	 */
	protected final IRABox<I, L, K, R> _raBox;
	/**
	 * The Abox
	 *
	 */
	private ABox<I, L, K, R> _abox;
	/**
	 * The set of concept terms for this node. Not final because of copy-on-write.
	 */
	protected final ABoxNodeTermSet<I, L, K, R> _terms;
	/**
	 * Wrapper around {@link #_terms} that prevents direct additions.
	 */
	private final NoAddTermSet<I, L, K, R> _noAddTerms;
	/**
	 * The set of node names.
	 */
	protected final SortedSet<N> _names = new TreeSet<>();


	/**
	 * Create a new, named ABox node referencing the specified {@literal individual}.
	 *
	 * @param abox The abox of the new individual.
	 * @param id The number of the new node.
	 * @param isDatatypeNode
	 */
	protected ABoxNode(final ABox<I, L, K, R> abox, final int id, final boolean isDatatypeNode)
	{
		_id = new NodeID(id);
		_isDatatypeNode = isDatatypeNode;
		_abox = abox;
		_terms = new ABoxNodeTermSet<>(this);
		if (isDatatypeNode) {
			_noAddTerms = new NoAddTermSet<>(TermTypes.DATATYPE_ONLY, _terms, this);
		} else {
			_noAddTerms = new NoAddTermSet<>(TermTypes.CLASS_ONLY, _terms, this);
		}

		_raBox = new RABox<>(this);
	}


	protected ABoxNode(final ABox<I, L, K, R> newABox,
					   final ABoxNode<N, I, L, K, R> klonee)
	{
		_id = klonee.getNodeID();
		_isDatatypeNode = klonee.isDatatypeNode();
		_abox = newABox;
		_names.addAll(klonee.getNames());
		_terms = klonee._terms.clone(this);
		if (_isDatatypeNode) {
			_noAddTerms = new NoAddTermSet<>(TermTypes.DATATYPE_ONLY, _terms, this);
		} else {
			_noAddTerms = new NoAddTermSet<>(TermTypes.CLASS_ONLY, _terms, this);
		}
		_raBox = klonee._raBox.clone(this);
	}


	@Override
	public SortedSet<N> getNames()
	{
		return Collections.unmodifiableSortedSet(_names);
	}


	@Override
	public N getPrimaryName()
	{
		if (_names.isEmpty()) {
			return null;
		} else {
			return _names.first();
		}
	}


	@Override
	public IRABox<I, L, K, R> getRABox()
	{
		return _raBox;
	}

	/// </editor-fold>

	public void setABox(final ABox<I, L, K, R> abox)
	{
		if (_abox != abox) {
			if (_abox != null) {
				final boolean wasRemoved = _abox.remove(this);
				// assert wasRemoved;
			}
			_abox = abox;
			if (_abox != null) {
				abox.add(this);
			}
		}
	}


	@Override
	public ITermSet<I, L, K, R> getTerms()
	{
		return _noAddTerms;
	}


	@Override
	public Collection<TermEntry<I, L, K, R>> getTermEntries()
	{
		/*
		 * XXX - it is not necessary to re-create the collection for every call
		 */
		return new ABoxNodeTermEntryCollection<>(this);
	}


//	/**
//	 * 
//	 * Perform concept unfolding an all concept terms of the current node. 
//	 * <p />
//	 * When the unfolding produces nominals references, node joins 
//	 * (see {@link IABox#mergeNodes(de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode, de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode)}
//	 * may take place. {@literal unfoldAll()} thus returns a {@link NodeMergeInfo} indicating the ID of the target node
//	 * containing the unfoldings and information, if the target node was modified. 
//	 * 
//	 *
//	 * @return A {@link NodeMergeInfo} indicating the ID of the target node containing the unfoldings and information,
//	 * if the target node was modified.
//	 * @throws ENodeMergeException
//	 */
//	@Override
//	public NodeMergeInfo<I, L, K, R> unfoldAll()
//		throws ENodeMergeException
//	{
//		final NodeMergeInfo<I, L, K, R> mergeInfo = new NodeMergeInfo<>(this, false);
//		/*
//		 * the local abox may go away
//		 */
//		IABoxNode<I, L, K, R> currentNode = this;
//		Iterator<IDLTerm<I, L, K, R>> iter = currentNode.getTerms().iterator();
//		while (iter.hasNext()) {
//			final IDLTerm<I, L, K, R> term = iter.next();
//			if (term instanceof IDLClassExpression) {
//				final IDLClassExpression<I, L, K, R> desc = (IDLClassExpression<I, L, K, R>) term;
//				final NodeMergeInfo<I, L, K, R> unfoldResult = addClassTerm(desc);
//				mergeInfo.append(unfoldResult);
//				assert _abox.contains(currentNode);
//				if (!currentNode.equals(unfoldResult.getCurrentNode())) {
//					// a merge operation occured, switch current node, restart description iterator
//					currentNode = unfoldResult.getCurrentNode();
//					iter = currentNode.getTerms().iterator();
//				} else if (unfoldResult.isModified(currentNode))
//					 // no merge operation, but the current node was modified, restart iterator
//					iter = currentNode.getTerms().iterator();
//			}
//		}
//
//
//		return mergeInfo;
//	}
/// <editor-fold defaultstate="collapsed" desc="Cloneable">
	@Override
	public abstract ABoxNode<N, I, L, K, R> clone(final IABox<I, L, K, R> newABox);
/// </editor-fold>


	@Override
	public ABox<I, L, K, R> getABox()
	{
		return _abox;
	}

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
		return toString(String.valueOf(fill));
	}


	@Override
	public int deepHashCode()
	{
		int hashcode = 147;
		hashcode += SetUtils.hashCodeForSet(getTerms());
		hashcode += _raBox.deepHashCode();
		return hashcode;
	}


	@Override
	public boolean deepEquals(final Object obj)
	{
		if (obj instanceof ABoxNode) {
			@SuppressWarnings("unchecked")
			final ABoxNode<?, I, L, K, R> other = (ABoxNode<?, I, L, K, R>) obj;
			if (!SetUtils.isEqualSet(getTerms(), other.getTerms())) {
				return false;
			}
			if (!_raBox.deepEquals(other.getRABox())) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}
/// </editor-fold>


	@Override
	public IABoxNode<I, L, K, R> getImmutable()
	{
		// XXX - this may cause problems */
		return getABox().getImmutable().getNode(getNodeID());
	}


	/**
	 *
	 * @return The {@link NodeID} of the current node.
	 */
	@Override
	public NodeID getNodeID()
	{
		return _id;
	}


	/**
	 *
	 * @return {@literal true} if the current node is a datatype node and cannot have successors.
	 */
	@Override
	public boolean isDatatypeNode()
	{
		return _isDatatypeNode;
	}


	@Override
	public boolean isAnonymous()
	{
		return _names.isEmpty();
	}


	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj) {
			return true;
		}
		if ((obj instanceof IABoxNode) && (obj.getClass().isInstance(this))) {
			IABoxNode<?, ?, ?, ?> other = (IABoxNode<?, ?, ?, ?>) obj;
			return getNodeID().equals(other.getNodeID());
		} else {
			return false;
		}
	}


	@Override
	public int hashCode()
	{
		return _id.hashCode();
	}


	@Override
	public int compareTo(final IABoxNode<I, L, K, R> o)
	{
		assert o != null;
		if (isAnonymous()) {
			/* this is anonymous */
			if (o.isAnonymous()) {
				return getNodeID().compareTo(o.getNodeID());
			} else {
				return 1;
			}
		} else {
			/* this is named */
			if (o.isAnonymous()) {
				return -1;
			} else {
				/* Since the primary name is subject to change, we compare by ID here, too */
				return getNodeID().compareTo(o.getNodeID());
			}
		}
	}


	@Override
	public String toString(final String prefix)
	{
		final StringBuilder sb = new StringBuilder(3 * (getTerms().size() + getNames().size()));
		if (this instanceof IDatatypeABoxNode) {
			sb.append("d");
		}
		sb.append(getNodeID().toString());
		sb.append(": names: ");
		sb.append(getNames().toString());
		sb.append(", terms: ");
		sb.append(getTerms().toString());
//		if (isBlocked()) {
//			sb.append(" (blocked by ");
//			sb.append(getBlocker().getNodeID().toString());
//			sb.append(")");
//		}
		sb.append("\n");
		sb.append(prefix);
		sb.append("\tlinks: [");
		for (Map.Entry<R, Collection<NodeID>> sEntry : getRABox().getAssertedSuccessors().entrySet()) {
			sb.append("(");
			final R role = sEntry.getKey();
			sb.append(role);

			sb.append(" -> ");
			sb.append(sEntry.getValue().toString());
			sb.append(")");
		}

		sb.append("]\n");
		return sb.toString();
	}

	/// <editor-fold defaultstate="collapsed" desc="lazy unfolding">

	/**
	 * Try to add {@literal desc} to the termset of the current node.
	 * < p/>
	 * This checks if the supplied term is of the proper type for the current node and dispatches to {@link IDatatypeABoxNode#addDataTerm(de.uniba.wiai.kinf.pw.projects.lillytab.terms.datatype.IDLDataRange)
	 * }
	 * and {@link IIndividualABoxNode#addDataTerm(de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression) ), as appropriate.
	 * <p /> 	 {@link EInconsistencyException} is raised, when the term is not of the appropriate type.
	 *
	 * @param desc
	 * @return The {@link NodeMergeInfo} resulting from adding the supplied term.
	 * @throws ENodeMergeException A node merge was required and could not
	 * @throws EIllegalTermTypeException The supplied term was not of the appropriate type for the target node.
	 *
	 *
	 */
	@Override
	public NodeMergeInfo<I, L, K, R> addTerm(final IDLRestriction<I, L, K, R> desc)
		throws ENodeMergeException, EIllegalTermTypeException
	{
		if (this instanceof IDatatypeABoxNode) {
			if (desc instanceof IDLDataRange) {
				final IDLDataRange<I, L, K, R> dataExp = (IDLDataRange<I, L, K, R>) desc;
				final IDatatypeABoxNode<I, L, K, R> thisNode = (IDatatypeABoxNode<I, L, K, R>) this;
				return thisNode.addDataTerm(dataExp);
			} else
				throw new EIllegalTermTypeException(desc, String.format(
					"Can only add data expression terms to datatype nodes, got %s",
					desc));
		} else {
			if (desc instanceof IDLClassExpression) {
				final IDLClassExpression<I, L, K, R> classExp = (IDLClassExpression<I, L, K, R>) desc;
				final IIndividualABoxNode<I, L, K, R> thisNode = (IIndividualABoxNode<I, L, K, R>) this;
				return thisNode.addClassTerm(classExp);
			} else
				throw new EIllegalTermTypeException(desc, String.format(
					"Can only add class expression terms to individual nodes, got %s",
					desc));
		}
	}


	@Override
	public NodeMergeInfo<I, L, K, R> addTerms(
		Collection<? extends IDLRestriction<I, L, K, R>> terms)
		throws ENodeMergeException, EIllegalTermTypeException
	{
		final NodeMergeInfo<I, L, K, R> mergeInfo = new NodeMergeInfo<>(this, false);
		for (IDLRestriction<I, L, K, R> term : terms) {
			final NodeMergeInfo<I, L, K, R> newMergeInfo = mergeInfo.getCurrentNode().addTerm(term);
			mergeInfo.append(newMergeInfo);
		}
		return mergeInfo;
	}
}
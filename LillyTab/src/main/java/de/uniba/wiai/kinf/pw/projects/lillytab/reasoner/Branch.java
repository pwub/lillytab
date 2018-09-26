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

import de.dhke.projects.cutil.collections.map.TransitiveHashMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.INodeMergeListener;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLIndividualReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;


/**
 * A branch represents a specific state in the reasoning process. It contains a (partially expanded) ABox as well as
 * various information needed to continue with the reasoning process. <p /> Branches do support
 * {@link Object#clone()}ing, making it possible to open up a secondary decision path (i.e. a new branch). <p />
 * Branches maintain two node queues: The non-generating node queue available via {@link #getNonGeneratingQueue()} and
 * the non-generating node queue {@link #getGeneratingQueue()}. <p /> This support is specified to {@link Reasoner}, as
 * the implemented algorithm first applies all non-generating rules (that do not generate a new node} and only if there
 * are no more applicable non-generating rules, start applying generating rules.
 * <p /> Both queues are sorted by the natural node order. The next node on each queue is available via {@link #nextNonGeneratingNode()
 * } and {@link #nextGeneratingNode()
 * }, respectively. Nodes may (re-) added to both queues at once via the null {@link #touchLiteral(java.lang.Comparable) }, {@link #touch(de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID) },
 * {@link #touchAll(java.util.Collection) },
 * {@link #touchNode(de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode)}, and {@link #touchNodes(java.util.Collection)
 * } methods.
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public class Branch<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	implements Cloneable // Comparable<Branch<I, L, K, R>>
{
	private static final boolean TO_STRING_ID_ONLY = false;
	/// </editor-fold>
	private IABox<I, L, K, R> _abox;
	/*
	 * node queues
	 */
	private final NodeMergeListener _mergeListener = new NodeMergeListener();
	private Map<NodeID, NodeID> _mergeMap = null;
	private ConsistencyInfo<I, L, K, R> _consistencyInfo;

	public Branch(final IABox<I, L, K, R> abox, final boolean enableMergeTracking)
	{
		if (enableMergeTracking) {
			_mergeMap = new TransitiveHashMap<>();
		}

		setABox(abox);

		_consistencyInfo = new ConsistencyInfo<>();
	}

	private Branch(final IABox<I, L, K, R> abox, final Map<NodeID, NodeID> mergeMap)
	{
		if (mergeMap != null) {
			_mergeMap = new TransitiveHashMap<>(mergeMap);
		}

		setABox(abox);

		_consistencyInfo = new ConsistencyInfo<>();
	}

	public ConsistencyInfo<I, L, K, R> getConsistencyInfo()
	{
		return _consistencyInfo;
	}

	public ConsistencyInfo<I, L, K, R> upgradeConsistencyInfo(final ConsistencyInfo<I, L, K, R> cInfo)
	{
		_consistencyInfo = _consistencyInfo.updateFrom(cInfo);
		return _consistencyInfo;
	}

	/**
	 * @return The branch's {@link ABox}.
	 */
	public IABox<I, L, K, R> getABox()
	{
		return _abox;
	}

	public ReasonerResult<I, L, K, R> dispose()
	{
		final IABox<I, L, K, R> abox = getABox();

		final Map<NodeID, NodeID> mergeMap = _mergeMap;
		if (_mergeMap != null) {
			boolean removed = _abox.getNodeMergeListeners().remove(_mergeListener);
			assert removed;
			_mergeMap = null;

		}
		assert !_abox.getNodeMergeListeners().contains(_mergeListener);
		setABox(null);

		return ReasonerResult.create(abox, mergeMap);
	}

	@Override
	public Branch<I, L, K, R> clone()
	{
		/*
		 * create clone
		 */
		final IABox<I, L, K, R> aboxClone = cloneABox();

		final Branch<I, L, K, R> klone = new Branch<>(aboxClone, _mergeMap);

		return klone;
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="toString()">
	@Override
	public String toString()
	{
		return toString("");
	}

	public String toString(int indent)
	{
		char[] fill = new char[indent];
		Arrays.fill(fill, ' ');
		return new String(fill);
	}

	public String toString(String prefix)
	{
		if (TO_STRING_ID_ONLY) {
			if (getABox() != null) {
				return getABox().toString();
			} else {
				return "(null ABox)";
			}
		} else {
			final StringBuilder sb = new StringBuilder();
			sb.append(prefix);
			sb.append("Branch:\n");
			final String subPrefix = prefix + "\t";
			sb.append(subPrefix);
			if (_abox != null) {
				sb.append(_abox.toString());
			}
			sb.append("\n");
			sb.append(subPrefix);
			sb.append("\n");
			sb.append(subPrefix);
			sb.append("\n");
			sb.append(subPrefix);
			return sb.toString();
		}
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="Merge tracking">
	public Map<NodeID, NodeID> getMergeMap()
	{
		return Collections.unmodifiableMap(_mergeMap);
	}
	/// </editor-fold>

	/**
	 * Update the ABox of the current branch and update the branch-specified listeners of the ABoxes, if appropriate.
	 *
	 * @param abox The new ABox
	 */
	private void setABox(final IABox<I, L, K, R> abox)
	{
		if (abox != _abox) {
			if (_abox != null) {
				if (_mergeMap != null) {
					assert _mergeListener != null;
					_abox.getNodeMergeListeners().remove(_mergeListener);
				}
				assert !_abox.getNodeMergeListeners().contains(_mergeListener);
			}
			if (abox != null) {
				/*
				 * add node merge listener only if merge tracking is enabled
				 */
				if (_mergeMap != null) {
					abox.getNodeMergeListeners().add(_mergeListener);
				}
			}
			_abox = abox;
		}
	}

	/// <editor-fold defaultstate="collapsed" desc="Cloneable">
	private IABox<I, L, K, R> cloneABox()
	{
		if (_abox != null) {
			if (_mergeMap != null) {
				_abox.getNodeMergeListeners().remove(_mergeListener);
			};
			final IABox<I, L, K, R> aboxClone = _abox.clone();
			_abox.getNodeMergeListeners().add(_mergeListener);
			return aboxClone;
		} else {
			return null;
		}
	}

	/// <editor-fold defaultstate="collaped" desc="class NodeMergeListener">

	final class NodeMergeListener
		implements INodeMergeListener<I, L, K, R> {
		@Override
		public void beforeNodeMerge(IABoxNode<I, L, K, R> source,
									IABoxNode<I, L, K, R> target)
		{
			assert _mergeMap != null;
			_mergeMap.put(source.getNodeID(), target.getNodeID());
		}
	}
	/// </editor-fold>
}

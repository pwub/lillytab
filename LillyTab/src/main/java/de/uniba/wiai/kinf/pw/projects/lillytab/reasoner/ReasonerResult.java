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

import de.uniba.wiai.kinf.pw.projects.lillytab.IReasonerResult;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import java.util.Collections;
import java.util.Map;

/**
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public class ReasonerResult<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	implements IReasonerResult<I, L, K, R> {
	private final IABox<I, L, K, R> _abox;
	private final Map<NodeID, NodeID> _mergeMap;


	protected ReasonerResult(final IABox<I, L, K, R> abox, final Map<NodeID, NodeID> mergeMap)
	{
		_abox = abox;
		_mergeMap = mergeMap;
	}


		public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> ReasonerResult<I, L, K, R> create(final IABox<I, L, K, R> abox, final Map<NodeID, NodeID> mergeMap)
	{
		return new ReasonerResult<>(abox, mergeMap);
	}


		public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> ReasonerResult<I, L, K, R> create(final IABox<I, L, K, R> abox)
	{
		return new ReasonerResult<>(abox, null);
	}


	@Override
	public IABox<I, L, K, R> getABox()
	{
		return _abox;
	}


	@Override
	public Map<NodeID, NodeID> getMergeMap()
	{
		return Collections.unmodifiableMap(_mergeMap);
	}


	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		} else if (obj instanceof IReasonerResult) {
			final IReasonerResult<?, ?, ?, ?> other = (IReasonerResult<?, ?, ?, ?>) obj;
			return _abox.equals(other.getABox());
		} else {
			return false;
		}
	}


	@Override
	public int hashCode()
	{
		return _abox.hashCode();
	}


	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(_abox);
		return sb.toString();
	}


	public boolean deepEquals(Object obj)
	{
		if (this == obj) {
			return true;
		} else if (obj instanceof IReasonerResult) {
			final IReasonerResult<?, ?, ?, ?> other = (IReasonerResult<?, ?, ?, ?>) obj;
			return _abox.deepEquals(other.getABox());
		} else {
			return false;
		}
	}


	public int deepHashCode()
	{
		return _abox.deepHashCode();
	}
}

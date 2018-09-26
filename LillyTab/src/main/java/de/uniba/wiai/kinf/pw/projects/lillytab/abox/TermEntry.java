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
package de.uniba.wiai.kinf.pw.projects.lillytab.abox;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import java.io.Serializable;

/**
 * Wrapper class combining a {@link NodeID} with a {@link IDLTerm}. This is mostly useful for dependency tracking.
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public final class TermEntry<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	implements Serializable, Comparable<TermEntry<I, L, K, R>> {
	
	private static final long serialVersionUID = 5144437688016123689L;
	private final NodeID _nodeID;
	private final IDLTerm<I, L, K, R> _term;
	
	
	protected TermEntry(NodeID nodeID, IDLTerm<I, L, K, R> term)
	{
		this._nodeID = nodeID;
		this._term = term;
	}
	
	
	public NodeID getNodeID()
	{
		return _nodeID;
	}
	
	
	public IDLTerm<I, L, K, R> getTerm()
	{
		return _term;
	}
	
	
	@Override
	public int hashCode()
	{
		return 1408217 + 346421 * _nodeID.hashCode() + _term.hashCode();
	}
	
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		} else if (obj instanceof TermEntry) {
			TermEntry<?, ?, ?, ?> other = (TermEntry<?, ?, ?, ?>) obj;
			return _nodeID.equals(other._nodeID) && _term.equals(other._term);
		} else {
			return false;
		}
	}
	
	
	@Override
	public int compareTo(TermEntry<I, L, K, R> o)
	{
		int c = _nodeID.compareTo(o._nodeID);
		if (c == 0) {
			c = _term.compareTo(o._term);
		}
		return c;
	}
	
	
	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("<");
		sb.append(getNodeID());
		sb.append(", ");
		sb.append(getTerm());
		sb.append(">");
		return sb.toString();
	}
	
	
	static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> TermEntry<I, L, K, R> wrap(
		final NodeID nodeID, final IDLTerm<I, L, K, R> term)
	{
		return new TermEntry<>(nodeID, term);
	}
	
	
	static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> TermEntry<I, L, K, R> wrap(
		final IABoxNode<?, ?, ?, ?> node, final IDLTerm<I, L, K, R> term)
	{
		return wrap(node.getNodeID(), term);
	}
}

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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.immutable;

import de.dhke.projects.cutil.collections.immutable.ImmutableMultiMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ILinkMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import java.util.Collection;


/**
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 *
 */
public class ImmutableLinkMap<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends ImmutableMultiMap<R, NodeID>
	implements ILinkMap<I, L, K, R> {

	private final IABoxNode<I, L, K, R> _node;

	protected ImmutableLinkMap(final ILinkMap<I, L, K, R> baseMap, final IABoxNode<I, L, K, R> node)
	{
		super(baseMap, null, null);
		_node = node;
	}

	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> ImmutableLinkMap<I, L, K, R> decorate(
		final ILinkMap<I, L, K, R> baseMap, final IABoxNode<I, L, K, R> node)
	{
		return new ImmutableLinkMap<>(baseMap, node);
	}

	@Override
	public IABoxNode<I, L, K, R> getNode()
	{
		return _node;
	}

	@Override
	public NodeID put(R role,
					  IABoxNode<I, L, K, R> node)
	{
		throw new UnsupportedOperationException("Cannot modify Immutable link map");
	}

	@Override
	public boolean putAll(R key,
						  Collection<? extends IABoxNode<I, L, K, R>> values)
	{
		throw new UnsupportedOperationException("Cannot modify Immutable link map");
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		} else if (obj instanceof ImmutableLinkMap) {
			final ImmutableLinkMap<?, ?, ?, ?> other = (ImmutableLinkMap<?, ?, ?, ?>) obj;
			return getDecoratee().equals(other.getDecoratee());
		} else {
			return getDecoratee().equals(obj);
		}
	}

	@Override
	public int hashCode()
	{
		return getDecoratee().hashCode();
	}

}

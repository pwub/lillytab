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

import de.dhke.projects.cutil.IDecorator;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IRABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ITermSet;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.EIllegalTermTypeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLNodeTerm;
import java.util.Collection;
import java.util.Collections;


/**
 *
 * A proxy object to an {@link IABoxNode} that forbids changes to the underlying node.
 * <p />
 * If an immutable is first created and the underlying node is modified, afterwards, behaviour of the immutable is
 * undefined.
 *
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public abstract class ImmutableABoxNode<N extends Comparable<? super N>, I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	implements IABoxNode<I, L, K, R>, IDecorator<IABoxNode<I, L, K, R>> {

	private final IABoxNode<I, L, K, R> _baseNode;
	private final IABox<I, L, K, R> _abox;
	private int _deepHashCode = 0;
	private int _hashCode = 0;

	ImmutableABoxNode(final IABoxNode<I, L, K, R> baseNode, final IABox<I, L, K, R> abox)
	{
		_baseNode = baseNode;
		_abox = abox;
	}

	@Override
	public Collection<TermEntry<I, L, K, R>> getTermEntries()
	{
		return Collections.unmodifiableCollection(_baseNode.getTermEntries());
	}

	@Override
	public NodeID getNodeID()
	{
		return _baseNode.getNodeID();
	}

	@Override
	public boolean isAnonymous()
	{
		return _baseNode.isAnonymous();
	}

	@Override
	public ITermSet<I, L, K, R> getTerms()
	{
		return _baseNode.getTerms().getImmutable();
	}

	@Override
	public IABoxNode<I, L, K, R> clone(IABox<I, L, K, R> newABox)
	{
		return _baseNode.clone(newABox);
	}

	@Override
	public IABox<I, L, K, R> getABox()
	{
		return _abox;
	}

	public void setABox(final IABox<I, L, K, R> abox)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABoxNode.");
	}

	@Override
	public int deepHashCode()
	{
		if (_deepHashCode == 0) {
			_deepHashCode = _baseNode.deepHashCode();
		}
		return _deepHashCode;
	}

	@Override
	public boolean deepEquals(Object obj)
	{
		return _baseNode.deepEquals(obj);
	}

	@Override
	public boolean isDatatypeNode()
	{
		return _baseNode.isDatatypeNode();
	}

	@Override
	public String toString(String prefix)
	{
		return _baseNode.toString(prefix);
	}

	@Override
	public String toString()
	{
		return _baseNode.toString();
	}

	@Override
	public int compareTo(IABoxNode<I, L, K, R> o)
	{
		return _baseNode.compareTo(o);
	}

	@Override
	public IABoxNode<I, L, K, R> getImmutable()
	{
		return this;
	}

	@Override
	public IRABox<I, L, K, R> getRABox()
	{
		return ImmutableRABox.decorate(this, _baseNode.getRABox());
	}

	@Override
	public NodeMergeInfo<I, L, K, R> addTerm(IDLNodeTerm<I, L, K, R> term) throws ENodeMergeException, EIllegalTermTypeException
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABoxNode.");
	}

	@Override
	public NodeMergeInfo<I, L, K, R> addTerms(
		Collection<? extends IDLNodeTerm<I, L, K, R>> terms)
		throws ENodeMergeException, EIllegalTermTypeException
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABoxNode.");
	}

	protected IABoxNode<I, L, K, R> getBaseNode()
	{
		return _baseNode;
	}

	@Override
	public int hashCode()
	{
		if (_hashCode == 0) {
			_hashCode = _baseNode.hashCode();
		}
		return _hashCode;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		} else if (obj instanceof ImmutableABoxNode) {
			final ImmutableABoxNode<?, ?, ?, ?, ?> other = (ImmutableABoxNode<?, ?, ?, ?, ?>) obj;
			return getDecoratee().equals(other.getDecoratee());
		} else {
			return _baseNode.equals(obj);
		}
	}

	@Override
	public IABoxNode<I, L, K, R> getDecoratee()
	{
		return _baseNode;
	}

}

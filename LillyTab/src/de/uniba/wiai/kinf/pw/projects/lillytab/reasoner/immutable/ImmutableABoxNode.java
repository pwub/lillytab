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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.immutable;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IRABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ITermSet;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;

/**
 * <p>
 * A proxy object to an {@link IABoxNode} that forbids changes to the underlying node.
 * </p><p>
 * If an immutable is first created and the underlying node is modified, afterwards, behaviour of the immutable is undefined.
 * </p>
 *
 * @param <Name> The type for nominals and values
 * @param <Klass> The type for DL classes
 * @param <Role> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ImmutableABoxNode<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	implements IABoxNode<Name, Klass, Role>
{
	private final IABoxNode<Name, Klass, Role> _baseNode;
	private final IABox<Name, Klass, Role> _abox;

	ImmutableABoxNode(final IABoxNode<Name, Klass, Role> baseNode, final IABox<Name, Klass, Role> abox)
	{
		_baseNode = baseNode;
		_abox = abox;
	}

	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
		ImmutableABoxNode<Name, Klass, Role> decorate(final IABoxNode<Name, Klass, Role> baseNode, final IABox<Name, Klass, Role> abox)
	{
		return new ImmutableABoxNode<Name, Klass, Role>(baseNode, abox);
	}


	public Collection<TermEntry<Name, Klass, Role>> getTermEntries()
	{
		return Collections.unmodifiableCollection(_baseNode.getTermEntries());
	}

	protected IABoxNode<Name, Klass, Role> getBaseNode()
	{
		return _baseNode;
	}

	public NodeID getNodeID()
	{
		return _baseNode.getNodeID();
	}

	public SortedSet<Name> getNames()
	{
		return _baseNode.getNames();
	}

	public Name getPrimaryName()
	{
		return _baseNode.getPrimaryName();
	}

	public boolean isAnonymous()
	{
		return _baseNode.isAnonymous();
	}

	public ITermSet<Name, Klass, Role> getTerms()
	{
		return _baseNode.getTerms().getImmutable();
	}

	public NodeMergeInfo<Name, Klass, Role> addUnfoldedDescription(IDLRestriction<Name, Klass, Role> desc)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABoxNode.");
	}

	public NodeMergeInfo<Name, Klass, Role> addUnfoldedDescriptions(Iterable<? extends IDLRestriction<Name, Klass, Role>> descs)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABoxNode.");
	}

	public IABoxNode<Name, Klass, Role> clone(IABox<Name, Klass, Role> newABox)
	{
		return _baseNode.clone(newABox);
	}

	public IABox<Name, Klass, Role> getABox()
	{
		return _abox;
	}

	public void setABox(final IABox<Name, Klass, Role> abox)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABoxNode.");
	}

	public NodeMergeInfo<Name, Klass, Role> unfoldAll()
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABoxNode.");
	}

	private int _hashCode = 0;
	public int deepHashCode()
	{
		if (_hashCode == 0)
			_hashCode = _baseNode.deepHashCode();
		return _hashCode;
	}

	public boolean deepEquals(Object obj)
	{
		return _baseNode.deepEquals(obj);
	}

	public boolean isDatatypeNode()
	{
		return _baseNode.isDatatypeNode();
	}

	public String toString(String prefix)
	{
		return _baseNode.toString(prefix);
	}

	public int compareTo(IABoxNode<Name, Klass, Role> o)
	{
		return _baseNode.compareTo(o);
	}

	public IABoxNode<Name, Klass, Role> getImmutable()
	{
		return this;
	}


	public IRABox<Name, Klass, Role> getRABox()
	{
		return ImmutableRABox.decorate(_baseNode.getRABox());
	}
}

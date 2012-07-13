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

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ABoxNodeTermEntryCollection<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends AbstractCollection<TermEntry<Name, Klass, Role>>
	implements Collection<TermEntry<Name, Klass, Role>> {

	private final IABoxNode<Name, Klass, Role> _node;

	class Itr
		implements Iterator<TermEntry<Name, Klass, Role>> {

		private final Iterator<IDLTerm<Name, Klass, Role>> _baseIter;


		public Itr()
		{
			_baseIter = _node.getTerms().iterator();
		}


		public boolean hasNext()
		{
			return _baseIter.hasNext();
		}


		public TermEntry<Name, Klass, Role> next()
		{
			final IDLTerm<Name, Klass, Role> term = _baseIter.next();
			final TermEntry<Name, Klass, Role> entry = _node.getABox().getTermEntryFactory().getEntry(_node, term);
			return entry;
		}


		public void remove()
		{
			_baseIter.remove();
		}
	}


	public ABoxNodeTermEntryCollection(final IABoxNode<Name, Klass, Role> node)
	{
		assert node.getABox() != null;
		_node = node;
	}


	@Override
	public Iterator<TermEntry<Name, Klass, Role>> iterator()
	{
		return new Itr();
	}


	@Override
	public int size()
	{
		return _node.getTerms().size();
	}


	@Override
	public boolean contains(Object o)
	{
		if (o == null)
			return false;
		else if (o instanceof TermEntry) {
			final TermEntry<?, ?, ?> other = (TermEntry<?, ?, ?>)o;
			return _node.getNodeID().equals(other.getNodeID()) && _node.getTerms().contains(other.getTerm());
		} else
			return false;
	}


	@Override
	public boolean remove(Object o)
	{
		if ((o != null) && (o instanceof TermEntry)) {
			final TermEntry<?, ?, ?> other = (TermEntry<?, ?, ?>)o;
			if (_node.getNodeID().equals(other.getNodeID())) {
				return _node.getTerms().remove(other.getTerm());
			} else 
				return false;
		} else
			return false;
	}
	
	@Override
	public boolean add(TermEntry<Name, Klass, Role> e)
	{
		if (_node.getNodeID().equals(e.getNodeID())) {
			return _node.getTerms().add(e.getTerm());
		} else
			throw new IllegalArgumentException(
				String.format("TermEntry node ID (%s) does not match target node ID (%s)", e.getNodeID(), _node.getNodeID()));
	}
	
	@Override
	public void clear()
	{
		_node.getTerms().clear();
	}
}

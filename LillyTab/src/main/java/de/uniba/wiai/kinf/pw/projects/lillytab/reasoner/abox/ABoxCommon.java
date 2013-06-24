/**
 * (c) 2009-2013 Otto-Friedrich-University Bamberg
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

import de.dhke.projects.cutil.collections.factories.HashMapFactory;
import de.dhke.projects.cutil.collections.factories.ICollectionFactory;
import de.dhke.projects.cutil.collections.factories.IMapFactory;
import de.dhke.projects.cutil.collections.factories.TreeSetFactory;
import de.dhke.projects.cutil.collections.factories.TreeSortedSetFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntryFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.util.LinearSequenceNumberGenerator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 * Wrapper class for various shared objects.
 *
 * This is not copied, but referenced during cloning, conserving memory.
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 * 
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public final class ABoxCommon<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>  {
	private final LinearSequenceNumberGenerator _aboxIDFactory = new LinearSequenceNumberGenerator();
	private final IDLTermFactory<I, L, K, R> _termFactory;
	private final ICollectionFactory<NodeID, Set<NodeID>> _nodeIDSetFactory = new TreeSetFactory<>();
	private final ICollectionFactory<IABoxNode<I, L, K, R>, SortedSet<IABoxNode<I, L, K, R>>> _nodeSetFactory = new TreeSortedSetFactory<>();
	private final IMapFactory<Object, IABoxNode<I, L, K, R>, Map<Object, IABoxNode<I, L, K, R>>> _nodeMapFactory = new HashMapFactory<>();
	private final TermEntryFactory<I, L, K, R> _termEntryFactory = new TermEntryFactory<>();

	public ABoxCommon(IDLTermFactory<I, L, K, R> termFactory)
	{
		this._termFactory = termFactory;
	}


	/**
	 * @return the aboxIDFactory
	 */
	public LinearSequenceNumberGenerator getAboxIDFactory()
	{
		return _aboxIDFactory;
	}


	/**
	 * @return the termFactory
	 */
	public IDLTermFactory<I, L, K, R> getTermFactory()
	{
		return _termFactory;
	}


	/**
	 * @return the nodeIDSetFactory
	 */
	public ICollectionFactory<NodeID, Set<NodeID>> getNodeIDSetFactory()
	{
		return _nodeIDSetFactory;
	}


	/**
	 * @return the nodeSetFactory
	 */
	public ICollectionFactory<IABoxNode<I, L, K, R>, SortedSet<IABoxNode<I, L, K, R>>> getNodeSetFactory()
	{
		return _nodeSetFactory;
	}


	/**
	 * @return the nodeMapFactory
	 */
	public IMapFactory<Object, IABoxNode<I, L, K, R>, Map<Object, IABoxNode<I, L, K, R>>> getNodeMapFactory()
	{
		return _nodeMapFactory;
	}


	public TermEntryFactory<I, L, K, R> getTermEntryFactory()
	{
		return _termEntryFactory;
	}
}

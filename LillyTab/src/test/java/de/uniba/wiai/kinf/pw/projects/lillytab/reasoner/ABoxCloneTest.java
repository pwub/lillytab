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

import de.dhke.projects.cutil.Pair;
import de.dhke.projects.cutil.collections.CollectionUtil;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentABoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.INodeMergeListener;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IUnfoldListener;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLIndividualReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleKRSSParser;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleStringDLTermFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public class ABoxCloneTest {
	private static IABoxFactory<String, String, String, String> _aboxFactory;
	private static SimpleKRSSParser _parser;
	private IABox<String, String, String, String> _abox;


	public ABoxCloneTest()
	{
	}


	@BeforeClass
	public static void setUpClass() throws Exception
	{
		final IDLTermFactory<String, String, String, String> termFactory = new SimpleStringDLTermFactory();
		_aboxFactory = new ABoxFactory<>(termFactory);
		_parser = new SimpleKRSSParser(_aboxFactory.getDLTermFactory());
	}


	@AfterClass
	public static void tearDownClass() throws Exception
	{
		_aboxFactory = null;
		_parser = null;
	}


	@Before
	public void setUp()
	{
		_abox = _aboxFactory.createABox();
	}


	@After
	public void tearDown()
	{
		_abox = null;
	}


	@Test
	public void testNodeClone()
		throws EInconsistentABoxException
	{
		final IABoxNode<String, String, String, String> node = _abox.createNode(false);

		final IABox<String, String, String, String> klone = _abox.clone();

		assertEquals(1, _abox.size());
		assertEquals(1, klone.size());
		assertNotNull(_abox.getNode(node.getNodeID()));
		assertNotNull(klone.getNode(node.getNodeID()));
	}


	@Test
	public void testNodeCloneChange()
		throws EInconsistencyException
	{
		final IABoxNode<String, String, String, String> node = _abox.createNode(false);
		final IABox<String, String, String, String> klone = _abox.clone();
		/*
		 * add a second node to the clone
		 */
		final IABoxNode<String, String, String, String> node2 = klone.getOrAddIndividualNode("clone");

		assertEquals(1, _abox.size());
		assertEquals(2, klone.size());

		assertNotNull(_abox.getNode(node.getNodeID()));
		assertNull(_abox.getNode(node2.getNodeID()));
		assertNotNull(klone.getNode(node.getNodeID()));
		assertNotNull(klone.getNode(node2.getNodeID()));
	}


	@Test
	public void testTermClone()
		throws EInconsistentABoxException
	{
		final IABoxNode<String, String, String, String> node = _abox.createNode(false);
		final IDLClassReference<String, String, String, String> A = _aboxFactory.getDLTermFactory().getDLClassReference("A");
		node.addTerm(A);

		final IABox<String, String, String, String> klone = _abox.clone();
		final IABoxNode<String, String, String, String> kloneNode = klone.getNode(node.getNodeID());

		assertArrayEquals(node.getTerms().toArray(), kloneNode.getTerms().toArray());
	}


	@Test
	public void testTermCloneChange()
		throws EInconsistentABoxException
	{
		final IABoxNode<String, String, String, String> node = _abox.createNode(false);
		final IDLClassReference<String, String, String, String> A = _aboxFactory.getDLTermFactory().getDLClassReference("A");
		node.addTerm(A);

		final IABox<String, String, String, String> klone = _abox.clone();
		final IABoxNode<String, String, String, String> kloneNode = klone.getNode(node.getNodeID());
		final IDLClassReference<String, String, String, String> B = _aboxFactory.getDLTermFactory().getDLClassReference("B");
		kloneNode.addTerm(B);

		assertTrue(node.getTerms().contains(A));
		assertFalse(node.getTerms().contains(B));

		assertTrue(kloneNode.getTerms().contains(A));
		assertTrue(kloneNode.getTerms().contains(B));
	}


	@Test
	public void testLinkClone()
		throws EInconsistentABoxException
	{
		final IABoxNode<String, String, String, String> node0 = _abox.createNode(false);
		final IABoxNode<String, String, String, String> node1 = _abox.createNode(false);
		node0.getRABox().getAssertedSuccessors().put("r", node1.getNodeID());

		final IABox<String, String, String, String> klone = _abox.clone();
		final IABoxNode<String, String, String, String> kloneNode0 = klone.getNode(node0.getNodeID());
		final IABoxNode<String, String, String, String> kloneNode1 = klone.getNode(node0.getNodeID());

		assertArrayEquals(node0.getRABox().getAssertedSuccessors().values().toArray(), kloneNode0.getRABox().
			getAssertedSuccessors().values().toArray());
	}


	@Test
	public void testLinkCloneChange()
		throws EInconsistentABoxException
	{
		final IABoxNode<String, String, String, String> node0 = _abox.createNode(false);
		final IABoxNode<String, String, String, String> node1 = _abox.createNode(false);
		node0.getRABox().getAssertedSuccessors().put("r1", node1.getNodeID());

		final IABox<String, String, String, String> klone = _abox.clone();
		final IABoxNode<String, String, String, String> kloneNode0 = klone.getNode(node0.getNodeID());
		final IABoxNode<String, String, String, String> kloneNode1 = klone.getNode(node1.getNodeID());
		kloneNode0.getRABox().getAssertedSuccessors().put("r2", node1.getNodeID());

		assertArrayEquals(new Object[]{kloneNode1.getNodeID()}, kloneNode0.getRABox().getAssertedSuccessors().get("r1").
			toArray());
		assertArrayEquals(new Object[]{kloneNode1.getNodeID()}, kloneNode0.getRABox().getAssertedSuccessors().get("r2").
			toArray());
	}


	@Test
	public void testNodeMergeListenerClone()
		throws EInconsistencyException
	{
		final HistoryNodeMergeListener nmListener = new HistoryNodeMergeListener();
		assert nmListener != null;
		_abox.getNodeMergeListeners().add(nmListener);

		final IDLIndividualReference<String, String, String, String> a = _aboxFactory.getDLTermFactory().getDLIndividualReference("a");

		final IABoxNode<String, String, String, String> node0 = _abox.getOrAddIndividualNode("a");
		final IABoxNode<String, String, String, String> node1 = _abox.createNode(false);

		final IABox<String, String, String, String> klone = _abox.clone();
		final IABoxNode<String, String, String, String> kloneNode0 = klone.getNode(node0.getNodeID());
		final IABoxNode<String, String, String, String> kloneNode1 = klone.getNode(node1.getNodeID());
		kloneNode0.getRABox().getAssertedSuccessors().put("r2", node1.getNodeID());

		/*
		 * force a merge
		 */
		kloneNode1.addTerm(a);

		assertTrue(klone.getNodeMergeListeners().isEmpty());
		assertFalse(klone.getNodeMergeListeners().contains(nmListener));

		assertTrue(nmListener.merges.isEmpty());
		assertFalse(nmListener.merges.contains(Pair.wrap(kloneNode1, kloneNode0)));
	}


	@Test
	public void testNodeMergeListenerCloneChange()
		throws EInconsistencyException
	{
		final IDLIndividualReference<String, String, String, String> a = _aboxFactory.getDLTermFactory().getDLIndividualReference("a");

		final IABoxNode<String, String, String, String> node0 = _abox.getOrAddIndividualNode("a");
		final IABoxNode<String, String, String, String> node1 = _abox.createNode(false);

		final IABox<String, String, String, String> klone = _abox.clone();
		final IABoxNode<String, String, String, String> kloneNode0 = klone.getNode(node0.getNodeID());
		final IABoxNode<String, String, String, String> kloneNode1 = klone.getNode(node1.getNodeID());
		kloneNode0.getRABox().getAssertedSuccessors().put("r2", node1.getNodeID());

		final HistoryNodeMergeListener nmListener = new HistoryNodeMergeListener();
		assert nmListener != null;
		klone.getNodeMergeListeners().add(nmListener);

		assertTrue(_abox.getNodeMergeListeners().isEmpty());
		assertTrue(nmListener.merges.isEmpty());

		assertEquals(1, klone.getNodeMergeListeners().size());
		assertTrue(klone.getNodeMergeListeners().contains(nmListener));

		/*
		 * force a merge in the initial abox
		 */
		node1.addTerm(a);

		assertTrue(nmListener.merges.isEmpty());

		kloneNode1.addTerm(a);

		assertEquals(1, nmListener.merges.size());
		assertTrue(nmListener.merges.contains(Pair.wrap(kloneNode1, kloneNode0)));
	}
	//	@Test
	
	static class HistoryNodeMergeListener
		implements INodeMergeListener<String, String, String, String> {

		public List<Pair<IABoxNode<String, String, String, String>, IABoxNode<String, String, String, String>>> merges = new ArrayList<>();


		@Override
		public void beforeNodeMerge(IABoxNode<String, String, String, String> source,
									IABoxNode<String, String, String, String> target)
		{
			merges.add(Pair.wrap(source, target));
		}
	}

	static class HistoryUnfoldListener
		implements IUnfoldListener<String, String, String, String> {
		public List<UnfoldInfo> unfoldInfos = new ArrayList<>();


		@Override
		public void beforeConceptUnfold(IABoxNode<String, String, String, String> node,
										IDLClassExpression<String, String, String, String> initial,
										Collection<IDLClassExpression<String, String, String, String>> unfolds)
		{
			final UnfoldInfo unfoldInfo = new UnfoldInfo(node, initial, unfolds);
			unfoldInfos.add(unfoldInfo);
		}

		static class UnfoldInfo {

			public final IABoxNode<String, String, String, String> node;
			public final IDLClassExpression<String, String, String, String> initial;
			public final Collection<IDLClassExpression<String, String, String, String>> unfolds;


			UnfoldInfo(
				final IABoxNode<String, String, String, String> node,
				final IDLClassExpression<String, String, String, String> initial,
				final Collection<IDLClassExpression<String, String, String, String>> unfolds)
			{
				this.node = node;
				this.initial = initial;
				this.unfolds = unfolds;
			}


			@Override
			public String toString()
			{
				final StringBuilder sb = new StringBuilder();
				sb.append("UnfoldInfo<");
				sb.append(node.getNodeID());
				sb.append(":");
				sb.append(initial);
				sb.append(" ==> ");
				sb.append(CollectionUtil.deepToString(unfolds));
				sb.append(">");
				return sb.toString();
			}
		}
	}
}

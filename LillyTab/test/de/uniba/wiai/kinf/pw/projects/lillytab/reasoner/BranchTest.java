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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner;

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


import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.EReasonerException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.Branch;
import java.lang.String;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl.DLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleKRSSParser;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleStringDLTermFactory;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class BranchTest {

	private IABox<String, String, String> _abox;
	private Branch<String, String, String> _branch;
	private IDLTermFactory<String, String, String> _termFactory;
	private IABoxFactory<String, String, String> _aboxFactory;

	public BranchTest()
	{
	}

	@BeforeClass
	public static void setUpClass() throws Exception
	{
	}

	@AfterClass
	public static void tearDownClass() throws Exception
	{
	}

	@Before
	public void setUp() throws ParseException, EReasonerException, EInconsistencyException
	{
		_termFactory = new SimpleStringDLTermFactory();
		_aboxFactory = new ABoxFactory<String, String, String>(_termFactory);
		SimpleKRSSParser parser = new SimpleKRSSParser(_termFactory);
		_abox = _aboxFactory.createABox();

		IABoxNode<String, String, String> a = _abox.getOrAddNamedNode("a", false);
		IABoxNode<String, String, String> b = _abox.getOrAddNamedNode("b", false);
		a.getRABox().getAssertedSuccessors().put("r0", b.getNodeID());

		a.addUnfoldedDescription(parser.parse("A"));
		b.addUnfoldedDescription(parser.parse("B"));
		_branch = new Branch<String, String, String>(_abox, true);
	}

	@After
	public void tearDown()
	{
		_termFactory = null;
		_abox = null;
		_branch = null;
	}

	/**
	 * Test of getABox method, of class Branch.
	 */
	@Test
	public void testGetAbox()
	{
		assertSame(_abox, _branch.getABox());
		Branch<String, String, String> secondBranch = _branch.clone();
		// only for copy-on-write
		//		assertNotSame(_abox, _branch.getABox());
		assertNotSame(_abox, secondBranch.getABox());
	}

	/**
	 * Test of clone method, of class Branch.
	 */
	@Test
	public void testClone()
		throws EReasonerException, EInconsistencyException
	{
		final Branch<String, String, String> secondBranch = _branch.clone();
		// only for copy-on-write
		// assertNotSame(_abox, _branch.getABox());
		assertNotSame(_abox, secondBranch.getABox());
		NodeID c = secondBranch.getABox().getOrAddNamedNode("c", false).getNodeID();
		NodeID d = secondBranch.getABox().getOrAddNamedNode("d", false).getNodeID();

		assertTrue(secondBranch.getABox().getNodeMap().containsKey(c));
		assertTrue(secondBranch.getABox().getNodeMap().containsKey("c"));
		assertTrue(secondBranch.getABox().getNodeMap().containsKey(d));
		assertTrue(secondBranch.getABox().getNodeMap().containsKey("d"));

		/* check, if link was copied */
		assertEquals(1, secondBranch.getABox().getNode("a").getRABox().getAssertedSuccessors().size());
		assertTrue(secondBranch.getABox().getNode("a").getRABox().getAssertedSuccessors().containsValue("r0", secondBranch.getABox().getNode("b").getNodeID()));
		assertEquals(1, secondBranch.getABox().getNode("b").getRABox().getAssertedPredecessors().size());
		assertTrue(secondBranch.getABox().getNode("b").getRABox().getAssertedPredecessors().containsValue("r0", secondBranch.getABox().getNode("a").getNodeID()));

		assertFalse(_branch.getABox().getNodeMap().containsKey(c));
		assertFalse(_branch.getABox().getNodeMap().containsKey("c"));
		assertFalse(_branch.getABox().getNodeMap().containsKey(d));
		assertFalse(_branch.getABox().getNodeMap().containsKey("d"));

		/*
		assertTrue(_branch.getGeneratingQueue().add(d));
		assertTrue(_branch.getGeneratingQueue().contains(d));
		assertTrue(_branch.getGeneratingQueue().remove(d));
		assertFalse(_branch.getGeneratingQueue().contains(d));
		assertTrue(_branch.getGeneratingQueue().add(d));

		assertTrue(_branch.getNonGeneratingQueue().add(d));
		assertTrue(_branch.getNonGeneratingQueue().contains(d));
		assertTrue(_branch.getNonGeneratingQueue().remove(d));
		assertFalse(_branch.getNonGeneratingQueue().contains(d));
		assertTrue(_branch.getNonGeneratingQueue().add(d));
		 */

		// assertTrue(secondBranch.getGeneratingQueue().add(c));
		// new nodes are automatically part of the queue, now
		assertTrue(secondBranch.getGeneratingQueue().contains(c));
		assertTrue(secondBranch.getGeneratingQueue().remove(c));
		assertFalse(secondBranch.getGeneratingQueue().contains(c));

		// assertTrue(secondBranch.getNonGeneratingQueue().add(c));
		assertTrue(secondBranch.getNonGeneratingQueue().contains(c));
		assertTrue(secondBranch.getNonGeneratingQueue().remove(c));
		assertFalse(secondBranch.getNonGeneratingQueue().contains(c));

		assertFalse(_branch.getABox().getNodeMap().containsKey(c));
		assertEquals(d, secondBranch.getABox().getOrAddNamedNode("d", false).getNodeID());

		/* cloning needs to preserve semantics */
		assertTrue(secondBranch.getGeneratingQueue().contains(d));
		assertTrue(secondBranch.getNonGeneratingQueue().contains(d));
	}

	/**
	 * Test of getNonGeneratingQueue method, of class Branch.
	 */
	@Test
	public void testgetNonGeneratingQueue()
		throws EReasonerException, EInconsistencyException
	{
		final NodeID c = _abox.getOrAddNamedNode("c", false).getNodeID();

		assertFalse(_branch.getNonGeneratingQueue().isEmpty());
		assertTrue(_branch.getNonGeneratingQueue().contains(c));
		assertFalse(_branch.getNonGeneratingQueue().add(c));
		assertTrue(_branch.getNonGeneratingQueue().contains(c));
		assertTrue(_branch.getNonGeneratingQueue().remove(c));
		assertFalse(_branch.getNonGeneratingQueue().contains(c));
	}

	/**
	 * Test of getGeneratingQueue method, of class Branch.
	 */
	@Test
	public void testgetGeneratingQueue()
		throws EReasonerException, EInconsistencyException
	{
		final NodeID c = _abox.getOrAddNamedNode("c", false).getNodeID();

		assertFalse(_branch.getGeneratingQueue().isEmpty());
		assertTrue(_branch.getGeneratingQueue().contains(c));
		assertFalse(_branch.getGeneratingQueue().add(c));
		assertTrue(_branch.getGeneratingQueue().contains(c));
		assertTrue(_branch.getGeneratingQueue().remove(c));
		assertFalse(_branch.getGeneratingQueue().contains(c));
	}

	/**
	 * Test of touch method, of class Branch.
	 */
	@Test
	public void testTouch_IABoxNode()
	{
		assertSame(_abox, _branch.getABox());
		_branch.getGeneratingQueue().clear();
		_branch.getGeneratingQueue().clear();
		/* only for copy on write */
		IABoxNode<String, String, String> aNode = _branch.getABox().getNode("a");
		assertNotNull(aNode);
		assertFalse(_branch.getGeneratingQueue().contains(aNode.getNodeID()));
		assertFalse(_branch.getGeneratingQueue().contains(aNode.getNodeID()));
		_branch.touchNode(aNode);
		assertTrue(_branch.getGeneratingQueue().contains(aNode.getNodeID()));
		assertTrue(_branch.getGeneratingQueue().contains(aNode.getNodeID()));
	}

	/**
	 * Test of touch method, of class Branch.
	 */
	@Test
	public void testTouch_NodeID()
		throws EReasonerException, EInconsistencyException
	{
		final NodeID c = _abox.getOrAddNamedNode("c", false).getNodeID();

		// new nodes are now added to the queues, automatically
		assertTrue(_branch.getGeneratingQueue().contains(c));
		assertTrue(_branch.getNonGeneratingQueue().contains(c));
		_branch.removeFromQueues(c);
		assertFalse(_branch.getGeneratingQueue().contains(c));
		assertFalse(_branch.getNonGeneratingQueue().contains(c));
		_branch.touch(c);
		assertTrue(_branch.getGeneratingQueue().contains(c));
		assertTrue(_branch.getNonGeneratingQueue().contains(c));
	}

	/**
	 * Test of touch method, of class Branch.
	 */
	@Test
	public void testTouch_Collection()
		throws EReasonerException, EInconsistencyException
	{
		final NodeID c = _abox.getOrAddNamedNode("c", false).getNodeID();
		final NodeID d = _abox.getOrAddNamedNode("d", false).getNodeID();
		final Collection<NodeID> names = new ArrayList<NodeID>();
		names.add(c);
		names.add(d);

		_branch.removeFromQueues(c);
		_branch.removeFromQueues(d);

		assertFalse(_branch.getGeneratingQueue().contains(c));
		assertFalse(_branch.getNonGeneratingQueue().contains(c));
		assertFalse(_branch.getGeneratingQueue().contains(d));
		assertFalse(_branch.getNonGeneratingQueue().contains(d));
		_branch.touchAll(names);
		assertTrue(_branch.getGeneratingQueue().contains(c));
		assertTrue(_branch.getNonGeneratingQueue().contains(c));
		assertTrue(_branch.getGeneratingQueue().contains(d));
		assertTrue(_branch.getNonGeneratingQueue().contains(d));
	}

	/**
	 * Test of touchNodes method, of class Branch.
	 */
	@Test
	public void testTouchNodes()
		throws EReasonerException, EInconsistencyException
	{
		final IABoxNode<String, String, String> a = _abox.getOrAddNamedNode("a", false);
		final IABoxNode<String, String, String> b = _abox.getOrAddNamedNode("b", false);
		final Collection<IABoxNode<String, String, String>> nodes = new ArrayList<IABoxNode<String, String, String>>();
		nodes.add(a);
		nodes.add(b);

		assertTrue(_branch.removeNodeFromQueues(a));
		assertTrue(_branch.removeNodeFromQueues(b));
		assertFalse(_branch.getGeneratingQueue().contains(a.getNodeID()));
		assertFalse(_branch.getNonGeneratingQueue().contains(a.getNodeID()));
		assertFalse(_branch.getGeneratingQueue().contains(b.getNodeID()));
		assertFalse(_branch.getNonGeneratingQueue().contains(b.getNodeID()));
		_branch.touchNodes(nodes);
		assertTrue(_branch.getGeneratingQueue().contains(a.getNodeID()));
		assertTrue(_branch.getNonGeneratingQueue().contains(a.getNodeID()));
		assertTrue(_branch.getGeneratingQueue().contains(b.getNodeID()));
		assertTrue(_branch.getNonGeneratingQueue().contains(b.getNodeID()));
	}

	@Test
	public void testNodeQueueSort()
		throws EReasonerException, ENodeMergeException
	{
		final IABox<String, String, String> abox = _aboxFactory.createABox();
		final Branch<String, String, String> branch = new Branch<String, String, String>(abox, true);

		final IABoxNode<String, String, String> n0 = abox.createNode(false);
		final IABoxNode<String, String, String> n1 = abox.createNode(false);
		assertEquals(2, abox.size());
		assertSame(abox.first(), n0);
		assertSame(abox.last(), n1);
		n1.addUnfoldedDescription(_termFactory.getDLNominalReference("b"));
		assertEquals(2, abox.size());
		assertSame(abox.first(), n1);
		assertSame(abox.last(), n0);
	}

	@Test
	public void testNodeAddTouch()
		throws EReasonerException, ENodeMergeException
	{
		final IABox<String, String, String> abox = _aboxFactory.createABox();
		final Branch<String, String, String> branch = new Branch<String, String, String>(abox, true);

		branch.getGeneratingQueue().clear();
		branch.getNonGeneratingQueue().clear();
		IABoxNode<String, String, String> node = abox.createNode(false);
		assertTrue(branch.getGeneratingQueue().contains(node.getNodeID()));
		assertTrue(branch.getNonGeneratingQueue().contains(node.getNodeID()));
	}
}

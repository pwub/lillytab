/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleStringDLTermFactory;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


/**
 *
 * @author peter
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ABoxRoleTest.class, de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ABoxNodeTest.class, de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ReasonerOntologyTest.class, de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.BranchTest.class, de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ReasonerClassifyTest.class, de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ReasonerDataTest.class, de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.GoverningTermTest.class, de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ABoxNotificationTest.class, de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ReasonerRBoxTest.class, de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.RABoxTest.class, de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.DependencyTrackingTest.class, de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ReasonerTest.class, de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ABoxCloneTest.class, de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ReasonerDatatypeTest.class, de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.SubsetBlockingStrategyTest.class, de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.RBoxTest.class, de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ABoxTest.class, de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.OWLAPILoaderTest.class})
public class ABoxNodeQueueTest
{
	private final IDLTermFactory<String, String, String, String> _termFactory = new SimpleStringDLTermFactory();
	private final IABoxFactory<String, String, String, String> _aboxFactory = new ABoxFactory<>(
		_termFactory);
	private ABox<String, String, String, String> _abox;

	@BeforeClass
	public static void setUpClass() throws Exception
	{
	}

	@AfterClass
	public static void tearDownClass() throws Exception
	{
	}

	@Before
	public void setUp() throws Exception
	{
		_abox = (ABox<String, String, String, String>) _aboxFactory.createABox();
	}

	@After
	public void tearDown() throws Exception
	{
		_abox = null;
	}

	/**
	 * Test of getNonGeneratingQueue method, of class Branch.
	 */
	@Test
	public void testgetNonGeneratingQueue()
		throws EReasonerException, EInconsistencyException
	{
		final NodeID c = _abox.getOrAddIndividualNode("c").getNodeID();

		assertFalse(_abox.getNonGeneratingQueue().isEmpty());
		assertTrue(_abox.getNonGeneratingQueue().contains(c));
		assertFalse(_abox.touch(c));
		assertTrue(_abox.getNonGeneratingQueue().contains(c));
		assertTrue(_abox.removeFromQueues(c));
		assertFalse(_abox.getNonGeneratingQueue().contains(c));
	}

	/**
	 * Test of getGeneratingQueue method, of class Branch.
	 */
	@Test
	public void testgetGeneratingQueue()
		throws EReasonerException, EInconsistencyException
	{
		final NodeID c = _abox.getOrAddIndividualNode("c").getNodeID();

		assertFalse(_abox.getGeneratingQueue().isEmpty());
		assertTrue(_abox.getGeneratingQueue().contains(c));
		assertFalse(_abox.touch(c));
		assertTrue(_abox.getGeneratingQueue().contains(c));
		assertTrue(_abox.removeFromQueues(c));
		assertFalse(_abox.getGeneratingQueue().contains(c));
	}

	/**
	 * Test of touchLiteral method, of class Branch.
	 */
	@Test
	public void testTouch_IABoxNode()
	{
		_abox.clearQueues();
		/* only for copy on write */
		IABoxNode<String, String, String, String> aNode = _abox.getIndividualNode("a");
		assertNotNull(aNode);
		assertTrue(_abox.getGeneratingQueue().contains(aNode.getNodeID()));
		assertTrue(_abox.getGeneratingQueue().contains(aNode.getNodeID()));
		_abox.removeNodeFromQueues(aNode);
		assertFalse(_abox.getGeneratingQueue().contains(aNode.getNodeID()));
		assertFalse(_abox.getGeneratingQueue().contains(aNode.getNodeID()));
		_abox.touchNode(aNode);
		assertTrue(_abox.getGeneratingQueue().contains(aNode.getNodeID()));
		assertTrue(_abox.getGeneratingQueue().contains(aNode.getNodeID()));
	}

	/**
	 * Test of touchLiteral method, of class Branch.
	 */
	@Test
	public void testTouch_NodeID()
		throws EReasonerException, EInconsistencyException
	{
		final NodeID c = _abox.getOrAddIndividualNode("c").getNodeID();

		// new nodes are now added to the queues, automatically
		assertTrue(_abox.getGeneratingQueue().contains(c));
		assertTrue(_abox.getNonGeneratingQueue().contains(c));
		_abox.removeFromQueues(c);
		assertFalse(_abox.getGeneratingQueue().contains(c));
		assertFalse(_abox.getNonGeneratingQueue().contains(c));
		_abox.touch(c);
		assertTrue(_abox.getGeneratingQueue().contains(c));
		assertTrue(_abox.getNonGeneratingQueue().contains(c));
	}

	/**
	 * Test of touchLiteral method, of class Branch.
	 */
	@Test
	public void testTouch_Collection()
		throws EReasonerException, EInconsistencyException
	{
		final NodeID c = _abox.getOrAddIndividualNode("c").getNodeID();
		final NodeID d = _abox.getOrAddIndividualNode("d").getNodeID();
		final Collection<NodeID> names = new ArrayList<>();
		names.add(c);
		names.add(d);

		_abox.removeFromQueues(c);
		_abox.removeFromQueues(d);

		assertFalse(_abox.getGeneratingQueue().contains(c));
		assertFalse(_abox.getNonGeneratingQueue().contains(c));
		assertFalse(_abox.getGeneratingQueue().contains(d));
		assertFalse(_abox.getNonGeneratingQueue().contains(d));
		_abox.touchAll(names);
		assertTrue(_abox.getGeneratingQueue().contains(c));
		assertTrue(_abox.getNonGeneratingQueue().contains(c));
		assertTrue(_abox.getGeneratingQueue().contains(d));
		assertTrue(_abox.getNonGeneratingQueue().contains(d));
	}

	/**
	 * Test of touchNodes method, of class Branch.
	 */
	@Test
	public void testTouchNodes()
		throws EReasonerException, EInconsistencyException
	{
		final IABoxNode<String, String, String, String> a = _abox.getIndividualNode("a");
		final IABoxNode<String, String, String, String> b = _abox.getIndividualNode("b");
		final Collection<IABoxNode<String, String, String, String>> nodes = new ArrayList<>();
		nodes.add(a);
		nodes.add(b);

		assertTrue(_abox.removeNodeFromQueues(a));
		assertTrue(_abox.removeNodeFromQueues(b));
		assertFalse(_abox.getGeneratingQueue().contains(a.getNodeID()));
		assertFalse(_abox.getNonGeneratingQueue().contains(a.getNodeID()));
		assertFalse(_abox.getGeneratingQueue().contains(b.getNodeID()));
		assertFalse(_abox.getNonGeneratingQueue().contains(b.getNodeID()));
		_abox.touchNodes(nodes);
		assertTrue(_abox.getGeneratingQueue().contains(a.getNodeID()));
		assertTrue(_abox.getNonGeneratingQueue().contains(a.getNodeID()));
		assertTrue(_abox.getGeneratingQueue().contains(b.getNodeID()));
		assertTrue(_abox.getNonGeneratingQueue().contains(b.getNodeID()));
	}

//	@Test
//	public void testNodeQueueSort()
//		throws EReasonerException, ENodeMergeException
//	{
//		final IABox<String, String, String, String> abox = _aboxFactory.createABox();
//		final Branch<String, String, String, String> branch = new Branch<>(abox, true);
//
//		final IABoxNode<String, String, String, String> n0 = abox.createNode(false);
//		final IABoxNode<String, String, String, String> n1 = abox.createNode(false);
//		assertEquals(2, abox.size());
//		assertSame(abox.first(), n0);
//		assertSame(abox.last(), n1);
//		n1.addTerm(_termFactory.getDLIndividualReference("b"));
//		assertEquals(2, abox.size());
//		assertSame(abox.first(), n1);
//		assertSame(abox.last(), n0);
//	}
	@Test
	public void testNodeAddTouch()
		throws EReasonerException, ENodeMergeException
	{
		_abox.clearQueues();
		IABoxNode<String, String, String, String> node = _abox.createNode(false);
		assertTrue(_abox.getGeneratingQueue().contains(node.getNodeID()));
		assertTrue(_abox.getNonGeneratingQueue().contains(node.getNodeID()));
	}

	@Test
	public void testCloneTouch()
		throws EReasonerException, ENodeMergeException
	{
		_abox.createNode(false);
		_abox.clearQueues();
		final IABoxNode<String, String, String, String> node = _abox.createNode(true);
		final ABox<String, String, String, String> klone = _abox.clone();
		final IABoxNode<String, String, String, String> node2 = klone.getNode(node.getNodeID());
		klone.touchNode(node2);
		assertTrue(klone.getGeneratingQueue().contains(node2.getNodeID()));
		assertTrue(klone.getNonGeneratingQueue().contains(node2.getNodeID()));
		assertFalse(_abox.getGeneratingQueue().contains(node2.getNodeID()));
		assertFalse(_abox.getNonGeneratingQueue().contains(node2.getNodeID()));
	}
}
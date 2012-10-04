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


import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentABoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.blocking.SubsetBlockingStrategy;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.Branch;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl.DLTermFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author peterw
 */
public class SubsetBlockingStrategyTest {
	private final IDLTermFactory<String, String, String> _termFactory = new DLTermFactory<String, String, String>();
	private final IABoxFactory<String, String, String> _aboxFactory = new ABoxFactory<String, String, String>(_termFactory);
	private IABox<String, String, String> _abox;
	private IABoxNode<String, String, String> _aboxNode;
	private SubsetBlockingStrategy<String, String, String> _blockingStrategy;

    public SubsetBlockingStrategyTest() {
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
    public void setUp()
		throws EInconsistencyException
	{
		_abox = _aboxFactory.createABox();
		_aboxNode = _abox.getOrAddNamedNode("Node", false);
		_aboxNode.addUnfoldedDescription(_termFactory.getDLClassReference("A"));
		_blockingStrategy = new SubsetBlockingStrategy<String, String, String>();
	}

    @After
    public void tearDown() {
    }


	/**
	 * Test of getBlockedNodeIDs method, of class ABoxNode.
	 */
	@Test
	public void testGetBlockedNodes()
		throws EInconsistentABoxException
	{
		assertTrue(_blockingStrategy.getBlockedNodeIDs(_aboxNode).isEmpty());
		IABoxNode<String, String, String> node2 = _abox.createNode(false);
		node2.addUnfoldedDescription(_termFactory.getDLClassReference("A"));
		assertTrue(_blockingStrategy.isBlocked(node2));
		assertEquals(_aboxNode, _blockingStrategy.getBlocker(node2));
		assertTrue(_blockingStrategy.getBlockedNodeIDs(_aboxNode).contains(node2.getNodeID()));
	}


	@Test
	public void testCloneBlock()
		throws EInconsistentABoxException
	{
		IABoxNode<String, String, String> node2 = _abox.createNode(false);
		node2.addUnfoldedDescription(_termFactory.getDLClassReference("A"));
		Branch<String, String, String> branch = new Branch<String, String, String>(_abox, true);
		assertTrue(_blockingStrategy.isBlocked(node2));
		assertEquals(_aboxNode, _blockingStrategy.getBlocker(node2));
		assertTrue(_blockingStrategy.getBlockedNodeIDs(_aboxNode).contains(node2.getNodeID()));

		IABox<String, String, String> clonedBox = _abox.clone();
		IABoxNode<String, String, String> klone = clonedBox.getNode(_aboxNode.getNodeID());;

		assertNotSame(_aboxNode, klone);
		assertTrue(_blockingStrategy.getBlockedNodeIDs(klone).contains(node2.getNodeID()));

	}


// This is an old test. Needs to be refactored.
//	/**
//	 * Test of isPotentialBlocker method, of class ABoxNode.
//	 */
//	@Test
//	public void testIsPotentialBlocker()
//	{
//		IABoxNode<String, String, String> node2 = _abox.createNode(false);
//		assertTrue(node2 instanceof ABoxNode);
//		node2.addUnfoldedDescription(_termFactory.getDLClassReference("A"));
//		_blockingStrategy.
//		assertTrue(((ABoxNode<String, String, String>) _aboxNode).isPotentialBlocker(node2));
//		node2.addUnfoldedDescription(_termFactory.getDLClassReference("B"));
//		assertFalse(((ABoxNode<String, String, String>) _aboxNode).isPotentialBlocker(node2));
//	}

	/**
	 * Test of isBlocked method, of class ABoxNode.
	 */
	@Test
	public void testIsBlocked()
		throws EInconsistentABoxException
	{
		assertTrue(_blockingStrategy.getBlockedNodeIDs(_aboxNode).isEmpty());
		IABoxNode<String, String, String> node2 = _abox.createNode(false);
		node2.addUnfoldedDescription(_termFactory.getDLClassReference("A"));
		Branch<String, String, String> branch = new Branch<String, String, String>(_abox, true);
		assertTrue(_blockingStrategy.isBlocked(node2));
	}

	/**
	 * Test of validateBlocks method, of class ABoxNode.
	 */
	@Test
	public void testValidateBlocks_0args()
		throws EInconsistentABoxException
	{
		assertTrue(_blockingStrategy.getBlockedNodeIDs(_aboxNode).isEmpty());
		IABoxNode<String, String, String> node2 = _abox.createNode(false);
		node2.addUnfoldedDescription(_termFactory.getDLClassReference("A"));
		assertTrue(_blockingStrategy.isBlocked(node2));
		node2.addUnfoldedDescription(_termFactory.getDLClassReference("B"));

		_blockingStrategy.validateBlocks(_aboxNode);
		assertFalse(_blockingStrategy.getBlockedNodeIDs(_aboxNode).contains(node2.getNodeID()));
		assertFalse(_blockingStrategy.isBlocked(node2));
		assertFalse(_blockingStrategy.getBlockedNodeIDs(node2).contains(node2.getNodeID()));

		/* add nominal, block should go away */
		node2.addUnfoldedDescription(_termFactory.getDLNominalReference("a0"));
		assertFalse(_blockingStrategy.isBlocked(node2));
	}

	/**
	 * Test of getBlocker method, of class ABoxNode.
	 */
	@Test
	public void testGetBlocker()
		throws EInconsistentABoxException
	{
		assertTrue(_blockingStrategy.getBlockedNodeIDs(_aboxNode).isEmpty());
		IABoxNode<String, String, String> node2 = _abox.createNode(false);
		node2.addUnfoldedDescription(_termFactory.getDLClassReference("A"));
		// Branch<String, String, String> branch = new Branch<String, String, String>(_abox);
		assertTrue(_blockingStrategy.isBlocked(node2));
		assertEquals(_aboxNode, _blockingStrategy.getBlocker(node2));
	}

}
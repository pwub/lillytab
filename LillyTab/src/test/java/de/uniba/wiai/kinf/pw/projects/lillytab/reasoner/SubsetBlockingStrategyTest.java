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
 * Use, modification and restribution of this file are covered by the terms of the Artistic License 2.0.
 *
 * You should have received a copy of the license terms in a file named "LICENSE" together with this software package.
 *
 * Disclaimer of Warranty: THE PACKAGE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS "AS IS' AND WITHOUT ANY
 * EXPRESS OR IMPLIED WARRANTIES. THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT ARE DISCLAIMED TO THE EXTENT PERMITTED BY YOUR LOCAL LAW. UNLESS REQUIRED BY LAW, NO COPYRIGHT
 * HOLDER OR CONTRIBUTOR WILL BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING IN ANY
 * WAY OUT OF THE USE OF THE PACKAGE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentABoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentRBoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.blocking.SubsetBlockingStrategy;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleType;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleStringDLTermFactory;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class SubsetBlockingStrategyTest
{
	@BeforeClass
	public static void setUpClass()
		throws Exception
	{
	}

	@AfterClass
	public static void tearDownClass()
		throws Exception
	{
	}
	private final IDLTermFactory<String, String, String, String> _termFactory = new SimpleStringDLTermFactory();
	private final IABoxFactory<String, String, String, String> _aboxFactory = new ABoxFactory<>(
		_termFactory);
	private IABox<String, String, String, String> _abox;
	private IABoxNode<String, String, String, String> _aboxNode;
	private SubsetBlockingStrategy<String, String, String, String> _blockingStrategy;

	public SubsetBlockingStrategyTest()
	{
	}

	@Before
	public void setUp()
		throws EInconsistencyException
	{
		_abox = _aboxFactory.createABox();
		_aboxNode = _abox.createIndividualNode();
		_aboxNode.addTerm(_termFactory.getDLClassReference("A"));
		_blockingStrategy = new SubsetBlockingStrategy<>();
	}

	@After
	public void tearDown()
	{
	}

	/**
	 * Test of getBlockedNodeIDs method, of class IABoxNode.
	 */
	@Test
	public void testGetBlockedNodes()
		throws EInconsistentABoxException, EInconsistentRBoxException
	{
		_abox.getRBox().getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		assertTrue(_blockingStrategy.getBlockedNodeIDs(_aboxNode).isEmpty());
		IABoxNode<String, String, String, String> node2 = _abox.createNode(false);
		_aboxNode.getRABox().getAssertedSuccessors().put("r", node2);
		node2.addTerm(_termFactory.getDLClassReference("A"));
		assertTrue(_blockingStrategy.isBlocked(node2));
		assertEquals(_aboxNode, _blockingStrategy.getBlocker(node2));
		assertTrue(_blockingStrategy.getBlockedNodeIDs(_aboxNode).contains(node2.getNodeID()));
	}

	@Test
	public void testCloneBlock() throws EInconsistentABoxException, EInconsistentRBoxException
	{
		_abox.getRBox().getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		IABoxNode<String, String, String, String> node2 = _abox.createNode(false);
		node2.addTerm(_termFactory.getDLClassReference("A"));
		_aboxNode.getRABox().getAssertedSuccessors().put("r", node2);
		Branch<String, String, String, String> branch = new Branch<>(_abox, true);
		assertTrue(_blockingStrategy.isBlocked(node2));
		assertEquals(_aboxNode, _blockingStrategy.getBlocker(node2));
		assertTrue(_blockingStrategy.getBlockedNodeIDs(_aboxNode).contains(node2.getNodeID()));

		IABox<String, String, String, String> clonedBox = _abox.clone();
		IABoxNode<String, String, String, String> klone = clonedBox.getNode(_aboxNode.getNodeID());;

		assertNotSame(_aboxNode, klone);
		assertTrue(_blockingStrategy.getBlockedNodeIDs(klone).contains(node2.getNodeID()));

	}

	// This is an old test. Needs to be refactored.
	//	/**
	@Test
	public void testIsBlocked()
		throws EInconsistentABoxException, EInconsistentRBoxException
	{
		_abox.getRBox().getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		assertTrue(_blockingStrategy.getBlockedNodeIDs(_aboxNode).isEmpty());
		IABoxNode<String, String, String, String> node2 = _abox.createNode(false);
		node2.addTerm(_termFactory.getDLClassReference("A"));
		_aboxNode.getRABox().getAssertedSuccessors().put("r", node2);
		Branch<String, String, String, String> branch = new Branch<>(_abox, true);
		assertTrue(_blockingStrategy.isBlocked(node2));
	}

	/**
	 * Test of validateBlocks method, of class IABoxNode.
	 */
	@Test
	public void testValidateBlocks_0args()
		throws EInconsistentABoxException, EInconsistentRBoxException
	{
		_abox.getRBox().getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		assertTrue(_blockingStrategy.getBlockedNodeIDs(_aboxNode).isEmpty());
		IABoxNode<String, String, String, String> node2 = _abox.createNode(false);
		_aboxNode.getRABox().getAssertedSuccessors().put("r", node2);
		node2.addTerm(_termFactory.getDLClassReference("A"));
		assertTrue(_blockingStrategy.isBlocked(node2));
		node2.addTerm(_termFactory.getDLClassReference("B"));

		_blockingStrategy.validateBlocks(_aboxNode);
		assertFalse(_blockingStrategy.getBlockedNodeIDs(_aboxNode).contains(node2.getNodeID()));
		assertFalse(_blockingStrategy.isBlocked(node2));
		assertFalse(_blockingStrategy.getBlockedNodeIDs(node2).contains(node2.getNodeID()));

		/* add nominal, block should go away */
		node2.addTerm(_termFactory.getDLIndividualReference("a0"));
		assertFalse(_blockingStrategy.isBlocked(node2));
	}

	/**
	 * Test of getBlocker method, of class IABoxNode.
	 */
	@Test
	public void testGetBlocker()
		throws EInconsistentABoxException, EInconsistentRBoxException
	{
		_abox.getRBox().getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		assertTrue(_blockingStrategy.getBlockedNodeIDs(_aboxNode).isEmpty());
		IABoxNode<String, String, String, String> node2 = _abox.createNode(false);
		_aboxNode.getRABox().getAssertedSuccessors().put("r", node2);
		node2.addTerm(_termFactory.getDLClassReference("A"));
		// Branch<String, String, String, String> branch = new Branch<String, String, String, String>(_abox);
		assertTrue(_blockingStrategy.isBlocked(node2));
		assertEquals(_aboxNode, _blockingStrategy.getBlocker(node2));
	}
}

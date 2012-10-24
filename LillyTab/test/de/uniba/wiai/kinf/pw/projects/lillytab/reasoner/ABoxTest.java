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
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentABoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl.DLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleStringDLTermFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * XXX, TODO: Fill in other ABox tests.
 **/
/**
 * 
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ABoxTest
{
	private final IDLTermFactory<String, String, String> _termFactory = new SimpleStringDLTermFactory();
	private final IABoxFactory<String, String, String> _aboxFactory = new ABoxFactory<String, String, String>(_termFactory);
	private IABox<String, String, String> _abox;

	public ABoxTest()
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
	public void setUp()
	{
		_abox = _aboxFactory.createABox();
	}

	@After
	public void tearDown()
	{
		_abox = null;
	}

	/**
	 * Test of getDLTermFactory method, of class ABox.
	 */
	@Test
	public void testGetDLTermFactory()
	{
	}

	/**
	 * Test of getTBox method, of class ABox.
	 */
	@Test
	public void testGetTBox()
	{
	}

	/**
	 * Test of mergeNodes method, of class ABox.
	 */
	@Test
	public void testMergeNodes()
		throws EInconsistentABoxException
	{
		IABoxNode<String, String, String> node0 = _abox.createNode(false);
		node0.addUnfoldedDescription(_termFactory.getDLClassReference("A"));

		IABoxNode<String, String, String> node1 = _abox.createNode(false);
		node1.addUnfoldedDescription(_termFactory.getDLClassReference("B"));

		IABoxNode<String, String, String> node2 = _abox.createNode(false);
		node2.addUnfoldedDescription(_termFactory.getDLClassReference("C"));

		node1.getRABox().getAssertedSuccessors().put("r", node2.getNodeID());
		node0.getRABox().getAssertedSuccessors().put("r", node2.getNodeID());

		NodeMergeInfo<String, String, String> mergeInfo = _abox.mergeNodes(node0, node2);
		assertEquals(node0, mergeInfo.getCurrentNode());
		assertEquals(node2, mergeInfo.getInitialNode());
		assertTrue(mergeInfo.getMergedNodes().contains(node2));
		assertTrue(mergeInfo.isModified(node0));

		assertFalse(_abox.contains(node2));
		assertEquals(2, _abox.size());

		assertTrue(node0.getRABox().getAssertedSuccessors().containsValue("r", node0.getNodeID()));
		assertTrue(node0.getRABox().getAssertedPredecessors().containsValue("r", node0.getNodeID()));
		assertFalse(node0.getRABox().getAssertedSuccessors().containsValue("r", node2.getNodeID()));

		assertTrue(node1.getRABox().getAssertedSuccessors().containsValue("r", node0.getNodeID()));
		assertTrue(node0.getRABox().getAssertedPredecessors().containsValue("r", node1.getNodeID()));
		assertFalse(node1.getRABox().getAssertedSuccessors().containsValue("r", node2.getNodeID()));

		assertTrue(node0.getTerms().contains(_termFactory.getDLClassReference("A")));
		assertTrue(node0.getTerms().contains(_termFactory.getDLClassReference("C")));

		mergeInfo = _abox.mergeNodes(node0, node1);

		assertTrue(node0.getTerms().contains(_termFactory.getDLClassReference("A")));
		assertTrue(node0.getTerms().contains(_termFactory.getDLClassReference("B")));
		assertTrue(node0.getTerms().contains(_termFactory.getDLClassReference("C")));

		assertEquals(1, _abox.size());
		assertTrue(node0.getRABox().getAssertedSuccessors().containsValue("r", node0.getNodeID()));
		assertTrue(node0.getRABox().getAssertedPredecessors().containsValue("r", node0.getNodeID()));
		assertFalse(node0.getRABox().getAssertedSuccessors().containsValue("r", node1.getNodeID()));
	}

	/**
	 * Test of notifyNodeMergeListeners method, of class ABox.
	 */
	@Test
	public void testNotifyNodeMergeListeners()
	{
	}

	/**
	 * Test of getNodeMergeListeners method, of class ABox.
	 */
	@Test
	public void testGetNodeMergeListeners()
	{
	}

	/**
	 * Test of getTermSetFactory method, of class ABox.
	 */
	@Test
	public void testGetTermSetFactory()
	{
	}

	/**
	 * Test of getDescriptionSetFactory method, of class ABox.
	 */
	@Test
	public void testGetDescriptionSetFactory()
	{
	}

	/**
	 * Test of getNodeIDSetFactory method, of class ABox.
	 */
	@Test
	public void testGetNodeIDSetFactory()
	{
	}

	/**
	 * Test of getLinkMapFactory method, of class ABox.
	 */
	@Test
	public void testGetLinkMapFactory()
	{
	}

	/**
	 * Test of toString method, of class ABox.
	 */
	@Test
	public void testToString_0args()
	{
	}

	/**
	 * Test of toString method, of class ABox.
	 */
	@Test
	public void testToString_int()
	{
	}

	/**
	 * Test of toString method, of class ABox.
	 */
	@Test
	public void testToString_String()
	{
	}

	/**
	 * Test of getNodeMap method, of class ABox.
	 */
	@Test
	public void testGetNodeMap()
	{
	}

	/**
	 * Test of createNode method, of class ABox.
	 */
	@Test
	public void testCreateNode()
	{
	}

	/**
	 * Test of getOrAddNamedNode method, of class ABox.
	 */
	@Test
	public void testGetOrAddNamedNode()
	{
	}

	/**
	 * Test of getNode method, of class ABox.
	 */
	@Test
	public void testGetNode_NodeID()
	{
	}

	/**
	 * Test of getNode method, of class ABox.
	 */
	@Test
	public void testGetNode_GenericType()
	{
	}

	/**
	 * Test of getNodeSetListeners method, of class ABox.
	 */
	@Test
	public void testGetNodeSetListeners()
	{
	}

	/**
	 * Test of comparator method, of class ABox.
	 */
	@Test
	public void testComparator()
	{
	}

	/**
	 * Test of subSet method, of class ABox.
	 */
	@Test
	public void testSubSet()
	{
	}

	/**
	 * Test of headSet method, of class ABox.
	 */
	@Test
	public void testHeadSet()
	{
	}

	/**
	 * Test of tailSet method, of class ABox.
	 */
	@Test
	public void testTailSet()
	{
	}

	/**
	 * Test of first method, of class ABox.
	 */
	@Test
	public void testFirst()
	{
	}

	/**
	 * Test of last method, of class ABox.
	 */
	@Test
	public void testLast()
	{
	}

	/**
	 * Test of size method, of class ABox.
	 */
	@Test
	public void testSize()
	{
	}

	/**
	 * Test of isEmpty method, of class ABox.
	 */
	@Test
	public void testIsEmpty()
	{
	}

	/**
	 * Test of contains method, of class ABox.
	 */
	@Test
	public void testContains()
	{
	}

	/**
	 * Test of iterator method, of class ABox.
	 */
	@Test
	public void testIterator()
	{
	}

	/**
	 * Test of toArray method, of class ABox.
	 */
	@Test
	public void testToArray_0args()
	{
	}

	/**
	 * Test of toArray method, of class ABox.
	 */
	@Test
	public void testToArray_GenericType()
	{
	}

	/**
	 * Test of add method, of class ABox.
	 */
	@Test
	public void testAdd()
	{
	}

	/**
	 * Test of remove method, of class ABox.
	 */
	@Test
	public void testRemove()
	{
	}

	/**
	 * Test of containsAll method, of class ABox.
	 */
	@Test
	public void testContainsAll()
	{
	}

	/**
	 * Test of addAll method, of class ABox.
	 */
	@Test
	public void testAddAll()
	{
	}

	/**
	 * Test of retainAll method, of class ABox.
	 */
	@Test
	public void testRetainAll()
	{
	}

	/**
	 * Test of removeAll method, of class ABox.
	 */
	@Test
	public void testRemoveAll()
	{
	}

	/**
	 * Test of clear method, of class ABox.
	 */
	@Test
	public void testClear()
	{
	}

	/**
	 * Test of deepHashCode method, of class ABox.
	 */
	@Test
	public void testDeepHashCode()
		throws EInconsistentABoxException
	{
		final IABox<String, String, String> abox2 = _aboxFactory.createABox();
		assertEquals(_abox.deepHashCode(), abox2.deepHashCode());

		/* not necessarily */
		final IABoxNode<String, String, String> node1 = _abox.createNode(false);
		assertTrue(_abox.deepHashCode() != abox2.deepHashCode());

		final IABoxNode<String, String, String> node2 = abox2.createNode(false);
		assertEquals(_abox.deepHashCode(), abox2.deepHashCode());

		/* not necessarily */
		node1.addUnfoldedDescription(_termFactory.getDLClassReference("A"));
		assertTrue(_abox.deepHashCode() != abox2.deepHashCode());

		node2.addUnfoldedDescription(_termFactory.getDLClassReference("A"));
		assertEquals(_abox.deepHashCode(), abox2.deepHashCode());
	}

	/**
	 * Test of deepEquals method, of class ABox.
	 */
	@Test
	public void testDeepEquals()
		throws EInconsistentABoxException
	{
		final IABox<String, String, String> abox2 = _aboxFactory.createABox();
		assertTrue(_abox.deepEquals(abox2));

		/* not necessarily */
		final IABoxNode<String, String, String> node1 = _abox.createNode(false);
		assertFalse(_abox.deepEquals(abox2));

		final IABoxNode<String, String, String> node2 = abox2.createNode(false);
		assertTrue(_abox.deepEquals(abox2));

		/* not necessarily */
		node1.addUnfoldedDescription(_termFactory.getDLClassReference("A"));
		assertFalse(_abox.deepEquals(abox2));

		node2.addUnfoldedDescription(_termFactory.getDLClassReference("A"));
		assertTrue(_abox.deepEquals(abox2));
	}
	
	
	
}

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
package de.uniba.wiai.kinf.pw.projects.lillytab;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentABoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.EReasonerException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleStringDLTermFactory;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class NodeMergeInfoTest {

	private static IDLTermFactory<String, String, String, String> _termFactory;
	private static IABoxFactory<String, String, String, String> _aboxFactory;


	@BeforeClass
	public static void setUpClass()
		throws Exception
	{
		_termFactory = new SimpleStringDLTermFactory();
		_aboxFactory = new ABoxFactory<>(_termFactory);
	}


	@AfterClass
	public static void tearDownClass()
		throws Exception
	{
		_aboxFactory = null;
		_termFactory = null;
	}
	private IABox<String, String, String, String> _abox;
	private IABoxNode<String, String, String, String> _targetNode;
	private IABoxNode<String, String, String, String> _mergedNode;


	public NodeMergeInfoTest()
	{
	}


	@Before
	public void setUp()
		throws EInconsistentABoxException, ENodeMergeException
	{
		_abox = _aboxFactory.createABox();
		_targetNode = _abox.createNode(false);
		_mergedNode = _abox.createNode(false);


	}


	@After
	public void tearDown()
	{
		_targetNode = null;
		_mergedNode = null;
		_abox = null;
	}


	/**
	 * Test of getCurrentNode method, of class NodeMergeInfo.
	 */
	@Test
	public void testGetCurrentNode()
		throws EReasonerException, ENodeMergeException
	{
		final NodeMergeInfo<String, String, String, String> mergeInfo = _abox.mergeNodes(_targetNode, _mergedNode);
		assertSame(_targetNode, mergeInfo.getCurrentNode());
	}


	/**
	 * Test of getMergedNodes method, of class NodeMergeInfo.
	 */
	@Test
	public void testGetMergedNodes() throws EReasonerException, ENodeMergeException
	{
		final NodeMergeInfo<String, String, String, String> mergeInfo = _abox.mergeNodes(_targetNode, _mergedNode);
		assertTrue(mergeInfo.getMergedNodes().contains(_mergedNode));
	}


	/**
	 * Test of getModifiedNodes method, of class NodeMergeInfo.
	 */
	@Test
	public void testGetModifiedNodesNoModify()
		throws EReasonerException, ENodeMergeException
	{
		final NodeMergeInfo<String, String, String, String> mergeInfo = _abox.mergeNodes(_targetNode, _mergedNode);
		assertTrue(mergeInfo.getModifiedNodes().isEmpty());
	}


	/**
	 * Test of getModifiedNodes method, of class NodeMergeInfo.
	 */
	@Test
	public void testGetModifiedNodes()
		throws EReasonerException, ENodeMergeException
	{
		_mergedNode.addTerm(_termFactory.getDLClassReference("A"));

		final NodeMergeInfo<String, String, String, String> mergeInfo = _abox.mergeNodes(_targetNode, _mergedNode);
		assertTrue(mergeInfo.getModifiedNodes().contains(_targetNode));
	}


	/**
	 * Test of getInitialNode method, of class NodeMergeInfo.
	 */
	@Test
	public void testGetInitialNode()
		throws EReasonerException, ENodeMergeException
	{
		final NodeMergeInfo<String, String, String, String> mergeInfo = _abox.mergeNodes(_targetNode, _mergedNode);
		assertSame(mergeInfo.getInitialNode(), _mergedNode);
	}


	/**
	 * Test of isModified method, of class NodeMergeInfo.
	 */
	@Test
	public void testIsModified()
	{
	}


	/**
	 * Test of setModified method, of class NodeMergeInfo.
	 */
	@Test
	public void testSetModified()
	{
	}


	/**
	 * Test of recordMerge method, of class NodeMergeInfo.
	 */
	@Test
	public void testRecordMerge_IABoxNode_boolean()
	{
	}


	/**
	 * Test of recordMerge method, of class NodeMergeInfo.
	 */
	@Test
	public void testRecordMerge_IABoxNode()
	{
	}


	/**
	 * Test of append method, of class NodeMergeInfo.
	 */
	@Test
	public void testAppend()
	{
	}


	/**
	 * Test of toString method, of class NodeMergeInfo.
	 */
	@Test
	public void testToString()
	{
	}
}

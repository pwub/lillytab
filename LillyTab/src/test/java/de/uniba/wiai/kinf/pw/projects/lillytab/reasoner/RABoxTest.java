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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner;

import de.dhke.projects.cutil.Pair;
import de.dhke.projects.cutil.collections.CollectionFromIterable;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleProperty;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleType;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleStringDLTermFactory;
import java.util.ArrayList;
import java.util.List;
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
public class RABoxTest {

	private IDLTermFactory<String, String, String, String> _termFactory;
	private IABoxFactory<String, String, String, String> _aboxFactory;
	private IABox<String, String, String, String> _abox;
	private IRBox<String, String, String, String> _rbox;


	public RABoxTest()
	{
	}


	@BeforeClass
	public static void setUpClass()
	{
	}


	@AfterClass
	public static void tearDownClass()
	{
	}


	@Before
	public void setUp()
	{
		_termFactory = new SimpleStringDLTermFactory();
		_aboxFactory = new ABoxFactory<>(_termFactory);
		_abox = _aboxFactory.createABox();
		_rbox = _abox.getTBox().getRBox();
	}


	@After
	public void tearDown()
	{
		_rbox = null;
		_abox = null;
		_aboxFactory = null;
		_termFactory = null;
	}


	@Test
	public void testGetSuccessors()
		throws EInconsistencyException
	{
		IABoxNode<String, String, String, String> aNode = _abox.getOrAddIndividualNode("a");
		IABoxNode<String, String, String, String> bNode = _abox.getOrAddIndividualNode("b");
		_rbox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		aNode.getRABox().getAssertedSuccessors().put("r", bNode.getNodeID());

		assertTrue(aNode.getRABox().getSuccessors().contains(bNode.getNodeID()));
		assertTrue(CollectionFromIterable.decorate(aNode.getRABox().getSuccessors("r"), true).contains(bNode.
			getNodeID()));

		assertFalse(aNode.getRABox().getSuccessors().contains(aNode.getNodeID()));
		assertFalse(CollectionFromIterable.decorate(aNode.getRABox().getSuccessors("r"), true).contains(aNode.
			getNodeID()));
	}


	@Test
	public void testGetSuccessorNodes()
		throws EInconsistencyException
	{
		IABoxNode<String, String, String, String> aNode = _abox.getOrAddIndividualNode("a");
		IABoxNode<String, String, String, String> bNode = _abox.getOrAddIndividualNode("b");
		_rbox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		aNode.getRABox().getAssertedSuccessors().put("r", bNode.getNodeID());

		assertTrue(CollectionFromIterable.decorate(aNode.getRABox().getSuccessorNodes(), true).contains(bNode));
		assertTrue(CollectionFromIterable.decorate(aNode.getRABox().getSuccessorNodes("r"), true).contains(bNode));

		assertFalse(CollectionFromIterable.decorate(aNode.getRABox().getSuccessorNodes(), true).contains(aNode));
		assertFalse(CollectionFromIterable.decorate(aNode.getRABox().getSuccessorNodes("r"), true).contains(aNode));

	}


	@Test
	public void testGetPredecessors()
		throws EInconsistencyException
	{
		IABoxNode<String, String, String, String> aNode = _abox.getOrAddIndividualNode("a");
		IABoxNode<String, String, String, String> bNode = _abox.getOrAddIndividualNode("b");
		_rbox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		aNode.getRABox().getAssertedSuccessors().put("r", bNode.getNodeID());

		assertTrue(bNode.getRABox().getPredecessors().contains(aNode.getNodeID()));
		assertTrue(CollectionFromIterable.decorate(bNode.getRABox().getPredecessors("r"), true).contains(aNode.
			getNodeID()));

		assertFalse(aNode.getRABox().getPredecessors().contains(aNode.getNodeID()));
		assertFalse(CollectionFromIterable.decorate(aNode.getRABox().getPredecessors("r"), true).contains(aNode.
			getNodeID()));
	}


	@Test
	public void testGetPredecessorNodes()
		throws EInconsistencyException
	{
		IABoxNode<String, String, String, String> aNode = _abox.getOrAddIndividualNode("a");
		IABoxNode<String, String, String, String> bNode = _abox.getOrAddIndividualNode("b");
		_rbox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		aNode.getRABox().getAssertedSuccessors().put("r", bNode.getNodeID());

		assertTrue(CollectionFromIterable.decorate(bNode.getRABox().getPredecessorNodes(), true).contains(aNode));
		assertTrue(CollectionFromIterable.decorate(bNode.getRABox().getPredecessorNodes("r"), true).contains(aNode));

		assertFalse(CollectionFromIterable.decorate(bNode.getRABox().getPredecessorNodes(), true).contains(bNode));
		assertFalse(CollectionFromIterable.decorate(bNode.getRABox().getPredecessorNodes("r"), true).contains(bNode));
	}


	@Test
	public void testSuccessorRemoveChange()
		throws EInconsistencyException
	{
		IABoxNode<String, String, String, String> aNode = _abox.getOrAddIndividualNode("a");
		IABoxNode<String, String, String, String> bNode = _abox.getOrAddIndividualNode("b");
		IABoxNode<String, String, String, String> cNode = _abox.getOrAddIndividualNode("b");

		_rbox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		aNode.getRABox().getAssertedSuccessors().put("r", bNode.getNodeID());
		bNode.getRABox().getAssertedSuccessors().put("r", cNode.getNodeID());

		assertTrue(CollectionFromIterable.decorate(aNode.getRABox().getSuccessorNodes(), true).contains(bNode));
		assertTrue(CollectionFromIterable.decorate(aNode.getRABox().getSuccessorNodes("r"), true).contains(bNode));

		assertTrue(CollectionFromIterable.decorate(bNode.getRABox().getSuccessorNodes(), true).contains(cNode));
		assertTrue(CollectionFromIterable.decorate(bNode.getRABox().getSuccessorNodes("r"), true).contains(cNode));

		bNode.getRABox().getAssertedPredecessors().remove("r", aNode.getNodeID());

		assertFalse(CollectionFromIterable.decorate(aNode.getRABox().getSuccessorNodes(), true).contains(bNode));
		assertFalse(CollectionFromIterable.decorate(aNode.getRABox().getSuccessorNodes("r"), true).contains(bNode));

		assertFalse(CollectionFromIterable.decorate(bNode.getRABox().getPredecessorNodes(), true).contains(aNode));
		assertFalse(CollectionFromIterable.decorate(bNode.getRABox().getPredecessorNodes(), true).contains(aNode));
	}


	@Test
	public void testLinkMapClone()
		throws EInconsistencyException
	{
		IABoxNode<String, String, String, String> aNode = _abox.getOrAddIndividualNode("a");
		IABoxNode<String, String, String, String> bNode = _abox.getOrAddIndividualNode("b");

		_rbox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		aNode.getRABox().getAssertedSuccessors().put("r", bNode.getNodeID());

		IABox<String, String, String, String> cloneBox = _abox.clone();
		assertTrue(cloneBox.getIndividualNode("a").getRABox().getAssertedSuccessors().containsValue("r", bNode.getNodeID()));
		assertTrue(cloneBox.getIndividualNode("b").getRABox().getAssertedPredecessors().containsValue("r", aNode.getNodeID()));
	}


	@Test
	public void getSuccessorPairsTest()
		throws EInconsistencyException
	{
		IABoxNode<String, String, String, String> aNode = _abox.getOrAddIndividualNode("a");
		IABoxNode<String, String, String, String> bNode = _abox.getOrAddIndividualNode("b");
		IABoxNode<String, String, String, String> cNode = _abox.getOrAddDatatypeNode("c");

		_rbox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_rbox.getAssertedRBox().addRole("d", RoleType.DATA_PROPERTY);
		_rbox.getAssertedRBox().addRole("topData", RoleType.DATA_PROPERTY);
		_rbox.getAssertedRBox().setRoleProperty("topData", RoleProperty.TOP);

		assert _rbox == _abox.getTBox().getRBox();

		aNode.getRABox().getAssertedSuccessors().put("r", bNode.getNodeID());
		aNode.getRABox().getAssertedSuccessors().put("d", cNode.getNodeID());

		assertTrue(CollectionFromIterable.decorate(aNode.getRABox().getSuccessorPairs(), true).contains(Pair.wrap(
			"topData", cNode.getNodeID())));
		assertTrue(CollectionFromIterable.decorate(aNode.getRABox().getSuccessorPairs(), true).contains(Pair.wrap("d",
																												  cNode.getNodeID())));

	}


	@Test
	public void assertedRoleSubRoleLinkgetSuccessorTest()
		throws EInconsistencyException
	{
		IABoxNode<String, String, String, String> aNode = _abox.getOrAddIndividualNode("a");
		IABoxNode<String, String, String, String> bNode = _abox.getOrAddIndividualNode("b");

		_rbox.getAssertedRBox().addRole("super", RoleType.OBJECT_PROPERTY);
		_rbox.getAssertedRBox().addRole("sub", RoleType.OBJECT_PROPERTY);
		_rbox.getAssertedRBox().addSubRole("super", "sub");

		aNode.getRABox().getAssertedSuccessors().put("super", bNode);
		aNode.getRABox().getAssertedSuccessors().put("sub", bNode);

		List<IABoxNode<String, String, String, String>> succNodes = new ArrayList<>(aNode.getRABox().getSuccessorNodes());
		assertEquals(1, succNodes.size());
		assertSame(bNode, succNodes.get(0));
	}
}

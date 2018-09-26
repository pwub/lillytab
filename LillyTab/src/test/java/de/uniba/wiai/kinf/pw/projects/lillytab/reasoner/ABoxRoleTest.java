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

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleType;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleKRSSParser;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleStringDLTermFactory;
import org.apache.commons.collections15.Bag;
import org.apache.commons.collections15.bag.HashBag;
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
public class ABoxRoleTest {

	private static IABoxFactory<String, String, String, String> _aboxFactory;
	private static SimpleKRSSParser _parser;
	private IABox<String, String, String, String> _abox;


	public ABoxRoleTest()
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
	public void dualRoleInheritanceTest() throws EInconsistencyException
	{
		final IRBox<String, String, String, String> rbox = _abox.getTBox().getRBox();
		rbox.getAssertedRBox().addRole("parent", RoleType.OBJECT_PROPERTY);
		rbox.getAssertedRBox().addRole("r0", RoleType.OBJECT_PROPERTY);
		rbox.getAssertedRBox().addRole("r1", RoleType.OBJECT_PROPERTY);
		rbox.getAssertedRBox().addSubRole("parent", "r0");
		rbox.getAssertedRBox().addSubRole("parent", "r1");

		final IABoxNode<String, String, String, String> aNode = _abox.getOrAddIndividualNode("a");
		final IABoxNode<String, String, String, String> bNode = _abox.getOrAddIndividualNode("b");
		aNode.getRABox().getAssertedSuccessors().put("r0", bNode.getNodeID());
		aNode.getRABox().getAssertedSuccessors().put("r1", bNode.getNodeID());

		final Bag<String> outRoles = new HashBag<>();
		for (String role : aNode.getRABox().getOutgoingRoles()) {
			outRoles.add(role);
		}
		assertEquals(1, outRoles.getCount("parent"));
	}
}

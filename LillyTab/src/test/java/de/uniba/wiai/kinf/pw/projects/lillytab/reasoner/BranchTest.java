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
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleKRSSParser;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleStringDLTermFactory;
import java.text.ParseException;
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
public class BranchTest
{
	private IABox<String, String, String, String> _abox;
	private Branch<String, String, String, String> _branch;
	private IDLTermFactory<String, String, String, String> _termFactory;
	private IABoxFactory<String, String, String, String> _aboxFactory;

	public BranchTest()
	{
	}

	@BeforeClass
	public static void setUpClass()
		throws Exception
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
		_aboxFactory = new ABoxFactory<>(_termFactory);
		SimpleKRSSParser parser = new SimpleKRSSParser(_termFactory);
		_abox = _aboxFactory.createABox();

		IABoxNode<String, String, String, String> a = _abox.getOrAddIndividualNode("a");
		IABoxNode<String, String, String, String> b = _abox.getOrAddIndividualNode("b");
		a.getRABox().getAssertedSuccessors().put("r0", b.getNodeID());

		a.addTerm(parser.parse("A"));
		b.addTerm(parser.parse("B"));
		_branch = new Branch<>(_abox, true);
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
		Branch<String, String, String, String> secondBranch = _branch.clone();
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
		final Branch<String, String, String, String> secondBranch = _branch.clone();
		// only for copy-on-write
		// assertNotSame(_abox, _branch.getABox());
		assertNotSame(_abox, secondBranch.getABox());
		NodeID c = secondBranch.getABox().getOrAddIndividualNode("c").getNodeID();
		NodeID d = secondBranch.getABox().getOrAddIndividualNode("d").getNodeID();

		assertTrue(secondBranch.getABox().getNodeMap().containsKey(c));
		assertTrue(secondBranch.getABox().getNodeMap().containsKey("c"));
		assertTrue(secondBranch.getABox().getNodeMap().containsKey(d));
		assertTrue(secondBranch.getABox().getNodeMap().containsKey("d"));

		/* check, if link was copied */
		assertEquals(1, secondBranch.getABox().getIndividualNode("a").getRABox().getAssertedSuccessors().size());
		assertTrue(secondBranch.getABox().getIndividualNode("a").getRABox().getAssertedSuccessors().containsValue("r0",
																												  secondBranch.
			getABox().getIndividualNode(
			"b").getNodeID()));
		assertEquals(1, secondBranch.getABox().getIndividualNode("b").getRABox().getAssertedPredecessors().size());
		assertTrue(
			secondBranch.getABox().getIndividualNode("b").getRABox().getAssertedPredecessors().containsValue("r0",
																											 secondBranch.
			getABox().getIndividualNode(
			"a").getNodeID()));

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

		// assertTrue(secondBranch.getNonGeneratingQueue().add(c));

		assertFalse(_branch.getABox().getNodeMap().containsKey(c));
		assertEquals(d, secondBranch.getABox().getIndividualNode("d").getNodeID());
	}
}

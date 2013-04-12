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
import de.uniba.wiai.kinf.pw.projects.lillytab.IReasonerResult;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleType;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleKRSSParser;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleStringDLTermFactory;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Beyond basic test cases for the lillytab Reasoner, using real ontological constructs.
 *
 * Basic functionality tests can be found in {@link ReasonerTest}.
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ReasonerOntologyTest {


	@BeforeClass
	public static void setUpClass()
		throws Exception
	{
		LogManager.getLogManager().reset();
		Logger.getLogger("").setLevel(Level.ALL);
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.ALL);
		Logger.getLogger("").addHandler(handler);
	}


	@AfterClass
	public static void tearDownClass()
		throws Exception
	{
	}

	private IDLTermFactory<String, String, String, String> _termFactory = new SimpleStringDLTermFactory();
	private IABoxFactory<String, String, String, String> _aboxFactory = new ABoxFactory<>(_termFactory);
	private IABox<String, String, String, String> _abox;
	private Reasoner<String, String, String, String> _reasoner;
	private SimpleKRSSParser _parser;


	public ReasonerOntologyTest()
	{
	}


	@Before
	public void setUp()
	{
		_reasoner = new Reasoner<>();
		_parser = new SimpleKRSSParser(_termFactory);
		_abox = _aboxFactory.createABox();
	}


	@After
	public void tearDown()
	{
		_reasoner = null;
		_abox = null;
		_parser = null;
	}


	@Test(expected = EInconsistencyException.class)
	public void testSimpleInconsistentStoneDamageOntology() throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("hasDamage", RoleType.OBJECT_PROPERTY);

		IDLClassReference<String, String, String, String> SpatialObject = _termFactory.getDLClassReference("S");
		IDLClassReference<String, String, String, String> Stone = _termFactory.getDLClassReference("S");
		IDLClassReference<String, String, String, String> Ashlar = _termFactory.getDLClassReference("A");
		IDLClassReference<String, String, String, String> Damage = _termFactory.getDLClassReference("D");
		IDLClassReference<String, String, String, String> StoneDamage = _termFactory.getDLClassReference("SD");
		IDLClassReference<String, String, String, String> MetalDamage = _termFactory.getDLClassReference("MD");
		IABoxNode<String, String, String, String> aks1 = _abox.getOrAddIndividualNode("aks1");
		IABoxNode<String, String, String, String> d1 = _abox.getOrAddIndividualNode("d1");

//			IABoxNode<String, String, String, String> aks2 = abox.getOrAddNamedNode("aks2");
//			IABoxNode<String, String, String, String> aks3 = abox.getOrAddNamedNode("aks3");

		Set<IDLClassExpression<String, String, String, String>> dlDesc = new HashSet<>();
		dlDesc.add(_parser.parse("(implies SO (some hasDamage _Thing_))"));
		dlDesc.add(_parser.parse("(not (and SD MD))"));
		dlDesc.add(_parser.parse("(implies A S)"));
		dlDesc.add(_parser.parse("(implies S (only hasDamage SD))"));
		// dlDesc.add(parser.parse("(implies SO (some hasDamage D))"));
		_abox.getTBox().addAll(dlDesc);

		aks1.addTerm(Ashlar);
		d1.addTerm(MetalDamage);
		aks1.getRABox().getAssertedSuccessors().put("hasDamage", d1.getNodeID());
		_reasoner.checkConsistency(_abox);
	}


	@Test
	public void testSimpleConsistentStoneDamageOntology() throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("hasDamage", RoleType.OBJECT_PROPERTY);

		SimpleKRSSParser parser = new SimpleKRSSParser(_termFactory);
		// IDLClassReference<String, String, String, String> SpatialObject = _termFactory.getDLClassReference("S");
		// IDLClassReference<String, String, String, String> Stone = _termFactory.getDLClassReference("S");
		IDLClassReference<String, String, String, String> Ashlar = _termFactory.getDLClassReference("A");
		// IDLClassReference<String, String, String, String> Damage = _termFactory.getDLClassReference("D");
		IDLClassReference<String, String, String, String> StoneDamage = _termFactory.getDLClassReference("SD");
		// IDLClassReference<String, String, String, String> MetalDamage = _termFactory.getDLClassReference("MD");
		IABoxNode<String, String, String, String> aks1 = _abox.getOrAddIndividualNode("aks1");
		IABoxNode<String, String, String, String> d1 = _abox.getOrAddIndividualNode("d1");

//			IABoxNode<String, String, String, String> aks2 = abox.getOrAddNamedNode("aks2");
//			IABoxNode<String, String, String, String> aks3 = abox.getOrAddNamedNode("aks3");

		Set<IDLClassExpression<String, String, String, String>> dlDesc = new HashSet<>();
		dlDesc.add(parser.parse("(implies SO (some hasDamage _Thing_))"));
		dlDesc.add(parser.parse("(not (and SD MD))"));
		dlDesc.add(parser.parse("(implies A S)"));
		dlDesc.add(parser.parse("(implies S (only hasDamage SD))"));
		_abox.getTBox().addAll(dlDesc);

		aks1.addTerm(Ashlar);
		d1.addTerm(StoneDamage);
		aks1.getRABox().getAssertedSuccessors().put("hasDamage", d1.getNodeID());

		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.checkConsistency(_abox);
		assertFalse(results.isEmpty());
	}


	@Test
	public void unionImpliesTest()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);

		IABoxNode<String, String, String, String> a = _abox.getOrAddIndividualNode("a");
		a.addTerm(_parser.parse("(implies (or A B) (some r C)))"));
		a.addTerm(_parser.parse("A"));
		a.addTerm(_parser.parse("(not B)"));
		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.checkConsistency(_abox,
																												 false);
		assertFalse(results.isEmpty());

	}


	@Test
	public void multiUnionBranchTest()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		IABoxNode<String, String, String, String> a = _abox.getOrAddIndividualNode("a");
		String[] ors = new String[]{
			"(or A B)",
			"(or C D)",
			"(or E F)",
			"(or G H)",
			"(or K L)",
			"(or M N)",
			"(or O P)",
			"(or Q R)",};
		for (String or : ors) {
			a.addTerm(_parser.parse(or));
		}

		_reasoner.getReasonerOptions().setSemanticBranching(false);
		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.checkConsistency(_abox,
																												 false);
		assertFalse(results.isEmpty());
		assertEquals(0x1 << ors.length, results.size());
	}


	@Test
	public void multiUnionSemanticBranchTest()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		IABoxNode<String, String, String, String> a = _abox.getOrAddIndividualNode("a");
		String[] ors = new String[]{
			"(or A B)",
			"(or C D)",
			"(or E F)",
			"(or G H)",
			"(or K L)",
			"(or M N)",
			"(or O P)",
			"(or Q R)",};
		for (String or : ors) {
			a.addTerm(_parser.parse(or));
		}

		_reasoner.getReasonerOptions().setSemanticBranching(true);
		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.checkConsistency(_abox,
																												 false);
		assertFalse(results.isEmpty());
		assertEquals((int) Math.pow(3, ors.length), results.size());
	}
}

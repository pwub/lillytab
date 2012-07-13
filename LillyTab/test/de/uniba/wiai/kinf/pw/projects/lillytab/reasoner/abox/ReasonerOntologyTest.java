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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentABoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl.DLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleKRSSParser;
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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Less than basic test cases for the lillytab Reasoner
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ReasonerOntologyTest {
	private IDLTermFactory<String, String, String> _termFactory = new DLTermFactory<String, String, String>();
	private IABoxFactory<String, String, String> _aboxFactory = new ABoxFactory<String, String, String>(_termFactory);
	private IABox<String, String, String> _abox;
	private Reasoner<String, String, String> _reasoner;
	private SimpleKRSSParser _parser;

	public ReasonerOntologyTest()
	{
	}


	@BeforeClass
	public static void setUpClass() throws Exception
	{
		LogManager.getLogManager().reset();
		Logger.getLogger("").setLevel(Level.ALL);
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.ALL);
		Logger.getLogger("").addHandler(handler);
	}


	@AfterClass
	public static void tearDownClass() throws Exception
	{
	}


	@Before
	public void setUp()
	{
		_reasoner = new Reasoner<String, String, String>();
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


	@Test(expected = EInconsistentABoxException.class)
	public void testSimpleInconsistentStoneDamageOntology()
		throws ParseException, EReasonerException, EInconsistentABoxException
	{
		IDLClassReference<String, String, String> SpatialObject = _termFactory.getDLClassReference("S");
		IDLClassReference<String, String, String> Stone = _termFactory.getDLClassReference("S");
		IDLClassReference<String, String, String> Ashlar = _termFactory.getDLClassReference("A");
		IDLClassReference<String, String, String> Damage = _termFactory.getDLClassReference("D");
		IDLClassReference<String, String, String> StoneDamage = _termFactory.getDLClassReference("SD");
		IDLClassReference<String, String, String> MetalDamage = _termFactory.getDLClassReference("MD");
		IABoxNode<String, String, String> aks1 = _abox.getOrAddNamedNode("aks1", false);
		IABoxNode<String, String, String> d1 = _abox.getOrAddNamedNode("d1", false);

//			IABoxNode<String, String, String> aks2 = abox.getOrAddNamedNode("aks2");
//			IABoxNode<String, String, String> aks3 = abox.getOrAddNamedNode("aks3");

		Set<IDLRestriction<String, String, String>> dlDesc = new HashSet<IDLRestriction<String, String, String>>();
		dlDesc.add(_parser.parse("(implies SO (some hasDamage _Thing_))"));
		dlDesc.add(_parser.parse("(not (and SD MD))"));
		dlDesc.add(_parser.parse("(implies A S)"));
		dlDesc.add(_parser.parse("(implies S (only hasDamage SD))"));
		// dlDesc.add(parser.parse("(implies SO (some hasDamage D))"));
		_abox.getTBox().addAll(dlDesc);

		aks1.addUnfoldedDescription(Ashlar);
		d1.addUnfoldedDescription(MetalDamage);
		aks1.getSuccessors().put("hasDamage", d1.getNodeID());
		_reasoner.checkConsistency(_abox);
	}


	@Test
	public void testSimpleConsistentStoneDamageOntology()
		throws ParseException, EReasonerException, EInconsistentABoxException
	{
		SimpleKRSSParser parser = new SimpleKRSSParser(_termFactory);
		IDLClassReference<String, String, String> SpatialObject = _termFactory.getDLClassReference("S");
		IDLClassReference<String, String, String> Stone = _termFactory.getDLClassReference("S");
		IDLClassReference<String, String, String> Ashlar = _termFactory.getDLClassReference("A");
		IDLClassReference<String, String, String> Damage = _termFactory.getDLClassReference("D");
		IDLClassReference<String, String, String> StoneDamage = _termFactory.getDLClassReference("SD");
		IDLClassReference<String, String, String> MetalDamage = _termFactory.getDLClassReference("MD");
		IABoxNode<String, String, String> aks1 = _abox.getOrAddNamedNode("aks1", false);
		IABoxNode<String, String, String> d1 = _abox.getOrAddNamedNode("d1", false);

//			IABoxNode<String, String, String> aks2 = abox.getOrAddNamedNode("aks2");
//			IABoxNode<String, String, String> aks3 = abox.getOrAddNamedNode("aks3");

		Set<IDLRestriction<String, String, String>> dlDesc = new HashSet<IDLRestriction<String, String, String>>();
		dlDesc.add(parser.parse("(implies SO (some hasDamage _Thing_))"));
		dlDesc.add(parser.parse("(not (and SD MD))"));
		dlDesc.add(parser.parse("(implies A S)"));
		dlDesc.add(parser.parse("(implies S (only hasDamage SD))"));
		// dlDesc.add(parser.parse("(implies SO (some hasDamage D))"));
		_abox.getTBox().addAll(dlDesc);

		aks1.addUnfoldedDescription(Ashlar);
		d1.addUnfoldedDescription(StoneDamage);
		aks1.getSuccessors().put("hasDamage", d1.getNodeID());

		_reasoner.checkConsistency(_abox);
	}
	
	@Test
	public void unionImpliesTest()
		throws ParseException, EReasonerException, EInconsistentABoxException
	{
	
		IABoxNode<String, String, String> a = _abox.getOrAddNamedNode("a", false);
		a.addUnfoldedDescription(_parser.parse("(implies (or A B) (some r C)))"));
		a.addUnfoldedDescription(_parser.parse("A"));
		a.addUnfoldedDescription(_parser.parse("(not B)"));
		final Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox, false);
		assertFalse(results.isEmpty());
		
	}

	
	@Test
	public void multiUnionBranchTest()
		throws ParseException, EReasonerException, EInconsistentABoxException
	{
		IABoxNode<String, String, String> a = _abox.getOrAddNamedNode("a", false);
		String[] ors = new String[]{
			"(or A B)",
			"(or C D)",
			"(or E F)",
			"(or G H)",
			"(or K L)",
			"(or M N)",
			"(or O P)",
			"(or Q R)",
		};
		for (String or: ors)
			a.addUnfoldedDescription(_parser.parse(or));
		
		final Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox, false);
		assertFalse(results.isEmpty());
		assertEquals(0x1 << ors.length, results.size());
	}
	

//	@Test
//	public void semanticBranchingTest()
//		throws ParseException, EReasonerException
//	{
//		System.out.println("Semantic branching");
//		final SimpleKRSSParser parser = new SimpleKRSSParser(_termFactory);
//		final IABoxNode<String, String, String> node = _abox.createNode();
//		node.getTerms().add(parser.parse("X"));
//		node.getTerms().add(parser.parse("(or A B)"));
//		node.getTerms().add(parser.parse("(or A (not B))"));
//
//		Collection<? extends IABox<String, String, String>> aboxes = _reasoner.checkConsistency(_abox);
//		System.out.println(aboxes);
//	}
}

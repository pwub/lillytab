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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.util;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLRule;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.impl.SWRLTermFactory;
import java.text.ParseException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author peterw
 */
public class SimpleSWRLParserTest {

	private final ISWRLTermFactory<String, String, String, String> _swrlFactory = new SWRLTermFactory<>();
	private SimpleSWRLParser _parser;


		public SimpleSWRLParserTest()
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
		_parser = new SimpleSWRLParser(_swrlFactory);
	}


	@After
	public void tearDown()
	{
		_parser = null;
	}


	/**
	 * Test of parseAtom method, of class SimpleSWRLParser.
	 */
	@Test
	public void testParseAtom()
		throws ParseException
	{
		final ISWRLTerm<String, String, String, String> term1 = _parser.parseAtom("(r {a} ?y)");
		assertEquals(
			_swrlFactory.getSWRLObjectRoleAtom("r", _swrlFactory.getSWRLIndividualReference("a"),
											   _swrlFactory.getSWRLVariable("y")), term1);
		final ISWRLTerm<String, String, String, String> term2 = _parser.parseAtom("(C ?y)");
		assertEquals(
			_swrlFactory.getSWRLClassAtom("C", _swrlFactory.getSWRLVariable("y")), term2);
	}


	/**
	 * Test of parseTermList method, of class SimpleSWRLParser.
	 */
	@Test
	public void testParseTermList_String()
		throws ParseException
	{
		final ISWRLTerm<String, String, String, String> term1 = _parser.parseTermList("(r ?x ?y)");
		assertEquals(
			_swrlFactory.getSWRLObjectRoleAtom("r", _swrlFactory.getSWRLVariable("x"), _swrlFactory.getSWRLVariable("y")),
			term1);

		final ISWRLTerm<String, String, String, String> term2 = _parser.parseTermList("(C ?x)");
		assertEquals(
			_swrlFactory.getSWRLClassAtom("C", _swrlFactory.getSWRLVariable("x")),
			term2);

		final ISWRLTerm<String, String, String, String> term3 = _parser.parseTermList("(r ?x ?y), (C {a})");
		assertEquals(
			_swrlFactory.getSWRLIntersection(
			_swrlFactory.getSWRLClassAtom("C", _swrlFactory.getSWRLIndividualReference("a")),
			_swrlFactory.getSWRLObjectRoleAtom("r", _swrlFactory.getSWRLVariable("x"), _swrlFactory.getSWRLVariable("y"))),
			term3);
	}


	/**
	 * Test of parseRule method, of class SimpleSWRLParser.
	 */
	@Test
	public void testParseRule_String()
		throws ParseException
	{
		final ISWRLRule<String, String, String, String> rule1 = _parser.parseRule("(P ?x), (r ?x ?y) :- (Q ?z).");
		assertEquals(
			_swrlFactory.getSWRLIntersection(
			_swrlFactory.getSWRLClassAtom("P", _swrlFactory.getSWRLVariable("x")),
			_swrlFactory.getSWRLObjectRoleAtom("r", _swrlFactory.getSWRLVariable("x"), _swrlFactory.getSWRLVariable("y"))),
			rule1.getHead());
		assertEquals(_swrlFactory.getSWRLClassAtom("Q", _swrlFactory.getSWRLVariable("z")), rule1.getBody());
	}
}

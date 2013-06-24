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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.util;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLDataSomeRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLImplies;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLIndividualReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLLiteralReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLObjectIntersection;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLObjectSomeRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLObjectUnion;
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
public class SimpleKRSSParserTest {
	

	@BeforeClass
	public static void setUpClass()
	{
	}
	

	@AfterClass
	public static void tearDownClass()
	{
	}
	private IABoxFactory<String, String, String, String> _aboxFactory;
	private SimpleStringDLTermFactory _termFactory;
	private SimpleKRSSParser _parser;
	private IABox<String, String, String, String> _abox;
	
	
	public SimpleKRSSParserTest()
	{
	}
	

	@Before
	public void setUp()
	{
		_termFactory = new SimpleStringDLTermFactory();
		_parser = new SimpleKRSSParser(_termFactory);
		_aboxFactory = new ABoxFactory<>(_termFactory);
		_abox = _aboxFactory.createABox();
	}
	

	@After
	public void tearDown()
	{
		_abox = null;
		_aboxFactory = null;
		_parser = null;
		_termFactory = null;
	}
	

	@Test
	public void testParseNamedClass() throws ParseException
	{
		assertEquals(_termFactory.getDLClassReference("A"), _parser.parse("A"));
		assertFalse(_termFactory.getDLClassReference("B").equals(_parser.parse("A")));
		assertFalse(_termFactory.getDLIndividualReference("A").equals(_parser.parse("A")));
		assertFalse(_termFactory.getDLLiteralReference("A").equals(_parser.parse("A")));
	}
	

	@Test
	public void testParseIndividual() throws ParseException
	{
		assertEquals(_termFactory.getDLIndividualReference("a"), _parser.parse("{a}"));
		assertFalse(_termFactory.getDLIndividualReference("b").equals(_parser.parse("{a}")));
		assertFalse(_termFactory.getDLClassReference("a").equals(_parser.parse("{a}")));
		assertFalse(_termFactory.getDLLiteralReference("A").equals(_parser.parse("{a}")));
	}


	@Test
	public void testParseLiteral() throws ParseException
	{
		assertEquals(_termFactory.getDLLiteralReference("a"), _parser.parseRestriction("{\"a\"}"));
		assertFalse(_termFactory.getDLLiteralReference("b").equals(_parser.parseRestriction("{\"a\"}")));
		assertFalse(_termFactory.getDLIndividualReference("a").equals(_parser.parseRestriction("{\"a\"}")));
		assertFalse(_termFactory.getDLClassReference("a").equals(_parser.parseRestriction("{\"a\"}")));
	}
	
	@Test
	public void testParseUnion() throws ParseException
	{
		final IDLClassExpression<String, String, String, String> A = _termFactory.getDLClassReference("A");
		final IDLClassExpression<String, String, String, String> B = _termFactory.getDLClassReference("B");
		final IDLClassExpression<String, String, String, String> C = _termFactory.getDLClassReference("C");
		final IDLObjectUnion<String, String, String, String> union = _termFactory.getDLObjectUnion(A, B);
		
		assertEquals(union, _parser.parse("(or A B)"));
		assertEquals(union, _parser.parse("(or B A)"));
		assertFalse(_termFactory.getDLObjectUnion(B, C).equals(_parser.parse("(or B A)")));
		assertFalse(A.equals(_parser.parse("(or B A)")));
		assertFalse(B.equals(_parser.parseRestriction("(or B A)")));
	}	
	
	@Test
	public void testParseIntersection() throws ParseException
	{
		final IDLClassExpression<String, String, String, String> A = _termFactory.getDLClassReference("A");
		final IDLClassExpression<String, String, String, String> B = _termFactory.getDLClassReference("B");
		final IDLClassExpression<String, String, String, String> C = _termFactory.getDLClassReference("C");
		final IDLObjectIntersection<String, String, String, String> intersection = _termFactory.getDLObjectIntersection(A, B);
		
		assertEquals(intersection, _parser.parse("(and A B)"));
		assertEquals(intersection, _parser.parse("(and B A)"));
		assertFalse(_termFactory.getDLObjectUnion(B, C).equals(_parser.parse("(and B A)")));
		assertFalse(A.equals(_parser.parse("(and B A)")));
		assertFalse(B.equals(_parser.parseRestriction("(and B A)")));
	}		
	
	@Test
	public void testParseImplication() throws ParseException
	{
		final IDLClassExpression<String, String, String, String> A = _termFactory.getDLClassReference("A");
		final IDLClassExpression<String, String, String, String> B = _termFactory.getDLClassReference("B");
		final IDLClassExpression<String, String, String, String> C = _termFactory.getDLClassReference("C");
		final IDLImplies<String, String, String, String> intersection = _termFactory.getDLImplies(A, B);
		
		assertEquals(intersection, _parser.parse("(implies A B)"));
		assertFalse(intersection.equals(_parser.parse("(implies B A)")));
		assertFalse(_termFactory.getDLImplies(B, C).equals(_parser.parse("(implies B A)")));
		assertFalse(A.equals(_parser.parse("(implies B A)")));
		assertFalse(B.equals(_parser.parseRestriction("(implies B A)")));
	}	
	
	@Test
	public void testDataSome() throws ParseException
	{
		final IDLLiteralReference<String, String, String, String> a = _termFactory.getDLLiteralReference("a");
		final IDLDataSomeRestriction<String, String, String, String> dataSome = _termFactory.getDLDataSomeRestriction(
			"r", a);
		final IDLIndividualReference<String, String, String, String> aInd = _termFactory.getDLIndividualReference("a");
		final IDLObjectSomeRestriction<String, String, String, String> objSome = _termFactory.getDLObjectSomeRestriction(
			"r", aInd);
		
		assertEquals(dataSome, _parser.parse("(some r {\"a\"})"));
		assertFalse(a.equals(_parser.parse("(some r {\"a\"})")));
		assertFalse(objSome.equals(_parser.parse("(some r {\"a\"})")));
	}		
	
	@Test
	public void testObjectSome() throws ParseException
	{
		final IDLIndividualReference<String, String, String, String> aInd = _termFactory.getDLIndividualReference("a");
		final IDLObjectSomeRestriction<String, String, String, String> objSome = _termFactory.getDLObjectSomeRestriction(
			"r", aInd);
		final IDLLiteralReference<String, String, String, String> a = _termFactory.getDLLiteralReference("a");
		final IDLDataSomeRestriction<String, String, String, String> dataSome = _termFactory.getDLDataSomeRestriction(
			"r", a);
		
		assertEquals(objSome, _parser.parse("(some r {a})"));
		assertFalse(a.equals(_parser.parse("(some r {a})")));
		assertFalse(dataSome.equals(_parser.parse("(some r {a})")));
	}		
	
}
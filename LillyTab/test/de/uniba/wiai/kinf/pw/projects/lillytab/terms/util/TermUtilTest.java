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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.util;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl.DLTermFactory;
import java.text.ParseException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class TermUtilTest {
	private IDLTermFactory<String, String, String> _termFactory;
	private SimpleKRSSParser _parser;

    public TermUtilTest() {
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
    public void setUp() {
		_termFactory = new DLTermFactory<String, String, String>();
		_parser = new SimpleKRSSParser(_termFactory);
    }

    @After
    public void tearDown() {
		_termFactory = null;
		_parser = null;
    }

	/**
	 * Test of isSyntacticNegation method, of class TermUtil.
	 */
	@Test
	public void testIsSyntacticNegation()
		throws ParseException
	{
		assertTrue(TermUtil.isSyntacticNegation(
			_parser.parse("(not A)"),
			_parser.parse("A"),
			_termFactory));
		assertFalse(TermUtil.isSyntacticNegation(
			_parser.parse("A"),
			_parser.parse("A"),
			_termFactory));

		assertTrue(TermUtil.isSyntacticNegation(
			_parser.parse("(not (not A))"),
			_parser.parse("(not A)"),
			_termFactory));
		assertFalse(TermUtil.isSyntacticNegation(
			_parser.parse("(not (not A))"),
			_parser.parse("A"),
			_termFactory));
		assertTrue(TermUtil.isSyntacticNegation(
			_parser.parse("(not (not (not A)))"),
			_parser.parse("A"),
			_termFactory));
	}


	/**
	 * Test of isSyntacticSubClass method, of class TermUtil.
	 */
	@Test
	public void testIsSyntacticSubClass()
		throws ParseException
	{
		assertTrue(TermUtil.isSyntacticSubClass(
			_parser.parse("A"),
			_parser.parse("A"),
			_termFactory));
		assertFalse(TermUtil.isSyntacticSubClass(
			_parser.parse("A"),
			_parser.parse("B"),
			_termFactory));
		assertTrue(TermUtil.isSyntacticSubClass(
			_parser.parse("A"),
			_parser.parse("(and A B)"),
			_termFactory));

		assertTrue(TermUtil.isSyntacticSubClass(
			_parser.parse("(or A B)"),
			_parser.parse("A"),
			_termFactory));

		assertTrue(TermUtil.isSyntacticSubClass(
			_parser.parse("(or A B)"),
			_parser.parse("(and (and A B) C)"),
			_termFactory));

	}
}
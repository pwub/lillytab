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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.EInvalidTermException;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.impl.SWRLTermFactory;
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
public class SWRLRuleTest {
	private ISWRLTermFactory<String, String, String> _termFactory;

    public SWRLRuleTest() {
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
 		_termFactory = new SWRLTermFactory<String, String, String>();
   }

    @After
    public void tearDown() {
		_termFactory = null;
    }

	@Test()
	public void variableInBodyTest()
	{
		final ISWRLVariable<String, String, String> varA = _termFactory.getSWRLVariable("a");
		final ISWRLVariable<String, String, String> varB = _termFactory.getSWRLVariable("b");

		final ISWRLClassAtom<String, String, String> head = _termFactory.getSWRLClassAtom("A", varA);
		final ISWRLTerm<String, String, String> body = _termFactory.getSWRLIntersection(
			_termFactory.getSWRLClassAtom("A", varA),
			_termFactory.getSWRLClassAtom("B", varB)
		);

		ISWRLRule<String, String, String> rule = _termFactory.getSWRLRule(head, body);
		assertTrue(rule.getVariables().contains(varA));
		assertTrue(rule.getVariables().contains(varB));
	}

//	@Test(expected=EInvalidTermException.class)
//	public void variableNotInBodyTest()
//	{
//		final ISWRLVariable<String, String, String> varA = _termFactory.getSWRLVariable("a");
//		final ISWRLVariable<String, String, String> varB = _termFactory.getSWRLVariable("b");
//
//		final ISWRLClassAtom<String, String, String> head = _termFactory.getSWRLClassAtom("A", varA);
//		final ISWRLAtomicTerm<String, String, String> body = _termFactory.getSWRLClassAtom("B", varB);
//
//		ISWRLRule<String, String, String> rule = _termFactory.getSWRLRule(head, body);
//	}
}

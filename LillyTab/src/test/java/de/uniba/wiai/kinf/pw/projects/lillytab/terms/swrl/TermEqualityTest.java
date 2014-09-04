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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.impl.SWRLTermFactory;
import java.util.ArrayList;
import java.util.Collection;
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
public class TermEqualityTest
{
	private ISWRLTermFactory<String, String, String, String> _termFactory;

	public TermEqualityTest()
	{
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
	public void setUp()
	{
		_termFactory = new SWRLTermFactory<>();
	}

	@After
	public void tearDown()
	{
		_termFactory = null;
	}

	@Test
	public void testIntersectionEquality()
	{
		final ISWRLIArgument<String, String, String, String> varA = _termFactory.getSWRLVariable("a");
		final ISWRLIArgument<String, String, String, String> varB = _termFactory.getSWRLVariable("b");
		final ISWRLIArgument<String, String, String, String> nomC = _termFactory.getSWRLIndividualReference("c");
		final ISWRLClassAtom<String, String, String, String> classAtom1 = _termFactory.getSWRLClassAtom("A", varA);
		final ISWRLRoleAtom<String, String, String, String> roleAtom1 = _termFactory.getSWRLObjectRoleAtom("r1", varA,
																										   varB);
		final ISWRLRoleAtom<String, String, String, String> roleAtom2 = _termFactory.getSWRLObjectRoleAtom("r2", varB,
																										   nomC);

		final Collection<ISWRLAtomicTerm<String, String, String, String>> terms1 = new ArrayList<>();
		terms1.add(roleAtom2);
		terms1.add(roleAtom1);
		terms1.add(classAtom1);
		final Collection<ISWRLAtomicTerm<String, String, String, String>> terms2 = new ArrayList<>();
		terms2.add(classAtom1);
		terms2.add(roleAtom2);
		terms2.add(roleAtom1);

		final ISWRLIntersection<String, String, String, String> int1 = _termFactory.getSWRLIntersection(terms1);
		final ISWRLIntersection<String, String, String, String> int2 = _termFactory.getSWRLIntersection(terms2);

		assertEquals(int1, int2);
	}

	public void testRoleTermInequality()
	{
		final ISWRLIArgument<String, String, String, String> varA = _termFactory.getSWRLVariable("a");
		final ISWRLIArgument<String, String, String, String> varB = _termFactory.getSWRLVariable("b");

		final ISWRLRoleAtom<String, String, String, String> ra1 = _termFactory.getSWRLObjectRoleAtom("r", varA, varB);
		final ISWRLRoleAtom<String, String, String, String> ra2 = _termFactory.getSWRLObjectRoleAtom("r", varB, varA);
		final ISWRLRoleAtom<String, String, String, String> ra3 = _termFactory.getSWRLObjectRoleAtom("p", varA, varB);

		assertFalse(ra1.equals(ra2));
		assertFalse(ra1.equals(ra3));
		assertFalse(ra2.equals(ra3));

	}
}

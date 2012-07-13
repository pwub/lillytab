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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl.DLTermFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * <p>
 * Test term equality for
 * for sample implementation {@link de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl}.
 * </p>
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class TermEqualityTest
{
	private IDLTermFactory<String, String, String> _termFactory;

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
		_termFactory = new DLTermFactory<String, String, String>();
	}

	@After
	public void tearDown()
	{
		_termFactory = null;
	}

	@Test
	public void testAtomEquality()
	{
		IAtom a0 = _termFactory.getDLClassReference("A");
		IAtom a1 = _termFactory.getDLClassReference("A");
		IAtom b = _termFactory.getDLClassReference("B");

		assertEquals(a0, a1);

		assertFalse(a0.equals(b));
		assertFalse(a1.equals(b));
		assertFalse(b.equals(a0));
		assertFalse(b.equals(a1));
	}

	@Test
	public void testNegationEquality()
	{
		IDLRestriction<String, String, String> a = _termFactory.getDLClassReference("A");
		IDLRestriction<String, String, String> b = _termFactory.getDLClassReference("B");

		IDLNegation na0 = _termFactory.getDLNegation(a);
		IDLNegation na1 = _termFactory.getDLNegation(a);
		IDLNegation nb = _termFactory.getDLNegation(b);

		assertEquals(na0, na1);
		assertFalse(na0.equals(nb));
		assertFalse(nb.equals(na0));

		assertFalse(na1.equals(nb));
		assertFalse(nb.equals(na1));
	}

	@Test
	public void testUnionEquality()
	{
		IDLRestriction<String, String, String> a = _termFactory.getDLClassReference("A");
		IDLRestriction<String, String, String> b = _termFactory.getDLClassReference("B");
		IDLRestriction<String, String, String> c = _termFactory.getDLClassReference("C");

		IDLRestriction<String, String, String> u0 = _termFactory.getDLUnion(a, b);
		IDLRestriction<String, String, String> u1 = _termFactory.getDLUnion(a, b);
		IDLRestriction<String, String, String> u2 = _termFactory.getDLUnion(b, a);
		IDLRestriction<String, String, String> u3 = _termFactory.getDLUnion(a, c);

		assertEquals(u0, u1);
		assertEquals(u0, u2);
		assertEquals(u1, u2);

		assertEquals(u1, u0);
		assertEquals(u2, u0);
		assertEquals(u0, u1);

		assertFalse(u0.equals(u3));
		assertFalse(u3.equals(u0));
	}

	@Test
	public void testIntersectionEquality()
	{
		IDLRestriction<String, String, String> a = _termFactory.getDLClassReference("A");
		IDLRestriction<String, String, String> b = _termFactory.getDLClassReference("B");
		IDLRestriction<String, String, String> c = _termFactory.getDLClassReference("C");

		IDLRestriction<String, String, String> i0 = _termFactory.getDLIntersection(a, b);
		IDLRestriction<String, String, String> i1 = _termFactory.getDLIntersection(a, b);
		IDLRestriction<String, String, String> i2 = _termFactory.getDLIntersection(b, a);
		IDLRestriction<String, String, String> i3 = _termFactory.getDLIntersection(a, c);

		assertEquals(i0, i1);
		assertEquals(i0, i2);
		assertEquals(i1, i2);

		assertEquals(i1, i0);
		assertEquals(i2, i0);
		assertEquals(i0, i1);

		assertFalse(i0.equals(i3));
		assertFalse(i3.equals(i0));
	}

	@Test
	public void testSomeRestrictionEquality()
	{
		IDLRestriction<String, String, String> a = _termFactory.getDLClassReference("A");
		IDLRestriction<String, String, String> b = _termFactory.getDLClassReference("B");
		String r0 = "r0";
		String r1 = "r1";

		IDLRestriction<String, String, String> rest0 = _termFactory.getDLSomeRestriction(r0, a);
		IDLRestriction<String, String, String> rest1 = _termFactory.getDLSomeRestriction(r0, a);
		IDLRestriction<String, String, String> rest2 = _termFactory.getDLSomeRestriction(r0, b);
		IDLRestriction<String, String, String> rest3 = _termFactory.getDLSomeRestriction(r1, a);

		assertEquals(rest0, rest1);
		assertFalse(rest1.equals(rest2));
		assertFalse(rest1.equals(rest3));
		assertFalse(rest2.equals(rest3));

		assertFalse(rest2.equals(rest1));
		assertFalse(rest3.equals(rest1));
		assertFalse(rest3.equals(rest2));
	}

	@Test
	public void testAllRestrictionEquality()
	{
		IDLRestriction<String, String, String> a = _termFactory.getDLClassReference("A");
		IDLRestriction<String, String, String> b = _termFactory.getDLClassReference("B");
		String r0 = "r0";
		String r1 = "r1";

		IDLRestriction<String, String, String> rest0 = _termFactory.getDLAllRestriction(r0, a);
		IDLRestriction<String, String, String> rest1 = _termFactory.getDLAllRestriction(r0, a);
		IDLRestriction<String, String, String> rest2 = _termFactory.getDLAllRestriction(r0, b);
		IDLRestriction<String, String, String> rest3 = _termFactory.getDLAllRestriction(r1, a);

		assertEquals(rest0, rest1);
		assertFalse(rest1.equals(rest2));
		assertFalse(rest1.equals(rest3));
		assertFalse(rest2.equals(rest3));

		assertFalse(rest2.equals(rest1));
		assertFalse(rest3.equals(rest1));
		assertFalse(rest3.equals(rest2));
	}


	/**
	 * Cross non-equality test for all term classes.
	 **/
	@Test
	@SuppressWarnings("unchecked")
	public void testCrossEquality()
	{
		IDLRestriction<String, String, String> a = _termFactory.getDLClassReference("A");
		IDLRestriction<String, String, String> b = _termFactory.getDLClassReference("B");
		IDLRestriction<String, String, String> neg = _termFactory.getDLNegation(a);
		IDLRestriction<String, String, String> union = _termFactory.getDLUnion(a, b);
		IDLRestriction<String, String, String> intersection = _termFactory.getDLIntersection(a, b);
		IDLRestriction<String, String, String> some = _termFactory.getDLSomeRestriction("r", a);
		IDLRestriction<String, String, String> all = _termFactory.getDLAllRestriction("r", a);

		IDLRestriction[] allDescriptions = new IDLRestriction[]{
			a, b, neg, union, intersection, some, all
		};

		for (IDLRestriction<String, String, String> d0: allDescriptions)
			for (IDLRestriction<String, String, String> d1: allDescriptions)
				if (d0 != d1)
					assertFalse(d0.equals(d1));
	}

	@Test
	public void testImpliesNonEquals()
	{
		/* regression test */
		IDLRestriction<String, String, String> a = _termFactory.getDLClassReference("A");
		IDLRestriction<String, String, String> b = _termFactory.getDLClassReference("B");
		IDLImplies<String, String, String> imp1 = _termFactory.getDLImplies(a, b);
		IDLImplies<String, String, String> imp2 = _termFactory.getDLImplies(b, a);
		assertNotSame(imp1, imp2);
		assertFalse(imp1.equals(imp2));
	}
}

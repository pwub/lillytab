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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleStringDLTermFactory;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *  Test term equality for for sample implementation {@link de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl}. 
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class TermEqualityTest {


	@BeforeClass
	public static void setUpClass()
		throws Exception
	{
	}


	@AfterClass
	public static void tearDownClass()
		throws Exception
	{
	}

	private IDLTermFactory<String, String, String, String> _termFactory;


	public TermEqualityTest()
	{
	}


	@Before
	public void setUp()
	{
		_termFactory = new SimpleStringDLTermFactory();
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
		IDLClassExpression<String, String, String, String> a = _termFactory.getDLClassReference("A");
		IDLClassExpression<String, String, String, String> b = _termFactory.getDLClassReference("B");

		IDLObjectNegation na0 = _termFactory.getDLObjectNegation(a);
		IDLObjectNegation na1 = _termFactory.getDLObjectNegation(a);
		IDLObjectNegation nb = _termFactory.getDLObjectNegation(b);

		assertEquals(na0, na1);
		assertFalse(na0.equals(nb));
		assertFalse(nb.equals(na0));

		assertFalse(na1.equals(nb));
		assertFalse(nb.equals(na1));
	}


	@Test
	public void testUnionEquality()
	{
		IDLClassExpression<String, String, String, String> a = _termFactory.getDLClassReference("A");
		IDLClassExpression<String, String, String, String> b = _termFactory.getDLClassReference("B");
		IDLClassExpression<String, String, String, String> c = _termFactory.getDLClassReference("C");

		IDLClassExpression<String, String, String, String> u0 = _termFactory.getDLObjectUnion(a, b);
		IDLClassExpression<String, String, String, String> u1 = _termFactory.getDLObjectUnion(a, b);
		IDLClassExpression<String, String, String, String> u2 = _termFactory.getDLObjectUnion(b, a);
		IDLClassExpression<String, String, String, String> u3 = _termFactory.getDLObjectUnion(a, c);

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
		IDLClassExpression<String, String, String, String> a = _termFactory.getDLClassReference("A");
		IDLClassExpression<String, String, String, String> b = _termFactory.getDLClassReference("B");
		IDLClassExpression<String, String, String, String> c = _termFactory.getDLClassReference("C");

		IDLClassExpression<String, String, String, String> i0 = _termFactory.getDLObjectIntersection(a, b);
		IDLClassExpression<String, String, String, String> i1 = _termFactory.getDLObjectIntersection(a, b);
		IDLClassExpression<String, String, String, String> i2 = _termFactory.getDLObjectIntersection(b, a);
		IDLClassExpression<String, String, String, String> i3 = _termFactory.getDLObjectIntersection(a, c);

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
		IDLClassExpression<String, String, String, String> a = _termFactory.getDLClassReference("A");
		IDLClassExpression<String, String, String, String> b = _termFactory.getDLClassReference("B");
		String r0 = "r0";
		String r1 = "r1";

		IDLClassExpression<String, String, String, String> rest0 = _termFactory.getDLObjectSomeRestriction(r0, a);
		IDLClassExpression<String, String, String, String> rest1 = _termFactory.getDLObjectSomeRestriction(r0, a);
		IDLClassExpression<String, String, String, String> rest2 = _termFactory.getDLObjectSomeRestriction(r0, b);
		IDLClassExpression<String, String, String, String> rest3 = _termFactory.getDLObjectSomeRestriction(r1, a);

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
		IDLClassExpression<String, String, String, String> a = _termFactory.getDLClassReference("A");
		IDLClassExpression<String, String, String, String> b = _termFactory.getDLClassReference("B");
		String r0 = "r0";
		String r1 = "r1";

		IDLClassExpression<String, String, String, String> rest0 = _termFactory.getDLObjectAllRestriction(r0, a);
		IDLClassExpression<String, String, String, String> rest1 = _termFactory.getDLObjectAllRestriction(r0, a);
		IDLClassExpression<String, String, String, String> rest2 = _termFactory.getDLObjectAllRestriction(r0, b);
		IDLClassExpression<String, String, String, String> rest3 = _termFactory.getDLObjectAllRestriction(r1, a);

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
	 *
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testCrossEquality()
	{
		IDLClassExpression<String, String, String, String> a = _termFactory.getDLClassReference("A");
		IDLClassExpression<String, String, String, String> b = _termFactory.getDLClassReference("B");
		IDLClassExpression<String, String, String, String> neg = _termFactory.getDLObjectNegation(a);
		IDLClassExpression<String, String, String, String> union = _termFactory.getDLObjectUnion(a, b);
		IDLClassExpression<String, String, String, String> intersection = _termFactory.getDLObjectIntersection(a, b);
		IDLClassExpression<String, String, String, String> some = _termFactory.getDLObjectSomeRestriction("r", a);
		IDLClassExpression<String, String, String, String> all = _termFactory.getDLObjectAllRestriction("r", a);

		IDLClassExpression[] allDescriptions = new IDLClassExpression[]{
			a, b, neg, union, intersection, some, all
		};

		for (IDLClassExpression<String, String, String, String> d0 : allDescriptions) {
			for (IDLClassExpression<String, String, String, String> d1 : allDescriptions) {
				if (d0 != d1) {
					assertFalse(d0.equals(d1));
				}
			}
		}
	}


	@Test
	public void testImpliesNonEquals()
	{
		/* regression test */
		IDLClassExpression<String, String, String, String> a = _termFactory.getDLClassReference("A");
		IDLClassExpression<String, String, String, String> b = _termFactory.getDLClassReference("B");
		IDLImplies<String, String, String, String> imp1 = _termFactory.getDLImplies(a, b);
		IDLImplies<String, String, String, String> imp2 = _termFactory.getDLImplies(b, a);
		assertNotSame(imp1, imp2);
		assertFalse(imp1.equals(imp2));
	}
}

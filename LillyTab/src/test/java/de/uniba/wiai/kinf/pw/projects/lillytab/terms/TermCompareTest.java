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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleStringDLTermFactory;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * Test {@link IDLTerm#compareTo(java.lang.Object) } total ordering for sample implementation
 * {@link de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl}.
 * 
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public class TermCompareTest {

	final String aClassName = "A";
	final String bClassName = "B";
	private IDLTermFactory<String, String, String, String> _termFactory;
	private IDLClassReference<String, String, String, String> _a0;
	private IDLClassReference<String, String, String, String> _a1;
	private IDLClassReference<String, String, String, String> _b0;
	private IDLClassReference<String, String, String, String> _b1;


	public TermCompareTest()
	{
	}


	@Before
	public void setUp()
	{
		_termFactory = new SimpleStringDLTermFactory();
		_a0 = _termFactory.getDLClassReference(aClassName);
		_a1 = _termFactory.getDLClassReference(aClassName);
		_b0 = _termFactory.getDLClassReference(bClassName);
		_b1 = _termFactory.getDLClassReference(bClassName);
	}


	@After
	public void tearDown()
	{
		_termFactory = null;
	}


	@Test
	public void testClassReferenceOrder()
	{
		assertTrue(aClassName.compareTo(bClassName) < 0);
		assertTrue(_a0.compareTo(_a0) == 0);
		assertTrue(_a0.compareTo(_b0) < 0);
		assertTrue(_b0.compareTo(_a0) > 0);
		assertTrue(_a0.compareTo(_a1) == 0);
	}


	@Test
	public void testNegationOrder()
	{
		IDLObjectNegation<String, String, String, String> negA0 = _termFactory.getDLObjectNegation(_a0);
		IDLObjectNegation<String, String, String, String> negA1 = _termFactory.getDLObjectNegation(_a1);
		IDLObjectNegation<String, String, String, String> negB0 = _termFactory.getDLObjectNegation(_b0);

		assertTrue(negA0.compareTo(negA0) == 0);
		assertTrue(negA0.compareTo(_a0) > 0);
		assertTrue(negA0.compareTo(_b0) > 0);

		assertTrue(negB0.compareTo(_a0) > 0);
		assertTrue(negB0.compareTo(_b0) > 0);

		assertTrue(negA0.compareTo(negA1) == 0);
		assertTrue(negA0.compareTo(negB0) < 0);
	}


	@Test
	public void testUnionOrder()
	{
		IDLObjectUnion<String, String, String, String> u0 = _termFactory.getDLObjectUnion(_a0, _b0);
		IDLObjectUnion<String, String, String, String> u1 = _termFactory.getDLObjectUnion(_a1, _b1);
		IDLObjectUnion<String, String, String, String> u2 = _termFactory.getDLObjectUnion(_b1, _a0);
		IDLObjectUnion<String, String, String, String> u3 = _termFactory.getDLObjectUnion(_a0, _a0);

		assertTrue(u0.compareTo(u0) == 0);
		assertTrue(u0.compareTo(u1) == 0);
		assertTrue(u0.compareTo(u2) == 0);
		assertTrue(u0.compareTo(u3) > 0);
	}


	@Test
	public void testIntersectionOrder()
	{
		IDLObjectIntersection<String, String, String, String> i0 = _termFactory.getDLObjectIntersection(_a0, _b0);
		IDLObjectIntersection<String, String, String, String> i1 = _termFactory.getDLObjectIntersection(_a1, _b1);
		IDLObjectIntersection<String, String, String, String> i2 = _termFactory.getDLObjectIntersection(_b1, _a0);
		IDLObjectIntersection<String, String, String, String> i3 = _termFactory.getDLObjectIntersection(_a0, _a0);

		assertTrue(i0.compareTo(i0) == 0);
		assertTrue(i0.compareTo(i1) == 0);
		assertTrue(i0.compareTo(i2) == 0);
		assertTrue(i0.compareTo(i3) > 0);
	}


	@Test
	public void testSomeRestrictionOrder()
	{
		String r0 = "r0";
		String r1 = "r1";
		IDLSomeRestriction<String, String, String, String> sr0 = _termFactory.getDLObjectSomeRestriction(r0, _a0);
		IDLSomeRestriction<String, String, String, String> sr1 = _termFactory.getDLObjectSomeRestriction(r0, _a1);
		IDLSomeRestriction<String, String, String, String> sr2 = _termFactory.getDLObjectSomeRestriction(r1, _a0);
		IDLSomeRestriction<String, String, String, String> sr3 = _termFactory.getDLObjectSomeRestriction(r0, _b0);

		assertTrue(sr0.compareTo(sr0) == 0);
		assertTrue(sr0.compareTo(sr1) == 0);
		assertTrue(sr0.compareTo(sr2) < 0);
		assertTrue(sr0.compareTo(sr3) < 0);
		assertTrue(sr2.compareTo(sr3) > 0);
	}


	@Test
	public void testAllRestrictionOrder()
	{
		String r0 = "r0";
		String r1 = "r1";
		IDLAllRestriction<String, String, String, String> ar0 = _termFactory.getDLObjectAllRestriction(r0, _a0);
		IDLAllRestriction<String, String, String, String> ar1 = _termFactory.getDLObjectAllRestriction(r0, _a1);
		IDLAllRestriction<String, String, String, String> ar2 = _termFactory.getDLObjectAllRestriction(r1, _a0);
		IDLAllRestriction<String, String, String, String> ar3 = _termFactory.getDLObjectAllRestriction(r0, _b0);

		assertTrue(ar0.compareTo(ar0) == 0);
		assertTrue(ar0.compareTo(ar1) == 0);
		assertTrue(ar0.compareTo(ar2) < 0);
		assertTrue(ar0.compareTo(ar3) < 0);
		assertTrue(ar2.compareTo(ar3) > 0);
	}


	@Test
	public void testClassReferenceCrossOrder()
	{
		String r0 = "r0";
		IDLObjectIntersection<String, String, String, String> i0 = _termFactory.getDLObjectIntersection(_a0, _b0);
		IDLObjectUnion<String, String, String, String> u0 = _termFactory.getDLObjectUnion(_a0, _b0);
		IDLObjectNegation<String, String, String, String> neg0 = _termFactory.getDLObjectNegation(_a0);
		IDLSomeRestriction<String, String, String, String> sr0 = _termFactory.getDLObjectSomeRestriction(r0, _a0);
		IDLAllRestriction<String, String, String, String> ar0 = _termFactory.getDLObjectAllRestriction(r0, _a0);

		assertTrue(_a0.compareTo(neg0) < 0);
		assertTrue(_a0.compareTo(i0) < 0);
		assertTrue(_a0.compareTo(u0) < 0);
		assertTrue(_a0.compareTo(sr0) < 0);
		assertTrue(_a0.compareTo(ar0) < 0);
	}


	@Test
	public void testNegationCrossOrder()
	{
		String r0 = "r0";
		IDLObjectIntersection<String, String, String, String> i0 = _termFactory.getDLObjectIntersection(_a0, _b0);
		IDLObjectUnion<String, String, String, String> u0 = _termFactory.getDLObjectUnion(_a0, _b0);
		IDLObjectNegation<String, String, String, String> neg0 = _termFactory.getDLObjectNegation(_a0);
		IDLSomeRestriction<String, String, String, String> sr0 = _termFactory.getDLObjectSomeRestriction(r0, _a0);
		IDLAllRestriction<String, String, String, String> ar0 = _termFactory.getDLObjectAllRestriction(r0, _a0);

		assertTrue(neg0.compareTo(_a0) > 0);
		assertTrue(neg0.compareTo(i0) > 0);
		assertTrue(neg0.compareTo(u0) > 0);
		assertTrue(neg0.compareTo(sr0) > 0);
		assertTrue(neg0.compareTo(ar0) > 0);
	}


	@Test
	public void testIntersectionCrossOrder()
	{
		String r0 = "r0";
		IDLObjectIntersection<String, String, String, String> i0 = _termFactory.getDLObjectIntersection(_a0, _b0);
		IDLObjectUnion<String, String, String, String> u0 = _termFactory.getDLObjectUnion(_a0, _b0);
		IDLObjectNegation<String, String, String, String> neg0 = _termFactory.getDLObjectNegation(_a0);
		IDLSomeRestriction<String, String, String, String> sr0 = _termFactory.getDLObjectSomeRestriction(r0, _a0);
		IDLAllRestriction<String, String, String, String> ar0 = _termFactory.getDLObjectAllRestriction(r0, _a0);

		assertTrue(i0.compareTo(_a0) >0);
		assertTrue(i0.compareTo(neg0) < 0);
		assertTrue(i0.compareTo(u0) < 0);
		assertTrue(i0.compareTo(sr0) < 0);
		assertTrue(i0.compareTo(ar0) < 0);
	}


	@Test
	public void testUnionCrossOrder()
	{
		String r0 = "r0";
		IDLObjectIntersection<String, String, String, String> i0 = _termFactory.getDLObjectIntersection(_a0, _b0);
		IDLObjectUnion<String, String, String, String> u0 = _termFactory.getDLObjectUnion(_a0, _b0);
		IDLObjectNegation<String, String, String, String> neg0 = _termFactory.getDLObjectNegation(_a0);
		IDLSomeRestriction<String, String, String, String> sr0 = _termFactory.getDLObjectSomeRestriction(r0, _a0);
		IDLAllRestriction<String, String, String, String> ar0 = _termFactory.getDLObjectAllRestriction(r0, _a0);


		assertTrue(u0.compareTo(_a0) > 0);
		assertTrue(u0.compareTo(neg0) < 0);
		assertTrue(u0.compareTo(i0) > 0);
		assertTrue(u0.compareTo(sr0) < 0);
		assertTrue(u0.compareTo(ar0) < 0);
	}


	@Test
	public void testSomeRestrictionCrossOrder()
	{
		String r0 = "r0";
		IDLObjectIntersection<String, String, String, String> i0 = _termFactory.getDLObjectIntersection(_a0, _b0);
		IDLObjectUnion<String, String, String, String> u0 = _termFactory.getDLObjectUnion(_a0, _b0);
		IDLObjectNegation<String, String, String, String> neg0 = _termFactory.getDLObjectNegation(_a0);
		IDLSomeRestriction<String, String, String, String> sr0 = _termFactory.getDLObjectSomeRestriction(r0, _a0);
		IDLAllRestriction<String, String, String, String> ar0 = _termFactory.getDLObjectAllRestriction(r0, _a0);

		assertTrue(sr0.compareTo(_a0) > 0);
		assertTrue(sr0.compareTo(neg0) < 0);
		assertTrue(sr0.compareTo(i0) > 0);
		assertTrue(sr0.compareTo(u0) > 0);
		assertTrue(sr0.compareTo(ar0) < 0);
	}


	@Test
	public void testAllRestrictionCrossOrder()
	{
		String r0 = "r0";
		IDLObjectIntersection<String, String, String, String> i0 = _termFactory.getDLObjectIntersection(_a0, _b0);
		IDLObjectUnion<String, String, String, String> u0 = _termFactory.getDLObjectUnion(_a0, _b0);
		IDLObjectNegation<String, String, String, String> neg0 = _termFactory.getDLObjectNegation(_a0);
		IDLSomeRestriction<String, String, String, String> sr0 = _termFactory.getDLObjectSomeRestriction(r0, _a0);
		IDLAllRestriction<String, String, String, String> ar0 = _termFactory.getDLObjectAllRestriction(r0, _a0);

		assertTrue(ar0.compareTo(_a0) > 0);
		assertTrue(ar0.compareTo(neg0) < 0);
		assertTrue(ar0.compareTo(i0) > 0);
		assertTrue(ar0.compareTo(u0) > 0);
		assertTrue(ar0.compareTo(sr0) > 0);
	}
}

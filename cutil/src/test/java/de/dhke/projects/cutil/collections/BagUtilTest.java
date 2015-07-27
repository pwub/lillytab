/**
 * (c) 2009-2014 Peter Wullinger
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
package de.dhke.projects.cutil.collections;

import java.util.Arrays;
import org.apache.commons.collections15.Bag;
import org.apache.commons.collections15.bag.TreeBag;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author peter
 */
public class BagUtilTest
{
	
	public BagUtilTest()
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
	}
	
	@After
	public void tearDown()
	{
	}

	/**
	 * Test of unionBags method, of class BagUtil.
	 */
	@Test
	public void testUnionBags()
	{
		System.out.println("unionBags");
		final Bag<String> b0 = new TreeBag<>(Arrays.asList("A", "B", "C"));
		final Bag<String> b1 = new TreeBag<>(Arrays.asList("A", "C"));
		
		final Bag<String> union = new TreeBag<>();
		BagUtil.unionBags(b0, b1, union);
		assertTrue(union.contains("A"));
		assertTrue(union.contains("B"));
		assertTrue(union.contains("C"));
		assertEquals(1, union.getCount("A"));
		assertEquals(1, union.getCount("B"));
		assertEquals(1, union.getCount("C"));
	}

	/**
	 * Test of intersectBags method, of class BagUtil.
	 */
	@Test
	public void testIntersectBags()
	{
		System.out.println("intersectBags");
		final Bag<String> b0 = new TreeBag<>(Arrays.asList("A", "B", "C"));
		final Bag<String> b1 = new TreeBag<>(Arrays.asList("A", "C"));
		
		final Bag<String> intersection = new TreeBag<>();
		BagUtil.intersectBags(b0, b1, intersection);
		assertTrue(intersection.contains("A"));
		assertFalse(intersection.contains("B"));
		assertTrue(intersection.contains("C"));
		assertEquals(1, intersection.getCount("A"));
		assertEquals(0, intersection.getCount("B"));
		assertEquals(1, intersection.getCount("C"));
	}
	
}

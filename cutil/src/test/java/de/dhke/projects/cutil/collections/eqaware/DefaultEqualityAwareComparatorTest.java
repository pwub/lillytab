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
package de.dhke.projects.cutil.collections.eqaware;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class DefaultEqualityAwareComparatorTest {

	public DefaultEqualityAwareComparatorTest()
	{
	}


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


	@Before
	public void setUp()
	{
	}


	@After
	public void tearDown()
	{
	}


	/**
	 * Test of compare method, of class DefaultEqualityAwareComparator.
	 */
	@Test
	public void testCompare()
	{
		final DefaultEqualityAwareComparator<SampleComparable> comparator = new DefaultEqualityAwareComparator<>();
		final SampleComparable c1_0 = new SampleComparable(1, 0);
		final SampleComparable c2_0 = new SampleComparable(2, 0);
		final SampleComparable c1_1 = new SampleComparable(1, 1);
		final SampleComparable c2_1 = new SampleComparable(2, 1);
		assertEquals(c1_0, c2_0);
		assertEquals(c1_1, c2_1);
		assertFalse(c1_0.equals(c1_1));
		assertFalse(c1_0.equals(c2_1));
		assertFalse(c2_0.equals(c1_1));
		assertFalse(c2_0.equals(c2_1));

		assertTrue(comparator.compare(c1_0, c1_0) == 0);
		assertTrue(comparator.compare(c1_0, c2_0) > 0);
		assertTrue(comparator.compare(c1_0, c2_1) > 0);
		assertTrue(comparator.compare(c2_1, c1_0) < 0);

		assertTrue(comparator.compare(c1_1, c1_1) == 0);
		assertTrue(comparator.compare(c1_1, c2_1) > 0);
		assertTrue(comparator.compare(c1_1, c2_0) > 0);
		assertTrue(comparator.compare(c2_0, c1_1) < 0);
	}

	class SampleComparable
		implements Comparable<SampleComparable> {

		private int _order;
		private int _value;


		SampleComparable(int order, int value)
		{
			_order = order;
			_value = value;
		}


		@Override
		public int compareTo(SampleComparable other)
		{
			return other._order - _order;
		}


		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if ((obj instanceof SampleComparable) && (obj.getClass().isInstance(this))) {
				return _value == ((SampleComparable) obj)._value;
			} else
				return false;
		}


		@Override
		public int hashCode()
		{
			return _value;
		}
	}
}

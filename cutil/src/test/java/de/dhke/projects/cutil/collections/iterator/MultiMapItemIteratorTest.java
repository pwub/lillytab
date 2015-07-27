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
package de.dhke.projects.cutil.collections.iterator;

import de.dhke.projects.cutil.collections.map.GenericMultiHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.keyvalue.DefaultMapEntry;
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
public class MultiMapItemIteratorTest {

	MultiMap<String, String> _map;


	public MultiMapItemIteratorTest()
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
		_map = new GenericMultiHashMap<>();
	}


	@After
	public void tearDown()
	{
		_map = null;
	}


	@Test
	public void testHasNextEmpty()
	{
		final Iterator<Map.Entry<String, String>> iter =
			MultiMapEntryIterator.decorate(_map);
		assertFalse(iter.hasNext());
	}


	@Test
	public void testHasNextSingleEntry()
	{
		_map.put("A", "1");

		final Iterator<Map.Entry<String, String>> iter =
			MultiMapEntryIterator.decorate(_map);
		assertTrue(iter.hasNext());
		assertEquals(new DefaultMapEntry<>("A", "1"), iter.next());
		assertFalse(iter.hasNext());
	}


	@Test
	public void testHasNextTwoEntry()
	{
		_map.put("A", "1");
		_map.put("B", "2");

		final Iterator<Map.Entry<String, String>> iter =
			MultiMapEntryIterator.decorate(_map);
		assertTrue(iter.hasNext());
		assertEquals(new DefaultMapEntry<>("A", "1"), iter.next());
		assertTrue(iter.hasNext());
		assertEquals(new DefaultMapEntry<>("B", "2"), iter.next());
		assertFalse(iter.hasNext());
	}


	@Test
	public void testHasNextDoubleEntry()
	{
		_map.put("A", "1");
		_map.put("A", "2");

		final Iterator<Map.Entry<String, String>> iter =
			MultiMapEntryIterator.decorate(_map);
		assertTrue(iter.hasNext());
		assertEquals(new DefaultMapEntry<>("A", "1"), iter.next());
		assertTrue(iter.hasNext());
		assertEquals(new DefaultMapEntry<>("A", "2"), iter.next());
		assertFalse(iter.hasNext());
	}


	@Test
	public void testHasNextDoubleDoubleEntry()
	{
		_map.put("A", "1");
		_map.put("A", "2");
		_map.put("B", "3");
		_map.put("B", "4");

		final Iterator<Map.Entry<String, String>> iter =
			MultiMapEntryIterator.decorate(_map);
		assertTrue(iter.hasNext());
		assertEquals(new DefaultMapEntry<>("A", "1"), iter.next());
		assertTrue(iter.hasNext());
		assertEquals(new DefaultMapEntry<>("A", "2"), iter.next());
		assertTrue(iter.hasNext());
		assertEquals(new DefaultMapEntry<>("B", "3"), iter.next());
		assertTrue(iter.hasNext());
		assertEquals(new DefaultMapEntry<>("B", "4"), iter.next());
		assertFalse(iter.hasNext());
	}


	@Test(expected = NoSuchElementException.class)
	public void testNextEmpty()
	{
		final Iterator<Map.Entry<String, String>> iter =
			MultiMapEntryIterator.decorate(_map);
		iter.next();
	}


	@Test
	public void testNextSingleEntry()
	{
		_map.put("A", "1");

		final Iterator<Map.Entry<String, String>> iter =
			MultiMapEntryIterator.decorate(_map);
		assertEquals(new DefaultMapEntry<>("A", "1"), iter.next());
		try {
			iter.next();
			assertTrue("Iterating past single entry iter", false);
		} catch (NoSuchElementException ex) {
			/* ignore */
		}
	}


	@Test
	public void testNextTwoEntry()
	{
		_map.put("A", "1");
		_map.put("B", "2");

		final Iterator<Map.Entry<String, String>> iter =
			MultiMapEntryIterator.decorate(_map);
		assertEquals(new DefaultMapEntry<>("A", "1"), iter.next());
		assertEquals(new DefaultMapEntry<>("B", "2"), iter.next());
		try {
			iter.next();
			assertTrue("Iterating past dual entry iter", false);
		} catch (NoSuchElementException ex) {
			/* ignore */
		}
	}


	@Test
	public void testNextDoubleEntry()
	{
		_map.put("A", "1");
		_map.put("A", "2");

		final Iterator<Map.Entry<String, String>> iter =
			MultiMapEntryIterator.decorate(_map);
		assertEquals(new DefaultMapEntry<>("A", "1"), iter.next());
		assertEquals(new DefaultMapEntry<>("A", "2"), iter.next());
		try {
			iter.next();
			assertTrue("Iterating past double entry iter", false);
		} catch (NoSuchElementException ex) {
			/* ignore */
		}
	}


	@Test
	public void testNextDoubleDoubleEntry()
	{
		_map.put("A", "1");
		_map.put("A", "2");
		_map.put("B", "3");
		_map.put("B", "4");

		final Iterator<Map.Entry<String, String>> iter =
			MultiMapEntryIterator.decorate(_map);
		assertEquals(new DefaultMapEntry<>("A", "1"), iter.next());
		assertEquals(new DefaultMapEntry<>("A", "2"), iter.next());
		assertEquals(new DefaultMapEntry<>("B", "3"), iter.next());
		assertEquals(new DefaultMapEntry<>("B", "4"), iter.next());
		try {
			iter.next();
			assertTrue("Iterating past double entry iter", false);
		} catch (NoSuchElementException ex) {
			/* ignore */
		}
	}


	@Test(expected = UnsupportedOperationException.class)
	public void testRemove()
	{
		_map.put("A", "1");

		final Iterator<Map.Entry<String, String>> iter =
			MultiMapEntryIterator.decorate(_map);
		assertEquals(new DefaultMapEntry<>("A", "1"), iter.next());
		iter.remove();
	}
}

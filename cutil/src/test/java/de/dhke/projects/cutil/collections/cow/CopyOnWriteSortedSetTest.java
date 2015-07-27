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
package de.dhke.projects.cutil.collections.cow;

import de.dhke.projects.cutil.collections.factories.ICollectionFactory;
import de.dhke.projects.cutil.collections.factories.TreeSetFactory;
import java.util.Arrays;
import java.util.SortedSet;
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
public class CopyOnWriteSortedSetTest {

	private final ICollectionFactory<String, SortedSet<String>> _factory = new TreeSetFactory<>();
	private SortedSet<String> _baseMap;
	private CopyOnWriteSortedSet<String> _cowSet;


	public CopyOnWriteSortedSetTest()
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
		_baseMap = _factory.getInstance();
		_baseMap.addAll(Arrays.asList("2", "3", "4"));
		_cowSet = new CopyOnWriteSortedSet<>(_baseMap, _factory);
	}


	@After
	public void tearDown()
	{
		_cowSet = null;
		_baseMap = null;
	}


	/**
	 * Test of comparator method, of class GenericCopyOnWriteSortedSet.
	 */
	@Test
	public void testComparator()
	{
		assertNull(_cowSet.comparator());
	}


	/**
	 * Test of subSet method, of class GenericCopyOnWriteSortedSet.
	 */
	@Test
	public void testSubSet()
	{
		assertEquals(2, _cowSet.subSet("3", "4.1").size());
		assertTrue(_cowSet.subSet("3", "4.1").contains("3"));
		assertTrue(_cowSet.subSet("3", "4.1").contains("4"));
		assertFalse(_cowSet.subSet("3", "4.1").contains("2"));
		assertFalse(_cowSet.subSet("3", "4.1").contains("5"));

		assertTrue(_cowSet.subSet("3", "4.1").add("3.1"));
		assertTrue(_cowSet.contains("3.1"));
		assertTrue(_cowSet.subSet("3", "4.1").contains("3.1"));
		assertFalse(_baseMap.contains("3.1"));
	}


	/**
	 * Test of headSet method, of class GenericCopyOnWriteSortedSet.
	 */
	@Test
	public void testHeadSet()
	{
		assertEquals(2, _cowSet.headSet("3.1").size());
		assertTrue(_cowSet.headSet("3.1").contains("2"));
		assertTrue(_cowSet.headSet("3.1").contains("3"));
		assertFalse(_cowSet.headSet("3.1").contains("4"));

		assertTrue(_cowSet.headSet("3.1").add("1"));
		assertTrue(_cowSet.headSet("3.1").contains("1"));
		assertTrue(_cowSet.contains("1"));
		assertFalse(_baseMap.contains("1"));
	}


	/**
	 * Test of tailSet method, of class GenericCopyOnWriteSortedSet.
	 */
	@Test
	public void testTailSet()
	{
		assertEquals(2, _cowSet.tailSet("3").size());
		assertFalse(_cowSet.tailSet("3").contains("2"));
		assertTrue(_cowSet.tailSet("3").contains("3"));
		assertTrue(_cowSet.tailSet("3").contains("4"));

		assertTrue(_cowSet.tailSet("3").add("5"));
		assertTrue(_cowSet.tailSet("3").contains("5"));
		assertTrue(_cowSet.contains("5"));
		assertFalse(_baseMap.contains("5"));
	}


	/**
	 * Test of first method, of class GenericCopyOnWriteSortedSet.
	 */
	@Test
	public void testFirst()
	{
		assertEquals("2", _cowSet.first());
		assertTrue(_cowSet.remove(_cowSet.first()));
		assertEquals("3", _cowSet.first());
		assertEquals("2", _baseMap.first());
	}


	/**
	 * Test of last method, of class GenericCopyOnWriteSortedSet.
	 */
	@Test
	public void testLast()
	{
		assertEquals("4", _cowSet.last());
		assertTrue(_cowSet.remove(_cowSet.last()));
		assertEquals("3", _cowSet.last());
		assertEquals("4", _baseMap.last());
	}
}

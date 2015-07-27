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
package de.dhke.projects.cutil.collections.aspect;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
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
public class AspectSetTest {
	private Set<Integer> _baseSet;
	private AspectCollection<Integer, Set<Integer>> _aspectSet;
	private AspectCollectionHistoryListener<Integer, Collection<Integer>> _listener;


	public AspectSetTest()
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
		_baseSet = new HashSet<>();
		_aspectSet = AspectSet.decorate(_baseSet, this);

		_baseSet.add(1);
		_baseSet.add(2);
		_baseSet.add(3);
		_baseSet.add(4);

		_listener = new AspectCollectionHistoryListener<>();
		_aspectSet.getListeners().add(_listener);
	}


	@After
	public void tearDown()
	{
		_listener = null;
		_aspectSet = null;
		_baseSet = null;
	}


	/**
	 * Test of decorate method, of class AspectList.
	 */
	@Test
	public void testDecorate()
	{
		assertSame(_baseSet, _aspectSet.getDecoratee());
	}


	/**
	 * Test of size method, of class AspectList.
	 */
	@Test
	public void testSize()
	{
		assertEquals(4, _aspectSet.size());
		assertTrue(_aspectSet.remove(2));
		assertEquals(3, _aspectSet.size());
		assertEquals(3, _baseSet.size());
	}


	/**
	 * Test of isEmpty method, of class AspectList.
	 */
	@Test
	public void testIsEmpty()
	{
		assertFalse(_aspectSet.isEmpty());
		assertFalse(_aspectSet.isEmpty());
		_aspectSet.clear();
		assertTrue(_aspectSet.isEmpty());
		assertTrue(_baseSet.isEmpty());
	}


	/**
	 * Test of contains method, of class AspectList.
	 */
	@Test
	public void testContains()
	{
		assertTrue(_aspectSet.contains(1));
		assertTrue(_aspectSet.contains(2));
		assertTrue(_aspectSet.contains(3));
		assertTrue(_aspectSet.contains(4));
		assertFalse(_aspectSet.contains(5));
		assertTrue(_baseSet.contains(1));
		assertTrue(_baseSet.contains(2));
		assertTrue(_baseSet.contains(3));
		assertTrue(_baseSet.contains(4));
		assertFalse(_baseSet.contains(5));
	}


	/**
	 * Test of iterator method, of class AspectList.
	 */
	@Test
	public void testIterator()
	{
		Iterator<Integer> iter = _aspectSet.iterator();
		assertEquals(Integer.valueOf(1), iter.next());
		assertEquals(Integer.valueOf(2), iter.next());
		assertEquals(Integer.valueOf(3), iter.next());
		assertEquals(Integer.valueOf(4), iter.next());

		iter = _aspectSet.iterator();
	}


	/**
	 * Test of iterator method, of class AspectList.
	 */
	@Test
	public void testIteratorRemove_veto()
	{
		Iterator<Integer> iter = _aspectSet.iterator();
		assertEquals(Integer.valueOf(1), iter.next());

		try {
			_listener.vetoRemove = true;
			iter.remove();
			fail("Veto exception not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_aspectSet.contains(1));
	}


	/**
	 * Test of toArray method, of class AspectList.
	 */
	@Test
	public void testToArray_0args()
	{
		assertArrayEquals(new Integer[]{1, 2, 3, 4}, _aspectSet.toArray());
	}


	/**
	 * Test of toArray method, of class AspectList.
	 */
	@Test
	public void testToArray_GenericType()
	{
		assertArrayEquals(new Integer[]{1, 2, 3, 4}, _aspectSet.toArray(new Integer[_aspectSet.size()]));
	}


	/**
	 * Test of add method, of class AspectList.
	 */
	@Test
	public void testAdd()
	{
		assertFalse(_aspectSet.add(2));
		assertTrue(_listener.beforeAddEvents.isEmpty());
		assertTrue(_listener.afterAddEvents.isEmpty());
		assertTrue(_aspectSet.add(5));
		assertEquals(1, _listener.beforeAddEvents.size());
		assertEquals(Integer.valueOf(5), _listener.beforeAddEvents.get(0).getItem());
		assertEquals(1, _listener.afterAddEvents.size());
		assertEquals(Integer.valueOf(5), _listener.afterAddEvents.get(0).getItem());
	}


	@Test
	public void testAdd_veto()
	{
		try {
			_listener.vetoAdd = true;
			_aspectSet.add(5);
			fail("Veto exception not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertFalse(_aspectSet.contains(5));
	}


	/**
	 * Test of remove method, of class AspectList.
	 */
	@Test
	public void testRemove()
	{
		assertFalse(_aspectSet.remove(7));
		assertTrue(_aspectSet.remove(3));
		assertEquals(1, _listener.beforeRemoveEvents.size());
		assertEquals(Integer.valueOf(3), _listener.beforeRemoveEvents.get(0).getItem());
		assertEquals(1, _listener.afterRemoveEvents.size());
		assertEquals(Integer.valueOf(3), _listener.afterRemoveEvents.get(0).getItem());
	}


	/**
	 * Test of remove method, of class AspectList.
	 */
	@Test
	public void testRemove_veto()
	{
		try {
			_listener.vetoRemove = true;
			_aspectSet.remove(3);
			fail("Veto exception not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_aspectSet.contains(3));
	}


	/**
	 * Test of containsAll method, of class AspectList.
	 */
	@Test
	public void testContainsAll()
	{
		assertTrue(_aspectSet.containsAll(Arrays.asList(1, 2, 3, 4)));
		assertFalse(_aspectSet.containsAll(Arrays.asList(1, 2, 3, 4, 5)));
	}


	/**
	 * Test of addAll method, of class AspectCollection.
	 */
	@Test
	public void testAddAll()
	{
		assertTrue(_aspectSet.addAll(Arrays.asList(5, 6)));
		assertTrue(_aspectSet.addAll(Arrays.asList(6, 7)));
		assertEquals(3, _listener.beforeAddEvents.size());
		assertEquals(Integer.valueOf(5), _listener.beforeAddEvents.get(0).getItem());
		assertEquals(Integer.valueOf(6), _listener.beforeAddEvents.get(1).getItem());
		assertEquals(Integer.valueOf(7), _listener.beforeAddEvents.get(2).getItem());
		assertEquals(3, _listener.afterAddEvents.size());
		assertEquals(Integer.valueOf(5), _listener.afterAddEvents.get(0).getItem());
		assertEquals(Integer.valueOf(6), _listener.afterAddEvents.get(1).getItem());
		assertEquals(Integer.valueOf(7), _listener.afterAddEvents.get(2).getItem());
		assertTrue(_aspectSet.contains(5));
		assertTrue(_aspectSet.contains(6));
		assertTrue(_aspectSet.contains(7));
		assertFalse(_aspectSet.contains(8));
	}


	@Test
	public void testAddAll_veto()
	{
		_listener.vetoAdd = true;
		try {
			_aspectSet.addAll(Arrays.asList(5, 6));
			fail("Veto exception not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_listener.beforeAddEvents.isEmpty());
		assertTrue(_listener.afterAddEvents.isEmpty());
		assertFalse(_aspectSet.contains(5));
		assertFalse(_aspectSet.contains(6));
		assertFalse(_aspectSet.contains(7));
	}


	/**
	 * Test of removeAll method, of class AspectList.
	 */
	@Test
	public void testRemoveAll()
	{
		assertFalse(_aspectSet.removeAll(Arrays.asList(5, 6)));
		_aspectSet.removeAll(Arrays.asList(2, 4));
		assertFalse(_aspectSet.contains(2));
		assertFalse(_aspectSet.contains(4));
		assertEquals(4, _listener.beforeRemoveEvents.size());
		assertEquals(Integer.valueOf(5), _listener.beforeRemoveEvents.get(0).getItem());
		assertEquals(Integer.valueOf(6), _listener.beforeRemoveEvents.get(1).getItem());
		assertEquals(Integer.valueOf(2), _listener.beforeRemoveEvents.get(2).getItem());
		assertEquals(Integer.valueOf(4), _listener.beforeRemoveEvents.get(3).getItem());
		assertEquals(2, _listener.afterRemoveEvents.size());
		assertEquals(Integer.valueOf(2), _listener.afterRemoveEvents.get(0).getItem());
		assertEquals(Integer.valueOf(4), _listener.afterRemoveEvents.get(1).getItem());
	}


	@Test
	public void testRemoveAll_veto()
	{
		_listener.vetoRemove = true;
		try {
			_aspectSet.removeAll(Arrays.asList(2, 4));
			fail("Veto exception not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_aspectSet.contains(2));
		assertTrue(_aspectSet.contains(4));
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());
	}


	/**
	 * Test of retainAll method, of class AspectList.
	 */
	@Test
	public void testRetainAll()
	{
		assertFalse(_aspectSet.retainAll(Arrays.asList(1, 2, 3, 4)));
		assertTrue(_aspectSet.retainAll(Arrays.asList(2, 3)));
		assertFalse(_aspectSet.contains(1));
		assertFalse(_aspectSet.contains(4));
	}

	/**
	 * Test of retainAll method, of class AspectList.
	 */
	@Test
	public void testRetainAll_veto()
	{
		_listener.vetoRemove = false;
		_aspectSet.retainAll(Arrays.asList(1, 2, 3, 4));
		_listener.vetoRemove = true;
		try {
			assertTrue(_aspectSet.retainAll(Arrays.asList(2, 3)));
			fail("Veto exception not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_aspectSet.contains(1));
		assertTrue(_aspectSet.contains(4));
	}


	/**
	 * Test of clear method, of class AspectList.
	 */
	@Test
	public void testClear()
	{
		_aspectSet.clear();
		assertTrue(_aspectSet.isEmpty());
		assertEquals(1, _listener.beforeClearEvents.size());
		assertEquals(1, _listener.afterClearEvents.size());

		_aspectSet.clear();
		assertEquals(1, _listener.beforeClearEvents.size());
		assertEquals(1, _listener.afterClearEvents.size());
	}

	/**
	 * Test of clear method, of class AspectList.
	 */
	@Test
	public void testClear_veto()
	{
		_listener.vetoClear = true;
		try {
			_aspectSet.clear();
			fail("Veto exception not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertFalse(_aspectSet.isEmpty());

		_baseSet.clear();
		_aspectSet.clear();
	}

	/**
	 * Test of getDecoratee method, of class AspectList.
	 */
	@Test
	public void testGetDecoratee()
	{
		assertSame(_baseSet, _aspectSet.getDecoratee());
	}

}

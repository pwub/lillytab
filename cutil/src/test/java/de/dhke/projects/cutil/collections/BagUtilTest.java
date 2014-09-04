/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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

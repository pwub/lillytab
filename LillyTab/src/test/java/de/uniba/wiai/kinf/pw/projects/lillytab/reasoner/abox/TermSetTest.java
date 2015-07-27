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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox;

import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.TermSet.TermTypes;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLLiteralReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataUnion;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleStringDLTermFactory;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author peterw
 */
public class TermSetTest {
	private IDLTermFactory <String, String, String, String> _termFactory;
	private TermSet<String, String, String, String> _termSet;
	
	public TermSetTest()
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
		_termFactory = new SimpleStringDLTermFactory();
		_termSet = new TermSet<>(TermTypes.ANY);
	}
	

	@After
	public void tearDown()
	{
		_termSet = null;
		_termFactory = null;
	}


	@Test
	public void testUnionSubSet()
	{
		final IDLLiteralReference<String, String, String, String> lit1 = _termFactory.getDLLiteralReference("a");
		final IDLLiteralReference<String, String, String, String> lit2 = _termFactory.getDLLiteralReference("b");
		final IDLDataUnion<String, String, String, String> union = _termFactory.getDLDataUnion(lit1, lit2);
			
		_termSet.add(union);
		assertEquals(1, _termSet.subSet(DLTermOrder.DL_DATA_UNION).size());
		assertTrue(_termSet.subSet(DLTermOrder.DL_DATA_UNION).contains(union));
	}
}
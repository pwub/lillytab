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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner;

import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.tbox.AssertedRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.tbox.TBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentRBoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.ITBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleProperty;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleType;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl.DLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleStringDLTermFactory;
import org.junit.*;
import static org.junit.Assert.*;


/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class RBoxTest
{
	private IDLTermFactory<String, String, String> _termFactory;
	private ITBox<String, String, String> _tbox;
	private AssertedRBox<String, String, String> _assertedRBox;
	
	public RBoxTest()
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
		_termFactory = new SimpleStringDLTermFactory();
		_tbox = new TBox<String, String, String>(_termFactory);
		_assertedRBox = new AssertedRBox<String, String, String>(_tbox);
	}
	
	@After
	public void tearDown()
	{
		_termFactory = null;
		_tbox = null;
		_assertedRBox = null;
	}

	@Test
	public void testDataPropertySubRole()
		throws EInconsistentRBoxException
	{
		_assertedRBox.addRole("r1", RoleType.DATA_PROPERTY);
		_assertedRBox.addRole("r2", RoleType.DATA_PROPERTY);
		_assertedRBox.addSubRole("r1", "r2");
		
		assertTrue(_assertedRBox.getRBox().isSubRole("r1", "r2"));
		assertFalse(_assertedRBox.getRBox().isSubRole("r2", "r1"));
		assertTrue(_assertedRBox.getRBox().isSuperRole("r2", "r1"));
		assertFalse(_assertedRBox.getRBox().isSuperRole("r1", "r2"));		
	}
	
	@Test
	public void testObjectPropertySubRole()
		throws EInconsistentRBoxException
	{
		_assertedRBox.addRole("r1", RoleType.OBJECT_PROPERTY);
		_assertedRBox.addRole("r2", RoleType.OBJECT_PROPERTY);
		_assertedRBox.addSubRole("r1", "r2");
		
		assertTrue(_assertedRBox.getRBox().isSubRole("r1", "r1"));
		assertTrue(_assertedRBox.getRBox().isSubRole("r2", "r2"));

		assertTrue(_assertedRBox.getRBox().isSubRole("r1", "r2"));
		assertFalse(_assertedRBox.getRBox().isSubRole("r2", "r1"));
		assertTrue(_assertedRBox.getRBox().isSuperRole("r2", "r1"));
		assertFalse(_assertedRBox.getRBox().isSuperRole("r1", "r2"));		
	}
	
	@Test(expected=EInconsistentRBoxException.class)
	public void testMixedSubRoleClash()
		throws EInconsistentRBoxException
	{
		_assertedRBox.addRole("r1", RoleType.OBJECT_PROPERTY);
		_assertedRBox.addRole("r2", RoleType.DATA_PROPERTY);
		_assertedRBox.addSubRole("r1", "r2");
	}
	
	@Test
	public void testTopRoleAllSub()
		throws EInconsistentRBoxException
	{
		_assertedRBox.addRole("op1", RoleType.OBJECT_PROPERTY);
		_assertedRBox.addRole("op2", RoleType.OBJECT_PROPERTY);
		_assertedRBox.addRole("op3", RoleType.OBJECT_PROPERTY);
		_assertedRBox.addRole("dp1", RoleType.DATA_PROPERTY);
		_assertedRBox.addRole("dp2", RoleType.DATA_PROPERTY);
		_assertedRBox.addRole("dp3", RoleType.DATA_PROPERTY);
		_assertedRBox.setRoleProperty("op1", RoleProperty.TOP);
		_assertedRBox.setRoleProperty("dp1", RoleProperty.TOP);
		assertTrue(_assertedRBox.getRBox().isSubRole("op1", "op1"));
		assertTrue(_assertedRBox.getRBox().isSubRole("op1", "op2"));
		assertTrue(_assertedRBox.getRBox().isSubRole("op1", "op3"));
		assertFalse(_assertedRBox.getRBox().isSubRole("op1", "dp1"));
		assertFalse(_assertedRBox.getRBox().isSubRole("op1", "dp2"));
		assertFalse(_assertedRBox.getRBox().isSubRole("op1", "dp3"));
		assertTrue(_assertedRBox.getRBox().isSubRole("dp1", "dp1"));
		assertTrue(_assertedRBox.getRBox().isSubRole("dp1", "dp2"));
		assertTrue(_assertedRBox.getRBox().isSubRole("dp1", "dp3"));		
		assertFalse(_assertedRBox.getRBox().isSubRole("dp1", "op1"));		
		assertFalse(_assertedRBox.getRBox().isSubRole("dp1", "op2"));		
		assertFalse(_assertedRBox.getRBox().isSubRole("dp1", "op3"));		
	}
	
	@Test
	public void testDoubleInvertEquality()
		throws EInconsistentRBoxException
	{
		_assertedRBox.addRole("a", RoleType.OBJECT_PROPERTY);
		_assertedRBox.addRole("b", RoleType.OBJECT_PROPERTY);
		_assertedRBox.addRole("c", RoleType.OBJECT_PROPERTY);
		_assertedRBox.addInverseRole("a", "b");
		_assertedRBox.addInverseRole("b", "c");
		assertTrue(_assertedRBox.getRBox().isEquivalentRole("a", "c"));
		assertFalse(_assertedRBox.getRBox().isEquivalentRole("a", "b"));
		assertFalse(_assertedRBox.getRBox().isInverseRole("a", "c"));
	}
	

	public void testInvertEqualityChain()
		throws EInconsistentRBoxException
	{
		_assertedRBox.addRole("a", RoleType.OBJECT_PROPERTY);
		_assertedRBox.addRole("ai", RoleType.OBJECT_PROPERTY);
		_assertedRBox.addRole("b", RoleType.OBJECT_PROPERTY);
		_assertedRBox.addRole("bi", RoleType.OBJECT_PROPERTY);
		_assertedRBox.addRole("c", RoleType.OBJECT_PROPERTY);
		_assertedRBox.addRole("ci", RoleType.OBJECT_PROPERTY);
		_assertedRBox.addRole("d", RoleType.OBJECT_PROPERTY);
		_assertedRBox.addRole("di", RoleType.OBJECT_PROPERTY);

		_assertedRBox.addInverseRole("a", "ai");
		_assertedRBox.addInverseRole("b", "bi");
		_assertedRBox.addInverseRole("c", "ci");
		_assertedRBox.addInverseRole("d", "di");
		_assertedRBox.addEquivalentRole("ai", "bi");
		_assertedRBox.addEquivalentRole("b", "c");
		_assertedRBox.addEquivalentRole("ci", "di");
		assertTrue(_assertedRBox.getRBox().isEquivalentRole("a", "b"));
		assertTrue(_assertedRBox.getRBox().isEquivalentRole("a", "c"));
		assertTrue(_assertedRBox.getRBox().isEquivalentRole("a", "d"));
		assertTrue(_assertedRBox.getRBox().isInverseRole("ai", "bi"));
		assertTrue(_assertedRBox.getRBox().isInverseRole("ai", "ci"));
		assertTrue(_assertedRBox.getRBox().isInverseRole("ai", "di"));
		assertFalse(_assertedRBox.getRBox().isEquivalentRole("a", "bi"));
		assertFalse(_assertedRBox.getRBox().isEquivalentRole("a", "ci"));
		assertFalse(_assertedRBox.getRBox().isEquivalentRole("a", "di"));
	}
	
	@Test(expected=EInconsistentRBoxException.class)
	public void testInvertEqualityChainClash()
		throws EInconsistentRBoxException
	{
		_assertedRBox.addRole("a", RoleType.OBJECT_PROPERTY);
		_assertedRBox.addRole("ai", RoleType.OBJECT_PROPERTY);
		_assertedRBox.addRole("b", RoleType.OBJECT_PROPERTY);
		_assertedRBox.addRole("bi", RoleType.OBJECT_PROPERTY);
		_assertedRBox.addRole("c", RoleType.OBJECT_PROPERTY);
		_assertedRBox.addRole("ci", RoleType.OBJECT_PROPERTY);
		_assertedRBox.addRole("d", RoleType.OBJECT_PROPERTY);
		_assertedRBox.addRole("di", RoleType.OBJECT_PROPERTY);

		_assertedRBox.addInverseRole("a", "ai");
		_assertedRBox.addInverseRole("b", "bi");
		_assertedRBox.addInverseRole("c", "ci");
		_assertedRBox.addInverseRole("d", "di");
		_assertedRBox.addEquivalentRole("ai", "bi");
		_assertedRBox.addEquivalentRole("b", "c");
		_assertedRBox.addEquivalentRole("c", "d");
		_assertedRBox.addEquivalentRole("di", "a");		
	}
}

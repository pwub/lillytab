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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public class ETermClashException
	extends EInconsistentABoxNodeException {

	private static final long serialVersionUID = 3926533409815064301L;
	private final Object[] _clashingTerms;


	/**
	 * Creates a new instance of
	 * <code>ETermClashException</code> without detail message.
	 * 
	 * @param node the affected node
	 * @param clashingTerms  The clashing set of terms
	 */
	public ETermClashException(final IABoxNode<?, ?, ?, ?> node, Object... clashingTerms)
	{
		super(node);
		_clashingTerms = clashingTerms;
	}


	/**
	 * Constructs an instance of
	 * <code>ETermClashException</code> with the specified detail message.
	 *
	 * @param node the affected node
	 * @param msg the detail message.
	 * @param clashingTerms the clashing set of terms
	 */
	public ETermClashException(final IABoxNode<?, ?, ?, ?> node, final String msg, Object... clashingTerms)
	{
		super(node, msg);
		_clashingTerms = clashingTerms;
	}

	/**
	 * Creates a new instance of
	 * <code>ETermClashException</code> without detail message.
	 * 
	 * @param node the affected node
	 * @param clashingTerms  The clashing set of terms
	 */
	public ETermClashException(final IABoxNode<?, ?, ?, ?> node, final Collection<?> clashingTerms)
	{
		super(node);
		_clashingTerms = clashingTerms.toArray();
	}


	/**
	 * Creates a new instance of
	 * <code>ETermClashException</code> without detail message.
	 * 
	 * @param node the affected node
	 * @param msg the detail message
	 * @param clashingTerms  The clashing set of terms
	 */
	public ETermClashException(final IABoxNode<?, ?, ?, ?> node, String msg, final Collection<?> clashingTerms)
	{
		super(node, msg);
		_clashingTerms = clashingTerms.toArray();
	}


	public Object[] getClashingTerms()
	{
		return _clashingTerms;
	}


	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(getClass());
		sb.append(getABoxNode().getNodeID());
		sb.append(": ");
		sb.append(Arrays.deepToString(_clashingTerms));
		return sb.toString();
	}
}

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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer;

import de.uniba.wiai.kinf.pw.projects.lillytab.blocking.IBlockingStrategy;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.INodeConsistencyChecker;

/**
 * 
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public abstract class AbstractGeneratingCompleter<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends AbstractCompleter<Name, Klass, Role>
{
	private final IBlockingStrategy<Name, Klass, Role> _blockingStrategy;

	/**
	 * Retrieve the blocking strategy associated with this completer.
	 * @return The {@link IBlockingStrategy} associated with this completer.
	 */
	public IBlockingStrategy<Name, Klass, Role> getBlockingStrategy()
	{
		return _blockingStrategy;
	}

	public AbstractGeneratingCompleter(final INodeConsistencyChecker<Name, Klass, Role> cChecker,
		final IBlockingStrategy<Name, Klass, Role> blockingStrategy,
		final boolean trace)
	{
		super(cChecker, trace);
		_blockingStrategy = blockingStrategy;
	}

	public AbstractGeneratingCompleter(final INodeConsistencyChecker<Name, Klass, Role> cChecker, final IBlockingStrategy<Name, Klass, Role> blockingStrategy)
	{
		this(cChecker, blockingStrategy, false);
	}
}

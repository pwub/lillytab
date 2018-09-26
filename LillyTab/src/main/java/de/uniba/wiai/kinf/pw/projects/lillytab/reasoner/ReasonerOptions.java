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


/**
 * 
 * Global reasoner options.
 * 
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public final class ReasonerOptions
{
	/**
	 * 
	 * Log a details trace of reasoner activity.
	 * <p />
	 * This significantly slows down the reasoning process.
	 * 
	 */
	private boolean _tracing = false;
	/**
	 * Periodically log reasoning progress.
	 */
	private boolean _progressLogging = false;
	/**
	 * Track node merges.
	 *
	 * @see IReasonerResult#getMergeMap()
	 *
	 */
	private boolean _mergeTracking = true;
	/**
	 * Enable semantic ((or A B) => (A B), (A (not B)), ((not A) B)) branching. Use traditional ((or A B) => A, B)
	 * branching, otherwise.
	 *
	 */
	private boolean _semanticBranching = false;

	public ReasonerOptions()
	{
	}

	/**
	 * @return the _tracing
	 */
	public boolean isTracing()
	{
		return _tracing;
	}

	/**
	 * @return the _progressLogging
	 */
	public boolean isProgressLogging()
	{
		return _progressLogging;
	}

	/**
	 * @return the _mergeTracking
	 */
	public boolean isMergeTracking()
	{
		return _mergeTracking;
	}

	/**
	 * @return the _semanticBranching
	 */
	public boolean isSemanticBranching()
	{
		return _semanticBranching;
	}

	/**
	 * @param tracing the _tracing to set
	 */
	public void setTracing(boolean tracing)
	{
		this._tracing = tracing;
	}

	/**
	 * @param progressLogging the _progressLogging to set
	 */
	public void setProgressLogging(boolean progressLogging)
	{
		this._progressLogging = progressLogging;
	}

	/**
	 * @param mergeTracking the _mergeTracking to set
	 */
	public void setMergeTracking(boolean mergeTracking)
	{
		this._mergeTracking = mergeTracking;
	}

	/**
	 * @param semanticBranching the _semanticBranching to set
	 */
	public void setSemanticBranching(boolean semanticBranching)
	{
		this._semanticBranching = semanticBranching;
	}
}

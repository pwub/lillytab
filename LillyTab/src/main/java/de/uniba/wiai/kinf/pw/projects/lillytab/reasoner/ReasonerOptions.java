/**
 * (c) 2009-2012 Otto-Friedrich-University Bamberg
 *
 * $Id$
 *
 * Use, modification and restribution of this file are covered by the terms of the Artistic License 2.0.
 *
 * You should have received a copy of the license terms in a file named "LICENSE" together with this software package.
 *
 * Disclaimer of Warranty: THE PACKAGE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS "AS IS' AND WITHOUT ANY
 * EXPRESS OR IMPLIED WARRANTIES. THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT ARE DISCLAIMED TO THE EXTENT PERMITTED BY YOUR LOCAL LAW. UNLESS REQUIRED BY LAW, NO COPYRIGHT
 * HOLDER OR CONTRIBUTOR WILL BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING IN ANY
 * WAY OUT OF THE USE OF THE PACKAGE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner;

/**
 * <p>
 * Global reasoner options.
 * </p>
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public final class ReasonerOptions {

	/**
	 * <p>
	 * Log a details trace of reasoner activity.
	 * </p><p>
	 * This significantly slows down the reasoning process.
	 * </p>
	 */
	public boolean TRACE = false;
	/**
	 * Periodically log reasoning progress.
	 */
	public boolean LOG_PROGRESS = false;
	/**
	 * Track node merges.
	 *
	 * @see IReasonerResult#getMergeMap()
	 *
	 */
	public boolean MERGE_TRACKING = true;
	/**
	 * Enable semantic ((or A B) => (A B), (A (not B)), ((not A) B)) branching. Use traditional ((or A B) => A, B)
	 * branching, otherwise.
	 *
	 */
	public boolean SEMANTIC_BRANCHING = false;


	public ReasonerOptions()
	{
	}
}

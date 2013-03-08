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
package de.uniba.wiai.kinf.pw.projects.lillytab.abox;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ENodeMergeException
	extends EInconsistentABoxException {

	static final long serialVersionUID = -4820027800791296834L;
	final IABoxNode<?, ?, ?> _targetNode;
	final IABoxNode<?, ?, ?> _sourceNode;


	public IABoxNode<?, ?, ?> getTargetNode()
	{
		return _targetNode;
	}


	public IABoxNode<?, ?, ?> getSourceNode()
	{
		return _sourceNode;
	}


	/**
	 * Creates a new instance of
	 * <code>ENodeMergeException</code> without detail message.
	 */
	public ENodeMergeException(final IABoxNode<?, ?, ?> targetNode, final IABoxNode<?, ?, ?> sourceNode)
	{
		super(targetNode.getABox());
		_targetNode = targetNode;
		_sourceNode = sourceNode;
	}


	/**
	 * Constructs an instance of
	 * <code>ENodeMergeException</code> with the specified detail message.
	 *
	 * @param msg the detail message.
	 */
	public ENodeMergeException(final IABoxNode<?, ?, ?> targetNode, final IABoxNode<?, ?, ?> sourceNode,
							   final String msg)
	{
		super(targetNode.getABox(), msg);
		_targetNode = targetNode;
		_sourceNode = sourceNode;
	}
}

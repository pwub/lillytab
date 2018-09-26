/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.blocking;

import de.dhke.projects.cutil.collections.set.Flat3Set;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.blocking.BlockInfo;
import java.util.Collection;
import java.util.Set;


/**
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public class DoubleBlockingBlockInfo
	extends BlockInfo {
	private final Set<NodeID> _blockPair = new Flat3Set<>();

	public DoubleBlockingBlockInfo(final NodeID x0, final NodeID x1, final NodeID y0, final NodeID y1)
	{
		super(x1);
		_blockPair.add(x0);
		_blockPair.add(y0);
		_blockPair.add(y1);
	}

	@Override
	public Collection<NodeID> getInvolvedNodes()
	{
		return _blockPair;
	}

}

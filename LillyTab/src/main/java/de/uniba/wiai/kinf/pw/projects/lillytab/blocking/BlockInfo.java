/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.blocking;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import java.util.Collection;
import java.util.Collections;
import org.apache.commons.collections15.CollectionUtils;


/**
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public class BlockInfo {
	private final NodeID _blocker;

	public BlockInfo(final NodeID blocker)
	{
		this._blocker = blocker;
	}

	public Collection<NodeID> getInvolvedNodes()
	{
		return Collections.singleton(_blocker);
	}

	public NodeID getBlocker()
	{
		return _blocker;
	}

	@Override
	public int hashCode()
	{
		return _blocker.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		} else if (obj instanceof BlockInfo) {
			final BlockInfo b = (BlockInfo) obj;
			return _blocker.equals(b._blocker) && CollectionUtils.isEqualCollection(getInvolvedNodes(), b.
				getInvolvedNodes());
		} else {
			return false;
		}
	}

}

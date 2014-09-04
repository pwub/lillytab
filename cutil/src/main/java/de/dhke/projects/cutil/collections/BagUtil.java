/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dhke.projects.cutil.collections;

import org.apache.commons.collections15.Bag;


/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class BagUtil
{
	public static <T> Bag<T> unionBags(final Bag<T> b0, final Bag<T> b1, final Bag<T> targetBag)
	{
		targetBag.clear();
		for (T item : b0) {
			final int count0 = b0.getCount(item);
			final int count1 = b1.getCount(item);
			targetBag.add(item, Math.max(count0, count1));
		}
		for (T item : b1) {
			if (!b0.contains(item)) {
				final int count1 = b1.getCount(item);
				targetBag.add(item, count1);
			}
		}
		return targetBag;
	}

	public static <T> Bag<T>
		intersectBags(final Bag<T> b0, final Bag<T> b1, final Bag<T> targetBag)
	{
		targetBag.clear();
		for (T item : b0) {
			final int count0 = b0.getCount(item);
			final int count1 = b1.getCount(item);
			if ((count0 > 0) && (count1 > 0)) {
				targetBag.add(item, Math.min(count0, count1));
			}
		}
		return targetBag;
	}
}

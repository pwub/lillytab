/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.uniba.wiai.kinf.pw.projects.lillytab.util;

import de.uniba.wiai.kinf.pw.projects.lillytab.IReasoner;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.EReasonerException;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ReasonerUtil {
	private ReasonerUtil()
	{		
	}
	
	/// <editor-fold defaultstate="collapsed" desc="add*Limit()">
	/**
	 * Try to add {@literal newConcept} to {@literal targetSet} making sure, that no concept in {@literal targetSet} is
	 * a superclass of some other concept in the same set.
	 *
	 *
	 * @param targetSet
	 * @param newConcept
	 *                      <p/>
	 * @return
	 */
	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>, T extends IDLClassExpression<I, L, K, R>> Set<T> addLowerLimit(
		final IReasoner<I, L, K, R> reasoner,
		final IABox<I, L, K, R> abox,
		final Set<T> targetSet,
		final T newConcept)
		throws EReasonerException, EInconsistencyException
	{
		final Iterator<T> iter = targetSet.iterator();
		/**
		 * Iterate over the elements of the target set.
		 *
		 * If we find an item that is a subclass of the new concept, set a flags not to add newConcept.
		 *
		 * If we find an item that is a superclass of the new concept, remove it from the target set.
		 *
		 *
		 */
		boolean doAdd = true;
		while (iter.hasNext()) {
			final T target = iter.next();
			if (reasoner.isSubClassOf(abox, target, newConcept))
				doAdd = false;
			else if (reasoner.isSubClassOf(abox, newConcept, target))
				iter.remove();
		}
		if (doAdd)
			targetSet.add(newConcept);

		return targetSet;
	}

	/**
	 * Try to add {@literal newConcept} to {@literal targetSet} making sure, that no concept in {@literal targetSet} is
	 * a superclass of some other concept in the same set.
	 *
	 *
	 * @param targetSet
	 * @param newConcept
	 *                      <p/>
	 * @return
	 */
	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>, T extends IDLClassExpression<I, L, K, R>> Set<T>  addUpperLimit(
		final IReasoner<I, L, K, R> reasoner,
		final IABox<I, L, K, R> abox,
		final Set<T> targetSet,
		final T newConcept)
		throws EReasonerException, EInconsistencyException
	{
		final Iterator<T> iter = targetSet.iterator();
		/**
		 * Iterate over the elements of the target set.
		 *
		 * If we find an item that is a superclass of the new concept, set a flag not to add newConcept.
		 *
		 * If we find an item that is a subclass of the new concept, remove it from the target set.
		 *
		 *
		 */
		boolean doAdd = true;
		while (iter.hasNext()) {
			final T target = iter.next();
			if (reasoner.isSubClassOf(abox, newConcept, target))
				doAdd = false;
			else if (reasoner.isSubClassOf(abox, target, newConcept))
				iter.remove();
		}
		if (doAdd)
			targetSet.add(newConcept);

		return targetSet;
	}
	/// </editor-fold>
}

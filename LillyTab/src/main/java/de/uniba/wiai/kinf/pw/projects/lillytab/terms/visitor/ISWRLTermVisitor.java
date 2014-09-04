/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.visitor;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLTerm;


/**
 *
 * @author peter
 */
public interface ISWRLTermVisitor<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends ITermVisitor<ISWRLTerm<I, L, K, R>>
{
}

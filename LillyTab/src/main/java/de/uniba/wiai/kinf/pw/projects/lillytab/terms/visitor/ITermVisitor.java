/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.visitor;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.ITerm;


/**
 *
 * @author peter
 */
public interface ITermVisitor<Term extends ITerm>
{
	void visitEnter(final Term term);

	void visit(final Term term);

	void visitLeave(final Term term);
}

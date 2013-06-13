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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner;

import de.dhke.projects.cutil.collections.CollectionUtil;
import de.dhke.projects.cutil.collections.tree.IDecisionTree;
import de.uniba.wiai.kinf.pw.projects.lillytab.IReasonerResult;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentABoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentRBoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IIndividualABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.blocking.IBlockingStrategy;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.blocking.SubsetBlockingStrategy;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.ForAllCompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.FunctionalRoleMergeCompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.IntersectionCompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.RoleRestrictionCompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.SemanticUnionCompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.SomeCompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.SymmetricRoleCompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.TransitiveCompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.UnionCompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.util.ICompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleProperty;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleType;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.TermUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;


/**
 * An implementation of an SHOIF(D) satisfiability tester. <p /> This class serves as a consistency checker for
 * description logic {@link ABox}es. It employs a tablaux-method to expand the the ABox node graph and check for
 * possible inconsistencies. <p /> The reasoners main method is
 * {@link #checkConsistency(de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox)}. It will take a single ABox node as
 * input and perform completion on the ABox. Completion is performed by applying certain completion rules, which
 * generate new concept terms ({@link IABoxNode#getTerms()}) for nodes, propagate concept terms between nodes and may
 * potentially even create new nodes in the ABox. After the tableaux completion, it is possible to detect
 * inconsistencies locally by comparing and individual node's concept terms for clashes. <p />
 * Some completion rules are non-deterministic and thus multiple different ABox-completions may result from the tablaux
 * expansion.. The reasoner will return all clash-free completitions of a particular ABox.
 *
 * @param <I> The type for nominals and values
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class Reasoner<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends AbstractReasoner<I, L, K, R>
{
	// private ABox<I, L, K, R> _initialAbox;
	private ReasonerOptions _reasonerOptions;
	private INodeConsistencyChecker<I, L, K, R> _nodeConsistencyChecker;

	public Reasoner(
		final INodeConsistencyChecker<I, L, K, R> cChecker,
		final ReasonerOptions reasonerOptions)
	{
		_nodeConsistencyChecker = cChecker;
		_reasonerOptions = reasonerOptions;
	}

	public Reasoner(
		final INodeConsistencyChecker<I, L, K, R> cChecker)
	{
		this(cChecker, new ReasonerOptions());
	}

	public Reasoner(final ReasonerOptions options)
	{
		_nodeConsistencyChecker = new NodeConsistencyChecker<>(options.isTracing());
		_reasonerOptions = options;
	}

	public Reasoner()
	{
		this(new ReasonerOptions());
	}

	/**
	 * Setup suitable non-generating completers to perform consistency checking on {@literal abox}.
	 * <p />
	 * This only adds those completers required to handle the supplied {@literal abox}
	 * <p />
	 * You can override this in derived classes to customize reasoner operation.
	 *
	 *
	 * @param abox The ABox to generate the completer list for
	 * @return The list of generating (non-node creating) completers for the reasoner.
	 */
	protected List<ICompleter<I, L, K, R>> getNonGeneratingCompleters(
		final IABox<I, L, K, R> abox)
	{
		final IRBox<I, L, K, R> rbox = abox.getTBox().getRBox();

		final List<ICompleter<I, L, K, R>> nonGeneratingCompleters = new ArrayList<>();

		if ((!rbox.getAssertedRBox().getRoleDomains().isEmpty()) || (!rbox.getAssertedRBox().getRoleRanges().isEmpty())) {
			nonGeneratingCompleters.
				add(new RoleRestrictionCompleter<>(getNodeConsistencyChecker(), getReasonerOptions().isTracing()));
		}

		nonGeneratingCompleters.add(new IntersectionCompleter<>(getNodeConsistencyChecker(), getReasonerOptions().
			isTracing()));
		nonGeneratingCompleters.
			add(new ForAllCompleter<>(getNodeConsistencyChecker(), getReasonerOptions().isTracing()));

		if (!CollectionUtil.isNullOrEmpty(rbox.getRoles(RoleProperty.TRANSITIVE))) {
			nonGeneratingCompleters.add(new TransitiveCompleter<>(_nodeConsistencyChecker, getReasonerOptions().
				isTracing()));
		}
		if (!CollectionUtil.isNullOrEmpty((rbox.getRoles(RoleProperty.SYMMETRIC)))) {
			throw new IllegalArgumentException("Symmetric roles are not supported, yet");
//			nonGeneratingCompleters.add(new SymmetricRoleCompleter<>(_nodeConsistencyChecker, getReasonerOptions().
//				isTracing()));		
		}


		if (_reasonerOptions.isSemanticBranching()) {
			nonGeneratingCompleters.add(new SemanticUnionCompleter<>(getNodeConsistencyChecker(), getReasonerOptions().
				isTracing()));
		} else {
			nonGeneratingCompleters.add(new UnionCompleter<>(getNodeConsistencyChecker(), getReasonerOptions().
				isTracing()));
		}

		if (rbox.hasInverseRoles()) {
			throw new IllegalArgumentException("Inverse roles are not supported, yet");
		}


		if (!CollectionUtil.isNullOrEmpty(rbox.getRoles(RoleProperty.INVERSE_FUNCTIONAL))) {
			throw new IllegalArgumentException("Inverse functional roles are not supported, yet");
		}
		if (!CollectionUtil.isNullOrEmpty(rbox.getRoles(RoleProperty.FUNCTIONAL))) {
			nonGeneratingCompleters.add(new FunctionalRoleMergeCompleter<>(_nodeConsistencyChecker,
																		   getReasonerOptions().
				isTracing()));
		}

		return nonGeneratingCompleters;
	}

	/**
	 *
	 * Create the list of generating completers for the reasoner.
	 * <p />
	 * This only adds those completers required to handle the supplied {@literal abox}
	 * <p />
	 * You can override this in derived classes to customize reasoner operation.
	 *
	 *
	 * @param abox The ABox to generate the completer list for
	 * @return The list of generating (node creating) completers for the reasoner.
	 *
	 */
	protected List<ICompleter<I, L, K, R>> getGeneratingCompleters(
		final IABox<I, L, K, R> abox)
	{
		final List<ICompleter<I, L, K, R>> generatingCompleters = new ArrayList<>();

		generatingCompleters.add(new SomeCompleter<>(getNodeConsistencyChecker(), getReasonerOptions().isTracing()));
		return generatingCompleters;
	}

	/**
	 *
	 * Set the node consistency checker for reasoning.
	 * <p />
	 * You can override this in derived classes to customize reasoner operation.
	 *
	 *
	 */
	protected void setupNodeConsistencyChecker()
	{
		setNodeConsistencyChecker(new NodeConsistencyChecker<I, L, K, R>(getReasonerOptions().isTracing()));
	}

	/// <editor-fold defaultstate="collapsed" desc="Prepare functions">
	/**
	 * Update the {@literal abox} for expansion. All global concepts that cannot be undolfded lazily are propagated into
	 * the existing nodes. Scan the node for unfoldable concepts and unfoled them recursively.
	 *
	 * @see IABoxNode#addClassTerm(de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression)
	 * @param abox
	 */
	/**
	 * Create the initial branch for reasoning. This copied the ABox and initializes a new {@link Branch } object..
	 *
	 * @param abox The ABox to start with. A copy is made.
	 * @return An empty, freshly created branch for the supplied ABox.
	 */
	private Branch<I, L, K, R> prepareInitialBranch(final IABox<I, L, K, R> abox)
		throws ENodeMergeException, EInconsistentRBoxException
	{
		/*
		 * start with a copy of the initial abox
		 */
		final Branch<I, L, K, R> initialBranch = new Branch<>(abox.clone(), _reasonerOptions.isMergeTracking());

		/**
		 * Make sure, existing terms have been considered for lazy unfolding. This is no longer needed.
		 */
		// initialBranch.getABox().unfoldAll();
		Iterator<IABoxNode<I, L, K, R>> nodeIter = initialBranch.getABox().iterator();

		/* add global descriptions to all nodes */
		while (nodeIter.hasNext()) {
			final IABoxNode<I, L, K, R> node = nodeIter.next();
			if (node instanceof IIndividualABoxNode) {
				final IIndividualABoxNode<I, L, K, R> iNode = (IIndividualABoxNode<I, L, K, R>) node;
				final NodeMergeInfo<I, L, K, R> mergeInfo = iNode.addTerms(
					initialBranch.getABox().getTBox().getGlobalDescriptions());
				if (!mergeInfo.getMergedNodes().isEmpty()) {
					nodeIter = initialBranch.getABox().iterator();
				}
			}
		}

		return initialBranch;
	}

//	private Queue<Branch<I, L, K, R>> prepareInitialBranchQueue(final Branch<I, L, K, R> initialBranch)
//	{
//		Queue<Branch<I, L, K, R>> branchQueue = new StackQueue<Branch<I, L, K, R>>();
//		// new Stac<Branch<I, L, K, R>>();
//		branchQueue.add(initialBranch);
//		return branchQueue;
//	}
	private BranchTree<I, L, K, R> prepareBranchTree(final Branch<I, L, K, R> initialBranch)
	{
		final BranchTree<I, L, K, R> branchTree = new BranchTree<>();
		branchTree.fork(branchTree.getRoot(), initialBranch);
		return branchTree;
	}

	/// </editor-fold>
	@Override
	public Collection<? extends IReasonerResult<I, L, K, R>> checkConsistency(final IABox<I, L, K, R> abox,
																			  final IDLClassExpression<I, L, K, R> concept,
																			  final boolean stopAtFirstModel)
		throws EReasonerException, EInconsistencyException
	{
		// logInfo("REASONER CALL TO isInRange(%s, %s, %s)", abox.getID(), concept, stopAtFirstModel);

		Branch<I, L, K, R> initialBranch;
		try {
			initialBranch = prepareInitialBranch(abox);
			final IIndividualABoxNode<I, L, K, R> conceptNode = initialBranch.getABox().createIndividualNode();
			conceptNode.addClassTerm(concept);
		} catch (ENodeMergeException ex) {
			// XXX - better exception */
			throw new EInconsistentABoxException(abox, "Node merge problem");
		}

		final List<ICompleter<I, L, K, R>> generatingCompleters = getGeneratingCompleters(
			initialBranch.getABox());
		final List<ICompleter<I, L, K, R>> nonGeneratingCompleters = getNonGeneratingCompleters(
			initialBranch.getABox());

		final BranchTree<I, L, K, R> branchTree = new BranchTree<>();
		branchTree.fork(branchTree.getRoot(), initialBranch);

		final IBlockingStrategy<I, L, K, R> blockingStrategy = new SubsetBlockingStrategy<>();
		return complete(branchTree, nonGeneratingCompleters, generatingCompleters, blockingStrategy, stopAtFirstModel);
	}

	@Override
	public Collection<? extends IReasonerResult<I, L, K, R>> checkConsistency(final IABox<I, L, K, R> abox,
																			  final boolean stopAtFirstModel)
		throws EReasonerException, EInconsistencyException
	{
		// logInfo("REASONER CALL TO checkConsistency(%s, %s)", abox.getID(), stopAtFirstModel);
		try {
			final Branch<I, L, K, R> initialBranch = prepareInitialBranch(abox);

			final List<ICompleter<I, L, K, R>> generatingCompleters = getGeneratingCompleters(
				initialBranch.getABox());
			final List<ICompleter<I, L, K, R>> nonGeneratingCompleters = getNonGeneratingCompleters(
				initialBranch.getABox());

			final IBlockingStrategy<I, L, K, R> blockingStrategy = new SubsetBlockingStrategy<>();
			final Collection<? extends IReasonerResult<I, L, K, R>> results = complete(initialBranch,
																					   nonGeneratingCompleters,
																					   generatingCompleters,
																					   blockingStrategy,
																					   stopAtFirstModel);
			if (results.isEmpty())
				throw new EInconsistentABoxException(abox);
			else
				return results;
		} catch (ENodeMergeException ex) {
			throw new EInconsistentABoxNodeException(ex.getSourceNode());
		}
	}

	@Override
	public boolean isSubClassOf(
		final IABox<I, L, K, R> abox,
		final IDLClassExpression<I, L, K, R> presumedSub,
		final IDLClassExpression<I, L, K, R> presumedSuper)
		throws EReasonerException, EInconsistencyException
	{
		// logInfo("REASONER CALL TO isSubClassOf(%s, %s, %s)", abox.getID(), presumedSub, presumedSuper);
		assert isConsistent(abox);

		try {
			if (TermUtil.isSyntacticSubClass(presumedSub, presumedSuper, abox.getDLTermFactory())) {
				return true;
			} else {
				final Branch<I, L, K, R> initialBranch = prepareInitialBranch(abox);
				IIndividualABoxNode<I, L, K, R> node = initialBranch.getABox().createIndividualNode();
				final IDLTermFactory<I, L, K, R> termFactory = initialBranch.getABox().getDLTermFactory();
				final IDLClassExpression<I, L, K, R> sub = TermUtil.toNNF(presumedSub, termFactory);
				IDLClassExpression<I, L, K, R> negSuper = termFactory.getDLObjectNegation(presumedSuper);
				negSuper = TermUtil.toNNF(negSuper, termFactory);

				/**
				 * track node merges
				 */
				node = (IIndividualABoxNode<I, L, K, R>) node.addClassTerm(sub).getCurrentNode();
				/* node = */ node.addClassTerm(negSuper).getCurrentNode();
				final List<ICompleter<I, L, K, R>> generatingCompleters = getGeneratingCompleters(
					initialBranch.getABox());
				final List<ICompleter<I, L, K, R>> nonGeneratingCompleters = getNonGeneratingCompleters(
					initialBranch.getABox());

				final IBlockingStrategy<I, L, K, R> blockingStrategy = new SubsetBlockingStrategy<>();
				final Collection<? extends IReasonerResult<I, L, K, R>> results =
					complete(initialBranch, nonGeneratingCompleters, generatingCompleters, blockingStrategy, true);

				return results.isEmpty();
			}
		} catch (ENodeMergeException ex) {
			throw new EInconsistentABoxNodeException(ex.getSourceNode());
		}
	}

	private boolean isConsistent(Branch<I, L, K, R> initialBranch,
								 final List<ICompleter<I, L, K, R>> nonGeneratingCompleters,
								 final List<ICompleter<I, L, K, R>> generatingCompleters)
		throws EReasonerException, EInconsistencyException
	{
		final IBlockingStrategy<I, L, K, R> blockingStrategy = new SubsetBlockingStrategy<>();

		final Collection<? extends IReasonerResult<I, L, K, R>> results = complete(initialBranch,
																				   nonGeneratingCompleters,
																				   generatingCompleters,
																				   blockingStrategy, true);
		return !results.isEmpty();
	}

	@Override
	public boolean isInDomain(final IABox<I, L, K, R> abox, final IDLClassExpression<I, L, K, R> desc,
							  final R role)
		throws EReasonerException, EInconsistencyException
	{
		// logInfo("REASONER CALL TO isInDomain(%s, %s, %s)", abox.getID(), desc, role);
		try {
			final Collection<IDLClassExpression<I, L, K, R>> roleDomains = abox.getTBox().getRBox().getRoleDomains(
				role);

			/*
			 * shortcut check first, avoid reasoning
			 */
			if ((roleDomains != null) && roleDomains.contains(desc)) {
				return true;
			} else {
				final Branch<I, L, K, R> initialBranch = prepareInitialBranch(abox);

				/*
				 * remember to use createNode from branch here!
				 */
				final IIndividualABoxNode<I, L, K, R> node = initialBranch.getABox().createIndividualNode();
				node.addClassTerm(desc);
				final boolean isDataTypeRole = abox.getTBox().getRBox().hasRoleType(role, RoleType.DATA_PROPERTY);
				final IABoxNode<I, L, K, R> succ = initialBranch.getABox().createNode(isDataTypeRole);
				node.getRABox().getAssertedSuccessors().put(role, succ.getNodeID());

				final List<ICompleter<I, L, K, R>> generatingCompleters = getGeneratingCompleters(
					initialBranch.getABox());
				final List<ICompleter<I, L, K, R>> nonGeneratingCompleters = getNonGeneratingCompleters(
					initialBranch.getABox());

				return isConsistent(initialBranch, nonGeneratingCompleters, generatingCompleters);
			}
		} catch (ENodeMergeException ex) {
			throw new EInconsistentABoxNodeException(ex.getSourceNode());
		}
	}

	@Override
	public boolean isInRange(final IABox<I, L, K, R> abox, final IDLClassExpression<I, L, K, R> desc,
							 final R role)
		throws EReasonerException, EInconsistencyException
	{
		// logInfo("REASONER CALL TO isInRange(%s, %s, %s)", abox.getID(), desc, role);
		try {
			final Collection<IDLRestriction<I, L, K, R>> roleDomains = abox.getTBox().getRBox().getRoleRanges(
				role);
			/*
			 * shortcut check first, avoid reasoning
			 */
			if ((roleDomains != null) && roleDomains.contains(desc)) {
				return true;
			}
			{
				final Branch<I, L, K, R> initialBranch = prepareInitialBranch(abox);

				final IIndividualABoxNode<I, L, K, R> node = initialBranch.getABox().createIndividualNode();
				node.addClassTerm(desc);
				final boolean isDataTypeRole = abox.getTBox().getRBox().hasRoleType(role, RoleType.DATA_PROPERTY);
				final IABoxNode<I, L, K, R> succ = initialBranch.getABox().createNode(isDataTypeRole);
				succ.getRABox().getAssertedSuccessors().put(role, node.getNodeID());

				final List<ICompleter<I, L, K, R>> generatingCompleters = getGeneratingCompleters(
					initialBranch.getABox());
				final List<ICompleter<I, L, K, R>> nonGeneratingCompleters = getNonGeneratingCompleters(
					initialBranch.getABox());

				return isConsistent(initialBranch, nonGeneratingCompleters, generatingCompleters);
			}
		} catch (ENodeMergeException ex) {
			throw new EInconsistentABoxNodeException(ex.getSourceNode());
		}
	}

	@Override
	public boolean isDisjoint(
		final IABox<I, L, K, R> abox,
		final IDLClassExpression<I, L, K, R> desc1,
		final IDLClassExpression<I, L, K, R> desc2)
		throws EReasonerException, EInconsistencyException
	{
		// logInfo("REASONER CALL TO isDisjoint(%s, %s, %s)", abox.getID(), desc1, desc2);
		try {
			if (desc1.equals(desc2)) /*
			 * terms are equal, cannot be disjoint
			 */ {
				return false;
			} else if (TermUtil.isSyntacticNegation(desc1, desc2, abox.getDLTermFactory())) /*
			 * syntactic shortcut check
			 */ {
				return true;
			} else {
				final Branch<I, L, K, R> initialBranch = prepareInitialBranch(abox);
				IIndividualABoxNode<I, L, K, R> node = initialBranch.getABox().createIndividualNode();
				/**
				 * Add both descriptions to the same node. If the resulting ABox is inconsistent, the concepts are
				 * disjoint
				 *
				 */
				node = (IIndividualABoxNode<I, L, K, R>) node.addClassTerm(desc1).getCurrentNode();
				node.addClassTerm(desc2);

				final List<ICompleter<I, L, K, R>> generatingCompleters = getGeneratingCompleters(
					initialBranch.getABox());
				final List<ICompleter<I, L, K, R>> nonGeneratingCompleters = getNonGeneratingCompleters(
					initialBranch.getABox());

				return !isConsistent(initialBranch, generatingCompleters, nonGeneratingCompleters);
			}
		} catch (ENodeMergeException ex) {
			throw new EInconsistentABoxNodeException(ex.getSourceNode());
		}
	}

	private Collection<? extends ReasonerResult<I, L, K, R>> complete(
		final Branch<I, L, K, R> initialBranch,
		final List<ICompleter<I, L, K, R>> nonGeneratingCompleters,
		final List<ICompleter<I, L, K, R>> generatingCompleters,
		final IBlockingStrategy<I, L, K, R> blockingStrategy,
		final boolean stopAtFirstModel)
		throws EReasonerException
	{


		BranchTree<I, L, K, R> branchTree = prepareBranchTree(initialBranch);

		final Collection<? extends ReasonerResult<I, L, K, R>> reasonerResults = complete(branchTree,
																						  nonGeneratingCompleters,
																						  generatingCompleters,
																						  blockingStrategy,
																						  stopAtFirstModel);
		return reasonerResults;
	}

	/**
	 * Perform completion on all branches below {@literal branchPoint}.
	 *
	 * @param branchQueue A queue of branches to complete. Will be extended
	 * @param stopAtFirstModel Shall we stop at the first model or determinal ALL saturated tableaux
	 * @return A collection of consistent {@link IABox}es.
	 * @throws EInconsistentOntologyException No consistent ABox was found.
	 * @throws EReasonerException A reasoner error occured
	 */
	private Collection<? extends ReasonerResult<I, L, K, R>> complete(
		final BranchTree<I, L, K, R> branchTree,
		final List<ICompleter<I, L, K, R>> nonGeneratingCompleters,
		final List<ICompleter<I, L, K, R>> generatingCompleters,
		final IBlockingStrategy<I, L, K, R> blockingStrategy,
		final boolean stopAtFirstModel)
		throws EReasonerException
	{
		final Collection<ReasonerResult<I, L, K, R>> reasonerResults = new HashSet<>();

		IDecisionTree.Node<Branch<I, L, K, R>> branchNode = pickBranch(branchTree);
		while ((branchNode != null) && ((!stopAtFirstModel) || reasonerResults.isEmpty())) {
			final ReasonerContinuationState contState = completeBranch(branchNode,
																	   nonGeneratingCompleters,
																	   generatingCompleters, blockingStrategy);
			if (contState == ReasonerContinuationState.DONE) {
				/* store completed branch, but only if it is consistent. */
				if (!branchNode.getData().getConsistencyInfo().isInconsistent()) {
					reasonerResults.add(branchNode.getData().dispose());
				}
				branchNode.remove();
			} else {
				if (contState == ReasonerContinuationState.INCONSISTENT) {
					/* prune branch tree according to the culprits recorded for the current branch */
					logFinest("Inconsistent branch found: %s", branchNode.getData());
					logFinest("Clash info: %s", branchNode.getData().getConsistencyInfo());
				}
				pruneBranchTree(branchTree, branchNode);
			}

			branchNode = pickBranch(branchTree);
		}

		if (getReasonerOptions().isTracing()) {
			logFine("Reasoning complete");
			if (reasonerResults.isEmpty()) {
				logFiner("No models found");
			} else {
				logFinest("The following models were found:");
				for (ReasonerResult<I, L, K, R> result : reasonerResults) {
					logFinest(result);
				}
			}
		}

		return reasonerResults;
	}

	/**
	 * Pick the first branch from {@literal branchQueue} and perform ABox completion on its {@link IABox}. <p />
	 * Completition rules are applied according to the following algorithm. <ul>
	 * <li> A node is picked from the first branch's {@link Branch#getNonGeneratingQueue()
	 * }. </li><li> The list of {@link ICompleter}s from the {@link #_nonGeneratingCompleters} collection is traversed.
	 * </li><li> Each completer's {@link ICompleter#completeNode(de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode, de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.Branch, java.util.Collection)
	 * }
	 * method is invoked on the current node. This may re-add nodes either of both completion queues. </li><li> If the
	 * current completer's completion method returned {@literal true}, the next completer in the list will be used.
	 * Otherwise the current completion loop is aborted and completion restarts with picking a node from the
	 * non-generating queue. </li><li> The process is repeated until the non-generating queue is empty. </li><li>
	 * When no more non-generating rules can be applied, the first node is picked from the current branch's {@link Branch#getGeneratingQueue()
	 * }.
	 * </li><li> The list of {@link ICompleter}s from the {@link #_generatingCompleters} collection is traversed.
	 * </li><li> Each completer's {@link ICompleter#completeNode(de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode, de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.Branch, java.util.Collection)
	 * }
	 * method is invoked on the current node. This may create new nodes.
	 * </li><li> If non-generating completer returned {@literal false}, this indicates a new node was generated. In this
	 * case, the algorithm returns to apply non-generating rules. </li><li> If no generating rule was applied, the next
	 * node is picked from the generating queue. </li><li> The process repeats until the generating queues is empty.
	 * </li> </ul> <p />
	 * Due to queue sorting, the current branch may change multiple times during the completion process. The function
	 * will return the first branch from the branch queue where no completion rules are applicable any more.
	 *
	 * @param bt The {@link BranchTree}.
	 * @return A completed branch
	 * @throws EReasonerException, EInconsistentOntologyException
	 */
	private ReasonerContinuationState completeBranch(
		final IDecisionTree.Node<Branch<I, L, K, R>> branchNode,
		final List<ICompleter<I, L, K, R>> nonGeneratingCompleters,
		final List<ICompleter<I, L, K, R>> generatingCompleters,
		final IBlockingStrategy<I, L, K, R> blockingStrategy)
		throws EReasonerException
	{
		int nLoops = 0;

		assert branchNode != null;
		final Branch<I, L, K, R> branch = branchNode.getData();
		assert branch != null;
		if (getReasonerOptions().isTracing()) {
			logFine("Before completion: %s", branch);
		}

		/**
		 * Iterate over both queues, performing completion steps in the required order (non-gen first, one generational
		 * step next)
		 *
		 */
		while (branch.hasMoreNonGeneratingNodes() || branch.hasMoreGeneratingNodes()) {
			while (branch.hasMoreNonGeneratingNodes()) {
				final IABoxNode<I, L, K, R> nextNonGenNode = branch.nextNonGeneratingNode();
				if (!blockingStrategy.isBlocked(nextNonGenNode)) {
					final ConsistencyInfo<I, L, K, R> cInfo = getNodeConsistencyChecker().
						isConsistent(nextNonGenNode);
					if (cInfo.isFinallyInconsistent()) {
						branch.upgradeConsistencyInfo(cInfo);
						return ReasonerContinuationState.INCONSISTENT;
					} else {
						/**
						 * Run any non-generating completers until one of them signals that we have to recheck to node queue
						 * or branch tree, or we run out of nodes on the non-generating queue.
						 */
						for (ICompleter<I, L, K, R> nonGenCompleter : nonGeneratingCompleters) {
							final ReasonerContinuationState contState = nonGenCompleter.completeNode(
								branchNode, nextNonGenNode);
							if ((contState == ReasonerContinuationState.RECHECK_BRANCH) || (contState == ReasonerContinuationState.INCONSISTENT)) {
								return contState;
							} else if (contState == ReasonerContinuationState.RECHECK_NODE) {
								break;
							}
						}
					}
				}
			}
			if (getReasonerOptions().isTracing()) {
				logFiner("After non-generating steps: %s", branch);
			}

			/**
			 * Pick the next node from the generating queue until at least one generating step took place or we run out
			 * of nodes.
			 */
			IABoxNode<I, L, K, R> nextGenNode;
			boolean wasGenerated = false;
			do {
				nextGenNode = branch.nextGeneratingNode();
				if ((nextGenNode != null) && (!blockingStrategy.isBlocked(nextGenNode))) {
					final ConsistencyInfo<I, L, K, R> cInfo = getNodeConsistencyChecker().isConsistent(
						nextGenNode);
					if (cInfo.isFinallyInconsistent()) {
						branch.upgradeConsistencyInfo(cInfo);
						return ReasonerContinuationState.INCONSISTENT;
					} else {
						/**
						 * Run any generating completers until one of them signals that we have to recheck to node queue
						 * or branch tree, or we run out of nodes on the non-generating queue.
						 *
						 */
						for (ICompleter<I, L, K, R> genCompleter : generatingCompleters) {
							final ReasonerContinuationState contState = genCompleter.completeNode(branchNode,
																								  nextGenNode);

							if ((contState == ReasonerContinuationState.RECHECK_BRANCH) || (contState == ReasonerContinuationState.INCONSISTENT)) {
								return contState;
							} else if (contState == ReasonerContinuationState.RECHECK_NODE) {
								wasGenerated = true;
								break;
							}
						}
					}
				}
			} while ((nextGenNode != null) && (!wasGenerated));
			if ((getReasonerOptions().isProgressLogging()) && (nLoops % 100 == 0)) {
				logFine("Branch %s: %d iterations, %d nodes on queue", branch.getABox().getID(), nLoops,
						branch.getGeneratingQueue().size() + branch.getNonGeneratingQueue().size());
				++nLoops;
			}
			if (getReasonerOptions().isTracing()) {
				logFiner("After generating step: %s", branch);
			}
		}

		return ReasonerContinuationState.DONE;
	}

	private IDecisionTree.Node<Branch<I, L, K, R>> pickBranch(final BranchTree<I, L, K, R> branchTree)
	{
		/*
		 * descend to leaf
		 */
		return branchTree.firstLeaf();
	}

	private void pruneBranchTree(
		final BranchTree<I, L, K, R> branchTree,
		final IDecisionTree.Node<Branch<I, L, K, R>> clashNode)
	{
		/**
		 * implement dependency directed backtracking: remove braches when the contain the culprit terms for current
		 * clash. (as determined by the clash information
		 */
		if ((clashNode != null) && (clashNode.getData() != null)) {
			final ConsistencyInfo<I, L, K, R> cInfo = clashNode.getData().getConsistencyInfo();
			IDecisionTree.Node<Branch<I, L, K, R>> nextNode = pickBranch(branchTree);
			int pruneCount = 0;
			while ((nextNode != null) && cInfo.hasClashingTerms(nextNode.getData().getABox())) {
				++pruneCount;
				if ((_reasonerOptions.isTracing()) && (pruneCount > 1)) {
					logFiner("DDB-pruning performed for, removed `%s'", nextNode);
				}
				nextNode.remove();
				nextNode = pickBranch(branchTree);
			}
		}
	}

	/**
	 * @return the _nodeConsistencyChecker
	 */
	public INodeConsistencyChecker<I, L, K, R> getNodeConsistencyChecker()
	{
		return _nodeConsistencyChecker;
	}

	/**
	 * @param nodeConsistencyChecker the _nodeConsistencyChecker to set
	 */
	public void setNodeConsistencyChecker(INodeConsistencyChecker<I, L, K, R> nodeConsistencyChecker)
	{
		this._nodeConsistencyChecker = nodeConsistencyChecker;
	}

	/**
	 * @return the _reasonerOptions
	 */
	protected ReasonerOptions getReasonerOptions()
	{
		return _reasonerOptions;
	}

	public void setReasonerOptions(ReasonerOptions reasonerOptions)
	{
		this._reasonerOptions = reasonerOptions;
	}

/// </editor-fold>
	protected boolean isNeedDoubleBlocking(final IABox<I, L, K, R> abox)
	{
		return abox.getTBox().getRBox().hasInverseRoles();
	}
}

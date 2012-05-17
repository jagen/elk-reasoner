/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.semanticweb.elk.reasoner;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.reasoner.consistency.ConsistencyChecking;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.OntologyIndexImpl;
import org.semanticweb.elk.reasoner.saturation.properties.ObjectPropertySaturation;
import org.semanticweb.elk.reasoner.taxonomy.IndividualClassTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.Node;
import org.semanticweb.elk.reasoner.taxonomy.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyComputation;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.TypeNode;

/**
 * The Reasoner manages an ontology (using an OntologyIndex) and provides
 * methods for solving reasoning tasks over that ontology. Ontologies are loaded
 * and updated by adding or removing {@link ElkAxiom}s (methods addAxiom() and
 * removeAxiom()). The Reasoner will ensure that all methods that solve
 * reasoning tasks return an answer that is correct for the current ontology,
 * that is, the reasoner updates its answers when changes occur.
 * 
 * Reasoners are created (and pre-configured) by the {@link ReasonerFactory}.
 */
public class Reasoner {
	/**
	 * The progress monitor that is used for reporting progress.
	 */
	protected ProgressMonitor progressMonitor;
	/**
	 * Logger for events.
	 */
	protected final static Logger LOGGER_ = Logger.getLogger(Reasoner.class);
	/**
	 * Executor used to run the jobs.
	 */
	protected final ExecutorService executor;
	/**
	 * Number of workers for concurrent jobs.
	 */
	protected final int workerNo;
	/**
	 * Should fresh entities in reasoner queries be accepted (configuration
	 * setting). If false, a FreshEntityException will be thrown when
	 * encountering entities that did not occur in the ontology.
	 */
	protected boolean allowFreshEntities;

	/**
	 * OntologyIndex for the ontology on which this reasoner operates.
	 */
	protected OntologyIndex ontologyIndex;

	/**
	 * Consistency flag for the current ontology;
	 */
	protected boolean consistentOntology;
	/**
	 * Taxonomy that stores (partial) reasoning results.
	 */
	protected IndividualClassTaxonomy taxonomy;
	/**
	 * Indicates if the reasoner has been initialized after ontology change.
	 */
	protected boolean doneInitialization = false;
	/**
	 * Indicates if classes have been classified after ontology change.
	 */
	protected boolean doneClassTaxonomy = false;
	/**
	 * Indicates if individuals have been classified after ontology change.
	 */
	protected boolean doneIndividualTaxonomy = false;

	/**
	 * Constructor. In most cases, Reasoners should be created by the
	 * {@link ReasonerFactory}.
	 * 
	 * @param executor
	 * @param workerNo
	 */
	protected Reasoner(ExecutorService executor, int workerNo) {
		this.executor = executor;
		this.workerNo = workerNo;
		this.progressMonitor = new DummyProgressMonitor();
		this.allowFreshEntities = true;
		reset();
	}

	/**
	 * Set the progress monitor that will be used for reporting progress on all
	 * potentially long-running operations.
	 * 
	 * @param progressMonitor
	 */
	public void setProgressMonitor(ProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}

	/**
	 * Get the progress monitor that is used for reporting progress on all
	 * potentially long-running operations.
	 */
	public ProgressMonitor getProgressMonitor() {
		return progressMonitor;
	}

	/**
	 * Set if fresh entities should be allowed. Fresh entities are entities that
	 * occur as parameters of reasoner queries without occurring in the loaded
	 * ontology. If false, a FreshENtityException will be thrown in such cases.
	 * If true, the reasoner will answer as if the entity had been declared (but
	 * not used in any axiom).
	 * 
	 * @param allow
	 */
	public void setAllowFreshEntities(boolean allow) {
		allowFreshEntities = allow;
	}

	/**
	 * Get whether fresh entities are allowed. See setAllowFreshEntities() for
	 * details.
	 * 
	 * @return
	 */
	public boolean getAllowFreshEntities() {
		return allowFreshEntities;
	}

	/**
	 * Reset the ontology all data. After this, the Reasoner holds an empty
	 * ontology.
	 */
	public void reset() {
		ontologyIndex = new OntologyIndexImpl();
		// no need to reset contexts since a fresh index starts with empty
		// contexts
		invalidate();
	}

	/**
	 * Invalidate all previously computed reasoning results. This does not mean
	 * that the deductions that have been made are deleted. Rather, this method
	 * would be called if the deductions (and the index in general) have
	 * changed, and the Reasoner should not rely on its records of earlier
	 * deductions any longer.
	 */
	protected void invalidate() {
		if (doneInitialization) {
			doneInitialization = false;
			doneClassTaxonomy = false;
			doneIndividualTaxonomy = false;
		}
	}

	/**
	 * Get the OntologyIndex. When changing this index (adding ro deleting
	 * axioms), the Reasoner will not be notified, and may thus fail to return
	 * up-to-date answers for reasoning tasks.
	 * 
	 * FIXME Should this really be public? If yes, then the index should notify
	 * the reasoner of changes.
	 * 
	 * @return the internal ontology index
	 */
	public OntologyIndex getOntologyIndex() {
		return ontologyIndex;
	}

	/**
	 * Add an axom to the ontology. This method makes sure that internal
	 * reasoning results are updated as required, so that all reasoning methods
	 * return correct answers in the future. Previously returned results (e.g.,
	 * taxonomies) will not be updated.
	 * 
	 * TODO Clarify what happens if an axiom is added twice.
	 * 
	 * @param axiom
	 */
	public void addAxiom(ElkAxiom axiom) {
		ontologyIndex.getAxiomInserter().process(axiom);
		invalidate();
	}

	/**
	 * Remove an axom from the ontology. This method makes sure that internal
	 * reasoning results are updated as required, so that all reasoning methods
	 * return correct answers in the future. Previously returned results (e.g.,
	 * taxonomies) will not be updated.
	 * 
	 * TODO Clarify what happens when deleting axioms that were not added.
	 * 
	 * @param axiom
	 */
	public void removeAxiom(ElkAxiom axiom) {
		ontologyIndex.getAxiomDeleter().process(axiom);
		invalidate();
	}

	/**
	 * Initialises reasoning by saturating object properties and checking
	 * consistency, if not yet done.
	 */
	protected void initializeReasoning() {
		if (doneInitialization)
			return;

		// saturate object properties
		(new ObjectPropertySaturation(executor, workerNo, ontologyIndex))
				.compute();

		// reset context
		for (IndexedClassExpression ice : ontologyIndex
				.getIndexedClassExpressions())
			ice.resetContext();

		// check consistency
		consistentOntology = (new ConsistencyChecking(executor, workerNo,
				progressMonitor, ontologyIndex)).checkConsistent();

		doneInitialization = true;
	}

	/**
	 * Check if the ontology is consistent, that is, satisfiable.
	 */
	public boolean isConsistent() {
		initializeReasoning();
		return consistentOntology;
	}

	/**
	 * Return the inferred taxonomy of the named classes. If this has not been
	 * computed yet, then it will be computed at this point.
	 * 
	 * @return class taxonomy
	 */
	public Taxonomy<ElkClass> getTaxonomy()
			throws InconsistentOntologyException {
		if (!isConsistent())
			throw new InconsistentOntologyException();

		if (!doneClassTaxonomy) {
			taxonomy = (new TaxonomyComputation(executor, workerNo,
					progressMonitor, ontologyIndex)).computeTaxonomy(true,
					false);
			doneClassTaxonomy = true;
		}

		return taxonomy;
	}

	/**
	 * Return the inferred taxonomy of the named classes and named individuals.
	 * If this has not been computed yet, then it will be computed at this
	 * point.
	 * 
	 * @return instance taxonomy
	 */
	public InstanceTaxonomy<ElkClass, ElkNamedIndividual> getInstanceTaxonomy()
			throws InconsistentOntologyException {
		if (!isConsistent())
			throw new InconsistentOntologyException();

		if (doneIndividualTaxonomy)
			return taxonomy;

		// reuse class taxonomy if already computed
		if (doneClassTaxonomy)
			taxonomy = (new TaxonomyComputation(executor, workerNo,
					progressMonitor, ontologyIndex, taxonomy).computeTaxonomy(
					false, true));
		else
			taxonomy = (new TaxonomyComputation(executor, workerNo,
					progressMonitor, ontologyIndex).computeTaxonomy(true, true));
		doneClassTaxonomy = true;
		doneIndividualTaxonomy = true;

		return taxonomy;
	}

	/**
	 * Helper method to get a taxonomy node node from the taxonomy.
	 * 
	 * @param elkClass
	 * @return
	 * @throws FreshEntitiesException
	 * @throws InconsistentOntologyException
	 */
	protected TaxonomyNode<ElkClass> getTaxonomyNode(ElkClass elkClass)
			throws FreshEntitiesException, InconsistentOntologyException {
		TaxonomyNode<ElkClass> node = getTaxonomy().getNode(elkClass);
		if (node == null) {
			if (allowFreshEntities) {
				node = new FreshClassTypeNode(elkClass);
			} else {
				throw new FreshEntitiesException(elkClass);
			}
		}
		return node;
	}

	/**
	 * Helper method to get an instance node node from the taxonomy.
	 * 
	 * @param elkNamedIndividual
	 * @return
	 * @throws FreshEntitiesException
	 * @throws InconsistentOntologyException
	 */
	protected InstanceNode<ElkClass, ElkNamedIndividual> getInstanceNode(
			ElkNamedIndividual elkNamedIndividual)
			throws FreshEntitiesException, InconsistentOntologyException {
		InstanceNode<ElkClass, ElkNamedIndividual> node = getInstanceTaxonomy()
				.getInstanceNode(elkNamedIndividual);
		if (node == null) {
			if (allowFreshEntities) {
				node = new FreshClassInstanceNode(elkNamedIndividual);
			} else {
				throw new FreshEntitiesException(elkNamedIndividual);
			}
		}
		return node;
	}

	/**
	 * Helper method to get a type node node from the taxonomy.
	 * 
	 * @param elkClass
	 * @return
	 * @throws FreshEntitiesException
	 * @throws InconsistentOntologyException
	 */
	protected TypeNode<ElkClass, ElkNamedIndividual> getTypeNode(
			ElkClass elkClass) throws FreshEntitiesException,
			InconsistentOntologyException {
		TypeNode<ElkClass, ElkNamedIndividual> node = getInstanceTaxonomy()
				.getTypeNode(elkClass);
		if (node == null) {
			if (allowFreshEntities) {
				node = new FreshClassTypeNode(elkClass);
			} else {
				throw new FreshEntitiesException(elkClass);
			}
		}
		return node;
	}

	/**
	 * Get the class node for one named class. This provides information about
	 * all equivalent classes. In theory, this does not demand that a full
	 * taxonomy is built, and the result does not provide access to such an
	 * object (or to sub- or superclasses).
	 * 
	 * @param elkClass
	 * @return
	 * @throws FreshEntitiesException
	 *             thrown if the given class is not known in the ontology yet
	 */
	public Node<ElkClass> getClassNode(ElkClass elkClass)
			throws FreshEntitiesException, InconsistentOntologyException {
		return getTaxonomyNode(elkClass);
	}

	/**
	 * Return the (direct or indirect) subclasses of the given
	 * {@link ElkClassExpression}. The method returns a set of Node<ElkClass>,
	 * each of which might represent multiple equivalent classes. In theory,
	 * this method does not require the whole taxonomy to be constructed, and
	 * the result does not provide (indirect) access to a taxonomy object.
	 * 
	 * @param classExpression
	 *            currently, only objects of type ElkClass are supported
	 * @param direct
	 *            if true, only direct subclasses are returned
	 * @throws FreshEntitiesException
	 * @throws InconsistentOntologyException
	 */
	public Set<? extends Node<ElkClass>> getSubClasses(
			ElkClassExpression classExpression, boolean direct)
			throws FreshEntitiesException, InconsistentOntologyException {
		if (classExpression instanceof ElkClass) {
			TaxonomyNode<ElkClass> node = getTaxonomyNode((ElkClass) classExpression);
			return (direct) ? node.getDirectSubNodes() : node.getAllSubNodes();
		} else { // TODO: complex class expressions currently not supported
			throw new UnsupportedOperationException(
					"ELK does not support retrieval of superclasses for unnamed class expressions.");
		}
	}

	/**
	 * Return the (direct or indirect) superclasses of the given
	 * {@link ElkClassExpression}. The method returns a set of Node<ElkClass>,
	 * each of which might represent multiple equivalent classes. In theory,
	 * this method does not require the whole taxonomy to be constructed, and
	 * the result does not provide (indirect) access to a taxonomy object.
	 * 
	 * @param classExpression
	 *            currently, only objects of type ElkClass are supported
	 * @param direct
	 *            if true, only direct superclasses are returned
	 * @throws FreshEntitiesException
	 * @throws InconsistentOntologyException
	 */
	public Set<? extends Node<ElkClass>> getSuperClasses(
			ElkClassExpression classExpression, boolean direct)
			throws FreshEntitiesException, InconsistentOntologyException {
		if (classExpression instanceof ElkClass) {
			TaxonomyNode<ElkClass> node = getTaxonomyNode((ElkClass) classExpression);
			return (direct) ? node.getDirectSuperNodes() : node
					.getAllSuperNodes();
		} else { // TODO: complex class expressions currently not supported
			throw new UnsupportedOperationException(
					"ELK does not support retrieval of superclasses for unnamed class expressions.");
		}
	}

	/**
	 * Return the (direct or indirect) instances of the given
	 * {@link ElkClassExpression}. The method returns a set of
	 * Node<ElkNamedIndividual>, each of which might represent multiple
	 * equivalent classes. In theory, this method does not require the whole
	 * taxonomy to be constructed, and the result does not provide (indirect)
	 * access to a taxonomy object.
	 * 
	 * @param classExpression
	 *            currently, only objects of type ElkClass are supported
	 * @param direct
	 *            if true, only direct subclasses are returned
	 * @throws FreshEntitiesException
	 * @throws InconsistentOntologyException
	 */
	public Set<? extends Node<ElkNamedIndividual>> getInstances(
			ElkClassExpression classExpression, boolean direct)
			throws FreshEntitiesException, InconsistentOntologyException {
		if (classExpression instanceof ElkClass) {
			TypeNode<ElkClass, ElkNamedIndividual> node = getTypeNode((ElkClass) classExpression);
			return direct ? node.getDirectInstanceNodes() : node
					.getAllInstanceNodes();
		} else { // TODO: complex class expressions currently not supported
			throw new UnsupportedOperationException(
					"ELK does not support retrieval of instances for unnamed class expressions.");
		}
	}

	/**
	 * Return the (direct or indirect) types of the given
	 * {@link ElkNamedIndividual}. The method returns a set of Node<ElkClass>,
	 * each of which might represent multiple equivalent classes. In theory,
	 * this method does not require the whole taxonomy to be constructed, and
	 * the result does not provide (indirect) access to a taxonomy object.
	 * 
	 * @param elkNamedIndividual
	 * @param direct
	 *            if true, only direct types are returned
	 * @throws FreshEntitiesException
	 * @throws InconsistentOntologyException
	 */
	public Set<? extends Node<ElkClass>> getTypes(
			ElkNamedIndividual elkNamedIndividual, boolean direct)
			throws FreshEntitiesException, InconsistentOntologyException {
		InstanceNode<ElkClass, ElkNamedIndividual> node = getInstanceNode(elkNamedIndividual);
		return direct ? node.getDirectTypeNodes() : node.getAllTypeNodes();
	}

	/**
	 * Check if the given class expression is satisfiable, that is, if it can
	 * possibly have instances. Classes that are not satisfiable if they are
	 * equivalent to owl:Nothing. A satisfiable class is also called consistent
	 * or coherent.
	 * 
	 * @param classExpression
	 *            currently, only objects of type ElkClass are supported
	 * @return
	 * @throws FreshEntitiesException
	 */
	public boolean isSatisfiable(ElkClassExpression classExpression)
			throws FreshEntitiesException, InconsistentOntologyException {
		if (classExpression instanceof ElkClass) {
			Node<ElkClass> classNode = getClassNode((ElkClass) classExpression);
			return (!classNode.getMembers().contains(
					PredefinedElkClass.OWL_NOTHING));
		} else {
			throw new UnsupportedOperationException(
					"ELK does not support satisfiability checking for unnamed class expressions");
		}
	}

	public void shutdown() {
		executor.shutdownNow();
	}

	// used for testing
	int getNumberOfWorkers() {
		return workerNo;
	}

	/**
	 * Auxiliary class for representing taxonomy nodes for fresh entities.
	 * 
	 * @author Markus Kroetzsch
	 * 
	 * @param <T>
	 */
	protected abstract class FreshTaxonomyNode<T extends ElkObject> implements
			Node<T> {
		protected final T elkClass;
		protected final TypeNode<ElkClass, ElkNamedIndividual> bottomNode;
		protected final TypeNode<ElkClass, ElkNamedIndividual> topNode;
		protected final InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy;

		public FreshTaxonomyNode(T elkClass)
				throws InconsistentOntologyException {
			// Get bottom and top node *now*; cannot do this later as ontology
			// might change
			this.taxonomy = Reasoner.this.getInstanceTaxonomy();
			this.bottomNode = taxonomy
					.getTypeNode(PredefinedElkClass.OWL_NOTHING);
			this.topNode = taxonomy.getTypeNode(PredefinedElkClass.OWL_THING);
			this.elkClass = elkClass;
		}

		@Override
		public Set<T> getMembers() {
			return Collections.unmodifiableSet(Collections.singleton(elkClass));
		}

		@Override
		public T getCanonicalMember() {
			return elkClass;
		}

		public InstanceTaxonomy<ElkClass, ElkNamedIndividual> getTaxonomy() {
			return taxonomy;
		}
	}

	/**
	 * Class for representing type nodes for fresh classes.
	 * 
	 * @author Markus Kroetzsch
	 */
	protected class FreshClassTypeNode extends FreshTaxonomyNode<ElkClass>
			implements TypeNode<ElkClass, ElkNamedIndividual> {

		public FreshClassTypeNode(ElkClass elkClass)
				throws InconsistentOntologyException {
			super(elkClass);
		}

		@Override
		public Set<? extends InstanceNode<ElkClass, ElkNamedIndividual>> getDirectInstanceNodes() {
			Set<? extends InstanceNode<ElkClass, ElkNamedIndividual>> result = Collections
					.emptySet();
			return Collections.unmodifiableSet(result);
		}

		@Override
		public Set<? extends InstanceNode<ElkClass, ElkNamedIndividual>> getAllInstanceNodes() {
			return getDirectInstanceNodes();
		}

		@Override
		public Set<TypeNode<ElkClass, ElkNamedIndividual>> getDirectSuperNodes() {
			return Collections.unmodifiableSet(Collections.singleton(topNode));
		}

		@Override
		public Set<TypeNode<ElkClass, ElkNamedIndividual>> getAllSuperNodes() {
			return getDirectSuperNodes();
		}

		@Override
		public Set<TypeNode<ElkClass, ElkNamedIndividual>> getDirectSubNodes() {
			return Collections.unmodifiableSet(Collections
					.singleton(bottomNode));
		}

		@Override
		public Set<TypeNode<ElkClass, ElkNamedIndividual>> getAllSubNodes() {
			return getDirectSubNodes();
		}
	}

	/**
	 * Class for representing instance nodes for fresh named individuals.
	 * 
	 * @author Markus Kroetzsch
	 */
	protected class FreshClassInstanceNode extends
			FreshTaxonomyNode<ElkNamedIndividual> implements
			InstanceNode<ElkClass, ElkNamedIndividual> {

		public FreshClassInstanceNode(ElkNamedIndividual elkClass)
				throws InconsistentOntologyException {
			super(elkClass);
		}

		@Override
		public Set<? extends TypeNode<ElkClass, ElkNamedIndividual>> getDirectTypeNodes() {
			return Collections.unmodifiableSet(Collections.singleton(topNode));
		}

		@Override
		public Set<? extends TypeNode<ElkClass, ElkNamedIndividual>> getAllTypeNodes() {
			return getDirectTypeNodes();
		}
	}

}

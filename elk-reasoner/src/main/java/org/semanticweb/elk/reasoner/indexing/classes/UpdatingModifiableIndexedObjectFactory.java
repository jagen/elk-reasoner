package org.semanticweb.elk.reasoner.indexing.classes;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDifferentIndividualsAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointUnionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkClassAssertionAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkDeclarationAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkDifferentIndividualsAxiomBinaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkDifferentIndividualsAxiomNaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkDisjointClassesAxiomBinaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkDisjointClassesAxiomNaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkDisjointUnionAxiomBinaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkDisjointUnionAxiomDefinitionConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkDisjointUnionAxiomNaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkDisjointUnionAxiomSubClassConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkEquivalentClassesAxiomDefinitionConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkEquivalentClassesAxiomSubClassConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkEquivalentObjectPropertiesAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkObjectPropertyAssertionAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkObjectPropertyDomainAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkObjectPropertyRangeAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkReflexiveObjectPropertyAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkSameIndividualAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkSubClassOfAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkSubObjectPropertyOfAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkTransitiveObjectPropertyAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedEntity;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;

/**
 * A {@link ModifiableIndexedObject.Factory} that constructs objects using
 * another {@link ModifiableIndexedObject.Factory} and updates the occurrence
 * counts for the constructed objects using the provided
 * {@link OccurrenceIncrement}.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public class UpdatingModifiableIndexedObjectFactory
		extends
			UpdatingCachedIndexedObjectFactory
		implements
			ModifiableIndexedObject.Factory {

	private final ModifiableIndexedObject.Factory baseFactory_;

	public <F extends CachedIndexedObject.Factory & ModifiableIndexedObject.Factory> UpdatingModifiableIndexedObjectFactory(
			F baseFactory, ModifiableOntologyIndex index,
			OccurrenceIncrement increment) {
		super(baseFactory, index, increment);
		this.baseFactory_ = baseFactory;
	}

	@Override
	public ModifiableElkClassAssertionAxiomConversion getElkClassAssertionAxiomConversion(
			ElkClassAssertionAxiom originalAxiom,
			ModifiableIndexedIndividual instance,
			ModifiableIndexedClassExpression type) {
		return update(baseFactory_.getElkClassAssertionAxiomConversion(
				originalAxiom, instance, type));
	}

	@Override
	public ModifiableElkDeclarationAxiomConversion getElkDeclarationAxiomConversion(
			ElkDeclarationAxiom originalAxiom, ModifiableIndexedEntity entity) {
		return update(baseFactory_
				.getElkDeclarationAxiomConversion(originalAxiom, entity));
	}

	@Override
	public ModifiableElkDifferentIndividualsAxiomBinaryConversion getElkDifferentIndividualsAxiomBinaryConversion(
			ElkDifferentIndividualsAxiom originalAxiom,
			int firstIndividualPosition, int secondIndividualPosition,
			ModifiableIndexedObjectIntersectionOf conjunction,
			ModifiableIndexedClass bottom) {
		return update(
				baseFactory_.getElkDifferentIndividualsAxiomBinaryConversion(
						originalAxiom, firstIndividualPosition,
						secondIndividualPosition, conjunction, bottom));
	}

	@Override
	public ModifiableElkDifferentIndividualsAxiomNaryConversion getElkDifferentIndividualsAxiomNaryConversion(
			ElkDifferentIndividualsAxiom originalAxiom,
			ModifiableIndexedClassExpressionList differentIndividuals) {
		return update(
				baseFactory_.getElkDifferentIndividualsAxiomNaryConversion(
						originalAxiom, differentIndividuals));
	}

	@Override
	public ModifiableElkDisjointClassesAxiomBinaryConversion getElkDisjointClassesAxiomBinaryConversion(
			ElkDisjointClassesAxiom originalAxiom, int firstClassPosition,
			int secondClassPosition,
			ModifiableIndexedObjectIntersectionOf conjunction,
			ModifiableIndexedClass bottom) {
		return update(baseFactory_.getElkDisjointClassesAxiomBinaryConversion(
				originalAxiom, firstClassPosition, secondClassPosition,
				conjunction, bottom));
	}

	@Override
	public ModifiableElkDisjointClassesAxiomNaryConversion getElkDisjointClassesAxiomNaryConversion(
			ElkDisjointClassesAxiom originalAxiom,
			ModifiableIndexedClassExpressionList disjointClasses) {
		return update(baseFactory_.getElkDisjointClassesAxiomNaryConversion(
				originalAxiom, disjointClasses));
	}

	@Override
	public ModifiableElkDisjointUnionAxiomBinaryConversion getElkDisjointUnionAxiomBinaryConversion(
			ElkDisjointUnionAxiom originalAxiom, int firstDisjunctPosition,
			int secondDisjunctPosition,
			ModifiableIndexedObjectIntersectionOf conjunction,
			ModifiableIndexedClass bottom) {
		return update(baseFactory_.getElkDisjointUnionAxiomBinaryConversion(
				originalAxiom, firstDisjunctPosition, secondDisjunctPosition,
				conjunction, bottom));
	}

	@Override
	public ModifiableElkDisjointUnionAxiomDefinitionConversion getElkDisjointUnionAxiomDefinitionConversion(
			ElkDisjointUnionAxiom originalAxiom,
			ModifiableIndexedClass definedClass,
			ModifiableIndexedClassExpression definition) {
		return update(baseFactory_.getElkDisjointUnionAxiomDefinitionConversion(
				originalAxiom, definedClass, definition));
	}

	@Override
	public ModifiableElkDisjointUnionAxiomNaryConversion getElkDisjointUnionAxiomNaryConversion(
			ElkDisjointUnionAxiom originalAxiom,
			ModifiableIndexedClassExpressionList disjointClasses) {
		return update(baseFactory_.getElkDisjointUnionAxiomNaryConversion(
				originalAxiom, disjointClasses));
	}

	@Override
	public ModifiableElkDisjointUnionAxiomSubClassConversion getElkDisjointUnionAxiomSubClassConversion(
			ElkDisjointUnionAxiom originalAxiom, int disjunctPosition,
			ModifiableIndexedClassExpression disjunct,
			ModifiableIndexedClass definedClass) {
		return update(baseFactory_.getElkDisjointUnionAxiomSubClassConversion(
				originalAxiom, disjunctPosition, disjunct, definedClass));
	}

	@Override
	public ModifiableElkEquivalentClassesAxiomDefinitionConversion getElkEquivalentClassesAxiomDefinitionConversion(
			ElkEquivalentClassesAxiom originalAxiom, int definedClassPosition,
			int definitionPosition, ModifiableIndexedClass definedClass,
			ModifiableIndexedClassExpression definition) {
		return update(
				baseFactory_.getElkEquivalentClassesAxiomDefinitionConversion(
						originalAxiom, definedClassPosition, definitionPosition,
						definedClass, definition));
	}

	@Override
	public ModifiableElkEquivalentClassesAxiomSubClassConversion getElkEquivalentClassesAxiomSubClassConversion(
			ElkEquivalentClassesAxiom originalAxiom, int subClassPosition,
			int superClassPosition, ModifiableIndexedClassExpression subClass,
			ModifiableIndexedClassExpression superClass) {
		return update(
				baseFactory_.getElkEquivalentClassesAxiomSubClassConversion(
						originalAxiom, subClassPosition, superClassPosition,
						subClass, superClass));
	}

	@Override
	public ModifiableElkEquivalentObjectPropertiesAxiomConversion getElkEquivalentObjectPropertiesAxiomConversion(
			ElkEquivalentObjectPropertiesAxiom originalAxiom,
			int subPropertyPosition, int superPropertyPosition,
			ModifiableIndexedObjectProperty subProperty,
			ModifiableIndexedObjectProperty superProperty) {
		return update(
				baseFactory_.getElkEquivalentObjectPropertiesAxiomConversion(
						originalAxiom, subPropertyPosition,
						superPropertyPosition, subProperty, superProperty));
	}

	@Override
	public ModifiableElkObjectPropertyAssertionAxiomConversion getElkObjectPropertyAssertionAxiomConversion(
			ElkObjectPropertyAssertionAxiom originalAxiom,
			ModifiableIndexedClassExpression subClass,
			ModifiableIndexedClassExpression superClass) {
		return update(baseFactory_.getElkObjectPropertyAssertionAxiomConversion(
				originalAxiom, subClass, superClass));
	}

	@Override
	public ModifiableElkObjectPropertyDomainAxiomConversion getElkObjectPropertyDomainAxiomConversion(
			ElkObjectPropertyDomainAxiom originalAxiom,
			ModifiableIndexedClassExpression subClass,
			ModifiableIndexedClassExpression superClass) {
		return update(baseFactory_.getElkObjectPropertyDomainAxiomConversion(
				originalAxiom, subClass, superClass));
	}

	@Override
	public ModifiableElkObjectPropertyRangeAxiomConversion getElkObjectPropertyRangeAxiomConversion(
			ElkObjectPropertyRangeAxiom originalAxiom,
			ModifiableIndexedObjectProperty property,
			ModifiableIndexedClassExpression range) {
		return update(baseFactory_.getElkObjectPropertyRangeAxiomConversion(
				originalAxiom, property, range));
	}

	@Override
	public ModifiableElkReflexiveObjectPropertyAxiomConversion getElkReflexiveObjectPropertyAxiomConversion(
			ElkReflexiveObjectPropertyAxiom originalAxiom,
			ModifiableIndexedClassExpression subClass,
			ModifiableIndexedClassExpression superClass) {
		return update(baseFactory_.getElkReflexiveObjectPropertyAxiomConversion(
				originalAxiom, subClass, superClass));
	}

	@Override
	public ModifiableElkSameIndividualAxiomConversion getElkSameIndividualAxiomConversion(
			ElkSameIndividualAxiom originalAxiom, int subIndividualPosition,
			int superIndividualPosition,
			ModifiableIndexedIndividual subIndividual,
			ModifiableIndexedIndividual superIndividual) {
		return update(baseFactory_.getElkSameIndividualAxiomConversion(
				originalAxiom, subIndividualPosition, superIndividualPosition,
				subIndividual, superIndividual));
	}

	@Override
	public ModifiableElkSubClassOfAxiomConversion getElkSubClassOfAxiomConversion(
			ElkSubClassOfAxiom originalAxiom,
			ModifiableIndexedClassExpression subClass,
			ModifiableIndexedClassExpression superClass) {
		return update(baseFactory_.getElkSubClassOfAxiomConversion(
				originalAxiom, subClass, superClass));
	}

	@Override
	public ModifiableElkSubObjectPropertyOfAxiomConversion getElkSubObjectPropertyOfAxiomConversion(
			ElkSubObjectPropertyOfAxiom originalAxiom,
			ModifiableIndexedPropertyChain subPropertyChain,
			ModifiableIndexedObjectProperty superProperty) {
		return update(baseFactory_.getElkSubObjectPropertyOfAxiomConversion(
				originalAxiom, subPropertyChain, superProperty));
	}

	@Override
	public ModifiableElkTransitiveObjectPropertyAxiomConversion getElkTransitiveObjectPropertyAxiomConversion(
			ElkTransitiveObjectPropertyAxiom originalAxiom,
			ModifiableIndexedPropertyChain subPropertyChain,
			ModifiableIndexedObjectProperty superProperty) {
		return update(
				baseFactory_.getElkTransitiveObjectPropertyAxiomConversion(
						originalAxiom, subPropertyChain, superProperty));
	}

}

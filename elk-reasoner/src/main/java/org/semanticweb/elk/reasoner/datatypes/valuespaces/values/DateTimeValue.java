/*
 * #%L
 * ELK Reasoner
 * *
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.datatypes.valuespaces.values;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;
import org.semanticweb.elk.owl.interfaces.ElkDatatype.ELDatatype;
import org.semanticweb.elk.reasoner.datatypes.index.ValueSpaceVisitor;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Value space that represent single dateTime value.
 *
 * @author Pospishnyi Olexandr
 */
public class DateTimeValue implements ValueSpace {

	public XMLGregorianCalendar value;
	public ELDatatype datatype;
	public ELDatatype effectiveDatatype;

	public DateTimeValue(XMLGregorianCalendar value, ELDatatype datatype) {
		this.value = value;
		this.datatype = datatype;
		if (value.getTimezone() != DatatypeConstants.FIELD_UNDEFINED) {
			effectiveDatatype = ELDatatype.xsd_dateTimeStamp;
		} else {
			effectiveDatatype = ELDatatype.xsd_dateTime;
		}
	}

	@Override
	public ELDatatype getDatatype() {
		return effectiveDatatype;
	}

	@Override
	public ValueSpaceType getType() {
		return ValueSpaceType.DATETIME_VALUE;
	}

	@Override
	public boolean isEmptyInterval() {
		return value != null;
	}

	/**
	 * DateTimeValue could contain only another DateTimeValue if both are equal.
	 * Note that according to XML Schema, two dateTime values representing the same
	 * time instant but with different time zone offsets are equal, but not
	 * identical.
	 *
	 * @param valueSpace
	 * @return true if this value space contains {@code valueSpace}
	 */
	@Override
	public boolean contains(ValueSpace valueSpace) {
		switch (valueSpace.getType()) {
			case DATETIME_VALUE:
				DateTimeValue vs = (DateTimeValue) valueSpace;
				return this.value.compare(vs.value) == 0
						&& this.value.getTimezone() == vs.value.getTimezone();
			default:
				return false;
		}
	}

	@Override
	public boolean isSubsumedBy(ValueSpace valueSpace) {
		return valueSpace.contains(this);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof DateTimeValue) {
			DateTimeValue otherEntry = (DateTimeValue) other;
			return this.datatype.equals(otherEntry.datatype)
				&& this.value.equals(otherEntry.value)
				&& this.value.getTimezone() == otherEntry.value.getTimezone();

		}
		return false;
	}

	@Override
	public int hashCode() {
		return HashGenerator.combinedHashCode(
			DateTimeValue.class,
			this.datatype,
			this.value,
			this.value.getTimezone()
			);
	}

	@Override
	public String toString() {
		return value.toString() + "^^" + datatype;
	}
	
	@Override
	public <O> O accept(ValueSpaceVisitor<O> visitor) {
		return visitor.visit(this);
	}
}
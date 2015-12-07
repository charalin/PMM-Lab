/*******************************************************************************
 * Copyright (c) 2015 Federal Institute for Risk Assessment (BfR), Germany
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     Department Biological Safety - BfR
 *******************************************************************************/
package de.bund.bfr.knime.pmm.common.generictablemodel;

import java.util.Enumeration;
import java.util.Iterator;

import org.knime.core.data.DataRow;
import org.knime.core.data.DataTable;
import org.knime.core.data.DataTableSpec;

import de.bund.bfr.knime.pmm.common.PmmException;

public class KnimeRelationReader implements Enumeration<KnimeTuple> {
	
	Iterator<DataRow> rowIterator;
	KnimeSchema schema;
	DataTableSpec spec;
	
	public KnimeRelationReader( KnimeSchema schema, DataTable buffer ) throws PmmException {
				
		if( !schema.conforms( buffer ) )
			throw new PmmException( "Mapping of buffer on schema impossible." );
		
		this.schema = schema;
		rowIterator = buffer.iterator();
		spec = buffer.getDataTableSpec();
	}

	@Override
	public boolean hasMoreElements() {
		return rowIterator.hasNext();
	}

	@Override
	public KnimeTuple nextElement() {
		try {
			return new KnimeTuple( schema, spec, rowIterator.next() );
		} catch ( PmmException ex ) {
			ex.printStackTrace();
			return null;
		}
	}

}

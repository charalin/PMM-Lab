/*******************************************************************************
 * Copyright (C) 2012 Data In Motion
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
 ******************************************************************************/
/**
 * Project: de.dim.bfr.ui
 * $HeadURL: http://data-in-motion.biz/statup/trunk/de.dim.bfr.ui/src/de/dim/bfr/ui/databinding/converter/BooleanNotNullConverter.java $
 * $LastChangedDate: 2012-01-24 11:03:21 +0100 (Di, 24 Jan 2012) $
 * $lastChangedBy$
 * $Revision: 652 $
 * (c) Data in Motion 2011
 */
package de.dim.bfr.ui.databinding.converter;

import org.eclipse.core.databinding.conversion.IConverter;

/**
 * Databinding converter that negates a boolean value
 * @author Mark Hoffmann
 * @since 20.11.2010
 */
public class BooleanNotNullConverter implements IConverter {

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.core.databinding.conversion.IConverter#getFromType()
	 */
	@Override
	public Object getFromType() {
		return Object.class;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.core.databinding.conversion.IConverter#getToType()
	 */
	@Override
	public Object getToType() {
		return Boolean.TYPE;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.core.databinding.conversion.IConverter#convert(java.lang.Object)
	 */
	@Override
	public Object convert(Object fromObject) {
		return fromObject != null;
	}

}

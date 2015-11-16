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
package de.bund.bfr.pmf.sbml;

import org.sbml.jsbml.xml.XMLAttributes;
import org.sbml.jsbml.xml.XMLNode;
import org.sbml.jsbml.xml.XMLTriple;

/**
 * @author Miguel Alba
 */
public class DataSourceNode {
	
	public static final String TAG = "dataSource";
	public static final String NS = "pmmlab";
	
	XMLNode node;
	
	public DataSourceNode(final XMLNode node) {
		this.node = node;
	}
	
	public DataSourceNode(final String dataName) {
		XMLTriple triple = new XMLTriple(TAG, null, NS);

		XMLAttributes attrs = new XMLAttributes();
		attrs.add("id", "source1");
		attrs.add("href", dataName);
		
		node = new XMLNode(triple, attrs);
	}
	
	public String getFile() { return node.getAttrValue("href"); }
	public XMLNode getNode() { return node; }
}

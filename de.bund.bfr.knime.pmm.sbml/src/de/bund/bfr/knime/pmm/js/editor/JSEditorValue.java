/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 * 
 * History
 *   30.04.2014 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package de.bund.bfr.knime.pmm.js.editor;

import javax.json.JsonException;
import javax.json.JsonValue;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.dialog.DialogNodeValue;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of
 *         Konstanz
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class JSEditorValue extends JSONViewContent implements DialogNodeValue {

	private static final String CFG_MODELS = "model";
	private static final String DEFAULT_MODELS = "";
	private String m_models = DEFAULT_MODELS;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveToNodeSettings(final NodeSettingsWO settings) {
		settings.addString(CFG_MODELS, m_models);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void loadFromNodeSettings(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		m_models = settings.getString(CFG_MODELS);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@JsonIgnore
	public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
		m_models = settings.getString(CFG_MODELS, DEFAULT_MODELS);
	}

	public void validateSettings(NodeSettingsRO settings)
			throws InvalidSettingsException {
		// TODO Auto-generated method stub
	}

	/**
	 * @return the models
	 */
	@JsonProperty("String")
	public String getModels() {
		return m_models;
	}

	/**
	 * @param models
	 *            the models to set
	 */
	@JsonProperty("String")
	public void setModels(final String models) {
		m_models = models;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("models=");
		sb.append(m_models);
		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(m_models).toHashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		JSEditorValue other = (JSEditorValue) obj;
		return new EqualsBuilder().append(m_models, other.m_models).isEquals();
	}

	@Override
	public void loadFromString(String fromCmdLine)
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadFromJson(JsonValue json) throws JsonException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public JsonValue toJson() {
		// TODO Auto-generated method stub
		return null;
	}
}
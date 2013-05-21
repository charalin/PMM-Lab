/*******************************************************************************
 * PMM-Lab � 2012, Federal Institute for Risk Assessment (BfR), Germany
 * 
 * PMM-Lab is a set of KNIME-Nodes and KNIME workflows running within the KNIME software plattform (http://www.knime.org.).
 * 
 * PMM-Lab � 2012, Federal Institute for Risk Assessment (BfR), Germany
 * Contact: armin.weiser@bfr.bund.de or matthias.filter@bfr.bund.de 
 * 
 * Developers and contributors to the PMM-Lab project are 
 * J�rgen Brandt (BfR)
 * Armin A. Weiser (BfR)
 * Matthias Filter (BfR)
 * Alexander Falenski (BfR)
 * Christian Th�ns (BfR)
 * Annemarie K�sbohrer (BfR)
 * Bernd Appel (BfR)
 * 
 * PMM-Lab is a project under development. Contributions are welcome.
 * 
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
package de.bund.bfr.knime.pmm.xlsmodelreader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import de.bund.bfr.knime.pmm.common.AgentXml;
import de.bund.bfr.knime.pmm.common.CatalogModelXml;
import de.bund.bfr.knime.pmm.common.DepXml;
import de.bund.bfr.knime.pmm.common.IndepXml;
import de.bund.bfr.knime.pmm.common.LiteratureItem;
import de.bund.bfr.knime.pmm.common.MatrixXml;
import de.bund.bfr.knime.pmm.common.PmmXmlDoc;
import de.bund.bfr.knime.pmm.common.XLSReader;
import de.bund.bfr.knime.pmm.common.XmlConverter;
import de.bund.bfr.knime.pmm.common.generictablemodel.KnimeTuple;
import de.bund.bfr.knime.pmm.common.math.MathUtilities;
import de.bund.bfr.knime.pmm.common.pmmtablemodel.AttributeUtilities;
import de.bund.bfr.knime.pmm.common.pmmtablemodel.Model1Schema;
import de.bund.bfr.knime.pmm.common.pmmtablemodel.SchemaFactory;
import de.bund.bfr.knime.pmm.common.pmmtablemodel.TimeSeriesSchema;

/**
 * This is the model implementation of XLSModelReader.
 * 
 * 
 * @author Christian Thoens
 */
public class XLSModelReaderNodeModel extends NodeModel {

	protected static final String CFGKEY_FILENAME = "FileName";
	protected static final String CFGKEY_SHEETNAME = "SheetName";
	protected static final String CFGKEY_MODELMAPPINGS = "ModelMappings";
	protected static final String CFGKEY_COLUMNMAPPINGS = "ColumnMappings";
	protected static final String CFGKEY_AGENTCOLUMN = "AgentColumn";
	protected static final String CFGKEY_AGENTMAPPINGS = "AgentMappings";
	protected static final String CFGKEY_MATRIXCOLUMN = "MatrixColumn";
	protected static final String CFGKEY_MATRIXMAPPINGS = "MatrixMappings";
	protected static final String CFGKEY_MODELTUPLE = "ModelTuple";
	protected static final String CFGKEY_AGENT = "Agent";
	protected static final String CFGKEY_MATRIX = "Matrix";
	protected static final String CFGKEY_LITERATURE = "Literature";

	private String fileName;
	private String sheetName;
	private Map<String, String> modelMappings;
	private Map<String, Object> columnMappings;
	private String agentColumn;
	private Map<String, AgentXml> agentMappings;
	private String matrixColumn;
	private Map<String, MatrixXml> matrixMappings;
	private KnimeTuple modelTuple;
	private AgentXml agent;
	private MatrixXml matrix;
	private List<LiteratureItem> literature;

	/**
	 * Constructor for the node model.
	 */
	protected XLSModelReaderNodeModel() {
		super(0, 1);
		fileName = null;
		sheetName = null;
		modelTuple = null;
		modelMappings = new LinkedHashMap<>();
		columnMappings = new LinkedHashMap<>();
		agentColumn = null;
		agentMappings = new LinkedHashMap<>();
		matrixColumn = null;
		matrixMappings = new LinkedHashMap<>();
		agent = null;
		matrix = null;
		literature = new ArrayList<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
			final ExecutionContext exec) throws Exception {
		PmmXmlDoc modelXml = modelTuple
				.getPmmXml(Model1Schema.ATT_MODELCATALOG);
		String formula = ((CatalogModelXml) modelXml.get(0)).getFormula();
		PmmXmlDoc depVar = modelTuple.getPmmXml(Model1Schema.ATT_DEPENDENT);
		PmmXmlDoc indepVar = modelTuple.getPmmXml(Model1Schema.ATT_INDEPENDENT);

		if (depVar.size() == 1) {
			formula = MathUtilities
					.replaceVariable(formula,
							((DepXml) depVar.get(0)).getName(),
							AttributeUtilities.LOGC);
			((DepXml) depVar.get(0)).setName(AttributeUtilities.LOGC);
		}

		if (indepVar.size() == 1) {
			formula = MathUtilities.replaceVariable(formula,
					((IndepXml) indepVar.get(0)).getName(),
					AttributeUtilities.TIME);
			((IndepXml) indepVar.get(0)).setName(AttributeUtilities.TIME);
		}

		((CatalogModelXml) modelXml.get(0)).setFormula(formula);
		modelTuple.setValue(Model1Schema.ATT_MODELCATALOG, modelXml);
		modelTuple.setValue(Model1Schema.ATT_DEPENDENT, depVar);
		modelTuple.setValue(Model1Schema.ATT_INDEPENDENT, indepVar);

		XLSReader xlsReader = new XLSReader();
		List<KnimeTuple> tuples = new ArrayList<KnimeTuple>(
				xlsReader
						.getPrimaryModelTuples(new File(fileName), sheetName,
								columnMappings, agentColumn, agentMappings,
								matrixColumn, matrixMappings, modelTuple,
								modelMappings).values());

		for (String warning : xlsReader.getWarnings()) {
			setWarningMessage(warning);
		}

		if (agentColumn == null && agent != null) {
			for (KnimeTuple tuple : tuples) {
				PmmXmlDoc agentXml = tuple
						.getPmmXml(TimeSeriesSchema.ATT_AGENT);

				((AgentXml) agentXml.get(0)).setID(agent.getID());
				((AgentXml) agentXml.get(0)).setName(agent.getName());
				((AgentXml) agentXml.get(0)).setDbuuid(agent.getDbuuid());
				tuple.setValue(TimeSeriesSchema.ATT_AGENT, agentXml);
			}
		}

		if (matrixColumn == null && matrix != null) {
			for (KnimeTuple tuple : tuples) {
				PmmXmlDoc matrixXml = tuple
						.getPmmXml(TimeSeriesSchema.ATT_MATRIX);

				((MatrixXml) matrixXml.get(0)).setID(matrix.getID());
				((MatrixXml) matrixXml.get(0)).setName(matrix.getName());
				((MatrixXml) matrixXml.get(0)).setDbuuid(matrix.getDbuuid());
				tuple.setValue(TimeSeriesSchema.ATT_MATRIX, matrixXml);
			}
		}

		PmmXmlDoc literatureXML = new PmmXmlDoc();

		for (LiteratureItem item : literature) {
			literatureXML.add(item);
		}

		for (KnimeTuple tuple : tuples) {
			tuple.setValue(Model1Schema.ATT_EMLIT, literatureXML);
		}

		BufferedDataContainer container = exec
				.createDataContainer(SchemaFactory.createM1DataSchema()
						.createSpec());

		for (KnimeTuple tuple : tuples) {
			container.addRowToTable(tuple);
		}

		container.close();

		return new BufferedDataTable[] { container.getTable() };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
			throws InvalidSettingsException {
		if (fileName == null) {
			throw new InvalidSettingsException("");
		}

		return new DataTableSpec[] { SchemaFactory.createM1DataSchema()
				.createSpec() };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		settings.addString(CFGKEY_FILENAME, fileName);
		settings.addString(CFGKEY_SHEETNAME, sheetName);
		settings.addString(CFGKEY_MODELTUPLE,
				XmlConverter.tupleToXml(modelTuple));
		settings.addString(CFGKEY_MODELMAPPINGS,
				XmlConverter.objectToXml(modelMappings));
		settings.addString(CFGKEY_COLUMNMAPPINGS,
				XmlConverter.objectToXml(columnMappings));
		settings.addString(CFGKEY_AGENTCOLUMN, agentColumn);
		settings.addString(CFGKEY_AGENTMAPPINGS,
				XmlConverter.objectToXml(agentMappings));
		settings.addString(CFGKEY_MATRIXCOLUMN, matrixColumn);
		settings.addString(CFGKEY_MATRIXMAPPINGS,
				XmlConverter.objectToXml(matrixMappings));
		settings.addString(CFGKEY_AGENT, XmlConverter.objectToXml(agent));
		settings.addString(CFGKEY_MATRIX, XmlConverter.objectToXml(matrix));
		settings.addString(CFGKEY_LITERATURE,
				XmlConverter.objectToXml(literature));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		fileName = settings.getString(CFGKEY_FILENAME);
		sheetName = settings.getString(CFGKEY_SHEETNAME);
		modelTuple = XmlConverter.xmlToTuple(settings
				.getString(CFGKEY_MODELTUPLE));
		modelMappings = XmlConverter.xmlToObject(
				settings.getString(CFGKEY_MODELMAPPINGS),
				new LinkedHashMap<String, String>());
		columnMappings = XmlConverter.xmlToObject(
				settings.getString(CFGKEY_COLUMNMAPPINGS),
				new LinkedHashMap<String, Object>());
		agentColumn = settings.getString(CFGKEY_AGENTCOLUMN);
		agentMappings = XmlConverter.xmlToObject(
				settings.getString(CFGKEY_AGENTMAPPINGS),
				new LinkedHashMap<String, AgentXml>());
		matrixColumn = settings.getString(CFGKEY_MATRIXCOLUMN);
		matrixMappings = XmlConverter.xmlToObject(
				settings.getString(CFGKEY_MATRIXMAPPINGS),
				new LinkedHashMap<String, MatrixXml>());
		agent = XmlConverter
				.xmlToObject(settings.getString(CFGKEY_AGENT), null);
		matrix = XmlConverter.xmlToObject(settings.getString(CFGKEY_MATRIX),
				null);
		literature = XmlConverter.xmlToObject(
				settings.getString(CFGKEY_LITERATURE),
				new ArrayList<LiteratureItem>());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings)
			throws InvalidSettingsException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
	}

}

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
package de.bund.bfr.knime.pmm.combaseio;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import de.bund.bfr.knime.pmm.combaseio.lib.CombaseReader;
import de.bund.bfr.knime.pmm.common.CatalogModelXml;
import de.bund.bfr.knime.pmm.common.DepXml;
import de.bund.bfr.knime.pmm.common.EstModelXml;
import de.bund.bfr.knime.pmm.common.IndepXml;
import de.bund.bfr.knime.pmm.common.MdInfoXml;
import de.bund.bfr.knime.pmm.common.ParamXml;
import de.bund.bfr.knime.pmm.common.PmmException;
import de.bund.bfr.knime.pmm.common.PmmTimeSeries;
import de.bund.bfr.knime.pmm.common.PmmXmlDoc;
import de.bund.bfr.knime.pmm.common.generictablemodel.KnimeSchema;
import de.bund.bfr.knime.pmm.common.generictablemodel.KnimeTuple;
import de.bund.bfr.knime.pmm.common.math.MathUtilities;
import de.bund.bfr.knime.pmm.common.pmmtablemodel.AttributeUtilities;
import de.bund.bfr.knime.pmm.common.pmmtablemodel.Model1Schema;
import de.bund.bfr.knime.pmm.common.pmmtablemodel.TimeSeriesSchema;
import de.bund.bfr.knime.pmm.common.units.Categories;

/**
 * This is the model implementation of CombaseReader.
 * 
 * 
 * @author Jorgen Brandt
 */
public class CombaseReaderNodeModel extends NodeModel {

	protected static final String PARAM_FILENAME = "filename";
	protected static final String PARAM_USESTARTVALUE = "useStartValue";
	protected static final String PARAM_STARTELIM = "startElim";
	protected static final String PARAM_STARTGROW = "startGrow";
	private static final String COMMENT_CLAUSE = "LogC0 artificially generated by Combase Reader.";

	protected static final String DEFAULT_FILENAME = "";
	protected static final int DEFAULT_USESTARTVALUE = 1;
	protected static final double DEFAULT_STARTELIM = 10.0;
	protected static final double DEFAULT_STARTGROW = 0.0;

	private String filename;
	private int useStartValue;
	private double startElim;
	private double startGrow;

	/**
	 * Constructor for the node model.
	 */
	protected CombaseReaderNodeModel() {
		super(0, 2);
		filename = DEFAULT_FILENAME;
		useStartValue = DEFAULT_USESTARTVALUE;
		startElim = DEFAULT_STARTELIM;
		startGrow = DEFAULT_STARTGROW;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
			throws Exception {
		KnimeSchema commonSchema = KnimeSchema.merge(new TimeSeriesSchema(), new Model1Schema());
		BufferedDataContainer buf = exec.createDataContainer(new TimeSeriesSchema().createSpec());
		BufferedDataContainer buf2 = exec.createDataContainer(commonSchema.createSpec());
		PmmXmlDoc dValue = new PmmXmlDoc(new CatalogModelXml(1000000, "D-Value",
				AttributeUtilities.CONCENTRATION + "=LogC0-1/Dvalue*" + AttributeUtilities.TIME, null));
		PmmXmlDoc linear = new PmmXmlDoc(new CatalogModelXml(1000001, "LogLinear",
				AttributeUtilities.CONCENTRATION + "=LogC0+mumax*" + AttributeUtilities.TIME, null));
		int j = 0;

		for (PmmTimeSeries timeSeries : new CombaseReader(filename).getResult()) {
			if (timeSeries.isEmpty()) {
				if (Double.isNaN(timeSeries.getMaximumRate()) || Double.isInfinite(timeSeries.getMaximumRate())) {
					continue;
				}

				KnimeTuple modelTuple = KnimeTuple.merge(commonSchema, new KnimeTuple(commonSchema), timeSeries);
				PmmXmlDoc paramDoc = new PmmXmlDoc();

				Double start;

				if (timeSeries.getMaximumRate() >= 0) {
					start = useStartValue == 1 ? startGrow : null;
					modelTuple.setValue(Model1Schema.ATT_MODELCATALOG, linear);
					paramDoc.add(new ParamXml("LogC0", true, start, null, null, null, null, null));
					paramDoc.add(new ParamXml("mumax", false, timeSeries.getMaximumRate(), null, null, null, null, null));
				} else {
					start = useStartValue == 1 ? startElim : null;
					modelTuple.setValue(Model1Schema.ATT_MODELCATALOG, dValue);
					paramDoc.add(new ParamXml("LogC0", true, start, null, null, null, null, null));
					paramDoc.add(
							new ParamXml("Dvalue", false, -1.0 / timeSeries.getMaximumRate(), null, null, null, null, null));
				}

				modelTuple.setValue(Model1Schema.ATT_PARAMETER, paramDoc);

				PmmXmlDoc depXml = new PmmXmlDoc();
				PmmXmlDoc indepXML = new PmmXmlDoc();

				depXml.add(new DepXml(AttributeUtilities.CONCENTRATION, Categories.getConcentrations().get(0),
						Categories.getConcentrationCategories().get(0).getStandardUnit()));
				indepXML.add(new IndepXml(AttributeUtilities.TIME, null, null, Categories.getTime(),
						Categories.getTimeCategory().getStandardUnit()));

				modelTuple.setValue(Model1Schema.ATT_DEPENDENT, depXml);
				modelTuple.setValue(Model1Schema.ATT_INDEPENDENT, indepXML);

				PmmXmlDoc emDoc = new PmmXmlDoc();
				PmmXmlDoc mdInfoDoc = new PmmXmlDoc();
				int estModelID = MathUtilities.getRandomNegativeInt();
				int dataID = MathUtilities.getRandomNegativeInt();

				emDoc.add(new EstModelXml(estModelID, "EM_" + estModelID, null, null, null, null, null, null));
				mdInfoDoc.add(new MdInfoXml(dataID, "i" + dataID, COMMENT_CLAUSE, null, null));

				modelTuple.setValue(Model1Schema.ATT_ESTMODEL, emDoc);
				modelTuple.setValue(TimeSeriesSchema.ATT_MDINFO, mdInfoDoc);
				modelTuple.setValue(Model1Schema.ATT_DATABASEWRITABLE, Model1Schema.WRITABLE);

				buf2.addRowToTable(new DefaultRow(String.valueOf(j++), modelTuple));
			} else {
				buf.addRowToTable(new DefaultRow(String.valueOf(j++), timeSeries));
			}
		}

		buf.close();
		buf2.close();

		return new BufferedDataTable[] { buf.getTable(), buf2.getTable() };
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
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {

		DataTableSpec[] ret;

		ret = null;

		if (filename.isEmpty())
			throw new InvalidSettingsException("Filename must be specified.");

		try {
			ret = new DataTableSpec[] { new TimeSeriesSchema().createSpec(),
					KnimeSchema.merge(new TimeSeriesSchema(), new Model1Schema()).createSpec() };
		} catch (PmmException e) {
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		settings.addString(PARAM_FILENAME, filename);
		settings.addInt(PARAM_USESTARTVALUE, useStartValue);
		settings.addDouble(PARAM_STARTELIM, startElim);
		settings.addDouble(PARAM_STARTGROW, startGrow);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
		filename = settings.getString(PARAM_FILENAME);
		useStartValue = settings.getInt(PARAM_USESTARTVALUE);
		startElim = settings.getDouble(PARAM_STARTELIM);
		startGrow = settings.getDouble(PARAM_STARTGROW);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir, final ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir, final ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
	}

	protected static DataTableSpec createXmlSpec() {

		DataColumnSpec[] spec;

		spec = new DataColumnSpec[1];
		// spec[ 0 ] = new DataColumnSpecCreator( "xmlString", XMLCell.TYPE
		// ).createSpec();
		spec[0] = new DataColumnSpecCreator("xmlString", StringCell.TYPE).createSpec();

		return new DataTableSpec(spec);
	}

}

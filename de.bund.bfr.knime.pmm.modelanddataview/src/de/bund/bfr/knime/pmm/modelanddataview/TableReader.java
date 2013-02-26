package de.bund.bfr.knime.pmm.modelanddataview;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.knime.core.data.DataTable;

import de.bund.bfr.knime.pmm.common.AgentXml;
import de.bund.bfr.knime.pmm.common.CatalogModelXml;
import de.bund.bfr.knime.pmm.common.DepXml;
import de.bund.bfr.knime.pmm.common.EstModelXml;
import de.bund.bfr.knime.pmm.common.IndepXml;
import de.bund.bfr.knime.pmm.common.MatrixXml;
import de.bund.bfr.knime.pmm.common.MdInfoXml;
import de.bund.bfr.knime.pmm.common.MiscXml;
import de.bund.bfr.knime.pmm.common.ParamXml;
import de.bund.bfr.knime.pmm.common.PmmXmlDoc;
import de.bund.bfr.knime.pmm.common.PmmXmlElementConvertable;
import de.bund.bfr.knime.pmm.common.QualityMeasurementComputation;
import de.bund.bfr.knime.pmm.common.TimeSeriesXml;
import de.bund.bfr.knime.pmm.common.chart.ChartConstants;
import de.bund.bfr.knime.pmm.common.chart.Plotable;
import de.bund.bfr.knime.pmm.common.generictablemodel.KnimeRelationReader;
import de.bund.bfr.knime.pmm.common.generictablemodel.KnimeTuple;
import de.bund.bfr.knime.pmm.common.pmmtablemodel.AttributeUtilities;
import de.bund.bfr.knime.pmm.common.pmmtablemodel.Model1Schema;
import de.bund.bfr.knime.pmm.common.pmmtablemodel.PmmUtilities;
import de.bund.bfr.knime.pmm.common.pmmtablemodel.SchemaFactory;
import de.bund.bfr.knime.pmm.common.pmmtablemodel.TimeSeriesSchema;

public class TableReader {

	private List<String> ids;
	private Map<String, Plotable> plotables;
	private List<String> stringColumns;
	private List<List<String>> stringColumnValues;
	private List<String> doubleColumns;
	private List<List<Double>> doubleColumnValues;
	private List<String> visibleColumns;
	private List<List<String>> infoParameters;
	private List<List<?>> infoParameterValues;
	private Map<String, String> shortLegend;
	private Map<String, String> longLegend;

	public TableReader(DataTable table, boolean schemaContainsData) {
		List<String> miscParams = null;
		KnimeRelationReader reader;
		List<KnimeTuple> tuples = new ArrayList<KnimeTuple>();
		List<KnimeTuple> newTuples = null;

		if (schemaContainsData) {
			reader = new KnimeRelationReader(
					SchemaFactory.createM1DataSchema(), table);
		} else {
			reader = new KnimeRelationReader(SchemaFactory.createM1Schema(),
					table);
		}

		while (reader.hasMoreElements()) {
			tuples.add(reader.nextElement());
		}

		ids = new ArrayList<String>();
		plotables = new LinkedHashMap<String, Plotable>();
		infoParameters = new ArrayList<List<String>>();
		infoParameterValues = new ArrayList<List<?>>();
		shortLegend = new LinkedHashMap<String, String>();
		longLegend = new LinkedHashMap<String, String>();

		if (schemaContainsData) {
			try {
				tuples = QualityMeasurementComputation.computePrimary(tuples,
						false);
			} catch (Exception e) {
			}

			try {
				newTuples = QualityMeasurementComputation.computePrimary(
						tuples, true);
			} catch (Exception e) {
			}

			miscParams = PmmUtilities.getAllMiscParams(table);
			stringColumns = Arrays.asList(Model1Schema.MODELNAME,
					AttributeUtilities.DATAID, ChartConstants.STATUS);
			stringColumnValues = new ArrayList<List<String>>();
			stringColumnValues.add(new ArrayList<String>());
			stringColumnValues.add(new ArrayList<String>());
			stringColumnValues.add(new ArrayList<String>());
			doubleColumns = new ArrayList<String>(Arrays.asList(
					Model1Schema.RMS, Model1Schema.RSQUARED, Model1Schema.AIC,
					Model1Schema.BIC, Model1Schema.RMS + " (Local)",
					Model1Schema.RSQUARED + " (Local)", Model1Schema.AIC
							+ " (Local)", Model1Schema.BIC + " (Local)"));
			doubleColumnValues = new ArrayList<List<Double>>();
			doubleColumnValues.add(new ArrayList<Double>());
			doubleColumnValues.add(new ArrayList<Double>());
			doubleColumnValues.add(new ArrayList<Double>());
			doubleColumnValues.add(new ArrayList<Double>());
			doubleColumnValues.add(new ArrayList<Double>());
			doubleColumnValues.add(new ArrayList<Double>());
			doubleColumnValues.add(new ArrayList<Double>());
			doubleColumnValues.add(new ArrayList<Double>());
			visibleColumns = new ArrayList<>(Arrays.asList(
					Model1Schema.MODELNAME, AttributeUtilities.DATAID));

			for (String param : miscParams) {
				doubleColumns.add(param);
				doubleColumnValues.add(new ArrayList<Double>());
				visibleColumns.add(param);
			}
		} else {
			stringColumns = Arrays.asList(Model1Schema.MODELNAME,
					ChartConstants.STATUS);
			stringColumnValues = new ArrayList<List<String>>();
			stringColumnValues.add(new ArrayList<String>());
			stringColumnValues.add(new ArrayList<String>());
			doubleColumns = Arrays.asList(Model1Schema.RMS,
					Model1Schema.RSQUARED, Model1Schema.AIC, Model1Schema.BIC);
			doubleColumnValues = new ArrayList<List<Double>>();
			doubleColumnValues.add(new ArrayList<Double>());
			doubleColumnValues.add(new ArrayList<Double>());
			doubleColumnValues.add(new ArrayList<Double>());
			doubleColumnValues.add(new ArrayList<Double>());
			visibleColumns = Arrays.asList(Model1Schema.MODELNAME);
		}

		Set<String> idSet = new LinkedHashSet<String>();

		for (int nr = 0; nr < tuples.size(); nr++) {
			KnimeTuple row = tuples.get(nr);
			Integer catID = ((CatalogModelXml) row.getPmmXml(
					Model1Schema.ATT_MODELCATALOG).get(0)).getID();
			Integer estID = ((EstModelXml) row.getPmmXml(
					Model1Schema.ATT_ESTMODEL).get(0)).getID();
			String id = "";

			if (estID != null) {
				id += estID;
			} else {
				id += catID;
			}

			if (schemaContainsData) {
				id += "(" + row.getInt(TimeSeriesSchema.ATT_CONDID) + ")";
			}

			if (!idSet.add(id)) {				
				continue;
			}

			ids.add(id);

			PmmXmlDoc modelXml = row.getPmmXml(Model1Schema.ATT_MODELCATALOG);
			PmmXmlDoc estModelXml = row.getPmmXml(Model1Schema.ATT_ESTMODEL);
			String modelName = ((CatalogModelXml) modelXml.get(0)).getName();
			String formula = ((CatalogModelXml) modelXml.get(0)).getFormula();
			String depVar = ((DepXml) row.getPmmXml(Model1Schema.ATT_DEPENDENT)
					.get(0)).getName();
			PmmXmlDoc indepXml = row.getPmmXml(Model1Schema.ATT_INDEPENDENT);
			PmmXmlDoc paramXml = row.getPmmXml(Model1Schema.ATT_PARAMETER);
			Plotable plotable = null;
			Map<String, List<Double>> variables = new LinkedHashMap<String, List<Double>>();
			Map<String, Double> varMin = new LinkedHashMap<String, Double>();
			Map<String, Double> varMax = new LinkedHashMap<String, Double>();
			Map<String, Double> parameters = new LinkedHashMap<String, Double>();
			Map<String, Map<String, Double>> covariances = new LinkedHashMap<String, Map<String, Double>>();
			List<String> infoParams = null;
			List<Object> infoValues = null;

			for (PmmXmlElementConvertable el : indepXml.getElementSet()) {
				IndepXml element = (IndepXml) el;

				variables.put(element.getName(),
						new ArrayList<Double>(Arrays.asList(0.0)));
				varMin.put(element.getName(), element.getMin());
				varMax.put(element.getName(), element.getMax());
			}

			for (PmmXmlElementConvertable el : paramXml.getElementSet()) {
				ParamXml element = (ParamXml) el;

				parameters.put(element.getName(), element.getValue());

				Map<String, Double> cov = new LinkedHashMap<String, Double>();

				for (PmmXmlElementConvertable el2 : paramXml.getElementSet()) {
					cov.put(((ParamXml) el2).getName(), element
							.getCorrelation(((ParamXml) el2).getOrigName()));
				}

				covariances.put(element.getName(), cov);
			}

			if (schemaContainsData) {
				PmmXmlDoc timeSeriesXml = row
						.getPmmXml(TimeSeriesSchema.ATT_TIMESERIES);
				List<Point2D.Double> dataPoints = new ArrayList<Point2D.Double>();
				PmmXmlDoc misc = row.getPmmXml(TimeSeriesSchema.ATT_MISC);
				List<Double> timeList = new ArrayList<Double>();
				List<Double> logcList = new ArrayList<Double>();
				int n = timeSeriesXml.getElementSet().size();

				for (PmmXmlElementConvertable el : timeSeriesXml
						.getElementSet()) {
					TimeSeriesXml element = (TimeSeriesXml) el;
					double time = Double.NaN;
					double logc = Double.NaN;

					if (element.getTime() != null) {
						time = element.getTime();
					}

					if (element.getLog10C() != null) {
						logc = element.getLog10C();
					}

					timeList.add(element.getTime());
					logcList.add(element.getLog10C());
					dataPoints.add(new Point2D.Double(time, logc));
				}

				plotable = new Plotable(Plotable.BOTH);
				plotable.addValueList(AttributeUtilities.TIME, timeList);
				plotable.addValueList(AttributeUtilities.LOGC, logcList);

				for (PmmXmlElementConvertable el : misc.getElementSet()) {
					MiscXml element = (MiscXml) el;

					if (element.getValue() != null) {
						plotable.addValueList(
								element.getName(),
								new ArrayList<Double>(Collections.nCopies(n,
										element.getValue())));
					}
				}

				String dataName;
				String agent;
				String matrix;

				PmmXmlDoc agentXml = row.getPmmXml(TimeSeriesSchema.ATT_AGENT);
				String agentName = ((AgentXml) agentXml.get(0)).getName();
				String agentDetail = ((AgentXml) agentXml.get(0)).getDetail();
				PmmXmlDoc matrixXml = row
						.getPmmXml(TimeSeriesSchema.ATT_MATRIX);
				String matrixName = ((MatrixXml) matrixXml.get(0)).getName();
				String matrixDetail = ((MatrixXml) matrixXml.get(0))
						.getDetail();

				if (row.getString(TimeSeriesSchema.ATT_COMBASEID) != null) {
					dataName = row.getString(TimeSeriesSchema.ATT_COMBASEID);
				} else {
					dataName = "" + row.getInt(TimeSeriesSchema.ATT_CONDID);
				}

				if (agentName != null) {
					agent = agentName + " (" + agentDetail + ")";
				} else {
					agent = agentDetail;
				}

				if (matrixName != null) {
					matrix = matrixName + " (" + matrixDetail + ")";
				} else {
					matrix = matrixDetail;
				}

				shortLegend.put(id, modelName + " (" + dataName + ")");
				longLegend
						.put(id, modelName + " (" + dataName + ") " + formula);
				stringColumnValues.get(0).add(modelName);
				stringColumnValues.get(1).add(dataName);
				doubleColumnValues.get(0).add(
						((EstModelXml) estModelXml.get(0)).getRMS());
				doubleColumnValues.get(1).add(
						((EstModelXml) estModelXml.get(0)).getR2());
				doubleColumnValues.get(2).add(
						((EstModelXml) estModelXml.get(0)).getAIC());
				doubleColumnValues.get(3).add(
						((EstModelXml) estModelXml.get(0)).getBIC());

				if (newTuples != null) {
					PmmXmlDoc newEstModelXml = newTuples.get(nr).getPmmXml(
							Model1Schema.ATT_ESTMODEL);

					doubleColumnValues.get(4).add(
							((EstModelXml) newEstModelXml.get(0)).getRMS());
					doubleColumnValues.get(5).add(
							((EstModelXml) newEstModelXml.get(0)).getR2());
					doubleColumnValues.get(6).add(
							((EstModelXml) newEstModelXml.get(0)).getAIC());
					doubleColumnValues.get(7).add(
							((EstModelXml) newEstModelXml.get(0)).getBIC());
				} else {
					doubleColumnValues.get(4).add(null);
					doubleColumnValues.get(5).add(null);
					doubleColumnValues.get(6).add(null);
					doubleColumnValues.get(7).add(null);
				}

				infoParams = new ArrayList<String>(Arrays.asList(
						Model1Schema.FORMULA, AttributeUtilities.DATAPOINTS,
						TimeSeriesSchema.ATT_AGENT,
						TimeSeriesSchema.ATT_MATRIX,
						AttributeUtilities.ATT_COMMENT));
				infoValues = new ArrayList<Object>(Arrays.asList(formula,
						dataPoints, agent, matrix,
						((MdInfoXml) row.getPmmXml(TimeSeriesSchema.ATT_MDINFO)
								.get(0)).getComment()));

				for (int i = 0; i < miscParams.size(); i++) {
					boolean paramFound = false;

					for (PmmXmlElementConvertable el : misc.getElementSet()) {
						MiscXml element = (MiscXml) el;

						if (miscParams.get(i).equals(element.getName())) {
							doubleColumnValues.get(i + 8).add(
									element.getValue());
							paramFound = true;
							break;
						}
					}

					if (!paramFound) {
						doubleColumnValues.get(i + 8).add(null);
					}
				}
			} else {
				plotable = new Plotable(Plotable.FUNCTION);
				shortLegend.put(id, modelName);
				longLegend.put(id, modelName + " " + formula);
				stringColumnValues.get(0).add(modelName);
				doubleColumnValues.get(0).add(
						((EstModelXml) estModelXml.get(0)).getRMS());
				doubleColumnValues.get(1).add(
						((EstModelXml) estModelXml.get(0)).getR2());
				doubleColumnValues.get(2).add(
						((EstModelXml) estModelXml.get(0)).getAIC());
				doubleColumnValues.get(3).add(
						((EstModelXml) estModelXml.get(0)).getBIC());
				infoParams = new ArrayList<String>(
						Arrays.asList(Model1Schema.FORMULA));
				infoValues = new ArrayList<Object>(Arrays.asList(formula));
			}

			plotable.setFunction(formula);
			plotable.setFunctionValue(depVar);
			plotable.setFunctionArguments(variables);
			plotable.setMinArguments(varMin);
			plotable.setMaxArguments(varMax);
			plotable.setFunctionParameters(parameters);
			plotable.setCovariances(covariances);
			plotable.setDegreesOfFreedom(((EstModelXml) estModelXml.get(0))
					.getDOF());

			if (schemaContainsData) {
				if (!plotable.isPlotable()) {
					stringColumnValues.get(2).add(ChartConstants.FAILED);
				} else if (PmmUtilities.isOutOfRange(paramXml)) {
					stringColumnValues.get(2).add(ChartConstants.OUT_OF_LIMITS);
				} else if (PmmUtilities.covarianceMatrixMissing(paramXml)) {
					stringColumnValues.get(2).add(ChartConstants.NO_COVARIANCE);
				} else {
					stringColumnValues.get(2).add(ChartConstants.OK);
				}
			} else {
				if (!plotable.isPlotable()) {
					stringColumnValues.get(1).add(ChartConstants.FAILED);
				} else if (PmmUtilities.isOutOfRange(paramXml)) {
					stringColumnValues.get(1).add(ChartConstants.OUT_OF_LIMITS);
				} else if (PmmUtilities.covarianceMatrixMissing(paramXml)) {
					stringColumnValues.get(1).add(ChartConstants.NO_COVARIANCE);
				} else {
					stringColumnValues.get(1).add(ChartConstants.OK);
				}
			}

			for (PmmXmlElementConvertable el : paramXml.getElementSet()) {
				ParamXml element = (ParamXml) el;

				infoParams.add(element.getName());
				infoValues.add(element.getValue());
				infoParams.add(element.getName() + ": SE");
				infoValues.add(element.getError());
				infoParams.add(element.getName() + ": t");
				infoValues.add(element.gett());
				infoParams.add(element.getName() + ": Pr > |t|");
				infoValues.add(element.getP());
			}

			plotables.put(id, plotable);
			infoParameters.add(infoParams);
			infoParameterValues.add(infoValues);
		}
	}

	public List<String> getIds() {
		return ids;
	}

	public Map<String, Plotable> getPlotables() {
		return plotables;
	}

	public List<String> getStringColumns() {
		return stringColumns;
	}

	public List<List<String>> getStringColumnValues() {
		return stringColumnValues;
	}

	public List<String> getDoubleColumns() {
		return doubleColumns;
	}

	public List<List<Double>> getDoubleColumnValues() {
		return doubleColumnValues;
	}

	public List<String> getVisibleColumns() {
		return visibleColumns;
	}

	public List<List<String>> getInfoParameters() {
		return infoParameters;
	}

	public List<List<?>> getInfoParameterValues() {
		return infoParameterValues;
	}

	public Map<String, String> getShortLegend() {
		return shortLegend;
	}

	public Map<String, String> getLongLegend() {
		return longLegend;
	}
}
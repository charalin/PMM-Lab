package de.bund.bfr.knime.pmm.fittedparameterview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.knime.core.data.DataTable;

import de.bund.bfr.knime.pmm.common.CatalogModelXml;
import de.bund.bfr.knime.pmm.common.MiscXml;
import de.bund.bfr.knime.pmm.common.ParamXml;
import de.bund.bfr.knime.pmm.common.PmmXmlDoc;
import de.bund.bfr.knime.pmm.common.PmmXmlElementConvertable;
import de.bund.bfr.knime.pmm.common.chart.Plotable;
import de.bund.bfr.knime.pmm.common.generictablemodel.KnimeTuple;
import de.bund.bfr.knime.pmm.common.pmmtablemodel.Model1Schema;
import de.bund.bfr.knime.pmm.common.pmmtablemodel.PmmUtilities;
import de.bund.bfr.knime.pmm.common.pmmtablemodel.SchemaFactory;
import de.bund.bfr.knime.pmm.common.pmmtablemodel.TimeSeriesSchema;
import de.bund.bfr.knime.pmm.common.units.Categories;
import de.bund.bfr.knime.pmm.common.units.Category;
import de.bund.bfr.knime.pmm.common.units.ConvertException;

public class TableReader {

	private List<String> ids;
	private List<Integer> colorCounts;
	private List<String> stringColumns;
	private List<List<String>> stringColumnValues;
	private List<String> conditions;
	private List<List<Double>> conditionMinValues;
	private List<List<Double>> conditionMaxValues;
	private List<List<String>> conditionUnits;
	private List<String> standardVisibleColumns;
	private List<String> filterableStringColumns;

	private Map<String, Plotable> plotables;
	private Map<String, String> shortLegend;
	private Map<String, String> longLegend;

	public TableReader(DataTable table, List<String> usedConditions) {
		Set<String> idSet = new LinkedHashSet<String>();
		Map<String, String> paramNames = new LinkedHashMap<String, String>();
		Map<String, List<Double>> paramDataMap = new LinkedHashMap<String, List<Double>>();
		Map<String, Map<String, List<Double>>> miscDataMaps = new LinkedHashMap<String, Map<String, List<Double>>>();
		List<KnimeTuple> tuples = PmmUtilities.getTuples(table,
				SchemaFactory.createM1DataSchema());
		Map<String, Integer> primModelIDs = new LinkedHashMap<String, Integer>();
		List<String> miscParams = PmmUtilities.getMiscParams(tuples);
		Map<String, List<String>> miscCategories = PmmUtilities
				.getMiscCategories(tuples);
		Map<Integer, List<KnimeTuple>> tuplesByPrimID = new LinkedHashMap<Integer, List<KnimeTuple>>();

		ids = new ArrayList<String>();
		plotables = new LinkedHashMap<String, Plotable>();
		shortLegend = new LinkedHashMap<String, String>();
		longLegend = new LinkedHashMap<String, String>();
		stringColumns = Arrays.asList(Model1Schema.ATT_PARAMETER);
		filterableStringColumns = new ArrayList<String>();
		stringColumnValues = new ArrayList<List<String>>();
		stringColumnValues.add(new ArrayList<String>());
		standardVisibleColumns = new ArrayList<String>(
				Arrays.asList(Model1Schema.ATT_PARAMETER));
		colorCounts = new ArrayList<Integer>();

		conditions = new ArrayList<String>();
		conditionMinValues = new ArrayList<List<Double>>();
		conditionMaxValues = new ArrayList<List<Double>>();
		conditionUnits = new ArrayList<List<String>>();

		for (String param : miscParams) {
			conditions.add(param);
			conditionMinValues.add(new ArrayList<Double>());
			conditionMaxValues.add(new ArrayList<Double>());
			conditionUnits.add(new ArrayList<String>());
			standardVisibleColumns.add(param);
		}

		for (KnimeTuple tuple : tuples) {
			CatalogModelXml modelXml = (CatalogModelXml) tuple.getPmmXml(
					Model1Schema.ATT_MODELCATALOG).get(0);

			if (!tuplesByPrimID.containsKey(modelXml.getId())) {
				tuplesByPrimID.put(modelXml.getId(),
						new ArrayList<KnimeTuple>());
			}

			tuplesByPrimID.get(modelXml.getId()).add(tuple);
		}

		Map<Integer, Map<String, String>> miscUnits = new LinkedHashMap<Integer, Map<String, String>>();

		for (int primID : tuplesByPrimID.keySet()) {
			miscUnits.put(primID,
					PmmUtilities.getMiscUnits(tuplesByPrimID.get(primID)));
		}

		for (KnimeTuple tuple : tuples) {
			CatalogModelXml modelXml = (CatalogModelXml) tuple.getPmmXml(
					Model1Schema.ATT_MODELCATALOG).get(0);
			PmmXmlDoc paramXml = tuple.getPmmXml(Model1Schema.ATT_PARAMETER);

			for (PmmXmlElementConvertable el1 : paramXml.getElementSet()) {
				ParamXml element1 = (ParamXml) el1;
				String id = element1.getName() + " (" + modelXml.getId() + ")";
				String name = element1.getName() + " (" + modelXml.getName()
						+ ")";

				if (idSet.add(id)) {
					paramNames.put(id, element1.getName());
					ids.add(id);
					primModelIDs.put(id, modelXml.getId());
					stringColumnValues.get(0).add(name);
					shortLegend.put(id, name);
					longLegend.put(id, name);

					paramDataMap.put(id, new ArrayList<Double>());
					miscDataMaps.put(id,
							new LinkedHashMap<String, List<Double>>());

					for (String param : miscParams) {
						miscDataMaps.get(id)
								.put(param, new ArrayList<Double>());
					}
				}

				paramDataMap.get(id).add(element1.getValue());

				PmmXmlDoc misc = tuple.getPmmXml(TimeSeriesSchema.ATT_MISC);

				for (String param : miscParams) {
					Double paramValue = null;

					for (PmmXmlElementConvertable el2 : misc.getElementSet()) {
						MiscXml element2 = (MiscXml) el2;

						if (param.equals(element2.getName())) {
							String unit = miscUnits.get(modelXml.getId()).get(
									element2.getName());
							Category category = Categories
									.getCategoryByUnit(unit);

							try {
								paramValue = category.convert(
										element2.getValue(),
										element2.getUnit(), unit);
							} catch (ConvertException e) {
								e.printStackTrace();
							}

							break;
						}
					}

					miscDataMaps.get(id).get(param).add(paramValue);
				}
			}
		}

		for (String id : ids) {
			Plotable plotable = new Plotable(Plotable.DATASET_STRICT);
			Map<String, List<Double>> arguments = new LinkedHashMap<String, List<Double>>();

			for (String param : usedConditions) {
				arguments.put(param, new ArrayList<Double>(Arrays.asList(0.0)));
			}

			plotable.setFunctionValue(paramNames.get(id));
			plotable.setFunctionArguments(arguments);
			plotable.addValueList(paramNames.get(id), paramDataMap.get(id));
			plotable.setCategories(miscCategories);
			plotable.setUnits(miscUnits.get(primModelIDs.get(id)));

			Map<String, List<Double>> miscs = miscDataMaps.get(id);

			for (String param : miscParams) {
				plotable.addValueList(param, miscs.get(param));
			}

			for (int i = 0; i < miscParams.size(); i++) {
				List<Double> nonNullValues = new ArrayList<Double>(
						miscs.get(miscParams.get(i)));
				Double min = null;
				Double max = null;
				String unit = null;

				nonNullValues.removeAll(Arrays.asList((Double) null));

				if (!nonNullValues.isEmpty()) {
					min = Collections.min(nonNullValues);
					max = Collections.max(nonNullValues);
					unit = miscUnits.get(primModelIDs.get(id)).get(
							miscParams.get(i));
				}

				conditionMinValues.get(i).add(min);
				conditionMaxValues.get(i).add(max);
				conditionUnits.get(i).add(unit);
			}

			colorCounts.add(plotable.getNumberOfCombinations());
			plotables.put(id, plotable);
		}
	}

	public List<String> getIds() {
		return ids;
	}

	public List<Integer> getColorCounts() {
		return colorCounts;
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

	public List<String> getConditions() {
		return conditions;
	}

	public List<List<Double>> getConditionMinValues() {
		return conditionMinValues;
	}

	public List<List<Double>> getConditionMaxValues() {
		return conditionMaxValues;
	}

	public List<List<String>> getConditionUnits() {
		return conditionUnits;
	}

	public List<String> getStandardVisibleColumns() {
		return standardVisibleColumns;
	}

	public List<String> getFilterableStringColumns() {
		return filterableStringColumns;
	}

	public Map<String, String> getShortLegend() {
		return shortLegend;
	}

	public Map<String, String> getLongLegend() {
		return longLegend;
	}
}

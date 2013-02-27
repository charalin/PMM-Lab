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
package de.bund.bfr.knime.pmm.secondarymodelanddataview;

import java.awt.Color;
import java.awt.Shape;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.knime.core.data.DataTable;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.image.png.PNGImageContent;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.image.ImagePortObject;
import org.knime.core.node.port.image.ImagePortObjectSpec;

import de.bund.bfr.knime.pmm.common.XmlConverter;
import de.bund.bfr.knime.pmm.common.chart.ChartConstants;
import de.bund.bfr.knime.pmm.common.chart.ChartCreator;
import de.bund.bfr.knime.pmm.common.chart.ChartUtilities;
import de.bund.bfr.knime.pmm.common.chart.Plotable;
import de.bund.bfr.knime.pmm.common.pmmtablemodel.SchemaFactory;

/**
 * This is the model implementation of SecondaryModelAndDataView.
 * 
 * 
 * @author Christian Thoens
 */
public class SecondaryModelAndDataViewNodeModel extends NodeModel {

	protected static final String CFG_SELECTEDID = "SelectedID";
	protected static final String CFG_CURRENTPARAMX = "CurrentParamX";
	protected static final String CFG_PARAMXVALUES = "ParamXValues";
	protected static final String CFG_SELECTEDVALUESX = "SelectedValuesX";
	protected static final String CFG_COLORS = "Colors";
	protected static final String CFG_SHAPES = "Shapes";
	protected static final String CFG_COLORLISTS = "ColorLists";
	protected static final String CFG_SHAPELISTS = "ShapeLists";
	protected static final String CFG_MANUALRANGE = "ManualRange";
	protected static final String CFG_MINX = "MinX";
	protected static final String CFG_MAXX = "MaxX";
	protected static final String CFG_MINY = "MinY";
	protected static final String CFG_MAXY = "MaxY";
	protected static final String CFG_DRAWLINES = "DrawLines";
	protected static final String CFG_SHOWLEGEND = "ShowLegend";
	protected static final String CFG_ADDLEGENDINFO = "AddLegendInfo";
	protected static final String CFG_DISPLAYHIGHLIGHTED = "DisplayHighlighted";
	protected static final String CFG_UNITX = "UnitX";
	protected static final String CFG_UNITY = "UnitY";
	protected static final String CFG_TRANSFORMY = "TransformY";
	protected static final String CFG_STANDARDVISIBLECOLUMNS = "StandardVisibleColumns";
	protected static final String CFG_VISIBLECOLUMNS = "VisibleColumns";
	protected static final String CFG_FITTEDFILTER = "FittedFilter";

	protected static final int DEFAULT_MANUALRANGE = 0;
	protected static final double DEFAULT_MINX = 0.0;
	protected static final double DEFAULT_MAXX = 100.0;
	protected static final double DEFAULT_MINY = 0.0;
	protected static final double DEFAULT_MAXY = 10.0;
	protected static final int DEFAULT_DRAWLINES = 0;
	protected static final int DEFAULT_SHOWLEGEND = 1;
	protected static final int DEFAULT_ADDLEGENDINFO = 0;
	protected static final int DEFAULT_DISPLAYHIGHLIGHTED = 0;
	protected static final String DEFAULT_TRANSFORMY = ChartConstants.NO_TRANSFORM;
	protected static final int DEFAULT_STANDARDVISIBLECOLUMNS = 1;

	private String selectedID;
	private String currentParamX;
	private Map<String, Double> paramXValues;
	private Map<String, List<Boolean>> selectedValuesX;
	private Map<String, Color> colors;
	private Map<String, Shape> shapes;
	private Map<String, List<Color>> colorLists;
	private Map<String, List<Shape>> shapeLists;
	private int manualRange;
	private double minX;
	private double maxX;
	private double minY;
	private double maxY;
	private int drawLines;
	private int showLegend;
	private int addLegendInfo;
	private int displayHighlighted;
	private String unitX;
	private String unitY;
	private String transformY;
	private int standardVisibleColumns;
	private List<String> visibleColumns;
	private String fittedFilter;

	/**
	 * Constructor for the node model.
	 */
	protected SecondaryModelAndDataViewNodeModel() {
		super(new PortType[] { BufferedDataTable.TYPE },
				new PortType[] { ImagePortObject.TYPE });
		selectedID = null;
		currentParamX = null;
		paramXValues = new LinkedHashMap<>();
		selectedValuesX = new LinkedHashMap<>();
		colors = new LinkedHashMap<>();
		shapes = new LinkedHashMap<>();
		colorLists = new LinkedHashMap<>();
		shapeLists = new LinkedHashMap<>();
		manualRange = DEFAULT_MANUALRANGE;
		minX = DEFAULT_MINX;
		maxX = DEFAULT_MAXX;
		minY = DEFAULT_MINY;
		maxY = DEFAULT_MAXY;
		drawLines = DEFAULT_DRAWLINES;
		showLegend = DEFAULT_SHOWLEGEND;
		addLegendInfo = DEFAULT_ADDLEGENDINFO;
		displayHighlighted = DEFAULT_DISPLAYHIGHLIGHTED;
		unitX = null;
		unitY = null;
		transformY = DEFAULT_TRANSFORMY;
		standardVisibleColumns = DEFAULT_STANDARDVISIBLECOLUMNS;
		visibleColumns = new ArrayList<>();
		fittedFilter = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
			throws Exception {
		DataTable table = (DataTable) inObjects[0];
		TableReader reader;
		boolean containsData;

		if (SchemaFactory.createDataSchema().conforms(table)) {
			reader = new TableReader(table, true);

			if (Collections.max(reader.getColorCounts()) == 0) {
				reader = new TableReader(table, false);
				containsData = false;
			} else {
				containsData = true;
			}
		} else {
			reader = new TableReader(table, false);
			containsData = false;
		}

		ChartCreator creator = new ChartCreator(reader.getPlotables(),
				reader.getShortLegend(), reader.getLongLegend());

		if (selectedID != null && reader.getPlotables().get(selectedID) != null) {
			Plotable plotable = reader.getPlotables().get(selectedID);
			Map<String, List<Double>> arguments = new LinkedHashMap<>();

			if (containsData) {
				Map<String, List<Double>> possibleValues = plotable
						.getPossibleArgumentValues(true, false);

				for (String param : selectedValuesX.keySet()) {
					List<Double> usedValues = new ArrayList<>();
					List<Double> valuesList = possibleValues.get(param);

					if (!param.equals(currentParamX)) {
						for (int i = 0; i < selectedValuesX.get(param).size(); i++) {
							if (selectedValuesX.get(param).get(i)) {
								usedValues.add(valuesList.get(i));
							}
						}
					} else {
						usedValues.add(0.0);
					}

					arguments.put(param, usedValues);
				}
			} else {
				for (Map.Entry<String, Double> entry : paramXValues.entrySet()) {
					arguments.put(entry.getKey(),
							Arrays.asList(entry.getValue()));
				}
			}

			plotable.setFunctionArguments(arguments);
			creator.setParamX(currentParamX);
			creator.setParamY(plotable.getFunctionValue());
			creator.setUseManualRange(manualRange == 1);
			creator.setMinX(minX);
			creator.setMaxX(maxX);
			creator.setMinY(minY);
			creator.setMaxY(maxY);
			creator.setDrawLines(drawLines == 1);
			creator.setShowLegend(showLegend == 1);
			creator.setAddInfoInLegend(addLegendInfo == 1);
			creator.setUnitX(unitX);
			creator.setUnitY(unitY);
			creator.setTransformY(transformY);

			if (containsData) {
				creator.setColorLists(colorLists);
				creator.setShapeLists(shapeLists);
			} else {
				creator.setColors(colors);
				creator.setShapes(shapes);
			}
		}

		return new PortObject[] { new ImagePortObject(
				ChartUtilities.convertToPNGImageContent(
						creator.getChart(selectedID), 640, 480),
				new ImagePortObjectSpec(PNGImageContent.TYPE)) };
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
	protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs)
			throws InvalidSettingsException {
		if (!SchemaFactory.createM2Schema()
				.conforms((DataTableSpec) inSpecs[0])) {
			throw new InvalidSettingsException("Wrong input!");
		}

		return new PortObjectSpec[] { new ImagePortObjectSpec(
				PNGImageContent.TYPE) };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		settings.addString(CFG_SELECTEDID, selectedID);
		settings.addString(CFG_CURRENTPARAMX, currentParamX);
		settings.addString(CFG_PARAMXVALUES,
				XmlConverter.mapToXml(paramXValues));
		settings.addString(CFG_SELECTEDVALUESX,
				XmlConverter.mapToXml(selectedValuesX));
		settings.addString(CFG_COLORS, XmlConverter.colorMapToXml(colors));
		settings.addString(CFG_SHAPES, XmlConverter.shapeMapToXml(shapes));
		settings.addString(CFG_COLORLISTS,
				XmlConverter.colorListMapToXml(colorLists));
		settings.addString(CFG_SHAPELISTS,
				XmlConverter.shapeListMapToXml(shapeLists));
		settings.addInt(CFG_MANUALRANGE, manualRange);
		settings.addDouble(CFG_MINX, minX);
		settings.addDouble(CFG_MAXX, maxX);
		settings.addDouble(CFG_MINY, minY);
		settings.addDouble(CFG_MAXY, maxY);
		settings.addInt(CFG_DRAWLINES, drawLines);
		settings.addInt(CFG_SHOWLEGEND, showLegend);
		settings.addInt(CFG_ADDLEGENDINFO, addLegendInfo);
		settings.addInt(CFG_DISPLAYHIGHLIGHTED, displayHighlighted);
		settings.addString(CFG_UNITX, unitX);
		settings.addString(CFG_UNITY, unitY);
		settings.addString(CFG_TRANSFORMY, transformY);
		settings.addInt(CFG_STANDARDVISIBLECOLUMNS, standardVisibleColumns);
		settings.addString(CFG_VISIBLECOLUMNS,
				XmlConverter.listToXml(visibleColumns));
		settings.addString(CFG_FITTEDFILTER, fittedFilter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		selectedID = settings.getString(CFG_SELECTEDID);
		currentParamX = settings.getString(CFG_CURRENTPARAMX);
		paramXValues = XmlConverter.xmlToDoubleMap(settings
				.getString(CFG_PARAMXVALUES));
		selectedValuesX = XmlConverter.xmlToBoolListMap(settings
				.getString(CFG_SELECTEDVALUESX));
		colors = XmlConverter.xmlToColorMap(settings.getString(CFG_COLORS));
		shapes = XmlConverter.xmlToShapeMap(settings.getString(CFG_SHAPES));
		colorLists = XmlConverter.xmlToColorListMap(settings
				.getString(CFG_COLORLISTS));
		shapeLists = XmlConverter.xmlToShapeListMap(settings
				.getString(CFG_SHAPELISTS));
		manualRange = settings.getInt(CFG_MANUALRANGE);
		minX = settings.getDouble(CFG_MINX);
		maxX = settings.getDouble(CFG_MAXX);
		minY = settings.getDouble(CFG_MINY);
		maxY = settings.getDouble(CFG_MAXY);
		drawLines = settings.getInt(CFG_DRAWLINES);
		showLegend = settings.getInt(CFG_SHOWLEGEND);
		addLegendInfo = settings.getInt(CFG_ADDLEGENDINFO);
		displayHighlighted = settings.getInt(CFG_DISPLAYHIGHLIGHTED);
		unitX = settings.getString(CFG_UNITX);
		unitY = settings.getString(CFG_UNITY);
		transformY = settings.getString(CFG_TRANSFORMY);
		standardVisibleColumns = settings.getInt(CFG_STANDARDVISIBLECOLUMNS);
		visibleColumns = XmlConverter.xmlToStringList(settings
				.getString(CFG_VISIBLECOLUMNS));
		fittedFilter = settings.getString(CFG_FITTEDFILTER);
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

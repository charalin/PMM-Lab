package de.bund.bfr.knime.pmm.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import de.bund.bfr.knime.pmm.common.generictablemodel.KnimeTuple;
import de.bund.bfr.knime.pmm.common.math.MathUtilities;
import de.bund.bfr.knime.pmm.common.pmmtablemodel.TimeSeriesSchema;

public class XLSReader {
	
	private XLSReader() {		
	}

	public static Map<String, KnimeTuple> getTuples(File file) throws Exception {
		Map<String, KnimeTuple> tuples = new LinkedHashMap<String, KnimeTuple>();
		InputStream inputStream = new FileInputStream(file);
		Workbook wb = WorkbookFactory.create(inputStream);
		Sheet sheet = wb.getSheetAt(0);

		KnimeTuple tuple = null;
		String id = null;

		for (int i = 1;; i++) {
			if (sheet.getRow(i).getCell(7) == null
					&& sheet.getRow(i).getCell(8) == null) {
				if (tuple != null) {
					tuples.put(id, tuple);
				}

				break;
			}

			if (sheet.getRow(i).getCell(0) != null
					&& !sheet.getRow(i).getCell(0).toString().isEmpty()
					&& !sheet.getRow(i).getCell(0).toString().equals(id)) {
				if (tuple != null) {
					tuples.put(id, tuple);
				}
				
				id = sheet.getRow(i).getCell(0).toString();
				tuple = new KnimeTuple(new TimeSeriesSchema());
				tuple.setValue(TimeSeriesSchema.ATT_CONDID,
						MathUtilities.getRandomNegativeInt());

				if (sheet.getRow(i).getCell(1) != null) {
					tuple.setValue(TimeSeriesSchema.ATT_AGENTDETAIL, sheet
							.getRow(i).getCell(1).toString());
				}

				if (sheet.getRow(i).getCell(2) != null) {
					tuple.setValue(TimeSeriesSchema.ATT_MATRIXDETAIL, sheet
							.getRow(i).getCell(2).toString());
				}

				if (sheet.getRow(i).getCell(3) != null) {
					tuple.setValue(TimeSeriesSchema.ATT_COMMENT, sheet
							.getRow(i).getCell(3).toString());
				}

				if (sheet.getRow(i).getCell(4) != null
						&& !sheet.getRow(i).getCell(4).toString().isEmpty()) {
					try {
						tuple.setValue(
								TimeSeriesSchema.ATT_TEMPERATURE,
								Double.parseDouble(sheet.getRow(i).getCell(4)
										.toString()));
					} catch (NumberFormatException e) {
						throw new Exception("Temperature value in row "
								+ (i + 1) + " is not valid");
					}
				}

				if (sheet.getRow(i).getCell(5) != null
						&& !sheet.getRow(i).getCell(5).toString().isEmpty()) {
					try {
						tuple.setValue(
								TimeSeriesSchema.ATT_PH,
								Double.parseDouble(sheet.getRow(i).getCell(5)
										.toString()));
					} catch (NumberFormatException e) {
						throw new Exception("pH value in row " + (i + 1)
								+ " is not valid");
					}
				}

				if (sheet.getRow(i).getCell(6) != null
						&& !sheet.getRow(i).getCell(6).toString().isEmpty()) {
					try {
						tuple.setValue(
								TimeSeriesSchema.ATT_WATERACTIVITY,
								Double.parseDouble(sheet.getRow(i).getCell(6)
										.toString()));
					} catch (NumberFormatException e) {
						throw new Exception("Water Activity value in row "
								+ (i + 1) + " is not valid");
					}
				}
			}

			if (sheet.getRow(i).getCell(7) != null
					&& !sheet.getRow(i).getCell(7).toString().isEmpty()) {
				try {
					tuple.addValue(
							TimeSeriesSchema.ATT_TIME,
							Double.parseDouble(sheet.getRow(i).getCell(7)
									.toString()));
				} catch (NumberFormatException e) {
					throw new Exception("Time value in row " + (i + 1)
							+ " is not valid");
				}
			}

			if (sheet.getRow(i).getCell(8) != null
					&& !sheet.getRow(i).getCell(8).toString().isEmpty()) {
				try {
					tuple.addValue(
							TimeSeriesSchema.ATT_LOGC,
							Double.parseDouble(sheet.getRow(i).getCell(8)
									.toString()));
				} catch (NumberFormatException e) {
					throw new Exception("LogC value in row " + (i + 1)
							+ " is not valid");
				}
			}
		}

		return tuples;
	}
}

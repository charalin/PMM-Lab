package de.bund.bfr.knime.pmm.sbmlutil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.bund.bfr.knime.pmm.annotation.CondIDNode;
import de.bund.bfr.knime.pmm.annotation.numl.AgentNuMLNode;
import de.bund.bfr.knime.pmm.annotation.numl.MatrixNuMLNode;
import de.bund.bfr.knime.pmm.annotation.numl.ReferenceNuMLNode;
import de.bund.bfr.knime.pmm.common.AgentXml;
import de.bund.bfr.knime.pmm.common.LiteratureItem;
import de.bund.bfr.knime.pmm.common.MatrixXml;
import de.bund.bfr.knime.pmm.common.units.UnitsFromDB;
import de.bund.bfr.knime.pmm.dbutil.DBUnits;
import de.bund.bfr.numl.AtomicDescription;
import de.bund.bfr.numl.AtomicValue;
import de.bund.bfr.numl.DataType;
import de.bund.bfr.numl.DimensionValue;
import de.bund.bfr.numl.NuMLDocument;
import de.bund.bfr.numl.OntologyTerm;
import de.bund.bfr.numl.ResultComponent;
import de.bund.bfr.numl.Tuple;
import de.bund.bfr.numl.TupleDescription;

public class DataFile {

	NuMLDocument doc;
	
	private static final String ANNOTATION_TAG = "annotation";
	private static final String METADATA_TAG = "pmf:metadata";
	private static final String CONDID_TAG = String.format("%s:%s", CondIDNode.NS, CondIDNode.TAG);
	private static final String COMBASEID_TAG = "pmmlab:combaseId";
	private static final String COMPARTMENT_TAG = String.format("%s:%s", MatrixNuMLNode.NS, MatrixNuMLNode.TAG);
	private static final String SPECIES_TAG = String.format("%s:%s", AgentNuMLNode.NS, AgentNuMLNode.TAG);
	private static final String CREATOR_TAG = "dc:creator";
	private static final String CREATED_TAG = "dc:created";
	private static final String MODIFIED_TAG = "dc:modified";
	private static final String TYPE_TAG = "dc:type";
	private static final String UNIT_DEFINITION_TAG = "sbml:unitDefinition";
	private static final String REFERENCE_TAG = "dc:reference";

	public DataFile(NuMLDocument doc) {
		this.doc = doc;
	}

	public DataFile(int condId, String combaseId, LinkedHashMap<Integer, List<Double>> dim, String concUnit,
			String timeUnit, Matrix matrix, Agent agent, List<LiteratureItem> lits, Metadata metadata) {

		// Creates utility w3c Document
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Document utilDoc = documentBuilder.newDocument();

		// Creates ontologies
		OntologyTerm time = createTimeOntology(timeUnit);
		OntologyTerm conc = createConcOntology(concUnit, matrix, agent);

		AtomicDescription concDesc = new AtomicDescription("concentration", conc, DataType.DOUBLE);
		AtomicDescription timeDesc = new AtomicDescription("Time", time, DataType.DOUBLE);
		TupleDescription td = new TupleDescription(Arrays.asList(timeDesc, concDesc));

		List<DimensionValue> values = new ArrayList<>();
		for (List<Double> dimPair : dim.values()) {
			AtomicValue timeValue = new AtomicValue(dimPair.get(0));
			AtomicValue concValue = new AtomicValue(dimPair.get(1));
			values.add(new Tuple(Arrays.asList(timeValue, concValue)));
		}

		ResultComponent result = new ResultComponent("exp1", td, values);

		// Adds PMF namespace to resultComponent's annotation
		Element rcAnnotation = utilDoc.createElement(ANNOTATION_TAG);
		result.setAnnotation(rcAnnotation);

		Element rcMetadata = utilDoc.createElement(METADATA_TAG);
		rcAnnotation.appendChild(rcMetadata);

		Element condIdNode = utilDoc.createElement(CONDID_TAG);
		condIdNode.setTextContent(Integer.toString(condId));
		rcMetadata.appendChild(condIdNode);

		if (combaseId != null) {
			Element combaseIdNode = utilDoc.createElement(COMBASEID_TAG);
			combaseIdNode.setTextContent(combaseId);
			rcMetadata.appendChild(combaseIdNode);
		}

		String givenName = metadata.getGivenName();
		String familyName = metadata.getFamilyName();
		String contact = metadata.getContact();
		if (givenName != null || familyName != null || contact != null) {
			String creator = String.format(Locale.ENGLISH, "%s.%s.%s", metadata.getGivenName(),
					metadata.getFamilyName(), metadata.getContact());

			Element creatorNode = utilDoc.createElement(CREATOR_TAG);
			creatorNode.setTextContent(creator);
			rcMetadata.appendChild(creatorNode);
		}

		// Adds created
		if (metadata.getCreatedDate() != null) {
			Element createdNode = utilDoc.createElement(CREATED_TAG);
			createdNode.setTextContent(metadata.getCreatedDate());
			rcMetadata.appendChild(createdNode);
		}

		// Adds last modified
		if (metadata.getModifiedDate() != null) {
			Element modifiedNode = utilDoc.createElement(MODIFIED_TAG);
			modifiedNode.setTextContent(metadata.getModifiedDate());
			rcMetadata.appendChild(utilDoc.importNode(modifiedNode, true));
		}

		// Adds type
		if (metadata.getType() != null) {
			Element typeNode = utilDoc.createElement(TYPE_TAG);
			typeNode.setTextContent(metadata.getType());
			rcMetadata.appendChild(utilDoc.importNode(typeNode, true));
		}

		// Adds annotations for literature items
		for (LiteratureItem lit : lits) {
			try {
				Element refNode = new ReferenceNuMLNode(lit).getNode();
				rcMetadata.appendChild(utilDoc.importNode(refNode, true));
			} catch (DOMException | ParserConfigurationException e) {
				e.printStackTrace();
			}
		}

		doc = new NuMLDocument(Arrays.asList(time, conc), Arrays.asList(result));
	}

	public NuMLDocument getDocument() {
		return doc;
	}

	public int getCondID() {
		// Gets result component metadata
		ResultComponent rc = doc.getResultComponents().get(0);
		Element annotation = rc.getAnnotation();
		NodeList rcMetadataNodes = annotation.getElementsByTagName(METADATA_TAG);
		Element rcMetadataElement = (Element) rcMetadataNodes.item(0);

		// Gets CondId
		NodeList condIdNodes = rcMetadataElement.getElementsByTagName(CONDID_TAG);
		Element condIdNode = (Element) condIdNodes.item(0);
		int condId = Integer.parseInt(condIdNode.getTextContent());

		return condId;
	}

	/**
	 * Gets the CombaseID within the ResultComponent.
	 * 
	 * @return CombaseID string. If missing a "?" will be returned.
	 */
	public String getCombaseID() {
		// Gets result component metadata
		ResultComponent rc = doc.getResultComponents().get(0);
		Element annotation = rc.getAnnotation();
		NodeList rcMetadataNodes = annotation.getElementsByTagName(METADATA_TAG);
		Element rcMetadataElement = (Element) rcMetadataNodes.item(0);

		// Gets CombaseID
		NodeList combaseIdNodes = rcMetadataElement.getElementsByTagName(COMBASEID_TAG);
		if (combaseIdNodes.getLength() == 1) {
			return ((Element) combaseIdNodes.item(0)).getTextContent();
		}
		return "?";
	}

	public String getConcUnit() {
		OntologyTerm ot = doc.getOntologyTerms().get(1);
		Element annotation = ot.getAnnotation();

		NodeList metadataNodes = annotation.getElementsByTagName(METADATA_TAG);
		Element metadataNode = (Element) metadataNodes.item(0);
		
		NodeList unitDefNodes = metadataNode.getElementsByTagName(UNIT_DEFINITION_TAG);
		Element unitDefNode = (Element) unitDefNodes.item(0);
		return unitDefNode.getAttribute("name");
	}

	public String getTimeUnit() {
		OntologyTerm ot = doc.getOntologyTerms().get(0);
		Element annotation = ot.getAnnotation();

		NodeList metadataNodes = annotation.getElementsByTagName(METADATA_TAG);
		Element metadataNode = (Element) metadataNodes.item(0);

		NodeList unitDefNodes = metadataNode.getElementsByTagName(UNIT_DEFINITION_TAG);
		Element unitDefNode = (Element) unitDefNodes.item(0);
		return unitDefNode.getAttribute("name");
	}

	public MatrixXml getMatrix() {
		OntologyTerm conc = doc.getOntologyTerms().get(1);
		Element annotation = conc.getAnnotation();
		
		NodeList metadataNodes = annotation.getElementsByTagName(METADATA_TAG);
		Element metadataNode = (Element) metadataNodes.item(0);
		
		// Gets matrix node
		NodeList matrixNodes = metadataNode.getElementsByTagName(COMPARTMENT_TAG);
		Element matrixNode = (Element) matrixNodes.item(0);

		return new MatrixNuMLNode(matrixNode).toMatrixXml();
	}

	public AgentXml getAgent() {
		OntologyTerm conc = doc.getOntologyTerms().get(1);
		Element annotation = conc.getAnnotation();

		NodeList metadataNodes = annotation.getElementsByTagName(METADATA_TAG);
		Element metadataNode = (Element) metadataNodes.item(0);

		// Gets agent node
		NodeList agentNodes = metadataNode.getElementsByTagName(SPECIES_TAG);
		Element agentNode = (Element) agentNodes.item(0);

		return new AgentNuMLNode(agentNode).toAgentXml();
	}

	public Map<String, Double> getMiscs() {
		OntologyTerm conc = doc.getOntologyTerms().get(1);
		Element annotation = conc.getAnnotation();

		NodeList metadataNodes = annotation.getElementsByTagName(METADATA_TAG);
		Element metadataNode = (Element) metadataNodes.item(0);
		
		// Gets matrix node
		NodeList matrixNodes = metadataNode.getElementsByTagName(COMPARTMENT_TAG);
		Element matrixNode = (Element) matrixNodes.item(0);

		Map<String, Double> miscs = new HashMap<>();
		NodeList miscNodes = matrixNode.getElementsByTagName("pmmlab:modelVariable");
		for (int i = 0; i < miscNodes.getLength(); i++) {
			Element miscNode = (Element) miscNodes.item(i);
			String name = miscNode.getAttribute("name");
			Double value = Double.parseDouble(miscNode.getAttribute("value"));
			miscs.put(name, value);
		}

		return miscs;
	}

	public List<LiteratureItem> getLits() {
		ResultComponent rc = doc.getResultComponents().get(0);
		Element annotation = rc.getAnnotation();

		// Gets result component metadata
		NodeList rcMetadataNodes = annotation.getElementsByTagName(METADATA_TAG);
		Element rcMetadataNode = (Element) rcMetadataNodes.item(0);

		// Gets literature items
		List<LiteratureItem> lits = new LinkedList<>();
		NodeList litNodes = rcMetadataNode.getElementsByTagName(REFERENCE_TAG);
		for (int i = 0; i < litNodes.getLength(); i++) {
			Element litNode = (Element) litNodes.item(i);
			lits.add(new ReferenceNuMLNode(litNode).toLiteratureItem());
		}

		return lits;
	}

	public Metadata getMetadata() {
		Metadata metadata = new Metadata();

		ResultComponent rc = doc.getResultComponents().get(0);

		// Gets result component metadata
		NodeList rcMetadataNodes = rc.getAnnotation().getElementsByTagName(METADATA_TAG);
		Element rcMetadataNode = (Element) rcMetadataNodes.item(0);

		NodeList creatorNodes = rcMetadataNode.getElementsByTagName(CREATOR_TAG);
		if (creatorNodes.getLength() == 1) {
			Element creatorNode = (Element) creatorNodes.item(0);
			String[] tempStrings = creatorNode.getTextContent().split("\\.", 3);
			metadata.setGivenName(tempStrings[0]);
			metadata.setFamilyName(tempStrings[1]);
			metadata.setContact(tempStrings[2]);
		}

		// Gets created date
		NodeList createdNodes = rcMetadataNode.getElementsByTagName(CREATED_TAG);
		if (createdNodes.getLength() == 1) {
			Element createdNode = (Element) createdNodes.item(0);
			metadata.setCreatedDate(createdNode.getTextContent());
		}

		// Gets last modification date
		NodeList modifiedNodes = rcMetadataNode.getElementsByTagName(MODIFIED_TAG);
		if (modifiedNodes.getLength() == 1) {
			Element modifiedNode = (Element) modifiedNodes.item(0);
			metadata.setModifiedDate(modifiedNode.getTextContent());
		}

		NodeList typeNodes = rcMetadataNode.getElementsByTagName(TYPE_TAG);
		if (typeNodes.getLength() == 1) {
			Element typeNode = (Element) typeNodes.item(0);
			metadata.setType(typeNode.getTextContent());
		}

		return metadata;
	}

	public double[][] getData() {
		
		List<DimensionValue> tuples = doc.getResultComponents().get(0).getDimension();
		double[][] data = new double[tuples.size()][2];

		int i = 0;
		for (DimensionValue dv : tuples) {
			@SuppressWarnings("unchecked")
			List<AtomicValue> atomicValues = (List<AtomicValue>) dv.getChildren();
			AtomicValue concValue = atomicValues.get(0);
			AtomicValue timeValue = atomicValues.get(1);
			
			data[i][0] = (double) concValue.getValue();
			data[i][1] = (double) timeValue.getValue();
			
			i++;
		}

		return data;
	}

	private static OntologyTerm createConcOntology(String unit, Matrix matrix, Agent agent) {
		// Creates utility w3c Document
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
		Document utilDoc = documentBuilder.newDocument();

		URI uri = null;
		try {
			uri = new URI("http://www.ebi.ac.uk/sbo/");
		} catch (URISyntaxException e) {
			// The URI assigned is valid so there is no need to do something
			// here
			e.printStackTrace();
		}

		OntologyTerm ontologyTerm = new OntologyTerm("concentration", "concentration", "SBO:0000196", uri.toString());

		// Creates annotation
		Element annot = utilDoc.createElement(ANNOTATION_TAG);

		// Creates and adds PMF annotation
		Element pmfNode = utilDoc.createElement(METADATA_TAG);
		annot.appendChild(pmfNode);

		// Gets unit definition from DB
		UnitsFromDB dbUnit = DBUnits.getDBUnits().get(unit);
		PMFUnitDefinition pud = PMFUnitDefinition.xmlToPMFUnitDefinition(dbUnit.getMathML_string());

		// Modifies the unit definition and adds it to the pmfNode
		pud.getUnitDefinition().setId(Util.createId(unit));
		pud.getUnitDefinition().setName(unit);
		Element pudNode = null;
		try {
			pudNode = (Element) utilDoc.importNode(pud.toNuMLNode(), true);
		} catch (DOMException | ParserConfigurationException e1) {
			e1.printStackTrace();
		}
		pmfNode.appendChild(pudNode);
		
		try {
			Element matrixNode = new MatrixNuMLNode(matrix).getNode();
			pmfNode.appendChild(utilDoc.importNode(matrixNode, true));
		} catch (DOMException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		try {
			Element agentNode = new AgentNuMLNode(agent).getNode();
			pmfNode.appendChild(utilDoc.importNode(agentNode, true));
		} catch (DOMException | ParserConfigurationException e) {
			e.printStackTrace();
		}

		ontologyTerm.setAnnotation(annot);
		return ontologyTerm;

	}
	
	private static OntologyTerm createTimeOntology(String unit) {
		// Creates utility w3c Document
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Document utilDoc = documentBuilder.newDocument();
		
		URI uri = null;
		try {
			uri = new URI("http://www.ebi.ac.uk/sbo/");
		} catch (URISyntaxException e) {
			// The URI assigned is valid so there is no need to do something
			// here
			e.printStackTrace();
		}

		OntologyTerm ontologyTerm = new OntologyTerm("time", "time", "SBO:0000345", uri.toString());

		// Creates annotation
		Element annot = utilDoc.createElement(ANNOTATION_TAG);
		ontologyTerm.setAnnotation(annot);

		// Creates and adds PMF annotation
		Element pmfNode = utilDoc.createElement(METADATA_TAG);
		annot.appendChild(pmfNode);

		// Gets unit definition from DB
		UnitsFromDB dbUnit = DBUnits.getDBUnits().get(unit);
		PMFUnitDefinition pud = PMFUnitDefinition.xmlToPMFUnitDefinition(dbUnit.getMathML_string());

		// Modifies the unit definition and adds it to the pmfNode
		pud.getUnitDefinition().setId(Util.createId(unit));
		pud.getUnitDefinition().setName(unit);
		Element pudNode = null;
		try {
			pudNode = (Element) utilDoc.importNode(pud.toNuMLNode(), true);
		} catch (DOMException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		pmfNode.appendChild(pudNode);
		return ontologyTerm;
	}
}
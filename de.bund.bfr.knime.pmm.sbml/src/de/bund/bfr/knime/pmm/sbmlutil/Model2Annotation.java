/**
 * Secondary model annotation. Holds its global model ID and references.
 * @author Miguel Alba (malba@optimumquality.es)
 */
package de.bund.bfr.knime.pmm.sbmlutil;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.sbml.jsbml.xml.XMLNode;
import org.sbml.jsbml.xml.XMLTriple;

import de.bund.bfr.knime.pmm.annotation.ReferenceNode;
import de.bund.bfr.knime.pmm.annotation.UncertaintyNode;
import de.bund.bfr.knime.pmm.common.LiteratureItem;

public class Model2Annotation {

	static final String METADATA_TAG = "metadata";
	static final String PMF_TAG = "pmf";
	static final String GLOBAL_MODEL_ID_TAG = "globalModelID";
	static final String REFERENCE_TAG = "reference";
	static final String MODEL_QUALITY_TAG = "modelquality";

	XMLNode node;
	List<LiteratureItem> literatureItems;
	int globalModelID;
	Map<String, String> uncertainties;

	// Get literature items and globalModelID from existing sec model annotation
	public Model2Annotation(XMLNode node) {
		this.node = node;
		XMLNode metadata = node.getChildElement(METADATA_TAG, "");

		// Get globalModelID
		XMLNode globalModelIDNode = metadata.getChildElement(
				GLOBAL_MODEL_ID_TAG, "");
		globalModelID = Integer.parseInt(globalModelIDNode.getChild(0)
				.getCharacters());

		// Get model quality annotation
		XMLNode qualityNode = metadata.getChildElement(MODEL_QUALITY_TAG, "");
		if (qualityNode != null) {
			uncertainties = new UncertaintyNode(qualityNode).getMeasures();
		}

		// Get references
		literatureItems = new LinkedList<>();
		for (XMLNode ref : metadata.getChildElements(REFERENCE_TAG, "")) {
			ReferenceNode refNode = new ReferenceNode(ref);
			literatureItems.add(refNode.toLiteratureItem());
		}
	}

	// Build new coefficient annotation for globalModelID and references
	public Model2Annotation(int globalModelID,
			Map<String, String> uncertainties, List<LiteratureItem> lits) {
		// Build metadata node
		node = new XMLNode(new XMLTriple(METADATA_TAG, null, PMF_TAG));

		// Build globalModelID node
		XMLTriple globalModelIDTriple = new XMLTriple(GLOBAL_MODEL_ID_TAG, "",
				PMF_TAG);
		XMLNode globalModelIDNode = new XMLNode(globalModelIDTriple);
		globalModelIDNode.addChild(new XMLNode(new Integer(globalModelID)
				.toString()));
		node.addChild(globalModelIDNode);
		
		// Build uncertainties node
		if (!uncertainties.isEmpty()) {
			node.addChild(new UncertaintyNode(uncertainties).getNode());
		}

		// Build references node
		for (LiteratureItem lit : lits) {
			ReferenceNode ref = new ReferenceNode(lit);
			node.addChild(ref.getNode());
		}

		// Save fields
		this.globalModelID = globalModelID;
		this.literatureItems = lits;
		this.uncertainties = uncertainties;
	}

	// Getters
	public XMLNode getNode() {
		return node;
	}

	public int getGlobalModelID() {
		return globalModelID;
	}

	public List<LiteratureItem> getLiteratureItems() {
		return literatureItems;
	}
	
	public Map<String, String> getUncertainties() {
		return uncertainties;
	}
}
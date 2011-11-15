package com.google.code.maven_replacer_plugin;

import static org.apache.commons.lang.StringUtils.isEmpty;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XPathReplacer implements Replacer {

	private TokenReplacer tokenReplacer;
	private DocumentBuilder docBuilder;
	private XPath xpath;
	private Transformer transformer;

	public XPathReplacer(TokenReplacer tokenReplacer) {
		this.tokenReplacer = tokenReplacer;
		try {
			this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			this.xpath = XPathFactory.newInstance().newXPath();
			this.transformer = TransformerFactory.newInstance().newTransformer();
		} catch (Exception e) {
			throw new IllegalStateException("Unable to initialize XML processing", e);
		}
	}

	public String replace(String content, Replacement replacement, boolean regex, int regexFlags) {
		try {
			Document doc = parseXml(content);
			NodeList replacementTargets = findReplacementNodes(doc, replacement.getXpath());
			replaceContent(replacementTargets, replacement, regex, regexFlags);
			return writeXml(doc);
		} catch (Exception e) {
			throw new RuntimeException("Error during XML replacement", e);
		}
	}

	// replaces the XML within the given nodes
	private void replaceContent(NodeList replacementNodes, Replacement context, boolean regex, int regexFlags) throws Exception {
		if (isEmpty(context.getToken())) {
			throw new IllegalArgumentException("Token or token file required");
		}

		// loop over the node list
		for (int i = 0; i < replacementNodes.getLength(); i++) {
			Node replacementNode = replacementNodes.item(i);

			// -> convert each node to a string
			String replacementNodeStr = convertNodeToString(replacementNode);

			// -> make the replacement on the string
			String replacedNodeStr = tokenReplacer.replace(replacementNodeStr, context, regex, regexFlags);

			// -> convert the modified string back to a node
			Node replacedNode = convertXmlToNode(replacedNodeStr);

			// -> replace the original node with the modified node
			Node parent = replacementNode.getParentNode();
			replacedNode = parent.getOwnerDocument().importNode(replacedNode, true);
			parent.replaceChild(replacedNode, replacementNode);
		}
	}

	private Document parseXml(String content) throws IOException {
		try {
			return docBuilder.parse(new InputSource(new StringReader(content)));
		} catch (SAXException e) {
			throw new IOException("Unable to parse XML", e);
		}
	}

	// finds the nodes in the document that match the specified XPath
	private NodeList findReplacementNodes(Document doc, String xpathString)
			throws SAXException, IOException, XPathExpressionException {
		XPathExpression xpathExpr = xpath.compile(xpathString);

		return (NodeList) xpathExpr.evaluate(doc, XPathConstants.NODESET);
	}

	// converts the given node into a Java String
	private String convertNodeToString(Node replacementTarget) throws TransformerException {
		DOMSource targetSource = new DOMSource(replacementTarget);
		StringWriter stringWriter = new StringWriter();
		Result stringResult = new StreamResult(stringWriter);
		transformer.transform(targetSource, stringResult);
		return stringWriter.toString();
	}

	// converts the given string into an XML node
	private Node convertXmlToNode(String xml) throws Exception {
		InputSource docSource = new InputSource(new StringReader(xml));
		Document doc = docBuilder.parse(docSource);
		return doc.getFirstChild();
	}

	// writes the given xml document to the specified file using Xerces
	// XMLSerializer
	private String writeXml(Document doc) throws Exception {
		OutputFormat of = new OutputFormat(doc);
		// we want the replaced xml in the same layout as the original
		of.setPreserveSpace(true);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		XMLSerializer serializer = new XMLSerializer(bos, of);
		serializer.serialize(doc);
		bos.flush();
		return bos.toString();
	}

//	private String writeXml(Document doc) throws Exception {
//		// Using the TrAX api for writing destroys the original xml layout.
//		// Different Prolog, disappearing DOCTYPE declarations, etc.
//		DOMSource domSource = new DOMSource(doc);
//		StreamResult target = new StreamResult();
//		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//		transformer.transform(domSource, target);
//		transformer.reset();
//		
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//		target.setOutputStream(bos);
//		return bos.toString();
//	}
}

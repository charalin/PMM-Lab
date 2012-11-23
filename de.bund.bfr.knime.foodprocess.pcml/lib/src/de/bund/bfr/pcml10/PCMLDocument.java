/*
 * An XML document type.
 * Localname: PCML
 * Namespace: http://www.bfr.bund.de/PCML-1_0
 * Java type: de.bund.bfr.pcml10.PCMLDocument
 *
 * Automatically generated - do not modify.
 */
package de.bund.bfr.pcml10;


/**
 * A document containing one PCML(@http://www.bfr.bund.de/PCML-1_0) element.
 *
 * This is a complex type.
 */
public interface PCMLDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(PCMLDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sFCF2A5D7EB02D4E76004908E4167DF4E").resolveHandle("pcmldea4doctype");
    
    /**
     * Gets the "PCML" element
     */
    de.bund.bfr.pcml10.PCMLDocument.PCML getPCML();
    
    /**
     * Sets the "PCML" element
     */
    void setPCML(de.bund.bfr.pcml10.PCMLDocument.PCML pcml);
    
    /**
     * Appends and returns a new empty "PCML" element
     */
    de.bund.bfr.pcml10.PCMLDocument.PCML addNewPCML();
    
    /**
     * An XML PCML(@http://www.bfr.bund.de/PCML-1_0).
     *
     * This is a complex type.
     */
    public interface PCML extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(PCML.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sFCF2A5D7EB02D4E76004908E4167DF4E").resolveHandle("pcml1f76elemtype");
        
        /**
         * Gets the "Header" element
         */
        de.bund.bfr.pcml10.HeaderDocument.Header getHeader();
        
        /**
         * Sets the "Header" element
         */
        void setHeader(de.bund.bfr.pcml10.HeaderDocument.Header header);
        
        /**
         * Appends and returns a new empty "Header" element
         */
        de.bund.bfr.pcml10.HeaderDocument.Header addNewHeader();
        
        /**
         * Gets the "ProcessChain" element
         */
        de.bund.bfr.pcml10.ProcessChainDocument.ProcessChain getProcessChain();
        
        /**
         * True if has "ProcessChain" element
         */
        boolean isSetProcessChain();
        
        /**
         * Sets the "ProcessChain" element
         */
        void setProcessChain(de.bund.bfr.pcml10.ProcessChainDocument.ProcessChain processChain);
        
        /**
         * Appends and returns a new empty "ProcessChain" element
         */
        de.bund.bfr.pcml10.ProcessChainDocument.ProcessChain addNewProcessChain();
        
        /**
         * Unsets the "ProcessChain" element
         */
        void unsetProcessChain();
        
        /**
         * Gets the "ProcessChainData" element
         */
        de.bund.bfr.pcml10.ProcessChainDataDocument.ProcessChainData getProcessChainData();
        
        /**
         * True if has "ProcessChainData" element
         */
        boolean isSetProcessChainData();
        
        /**
         * Sets the "ProcessChainData" element
         */
        void setProcessChainData(de.bund.bfr.pcml10.ProcessChainDataDocument.ProcessChainData processChainData);
        
        /**
         * Appends and returns a new empty "ProcessChainData" element
         */
        de.bund.bfr.pcml10.ProcessChainDataDocument.ProcessChainData addNewProcessChainData();
        
        /**
         * Unsets the "ProcessChainData" element
         */
        void unsetProcessChainData();
        
        /**
         * Gets array of all "Extension" elements
         */
        de.bund.bfr.pcml10.ExtensionDocument.Extension[] getExtensionArray();
        
        /**
         * Gets ith "Extension" element
         */
        de.bund.bfr.pcml10.ExtensionDocument.Extension getExtensionArray(int i);
        
        /**
         * Returns number of "Extension" element
         */
        int sizeOfExtensionArray();
        
        /**
         * Sets array of all "Extension" element
         */
        void setExtensionArray(de.bund.bfr.pcml10.ExtensionDocument.Extension[] extensionArray);
        
        /**
         * Sets ith "Extension" element
         */
        void setExtensionArray(int i, de.bund.bfr.pcml10.ExtensionDocument.Extension extension);
        
        /**
         * Inserts and returns a new empty value (as xml) as the ith "Extension" element
         */
        de.bund.bfr.pcml10.ExtensionDocument.Extension insertNewExtension(int i);
        
        /**
         * Appends and returns a new empty value (as xml) as the last "Extension" element
         */
        de.bund.bfr.pcml10.ExtensionDocument.Extension addNewExtension();
        
        /**
         * Removes the ith "Extension" element
         */
        void removeExtension(int i);
        
        /**
         * Gets the "version" attribute
         */
        java.lang.String getVersion();
        
        /**
         * Gets (as xml) the "version" attribute
         */
        org.apache.xmlbeans.XmlString xgetVersion();
        
        /**
         * Sets the "version" attribute
         */
        void setVersion(java.lang.String version);
        
        /**
         * Sets (as xml) the "version" attribute
         */
        void xsetVersion(org.apache.xmlbeans.XmlString version);
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static de.bund.bfr.pcml10.PCMLDocument.PCML newInstance() {
              return (de.bund.bfr.pcml10.PCMLDocument.PCML) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static de.bund.bfr.pcml10.PCMLDocument.PCML newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (de.bund.bfr.pcml10.PCMLDocument.PCML) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static de.bund.bfr.pcml10.PCMLDocument newInstance() {
          return (de.bund.bfr.pcml10.PCMLDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static de.bund.bfr.pcml10.PCMLDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (de.bund.bfr.pcml10.PCMLDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static de.bund.bfr.pcml10.PCMLDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (de.bund.bfr.pcml10.PCMLDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static de.bund.bfr.pcml10.PCMLDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (de.bund.bfr.pcml10.PCMLDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static de.bund.bfr.pcml10.PCMLDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.bund.bfr.pcml10.PCMLDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static de.bund.bfr.pcml10.PCMLDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.bund.bfr.pcml10.PCMLDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static de.bund.bfr.pcml10.PCMLDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.bund.bfr.pcml10.PCMLDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static de.bund.bfr.pcml10.PCMLDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.bund.bfr.pcml10.PCMLDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static de.bund.bfr.pcml10.PCMLDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.bund.bfr.pcml10.PCMLDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static de.bund.bfr.pcml10.PCMLDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.bund.bfr.pcml10.PCMLDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static de.bund.bfr.pcml10.PCMLDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.bund.bfr.pcml10.PCMLDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static de.bund.bfr.pcml10.PCMLDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.bund.bfr.pcml10.PCMLDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static de.bund.bfr.pcml10.PCMLDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (de.bund.bfr.pcml10.PCMLDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static de.bund.bfr.pcml10.PCMLDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (de.bund.bfr.pcml10.PCMLDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static de.bund.bfr.pcml10.PCMLDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (de.bund.bfr.pcml10.PCMLDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static de.bund.bfr.pcml10.PCMLDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (de.bund.bfr.pcml10.PCMLDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static de.bund.bfr.pcml10.PCMLDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (de.bund.bfr.pcml10.PCMLDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static de.bund.bfr.pcml10.PCMLDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (de.bund.bfr.pcml10.PCMLDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}

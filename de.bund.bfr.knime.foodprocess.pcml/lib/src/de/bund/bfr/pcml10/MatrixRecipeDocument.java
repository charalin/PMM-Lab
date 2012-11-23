/*
 * An XML document type.
 * Localname: MatrixRecipe
 * Namespace: http://www.bfr.bund.de/PCML-1_0
 * Java type: de.bund.bfr.pcml10.MatrixRecipeDocument
 *
 * Automatically generated - do not modify.
 */
package de.bund.bfr.pcml10;


/**
 * A document containing one MatrixRecipe(@http://www.bfr.bund.de/PCML-1_0) element.
 *
 * This is a complex type.
 */
public interface MatrixRecipeDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(MatrixRecipeDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sFCF2A5D7EB02D4E76004908E4167DF4E").resolveHandle("matrixreciped7a1doctype");
    
    /**
     * Gets the "MatrixRecipe" element
     */
    de.bund.bfr.pcml10.MatrixRecipeDocument.MatrixRecipe getMatrixRecipe();
    
    /**
     * Sets the "MatrixRecipe" element
     */
    void setMatrixRecipe(de.bund.bfr.pcml10.MatrixRecipeDocument.MatrixRecipe matrixRecipe);
    
    /**
     * Appends and returns a new empty "MatrixRecipe" element
     */
    de.bund.bfr.pcml10.MatrixRecipeDocument.MatrixRecipe addNewMatrixRecipe();
    
    /**
     * An XML MatrixRecipe(@http://www.bfr.bund.de/PCML-1_0).
     *
     * This is a complex type.
     */
    public interface MatrixRecipe extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(MatrixRecipe.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sFCF2A5D7EB02D4E76004908E4167DF4E").resolveHandle("matrixrecipe62d6elemtype");
        
        /**
         * Gets array of all "MatrixIncredient" elements
         */
        de.bund.bfr.pcml10.MatrixIncredientDocument.MatrixIncredient[] getMatrixIncredientArray();
        
        /**
         * Gets ith "MatrixIncredient" element
         */
        de.bund.bfr.pcml10.MatrixIncredientDocument.MatrixIncredient getMatrixIncredientArray(int i);
        
        /**
         * Returns number of "MatrixIncredient" element
         */
        int sizeOfMatrixIncredientArray();
        
        /**
         * Sets array of all "MatrixIncredient" element
         */
        void setMatrixIncredientArray(de.bund.bfr.pcml10.MatrixIncredientDocument.MatrixIncredient[] matrixIncredientArray);
        
        /**
         * Sets ith "MatrixIncredient" element
         */
        void setMatrixIncredientArray(int i, de.bund.bfr.pcml10.MatrixIncredientDocument.MatrixIncredient matrixIncredient);
        
        /**
         * Inserts and returns a new empty value (as xml) as the ith "MatrixIncredient" element
         */
        de.bund.bfr.pcml10.MatrixIncredientDocument.MatrixIncredient insertNewMatrixIncredient(int i);
        
        /**
         * Appends and returns a new empty value (as xml) as the last "MatrixIncredient" element
         */
        de.bund.bfr.pcml10.MatrixIncredientDocument.MatrixIncredient addNewMatrixIncredient();
        
        /**
         * Removes the ith "MatrixIncredient" element
         */
        void removeMatrixIncredient(int i);
        
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
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static de.bund.bfr.pcml10.MatrixRecipeDocument.MatrixRecipe newInstance() {
              return (de.bund.bfr.pcml10.MatrixRecipeDocument.MatrixRecipe) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static de.bund.bfr.pcml10.MatrixRecipeDocument.MatrixRecipe newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (de.bund.bfr.pcml10.MatrixRecipeDocument.MatrixRecipe) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static de.bund.bfr.pcml10.MatrixRecipeDocument newInstance() {
          return (de.bund.bfr.pcml10.MatrixRecipeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static de.bund.bfr.pcml10.MatrixRecipeDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (de.bund.bfr.pcml10.MatrixRecipeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static de.bund.bfr.pcml10.MatrixRecipeDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (de.bund.bfr.pcml10.MatrixRecipeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static de.bund.bfr.pcml10.MatrixRecipeDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (de.bund.bfr.pcml10.MatrixRecipeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static de.bund.bfr.pcml10.MatrixRecipeDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.bund.bfr.pcml10.MatrixRecipeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static de.bund.bfr.pcml10.MatrixRecipeDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.bund.bfr.pcml10.MatrixRecipeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static de.bund.bfr.pcml10.MatrixRecipeDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.bund.bfr.pcml10.MatrixRecipeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static de.bund.bfr.pcml10.MatrixRecipeDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.bund.bfr.pcml10.MatrixRecipeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static de.bund.bfr.pcml10.MatrixRecipeDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.bund.bfr.pcml10.MatrixRecipeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static de.bund.bfr.pcml10.MatrixRecipeDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.bund.bfr.pcml10.MatrixRecipeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static de.bund.bfr.pcml10.MatrixRecipeDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.bund.bfr.pcml10.MatrixRecipeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static de.bund.bfr.pcml10.MatrixRecipeDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.bund.bfr.pcml10.MatrixRecipeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static de.bund.bfr.pcml10.MatrixRecipeDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (de.bund.bfr.pcml10.MatrixRecipeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static de.bund.bfr.pcml10.MatrixRecipeDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (de.bund.bfr.pcml10.MatrixRecipeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static de.bund.bfr.pcml10.MatrixRecipeDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (de.bund.bfr.pcml10.MatrixRecipeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static de.bund.bfr.pcml10.MatrixRecipeDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (de.bund.bfr.pcml10.MatrixRecipeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static de.bund.bfr.pcml10.MatrixRecipeDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (de.bund.bfr.pcml10.MatrixRecipeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static de.bund.bfr.pcml10.MatrixRecipeDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (de.bund.bfr.pcml10.MatrixRecipeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}

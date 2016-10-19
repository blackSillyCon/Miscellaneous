 /////////////////////////////////////////////////////////////////////////
// Author: Coulibaly Falla Zoumana                                       //
// Date: 09/09/2016                                                      //
// Description: Simple web site scraper using HtmlCleaner and Xpath      //
 ////////////////////////////////////////////////////////////////////////
 
package /*Your package here!*/

import java.io.IOException;
import java.net.URL;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class JScraper {
    
    private XPath xPath;
    private Document document;
    private String regex;
    
    public JScraper(URL url)
    {
        Thread jsConstructor = new Thread(new JScraperConstructor(url));
        jsConstructor.start();
        try {
            jsConstructor.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("CallToPrintStackTrace")
    public NodeList findTags(String tagName, String attrName, String attrValue)
    {
        NodeList nodes = null;
        StringBuilder regexBuilder = new StringBuilder(255);
        
        /*Build a customised Xpath expression depending on the parameters*/
        regexBuilder.append("//").append(tagName);
        
        if( !(attrName == null || attrName.isEmpty())){
        /*Append attribute name to the search query*/
            regexBuilder.append("[@").append(attrName);
            
            /*Append attribute value to the search query*/
            if( !(attrValue == null || attrValue.isEmpty())){
                regexBuilder.append("=\'").append(attrValue).append("\'");
            }
            
            regexBuilder.append("]");
        }
        
        this.regex = regexBuilder.toString().replaceAll(" ", "");
        
        try {
            nodes = (NodeList) xPath.evaluate (regex, document, 
                    XPathConstants.NODESET);
            
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return nodes;
    }

    /*Handles initialisation of JScraper's fields since readDocument relies on the network
     *it must be ran separately because android does not allow network operation on the
     *main thread
     */ 
    private class JScraperConstructor implements Runnable {
        URL url;

        public JScraperConstructor(URL url)
        {
            this.url = url;
        }

        public void run()
        {
            JScraper.this.document = readDocument(url);
            JScraper.this.xPath = XPathFactory.newInstance().newXPath();
        }

        private Document readDocument(URL url)
        {
            Document doc = null;
            TagNode tagNode = null;

            try {
                /*HtmlCleaner retrieves/handles documents(malformed)*/
                tagNode = new HtmlCleaner().clean(url);
                doc = new DomSerializer(new CleanerProperties()).createDOM(tagNode);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }

            return doc;
        }
    }
}

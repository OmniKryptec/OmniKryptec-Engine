package omnikryptec.util;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import omnikryptec.logger.Logger;

/**
 *
 * @author Panzer1119
 */
public class XMLUtil {

    public static final SAXBuilder saxBuilder = new SAXBuilder();
    public static final XMLOutputter xmlOutput = new XMLOutputter();
    
    public static final Element getChild(Element element, String childName) {
        for(Element child : element.getChildren()) {
            if(child.getName().equals(childName)) {
                return child;
            }
        }
        return null;
    }
    
    public static final List<Element> getChildren(Element element, String childName) {
        return element.getChildren().stream().filter((child) -> child.getName().equals(childName)).collect(Collectors.toList());
    }
    
    public static final Element getChildWithAttribute(Element element, String childName, String attributeName, String attributeValue) {
        final List<Element> children = getChildren(element, childName);
        if(children == null || children.isEmpty()) {
            return null;
        }
        for(Element child : children) {
            String value = child.getAttributeValue(attributeName);
            if(value != null && value.equals(attributeValue)) {
                return child;
            }
        }
        return null;
    }
    
    public static final Document getDocument(InputStream inputStream) {
        try {
            return saxBuilder.build(inputStream);
        } catch (Exception ex) {
            Logger.logErr("Error while loading XML-File: " + ex, ex);
            return null;
        }
    }
    
}

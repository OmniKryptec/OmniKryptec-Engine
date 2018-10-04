/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.old.util;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import de.omnikryptec.old.util.logger.Logger;

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

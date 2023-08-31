package xml;

import java.util.ArrayList;

import org.xml.sax.Attributes;

/*
 * @Author: Joao
 * @Date: 2023/05/19
 */
public class Document {
	private ArrayList<Element> elements = new ArrayList<>();
	
	public Element addElement(String name, Attributes attributes) {
        Element element = new Element(name, attributes);
        this.elements.add(element);
        
        return element;
    }
}

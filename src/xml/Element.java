package xml;

import java.util.ArrayList;

import org.xml.sax.Attributes;

/*
 * @Author: Joao
 * @Date: 2023/05/19
 */
public class Element {
	private String name;
	private String content;
	private ArrayList<Attribute> attrs = new ArrayList<>();
	private Integer childCount = 0;
	
	public Element(String name, Attributes attributes) {
        this.name = name;
        for (int i = 0; i < attributes.getLength(); i++) {
            this.attrs.add(new Attribute(attributes.getQName(i), attributes.getValue(i)));
        }
    }
	
	public String getName() {
		return this.name;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public Element addChild(String name, Attributes attributes, Element parent) {
		this.childCount ++;
		Element element = new Element(name, attributes);
		
        return element;
	}
	
	public int getChildCount() {
		return this.childCount;
	}
	
	public int increaseChildCount() {
		return this.childCount ++;
	}
	
	@Override
	public String toString() {
		if (this.content == null || this.content.isEmpty()) {
			if (this.childCount == 0)
				return this.name + ": null";
			
			return this.name;
		}
		
		return this.name + ": \"" + this.content + "\"";
	}
}

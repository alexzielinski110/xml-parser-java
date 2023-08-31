package xml;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/*
 * @Author: Joao
 * @Date: 2023/05/19
 */
public class Parser extends DefaultHandler {
	private Document doc = new Document();
	private Stack<Element> stackElement = new Stack<>();
    private Element currentElement, parentElement, prevParentElement;
    
    private ArrayList<ArrayList<Attribute>> cacheElements = new ArrayList<ArrayList<Attribute>>();
    private ArrayList<Attribute> cacheAttrs = new ArrayList<Attribute>();
    
    public static Document parse(File file) {
    	Parser xmlParser = new Parser();
    	
        return xmlParser.parseXML(file);
    }
    
    private Document parseXML(File file) {
    	SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        try {
        	SAXParser saxParser = saxFactory.newSAXParser();
			saxParser.parse(file, this);
			
			return this.doc;
		} catch (Exception e) {
            throw new RuntimeException(e);
        }
	}
    
    public void printcacheElements() {
    	String unique = this.determineUniqueAttr();
    	for (ArrayList<Attribute> attrs: this.cacheElements) {
    		StringBuilder sb = new StringBuilder();
    		for (Attribute attr: attrs) {
    			sb.append("   " + attr.toString() + "\n");
    			if (attr.getName().equals(unique)) {
    				sb.insert(0, this.prevParentElement.getName() + "(unique property to identify: " + unique + "=\"" + attr.getValue() + "\")\n");
    			}
    		}
    		
    		System.out.println(sb.toString());
    	}
    	
    	this.cacheElements.clear();
    }
    
    @SuppressWarnings("rawtypes")
	public String determineUniqueAttr() {
    	LinkedHashMap<String, Integer> frequency = new LinkedHashMap<String, Integer>();
    	
    	for (ArrayList<Attribute> attrs: this.cacheElements) {
    		for (Attribute attr: attrs) {
    			String key = attr.getName() + ";" + attr.getValue();
    			Integer value = frequency.get(key);
    			frequency.put(key, value == null ? 1 : value + 1);
    		}
    	}
    	
    	Iterator it = frequency.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			
			String key = (String)pair.getKey();
			Integer cnt = (Integer)pair.getValue();
			if (cnt == 1) {
				String[] arr = key.split(";");
				if (arr.length < 2) continue;
				
				String regex = "^.*[a-zA-Z0-9]+.*$";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(arr[1]);
				if (matcher.matches()) {
					return arr[0];
				}
			}
			
			it.remove();
		}
		
		return null;
    }
	
	@Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (this.currentElement == null) {
            this.currentElement = this.doc.addElement(qName, attributes);
        } else {
            this.currentElement = this.currentElement.addChild(qName, attributes, this.currentElement);
        }
        
        this.stackElement.push(this.currentElement);
    }
	
	@Override
    public void endElement(String uri, String localName, String qName) {
		this.stackElement.pop();
        
        Integer childCount = this.currentElement.getChildCount();
        if (childCount == 0) {
        	this.cacheAttrs.add(new Attribute(this.currentElement.getName(), this.currentElement.getContent()));
        }
        
        if (!this.stackElement.isEmpty()) {
            this.currentElement = this.stackElement.peek();
            
            if (this.parentElement == null) {
            	if (childCount - this.currentElement.getChildCount() < 0) {
            		this.parentElement = this.currentElement;
            	}
            } else if (this.parentElement.getName() != this.currentElement.getName()
        		&& !this.cacheAttrs.isEmpty()) {
        		if (this.prevParentElement != null
    				&& this.prevParentElement.getName() != this.parentElement.getName()) {
        			this.printcacheElements();
        			this.prevParentElement = null;
        		}
        		
        		this.cacheElements.add(new ArrayList<Attribute>(this.cacheAttrs));
        		this.cacheAttrs.clear();
        		this.prevParentElement = this.parentElement;
        		this.parentElement = null;
            }
        } else if (!this.cacheElements.isEmpty()) {
        	this.printcacheElements();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        String content = new String(ch, start, length).trim();
        this.currentElement.setContent(content);
    }
}

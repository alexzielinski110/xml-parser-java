package xml;

/*
 * @Author: Joao
 * @Date: 2023/05/19
 */
public class Attribute {
	private String name;
	private String value;
	
	public Attribute(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getValue() {
		return this.value;
	}
	
	@Override
	public String toString() {
		return this.name + ": \"" + this.value + "\"";
	}
}

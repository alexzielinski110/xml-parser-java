import java.io.File;

import xml.Parser;

public class Main {
	public static void main(String[] args) {
		File xml = new File("data.xml");
		
		Parser.parse(xml);
	}
}

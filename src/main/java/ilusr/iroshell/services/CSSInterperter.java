package ilusr.iroshell.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a helper class that reads in a css string or file and
 * turns that file or string into an object that lets you alter and
 * inspect css.
 * 
 * @author Jeffrey Riggle
 *
 */
//TODO: add support for class,class{} and id,id{}
public class CSSInterperter {

	private final String CSS_PROPERTY_FINDER = "([^:\n]*):([^;]*);";
	private final String CSS_CLASS_FINDER = "\\.([^\\{]*)\\{([^\\}]*)\\}";
	private final String CSS_ID_FINDER = "#([^\\{]*)\\{([^\\}]*)\\}";
	
	private String css;
	private Map<String, Map<String, String>> classes;
	private Map<String, Map<String, String>> ids;
	private Map<String, String> unparented;
	
	/**
	 * 
	 * @param css A string representing a body of css.
	 */
	public CSSInterperter(String css) {
		this();
		this.css = css;
	}
	
	/**
	 * 
	 * @param cssFile A css file to be inteperted.
	 * @throws IOException In the case that the file cannot be read.
	 * @throws IllegalArgumentException If the file is not a valid css or less file.
	 */
	public CSSInterperter(File cssFile) throws IOException, IllegalArgumentException {
		this();
		setCSS(cssFile);
	}
	
	//TODO: Consider making this into a FileUtilies class in core.
	private String getExtension(File file) {
		int sep = file.getPath().lastIndexOf('.');
		return file.getPath().substring(sep + 1);
	}
	
	private String getFileContent(File file) throws IOException {
		String output = new String();
		FileInputStream inputStream = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line = new String();
		
		while((line = reader.readLine()) != null) {
			output += line;
		}
		
		reader.close();
		return output;
	}
	
	/**
	 * Base Ctor.
	 */
	public CSSInterperter() {
		//TODO: Should these be hash maps?
		classes = new HashMap<String, Map<String, String>>();
		ids = new HashMap<String, Map<String, String>>();
		unparented = new HashMap<String, String>();
	}
	
	/**
	 * Changes the interperted css and resets all maps.
	 * @param css The new css content to use.
	 */
	public void setCSS(String css) {
		classes.clear();
		ids.clear();
		unparented.clear();
		this.css = css;
	}
	
	/**
	 * Changes the interperted css and resets all maps.
	 * @param cssFile A css file to be inteperted.
	 * @throws IOException In the case that the file cannot be read.
	 * @throws IllegalArgumentException If the file is not a valid css or less file.
	 **/
	public void setCSS(File cssFile) throws IOException, IllegalArgumentException {
		if (!getExtension(cssFile).equalsIgnoreCase("css") && !getExtension(cssFile).equalsIgnoreCase("less")) {
			throw new IllegalArgumentException("File must be a css file");
		}
		
		String css = getFileContent(cssFile);
		setCSS(css);
	}
	
	/**
	 * 
	 * @return The uninteperted css content.
	 */
	public String getCss() {
		return css;
	}
	
	/**
	 * Interperts the css and builds out css maps.
	 */
	public void interpertCSS() {
		generateClassMap();
		generateIdMap();
		String unPareneted = generateUnparentedString();
		unparented = generateUnparentedMap(unPareneted);
	}
	
	private void generateClassMap() {
		Pattern classPattern = Pattern.compile(CSS_CLASS_FINDER);
		Matcher classMatcher = classPattern.matcher(css);
		while(classMatcher.find()) {
			String cssClass = classMatcher.group(1);
			Map<String, String> properties = generateUnparentedMap(classMatcher.group(2));
			
			classes.put(cssClass, properties);
		}
	}
	
	private void generateIdMap() {
		Pattern idPattern = Pattern.compile(CSS_ID_FINDER);
		Matcher idMatcher = idPattern.matcher(css);
		while(idMatcher.find()) {
			String cssId = idMatcher.group(1);
			Map<String, String> properties = generateUnparentedMap(idMatcher.group(2));
			
			ids.put(cssId, properties);
		}
	}
	
	private String generateUnparentedString() {
		String retVal = new String();
		
		retVal = css.replaceAll(CSS_CLASS_FINDER, "");
		retVal = retVal.replaceAll(CSS_ID_FINDER, "");
		return retVal;
	}
	
	private Map<String, String> generateUnparentedMap(String css) {
		Map<String, String> retVal = new HashMap<String, String>();
		Pattern propertyPattern = Pattern.compile(CSS_PROPERTY_FINDER);
		Matcher propertyMatcher = propertyPattern.matcher(css);
		
		while(propertyMatcher.find()) {
			retVal.put(propertyMatcher.group(1), propertyMatcher.group(2));
		}
		
		return retVal;
	}
	
	/**
	 * 
	 * @return A map of class name to class properties.
	 */
	public Map<String, Map<String, String>> classMap() {
		return classes;
	}
	
	/**
	 * 
	 * @return A map of id name to id properties.
	 */
	public Map<String, Map<String, String>> idMap() {
		return ids;
	}
	
	/**
	 * 
	 * @return A map of property name to property value.
	 */
	public Map<String, String> unparentedMap() {
		return unparented;
	}
	
	/**
	 * 
	 * @return The new css as determined by the maps.
	 */
	public String getCSSString() {
		StringBuilder sb = new StringBuilder();
		
		for(Entry<String, Map<String, String>> clz : classes.entrySet()) {
			sb.append(String.format(".%s{\n%s\n}\n", clz.getKey(), generateProperties(clz.getValue())));
		}
		
		for(Entry<String, Map<String, String>> id : ids.entrySet()) {
			sb.append(String.format("#%s{\n%s\n}\n", id.getKey(), generateProperties(id.getValue())));
		}

		for(Entry<String, String> prop : unparented.entrySet()) {
			sb.append(String.format("%s:%s;\n", prop.getKey(), prop.getValue()));
		}

		return sb.toString();
	}
	
	private String generateProperties(Map<String, String> props) {
		StringBuilder sb = new StringBuilder();
		
		for(Entry<String, String> prop : props.entrySet()) {
			sb.append(String.format("\t%s:%s;\n", prop.getKey(), prop.getValue()));
		}
		
		return sb.toString();
	}
}

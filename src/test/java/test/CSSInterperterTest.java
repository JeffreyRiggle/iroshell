package test;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import ilusr.iroshell.services.CSSInterperter;

public class CSSInterperterTest {

	private static String UNPARENTED_CSS = "-fx-background-color:green;-fx-font-family:\"Arial\";width:80px;";
	private static String UNPARENTED_MULTI_LINE_CSS = "-fx-background-color:green;\n-fx-font-family:\"Arial\";\nwidth:80px;\n";

	private static String CLASS_CSS = ".testClass{-fx-background-color:green;-fx-font-family:\"Arial\";width:80px;}";
	private static String MULTI_CLASS_CSS = ".class1{\nwidth:80px;\n}\n.testClass{\n-fx-background-color:green;\n-fx-font-family:\"Arial\";\nwidth:80px;\n}";

	private static String ID_CSS = "#id{-fx-background-color:green;width:80px;height:40pt;}";
	private static String MULTI_ID_CSS = "#id{\n-fx-background-color:green;\nwidth:80px;\nheight:40pt;\n}\n#other{\n-fx-background-color:red;\n}";

	private static String MULTI_TYPE_CSS = ".testClass{\n-fx-background-color:green;\n-fx-font-family:\"Arial\";\nwidth:80px;\n}\n#id{\n-fx-background-color:green;\nwidth:80px;\nheight:40pt;\n}z-index:1000;";
	
	@Test
	public void testCreate() {
		CSSInterperter interperter = new CSSInterperter();
		assertNotNull(interperter);
		assertNotNull(interperter.classMap());
		assertNotNull(interperter.idMap());
		assertNotNull(interperter.unparentedMap());
	}

	@Test
	public void testCSSCreate() {
		CSSInterperter interperter = new CSSInterperter(UNPARENTED_CSS);
		assertNotNull(interperter);
		assertNotNull(interperter.classMap());
		assertNotNull(interperter.idMap());
		assertNotNull(interperter.unparentedMap());
	}
	
	@Test
	public void testInterpertUnparented() {
		CSSInterperter interperter = new CSSInterperter(UNPARENTED_CSS);
		
		interperter.interpertCSS();
		assertEquals(3, interperter.unparentedMap().size());
		assertTrue(interperter.unparentedMap().containsKey("-fx-background-color"));
		assertEquals("green", interperter.unparentedMap().get("-fx-background-color"));
		assertTrue(interperter.unparentedMap().containsKey("-fx-font-family"));
		assertEquals("\"Arial\"", interperter.unparentedMap().get("-fx-font-family"));
		assertTrue(interperter.unparentedMap().containsKey("width"));
		assertEquals("80px", interperter.unparentedMap().get("width"));
	}
	
	@Test
	public void testInterpertUnparentedMultiLine() {
		CSSInterperter interperter = new CSSInterperter(UNPARENTED_MULTI_LINE_CSS);
		
		interperter.interpertCSS();
		assertEquals(3, interperter.unparentedMap().size());
		assertTrue(interperter.unparentedMap().containsKey("-fx-background-color"));
		assertEquals("green", interperter.unparentedMap().get("-fx-background-color"));
		assertTrue(interperter.unparentedMap().containsKey("-fx-font-family"));
		assertEquals("\"Arial\"", interperter.unparentedMap().get("-fx-font-family"));
		assertTrue(interperter.unparentedMap().containsKey("width"));
		assertEquals("80px", interperter.unparentedMap().get("width"));
	}
	
	@Test
	public void testInterpertClass() {
		CSSInterperter interperter = new CSSInterperter(CLASS_CSS);
		
		interperter.interpertCSS();
		assertEquals(1, interperter.classMap().size());
		assertTrue(interperter.classMap().containsKey("testClass"));
		assertEquals(3, interperter.classMap().get("testClass").size());
	}
	
	@Test
	public void testInterpertMultiClass() {
		CSSInterperter interperter = new CSSInterperter(MULTI_CLASS_CSS);
		
		interperter.interpertCSS();
		assertEquals(2, interperter.classMap().size());
		assertTrue(interperter.classMap().containsKey("testClass"));
		assertEquals(3, interperter.classMap().get("testClass").size());
		
		assertTrue(interperter.classMap().containsKey("class1"));
		assertEquals(1, interperter.classMap().get("class1").size());
	}
	
	@Test
	public void testInterpertId() {
		CSSInterperter interperter = new CSSInterperter(ID_CSS);
		
		interperter.interpertCSS();
		assertEquals(1, interperter.idMap().size());
		assertTrue(interperter.idMap().containsKey("id"));
		assertEquals(3, interperter.idMap().get("id").size());
	}
	
	@Test
	public void testInterpertMultiId() {
		CSSInterperter interperter = new CSSInterperter(MULTI_ID_CSS);
		
		interperter.interpertCSS();
		assertEquals(2, interperter.idMap().size());
		assertTrue(interperter.idMap().containsKey("id"));
		assertEquals(3, interperter.idMap().get("id").size());
		
		assertTrue(interperter.idMap().containsKey("other"));
		assertEquals(1, interperter.idMap().get("other").size());
	}
	
	@Test
	public void testInterpertMixed() {
		CSSInterperter interperter = new CSSInterperter(MULTI_TYPE_CSS);
		
		interperter.interpertCSS();
		
		assertEquals(1, interperter.classMap().size());
		assertEquals(1, interperter.idMap().size());
		assertEquals(1, interperter.unparentedMap().size());
	}
	
	@Test
	public void testSetCSS() {
		CSSInterperter interperter = new CSSInterperter(MULTI_TYPE_CSS);
		
		interperter.interpertCSS();
		
		interperter.setCSS(UNPARENTED_CSS);
		assertEquals(UNPARENTED_CSS, interperter.getCss());
		assertEquals(0, interperter.classMap().size());
		assertEquals(0, interperter.idMap().size());
		assertEquals(0, interperter.unparentedMap().size());
	}
	
	@Test
	public void testSetCSSFromFile() {
		CSSInterperter interperter = new CSSInterperter();
		
		try {
			File cssFile = new File(getClass().getResource("testcssfile.css").toURI().getSchemeSpecificPart());
			interperter.setCSS(cssFile);
			
			interperter.interpertCSS();
			
			assertEquals(1, interperter.classMap().size());
			assertEquals(1, interperter.idMap().size());
			assertEquals(1, interperter.unparentedMap().size());
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	@Test
	public void testSetCSSWithInvalidFile() {
		boolean errored = false;
		try {
			CSSInterperter interperter = new CSSInterperter();
			File cssFile = new File(CSSInterperterTest.class.getResource("teststyle.bad").toURI().getSchemeSpecificPart());
		
			interperter.setCSS(cssFile);
		} catch (Exception e) {
			errored = true;
		}
		
		assertTrue(errored);
	}
	
	@Test
	public void testCreateWithCSSFile() {
		try {
			File cssFile = new File(getClass().getResource("testcssfile.css").toURI().getSchemeSpecificPart());
			CSSInterperter interperter = new CSSInterperter(cssFile);
			
			interperter.interpertCSS();
			
			assertEquals(1, interperter.classMap().size());
			assertEquals(1, interperter.idMap().size());
			assertEquals(1, interperter.unparentedMap().size());
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	@Test
	public void testCreateWithInvalidCSSFile() {
		boolean errored = false;
		File cssFile = new File(getClass().getResource("teststyle.bad").toString());
		
		try {
			@SuppressWarnings("unused")
			CSSInterperter interperter = new CSSInterperter(cssFile);
		} catch (Exception e) {
			errored = true;
		}
		
		assertTrue(errored);
	}
}

package org.eclipse.epf.export.pattern.domain;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name = "pattern")
@XmlAccessorType(XmlAccessType.FIELD)
public class PatternProject {

	private String name;

	private HashMap<String, PatternWorkProduct> patternWorkProducts = new HashMap<String, PatternWorkProduct>();

	private HashMap<String, PatternTask> patternTasks = new HashMap<String, PatternTask>();

	private HashMap<String, PatternRole> patternRoles = new HashMap<String, PatternRole>();

	private HashMap<String, PatternPhase> patternPhases = new HashMap<String, PatternPhase>();

	private boolean isPattern = false;

	public HashMap<String, PatternWorkProduct> getPatternWorkProducts() {
		return patternWorkProducts;
	}

	public void setPatternWorkProducts(HashMap<String, PatternWorkProduct> patternWorkProducts) {
		this.patternWorkProducts = patternWorkProducts;
	}

	public HashMap<String, PatternTask> getPatternTasks() {
		return patternTasks;
	}

	public void setPatternTasks(HashMap<String, PatternTask> patternTasks) {
		this.patternTasks = patternTasks;
	}

	public HashMap<String, PatternRole> getPatternRoles() {
		return patternRoles;
	}

	public void setPatternRoles(HashMap<String, PatternRole> patternRoles) {
		this.patternRoles = patternRoles;
	}

	public HashMap<String, PatternPhase> getPatternPhases() {
		return patternPhases;
	}

	public void setPatternPhases(HashMap<String, PatternPhase> patternPhases) {
		this.patternPhases = patternPhases;
	}

	public boolean isPattern() {
		return isPattern;
	}

	public void setPattern(boolean isPattern) {
		this.isPattern = isPattern;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

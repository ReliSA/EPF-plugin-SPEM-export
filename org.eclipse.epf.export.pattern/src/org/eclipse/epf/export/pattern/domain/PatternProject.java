package org.eclipse.epf.export.pattern.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.epf.uma.Discipline;

import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name = "pattern")
@XmlAccessorType (XmlAccessType.FIELD)
public class PatternProject {
	
	@XmlElement(name = "work_product")
	private List<PatternWorkProduct> patternWorkProductList = new ArrayList<PatternWorkProduct>();
	
	@XmlElement(name = "task")
	private List<PatternTask> PatternTaskList = new ArrayList<PatternTask>();
	
	@XmlElement(name = "role")
	private List<PatternRole> PatternRolesList = new ArrayList<PatternRole>();
	
	@XmlElement(name = "phases")
	private List<PatternPhase> PatternPhasesList = new ArrayList<PatternPhase>();
	
	@XmlElement(name = "disciplines")
	private List<PatternDiscipline> patternDisciplinesList = new ArrayList<PatternDiscipline>();

	public List<PatternWorkProduct> getPatternWorkProductList() {
		return patternWorkProductList;
	}

	public void setPatternWorkProductList(List<PatternWorkProduct> patternWorkProductList) {
		this.patternWorkProductList = patternWorkProductList;
	}

	public List<PatternTask> getPatternTaskList() {
		return PatternTaskList;
	}

	public void setPatternTaskList(List<PatternTask> patternTaskList) {
		PatternTaskList = patternTaskList;
	}

	public List<PatternRole> getPatternRolesList() {
		return PatternRolesList;
	}

	public void setPatternRolesList(List<PatternRole> patternRolesList) {
		PatternRolesList = patternRolesList;
	}

	public List<PatternPhase> getPatternPhasesList() {
		return PatternPhasesList;
	}

	public void setPatternPhasesList(List<PatternPhase> patternPhasesList) {
		PatternPhasesList = patternPhasesList;
	}
	
}

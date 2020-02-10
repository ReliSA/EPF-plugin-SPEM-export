package org.eclipse.epf.export.pattern.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "phase")
@XmlAccessorType (XmlAccessType.FIELD)
public class PatternPhase {
	
	@XmlElement(name = "name")
	private String name;
	
	@XmlElement(name = "tasks")
	private List<PatternTask> phaseTasks;

	public List<PatternTask> getPhaseTasks() {
		return phaseTasks;
	}

	public void setPhaseTasks(List<PatternTask> phaseTasks) {
		this.phaseTasks = phaseTasks;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}

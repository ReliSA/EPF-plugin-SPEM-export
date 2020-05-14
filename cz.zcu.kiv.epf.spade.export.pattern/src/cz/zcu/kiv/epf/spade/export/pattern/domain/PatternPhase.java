package cz.zcu.kiv.epf.spade.export.pattern.domain;

import java.util.List;

public class PatternPhase {
	
	private String name;
	
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

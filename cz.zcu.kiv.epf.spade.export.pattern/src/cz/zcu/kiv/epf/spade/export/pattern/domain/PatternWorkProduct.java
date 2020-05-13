package cz.zcu.kiv.epf.spade.export.pattern.domain;

public class PatternWorkProduct implements Descriptable {
	
	private String guid;
	
	private String name;
	
	private PatternTask task;
	
	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PatternTask getTask() {
		return task;
	}

	public void setTask(PatternTask task) {
		this.task = task;
	}

	enum WorkProductTypeEnum {
		
	}

}

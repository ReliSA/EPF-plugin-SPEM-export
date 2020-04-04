package org.eclipse.epf.export.pattern.domain;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.epf.export.pattern.ExportPatternLogger;

@XmlRootElement(name = "work_product")
@XmlAccessorType (XmlAccessType.FIELD)
public class PatternWorkProduct implements Descriptable {
	
	@XmlElement(name = "guid")
	private String guid;
	
	@XmlElement(name = "name")
	private String name;
	
	private String[] tokens;
	
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
	
	public String[] getTokens() {
		return tokens;
	}

	public void setTokens(String[] tokens) {
		this.tokens = tokens;
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

package org.eclipse.epf.export.pattern.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "work_product")
@XmlAccessorType (XmlAccessType.FIELD)
public class PatternWorkProduct {
	
	@XmlElement(name = "guid")
	private String guid;
	
	@XmlElement(name = "name")
	private String name;
	
	@XmlElement(name = "main_description")
	private String mainDescription;

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

	public String getMainDescription() {
		return mainDescription;
	}

	public void setMainDescription(String mainDescription) {
		this.mainDescription = mainDescription;
	}

}

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
	
	private String[] tokens;

	private String amount;
	
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

	public void setMainDescription(String mainDescription) {
		String[] lines = mainDescription.split(System.getProperty("line.separator"));
		for (String line : lines) {
			if (line.startsWith("keywords")) {
				this.setTokens(line.split("=")[1].split(","));
			} else if (line.startsWith("amount")) {
				this.setAmount(line);
			} else {
				// TODO
			}
		}
	}
	
	public String[] getTokens() {
		return tokens;
	}

	public void setTokens(String[] tokens) {
		this.tokens = tokens;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	enum WorkProductTypeEnum {
		
	}

}

package org.eclipse.epf.export.pattern.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "task")
@XmlAccessorType (XmlAccessType.FIELD)
public class PatternTask {
	
	@XmlElement(name = "guid")
	private String guid;
	
	@XmlElement(name = "name")
	private String name;

	@XmlElement(name = "main_description")
	private String mainDescription;
	
	@XmlElement(name = "inputs")
	private List<PatternWorkProduct> inputs = new ArrayList<PatternWorkProduct>();
	
	@XmlElement(name = "optional_inputs")
	private List<PatternWorkProduct> optionalInputs = new ArrayList<PatternWorkProduct>();
	
	@XmlElement(name = "outputs")
	private List<PatternWorkProduct> outputs = new ArrayList<PatternWorkProduct>();
	
	@XmlElement(name = "phase")
	private PatternPhase phase;
	
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

	public List<PatternWorkProduct> getInputs() {
		return inputs;
	}

	public void setInputs(List<PatternWorkProduct> inputs) {
		this.inputs = inputs;
	}

	public List<PatternWorkProduct> getOutputs() {
		return outputs;
	}

	public void setOutputs(List<PatternWorkProduct> outputs) {
		this.outputs = outputs;
	}

	public List<PatternWorkProduct> getOptionalInputs() {
		return optionalInputs;
	}

	public void setOptionalInputs(List<PatternWorkProduct> optionalInputs) {
		this.optionalInputs = optionalInputs;
	}

	public PatternPhase getPhase() {
		return phase;
	}

	public void setPhase(PatternPhase phase) {
		this.phase = phase;
	}

}

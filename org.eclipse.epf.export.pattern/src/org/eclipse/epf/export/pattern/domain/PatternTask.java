package org.eclipse.epf.export.pattern.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.epf.export.pattern.ExportPatternLogger;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;

public class PatternTask implements Descriptable {
	
	private String guid;
	
	private String name;
	
	private String amount;

	private String[] tokens;
	
	private List<PatternWorkProduct> inputs = new ArrayList<PatternWorkProduct>();
	
	private List<PatternWorkProduct> optionalInputs = new ArrayList<PatternWorkProduct>();
	
	private List<PatternWorkProduct> outputs = new ArrayList<PatternWorkProduct>();
	
	private List<PatternRole> performers = new ArrayList<PatternRole>();
	
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

	public List<PatternRole> getPerformers() {
		return performers;
	}

	public void setPerformers(List<PatternRole> performers) {
		this.performers = performers;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String[] getTokens() {
		return tokens;
	}

	public void setTokens(String[] tokens) {
		this.tokens = tokens;
	}

}

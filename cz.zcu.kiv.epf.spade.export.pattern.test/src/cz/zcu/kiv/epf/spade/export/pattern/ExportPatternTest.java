package cz.zcu.kiv.epf.spade.export.pattern;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import cz.zcu.kiv.epf.spade.export.pattern.ExportPatternSQLService;
import cz.zcu.kiv.epf.spade.export.pattern.domain.PatternArtifact;
import cz.zcu.kiv.epf.spade.export.pattern.domain.PatternOutcome;
import cz.zcu.kiv.epf.spade.export.pattern.domain.PatternProject;
import cz.zcu.kiv.epf.spade.export.pattern.domain.PatternRole;
import cz.zcu.kiv.epf.spade.export.pattern.domain.PatternTask;
import cz.zcu.kiv.epf.spade.export.pattern.domain.PatternWorkProduct;

public class ExportPatternTest {
	
	static ExportPatternSQLService exportPatternSQLService;
	static ExportPatternMapService exportPatternMapService;
	
	
	@Before
    public void initAll() {
		exportPatternSQLService = ExportPatternSQLService.getInstance(null, null);
		exportPatternMapService = ExportPatternMapService.getInstance(null);
    }
	
	@Test
    public void addRoleConstraint1() {
		List<String> conditions = new ArrayList<>();
		List<String> joins = new ArrayList<>();
		List<PatternRole> roles = new ArrayList<>();
		PatternRole role = new PatternRole();
		role.setName("developer");
		roles.add(role);
		exportPatternSQLService.addRoleConstraint(conditions, joins, roles);
		
		boolean contains = false;
		for(String curr: conditions) {
		    if(curr.equals("AND (rc.class = 'DEVELOPER')")) {
		       contains = true;	
		    }
		}
		
		assertTrue(contains);
    }
	
	@Test
    public void addRoleConstraint2() {
		List<String> conditions = new ArrayList<>();
		List<String> joins = new ArrayList<>();
		List<PatternRole> roles = new ArrayList<>();
		PatternRole role = new PatternRole();
		role.setName("project manager");
		roles.add(role);
		exportPatternSQLService.addRoleConstraint(conditions, joins, roles);
		
		boolean contains = false;
		for(String curr: conditions) {
		    if(curr.equals("AND (rc.class = 'PROJECTMANAGER')")) {
		       contains = true;
		    }
		}
		
		assertTrue(contains);
    }
	
	@Test
    public void addRateConditionTest1() {
		String amount = "amount<this.type/6";
		String addRateCondition = exportPatternSQLService.addRateCondition(amount);
		assertTrue(addRateCondition.equals("\nWHERE a.sum < (b.sum/6)"));
    }
	
	@Test
    public void addRateConditionTest2() {
		String amount = "amount > this.type / 20";
		String addRateCondition = exportPatternSQLService.addRateCondition(amount);
		assertTrue(addRateCondition.equals("\nWHERE a.sum > (b.sum/20)"));
    }
	
	@Test
    public void addNameConstraint1() {
		List<String> conditions = new ArrayList<>();
		String[] tokens = new String[1];
		tokens[0] = "specif";
		exportPatternSQLService.addNameConstraint(conditions, tokens);
		
		boolean contains = false;
		for(String curr: conditions) {
		    if(curr.equals("AND (wi.name LIKE '%specif%')")) {
		       contains = true;
		    }
		}
		
		assertTrue(contains);
    }
	
	@Test
    public void addNameConstraint2() {
		List<String> conditions = new ArrayList<>();
		String[] tokens = new String[2];
		tokens[0] = "specif";
		tokens[1] = "zapis%schuz";
		exportPatternSQLService.addNameConstraint(conditions, tokens);
		
		boolean contains = false;
		for(String curr: conditions) {
		    if(curr.equals("AND (wi.name LIKE '%specif%' OR wi.name LIKE '%zapis%schuz%')")) {
		       contains = true;
		    }
		}
		
		assertTrue(contains);
    }
	
	@Test
    public void addTypeConstraint1() {
		List<String> conditions = new ArrayList<>();
		List<PatternWorkProduct> wp = new ArrayList<>();
		exportPatternSQLService.addTypeConstraint(conditions, wp);
		
		boolean contains = false;
		for(String curr: conditions) {
		    if(curr.equals("AND wi.workItemType = 'WORK_UNIT'")) {
		       contains = true;
		    }
		}
		
		assertTrue(contains);
    }
	
	@Test
    public void addTypeConstraint2() {
		List<String> conditions = new ArrayList<>();
		List<PatternWorkProduct> wp = new ArrayList<>();
		wp.add(new PatternArtifact());
		exportPatternSQLService.addTypeConstraint(conditions, wp);
		
		boolean contains = false;
		for(String curr: conditions) {
		    if(curr.equals("AND wi.workItemType = 'ARTIFACT'")) {
		       contains = true;
		    }
		}
		
		assertTrue(contains);
    }

	@Test
    public void addTypeConstraint3() {
		List<String> conditions = new ArrayList<>();
		List<PatternWorkProduct> wp = new ArrayList<>();
		PatternOutcome outcome = new PatternOutcome();
		outcome.setType("COMMIT");
		wp.add(outcome);
		exportPatternSQLService.addTypeConstraint(conditions, wp);
		
		boolean contains = false;
		for(String curr: conditions) {
		    if(curr.equals("AND wi.workItemType = 'COMMIT'")) {
		       contains = true;
		    }
		}
		
		assertTrue(contains);
    }
	
	@Test
    public void parseMethodPluginDescriptionTest1() {
		PatternProject pp = new PatternProject();
		String desc = "pattern=false";
		exportPatternMapService.parseMethodPluginDescription(pp, desc);
		assertTrue(!pp.isPattern());
    }
	
	@Test
    public void parseMethodPluginDescriptionTest2() {
		PatternProject pp = new PatternProject();
		String desc = "";
		exportPatternMapService.parseMethodPluginDescription(pp, desc);
		assertTrue(!pp.isPattern());
    }
	
	@Test
    public void parseMethodPluginDescriptionTest3() {
		PatternProject pp = new PatternProject();
		String desc = "pattern=true";
		exportPatternMapService.parseMethodPluginDescription(pp, desc);
		assertTrue(pp.isPattern());
    }
	
	@Test
    public void parseMethodPluginDescriptionTest4() {
		PatternProject pp = new PatternProject();
		String desc = "pattern = true";
		exportPatternMapService.parseMethodPluginDescription(pp, desc);
		assertTrue(pp.isPattern());
    }
	
	@Test
    public void parseMainDescriptionTest1() {
		PatternOutcome po = new PatternOutcome();
		String desc = "type = commit";
		exportPatternMapService.parseMainDescription(po, desc);
		assertTrue(po.getType().equals("commit"));
    }
	
	@Test
    public void parseMainDescriptionTest2() {
		PatternTask po = new PatternTask();
		String desc = "keywords = spec, archit";
		exportPatternMapService.parseMainDescription(po, desc);
		String[] tokens = po.getTokens();
		assertTrue(tokens.length == 2);
    }
	
}

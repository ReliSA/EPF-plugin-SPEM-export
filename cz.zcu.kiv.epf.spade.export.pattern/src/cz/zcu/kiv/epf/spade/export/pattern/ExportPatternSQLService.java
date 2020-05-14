package cz.zcu.kiv.epf.spade.export.pattern;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.zcu.kiv.epf.spade.export.pattern.domain.PatternArtifact;
import cz.zcu.kiv.epf.spade.export.pattern.domain.PatternOutcome;
import cz.zcu.kiv.epf.spade.export.pattern.domain.PatternProject;
import cz.zcu.kiv.epf.spade.export.pattern.domain.PatternRole;
import cz.zcu.kiv.epf.spade.export.pattern.domain.PatternTask;
import cz.zcu.kiv.epf.spade.export.pattern.domain.PatternWorkProduct;

/**
 * Export class for SQL export.
 * 
 * @author Lenka Simeckova
 * @since 1.0
 *
 */
public class ExportPatternSQLService implements IExportPatternSpecificService {
	
	private static final ExportPatternSQLService exportPatternSQLService = new ExportPatternSQLService();

	ExportPatternConfig data;

	ExportPatternLogger logger;

	static List<PatternProject> patternProjects = new ArrayList<PatternProject>();

	/**
	 * Returns shared instance.
	 * @param datap data from UI
	 * @param loggerp shared logger
	 * @return shared instance
	 */
	public static ExportPatternSQLService getInstance(ExportPatternConfig datap, ExportPatternLogger loggerp) {
		exportPatternSQLService.data = datap;
		exportPatternSQLService.logger = loggerp;
		return exportPatternSQLService;
	}

	/**
	 * Calls method for creating SQL script for each anti-pattern.
	 * @param patternProjects list of anti-pattern projects
	 */
	public void export(List<PatternProject> patternProjects) {
		this.logger.logMessage("Exportig patterns to SQL started.");
		
		for (PatternProject patternProject : patternProjects) {
			createSQLScript(patternProject);
		}
	}

	/**
	 * Creates SQL script with one or more queries.
	 * @param patternProject
	 */
	private void createSQLScript(PatternProject patternProject) {

		// creating SQL file
		String path = data.getDirectory() + File.separator + patternProject.getName() + ".sql";
		PrintWriter writer;
		try {
			writer = new PrintWriter(new FileWriter(path));		
			
			// for each task in method plug-in
			for (PatternTask patternTask : patternProject.getPatternTasks().values()) {
				writer.println(generateSql(patternTask, patternProject.isPattern()));
				writer.println();
			}

			writer.close();
			
		} catch (IOException e) {
			this.logger.logError(e.getMessage(), e);
		}
		
	}

	/**
	 * Builds whole SQL query.
	 * @param patternTask
	 * @param isPattern indicator of "inverted" anti-patterns
	 * @return query
	 */
	private String generateSql(PatternTask patternTask, boolean isPattern) {
		String sql = String.format("SELECT (CASE WHEN COUNT(*) > 0 THEN %d ELSE %d END) FROM ", isPattern ? 0 : 1, isPattern ? 1 : 0);
		
		if (patternTask.getAmount() == null || patternTask.getAmount().isEmpty()) {
			sql += generateBasicSql(patternTask.getTokens(), patternTask.getOutputs(), patternTask.getPerformers());
		} else {
			sql += generateRateSql(patternTask.getTokens(), patternTask.getOutputs(), patternTask.getPerformers(), patternTask.getAmount());
		}
		return sql;
	}

	/**
	 * Builds rate query prototype.
	 * @param tokens keywords
	 * @param outputs
	 * @param performers
	 * @param amount
	 * @return rate query prototype
	 */
	private String generateRateSql(String[] tokens, List<PatternWorkProduct> outputs, List<PatternRole> performers, String amount) {
		String sql = "";
		String start = "\n(SELECT COUNT(*) as sum FROM ";
		sql += start;
		sql += generateBasicSql(tokens, outputs, performers);
		sql += ")\r\nAS a,";
		sql += start;
		sql += generateBasicSql(null, outputs, performers);
		sql += ")\r\nAS b";
		sql += addRateCondition(amount);
		return sql;
	}

	/**
	 * Adds final rate condition for rate query.
	 * @param amount unparsed amount attribute
	 * @return condition
	 */
	public String addRateCondition(String amount) {
		String[] tokens = amount.trim().split("( )*(amount)( )*|( )*(this.type)( )*(/)( )*");
		String sign = tokens[1].replace("&lt;", "<").replace("&gt;", ">");
		
		return String.format("\nWHERE a.sum %s (b.sum/%s)", sign, tokens[2]);
	}

	/**
	 * Creates basic query prototype from given task data.
	 * @param tokens keywords
	 * @param outputs
	 * @param roles
	 * @return basic query prototype
	 */
	public String generateBasicSql(String[] tokens, List<PatternWorkProduct> outputs, List<PatternRole> roles) {

		List<String> joins = new ArrayList<String>();
		joins.add("JOIN person p ON p.id = wi.authorId");

		List<String> conditions = new ArrayList<String>();
		conditions.add("AND p.projectId = PROJECT_ID");

		addRoleConstraint(conditions, joins, roles);
		
		addTypeConstraint(conditions, outputs);

		addNameConstraint(conditions, tokens);

		return assembleBasicQuery(joins, conditions);
	}
	
	/**
	 * Adds constraint for roles.
	 * @param conditions
	 * @param joins
	 * @param roles
	 */
	void addRoleConstraint(List<String> conditions, List<String> joins, List<PatternRole> roles) {
		if (!roles.isEmpty()) {
			joins.add("JOIN person_role pr ON p.id = pr.personId");
			joins.add("JOIN role ON r.id = pr.roleId");
			joins.add("JOIN role_classification rc ON r.classId = rc.id");
			
			String roleCondition = "AND (";
				
			for (PatternRole performer : roles) {
				roleCondition += "rc.class = '"  + performer.getName().replaceAll("_| ", "").toUpperCase() + "'";
			}
			
			roleCondition += ")";

			conditions.add(roleCondition);
		}
	}

	/**
	 * Adds constraint for work item type (task, artifact, ...).
	 * @param conditions
	 * @param outputs work items
	 */
	private void addTypeConstraint(List<String> conditions, List<PatternWorkProduct> outputs) {
		if (outputs.isEmpty()) {
			
			conditions.add("AND wi.workItemType = 'WORK_UNIT'");
			
		} else {
			for (PatternWorkProduct output : outputs) {
				
				if (output instanceof PatternArtifact) {

					conditions.add("AND wi.workItemType = 'ARTIFACT'");

				} else if (output instanceof PatternOutcome) {

					conditions.add("AND wi.workItemType = 'COMMIT'");

				} else {
					// TODO
				}
			}
		}
		
	}

	/**
	 * Creates prototype of basic query from given joins and conditions.
	 * @param joins
	 * @param conditions
	 * @return query prototype
	 */
	private String assembleBasicQuery(List<String> joins, List<String> conditions) {
		String result = "";
		String start = "work_item wi";
		
		result += start;

		for (String join : joins) {
			result += "\n\t" + join;
		}

		for (String cond : conditions) {
			result += "\n\t" + cond;
		}
		return result;
	}

	/**
	 * Adds condition for element name.
	 * @param conditions
	 * @param tokens parsed keywords
	 */
	private void addNameConstraint(List<String> conditions, String[] tokens) {
		this.logger.logMessage("tokens " + Arrays.toString(tokens));
		if (tokens != null && tokens.length != 0) {

			String likeCondition = "AND (";

			for (int i = 0; i < tokens.length; i++) {
				if (i != 0) {
					likeCondition += " OR ";
				}
				likeCondition += "wi.name LIKE '%" + tokens[i] + "%'";
			}

			likeCondition += ")";

			conditions.add(likeCondition);
		}
	}

}

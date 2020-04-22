package org.eclipse.epf.export.pattern;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.epf.export.pattern.domain.PatternArtifact;
import org.eclipse.epf.export.pattern.domain.PatternOutcome;
import org.eclipse.epf.export.pattern.domain.PatternProject;
import org.eclipse.epf.export.pattern.domain.PatternRole;
import org.eclipse.epf.export.pattern.domain.PatternTask;
import org.eclipse.epf.export.pattern.domain.PatternWorkProduct;
import org.eclipse.epf.uma.MethodPlugin;

public class ExportPatternSQLService implements IExportPatternSpecificService {

	ExportPatternData data;

	ExportPatternLogger logger;

	static List<PatternProject> patternProjects = new ArrayList<PatternProject>();

	public ExportPatternSQLService(ExportPatternData data, ExportPatternLogger logger) {
		this.data = data;
		this.logger = logger;
	}

	public static ExportPatternSQLService getInstance(ExportPatternData data, ExportPatternLogger logger) {
		return new ExportPatternSQLService(data, logger);
	}

	public void export(List<PatternProject> patternProjects) {
		this.logger.logMessage("Exportig patterns to SQL started.");
		
		for (PatternProject patternProject : patternProjects) {
			createSQLScript(patternProject);
		}

		return;
	}

	private void createSQLScript(PatternProject patternProject) {

		// creating SQL file
		String path = data.getDirectory() + "\\" + patternProject.getName() + ".sql";
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

	private String generateSql(PatternTask patternTask, boolean isPattern) {
		String sql = String.format("SELECT (CASE WHEN COUNT(*) > 0 THEN %d ELSE %d END) FROM ", isPattern ? 0 : 1, isPattern ? 1 : 0);
		
		if (patternTask.getAmount() == null || patternTask.getAmount().isEmpty()) {
			sql += generateBasicSql(patternTask.getTokens(), patternTask.getOutputs(), patternTask.getPerformers());
		} else {
			sql += generateRateSql(patternTask.getTokens(), patternTask.getOutputs(), patternTask.getPerformers(), patternTask.getAmount());
		}
		return sql;
	}

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

	private String addRateCondition(String amount) {
		String[] tokens = amount.trim().split("( )*(amount)( )*|( )*(this.type)( )*(/)( )*");
		String sign = tokens[1].replace("&lt;", "<").replace("&gt;", ">");
		
		return String.format("\nWHERE a.sum %s (b.sum/%s)", sign, tokens[2]);
	}

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

	private void addRoleConstraint(List<String> conditions, List<String> joins, List<PatternRole> roles) {
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
	 * 
	 * @param joins
	 * @param conditions
	 * @return
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
	 * 
	 * @param conditions
	 * @param tokens
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

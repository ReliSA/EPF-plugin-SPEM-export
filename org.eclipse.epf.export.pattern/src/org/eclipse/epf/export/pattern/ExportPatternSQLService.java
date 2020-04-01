package org.eclipse.epf.export.pattern;

import java.io.File;
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

public class ExportPatternSQLService implements IExportPatternService {

	ExportPatternData data;

	ExportPatternLogger logger;

	static List<PatternProject> patternProjects = new ArrayList<PatternProject>();

	public ExportPatternSQLService(ExportPatternData data) {
		this.data = data;
		this.logger = new ExportPatternLogger(new File(System.getProperty("user.dir")), "sql");
	}

	public static ExportPatternSQLService getInstance(ExportPatternData data) {
		return new ExportPatternSQLService(data);
	}

	public void export() {
		this.logger.logMessage("Exportig patterns to SQL started.");
		
		for (MethodPlugin methodPlugin : data.getSelectedPlugins()) {
			
			PatternProject patternProject = ExportPatternMapService.map(methodPlugin, this.logger);
			createSQLScript(methodPlugin, patternProject);
		}

		return;
	}

	private void createSQLScript(MethodPlugin methodPlugin, PatternProject patternProject) {

		String path = data.getDirectory() + "\\" + methodPlugin.getName() + ".sql";
		PrintWriter writer;
		try {
			writer = new PrintWriter(new FileWriter(path));		
			
			for (PatternTask patternTask : patternProject.getPatternTasks().values()) {
				writer.println(generateSql(patternTask.getTokens(), patternTask.getOutputs(), patternTask.getPerformers()));
				writer.println();
			}

			writer.close();
			
		} catch (IOException e) {
			this.logger.logError(e.getMessage(), e);
		}
		
	}

	public String generateSql(String[] tokens, List<PatternWorkProduct> outputs, List<PatternRole> roles) {

		List<String> joins = new ArrayList<String>();
		joins.add("JOIN person p ON p.id = wi.authorId");

		List<String> conditions = new ArrayList<String>();
		conditions.add("AND p.projectId = PROJECT_ID");

		addRoleConstraint(conditions, joins, roles);
		
		addTypeConstraint(conditions, outputs);

		addNameConstraint(conditions, tokens);

		String result = assembleBasicQuery(joins, conditions);

		return result;
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
				
				addNameConstraint(conditions, output.getTokens());
			}
		}
		
	}

	private String assembleBasicQuery(List<String> joins, List<String> conditions) {
		String result = "";
		String start = "SELECT COUNT(*) FROM work_item wi";
		
		result += start;

		for (String join : joins) {
			result += "\n" + join;
		}

		for (String cond : conditions) {
			result += "\n" + cond;
		}
		return result;
	}

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

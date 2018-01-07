/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.github.skjolberg.log.domain.codegen.gradle;

import java.util.HashMap;
import java.util.Map;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.DependencySet;

public class JsonLogDomainPlugin implements Plugin<Project> {
	
	public JsonLogDomainPlugin() {
	}
	
    public void apply(Project project) {
    	// make sure the project has the java plugin
    	Map<String, String> map = new HashMap<>();
    	map.put("plugin", "java");
    	project.apply(map);
    	
    	// add source dependency
    	// https://stackoverflow.com/questions/18594625/gradle-custom-plugin-add-dependency-from-extension-object
        final Configuration config = project.getConfigurations().create("jsonLogDomain")
                .setVisible(false)
                .setDescription("The data artifacts to be processed for this plugin.");

    	LoggingPluginExtension extension = project.getExtensions().create("jsonLogDomain", LoggingPluginExtension.class, project);

    	String version = extension.getVersion().getOrElse("1.0.3-SNAPSHOT");
    	
        JsonLogDomainTask logging = project.getTasks().create("jsonLogDomain", JsonLogDomainTask.class, (task) -> { 
        	System.out.println("Logging task created");
        	
        	task.definitions = extension.getDefinitions();
        	task.markdown = extension.getMarkdown();
        	task.logback = extension.getLogback();
        	task.elastic = extension.getElastic();
        	task.stackDriver = extension.getStackDriver();
        });

        // make sure task runs before compile
        Task compileJava = project.getTasks().findByName("compileJava");
        compileJava.dependsOn(logging);
        	
        // add support-library as dependency
        project.afterEvaluate(new Action<Project>() {
			@Override
			public void execute(Project project) {
				project.getDependencies().add(config.getName(), "com.github.skjolber.log-domain:log-domain-support-logback:" + version);
			}
		});
    }
}
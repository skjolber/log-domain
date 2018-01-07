/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.github.skjolberg.log.domain.codegen.gradle;


import static org.junit.Assert.assertNotNull;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;


public class PluginTest {
	
	@Test
	public void testTasks() {
		// 'ProjectBuilder' is meant for lower-level tests that do not execute a build. 
		// It's a good fit for testing how plugins configure the object model. 
		Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("com.github.skjolber.json-log-domain");

        JsonLogDomainTask task = (JsonLogDomainTask)project.getTasks().getByName("jsonLogDomain");
        assertNotNull(task);
	}

}

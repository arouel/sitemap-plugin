/*******************************************************************************
 * Copyright 2012 André Rouél
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sf.sitemapplugin;

import java.io.File;
import java.util.List;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.doxia.tools.SiteTool;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.i18n.I18N;
import org.codehaus.plexus.util.ReaderFactory;

/**
 * Base class for sitemap mojos.
 * 
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 */
public abstract class AbstractSiteMojo extends AbstractMojo {
	/**
	 * A comma separated list of locales supported by Maven. The first valid token will be the default Locale for this
	 * instance of the Java Virtual Machine.
	 * 
	 * @parameter expression="${locales}"
	 */
	protected String locales;

	/**
	 * SiteTool.
	 * 
	 * @component
	 */
	protected SiteTool siteTool;

	/**
	 * Internationalization.
	 * 
	 * @component
	 */
	protected I18N i18n;

	/**
	 * Directory containing the site.xml file and the source for apt, fml and xdoc docs.
	 * 
	 * @parameter default-value="${basedir}/src/site"
	 */
	protected File siteDirectory;

	/**
	 * The maven project.
	 * 
	 * @parameter default-value="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;

	/**
	 * The local repository.
	 * 
	 * @parameter default-value="${localRepository}"
	 * @readonly
	 */
	protected ArtifactRepository localRepository;

	/**
	 * The reactor projects.
	 * 
	 * @parameter default-value="${reactorProjects}"
	 * @required
	 * @readonly
	 */
	protected List<MavenProject> reactorProjects;

	/**
	 * Specifies the input encoding.
	 * 
	 * @parameter expression="${encoding}" default-value="${project.build.sourceEncoding}"
	 */
	private String inputEncoding;

	/**
	 * Specifies the output encoding.
	 * 
	 * @parameter expression="${outputEncoding}" default-value="${project.reporting.outputEncoding}"
	 */
	private String outputEncoding;

	/**
	 * Gets the input files encoding.
	 * 
	 * @return The input files encoding, never <code>null</code>.
	 */
	protected String getInputEncoding() {
		return (inputEncoding == null) ? ReaderFactory.ISO_8859_1 : inputEncoding;
	}

	/**
	 * Gets the effective reporting output files encoding.
	 * 
	 * @return The effective reporting output file encoding, never <code>null</code>.
	 */
	protected String getOutputEncoding() {
		return (outputEncoding == null) ? ReaderFactory.UTF_8 : outputEncoding;
	}

}

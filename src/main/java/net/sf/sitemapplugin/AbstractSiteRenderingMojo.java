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
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.doxia.site.decoration.DecorationModel;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.doxia.siterenderer.SiteRenderingContext;
import org.apache.maven.doxia.tools.SiteToolException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 * Base class for sitemap rendering mojos.
 * 
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 */
public abstract class AbstractSiteRenderingMojo extends AbstractSiteMojo implements Contextualizable {

	/**
	 * Remote repositories used for the project.
	 * 
	 * @todo this is used for site descriptor resolution - it should relate to the actual project but for some reason
	 *       they are not always filled in
	 * @parameter default-value="${project.remoteArtifactRepositories}"
	 * @readonly
	 */
	protected List<ArtifactRepository> repositories;

	/**
	 * The location of a Velocity template file to use. When used, skins and the default templates, CSS and images are
	 * disabled. It is highly recommended that you package this as a skin instead.
	 * 
	 * @parameter expression="${templateFile}"
	 */
	private File templateFile;

	/**
	 * The template properties for rendering the site.
	 * 
	 * @parameter
	 */
	private Map<String, Object> attributes;

	/**
	 * Site renderer.
	 * 
	 * @component
	 */
	protected Renderer siteRenderer;

	/**
	 * Directory containing generated documentation. This is used to pick up other source docs that might have been
	 * generated at build time.
	 * 
	 * @parameter alias="workingDirectory" default-value="${project.build.directory}/generated-site"
	 * 
	 * @todo should we deprecate in favour of reports?
	 */
	protected File generatedSiteDirectory;

	/**
	 * The current Maven session.
	 * 
	 * @parameter expression="${session}"
	 * @required
	 * @readonly
	 */
	protected MavenSession mavenSession;

	/** {@inheritDoc} */
	@Override
	public void contextualize(final Context context) throws ContextException {
		context.get(PlexusConstants.PLEXUS_KEY);
	}

	protected SiteRenderingContext createSiteRenderingContext(final Locale locale) throws MojoExecutionException, IOException,
			MojoFailureException {
		if (attributes == null) {
			attributes = new HashMap<String, Object>();
		}

		if (attributes.get("project") == null) {
			attributes.put("project", project);
		}

		if (attributes.get("inputEncoding") == null) {
			attributes.put("inputEncoding", getInputEncoding());
		}

		if (attributes.get("outputEncoding") == null) {
			attributes.put("outputEncoding", getOutputEncoding());
		}

		// Put any of the properties in directly into the Velocity context
		for (final Map.Entry<Object, Object> entry : project.getProperties().entrySet()) {
			attributes.put((String) entry.getKey(), entry.getValue());
		}

		DecorationModel decorationModel;
		try {
			decorationModel = siteTool.getDecorationModel(project, reactorProjects, localRepository, repositories,
					siteTool.getRelativePath(siteDirectory.getAbsolutePath(), project.getBasedir().getAbsolutePath()), locale,
					getInputEncoding(), getOutputEncoding());
		} catch (final SiteToolException e) {
			throw new MojoExecutionException("SiteToolException: " + e.getMessage(), e);
		}

		File skinFile;
		try {
			final Artifact skinArtifact = siteTool.getSkinArtifactFromRepository(localRepository, repositories, decorationModel);
			getLog().info("Rendering site with " + skinArtifact.getId() + " skin.");

			skinFile = skinArtifact.getFile();
		} catch (final SiteToolException e) {
			throw new MojoExecutionException("SiteToolException: " + e.getMessage(), e);
		}
		SiteRenderingContext context;
		if (templateFile != null) {
			if (!templateFile.exists()) {
				throw new MojoFailureException("Template file '" + templateFile + "' does not exist");
			}
			context = siteRenderer.createContextForTemplate(templateFile, skinFile, attributes, decorationModel, project.getName(), locale);
		} else {
			context = siteRenderer.createContextForSkin(skinFile, attributes, decorationModel, project.getName(), locale);
		}

		// Generate static site
		if (!locale.getLanguage().equals(Locale.getDefault().getLanguage())) {
			context.addSiteDirectory(new File(siteDirectory, locale.getLanguage()));
		} else {
			context.addSiteDirectory(siteDirectory);
		}

		return context;
	}

}

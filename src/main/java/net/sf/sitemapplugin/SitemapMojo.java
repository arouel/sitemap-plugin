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
import java.util.List;
import java.util.Locale;

import org.apache.maven.doxia.siterenderer.SiteRenderingContext;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.redfin.sitemapgenerator.ChangeFreq;
import com.redfin.sitemapgenerator.W3CDateFormat;
import com.redfin.sitemapgenerator.W3CDateFormat.Pattern;
import com.redfin.sitemapgenerator.WebSitemapGenerator;

/**
 * Generates the sitemap for a single project.
 * <p>
 * Note that links between module sites in a multi module build will <b>not</b> work.
 * </p>
 * 
 * @goal sitemap
 * @phase site
 */
public final class SitemapMojo extends AbstractSiteRenderingMojo {
	/**
	 * Directory where the project sites and report distributions will be generated.
	 * 
	 * @parameter expression="${siteOutputDirectory}" default-value="${project.reporting.outputDirectory}"
	 */
	private File outputDirectory;

	/**
	 * Whether to validate xml input documents. If set to true, <strong>all</strong> input documents in xml format (in
	 * particular xdoc and fml) will be validated and any error will lead to a build failure.
	 * 
	 * @parameter expression="${validate}" default-value="false"
	 */
	private boolean validate;

	/**
	 * Set this to 'true' to skip site generation.
	 * 
	 * @parameter expression="${maven.sitemap.skip}" default-value="false"
	 */
	private boolean skip;

	/**
	 * How frequently the site is likely to change.
	 * 
	 * @parameter expression="${changeFreq}" default-value="MONTHLY"
	 */
	private String changeFreq;

	/**
	 * {@inheritDoc}
	 * 
	 * Generate the project site
	 * <p/>
	 * throws MojoExecutionException if any
	 * 
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (skip) {
			getLog().info("maven.sitemap.skip = true: Skipping site generation");
			return;
		}

		if (getLog().isDebugEnabled()) {
			getLog().debug("executing Site Mojo");
		}

		try {
			final List<Locale> localesList = siteTool.getAvailableLocales(locales);

			// Default is first in the list
			final Locale defaultLocale = localesList.get(0);
			Locale.setDefault(defaultLocale);

			for (final Locale locale : localesList) {
				render(locale);
			}
		} catch (final Exception e) {
			throw new MojoExecutionException("Error during sitemap generation", e);
		}
	}

	private File getOutputDirectory(final Locale locale) {
		File file;
		if (locale.getLanguage().equals(Locale.getDefault().getLanguage())) {
			file = outputDirectory;
		} else {
			file = new File(outputDirectory, locale.getLanguage());
		}

		// Safety
		if (!file.exists()) {
			file.mkdirs();
		}

		return file;
	}

	private void render(final Locale locale) throws IOException, MojoExecutionException, MojoFailureException {
		final SiteRenderingContext context = createSiteRenderingContext(locale);

		context.setInputEncoding(getInputEncoding());
		context.setOutputEncoding(getOutputEncoding());
		context.setValidate(validate);
		if (validate) {
			getLog().info("Validation is switched on, xml input documents will be validated!");
		}

		final ChangeFreq changeFreq = ChangeFreq.valueOf(this.changeFreq.toUpperCase());
		if (changeFreq == null) {
			getLog().error("Change frequency must be set or is wrong.");
			return;
		}

		final File outputDir = getOutputDirectory(locale);
		getLog().info("Generating Sitemap.");

		final W3CDateFormat dateFormat = new W3CDateFormat(Pattern.DAY);
		final WebSitemapGenerator generator = WebSitemapGenerator.builder(project.getUrl(), outputDir).autoValidate(validate)
				.dateFormat(dateFormat).build();
		new Sitemap(getOutputEncoding(), i18n).generate(project, context.getDecoration(), generator, changeFreq);
		final List<File> files = generator.write();
		for (final File file : files) {
			getLog().info("Generated Sitemap: " + file.getPath());
		}
	}

}

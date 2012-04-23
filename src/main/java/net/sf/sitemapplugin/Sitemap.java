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

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;

import org.apache.maven.doxia.site.decoration.DecorationModel;
import org.apache.maven.doxia.site.decoration.Menu;
import org.apache.maven.doxia.site.decoration.MenuItem;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.i18n.I18N;

import com.redfin.sitemapgenerator.ChangeFreq;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl.Options;

/**
 * Generate a sitemap.
 */
public class Sitemap {

	private static void extractItems(final MavenProject project, final List<MenuItem> items, final WebSitemapGenerator generator,
			final ChangeFreq changeFreq) throws MalformedURLException {
		if (items == null || items.isEmpty()) {
			return;
		}

		for (final MenuItem item : items) {
			final Options options = new Options(project.getUrl() + relativePath(item.getHref()));
			options.lastMod(new Date());
			options.changeFreq(changeFreq);
			generator.addUrl(options.build());
			extractItems(project, item.getItems(), generator, changeFreq);
		}
	}

	private static String relativePath(final String href) {
		return href.startsWith("/") ? "." + href : href;
	}

	private String encoding;

	private I18N i18n;

	/**
	 * Constructor sets default values.
	 * 
	 * @param encoding
	 *            the default encoding to use when writing the output file.
	 * @param i18n
	 *            the default I18N for translations.
	 */
	public Sitemap(final String encoding, final I18N i18n) {
		this.encoding = encoding;
		this.i18n = i18n;
	}

	private void extract(final MavenProject project, final DecorationModel decoration, final WebSitemapGenerator generator,
			final ChangeFreq changeFreq) throws MalformedURLException {
		for (final Menu menu : decoration.getMenus()) {
			extractItems(project, menu.getItems(), generator, changeFreq);
		}
	}

	/**
	 * Generates a sitemap.xml within the given {@code WebSitemapGenerator}. This is a valid XML document that can be
	 * processed by any parser who understand the XML Sitemaps Protocol.
	 * 
	 * @param model
	 *            the DecorationModel to extract the menus from
	 * @param generator
	 *            the the sitemap generator
	 * 
	 * @throws IllegalArgumentException
	 *             if one of the given arguments is {@code null}
	 */
	public void generate(final MavenProject project, final DecorationModel model, final WebSitemapGenerator generator,
			final ChangeFreq changeFreq) throws IOException {
		if (model == null) {
			throw new IllegalArgumentException("Argument 'model' must not be null.");
		}
		if (generator == null) {
			throw new IllegalArgumentException("Argument 'generator' must not be null.");
		}
		if (changeFreq == null) {
			throw new IllegalArgumentException("Argument 'changeFreq' must not be null.");
		}

		extract(project, model, generator, changeFreq);
	}

	/**
	 * Get the encoding to use when writing the output file.
	 * 
	 * @return the value of encoding.
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * Get the value of i18n.
	 * 
	 * @return the value of i18n.
	 */
	public I18N getI18n() {
		return i18n;
	}

	/**
	 * Set the encoding to use when writing the output file.
	 * 
	 * @param enc
	 *            new value of encoding.
	 */
	public void setEncoding(final String enc) {
		this.encoding = enc;
	}

	/**
	 * Set the value of i18n.
	 * 
	 * @param i18n
	 *            new value of i18n.
	 */
	public void setI18n(final I18N i18n) {
		this.i18n = i18n;
	}

}

package net.sf.sitemapplugin;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SitemapMojoTest extends AbstractMojoTestCase {

	@Before
	public void setup() throws Exception {
		super.setUp();
	}

	/**
	 * Test method for 'org.apache.maven.plugins.site.AbstractSiteMojo.getInterpolatedSiteDescriptorContent(Map,
	 * MavenProject, String)'
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSitemapMojo() throws Exception {
		final File pom = getTestFile("src/test/resources/unit/plugin-to-test/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		final SitemapMojo mojo = (SitemapMojo) lookupMojo("sitemap", pom);
		assertNotNull(mojo);

		final File descriptorFile = getTestFile("src/test/resources/unit/plugin-to-test/src/site/site.xml");
		assertNotNull(descriptorFile);
		assertTrue(descriptorFile.exists());
	}

}

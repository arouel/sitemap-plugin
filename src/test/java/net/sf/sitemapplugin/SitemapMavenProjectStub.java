package net.sf.sitemapplugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.apache.maven.plugin.testing.stubs.StubArtifactRepository;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.ReaderFactory;

public class SitemapMavenProjectStub extends MavenProjectStub {

	public SitemapMavenProjectStub() {
		this(new File(PlexusTestCase.getBasedir(), "src/test/resources/unit/plugin-to-test/pom.xml"));
	}

	public SitemapMavenProjectStub(final File pom) {
		final MavenXpp3Reader pomReader = new MavenXpp3Reader();
		Model model;
		try {
			model = pomReader.read(ReaderFactory.newXmlReader(pom));
			setModel(model);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}

		setGroupId(model.getGroupId());
		setArtifactId(model.getArtifactId());
		setVersion(model.getVersion());
		setName(model.getName());
		setUrl(model.getUrl());
		setPackaging(model.getPackaging());

		final Build build = new Build();
		build.setFinalName(model.getArtifactId());
		build.setDirectory(getBasedir() + "/target");
		build.setSourceDirectory(getBasedir() + "/src/main/java");
		build.setOutputDirectory(getBasedir() + "/target/classes");
		build.setTestSourceDirectory(getBasedir() + "/src/test/java");
		build.setTestOutputDirectory(getBasedir() + "/target/test-classes");
		setBuild(build);

		final List<String> compileSourceRoots = new ArrayList<String>();
		compileSourceRoots.add(getBasedir() + "/src/main/java");
		setCompileSourceRoots(compileSourceRoots);

		final List<String> testCompileSourceRoots = new ArrayList<String>();
		testCompileSourceRoots.add(getBasedir() + "/src/test/java");
		setTestCompileSourceRoots(testCompileSourceRoots);
	}

	@Override
	public List<ArtifactRepository> getRemoteArtifactRepositories() {
		final ArtifactRepository repository = new StubArtifactRepository(getBasedir() + "/target/local-repo");
		return Collections.singletonList(repository);
	}

}

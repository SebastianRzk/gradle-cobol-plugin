package de.sebastianruziczka

import java.util.concurrent.Callable

import org.gradle.api.Action
import org.gradle.api.AntBuilder
import org.gradle.api.InvalidUserDataException
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.PathValidation
import org.gradle.api.Project
import org.gradle.api.ProjectState
import org.gradle.api.Task
import org.gradle.api.UnknownProjectException
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.dsl.ArtifactHandler
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.component.SoftwareComponentContainer
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.CopySpec
import org.gradle.api.file.DeleteSpec
import org.gradle.api.file.FileTree
import org.gradle.api.file.ProjectLayout
import org.gradle.api.initialization.dsl.ScriptHandler
import org.gradle.api.invocation.Gradle
import org.gradle.api.logging.Logger
import org.gradle.api.logging.LoggingManager
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.Convention
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.ObjectConfigurationAction
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.PropertyState
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.resources.ResourceHandler
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.WorkResult
import org.gradle.normalization.InputNormalizationHandler
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec
import org.gradle.process.JavaExecSpec

class StubbedProject implements Project{

	@Override
	public int compareTo(Project o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void apply(Closure arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void apply(Action<? super ObjectConfigurationAction> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void apply(Map<String, ?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public PluginManager getPluginManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PluginContainer getPlugins() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String absoluteProjectPath(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void afterEvaluate(Action<? super Project> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterEvaluate(Closure arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void allprojects(Action<? super Project> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void allprojects(Closure arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public AntBuilder ant(Closure arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AntBuilder ant(Action<? super AntBuilder> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void artifacts(Closure arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void artifacts(Action<? super ArtifactHandler> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeEvaluate(Action<? super Project> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeEvaluate(Closure arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void buildscript(Closure arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void configurations(Closure arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object configure(Object arg0, Closure arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<?> configure(Iterable<?> arg0, Closure arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Iterable<T> configure(Iterable<T> arg0, Action<? super T> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> NamedDomainObjectContainer<T> container(Class<T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> NamedDomainObjectContainer<T> container(Class<T> arg0, NamedDomainObjectFactory<T> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> NamedDomainObjectContainer<T> container(Class<T> arg0, Closure arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WorkResult copy(Closure arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WorkResult copy(Action<? super CopySpec> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CopySpec copySpec() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CopySpec copySpec(Closure arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CopySpec copySpec(Action<? super CopySpec> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AntBuilder createAntBuilder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void defaultTasks(String... arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean delete(Object... arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public WorkResult delete(Action<? super DeleteSpec> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dependencies(Closure arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public int depthCompare(Project arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Project evaluationDependsOn(String arg0) throws UnknownProjectException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void evaluationDependsOnChildren() {
		// TODO Auto-generated method stub

	}

	@Override
	public ExecResult exec(Closure arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExecResult exec(Action<? super ExecSpec> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File file(Object o) {
		return new File('/absolute/' + o);
	}

	@Override
	public File file(Object arg0, PathValidation arg1) throws InvalidUserDataException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConfigurableFileTree fileTree(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConfigurableFileTree fileTree(Map<String, ?> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConfigurableFileTree fileTree(Object arg0, Closure arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConfigurableFileTree fileTree(Object arg0, Action<? super ConfigurableFileTree> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConfigurableFileCollection files(Object... arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConfigurableFileCollection files(Object arg0, Closure arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConfigurableFileCollection files(Object arg0, Action<? super ConfigurableFileCollection> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Project findProject(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object findProperty(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Project, Set<Task>> getAllTasks(boolean arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Project> getAllprojects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AntBuilder getAnt() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArtifactHandler getArtifacts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getBuildDir() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getBuildFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScriptHandler getBuildscript() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Project> getChildProjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SoftwareComponentContainer getComponents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConfigurationContainer getConfigurations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Convention getConvention() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getDefaultTasks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DependencyHandler getDependencies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getDepth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExtensionContainer getExtensions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Gradle getGradle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProjectLayout getLayout() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LoggingManager getLogging() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputNormalizationHandler getNormalization() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObjectFactory getObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Project getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Project getProject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getProjectDir() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, ?> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProviderFactory getProviders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RepositoryHandler getRepositories() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResourceHandler getResources() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getRootDir() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Project getRootProject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProjectState getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Project> getSubprojects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TaskContainer getTasks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Task> getTasksByName(String arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasProperty(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ExecResult javaexec(Closure arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExecResult javaexec(Action<? super JavaExecSpec> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File mkdir(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void normalization(Action<? super InputNormalizationHandler> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public Project project(String arg0) throws UnknownProjectException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Project project(String arg0, Closure arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Project project(String arg0, Action<? super Project> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> PropertyState<T> property(Class<T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object property(String arg0) throws MissingPropertyException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Provider<T> provider(Callable<T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String relativePath(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String relativeProjectPath(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void repositories(Closure arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBuildDir(File arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBuildDir(Object arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDefaultTasks(List<String> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDescription(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGroup(Object arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setStatus(Object arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setVersion(Object arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void subprojects(Action<? super Project> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void subprojects(Closure arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public WorkResult sync(Action<? super CopySpec> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileTree tarTree(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Task task(String arg0) throws InvalidUserDataException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Task task(Map<String, ?> arg0, String arg1) throws InvalidUserDataException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Task task(String arg0, Closure arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Task task(Map<String, ?> arg0, String arg1, Closure arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI uri(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileTree zipTree(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
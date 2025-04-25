import java.nio.file.Files;
import java.nio.file.Path;

record Project(Path archive) implements ToolRunner {
  static Project ofCurrentWorkingDirectory() {
    var archive = Path.of("web", "demo.jar");
    return new Project(archive);
  }

  void build() throws Exception {
    var classes = Files.createTempDirectory("classes-");
    var classesDemo = classes.resolve("demo");
    run("javac", "-d", classes, "--release=11", "--module-source-path=src/*/main", "--module=demo");
    run("jar", "--create", "--file=" + archive, "--main-class=demo.Main", "-C", classesDemo, ".");
    run("jar", "--describe-module", "--file=" + archive);
    run("jar", "--list", "--file=" + archive);
  }

  void start() throws Exception {
    if (Files.notExists(archive)) build();
    run("java", "-jar", archive);
  }

  void serve() throws Exception {
    if (Files.notExists(archive)) build();
    var folder = archive.getParent().toAbsolutePath();
    run("java", "--module", "jdk.httpserver", "--output", "verbose", "--directory", folder);
  }
}

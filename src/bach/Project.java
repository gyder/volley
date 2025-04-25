import java.lang.module.ModuleDescriptor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

record Project(Path archive) implements ToolRunner {
  static Project ofCurrentWorkingDirectory() {
    var archive = Path.of("web", "demo.jar");
    return new Project(archive);
  }

  void build() throws Exception {
    var now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    var classes = Files.createTempDirectory("classes-");
    run("javac", "-d", classes, "--release=11", "--module-source-path=src/*/main", "--module=demo");
    run(
        "jar",
        "--create",
        "--file=" + archive,
        "--module-version",
            ModuleDescriptor.Version.parse(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(now)),
        "--main-class=demo.Main",
        "-C",
            classes.resolve("demo"),
        ".");
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

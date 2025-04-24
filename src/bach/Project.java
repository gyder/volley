import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.spi.ToolProvider;
import java.util.stream.Stream;

record Project(Path archive) {
  static Project ofCurrentWorkingDirectory() {
    return new Project(Path.of("web", "demo.jar"));
  }

  void build() throws Exception {
    var classes = Files.createTempDirectory("classes-");
    run("javac", "-d", classes, "--release=11", "--module-source-path=src/*/main", "--module=demo");
    run("jar", "--create", "--file=" + archive, "--main-class=demo.Main", "-C", classes.resolve("demo"), ".");
    run("jar", "--describe-module", "--file=" + archive);
    run("jar", "--list", "--file=" + archive);
  }

  void start() throws Exception {
    if (Files.notExists(archive)) build();
    run("java", "-jar", archive);
  }

  private void run(String name, Object... arguments) {
    var args = Stream.of(arguments).map(String::valueOf).toArray(String[]::new);
    System.out.println("| " + name + " " + String.join(" ", args));
    var found = ToolProvider.findFirst(name);
    if (found.isPresent()) {
      var tool = found.get();
      var loader = Thread.currentThread().getContextClassLoader();
      try {
        Thread.currentThread().setContextClassLoader(tool.getClass().getClassLoader());
        var code = tool.run(System.out, System.err, args);
        if (code == 0) return;
        throw new RuntimeException(name + " returned non-zero exit code: " + code);
      } finally {
        Thread.currentThread().setContextClassLoader(loader);
      }
    }
    var program = Path.of(System.getProperty("java.home"), "bin", name);
    try {
      var builder = new ProcessBuilder(program.toString());
      builder.command().addAll(List.of(args));
      var process = builder.inheritIO().start();
      var code = process.waitFor();
      if (code == 0) return;
      throw new Error(name + " returned non-zero exit code: " + code);
    } catch (Exception exception) {
      throw new RuntimeException(name + " failed.", exception);
    }
  }
}

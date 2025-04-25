package demo;

import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.nio.file.Path;
import java.util.StringJoiner;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Main {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(Main::createAndShowGUI);
  }

  private static void createAndShowGUI() {
    var frame = new JFrame("bach-demo-cheerpj");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(400, 300);
    frame.getContentPane().add(new JScrollPane(new JTextArea(computeText())));
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  private static String computeText() {
    var joiner = new StringJoiner("\n");
    try {
      var location = Main.class.getProtectionDomain().getCodeSource().getLocation();
      joiner.add(location.toString());
      ModuleFinder.of(Path.of(location.toURI())).findAll().stream()
          .map(ModuleReference::descriptor)
          .map(ModuleDescriptor::toNameAndVersion)
          .forEach(joiner::add);
    } catch (Exception exception) {
      joiner.add(exception.getMessage());
    }
    joiner.add("");
    ModuleLayer.boot().modules().stream()
        .map(Module::getDescriptor)
        .sorted()
        .map(ModuleDescriptor::toNameAndVersion)
        .forEach(joiner::add);
    joiner.add("");
    System.getProperties().stringPropertyNames().stream()
        .sorted()
        .forEach(key -> joiner.add(key + " -> " + System.getProperty(key)));
    return joiner.toString();
  }
}

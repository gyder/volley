package demo;

import java.awt.Color;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Main {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(Main::createAndShowGUI);
    spoolSystemInformation(System.out::println);
  }

  private static void createAndShowGUI() {
    var frame = new JFrame("bach-demo-cheerpj");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setUndecorated(true);
    frame.getContentPane().add(new Starfield());
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
  }

  static class Starfield extends JPanel {
    public void paintComponent(Graphics graphics) {
      graphics.setColor(Color.BLACK);
      graphics.fillRect(0, 0, getWidth(), getHeight());
      graphics.setColor(Color.ORANGE);
      graphics.drawString("Here be star traces...",getWidth()/2,getHeight()/2);
    }
  }

  private static void spoolSystemInformation(Consumer<String> printer) {
    try {
      var location = Main.class.getProtectionDomain().getCodeSource().getLocation();
      printer.accept(location.toString());
      ModuleFinder.of(Path.of(location.toURI())).findAll().stream()
          .map(ModuleReference::descriptor)
          .map(ModuleDescriptor::toNameAndVersion)
          .forEach(printer);
    } catch (Exception exception) {
      printer.accept(exception.getMessage());
    }
    printer.accept("");
    ModuleLayer.boot().modules().stream()
        .map(Module::getDescriptor)
        .sorted()
        .map(ModuleDescriptor::toNameAndVersion)
        .forEach(printer);
    printer.accept("");
    System.getProperties().stringPropertyNames().stream()
        .sorted()
        .forEach(key -> printer.accept(key + " -> " + System.getProperty(key)));
  }
}

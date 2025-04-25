package demo;

import javax.swing.*;
import java.util.StringJoiner;

public class Main {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(Main::createAndShowGUI);
  }

  private static void createAndShowGUI() {
    var frame = new JFrame("bach-demo-cheerpj");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(400, 300);
    frame.getContentPane().add(new JScrollPane(new JTextArea(computeText())), SwingConstants.CENTER);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  private static String computeText() {
    var joiner = new StringJoiner("\n");
    try {
      joiner.add(Main.class.getProtectionDomain().getCodeSource().getLocation().toString());
    } catch (Exception exception) {
      joiner.add(exception.getMessage());
    }
    System.getProperties().stringPropertyNames().stream().sorted().forEach(key -> joiner.add(key + " -> " + System.getProperty(key)));
    return joiner.toString();
  }
}

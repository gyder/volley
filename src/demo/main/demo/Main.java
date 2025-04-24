package demo;

import javax.swing.*;

public class Main {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(Main::createAndShowGUI);
  }

  private static void createAndShowGUI() {
    var frame = new JFrame("bach-demo-cheerpj");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(400, 300);
    frame.getContentPane().add(new JLabel("bach-demo-cheerpj", SwingConstants.CENTER));
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}

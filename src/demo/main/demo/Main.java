package demo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Main {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(Main::createAndShowGUI);
    // spoolSystemInformation(System.out::println);
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
    private final List<Trace> traces = new ArrayList<>();

    Starfield() {
      addComponentListener(
          new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
              super.componentResized(e);
              var width = getWidth();
              var height = getHeight();
              for (int i = 0; i < 10; i++) {
                traces.add(
                    new Trace(
                        (int) (Math.random() * width) + (-width / 2),
                        (int) (Math.random() * height) + (-height / 2),
                        Starfield.this));
              }
            }
          });

      var timer =
          new Timer(
              5,
              e -> {
                traces.forEach(Trace::update);
                repaint();
              });
      timer.start();
    }

    @Override
    public void paintComponent(Graphics graphics) {
      var g = (Graphics2D) graphics;
      var width = getWidth();
      var height = getHeight();
      g.setColor(new Color(0, 0, 0, 20));
      g.fillRect(0, 0, width, height);
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      var old = g.getTransform();
      g.translate(width / 2, height / 2);
      for (var trace : traces) trace.draw(g);
      g.setTransform(old);
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
      g.setColor(Color.ORANGE);
      g.drawString("Here be star traces...", width / 2, height / 2);
    }

    static class Trace {
      private final int width;
      private final int height;
      private static final double traceLength = 50;
      private double x, y, r, len;
      private final double cosVal;
      private final double sinVal;

      Trace(int x, int y, JPanel canvas) {
        this.width = canvas.getWidth();
        this.height = canvas.getHeight();
        this.x = x;
        this.y = y;
        this.r = Math.hypot(x, y);
        this.len = 1;
        this.cosVal = x / r;
        this.sinVal = y / r;
      }

      void update() {
        if (!(-width / 2 <= x && x <= width / 2 && -height / 2 <= y && y <= height / 2)) {
          r = Math.random() * Math.hypot(height, width) / 8 + (width / 24);
          x = cosVal * r;
          y = sinVal * r;
          len = 1;
        }
        if (len < traceLength) {
          len++;
        } else {
          r += 2;
          x = cosVal * r;
          y = sinVal * r;
        }
      }

      public void draw(Graphics2D graphics) {
        double eR = r + len;
        double eX = cosVal * eR;
        double eY = sinVal * eR;
        graphics.setColor(Color.WHITE);
        graphics.drawLine((int) x, (int) y, (int) eX, (int) eY);
      }
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

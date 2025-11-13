package app;

import ui.MainFrame;

import javax.swing.*;

public class InventoryApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}

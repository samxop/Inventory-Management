package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Inventory Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Products", new ProductPanel());
        tabs.addTab("Suppliers", new SupplierPanel());
        add(tabs, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem exit = new JMenuItem("Exit"); exit.addActionListener(e -> System.exit(0));
        file.add(exit);
        menuBar.add(file);
        setJMenuBar(menuBar);
    }
}

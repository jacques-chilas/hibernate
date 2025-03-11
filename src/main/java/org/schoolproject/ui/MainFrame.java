package org.schoolproject.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {

    private JTabbedPane tabbedPane;
    private EmployePanel employePanel;
    private LieuPanel lieuPanel;
    private AffecterPanel affecterPanel;

    public MainFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Gestion des Affectations des Employés");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create panels for each entity
        employePanel = new EmployePanel();
        lieuPanel = new LieuPanel();
        affecterPanel = new AffecterPanel();

        // Create tabbed pane
        tabbedPane = new JTabbedPane();

        // Add panels to the tabbed pane
        tabbedPane.addTab("Employés", employePanel);
        tabbedPane.addTab("Lieux", lieuPanel);
        tabbedPane.addTab("Affectations", affecterPanel);

        // Add change listener to refresh data when switching tabs
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();

            // Refresh the data in the selected tab
            switch (selectedIndex) {
                case 0: // Employés tab
                    employePanel.refresh();
                    break;
                case 1: // Lieux tab
                    lieuPanel.refresh();
                    break;
                case 2: // Affectations tab
                    affecterPanel.refresh();
                    break;
            }
        });

        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        // Handle window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}
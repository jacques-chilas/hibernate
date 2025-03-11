package org.schoolproject.ui;

// MainApp.java

import org.schoolproject.util.HibernateUtil;

import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize Hibernate
        HibernateUtil.getSessionFactory();

        // Launch the GUI
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });

        // Add shutdown hook to close Hibernate SessionFactory
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            HibernateUtil.shutdown();
        }));
    }
}


package org.schoolproject.ui;

import org.schoolproject.dao.LieuDAO;
import org.schoolproject.model.Lieu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LieuPanel extends JPanel {

    private JTextField txtCodeLieu;
    private JTextField txtDesignation;
    private JTextField txtProvince;
    private JTable tblLieux;
    private JButton btnSave;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

    private LieuDAO lieuDAO;
    private DefaultTableModel tableModel;

    public LieuPanel() {
        lieuDAO = new LieuDAO();
        initComponents();
        loadLieux();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Détails du lieu"));

        formPanel.add(new JLabel("Code Lieu:"));
        txtCodeLieu = new JTextField();
        txtCodeLieu.setEditable(false);
        formPanel.add(txtCodeLieu);

        formPanel.add(new JLabel("Désignation:"));
        txtDesignation = new JTextField();
        formPanel.add(txtDesignation);

        formPanel.add(new JLabel("Province:"));
        txtProvince = new JTextField();
        formPanel.add(txtProvince);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));

        btnSave = new JButton("Ajouter");
        btnSave.addActionListener(e -> saveLieu());
        buttonPanel.add(btnSave);

        btnUpdate = new JButton("Modifier");
        btnUpdate.addActionListener(e -> updateLieu());
        buttonPanel.add(btnUpdate);

        btnDelete = new JButton("Supprimer");
        btnDelete.addActionListener(e -> deleteLieu());
        buttonPanel.add(btnDelete);

        btnClear = new JButton("Effacer");
        btnClear.addActionListener(e -> clearForm());
        buttonPanel.add(btnClear);

        // Table
        String[] columnNames = {"Code", "Désignation", "Province"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };

        tblLieux = new JTable(tableModel);
        tblLieux.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblLieux.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tblLieux.getSelectedRow();
                if (selectedRow != -1) {
                    displaySelectedLieu(selectedRow);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblLieux);

        // Top panel for form and buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add components to the main panel
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void saveLieu() {
        // Validate input data
        if (!validateInputData()) {
            return;
        }

        String designation = txtDesignation.getText().trim();
        String province = txtProvince.getText().trim();

        // Check for uniqueness of designation-province combination
        if (!isUniquePair(null, designation, province)) {
            JOptionPane.showMessageDialog(this,
                    "Un lieu avec la même désignation et province existe déjà.",
                    "Conflit de données",
                    JOptionPane.ERROR_MESSAGE);
            txtDesignation.requestFocus();
            return;
        }

        try {
            Lieu lieu = new Lieu(designation, province);
            lieuDAO.saveLieu(lieu);

            clearForm();
            loadLieux();
            JOptionPane.showMessageDialog(this,
                    "Lieu ajouté avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'ajout du lieu: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateLieu() {
        // Validate selection
        if (txtCodeLieu.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un lieu à modifier",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate input data
        if (!validateInputData()) {
            return;
        }

        Long codeLieu = Long.parseLong(txtCodeLieu.getText());
        String designation = txtDesignation.getText().trim();
        String province = txtProvince.getText().trim();

        // Check for uniqueness of designation-province combination (excluding this lieu)
        if (!isUniquePair(codeLieu, designation, province)) {
            JOptionPane.showMessageDialog(this,
                    "Un autre lieu avec la même désignation et province existe déjà.",
                    "Conflit de données",
                    JOptionPane.ERROR_MESSAGE);
            txtDesignation.requestFocus();
            return;
        }

        try {
            Lieu lieu = lieuDAO.getLieuById(codeLieu);
            if (lieu == null) {
                JOptionPane.showMessageDialog(this,
                        "Lieu non trouvé. Il a peut-être été supprimé.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                clearForm();
                loadLieux();
                return;
            }

            lieu.setDesignation(designation);
            lieu.setProvince(province);

            lieuDAO.updateLieu(lieu);

            clearForm();
            loadLieux();
            JOptionPane.showMessageDialog(this,
                    "Lieu modifié avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la modification du lieu: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteLieu() {
        if (txtCodeLieu.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un lieu à supprimer",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment supprimer ce lieu?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Long codeLieu = Long.parseLong(txtCodeLieu.getText());
                lieuDAO.deleteLieu(codeLieu);

                clearForm();
                loadLieux();
                JOptionPane.showMessageDialog(this,
                        "Lieu supprimé avec succès",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                // Check if the error is related to foreign key constraints
                if (e.getMessage() != null && e.getMessage().toLowerCase().contains("constraint")) {
                    JOptionPane.showMessageDialog(this,
                            "Ce lieu est référencé par d'autres données et ne peut pas être supprimé.",
                            "Erreur de suppression",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Erreur lors de la suppression du lieu: " + e.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void clearForm() {
        txtCodeLieu.setText("");
        txtDesignation.setText("");
        txtProvince.setText("");
        tblLieux.clearSelection();
    }

    // Add this method to your LieuPanel class
    public void refresh() {
        loadLieux();
        clearForm();
    }

    private void loadLieux() {
        try {
            List<Lieu> lieux = lieuDAO.getAllLieux();
            populateTable(lieux);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des lieux: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(List<Lieu> lieux) {
        tableModel.setRowCount(0);

        if (lieux != null) {
            for (Lieu lieu : lieux) {
                Object[] row = {
                        lieu.getCodeLieu(),
                        lieu.getDesignation(),
                        lieu.getProvince()
                };
                tableModel.addRow(row);
            }
        }
    }

    private void displaySelectedLieu(int selectedRow) {
        try {
            Long codeLieu = (Long) tableModel.getValueAt(selectedRow, 0);
            Lieu lieu = lieuDAO.getLieuById(codeLieu);

            if (lieu != null) {
                txtCodeLieu.setText(lieu.getCodeLieu().toString());
                txtDesignation.setText(lieu.getDesignation());
                txtProvince.setText(lieu.getProvince());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la sélection du lieu: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Validates input data from the form
     * @return true if all validations pass, false otherwise
     */
    private boolean validateInputData() {
        String designation = txtDesignation.getText().trim();
        String province = txtProvince.getText().trim();

        // Check for empty fields
        if (designation.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "La désignation est obligatoire",
                    "Champ requis",
                    JOptionPane.ERROR_MESSAGE);
            txtDesignation.requestFocus();
            return false;
        }

        if (province.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "La province est obligatoire",
                    "Champ requis",
                    JOptionPane.ERROR_MESSAGE);
            txtProvince.requestFocus();
            return false;
        }

        // Validate designation (letters, numbers, spaces, and some special chars)
        if (!designation.matches("^[a-zA-ZÀ-ÿ0-9\\s\\-_&/(),.]+$")) {
            JOptionPane.showMessageDialog(this,
                    "La désignation contient des caractères non autorisés",
                    "Format invalide",
                    JOptionPane.ERROR_MESSAGE);
            txtDesignation.requestFocus();
            return false;
        }

        // Validate province (only letters, spaces, and hyphens)
        if (!province.matches("^[a-zA-ZÀ-ÿ\\s\\-]+$")) {
            JOptionPane.showMessageDialog(this,
                    "La province ne doit contenir que des lettres, espaces et tirets",
                    "Format invalide",
                    JOptionPane.ERROR_MESSAGE);
            txtProvince.requestFocus();
            return false;
        }

        // Check length
        if (designation.length() > 100) {
            JOptionPane.showMessageDialog(this,
                    "La désignation est trop longue (maximum 100 caractères)",
                    "Entrée invalide",
                    JOptionPane.ERROR_MESSAGE);
            txtDesignation.requestFocus();
            return false;
        }

        if (province.length() > 50) {
            JOptionPane.showMessageDialog(this,
                    "La province est trop longue (maximum 50 caractères)",
                    "Entrée invalide",
                    JOptionPane.ERROR_MESSAGE);
            txtProvince.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Checks if the combination of designation and province is unique
     * @param currentCodeLieu The ID of the current lieu (null for new lieu)
     * @param designation The designation to check
     * @param province The province to check
     * @return true if the combination is unique, false otherwise
     */
    private boolean isUniquePair(Long currentCodeLieu, String designation, String province) {
        List<Lieu> allLieux = lieuDAO.getAllLieux();

        for (Lieu lieu : allLieux) {
            // Skip the current lieu when updating
            if (currentCodeLieu != null && lieu.getCodeLieu().equals(currentCodeLieu)) {
                continue;
            }

            // Check if the pair already exists (case insensitive comparison)
            if (lieu.getDesignation().equalsIgnoreCase(designation) &&
                    lieu.getProvince().equalsIgnoreCase(province)) {
                return false;
            }
        }

        return true;
    }
}
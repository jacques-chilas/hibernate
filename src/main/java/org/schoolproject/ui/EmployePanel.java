package org.schoolproject.ui;

import org.schoolproject.dao.EmployeDAO;
import org.schoolproject.model.Employe;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EmployePanel extends JPanel {

    private JTextField txtCodeEmp;
    private JTextField txtNom;
    private JTextField txtPrenom;
    private JTextField txtPoste;
    private JTextField txtSearch;
    private JTable tblEmployes;
    private JButton btnSave;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnSearch;
    private JComboBox<String> cmbSearchType;

    private EmployeDAO employeDAO;
    private DefaultTableModel tableModel;

    public EmployePanel() {
        employeDAO = new EmployeDAO();
        initComponents();
        loadEmployes();
    }

    private void initComponents() {
        // Use BoxLayout for vertical stacking to ensure proper arrangement
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create the main sections
        JPanel formPanel = createFormPanel();
        JPanel buttonsPanel = createButtonsPanel();
        JPanel searchPanel = createSearchPanel();
        JPanel tablePanel = createTablePanel();

        // Add the sections to the main panel
        add(formPanel);
        add(Box.createRigidArea(new Dimension(0, 10))); // Space between sections
        add(buttonsPanel);
        add(Box.createRigidArea(new Dimension(0, 10))); // Space between sections
        add(searchPanel);
        add(Box.createRigidArea(new Dimension(0, 10))); // Space between sections
        add(tablePanel);
    }

    private JPanel createFormPanel() {
        // Form panel for employee details using GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Détails de l'employé"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        formPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180)); // Control height
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Code Employé
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Code Employé:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtCodeEmp = new JTextField(10);
        txtCodeEmp.setEditable(false);
        formPanel.add(txtCodeEmp, gbc);

        // Nom
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Nom:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtNom = new JTextField(20);
        formPanel.add(txtNom, gbc);

        // Prénom
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Prénom:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtPrenom = new JTextField(20);
        formPanel.add(txtPrenom, gbc);

        // Poste
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Poste:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtPoste = new JTextField(20);
        formPanel.add(txtPoste, gbc);

        return formPanel;
    }

    private JPanel createButtonsPanel() {
        // Button panel with a clear flow layout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnSave = new JButton("Ajouter");
        btnSave.addActionListener(e -> saveEmploye());
        buttonPanel.add(btnSave);

        btnUpdate = new JButton("Modifier");
        btnUpdate.addActionListener(e -> updateEmploye());
        buttonPanel.add(btnUpdate);

        btnDelete = new JButton("Supprimer");
        btnDelete.addActionListener(e -> deleteEmploye());
        buttonPanel.add(btnDelete);

        btnClear = new JButton("Effacer");
        btnClear.addActionListener(e -> clearForm());
        buttonPanel.add(btnClear);

        return buttonPanel;
    }

    private JPanel createSearchPanel() {
        // Search panel with improved layout and search options
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Recherche"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add search type dropdown
        searchPanel.add(new JLabel("Rechercher par:"));
        cmbSearchType = new JComboBox<>(new String[]{"Code", "Nom"});
        searchPanel.add(cmbSearchType);

        // Search field
        searchPanel.add(new JLabel("Valeur:"));
        txtSearch = new JTextField(20);
        searchPanel.add(txtSearch);

        // Search button
        btnSearch = new JButton("Rechercher");
        btnSearch.addActionListener(e -> searchEmployes());
        searchPanel.add(btnSearch);

        // Reset search button
        JButton btnResetSearch = new JButton("Réinitialiser");
        btnResetSearch.addActionListener(e -> {
            txtSearch.setText("");
            loadEmployes();
        });
        searchPanel.add(btnResetSearch);

        return searchPanel;
    }

    private JPanel createTablePanel() {
        // Table panel with sufficient height
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Liste des employés"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Create table with proper column names
        String[] columnNames = {"Code", "Nom", "Prénom", "Poste"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };

        tblEmployes = new JTable(tableModel);
        tblEmployes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblEmployes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tblEmployes.getSelectedRow();
                if (selectedRow != -1) {
                    displaySelectedEmploye(selectedRow);
                }
            }
        });

        // Set column widths proportionally
        tblEmployes.getColumnModel().getColumn(0).setPreferredWidth(60);  // Code
        tblEmployes.getColumnModel().getColumn(1).setPreferredWidth(120); // Nom
        tblEmployes.getColumnModel().getColumn(2).setPreferredWidth(120); // Prénom
        tblEmployes.getColumnModel().getColumn(3).setPreferredWidth(150); // Poste

        // Create scroll pane with appropriate size
        JScrollPane scrollPane = new JScrollPane(tblEmployes);
        scrollPane.setPreferredSize(new Dimension(600, 250));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private void saveEmploye() {
        // Get and validate input data
        if (!validateInputData()) {
            return;
        }

        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        String poste = txtPoste.getText().trim();

        // Check for uniqueness of nom, prenom, poste combination
        if (!isUniqueTriplet(null, nom, prenom, poste)) {
            JOptionPane.showMessageDialog(this,
                    "Un employé avec le même nom, prénom et poste existe déjà.",
                    "Conflit de données",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Employe employe = new Employe(nom, prenom, poste);
            employeDAO.saveEmploye(employe);

            clearForm();
            loadEmployes();
            JOptionPane.showMessageDialog(this,
                    "Employé ajouté avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'ajout de l'employé: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEmploye() {
        // Validate selection
        if (txtCodeEmp.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un employé à modifier",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate input data
        if (!validateInputData()) {
            return;
        }

        Long codeEmp = Long.parseLong(txtCodeEmp.getText());
        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        String poste = txtPoste.getText().trim();

        // Check for uniqueness of nom, prenom, poste combination (excluding this employee)
        if (!isUniqueTriplet(codeEmp, nom, prenom, poste)) {
            JOptionPane.showMessageDialog(this,
                    "Un autre employé avec le même nom, prénom et poste existe déjà.",
                    "Conflit de données",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Employe employe = employeDAO.getEmployeById(codeEmp);
            if (employe == null) {
                JOptionPane.showMessageDialog(this,
                        "Employé non trouvé. Il a peut-être été supprimé.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                clearForm();
                loadEmployes();
                return;
            }

            employe.setNom(nom);
            employe.setPrenom(prenom);
            employe.setPoste(poste);

            employeDAO.updateEmploye(employe);

            clearForm();
            loadEmployes();
            JOptionPane.showMessageDialog(this,
                    "Employé modifié avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la modification de l'employé: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEmploye() {
        if (txtCodeEmp.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un employé à supprimer",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment supprimer cet employé?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Long codeEmp = Long.parseLong(txtCodeEmp.getText());
                employeDAO.deleteEmploye(codeEmp);

                clearForm();
                loadEmployes();
                JOptionPane.showMessageDialog(this,
                        "Employé supprimé avec succès",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la suppression de l'employé: " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchEmployes() {
        String searchTerm = txtSearch.getText().trim();
        if (searchTerm.isEmpty()) {
            loadEmployes();
            return;
        }

        String searchType = (String) cmbSearchType.getSelectedItem();
        List<Employe> employes;

        if ("Code".equals(searchType)) {
            try {
                Long codeEmp = Long.parseLong(searchTerm);
                Employe employe = employeDAO.getEmployeById(codeEmp);
                employes = employe != null ? List.of(employe) : List.of();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Veuillez entrer un numéro valide pour le code employé",
                        "Erreur de recherche",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else { // Search by name
            employes = employeDAO.searchEmployesByName(searchTerm);
        }

        populateTable(employes);

        if (employes.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Aucun employé trouvé avec ces critères",
                    "Résultat de recherche",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void clearForm() {
        txtCodeEmp.setText("");
        txtNom.setText("");
        txtPrenom.setText("");
        txtPoste.setText("");
        tblEmployes.clearSelection();
    }

    // Add this method to your EmployePanel class
    public void refresh() {
        loadEmployes();
        clearForm();
    }

    private void loadEmployes() {
        List<Employe> employes = employeDAO.getAllEmployes();
        populateTable(employes);
    }

    private void populateTable(List<Employe> employes) {
        tableModel.setRowCount(0);

        if (employes != null) {
            for (Employe employe : employes) {
                Object[] row = {
                        employe.getCodeEmp(),
                        employe.getNom(),
                        employe.getPrenom(),
                        employe.getPoste()
                };
                tableModel.addRow(row);
            }
        }
    }

    private void displaySelectedEmploye(int selectedRow) {
        Long codeEmp = (Long) tableModel.getValueAt(selectedRow, 0);
        Employe employe = employeDAO.getEmployeById(codeEmp);

        if (employe != null) {
            txtCodeEmp.setText(employe.getCodeEmp().toString());
            txtNom.setText(employe.getNom());
            txtPrenom.setText(employe.getPrenom());
            txtPoste.setText(employe.getPoste());
        }
    }

    /**
     * Validates input data from the form
     * @return true if all validations pass, false otherwise
     */
    private boolean validateInputData() {
        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        String poste = txtPoste.getText().trim();

        // Check for empty fields
        if (nom.isEmpty() || prenom.isEmpty() || poste.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez remplir tous les champs obligatoires",
                    "Champs requis",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate name (only letters, spaces, and hyphens)
        if (!nom.matches("^[a-zA-ZÀ-ÿ\\s\\-]+$")) {
            JOptionPane.showMessageDialog(this,
                    "Le nom ne doit contenir que des lettres, espaces et tirets",
                    "Format invalide",
                    JOptionPane.ERROR_MESSAGE);
            txtNom.requestFocus();
            return false;
        }

        // Validate first name (only letters, spaces, and hyphens)
        if (!prenom.matches("^[a-zA-ZÀ-ÿ\\s\\-]+$")) {
            JOptionPane.showMessageDialog(this,
                    "Le prénom ne doit contenir que des lettres, espaces et tirets",
                    "Format invalide",
                    JOptionPane.ERROR_MESSAGE);
            txtPrenom.requestFocus();
            return false;
        }

        // Validate job position (alphanumeric, spaces, and some special chars)
        if (!poste.matches("^[a-zA-ZÀ-ÿ0-9\\s\\-_&/()]+$")) {
            JOptionPane.showMessageDialog(this,
                    "Le poste ne doit contenir que des caractères alphanumériques et certains caractères spéciaux",
                    "Format invalide",
                    JOptionPane.ERROR_MESSAGE);
            txtPoste.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Checks if the combination of nom, prenom, poste is unique
     * @param currentCodeEmp The ID of the current employee (null for new employees)
     * @param nom The name to check
     * @param prenom The first name to check
     * @param poste The job position to check
     * @return true if the combination is unique, false otherwise
     */
    private boolean isUniqueTriplet(Long currentCodeEmp, String nom, String prenom, String poste) {
        List<Employe> allEmployes = employeDAO.getAllEmployes();

        for (Employe emp : allEmployes) {
            // Skip the current employee when updating
            if (currentCodeEmp != null && emp.getCodeEmp().equals(currentCodeEmp)) {
                continue;
            }

            // Check if the triplet already exists
            if (emp.getNom().equalsIgnoreCase(nom) &&
                    emp.getPrenom().equalsIgnoreCase(prenom) &&
                    emp.getPoste().equalsIgnoreCase(poste)) {
                return false;
            }
        }

        return true;
    }
}
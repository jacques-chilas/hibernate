package org.schoolproject.ui;

import org.schoolproject.dao.AffecterDAO;
import org.schoolproject.dao.EmployeDAO;
import org.schoolproject.dao.LieuDAO;
import org.schoolproject.model.Affecter;
import org.schoolproject.model.AffecterPK;
import org.schoolproject.model.Employe;
import org.schoolproject.model.Lieu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AffecterPanel extends JPanel {

    private JComboBox<EmployeItem> cmbEmploye;
    private JComboBox<LieuItem> cmbLieu;
    private JTextField txtDate;
    private JTable tblAffectations;
    private JButton btnSave;
    private JButton btnModify;
    private JButton btnDelete;
    private JButton btnClear;

    private AffecterDAO affecterDAO;
    private EmployeDAO employeDAO;
    private LieuDAO lieuDAO;
    private DefaultTableModel tableModel;
    private SimpleDateFormat dateFormat;

    // Track original values for modification
    private Long originalEmployeCode;
    private Long originalLieuCode;
    private Date originalDate;

    public AffecterPanel() {
        affecterDAO = new AffecterDAO();
        employeDAO = new EmployeDAO();
        lieuDAO = new LieuDAO();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        initComponents();
        loadEmployes();
        loadLieux();
        loadAffectations();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Détails de l'affectation"));

        formPanel.add(new JLabel("Employé:"));
        cmbEmploye = new JComboBox<>();
        formPanel.add(cmbEmploye);

        formPanel.add(new JLabel("Lieu:"));
        cmbLieu = new JComboBox<>();
        formPanel.add(cmbLieu);

        formPanel.add(new JLabel("Date (yyyy-MM-dd):"));
        txtDate = new JTextField();
        formPanel.add(txtDate);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));

        btnSave = new JButton("Ajouter");
        btnSave.addActionListener(e -> saveAffectation());
        buttonPanel.add(btnSave);

        btnModify = new JButton("Modifier");
        btnModify.addActionListener(e -> modifyAffectation());
        buttonPanel.add(btnModify);

        btnDelete = new JButton("Supprimer");
        btnDelete.addActionListener(e -> deleteAffectation());
        buttonPanel.add(btnDelete);

        btnClear = new JButton("Effacer");
        btnClear.addActionListener(e -> clearForm());
        buttonPanel.add(btnClear);

        // Table
        String[] columnNames = {"Code Emp", "Nom Employé", "Code Lieu", "Lieu", "Date"};
        tableModel = new DefaultTableModel(columnNames, 0);
        tblAffectations = new JTable(tableModel);
        tblAffectations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblAffectations.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tblAffectations.getSelectedRow();
                if (selectedRow != -1) {
                    displaySelectedAffectation(selectedRow);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblAffectations);

        // Top panel for form and buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add components to the main panel
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadEmployes() {
        cmbEmploye.removeAllItems();
        List<Employe> employes = employeDAO.getAllEmployes();

        if (employes != null) {
            for (Employe employe : employes) {
                cmbEmploye.addItem(new EmployeItem(employe));
            }
        }
    }

    private void loadLieux() {
        cmbLieu.removeAllItems();
        List<Lieu> lieux = lieuDAO.getAllLieux();

        if (lieux != null) {
            for (Lieu lieu : lieux) {
                cmbLieu.addItem(new LieuItem(lieu));
            }
        }
    }

    private boolean validateDate(String dateStr) {
        // Check if date is empty
        if (dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La date est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Check format (YYYY-MM-DD)
        if (!dateStr.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            JOptionPane.showMessageDialog(this, "Format de date invalide. Utilisez YYYY-MM-DD", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            String[] parts = dateStr.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            // Check month range (1-12)
            if (month < 1 || month > 12) {
                JOptionPane.showMessageDialog(this, "Le mois doit être entre 1 et 12", "Erreur", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Check day range based on month
            int maxDays;
            switch (month) {
                case 4: case 6: case 9: case 11:
                    maxDays = 30;
                    break;
                case 2:
                    // Check for leap year
                    boolean isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
                    maxDays = isLeapYear ? 29 : 28;
                    break;
                default:
                    maxDays = 31;
            }

            if (day < 1 || day > maxDays) {
                JOptionPane.showMessageDialog(this, "Jour invalide pour le mois " + month, "Erreur", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Final validation with Calendar
            Calendar cal = Calendar.getInstance();
            cal.setLenient(false);
            cal.set(year, month - 1, day);
            cal.getTime();

            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Format de date invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Date invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private boolean checkDuplicateAffectation(Long codeEmp, Long codeLieu, Date date) {
        List<Affecter> existingAffectations = affecterDAO.getAllAffectations();
        if (existingAffectations != null) {
            for (Affecter affectation : existingAffectations) {
                if (affectation.getEmploye().getCodeEmp().equals(codeEmp) &&
                        affectation.getLieu().getCodeLieu().equals(codeLieu) &&
                        dateFormat.format(affectation.getDate()).equals(dateFormat.format(date))) {
                    return true;
                }
            }
        }
        return false;
    }

    private void saveAffectation() {
        EmployeItem selectedEmployeItem = (EmployeItem) cmbEmploye.getSelectedItem();
        LieuItem selectedLieuItem = (LieuItem) cmbLieu.getSelectedItem();
        String dateStr = txtDate.getText().trim();

        if (selectedEmployeItem == null || selectedLieuItem == null) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate date before attempting to parse
        if (!validateDate(dateStr)) {
            return;
        }

        try {
            Date date = dateFormat.parse(dateStr);
            Employe employe = selectedEmployeItem.getEmploye();
            Lieu lieu = selectedLieuItem.getLieu();

            // Check if exact same affectation already exists
            if (checkDuplicateAffectation(employe.getCodeEmp(), lieu.getCodeLieu(), date)) {
                JOptionPane.showMessageDialog(this, "Cette affectation existe déjà pour cette date", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            affecterDAO.saveAffectation(employe, lieu, date);
            clearForm();
            loadAffectations();
            JOptionPane.showMessageDialog(this, "Affectation ajoutée avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);

        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Format de date invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifyAffectation() {
        int selectedRow = tblAffectations.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une affectation à modifier", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (originalEmployeCode == null || originalLieuCode == null || originalDate == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une affectation valide", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        EmployeItem selectedEmployeItem = (EmployeItem) cmbEmploye.getSelectedItem();
        LieuItem selectedLieuItem = (LieuItem) cmbLieu.getSelectedItem();
        String dateStr = txtDate.getText().trim();

        if (selectedEmployeItem == null || selectedLieuItem == null) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate date before attempting to parse
        if (!validateDate(dateStr)) {
            return;
        }

        try {
            Date newDate = dateFormat.parse(dateStr);
            Employe newEmploye = selectedEmployeItem.getEmploye();
            Lieu newLieu = selectedLieuItem.getLieu();

            // Count how many fields changed
            int changedFields = 0;
            if (!newEmploye.getCodeEmp().equals(originalEmployeCode)) changedFields++;
            if (!newLieu.getCodeLieu().equals(originalLieuCode)) changedFields++;
            if (!dateFormat.format(newDate).equals(dateFormat.format(originalDate))) changedFields++;

            // If more than one field changed, this should be a new entry not a modification
            if (changedFields > 1) {
                JOptionPane.showMessageDialog(this,
                        "Vous ne pouvez modifier qu'un seul champ à la fois (employé, lieu ou date). " +
                                "Pour changer plusieurs champs, supprimez cette affectation et créez-en une nouvelle.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if the modified affectation would create a duplicate
            if (checkDuplicateAffectation(newEmploye.getCodeEmp(), newLieu.getCodeLieu(), newDate)) {
                JOptionPane.showMessageDialog(this, "Cette affectation existe déjà pour cette date", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Delete the original affectation
            AffecterPK originalPk = new AffecterPK(originalEmployeCode, originalLieuCode);
            Affecter originalAffectation = affecterDAO.getAffectationById(originalPk);

            if (originalAffectation != null) {
                // Make sure we're deleting the right date record
                if (dateFormat.format(originalAffectation.getDate()).equals(dateFormat.format(originalDate))) {
                    affecterDAO.deleteAffectation(originalPk);

                    // Create the new affectation
                    affecterDAO.saveAffectation(newEmploye, newLieu, newDate);

                    clearForm();
                    loadAffectations();
                    JOptionPane.showMessageDialog(this, "Affectation modifiée avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Affectation introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Affectation introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Format de date invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAffectation() {
        int selectedRow = tblAffectations.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une affectation à supprimer", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer cette affectation?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Long codeEmp = (Long) tableModel.getValueAt(selectedRow, 0);
            Long codeLieu = (Long) tableModel.getValueAt(selectedRow, 2);
            String dateStr = (String) tableModel.getValueAt(selectedRow, 4);

            try {
                Date date = dateFormat.parse(dateStr);
                AffecterPK pk = new AffecterPK(codeEmp, codeLieu);

                // Get the affectation to check the date
                Affecter affectation = affecterDAO.getAffectationById(pk);
                if (affectation != null && dateFormat.format(affectation.getDate()).equals(dateStr)) {
                    affecterDAO.deleteAffectation(pk);
                    clearForm();
                    loadAffectations();
                    JOptionPane.showMessageDialog(this, "Affectation supprimée avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Affectation introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, "Format de date invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void refresh() {
        // Reload all data
        loadEmployes();
        loadLieux();
        loadAffectations();
        clearForm();
    }

    private void clearForm() {
        if (cmbEmploye.getItemCount() > 0) {
            cmbEmploye.setSelectedIndex(0);
        }
        if (cmbLieu.getItemCount() > 0) {
            cmbLieu.setSelectedIndex(0);
        }
        txtDate.setText("");
        tblAffectations.clearSelection();
        originalEmployeCode = null;
        originalLieuCode = null;
        originalDate = null;
    }

    private void loadAffectations() {
        List<Affecter> affectations = affecterDAO.getAllAffectations();
        populateTable(affectations);
    }

    private void populateTable(List<Affecter> affectations) {
        tableModel.setRowCount(0);

        if (affectations != null) {
            for (Affecter affecter : affectations) {
                Object[] row = {
                        affecter.getEmploye().getCodeEmp(),
                        affecter.getEmploye().getNom() + " " + affecter.getEmploye().getPrenom(),
                        affecter.getLieu().getCodeLieu(),
                        affecter.getLieu().getDesignation(),
                        dateFormat.format(affecter.getDate())
                };
                tableModel.addRow(row);
            }
        }
    }

    private void displaySelectedAffectation(int selectedRow) {
        Long codeEmp = (Long) tableModel.getValueAt(selectedRow, 0);
        Long codeLieu = (Long) tableModel.getValueAt(selectedRow, 2);
        String dateStr = (String) tableModel.getValueAt(selectedRow, 4);

        AffecterPK pk = new AffecterPK(codeEmp, codeLieu);
        Affecter affecter = affecterDAO.getAffectationById(pk);

        if (affecter != null && dateFormat.format(affecter.getDate()).equals(dateStr)) {
            // Store original values for modification
            originalEmployeCode = codeEmp;
            originalLieuCode = codeLieu;
            originalDate = affecter.getDate();

            // Select the employee in the combobox
            for (int i = 0; i < cmbEmploye.getItemCount(); i++) {
                EmployeItem item = cmbEmploye.getItemAt(i);
                if (item.getEmploye().getCodeEmp().equals(codeEmp)) {
                    cmbEmploye.setSelectedIndex(i);
                    break;
                }
            }

            // Select the lieu in the combobox
            for (int i = 0; i < cmbLieu.getItemCount(); i++) {
                LieuItem item = cmbLieu.getItemAt(i);
                if (item.getLieu().getCodeLieu().equals(codeLieu)) {
                    cmbLieu.setSelectedIndex(i);
                    break;
                }
            }

            txtDate.setText(dateFormat.format(affecter.getDate()));
        }
    }

    // Helper classes for ComboBox items
    private class EmployeItem {
        private Employe employe;

        public EmployeItem(Employe employe) {
            this.employe = employe;
        }

        public Employe getEmploye() {
            return employe;
        }

        @Override
        public String toString() {
            return employe.getCodeEmp() + " - " + employe.getNom() + " " + employe.getPrenom();
        }
    }

    private class LieuItem {
        private Lieu lieu;

        public LieuItem(Lieu lieu) {
            this.lieu = lieu;
        }

        public Lieu getLieu() {
            return lieu;
        }

        @Override
        public String toString() {
            return lieu.getCodeLieu() + " - " + lieu.getDesignation();
        }
    }
}
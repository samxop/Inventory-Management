package ui;

import dao.SupplierDAO;
import model.Supplier;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class SupplierPanel extends JPanel {
    private SupplierDAO supplierDAO = new SupplierDAO();
    private JTable table;
    private DefaultTableModel model;

    public SupplierPanel() {
        setLayout(new BorderLayout(5,5));
        model = new DefaultTableModel(new Object[]{"ID","Name","Contact","Email"},0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Add"); btnAdd.addActionListener(e -> onAdd());
        JButton btnEdit = new JButton("Edit"); btnEdit.addActionListener(e -> onEdit());
        JButton btnDelete = new JButton("Delete"); btnDelete.addActionListener(e -> onDelete());
        JButton btnRefresh = new JButton("Refresh"); btnRefresh.addActionListener(e -> loadData());
        top.add(btnAdd); top.add(btnEdit); top.add(btnDelete); top.add(btnRefresh);
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        loadData();
    }

    private void loadData() {
        try {
            model.setRowCount(0);
            List<Supplier> list = supplierDAO.getAll();
            for (Supplier s : list) model.addRow(new Object[]{s.getSupplierId(), s.getName(), s.getContactNumber(), s.getEmail()});
        } catch (SQLException e) { Dialogs.error(this, "Error loading suppliers: " + e.getMessage()); }
    }

    private void onAdd() {
        Supplier s = Dialogs.showSupplierDialog(this, null);
        if (s == null) return;
        try {
            if (supplierDAO.insert(s)) { Dialogs.info(this, "Supplier added"); loadData(); }
            else Dialogs.error(this, "Add failed");
        } catch (SQLException e) { Dialogs.error(this, "Error: " + e.getMessage()); }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row == -1) { Dialogs.info(this, "Select a supplier to edit"); return; }
        int id = (int) model.getValueAt(row, 0);
        try {
            Supplier s = supplierDAO.getById(id);
            if (s == null) { Dialogs.error(this, "Supplier not found"); return; }
            Supplier edited = Dialogs.showSupplierDialog(this, s);
            if (edited != null) {
                if (supplierDAO.update(edited)) { Dialogs.info(this, "Supplier updated"); loadData(); }
                else Dialogs.error(this, "Update failed");
            }
        } catch (SQLException e) { Dialogs.error(this, "Error: " + e.getMessage()); }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row == -1) { Dialogs.info(this, "Select a supplier to delete"); return; }
        int id = (int) model.getValueAt(row, 0);
        if (!Dialogs.confirm(this, "Delete selected supplier?")) return;
        try {
            if (supplierDAO.delete(id)) { Dialogs.info(this, "Deleted"); loadData(); }
            else Dialogs.error(this, "Delete failed (check foreign key constraints)");
        } catch (SQLException e) { Dialogs.error(this, "Error: " + e.getMessage()); }
    }
}

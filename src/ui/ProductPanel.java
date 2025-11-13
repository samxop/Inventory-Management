package ui;

import dao.ProductDAO;
import dao.SupplierDAO;
import model.Product;
import model.Supplier;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileWriter;
import java.sql.SQLException;
import java.util.List;

public class ProductPanel extends JPanel {
    private ProductDAO productDAO = new ProductDAO();
    private SupplierDAO supplierDAO = new SupplierDAO();
    private JTable table;
    private DefaultTableModel model;
    private JTextField tfSearch;
    private JLabel lblInventoryValue;

    public ProductPanel() {
        setLayout(new BorderLayout(5,5));
        model = new DefaultTableModel(new Object[]{"ID","Name","Category","Quantity","Price","Supplier ID"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel top = new JPanel(new BorderLayout(5,5));
        tfSearch = new JTextField();
        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(this::onSearch);
        JButton btnRefresh = new JButton("Refresh"); btnRefresh.addActionListener(e -> loadData());
        JPanel searchPane = new JPanel(new BorderLayout(5,5)); searchPane.add(tfSearch, BorderLayout.CENTER); searchPane.add(btnSearch, BorderLayout.EAST);
        top.add(searchPane, BorderLayout.CENTER);

        JPanel actions = new JPanel();
        JButton btnAdd = new JButton("Add"); btnAdd.addActionListener(e -> onAdd());
        JButton btnEdit = new JButton("Edit"); btnEdit.addActionListener(e -> onEdit());
        JButton btnDelete = new JButton("Delete"); btnDelete.addActionListener(e -> onDelete());
        JButton btnExport = new JButton("Export CSV"); btnExport.addActionListener(e -> onExport());
        actions.add(btnAdd); actions.add(btnEdit); actions.add(btnDelete); actions.add(btnExport); actions.add(btnRefresh);
        top.add(actions, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        lblInventoryValue = new JLabel("Total Inventory Value: 0.00");
        bottom.add(lblInventoryValue, BorderLayout.WEST);
        add(bottom, BorderLayout.SOUTH);

        loadData();
    }

    private void loadData() {
        try {
            model.setRowCount(0);
            List<Product> products = productDAO.getAll();
            for (Product p : products) {
                model.addRow(new Object[]{p.getProductId(), p.getName(), p.getCategory(), p.getQuantity(), p.getPrice(), p.getSupplierId()});
            }
            updateInventoryValue();
            highlightLowStock();
        } catch (SQLException e) {
            Dialogs.error(this, "Error loading products: " + e.getMessage());
        }
    }

    private void onSearch(ActionEvent e) {
        try {
            String term = tfSearch.getText().trim();
            model.setRowCount(0);
            if (term.isEmpty()) {
                loadData();
                return;
            }
            List<Product> products = productDAO.searchByNameOrId(term);
            for (Product p : products) model.addRow(new Object[]{p.getProductId(), p.getName(), p.getCategory(), p.getQuantity(), p.getPrice(), p.getSupplierId()});
            highlightLowStock();
        } catch (SQLException ex) {
            Dialogs.error(this, "Search failed: " + ex.getMessage());
        }
    }

    private void onAdd() {
        try {
            java.util.List<Supplier> suppliers = supplierDAO.getAll();
            Product p = Dialogs.showProductDialog(this, null, suppliers);
            if (p != null) {
                if (productDAO.insert(p)) { Dialogs.info(this, "Product added successfully"); loadData(); }
                else Dialogs.error(this, "Failed to add product");
            }
        } catch (SQLException e) { Dialogs.error(this, "Error: " + e.getMessage()); }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row == -1) { Dialogs.info(this, "Select a product to edit"); return; }
        int id = (int) model.getValueAt(row, 0);
        try {
            Product p = productDAO.getById(id);
            if (p == null) { Dialogs.error(this, "Product not found"); return; }
            java.util.List<Supplier> suppliers = supplierDAO.getAll();
            Product edited = Dialogs.showProductDialog(this, p, suppliers);
            if (edited != null) {
                if (productDAO.update(edited)) { Dialogs.info(this, "Product updated"); loadData(); }
                else Dialogs.error(this, "Update failed");
            }
        } catch (SQLException e) { Dialogs.error(this, "Error: " + e.getMessage()); }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row == -1) { Dialogs.info(this, "Select a product to delete"); return; }
        int id = (int) model.getValueAt(row, 0);
        if (!Dialogs.confirm(this, "Delete selected product?")) return;
        try {
            if (productDAO.delete(id)) { Dialogs.info(this, "Deleted"); loadData(); }
            else Dialogs.error(this, "Delete failed");
        } catch (SQLException e) { Dialogs.error(this, "Error: " + e.getMessage()); }
    }

    private void onExport() {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fw = new FileWriter(fc.getSelectedFile())) {
                fw.write("product_id,name,category,quantity,price,supplier_id\n");
                for (int r = 0; r < model.getRowCount(); r++) {
                    for (int c = 0; c < model.getColumnCount(); c++) {
                        fw.write(String.valueOf(model.getValueAt(r,c)));
                        if (c < model.getColumnCount()-1) fw.write(',');
                    }
                    fw.write('\n');
                }
                Dialogs.info(this, "Exported successfully");
            } catch (Exception ex) { Dialogs.error(this, "Export failed: " + ex.getMessage()); }
        }
    }

    private void updateInventoryValue() {
        try {
            double total = productDAO.getTotalInventoryValue();
            lblInventoryValue.setText(String.format("Total Inventory Value: %.2f", total));
        } catch (SQLException e) { lblInventoryValue.setText("Total Inventory Value: error"); }
    }

    private void highlightLowStock() {
        int threshold = 10;
        for (int r = 0; r < model.getRowCount(); r++) {
            Object qObj = model.getValueAt(r, 3);
            if (qObj instanceof Integer && ((Integer) qObj) < threshold) {
                table.setRowSelectionInterval(r, r);
                // simple visual: select first low stock row (can enhance with custom renderer)
                break;
            }
        }
    }
}

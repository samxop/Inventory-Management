package ui;

import model.Product;
import model.Supplier;

import javax.swing.*;
import java.awt.*;

public class Dialogs {
    public static boolean confirm(Component parent, String message) {
        int res = JOptionPane.showConfirmDialog(parent, message, "Confirm", JOptionPane.YES_NO_OPTION);
        return res == JOptionPane.YES_OPTION;
    }

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static Product showProductDialog(Component parent, Product defaultProduct, java.util.List<Supplier> suppliers) {
        JTextField tfName = new JTextField();
        JTextField tfCategory = new JTextField();
        JTextField tfQuantity = new JTextField();
        JTextField tfPrice = new JTextField();
        JComboBox<Supplier> cbSuppliers = new JComboBox<>();
        cbSuppliers.addItem(null);
        for (Supplier s : suppliers) cbSuppliers.addItem(s);

        if (defaultProduct != null) {
            tfName.setText(defaultProduct.getName());
            tfCategory.setText(defaultProduct.getCategory());
            tfQuantity.setText(String.valueOf(defaultProduct.getQuantity()));
            tfPrice.setText(String.valueOf(defaultProduct.getPrice()));
            if (defaultProduct.getSupplierId() != null) {
                for (int i = 0; i < cbSuppliers.getItemCount(); i++) {
                    Supplier si = cbSuppliers.getItemAt(i);
                    if (si != null && si.getSupplierId() == defaultProduct.getSupplierId()) {
                        cbSuppliers.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }

        JPanel panel = new JPanel(new GridLayout(0,1,5,5));
        panel.add(new JLabel("Name:")); panel.add(tfName);
        panel.add(new JLabel("Category:")); panel.add(tfCategory);
        panel.add(new JLabel("Quantity:")); panel.add(tfQuantity);
        panel.add(new JLabel("Price:")); panel.add(tfPrice);
        panel.add(new JLabel("Supplier:")); panel.add(cbSuppliers);

        int result = JOptionPane.showConfirmDialog(parent, panel, (defaultProduct == null ? "Add Product" : "Edit Product"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String name = tfName.getText().trim();
            String category = tfCategory.getText().trim();
            int quantity;
            double price;
            try {
                if (name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty");
                quantity = Integer.parseInt(tfQuantity.getText().trim());
                if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");
                price = Double.parseDouble(tfPrice.getText().trim());
                if (price < 0) throw new IllegalArgumentException("Price cannot be negative");
            } catch (Exception e) {
                Dialogs.error(parent, "Invalid input: " + e.getMessage());
                return null;
            }
            Product p = (defaultProduct == null) ? new Product() : defaultProduct;
            p.setName(name);
            p.setCategory(category);
            p.setQuantity(quantity);
            p.setPrice(price);
            Supplier sel = (Supplier) cbSuppliers.getSelectedItem();
            p.setSupplierId(sel == null ? null : sel.getSupplierId());
            return p;
        }
        return null;
    }

    public static Supplier showSupplierDialog(Component parent, Supplier defaultSupplier) {
        JTextField tfName = new JTextField();
        JTextField tfContact = new JTextField();
        JTextField tfEmail = new JTextField();

        if (defaultSupplier != null) {
            tfName.setText(defaultSupplier.getName());
            tfContact.setText(defaultSupplier.getContactNumber());
            tfEmail.setText(defaultSupplier.getEmail());
        }

        JPanel panel = new JPanel(new GridLayout(0,1,5,5));
        panel.add(new JLabel("Name:")); panel.add(tfName);
        panel.add(new JLabel("Contact Number:")); panel.add(tfContact);
        panel.add(new JLabel("Email:")); panel.add(tfEmail);

        int result = JOptionPane.showConfirmDialog(parent, panel, (defaultSupplier == null ? "Add Supplier" : "Edit Supplier"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String name = tfName.getText().trim();
            String contact = tfContact.getText().trim();
            String email = tfEmail.getText().trim();
            if (name.isEmpty()) { Dialogs.error(parent, "Name cannot be empty"); return null; }
            Supplier s = (defaultSupplier == null) ? new Supplier() : defaultSupplier;
            s.setName(name); s.setContactNumber(contact); s.setEmail(email);
            return s;
        }
        return null;
    }
}

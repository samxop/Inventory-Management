package dao;

import db.DBConnection;
import model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    public List<Product> getAll() throws SQLException {
        String sql = "SELECT product_id, name, category, quantity, price, supplier_id FROM products ORDER BY name";
        List<Product> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Product p = new Product(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getDouble(5),
                        (rs.getObject(6) == null ? null : rs.getInt(6)));
                list.add(p);
            }
        }
        return list;
    }

    public Product getById(int id) throws SQLException {
        String sql = "SELECT product_id, name, category, quantity, price, supplier_id FROM products WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Product(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getDouble(5),
                            (rs.getObject(6) == null ? null : rs.getInt(6)));
                }
            }
        }
        return null;
    }

    public List<Product> searchByNameOrId(String term) throws SQLException {
        String sql = "SELECT product_id, name, category, quantity, price, supplier_id FROM products WHERE name LIKE ? OR product_id = ?";
        List<Product> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + term + "%");
            int id = -1;
            try {
                id = Integer.parseInt(term);
            } catch (NumberFormatException ignored) {}
            ps.setInt(2, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = new Product(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getDouble(5),
                            (rs.getObject(6) == null ? null : rs.getInt(6)));
                    list.add(p);
                }
            }
        }
        return list;
    }

    public boolean insert(Product p) throws SQLException {
        String sql = "INSERT INTO products (name, category, quantity, price, supplier_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getCategory());
            ps.setInt(3, p.getQuantity());
            ps.setDouble(4, p.getPrice());
            if (p.getSupplierId() == null) ps.setNull(5, Types.INTEGER); else ps.setInt(5, p.getSupplierId());
            int affected = ps.executeUpdate();
            if (affected == 0) return false;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setProductId(keys.getInt(1));
            }
            return true;
        }
    }

    public boolean update(Product p) throws SQLException {
        String sql = "UPDATE products SET name = ?, category = ?, quantity = ?, price = ?, supplier_id = ? WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getCategory());
            ps.setInt(3, p.getQuantity());
            ps.setDouble(4, p.getPrice());
            if (p.getSupplierId() == null) ps.setNull(5, Types.INTEGER); else ps.setInt(5, p.getSupplierId());
            ps.setInt(6, p.getProductId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM products WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Product> getLowStock(int threshold) throws SQLException {
        String sql = "SELECT product_id, name, category, quantity, price, supplier_id FROM products WHERE quantity < ? ORDER BY quantity";
        List<Product> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, threshold);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Product(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getDouble(5),
                            (rs.getObject(6) == null ? null : rs.getInt(6))));
                }
            }
        }
        return list;
    }

    public double getTotalInventoryValue() throws SQLException {
        String sql = "SELECT SUM(quantity * price) FROM products";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        }
        return 0.0;
    }
}

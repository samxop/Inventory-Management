package dao;

import db.DBConnection;
import model.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {
    public List<Supplier> getAll() throws SQLException {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT supplier_id, name, contact_number, email FROM suppliers ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Supplier s = new Supplier(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
                list.add(s);
            }
        }
        return list;
    }

    public Supplier getById(int id) throws SQLException {
        String sql = "SELECT supplier_id, name, contact_number, email FROM suppliers WHERE supplier_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Supplier(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
                }
            }
        }
        return null;
    }

    public boolean insert(Supplier s) throws SQLException {
        String sql = "INSERT INTO suppliers (name, contact_number, email) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getContactNumber());
            ps.setString(3, s.getEmail());
            int affected = ps.executeUpdate();
            if (affected == 0) return false;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) s.setSupplierId(keys.getInt(1));
            }
            return true;
        }
    }

    public boolean update(Supplier s) throws SQLException {
        String sql = "UPDATE suppliers SET name = ?, contact_number = ?, email = ? WHERE supplier_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getContactNumber());
            ps.setString(3, s.getEmail());
            ps.setInt(4, s.getSupplierId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM suppliers WHERE supplier_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}

package com.mycompany.uas_project;

import java.sql.*;

public class MahasiswaDAO {
    public static Mahasiswa getMahasiswa(String nim, String nama) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM mahasiswa WHERE nim = ? AND nama = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nim);
            stmt.setString(2, nama);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Mahasiswa(
                    rs.getString("nim"),
                    rs.getString("nama"),
                    rs.getString("kelas"),
                    rs.getString("jurusan")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

package com.mycompany.uas_project;

import java.sql.*;

public class DataMahasiswa {
    public static Mahasiswa cariByNIM(String nim) {
        Mahasiswa mhs = null;

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM mahasiswa WHERE nim = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nim);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                mhs = new Mahasiswa(
                        rs.getString("nim"),
                        rs.getString("nama"),
                        rs.getString("kelas"),
                        rs.getString("jurusan")
                );
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mhs;
    }
}

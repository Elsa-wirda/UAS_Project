package com.mycompany.uas_project;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class FormKehadiran extends JPanel {
    private Mahasiswa mhs;
    private ArrayList<Kehadiran> listKehadiran = new ArrayList<>();

    public FormKehadiran(Mahasiswa mhs) {
        this.mhs = mhs;

        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        form.add(new JLabel("NIM:"));
        form.add(new JLabel(mhs.getNim()));
        form.add(new JLabel("Nama:"));
        form.add(new JLabel(mhs.getNama()));
        form.add(new JLabel("Kelas:"));
        form.add(new JLabel(mhs.getKelas()));
        form.add(new JLabel("Jurusan:"));
        form.add(new JLabel(mhs.getJurusan()));
        form.add(new JLabel("Tanggal:"));
        form.add(new JLabel(LocalDate.now().toString()));

        JButton btnHadir = new JButton("Absen Hadir");
        JButton btnKembali = new JButton("Kembali");

        form.add(btnHadir);
        form.add(btnKembali);

        add(form, BorderLayout.CENTER);

        btnHadir.addActionListener(e -> simpanAbsensi());
        btnKembali.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin kembali?", "Konfirmasi Kembali", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Container container = SwingUtilities.getAncestorOfClass(JDialog.class, this);
                if (container instanceof JDialog dialog) {
                    dialog.setContentPane(new UAS_Project());
                    dialog.pack();
                    dialog.setLocationRelativeTo(null);
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal kembali ke halaman login (bukan dalam JDialog).");
                }
            }
        });
    }

    private void simpanAbsensi() {
        String cekSql = "SELECT COUNT(*) FROM kehadiran WHERE nim = ? AND tanggal = ?";
        String insertSql = "INSERT INTO kehadiran (nim, nama, kelas, jurusan, tanggal, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {

            try (PreparedStatement psCek = conn.prepareStatement(cekSql)) {
                psCek.setString(1, mhs.getNim());
                psCek.setDate(2, Date.valueOf(LocalDate.now()));
                ResultSet rs = psCek.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "Anda sudah absen hari ini.");
                    return;
                }
            }

            try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                psInsert.setString(1, mhs.getNim());
                psInsert.setString(2, mhs.getNama());
                psInsert.setString(3, mhs.getKelas());
                psInsert.setString(4, mhs.getJurusan());
                psInsert.setDate(5, Date.valueOf(LocalDate.now()));
                psInsert.setString(6, "Hadir");

                int rows = psInsert.executeUpdate();
                if (rows > 0) {
                    // Tambahkan ke ArrayList
                    Kehadiran kehadiranBaru = new Kehadiran(
                        0, // ID bisa diabaikan atau di-set belakangan
                        mhs.getNim(),
                        LocalDate.now(),
                        "Hadir"
                    );
                    listKehadiran.add(kehadiranBaru);

                    JOptionPane.showMessageDialog(this, "Absensi berhasil ditambahkan ke daftar.");
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menyimpan absensi.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Kesalahan database:\n" + e.getMessage());
        }
    }
}

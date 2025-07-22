package com.mycompany.uas_project;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
    
public class AdminDashboard extends JPanel {
    private JPanel sidebar;
    private JPanel contentPanel;
    private Connection conn;

    public AdminDashboard() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(900, 550));

        try {
            conn = DBConnection.getConnection();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal koneksi database:\n" + ex.getMessage());
        }

        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 550));
        sidebar.setBackground(new Color(12, 12, 12));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel lblMenu = new JLabel(" Menu");
        lblMenu.setForeground(Color.WHITE);
        lblMenu.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sidebar.add(lblMenu);
        sidebar.add(Box.createVerticalStrut(20));

        JButton btnDashboard = createSidebarButton("Dashboard");
        JButton btnMahasiswa = createSidebarButton("Data Mahasiswa");
        JButton btnKehadiran = createSidebarButton("Data Kehadiran");
        JButton btnLogout = createSidebarButton("Logout");


        sidebar.add(btnDashboard);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnMahasiswa);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnKehadiran);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnLogout);
        
        


        contentPanel = new JPanel(new BorderLayout());
        JLabel welcome = new JLabel("Selamat Datang di Dashboard Admin", SwingConstants.CENTER);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 18));
        contentPanel.add(welcome, BorderLayout.CENTER);

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        btnDashboard.addActionListener(e -> showDashboard());
        btnMahasiswa.addActionListener(e -> showDataMahasiswa());
        btnKehadiran.addActionListener(e -> showDataKehadiran());
        btnLogout.addActionListener(e -> {
    int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin logout?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
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

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setPreferredSize(new Dimension(180, 35));
        button.setMaximumSize(new Dimension(180, 35));
        button.setBackground(new Color(100, 149, 237));
        button.setForeground(Color.WHITE);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    

    private void showDashboard() {
        contentPanel.removeAll();

        int totalMahasiswa = DashboardStats.getTotalMahasiswa();
        int sudahAbsen = DashboardStats.getTotalAbsenHariIni();
        int belumAbsen = totalMahasiswa - sudahAbsen;

        JPanel wrapperPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        wrapperPanel.add(createCard("Total Mahasiswa", totalMahasiswa));
        wrapperPanel.add(createCard("Sudah Absen", sudahAbsen));
        wrapperPanel.add(createCard("Belum Absen", belumAbsen));

        contentPanel.add(wrapperPanel, BorderLayout.NORTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createCard(String title, int value) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(200, 100));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblValue = new JLabel(String.valueOf(value));
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValue.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(10));
        card.add(lblValue);
        return card;
    }
private void showDataMahasiswa() {
    contentPanel.removeAll();

    String[] columns = {"NIM", "Nama", "Kelas", "Jurusan"};
    DefaultTableModel model = new DefaultTableModel(columns, 0);
    JTable table = new JTable(model);
    JScrollPane scrollPane = new JScrollPane(table);

    try (Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT * FROM mahasiswa")) {
        while (rs.next()) {
            model.addRow(new Object[]{
                    rs.getString("nim"),
                    rs.getString("nama"),
                    rs.getString("kelas"),
                    rs.getString("jurusan")
            });
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Gagal mengambil data mahasiswa: " + e.getMessage());
    }

    // Panel tombol
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton btnTambah = new JButton("Tambah");
    JButton btnEdit = new JButton("Edit");
    JButton btnHapus = new JButton("Hapus");

    btnTambah.addActionListener(e -> showFormMahasiswa(null, model));
    btnEdit.addActionListener(e -> {
        int row = table.getSelectedRow();
        if (row >= 0) {
            String nim = model.getValueAt(row, 0).toString();
            showFormMahasiswa(nim, model);
        } else {
            JOptionPane.showMessageDialog(this, "Pilih baris yang akan diedit.");
        }
    });
    btnHapus.addActionListener(e -> {
        int row = table.getSelectedRow();
        if (row >= 0) {
            String nim = model.getValueAt(row, 0).toString();
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus mahasiswa NIM " + nim + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM mahasiswa WHERE nim = ?")) {
                    ps.setString(1, nim);
                    ps.executeUpdate();
                    model.removeRow(row);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih baris yang akan dihapus.");
        }
    });

    buttonPanel.add(btnTambah);
    buttonPanel.add(btnEdit);
    buttonPanel.add(btnHapus);

    contentPanel.add(buttonPanel, BorderLayout.NORTH);
    contentPanel.add(scrollPane, BorderLayout.CENTER);
    contentPanel.revalidate();
    contentPanel.repaint();
}

private void showFormMahasiswa(String nim, DefaultTableModel model) {
    JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
    JTextField txtNIM = new JTextField();
    JTextField txtNama = new JTextField();
    JTextField txtKelas = new JTextField();
    JTextField txtJurusan = new JTextField();

    formPanel.add(new JLabel("NIM:"));
    formPanel.add(txtNIM);
    formPanel.add(new JLabel("Nama:"));
    formPanel.add(txtNama);
    formPanel.add(new JLabel("Kelas:"));
    formPanel.add(txtKelas);
    formPanel.add(new JLabel("Jurusan:"));
    formPanel.add(txtJurusan);

    if (nim != null) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM mahasiswa WHERE nim = ?")) {
            ps.setString(1, nim);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtNIM.setText(rs.getString("nim"));
                txtNama.setText(rs.getString("nama"));
                txtKelas.setText(rs.getString("kelas"));
                txtJurusan.setText(rs.getString("jurusan"));
                txtNIM.setEnabled(false); // NIM tidak bisa diedit
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal mengambil data: " + e.getMessage());
            return;
        }
    }

    int result = JOptionPane.showConfirmDialog(this, formPanel, nim == null ? "Tambah Mahasiswa" : "Edit Mahasiswa", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
        try {
            if (nim == null) { // Tambah baru
                String insert = "INSERT INTO mahasiswa (nim, nama, kelas, jurusan) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insert)) {
                    ps.setString(1, txtNIM.getText());
                    ps.setString(2, txtNama.getText());
                    ps.setString(3, txtKelas.getText());
                    ps.setString(4, txtJurusan.getText());
                    ps.executeUpdate();
                    model.addRow(new Object[]{txtNIM.getText(), txtNama.getText(), txtKelas.getText(), txtJurusan.getText()});
                }
            } else { // Update
                String update = "UPDATE mahasiswa SET nama = ?, kelas = ?, jurusan = ? WHERE nim = ?";
                try (PreparedStatement ps = conn.prepareStatement(update)) {
                    ps.setString(1, txtNama.getText());
                    ps.setString(2, txtKelas.getText());
                    ps.setString(3, txtJurusan.getText());
                    ps.setString(4, txtNIM.getText());
                    ps.executeUpdate();

                    // Perbarui tabel
                    int rowCount = model.getRowCount();
                    for (int i = 0; i < rowCount; i++) {
                        if (model.getValueAt(i, 0).equals(txtNIM.getText())) {
                            model.setValueAt(txtNama.getText(), i, 1);
                            model.setValueAt(txtKelas.getText(), i, 2);
                            model.setValueAt(txtJurusan.getText(), i, 3);
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data: " + ex.getMessage());
        }
    }
}



   private void showDataKehadiran() {
    contentPanel.removeAll();

    String[] columns = {"ID", "Tanggal", "NIM", "Nama", "Kelas", "Jurusan", "Status"};
    DefaultTableModel model = new DefaultTableModel(columns, 0);
    JTable table = new JTable(model);
    JScrollPane scrollPane = new JScrollPane(table);

    String query = """
        SELECT k.id, k.tanggal, m.nim, m.nama, m.kelas, m.jurusan, k.status
        FROM kehadiran k
        JOIN mahasiswa m ON k.nim = m.nim
        ORDER BY k.tanggal DESC
    """;

    try (Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {
        while (rs.next()) {
            model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("tanggal"),
                    rs.getString("nim"),
                    rs.getString("nama"),
                    rs.getString("kelas"),
                    rs.getString("jurusan"),
                    rs.getString("status")
            });
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Gagal mengambil data kehadiran: " + e.getMessage());
    }

    // Tombol Hapus Semua
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton btnHapusSemua = new JButton("Hapus Semua");

    btnHapusSemua.addActionListener(e -> {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus semua data kehadiran?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM kehadiran");
                model.setRowCount(0); // Kosongkan tabel setelah delete
                JOptionPane.showMessageDialog(this, "Semua data kehadiran berhasil dihapus.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus semua data: " + ex.getMessage());
            }
        }
    });

    buttonPanel.add(btnHapusSemua);

    contentPanel.add(buttonPanel, BorderLayout.NORTH);
    contentPanel.add(scrollPane, BorderLayout.CENTER);
    contentPanel.revalidate();
    contentPanel.repaint();
}
   
}


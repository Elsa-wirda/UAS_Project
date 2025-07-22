package com.mycompany.uas_project;

import javax.swing.*;
import java.awt.*;

public class UAS_Project extends JPanel {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtNIM, txtNama;
    private JButton btnLogin;
    private JRadioButton rbAdmin, rbMahasiswa;
    private JPanel formPanel;

    public UAS_Project() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        rbAdmin = new JRadioButton("Login Admin");
        rbMahasiswa = new JRadioButton("Login Mahasiswa");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbAdmin);
        bg.add(rbMahasiswa);
        rbAdmin.setSelected(true);
        rolePanel.add(rbAdmin);
        rolePanel.add(rbMahasiswa);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(rolePanel, gbc);

        formPanel = new JPanel(new GridBagLayout());
        loadAdminForm(); 

        gbc.gridy = 1;
        add(formPanel, gbc);

        btnLogin = new JButton("Login");
        btnLogin.setPreferredSize(new Dimension(120, 30));
        btnLogin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        loginPanel.add(btnLogin);

        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        add(loginPanel, gbc);

        rbAdmin.addActionListener(e -> loadAdminForm());
        rbMahasiswa.addActionListener(e -> loadMahasiswaForm());

        btnLogin.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (!(window instanceof JDialog dialog)) return;

            if (rbAdmin.isSelected()) {
                String user = txtUsername.getText();
                String pass = new String(txtPassword.getPassword());
                if (user.equals("123") && pass.equals("123")) {
                    dialog.setContentPane(new AdminDashboard());
                    dialog.pack();
                    dialog.setLocationRelativeTo(null);
                } else {
                    JOptionPane.showMessageDialog(this, "Login Admin gagal.");
                }
            } else {
                String nim = txtNIM.getText().trim();
                String nama = txtNama.getText().trim();
                if (nim.isEmpty() || nama.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Lengkapi NIM dan Nama.");
                    return;
                }

                Mahasiswa mhs = MahasiswaDAO.getMahasiswa(nim, nama);
                if (mhs == null) {
                    JOptionPane.showMessageDialog(this, "Data mahasiswa tidak ditemukan di database.");
                    return;
                }

                dialog.setContentPane(new FormKehadiran(mhs));
                dialog.pack();
                dialog.setLocationRelativeTo(null);
            }
        });
    }

    private void loadAdminForm() {
        formPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblUsername = new JLabel("Username:");
        txtUsername = new JTextField(15);
        JLabel lblPassword = new JLabel("Password:");
        txtPassword = new JPasswordField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(lblUsername, gbc);
        gbc.gridy++;
        formPanel.add(txtUsername, gbc);
        gbc.gridy++;
        formPanel.add(lblPassword, gbc);
        gbc.gridy++;
        formPanel.add(txtPassword, gbc);

        formPanel.revalidate();
        formPanel.repaint();
    }

    private void loadMahasiswaForm() {
        formPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblNIM = new JLabel("NIM:");
        txtNIM = new JTextField(15);
        JLabel lblNama = new JLabel("Nama:");
        txtNama = new JTextField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(lblNIM, gbc);
        gbc.gridy++;
        formPanel.add(txtNIM, gbc);
        gbc.gridy++;
        formPanel.add(lblNama, gbc);
        gbc.gridy++;
        formPanel.add(txtNama, gbc);

        formPanel.revalidate();
        formPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JDialog dialog = new JDialog((Frame) null, "Aplikasi Absensi", true);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setContentPane(new UAS_Project());
            dialog.setSize(400, 330);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        });
    }
}

package gui;

import dbconnection.DBUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Railway Reservation System");
        setSize(400, 300); // Increased size
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2, 15, 15));

        Font labelFont = new Font("SansSerif", Font.BOLD, 16);
        Font fieldFont = new Font("SansSerif", Font.PLAIN, 16);
        Font buttonFont = new Font("SansSerif", Font.BOLD, 16);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(labelFont);
        add(userLabel);

        usernameField = new JTextField();
        usernameField.setFont(fieldFont);
        add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(labelFont);
        add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(fieldFont);
        add(passwordField);

        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(buttonFont);
        loginBtn.addActionListener(e -> loginUser());
        add(loginBtn);

        JButton registerBtn = new JButton("Register");
        registerBtn.setFont(buttonFont);
        registerBtn.addActionListener(e -> {
            dispose();
            new RegisterFrame().setVisible(true);
        });
        add(registerBtn);
    }

    private void loginUser() {
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                dispose();
                new DashboardFrame(username).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}

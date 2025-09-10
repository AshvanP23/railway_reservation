package gui;

import dbconnection.DBUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class RegisterFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    public RegisterFrame() {
        setTitle("User Registration");
        setSize(600, 500); // Slightly bigger
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(6, 2, 15, 15));

        Font labelFont = new Font("SansSerif", Font.BOLD, 16);
        Font fieldFont = new Font("SansSerif", Font.PLAIN, 16);
        Font buttonFont = new Font("SansSerif", Font.BOLD, 16);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(labelFont);
        JTextField userField = new JTextField();
        userField.setFont(fieldFont);
        add(userLabel);
        add(userField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(labelFont);
        JPasswordField passField = new JPasswordField();
        passField.setFont(fieldFont);
        add(passLabel);
        add(passField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        JTextField emailField = new JTextField();
        emailField.setFont(fieldFont);
        add(emailLabel);
        add(emailField);

        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(labelFont);
        JTextField phoneField = new JTextField();
        phoneField.setFont(fieldFont);
        add(phoneLabel);
        add(phoneField);

        add(new JLabel()); // Empty label for spacing

        JButton registerBtn = new JButton("Register");
        registerBtn.setFont(buttonFont);
        registerBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();

            try (Connection conn = DBUtil.getConnection()) {
                String sql = "INSERT INTO users (username, password, email, phone) VALUES (?, ?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, username);
                pst.setString(2, password);
                pst.setString(3, email);
                pst.setString(4, phone);

                int rows = pst.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Registration Successful!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Registration Failed!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        add(registerBtn);
    }
}

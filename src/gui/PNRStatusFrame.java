package gui;

import dbconnection.DBUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class PNRStatusFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField pnrField;
    private JTextArea statusArea;

    public PNRStatusFrame() {
        setTitle("PNR Status Check");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        add(new NavigationPanel(this,null), BorderLayout.NORTH);
        Font labelFont = new Font("Arial", Font.BOLD, 16);
        Font buttonFont = new Font("Arial", Font.BOLD, 16);
        Font textAreaFont = new Font("Monospaced", Font.PLAIN, 14);

        JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JLabel label = new JLabel("Enter PNR Number:");
        label.setFont(labelFont);
        pnrField = new JTextField();
        pnrField.setFont(textAreaFont);
        topPanel.add(label);
        topPanel.add(pnrField);

        JButton checkBtn = new JButton("Check Status");
        checkBtn.setFont(buttonFont);
        checkBtn.addActionListener(e -> checkPNRStatus());

        statusArea = new JTextArea();
        statusArea.setEditable(false);
        statusArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(statusArea), BorderLayout.CENTER);
        add(checkBtn, BorderLayout.SOUTH);
    }

    private void checkPNRStatus() {
        String pnr = pnrField.getText().trim();

        if (pnr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid PNR number.");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            String query = """
                SELECT b.pnr_number, t.train_number, t.train_name, t.source_code, t.dest_code, 
                       p.name, p.age, p.gender
                FROM bookings b
                JOIN trains t ON b.train_id = t.train_id
                JOIN passengers p ON b.booking_id = p.booking_id
                WHERE b.pnr_number = ?
            """;

            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, pnr);
            ResultSet rs = pst.executeQuery();

            StringBuilder sb = new StringBuilder();
            boolean found = false;

            while (rs.next()) {
                if (!found) {
                    found = true;
                    sb.append("PNR: ").append(rs.getString("pnr_number")).append("\n");
                    sb.append("Train: ").append(rs.getString("train_number")).append(" - ")
                      .append(rs.getString("train_name")).append("\n");
                    sb.append("Route: ").append(rs.getString("source_code")).append(" → ")
                      .append(rs.getString("dest_code")).append("\n\n");
                    sb.append("Passenger Details:\n");
                }

                sb.append("- Name: ").append(rs.getString("name"))
                  .append(", Age: ").append(rs.getInt("age"))
                  .append(", Gender: ").append(rs.getString("gender"))
                  .append("\n");
            }

            if (!found) {
                statusArea.setText("No booking found for PNR: " + pnr);
            } else {
                statusArea.setText(sb.toString());
            }

        } catch (Exception ex) {
            statusArea.setText("Error retrieving PNR status: " + ex.getMessage());
        }
    }
}

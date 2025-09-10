package gui;

import dbconnection.DBUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TrainStatusFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField trainNumberField;
    private JTextArea statusArea;

    public TrainStatusFrame() {
        setTitle("Train Status");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        add(new NavigationPanel(this, null), BorderLayout.NORTH);
        Font labelFont = new Font("Arial", Font.BOLD, 16);
        Font buttonFont = new Font("Arial", Font.BOLD, 16);
        Font textAreaFont = new Font("Monospaced", Font.PLAIN, 14);

        JPanel inputPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JLabel label = new JLabel("Enter Train Number:");
        label.setFont(labelFont);
        trainNumberField = new JTextField();
        trainNumberField.setFont(textAreaFont);
        inputPanel.add(label);
        inputPanel.add(trainNumberField);

        JButton checkBtn = new JButton("Check Train Status");
        checkBtn.setFont(buttonFont);
        checkBtn.addActionListener(e -> fetchTrainStatus());

        statusArea = new JTextArea();
        statusArea.setEditable(false);
        statusArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(statusArea), BorderLayout.CENTER);
        add(checkBtn, BorderLayout.SOUTH);
    }

    private void fetchTrainStatus() {
        String trainNumber = trainNumberField.getText().trim();

        if (trainNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a train number.");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            // Get train details
            String trainQuery = "SELECT train_id, train_name, source_code, dest_code, total_hours FROM train WHERE train_number = ?";
            PreparedStatement pst = conn.prepareStatement(trainQuery);
            pst.setString(1, trainNumber);
            ResultSet rs = pst.executeQuery();

            if (!rs.next()) {
                statusArea.setText("Train not found for number: " + trainNumber);
                return;
            }

            int trainId = rs.getInt("train_id");
            String trainName = rs.getString("train_name");
            String source = rs.getString("source_code");
            String dest = rs.getString("dest_code");
            String hours = rs.getString("total_hours");

            StringBuilder sb = new StringBuilder();
            sb.append("Train Number: ").append(trainNumber).append("\n");
            sb.append("Train Name  : ").append(trainName).append("\n");
            sb.append("Source      : ").append(source).append("\n");
            sb.append("Destination : ").append(dest).append("\n");
            sb.append("Journey Time: ").append(hours).append(" hours").append("\n\n");

            // Get route details
            sb.append("Full Route:\n");
            sb.append("--------------------------------------------\n");

            String routeQuery = "SELECT station_name, route_order, arrival_time, departure_time FROM train_routes WHERE train_id = ? ORDER BY route_order ASC";
            pst = conn.prepareStatement(routeQuery);
            pst.setInt(1, trainId);
            rs = pst.executeQuery();

            while (rs.next()) {
                sb.append("Stop #").append(rs.getInt("route_order"))
                  .append(" → ").append(rs.getString("station_name"))
                  .append(" | Arrival: ").append(rs.getString("arrival_time"))
                  .append(" | Departure: ").append(rs.getString("departure_time"))
                  .append("\n");
            }

            statusArea.setText(sb.toString());

        } catch (Exception ex) {
            statusArea.setText("Error: " + ex.getMessage());
        }
    }
}

 package gui;

import dbconnection.DBUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class SearchTrainFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JComboBox<String> fromBox, toBox;
    private JTextArea resultArea;
    private String username;

    public SearchTrainFrame(String username) {
        this.username = username;

        setTitle("Search Trains");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        add(new NavigationPanel(this, username), BorderLayout.SOUTH);
        Font labelFont = new Font("Arial", Font.BOLD, 16);
        Font comboFont = new Font("Arial", Font.PLAIN, 16);
        Font buttonFont = new Font("Arial", Font.BOLD, 16);
        Font textAreaFont = new Font("Monospaced", Font.PLAIN, 14);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        fromBox = new JComboBox<>();
        toBox = new JComboBox<>();

        fromBox.setFont(comboFont);
        toBox.setFont(comboFont);

        JLabel fromLabel = new JLabel("From Station:");
        fromLabel.setFont(labelFont);
        JLabel toLabel = new JLabel("To Station:");
        toLabel.setFont(labelFont);

        loadStations();

        panel.add(fromLabel);
        panel.add(fromBox);
        panel.add(toLabel);
        panel.add(toBox);

        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(buttonFont);
        searchBtn.addActionListener(e -> searchTrains());
        panel.add(new JLabel()); // empty label for spacing
        panel.add(searchBtn);

        add(panel, BorderLayout.NORTH);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(textAreaFont);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);
    }

    private void loadStations() {
        try (Connection conn = DBUtil.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Code,StationName FROM Stations");

            while (rs.next()) {
            	String code= rs.getString("Code");
                String name = rs.getString("StationName");
                fromBox.addItem(code+" | "+name);
                toBox.addItem(code+" | "+name);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading stations: " + ex.getMessage());
        }
    }

    private void searchTrains() {
        String from = (String) fromBox.getSelectedItem();
        String to = (String) toBox.getSelectedItem();

        // Extract station codes (before the '|')
        String fromCode = from.split("\\|")[0].trim();
        String toCode = to.split("\\|")[0].trim();

        if (fromCode.equals(toCode)) {
            resultArea.setText("From and To stations cannot be same.");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            String query = """
                SELECT t.train_number, t.train_name, t.source_code, t.dest_code
                FROM trains t
                JOIN train_routes r1 ON t.train_id = r1.train_id
                JOIN train_routes r2 ON t.train_id = r2.train_id
                WHERE r1.station_code = ? AND r2.station_code = ? AND r1.stop_order < r2.stop_order
            """;

            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, fromCode);
            pst.setString(2, toCode);

            ResultSet rs = pst.executeQuery();
            StringBuilder sb = new StringBuilder();

            while (rs.next()) {
                sb.append("Train No: ").append(rs.getString("train_number"))
                  .append(", Name: ").append(rs.getString("train_name"))
                  .append(", Source: ").append(rs.getString("source_code"))
                  .append(", Destination: ").append(rs.getString("dest_code"))
                  .append("\n");
            }

            if (sb.length() == 0) {
                resultArea.setText("No trains found between " + fromCode + " and " + toCode);
            } else {
                resultArea.setText(sb.toString());
            }

        } catch (Exception ex) {
            resultArea.setText("Error: " + ex.getMessage());
        }
    }
}

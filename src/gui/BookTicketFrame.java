package gui;

import dbconnection.DBUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BookTicketFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private String username;
    private JComboBox<String> fromBox, toBox, trainListComboBox;
    private DefaultComboBoxModel<String> trainModel;
    private JSpinner dateSpinner;

    public BookTicketFrame(String username) {
        this.username = username;
        setTitle("Book Ticket - " + username);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        add(new NavigationPanel(this, username), BorderLayout.NORTH);

        Font font = new Font("Arial", Font.PLAIN, 16);

        fromBox = new JComboBox<>();
        toBox = new JComboBox<>();
        fromBox.setFont(font);
        toBox.setFont(font);

        JButton searchBtn = new JButton("Search Trains");
        searchBtn.setFont(font);

        trainModel = new DefaultComboBoxModel<>();
        trainListComboBox = new JComboBox<>(trainModel);
        trainListComboBox.setFont(font);

        JButton bookBtn = new JButton("Book Ticket");
        bookBtn.setFont(font);

        // Date Spinner Setup
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.add(Calendar.DATE, 10);
        Date maxDate = calendar.getTime();

        SpinnerDateModel dateModel = new SpinnerDateModel(today, today, maxDate, Calendar.DAY_OF_MONTH);
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(editor);
        dateSpinner.setFont(font);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.add(new JLabel("From Station:"));
        inputPanel.add(fromBox);
        inputPanel.add(new JLabel("To Station:"));
        inputPanel.add(toBox);
        inputPanel.add(new JLabel("Travel Date:"));
        inputPanel.add(dateSpinner);
        inputPanel.add(searchBtn);
        inputPanel.add(trainListComboBox);

        add(inputPanel, BorderLayout.CENTER);
        add(bookBtn, BorderLayout.SOUTH);

        loadStations();

        searchBtn.addActionListener(e -> searchTrains());

        bookBtn.addActionListener(e -> {
            String selected = (String) trainListComboBox.getSelectedItem();
            if (selected != null && !selected.contains("No trains")) {
                String[] parts = selected.split("\\s*\\|\\s*");
                int trainId = Integer.parseInt(parts[0]);

                String from = ((String) fromBox.getSelectedItem()).split("\\|")[0].trim();
                String to = ((String) toBox.getSelectedItem()).split("\\|")[0].trim();

                Date selectedDate = (Date) dateSpinner.getValue();
                String travelDate = new SimpleDateFormat("yyyy-MM-dd").format(selectedDate);

                new BookTrainTicketFrame(username, trainId, from, to, travelDate).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a valid train.");
            }
        });
    }

    private void loadStations() {
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Code, StationName FROM Stations")) {

            while (rs.next()) {
                String code = rs.getString("Code");
                String name = rs.getString("StationName");
                String full = code + " | " + name;
                fromBox.addItem(full);
                toBox.addItem(full);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading stations: " + ex.getMessage());
        }
    }

    private void searchTrains() {
        String from = ((String) fromBox.getSelectedItem()).split("\\|")[0].trim();
        String to = ((String) toBox.getSelectedItem()).split("\\|")[0].trim();

        if (from.equals(to)) {
            JOptionPane.showMessageDialog(this, "From and To stations cannot be same.");
            return;
        }

        String query = """
                SELECT t.train_id, t.train_number, t.train_name
                FROM trains t
                JOIN train_routes r1 ON t.train_id = r1.train_id
                JOIN train_routes r2 ON t.train_id = r2.train_id
                WHERE r1.station_code = ? AND r2.station_code = ? AND r1.stop_order < r2.stop_order
                """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, from);
            pst.setString(2, to);
            ResultSet rs = pst.executeQuery();

            trainModel.removeAllElements();
            while (rs.next()) {
                int trainId = rs.getInt("train_id");
                String entry = trainId + " | " + rs.getString("train_number") + " | " + rs.getString("train_name");
                trainModel.addElement(entry);
            }

            if (trainModel.getSize() == 0) {
                trainModel.addElement("No trains available.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error searching trains: " + ex.getMessage());
        }
    }
}

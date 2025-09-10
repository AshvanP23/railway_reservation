package gui;

import dbconnection.DBUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Random;

public class BookTrainTicketFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private String username;
    private int trainId;
    private int passengerCount;
    private int currentPassenger = 1;
    private JTextField nameField, ageField;
    private JComboBox<String> genderBox;
    private JLabel passengerLabel;
    private JButton nextButton;
    private int bookingId;
    private String from_station_code, to_station_code;
    private String travelDate;
    private int distance = 0;
    private int totalFare = 0;

    public BookTrainTicketFrame(String username, int trainId, String fsc, String tsc, String travelDate) {
        this.username = username;
        this.trainId = trainId;
        this.from_station_code = fsc;
        this.to_station_code = tsc;
        this.travelDate = travelDate;

        setTitle("Enter Passenger Details");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(6, 2, 10, 10));

        askPassengerCount();
    }

    private void askPassengerCount() {
        while (true) {
            String input = JOptionPane.showInputDialog(this, "Enter number of passengers (max 4):");
            if (input == null) {
                dispose();
                return;
            }
            try {
                passengerCount = Integer.parseInt(input);
                if (passengerCount >= 1 && passengerCount <= 4) {
                    break;
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter a number between 1 and 4.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter a number.");
            }
        }

        initBooking(); // create booking and get bookingId
        setupPassengerForm();
    }

    private void setupPassengerForm() {
        getContentPane().removeAll();

        Font font = new Font("Arial", Font.PLAIN, 16);
        passengerLabel = new JLabel("Passenger " + currentPassenger + " Details:");
        passengerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(passengerLabel);
        add(new JLabel(""));

        add(new JLabel("Name:"));
        nameField = new JTextField();
        nameField.setFont(font);
        add(nameField);

        add(new JLabel("Age:"));
        ageField = new JTextField();
        ageField.setFont(font);
        add(ageField);

        add(new JLabel("Gender:"));
        genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderBox.setFont(font);
        add(genderBox);

        nextButton = new JButton(currentPassenger == passengerCount ? "Finish Booking" : "Next");
        nextButton.setFont(font);
        nextButton.addActionListener(e -> handleNext());
        add(new JLabel(""));
        add(nextButton);

        revalidate();
        repaint();
    }

    private void handleNext() {
        String name = nameField.getText().trim();
        String ageStr = ageField.getText().trim();
        String gender = (String) genderBox.getSelectedItem();

        if (name.isEmpty() || ageStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        try {
            int age = Integer.parseInt(ageStr);
            savePassenger(name, age, gender);

            // Add fare for this passenger
            totalFare += distance;

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid age entered.");
            return;
        }

        if (currentPassenger == passengerCount) {
            String pnr = getPNR(bookingId);
            JOptionPane.showMessageDialog(this, "Booking successful!\nPNR: " + pnr + "\nTotal Fare: ₹" + totalFare);
            new BookingSummaryFrame(pnr, bookingId, totalFare).setVisible(true);
            dispose();
        } else {
            currentPassenger++;
            setupPassengerForm();
        }
    }

    private void initBooking() {
        try (Connection conn = DBUtil.getConnection()) {
            // Generate 6-digit PNR
            String pnr = generatePNR();

            // Fetch distance between stations
            String distQuery = "SELECT distance FROM train_routes WHERE train_id = ? AND from_station_code = ? AND to_station_code = ?";
            PreparedStatement distPst = conn.prepareStatement(distQuery);
            distPst.setInt(1, trainId);
            distPst.setString(2, from_station_code);
            distPst.setString(3, to_station_code);
            ResultSet distRs = distPst.executeQuery();
            if (distRs.next()) {
                distance = distRs.getInt("distance");
            } else {
                JOptionPane.showMessageDialog(this, "Distance not found for the selected route.");
                dispose();
                return;
            }

            // Insert booking
            String bookingQuery = "INSERT INTO bookings (username, train_id, from_station_code, to_station_code, travel_date, pnr_number, booking_date) VALUES (?, ?, ?, ?, ?, ?, NOW())";
            PreparedStatement pst = conn.prepareStatement(bookingQuery, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, username);
            pst.setInt(2, trainId);
            pst.setString(3, from_station_code);
            pst.setString(4, to_station_code);
            pst.setString(5, travelDate);
            pst.setString(6, pnr);
            pst.executeUpdate();

            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                bookingId = rs.getInt(1);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error creating booking: " + e.getMessage());
            dispose();
        }
    }

    private void savePassenger(String name, int age, String gender) {
        try (Connection conn = DBUtil.getConnection()) {
            String query = "INSERT INTO passengers (booking_id, name, age, gender) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, bookingId);
            pst.setString(2, name);
            pst.setInt(3, age);
            pst.setString(4, gender);
            pst.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving passenger: " + e.getMessage());
        }
    }

    private String generatePNR() {
        Random rand = new Random();
        int number = 100000 + rand.nextInt(900000); // ensures 6-digit number
        return String.valueOf(number);
    }

    private String getPNR(int bookingId) {
        try (Connection conn = DBUtil.getConnection()) {
            String query = "SELECT pnr_number FROM bookings WHERE booking_id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, bookingId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getString("pnr_number");
            }
        } catch (Exception e) {
            return "Unknown";
        }
        return "Unknown";
    }
}

package gui;

import dbconnection.DBUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class BookingSummaryFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private String pnr;
    private int bookingId;
    private int totalFare;

    public BookingSummaryFrame(String pnr, int bookingId, int totalFare) {
        this.pnr = pnr;
        this.bookingId = bookingId;
        this.totalFare = totalFare;

        setTitle("Booking Summary");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JTextArea summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(summaryArea);
        add(scrollPane, BorderLayout.CENTER);

        loadSummary(summaryArea);
    }

    private void loadSummary(JTextArea area) {
        try (Connection conn = DBUtil.getConnection()) {
            StringBuilder sb = new StringBuilder();
            sb.append("---------- Booking Summary ----------\n");
            sb.append("PNR Number     : ").append(pnr).append("\n");
            sb.append("Booking ID     : ").append(bookingId).append("\n");

            // Fetch booking info
            String bookingQuery = "SELECT b.username, b.train_id, b.from_station_code, b.to_station_code, b.travel_date, t.train_name " +
                    "FROM bookings b JOIN trains t ON b.train_id = t.train_id WHERE b.booking_id = ?";
            PreparedStatement pst = conn.prepareStatement(bookingQuery);
            pst.setInt(1, bookingId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                sb.append("Username        : ").append(rs.getString("username")).append("\n");
                sb.append("Train ID        : ").append(rs.getInt("train_id")).append("\n");
                sb.append("Train Name      : ").append(rs.getString("train_name")).append("\n");
                sb.append("From Station    : ").append(rs.getString("from_station_code")).append("\n");
                sb.append("To Station      : ").append(rs.getString("to_station_code")).append("\n");
                sb.append("Travel Date     : ").append(rs.getString("travel_date")).append("\n");
            }

            sb.append("\n--- Passenger Details ---\n");
            String passengerQuery = "SELECT name, age, gender FROM passengers WHERE booking_id = ?";
            pst = conn.prepareStatement(passengerQuery);
            pst.setInt(1, bookingId);
            rs = pst.executeQuery();
            int count = 1;
            while (rs.next()) {
                sb.append("Passenger ").append(count++).append(": ")
                        .append(rs.getString("name")).append(", Age: ")
                        .append(rs.getInt("age")).append(", Gender: ")
                        .append(rs.getString("gender")).append("\n");
            }

            sb.append("\nTotal Fare      : ₹").append(totalFare).append("\n");
            sb.append("--------------------------------------\n");

            area.setText(sb.toString());
        } catch (Exception e) {
            area.setText("Error loading booking summary: " + e.getMessage());
        }
    }
}

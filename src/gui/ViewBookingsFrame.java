package gui;

import dbconnection.DBUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ViewBookingsFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private String username;
    private JTextArea bookingsArea;

    public ViewBookingsFrame(String username) {
        this.username = username;

        setTitle("Your Bookings - " + username);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        add(new NavigationPanel(this, username), BorderLayout.NORTH);
        Font textAreaFont = new Font("Monospaced", Font.PLAIN, 14);
        bookingsArea = new JTextArea();
        bookingsArea.setFont(textAreaFont);
        bookingsArea.setEditable(false);
        bookingsArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(bookingsArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        add(scrollPane, BorderLayout.CENTER);

        loadBookings();
    }

    private void loadBookings() {
        String query = """
            SELECT b.booking_id, t.train_number, t.train_name,
                   s1.StationName AS from_station, s2.StationName AS to_station,
                   b.booking_date
            FROM bookings b
            JOIN trains t ON b.train_id = t.train_id
            JOIN Stations s1 ON b.from_station_code = s1.Code
            JOIN Stations s2 ON b.to_station_code = s2.Code
            WHERE b.username = ?
            ORDER BY b.booking_date DESC
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();

            StringBuilder sb = new StringBuilder();

            while (rs.next()) {
                sb.append("Booking ID : ").append(rs.getInt("booking_id")).append("\n");
                sb.append("Train      : ").append(rs.getString("train_number"))
                  .append(" - ").append(rs.getString("train_name")).append("\n");
                sb.append("From       : ").append(rs.getString("from_station")).append("\n");
                sb.append("To         : ").append(rs.getString("to_station")).append("\n");
                sb.append("Booked On  : ").append(rs.getDate("booking_date")).append("\n");
                sb.append("----------------------------------------------------------\n");
            }

            if (sb.length() == 0) {
                bookingsArea.setText("No bookings found.");
            } else {
                bookingsArea.setText(sb.toString());
            }

        } catch (Exception ex) {
            bookingsArea.setText("Error loading bookings: " + ex.getMessage());
        }
    }
}

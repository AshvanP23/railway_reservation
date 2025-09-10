package gui;

import dbconnection.DBUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CancelTicketFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField pnrField;
    private JButton cancelBtn;

    public CancelTicketFrame() {
        setTitle("Cancel Ticket");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(3, 1, 10, 10));
        add(new NavigationPanel(this,null), BorderLayout.PAGE_END);
        Font font = new Font("Arial", Font.PLAIN, 16);

        JLabel pnrLabel = new JLabel("Enter PNR Number:");
        pnrLabel.setFont(font);
        pnrLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(pnrLabel);

        pnrField = new JTextField(100);
        pnrField.setFont(font);
        add(pnrField);

        cancelBtn = new JButton("Cancel Ticket");
        cancelBtn.setFont(font);
        cancelBtn.addActionListener(e -> cancelTicket());
        add(cancelBtn);
    }

    private void cancelTicket() {
        String pnr = pnrField.getText().trim();

        if (pnr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid PNR number.");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);

            // Check if PNR exists
            String checkQuery = "SELECT booking_id FROM bookings WHERE pnr_number = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, pnr);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "No booking found for PNR: " + pnr);
                conn.rollback();
                return;
            }

            int bookingId = rs.getInt("booking_id");

            // Delete passengers
            String deletePassengers = "DELETE FROM passengers WHERE booking_id = ?";
            PreparedStatement delPassStmt = conn.prepareStatement(deletePassengers);
            delPassStmt.setInt(1, bookingId);
            delPassStmt.executeUpdate();

            // Delete booking
            String deleteBooking = "DELETE FROM bookings WHERE booking_id = ?";
            PreparedStatement delBookStmt = conn.prepareStatement(deleteBooking);
            delBookStmt.setInt(1, bookingId);
            delBookStmt.executeUpdate();

            conn.commit();

            JOptionPane.showMessageDialog(this, "Ticket cancelled successfully for PNR: " + pnr);
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cancelling ticket: " + ex.getMessage());
        }
    }
}

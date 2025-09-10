package gui;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private String username;

    public DashboardFrame(String username) {
        this.username = username;
        setTitle("Railway Reservation System\nWelcome" + username);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 1, 15, 15));

        Font buttonFont = new Font("Arial", Font.BOLD, 20);

        JButton searchBtn = new JButton("Search Trains");
        searchBtn.setFont(buttonFont);
        searchBtn.addActionListener(e -> new SearchTrainFrame(username).setVisible(true));
        add(searchBtn);

        JButton bookBtn = new JButton("Book Ticket");
        bookBtn.setFont(buttonFont);
        bookBtn.addActionListener(e -> new BookTicketFrame(username).setVisible(true));
        add(bookBtn);

        JButton viewBtn = new JButton("View Bookings");
        viewBtn.setFont(buttonFont);
        viewBtn.addActionListener(e -> new ViewBookingsFrame(username).setVisible(true));
        add(viewBtn);
        
        JButton pnrStatusBtn = new JButton("PNR Status");
        pnrStatusBtn.setFont(buttonFont);
        pnrStatusBtn.addActionListener(e -> new PNRStatusFrame().setVisible(true));
        add(pnrStatusBtn);
        
        JButton trainStatusBtn = new JButton("Train Status");
        trainStatusBtn.setFont(buttonFont);
        trainStatusBtn.addActionListener(e -> new TrainStatusFrame().setVisible(true));
        add(trainStatusBtn);


        JButton cancelBtn = new JButton("Cancel Booking");
        cancelBtn.setFont(buttonFont);
        cancelBtn.addActionListener(e -> new CancelTicketFrame().setVisible(true));
        add(cancelBtn);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(buttonFont);
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        add(logoutBtn);
    }
}

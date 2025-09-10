package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class NavigationPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    public NavigationPanel(JFrame parentFrame, String username) {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setBackground(new Color(220, 220, 220));  // Light gray

        Font font = new Font("Arial", Font.PLAIN, 14);

        String[] buttons = {
            "Book Ticket", "Cancel Ticket", "Train Status",
            "PNR Status", "View Trains", "Train Schedule", "Logout"
        };

        for (String label : buttons) {
            JButton btn = new JButton(label);
            btn.setFont(font);
            btn.setFocusable(false);
            add(btn);

            btn.addActionListener(e -> {
                parentFrame.dispose(); // Close current frame

                switch (label) {
                    case "Book Ticket":
                        new BookTicketFrame(username).setVisible(true);
                        break;
                    case "Cancel Ticket":
                        new CancelTicketFrame().setVisible(true);
                        break;
                    case "Train Status":
                        new TrainStatusFrame().setVisible(true);
                        break;
                    case "PNR Status":
                        new PNRStatusFrame().setVisible(true);
                        break;
                    case "Search Trains":
                        new SearchTrainFrame(null).setVisible(true);
                        break;
                    case "Dashboard":
                        new DashboardFrame(null).setVisible(true);
                        break;
                    case "Logout":
                        new LoginFrame().setVisible(true);
                        break;
                }
            });
        }
    }
}
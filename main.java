import objects.Show;
import objects.Ticket;
import utility.AdminCommandHandler;
import utility.BuyerCommandHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;

public class main {
    public static void main(String[] args) {
        // We use this hashmap as a mock DB
        HashMap<Integer, Show> shows = new HashMap<>();
        HashMap<UUID, Ticket> tickets = new HashMap<>();

        // Initialise utility methods
        AdminCommandHandler adminCommandHandler = new AdminCommandHandler(shows);
        BuyerCommandHandler buyerCommandHandler = new BuyerCommandHandler(shows, tickets);
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the show booking system, please enter your command.");
        while (scanner.hasNextLine()) {
            try {
                String[] commandline = scanner.nextLine().split(" ");
                switch (commandline[0]) {
                    // Admin commands
                    case "Setup": {
                        if (commandline.length != 5) {
                            System.out.println("Invalid params for Setup command");
                            break;
                        }
                        // Setup 1 5 10 2
                        Show show = adminCommandHandler.setup(Integer.parseInt(commandline[1]),
                                Integer.parseInt(commandline[2]), Integer.parseInt(commandline[3]),
                                Integer.parseInt(commandline[4]));
                        System.out.println("Show " + show.getNumber() + " set up successfully");
                        break;
                    }
                    case "View": {
                        if (commandline.length != 2) {
                            System.out.println("Invalid params for View command");
                            break;
                        }
                        // View 1
                        adminCommandHandler.viewAndPrintDetails(Integer.parseInt(commandline[1]));
                        break;
                    }
                    // Buyer commands
                    case "Availability": {
                        if (commandline.length != 2) {
                            System.out.println("Invalid params for Availability command");
                            break;
                        }
                        // Availability 1
                        buyerCommandHandler.retrieveAndPrintAvailability(Integer.parseInt(commandline[1]));
                        break;
                    }
                    case "Book": {
                        if (commandline.length != 4) {
                            System.out.println("Invalid params for Book command");
                            break;
                        }
                        // Book 1 98244587 A1,A2,A3
                        Ticket ticket = buyerCommandHandler.book(Integer.parseInt(commandline[1]), commandline[2],
                                commandline[3]);
                        System.out.println("Your ticket number is: " + ticket.getTicketNumber());
                        break;
                    }
                    case "Cancel": {
                        if (commandline.length != 3) {
                            System.out.println("Invalid params for Cancel command");
                            break;
                        }
                        // Cancel <UUID> 98244587
                        Ticket cancelledTicket = buyerCommandHandler.cancel(UUID.fromString(commandline[1]),
                                commandline[2], new Date());
                        System.out.println("Booking cancelled for ticket number: " + cancelledTicket.getTicketNumber());
                        break;
                    }
                    default:
                        System.out.println("Unknown command");
                        break;
                }
            }
            catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}

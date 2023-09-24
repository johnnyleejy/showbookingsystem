package utility;

import exceptions.InvalidSetupException;
import exceptions.NoSuchShowException;
import objects.Seat;
import objects.Show;
import objects.Ticket;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminCommandHandler {
    private final HashMap<Integer, Show> shows;
    public AdminCommandHandler(HashMap<Integer, Show> shows) {
        this.shows = shows;
    }

    /**
     * Creates a new show based on the provided details
     *
     * @param showNumber The number to assign the show to
     * @param numOfRows The number of rows for this show
     * @param numOfSeats The number of seats per row
     * @param cancellationWindow The cancellation window that the buyer is allowed to cancel their booking
     * @return The created show
     * @throws InvalidSetupException if the setup details are invalid
     */
    public Show setup(int showNumber, int numOfRows, int numOfSeats, int cancellationWindow) throws InvalidSetupException {
        // Seat validation
        if (numOfSeats > 10) {
            throw new InvalidSetupException("Number of seats must be 10 or less.");
        }
        if (numOfRows > 26) {
            throw new InvalidSetupException("Number of rows must be 26 or less.");
        }
        // Show number validation
        if (shows.containsKey(showNumber)) {
            throw new InvalidSetupException("Show " + showNumber + " already exists.");
        }
        ArrayList<Seat> seats = new ArrayList<>();
        for (int i = 0; i < numOfRows; i++) {
            for (int j = 0; j < numOfSeats; j++){
                String alphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                seats.add(new Seat(alphabets.charAt(i) + Integer.toString(j + 1), false));
            }
        }
        Show show = new Show(showNumber, seats, new ArrayList<>(), cancellationWindow);
        this.shows.put(showNumber, show);
        return show;
    }

    /**
     * Views and prints the booking details for a given show
     *
     * @param showNumber The show number to view
     * @return The show to view
     * @throws NoSuchShowException if the show does not exist
     */
    public Show viewAndPrintDetails(int showNumber) throws NoSuchShowException {
        Show show = shows.get(showNumber);
        if (show == null) {
            throw new NoSuchShowException();
        }
        // Print show
        System.out.println("Show number: " + show.getNumber());
        if (show.getTickets().size() == 0){
            System.out.println("No bookings yet.");
        }
        // Loop for each ticket and print ticket
        for (Ticket ticket: show.getTickets()) {
            System.out.println("--------------------------------------------");
            System.out.println("Ticket number: " + ticket.getTicketNumber());
            System.out.println("Buyer phone number: " + ticket.getPhoneNumber());
            System.out.println("Seats booked: ");
            for (Seat seat: ticket.getSeats()) {
                System.out.print(seat.getSeatNumber() + " ");
            }
            System.out.println(" ");
        }
        System.out.println("--------------------------------------------");
        return show;
    }
}

package utility;

import exceptions.BookingException;
import exceptions.CancelBookingException;
import exceptions.NoSuchShowException;
import exceptions.NoSuchTicketException;
import objects.Seat;
import objects.Show;
import objects.Ticket;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.PatternSyntaxException;

public class BuyerCommandHandler {
    private HashMap<Integer, Show> shows;
    private HashMap<UUID, Ticket> tickets;

    public BuyerCommandHandler(HashMap<Integer, Show> shows, HashMap<UUID, Ticket> tickets) {
        this.shows = shows;
        this.tickets = tickets;
    }

    /**
     * Views and prints the available seats for a given show
     *
     * @param showNumber The show number
     * @return A hashmap of the available seats
     */
    public HashMap<String, Seat> retrieveAndPrintAvailability(int showNumber) throws NoSuchShowException {
        Show show = shows.get(showNumber);
        if (show == null) {
            throw new NoSuchShowException();
        }
        HashMap<String, Seat> availableSeats = show.getAvailableSeats();
        System.out.println("Available seats: ");
        System.out.println("----------------------------------");
        for (Seat seat: availableSeats.values()) {
            System.out.print(seat.getSeatNumber() + " ");
        }
        System.out.println("");
        System.out.println("----------------------------------");
        return availableSeats;
    }

    /**
     * Books a ticket for a given show
     *
     * @param showNumber The show number for the booking
     * @param phoneNumber The phone number of the buyer
     * @param seats The seats to book
     * @return The newly created ticket
     */
    public Ticket book(int showNumber, String phoneNumber, String seats) throws NoSuchShowException, BookingException {
        Show show = shows.get(showNumber);
        if (show == null) {
            throw new NoSuchShowException();
        }
        if (!phoneNumber.matches("\\d+")) {
            throw new BookingException("Invalid phoneNumber.");
        }
        // Check if phone number has booked before
        if (show.hasBookedBefore(phoneNumber)) {
            // Can also return booking under the phoneNumber instead of throwing exception
            throw new BookingException("Phone number: " + phoneNumber + " already has an existing booking.");
        }
        try {
            String[] seatArray = seats.split(",");
            HashMap<String, Seat> availableSeatHashMap = new HashMap<>();
            // Add available seats to hashmap
            for (Seat seat: show.getSeats()) {
                if (!seat.isOccupied()) {
                    availableSeatHashMap.put(seat.getSeatNumber(), seat);
                }
            }
            // Validate if seat is available for booking
            ArrayList<String> seatNumbersForBooking = new ArrayList<>();
            for (int i = 0; i < seatArray.length; i++) {
                if (!availableSeatHashMap.containsKey(seatArray[i])) {
                    throw new BookingException("Seat: " + seatArray[i] + " is not available.");
                }
                seatNumbersForBooking.add(seatArray[i]);
            }
            // Create ticket
            UUID ticketNumber = UUID.randomUUID();
            Ticket ticket = new Ticket(phoneNumber, ticketNumber, seatNumbersForBooking, new Date(), show);
            tickets.put(ticketNumber, ticket);
            show.getTickets().add(ticket);
            // Update seat to occupied
            show.occupySeats(seatNumbersForBooking);
            return ticket;
        }
        catch (PatternSyntaxException e) {
            throw new PatternSyntaxException("Seats should be comma seperated.", ",", 0);
        }
    }

    /**
     * Cancels a booking for a given ticket number
     *
     * @param ticketNumber The ticket number for the booking
     * @param phoneNumber The phone number of the buyer
     * @return The cancelled ticket
     */
    public Ticket cancel(UUID ticketNumber, String phoneNumber, Date currentDate) throws NoSuchTicketException, CancelBookingException {
        Ticket ticket = tickets.get(ticketNumber);
        if (ticket == null) {
            throw new NoSuchTicketException();
        }
        if (!ticket.getPhoneNumber().equals(phoneNumber)) {
            throw new CancelBookingException("Phone number does not match the one in booking.");
        }
        // Reject cancellation if current time - ticket's bookingTime > show's cancellationWindow
        Show show = ticket.getShow();
        long minutesDiff = (currentDate.getTime() - ticket.getBookingTime().getTime()) / (60 * 1000);
        if (minutesDiff > show.getCancellationWindow()) {
            throw new CancelBookingException("You cannot cancel your booking as it passed the window of: " +
                    show.getCancellationWindow() + " minutes");
        }
        // Cancel booking
        show.cancelBooking(ticketNumber);
        tickets.remove(ticketNumber);
        return ticket;
    }
}

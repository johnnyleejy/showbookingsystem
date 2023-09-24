package utility;

import exceptions.BookingException;
import exceptions.CancelBookingException;
import exceptions.NoSuchShowException;
import exceptions.NoSuchTicketException;
import objects.Seat;
import objects.Show;
import objects.Ticket;

import java.util.*;

public class BuyerCommandHandler {
    private final HashMap<Integer, Show> shows;
    private final HashMap<UUID, Ticket> tickets;

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
        availableSeats.values().forEach(s -> System.out.print(s.getSeatNumber() + " "));
        System.out.println(" ");
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
        String[] seatArray = seats.split(",");
        // Validate if seat is available for booking
        HashMap<String, Seat> availableSeats = show.getAvailableSeats();
        ArrayList<Seat> seatsForBooking = new ArrayList<>();
        for (String value : seatArray) {
            if (!availableSeats.containsKey(value)) {
                throw new BookingException("Seat: " + value + " is not available.");
            }
            seatsForBooking.add(availableSeats.get(value));
        }
        // Create ticket
        UUID ticketNumber = UUID.randomUUID();
        Ticket ticket = new Ticket(phoneNumber, ticketNumber, seatsForBooking, new Date(), show);
        tickets.put(ticketNumber, ticket);
        show.getTickets().add(ticket);
        // Update seat to occupied
        seatsForBooking.forEach(s -> s.setOccupied(true));
        return ticket;
    }

    /**
     * Cancels a booking for a given ticket number
     *
     * @param ticketNumber The ticket number for the booking
     * @param phoneNumber The phone number of the buyer
     * @return The cancelled ticket
     */
    public Ticket cancel(UUID ticketNumber, String phoneNumber, Date currentDate) throws NoSuchTicketException,
            CancelBookingException, BookingException {
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

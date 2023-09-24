package objects;

import exceptions.BookingException;

import java.util.*;

public class Show {
    // ID of the show
    private int number;
    private ArrayList<Seat> seats;
    private ArrayList<Ticket> tickets;
    private int cancellationWindow;

    public Show(int number, ArrayList<Seat> seats, ArrayList<Ticket> tickets, int cancellationWindow) {
        this.setNumber(number);
        this.setSeats(seats);
        this.setCancellationWindow(cancellationWindow);
        this.setTickets(tickets);
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public ArrayList<Seat> getSeats() {
        return seats;
    }

    public void setSeats(ArrayList<Seat> seats) {
        this.seats = seats;
    }

    public int getCancellationWindow() {
        return cancellationWindow;
    }

    public void setCancellationWindow(int cancellationWindow) {
        this.cancellationWindow = cancellationWindow;
    }

    public ArrayList<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(ArrayList<Ticket> tickets) {
        this.tickets = tickets;
    }

    /**
     * Cancels a specific booking for a given show
     *
     * @param ticketNumber The ticket number to cancel
     * @throws BookingException if the ticket number is not found
     */
    public void cancelBooking(UUID ticketNumber) throws BookingException {
        Optional<Ticket> toRemove = this.getTickets().stream().filter(t -> t.getTicketNumber().equals(ticketNumber)).findFirst();
        if (toRemove.isEmpty()) {
            // This should not happen
            throw new BookingException("Unexpected state: ticket with UUID " + ticketNumber + " not found.");
        }
        // Set seats to vacant
        toRemove.get().getSeats().forEach(s -> s.setOccupied(false));
        // Remove cancelled ticket
        this.getTickets().remove(toRemove.get());
    }

    /**
     * Checks if the specified phone number has an existing booking
     *
     * @param phoneNumber The phone number to check
     * @return The boolean value whether this phone number has a booking for the show
     */
    public boolean hasBookedBefore(String phoneNumber) {
        Optional<Ticket> existingBooking = this.getTickets().stream().filter(t -> t.getPhoneNumber()
                .equals(phoneNumber)).findFirst();
        return existingBooking.isPresent();
    }

    /**
     * Returns the available seats for the show
     *
     * @return The hashmap containing available seats. The key will be the seat number while the value will be the seat
     */
    public HashMap<String, Seat> getAvailableSeats() {
        HashMap<String, Seat> availableSeats = new HashMap<>();
        this.getSeats().stream().filter(s -> !s.isOccupied()).forEach(s -> availableSeats.put(s.getSeatNumber(), s));
        return availableSeats;
    }
}

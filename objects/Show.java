package objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

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
     */
    public void cancelBooking(UUID ticketNumber) {
        Ticket toRemove = null;
        for (Ticket ticket: this.getTickets()) {
            if (ticket.getTicketNumber().equals(ticketNumber)) {
                toRemove = ticket;
                break;
            }
        }
        for (String seatNumber: toRemove.getSeatNumbers()) {
            Optional<Seat> occupiedSeat = this.getSeats().stream().filter(seat -> seat.getSeatNumber().equals(seatNumber)).findFirst();
            if (occupiedSeat.isPresent()) {
                // Set seats back to vacant
                occupiedSeat.get().setOccupied(false);
            }
        }
        // Remove cancelled ticket
        this.getTickets().remove(toRemove);
    }

    /**
     * Checks if the specified phone number has an existing booking
     *
     * @param phoneNumber The phone number to check
     * @return The boolean value whether this phone number has a booking for the show
     */
    public boolean hasBookedBefore(String phoneNumber) {
        for (Ticket ticket: this.getTickets()) {
            if (ticket.getPhoneNumber().equals(phoneNumber)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the available seats for the show
     *
     * @return The hashmap containing available seats. The key will be the seat number while the value will be the seat
     */
    public HashMap<String, Seat> getAvailableSeats() {
        HashMap<String, Seat> availableSeats = new HashMap<>();
        for (Seat seat: this.getSeats()) {
            if (!seat.isOccupied()) {
                availableSeats.put(seat.getSeatNumber(), seat);
            }
        }
        return availableSeats;
    }

    /**
     * Updates the isOccupied status for the specified seats to true
     *
     * @param seatNumbersForBooking The arraylist containing the seat numbers to occupy
     */
    public void occupySeats(ArrayList<String> seatNumbersForBooking) {
        HashMap<String, Seat> availableSeats = this.getAvailableSeats();
        for (String seatNumber: seatNumbersForBooking) {
            Seat seat = availableSeats.get(seatNumber);
            seat.setOccupied(true);
        }
    }
}

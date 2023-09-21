package objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Ticket {
    private String phoneNumber;
    // ID of the ticket
    private UUID ticketNumber;
    private ArrayList<String> seatNumbers;
    private Date bookingTime;
    private Show show;

    public Ticket(String phoneNumber, UUID ticketNumber, ArrayList<String> seats, Date bookingTime, Show show) {
        this.setPhoneNumber(phoneNumber);
        this.setSeatNumbers(seats);
        this.setTicketNumber(ticketNumber);
        this.setBookingTime(bookingTime);
        this.setShow(show);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UUID getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(UUID ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public ArrayList<String> getSeatNumbers() {
        return seatNumbers;
    }

    public void setSeatNumbers(ArrayList<String> seatNumbers) {
        this.seatNumbers = seatNumbers;
    }

    public Date getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(Date bookingTime) {
        this.bookingTime = bookingTime;
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
    }
}

package tests;

import exceptions.*;
import objects.Seat;
import objects.Show;
import objects.Ticket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import utility.AdminCommandHandler;
import utility.BuyerCommandHandler;

import java.util.*;

public class BuyerCommandHandlerTest {
    private final HashMap<Integer, Show> shows = new HashMap<>();
    private final HashMap<UUID, Ticket> tickets = new HashMap<>();

    AdminCommandHandler adminCommandHandler = new AdminCommandHandler(shows);
    BuyerCommandHandler buyerCommandHandler = new BuyerCommandHandler(shows, tickets);

    private void setupShow(int showNumber, int numOfRows, int numOfSeats, int cancellationWindow) throws InvalidSetupException {
        // Given a show number of 1 with 10 rows, 10 seats and 2 minutes cancellation window
        adminCommandHandler.setup(showNumber, numOfRows, numOfSeats, cancellationWindow);
    }

    @Nested
    class BookingErrorScenarios {
        @Test
        public void booking_fail_duplicatePhoneNumber() throws BookingException, NoSuchShowException, InvalidSetupException {
            // Given a created show and an existing booking
            int showNumber = 1;
            setupShow(showNumber, 10, 10, 2);
            String phoneNumber = "92344321";
            String seats = "A1,A2,A3";
            buyerCommandHandler.book(showNumber, phoneNumber, seats);
            Assertions.assertEquals(1, tickets.size());

            // When the buyer books a ticket for the same show again
            // Then an exception will be thrown
            Exception exception = Assertions.assertThrows(BookingException.class, () ->   buyerCommandHandler.book(showNumber, phoneNumber, "B1"));
            Assertions.assertEquals(exception.getMessage(), "Phone number: " + phoneNumber + " already has an existing booking.");

            // And no bookings will be created
            Assertions.assertEquals(1, tickets.size());
        }

        @Test
        public void booking_fail_invalidPhoneNumber() throws InvalidSetupException {
            // Given a created show
            int showNumber = 1;
            setupShow(showNumber, 10, 10, 2);

            // When the buyer books a ticket using an invalid phone number
            String phoneNumber = "92344321#!@!@,";
            String seats = "A1,A2,A3";

            // Then an exception will be thrown
            Exception exception = Assertions.assertThrows(BookingException.class, () ->   buyerCommandHandler.book(showNumber, phoneNumber, seats));
            Assertions.assertEquals(exception.getMessage(), "Invalid phoneNumber.");

            // And no bookings will be created
            Assertions.assertEquals(0, tickets.size());
        }

        @Test
        public void booking_fail_noSuchShow() throws InvalidSetupException {
            // Given a created show
            int showNumber = 1;
            setupShow(showNumber, 10, 10, 2);

            // When the buyer books a ticket using an invalid phone number
            String phoneNumber = "92344321";
            String seats = "A1,A2,A3";

            // Then an exception will be thrown
            Assertions.assertThrows(NoSuchShowException.class, () ->   buyerCommandHandler.book(20, phoneNumber, seats));

            // And no bookings will be created
            Assertions.assertEquals(0, tickets.size());
        }
    }

    @Nested
    class BookingSuccessScenarios {
        @Test
        public void booking_success() throws BookingException, NoSuchShowException, InvalidSetupException {
            // Given a created show
            int showNumber = 1;
            setupShow(showNumber, 10, 10, 2);

            // When the buyer books a ticket for 3 seats, A1, A2 and A3
            String phoneNumber = "92344321";
            String seats = "A1,A2,A3";
            Ticket ticket = buyerCommandHandler.book(showNumber, phoneNumber, seats);

            // Then the booking should be created successfully
            Assertions.assertEquals(1, tickets.size());
            Assertions.assertNotNull(ticket.getTicketNumber());
            Assertions.assertEquals(showNumber, ticket.getShow().getNumber());
            Assertions.assertEquals(phoneNumber, ticket.getPhoneNumber());
            Assertions.assertEquals(ticket.getSeatNumbers().size(), seats.split(",").length);
            Assertions.assertArrayEquals(ticket.getSeatNumbers().toArray(), new ArrayList<>(Arrays.asList("A1", "A2", "A3")).toArray());
            Assertions.assertEquals(1, shows.get(showNumber).getTickets().size());
        }
    }

    @Nested
    class RetrieveAndPrintAvailabilitySuccessScenarios {
        @Test
        public void retrieveAndPrintAvailability_success() throws BookingException, NoSuchShowException, InvalidSetupException {
            // Given a created show
            int showNumber = 1;
            int numOfRows = 3;
            int numOfSeats = 3;
            setupShow(showNumber, numOfRows, numOfSeats, 2);

            // When retrieveAndPrintAvailability is called
            HashMap<String, Seat> availableSeats = buyerCommandHandler.retrieveAndPrintAvailability(showNumber);

            // Then the available seats should be retrieved successfully
            Assertions.assertEquals(numOfRows * numOfSeats, availableSeats.size());
            Object[] expectedAvailableSeats = availableSeats.keySet().toArray();
            Arrays.sort(expectedAvailableSeats);
            Assertions.assertArrayEquals(expectedAvailableSeats, new String[]{"A1", "A2", "A3", "B1", "B2", "B3",
                    "C1", "C2", "C3"});

            // And when a booking is made
            buyerCommandHandler.book(showNumber, "98322213", "A1,A2,A3");

            // Then the available seats should be updated accordingly
            availableSeats = buyerCommandHandler.retrieveAndPrintAvailability(showNumber);
            Assertions.assertEquals(numOfRows * numOfSeats - 3, availableSeats.size());
            expectedAvailableSeats = availableSeats.keySet().toArray();
            Arrays.sort(expectedAvailableSeats);
            Assertions.assertArrayEquals(expectedAvailableSeats, new String[]{"B1", "B2", "B3", "C1", "C2", "C3"});
        }
    }

    @Nested
    class RetrieveAndPrintAvailabilityErrorScenarios {
        @Test
        public void retrieveAndPrintAvailability_fail_noSuchShow() {
            // When retrieveAndPrintAvailability is called on an invalid show number
            // Then an exception will be thrown
            Assertions.assertThrows(NoSuchShowException.class, () ->   buyerCommandHandler.retrieveAndPrintAvailability(20));
        }
    }

    @Nested
    class CancelBookingErrorScenarios {
        @Test
        public void cancel_fail_noSuchBooking() throws InvalidSetupException, BookingException, NoSuchShowException {
            // Given a created show and a valid booking
            int showNumber = 1;
            setupShow(showNumber, 10, 10, 2);
            String phoneNumber = "92344321";
            String seats = "A1,A2,A3";
            buyerCommandHandler.book(showNumber, phoneNumber, seats);
            Assertions.assertEquals(1, tickets.size());

            // When the buyer cancels with an invalid ticket number
            // Then an exception will be thrown
            Assertions.assertThrows(NoSuchTicketException.class, () -> buyerCommandHandler.cancel(UUID.randomUUID(),
                    phoneNumber, new Date()));

            // And no tickets will be cancelled
            Assertions.assertEquals(1, tickets.size());
        }

        @Test
        public void cancel_fail_phoneNumberDoesNotMatchBooking() throws InvalidSetupException, BookingException, NoSuchShowException {
            // Given a created show and a valid booking
            int showNumber = 1;
            setupShow(showNumber, 10, 10, 2);
            String phoneNumber = "92344321";
            String seats = "A1,A2,A3";
            Ticket ticket = buyerCommandHandler.book(showNumber, phoneNumber, seats);
            Assertions.assertEquals(1, tickets.size());

            // When the buyer cancels with a different phone number
            // Then an exception will be thrown
            Exception exception = Assertions.assertThrows(CancelBookingException.class, () -> buyerCommandHandler.cancel(
                    ticket.getTicketNumber(), "123456", new Date()));
            Assertions.assertEquals(exception.getMessage(), "Phone number does not match the one in booking.");

            // And no tickets will be cancelled
            Assertions.assertEquals(1, tickets.size());
        }

        @Test
        public void cancel_fail_exceedCancellationWindow() throws InvalidSetupException, BookingException, NoSuchShowException {
            // Given a created show and a valid booking
            int showNumber = 1;
            int numOfRows = 3;
            int numOfSeats = 3;
            int cancellationWindow = 2;
            setupShow(showNumber, numOfRows, numOfSeats, cancellationWindow);
            String phoneNumber = "92344321";
            String seats = "A1,A2,A3";
            Ticket ticket = buyerCommandHandler.book(showNumber, phoneNumber, seats);
            Assertions.assertEquals(1, tickets.size());

            // When the buyer attempts to cancel the booking 1 minute after the cancellation window
            // Then an exception will be thrown
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE,  cancellationWindow + 1);
            Exception exception = Assertions.assertThrows(CancelBookingException.class, () -> buyerCommandHandler.cancel(
                    ticket.getTicketNumber(), phoneNumber, calendar.getTime()));
            Assertions.assertEquals(exception.getMessage(), "You cannot cancel your booking as it passed the window of: " +
                    ticket.getShow().getCancellationWindow() + " minutes");

            // And no tickets will be cancelled
            Assertions.assertEquals(1, tickets.size());
        }
    }

    @Nested
    class CancelBookingSuccessScenarios {
        @Test
        public void cancel_success() throws BookingException, NoSuchShowException, InvalidSetupException,
                NoSuchTicketException, CancelBookingException {
            // Given a created show and a valid booking
            int showNumber = 1;
            int numOfRows = 3;
            int numOfSeats = 3;
            setupShow(showNumber, numOfRows, numOfSeats, 2);
            String phoneNumber = "92344321";
            String seats = "A1,A2,A3";
            Ticket ticket = buyerCommandHandler.book(showNumber, phoneNumber, seats);
            Assertions.assertEquals(1, tickets.size());

            // When the buyer cancels the ticket
            buyerCommandHandler.cancel(ticket.getTicketNumber(), phoneNumber, new Date());

            // Then the ticket should be cancelled
            Assertions.assertEquals(0, tickets.size());
            Assertions.assertEquals(0, shows.get(showNumber).getTickets().size());

            // And the booked seats should be vacant again
            Assertions.assertEquals(numOfRows * numOfSeats, shows.get(showNumber).getSeats().size());
            Object[] expectedAvailableSeats = shows.get(showNumber).getAvailableSeats().keySet().toArray();
            Arrays.sort(expectedAvailableSeats);
            Assertions.assertArrayEquals(expectedAvailableSeats, new String[]{"A1", "A2", "A3", "B1", "B2", "B3", "C1", "C2", "C3"});
        }
    }
}

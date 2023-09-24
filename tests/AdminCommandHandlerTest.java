package tests;

import exceptions.BookingException;
import exceptions.InvalidSetupException;
import exceptions.NoSuchShowException;
import objects.Show;
import objects.Ticket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import utility.AdminCommandHandler;
import utility.BuyerCommandHandler;

import java.util.HashMap;
import java.util.UUID;

public class AdminCommandHandlerTest {
    private final HashMap<Integer, Show> shows = new HashMap<>();
    private final HashMap<UUID, Ticket> tickets = new HashMap<>();

    AdminCommandHandler adminCommandHandler = new AdminCommandHandler(shows);
    BuyerCommandHandler buyerCommandHandler = new BuyerCommandHandler(shows, tickets);

    @Nested
    class SetupSuccessScenarios {
        @Test
        public void setup_success() throws InvalidSetupException {
            // Given a show number of 1 with 10 rows, 10 seats and 2 minutes cancellation window
            int showNumber = 1;
            int numOfRows = 10;
            int numOfSeats = 10;
            int cancellationWindow = 2;

            // When the setup command is called
            Show show = adminCommandHandler.setup(showNumber, numOfRows, numOfSeats, cancellationWindow);

            // Then the show should be created successfully
            Assertions.assertEquals(1, shows.size());
            Assertions.assertEquals(showNumber, show.getNumber());
            Assertions.assertEquals(cancellationWindow, show.getCancellationWindow());
            Assertions.assertEquals(0, show.getTickets().size());
            Assertions.assertEquals(numOfRows * numOfSeats, show.getSeats().size());
            Assertions.assertEquals(numOfRows * numOfSeats, show.getAvailableSeats().size());
        }
    }

    @Nested
    class SetupErrorScenarios {
        @Test
        public void setup_fail_numOfRowsExceed26() {
            // Given a show number of 1 with 27 rows, 10 seats and 2 minutes cancellation window
            // When the setup command is called
            int showNumber = 1;
            int numOfRows = 27;
            int numOfSeats = 10;
            int cancellationWindow = 2;

            // Then the show setup should fail
            Exception exception = Assertions.assertThrows(InvalidSetupException.class, () -> adminCommandHandler.setup(
                    showNumber, numOfRows, numOfSeats, cancellationWindow));
            Assertions.assertEquals(exception.getMessage(), "Number of rows must be 26 or less.");

            // And no shows should be created
            Assertions.assertEquals(0, shows.size());
        }

        @Test
        public void setup_fail_numOfSeatsExceed10() {
            // Given a show number of 1 with 10 rows, 11 seats and 2 minutes cancellation window
            // When the setup command is called
            int showNumber = 1;
            int numOfRows = 10;
            int numOfSeats = 11;
            int cancellationWindow = 2;

            // Then the show setup should fail
            Exception exception = Assertions.assertThrows(InvalidSetupException.class, () -> adminCommandHandler.setup(
                    showNumber, numOfRows, numOfSeats, cancellationWindow));
            Assertions.assertEquals(exception.getMessage(), "Number of seats must be 10 or less.");

            // And no shows should be created
            Assertions.assertEquals(0, shows.size());
        }

        @Test
        public void setup_fail_duplicateShow() throws InvalidSetupException {
            // Given a created show
            int showNumber = 1;
            int numOfRows = 10;
            int numOfSeats = 10;
            int cancellationWindow = 2;
            adminCommandHandler.setup(showNumber, numOfRows, numOfSeats, cancellationWindow);
            Assertions.assertEquals(1, shows.size());

            // When another show with the same show number is added
            // Then the show setup should fail
            Exception exception = Assertions.assertThrows(InvalidSetupException.class, () -> adminCommandHandler.setup(
                    showNumber, numOfRows, numOfSeats, cancellationWindow));
            Assertions.assertEquals(exception.getMessage(), "Show " + showNumber + " already exists.");

            // And the second show should not be created
            Assertions.assertEquals(1, shows.size());
        }
    }

    @Nested
    class ViewAndPrintDetails {
        @Test
        public void viewAndPrintDetails_success_noBookings() throws InvalidSetupException, NoSuchShowException {
            // Given a created show
            int showNumber = 1;
            int numOfRows = 10;
            int numOfSeats = 10;
            int cancellationWindow = 2;
            adminCommandHandler.setup(showNumber, numOfRows, numOfSeats, cancellationWindow);

            // When the show is viewed
            Show show = adminCommandHandler.viewAndPrintDetails(showNumber);

            // Then the correct show should be returned
            Assertions.assertEquals(1, shows.size());
            Assertions.assertEquals(showNumber, show.getNumber());
            Assertions.assertEquals(cancellationWindow, show.getCancellationWindow());

            // And the show should reflect no bookings
            Assertions.assertEquals(0, show.getTickets().size());
            Assertions.assertEquals(numOfRows * numOfSeats, show.getSeats().size());
            Assertions.assertEquals(numOfRows * numOfSeats, show.getAvailableSeats().size());
        }

        @Test
        public void viewAndPrintDetails_success_withBooking() throws InvalidSetupException, NoSuchShowException, BookingException {
            // Given a created show with bookings
            int showNumber = 1;
            int numOfRows = 10;
            int numOfSeats = 10;
            int cancellationWindow = 2;
            adminCommandHandler.setup(showNumber, numOfRows, numOfSeats, cancellationWindow);
            String phoneNumber = "92344321";
            String seats = "A1,A2,A3";
            buyerCommandHandler.book(showNumber, phoneNumber, seats);
            Assertions.assertEquals(1, tickets.size());

            // When the show is viewed
            Show show = adminCommandHandler.viewAndPrintDetails(showNumber);

            // Then the correct show should be returned
            Assertions.assertEquals(1, shows.size());
            Assertions.assertEquals(showNumber, show.getNumber());
            Assertions.assertEquals(cancellationWindow, show.getCancellationWindow());
            Assertions.assertEquals(numOfRows * numOfSeats, show.getSeats().size());

            // And the show should reflect the correct booking and seat availabilities
            Assertions.assertEquals(1, show.getTickets().size());
            Assertions.assertEquals(numOfRows * numOfSeats - 3, show.getAvailableSeats().size());
        }
    }

    @Nested
    class ViewAndPrintDetailsErrorScenarios {
        @Test
        public void viewAndPrintDetails_fail_noSuchShow() {
            // When viewing a non-existent show
            // Then an exception will be thrown
            Assertions.assertThrows(NoSuchShowException.class, () -> adminCommandHandler.
                    viewAndPrintDetails(1));
        }
    }
}

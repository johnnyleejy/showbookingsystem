# Show Booking System

# About this application

This is a simple Java CLI application that allows you to manage shows and make bookings. Examples are provided below.

## Assumptions made
1. Buyer cannot book seats that are already occupied
2. Cancelling a ticket will free the booked seats for other buyers
3. Buyer cannot make bookings for shows that don't exist
4. Other assumptions can be found in the written test cases

## Sample commands:

**Admin**
1. Setup shows: `Setup 1 5 10 2`
2. View shows and bookings: `View 1`

**Buyer**
1. View available seats for a shows: `Availability 1`
2. Book seats for a show: `Book 1 98244587 A1,A2,A3`
3. Cancel booking for a show: `Cancel <UUID> 98244587`

## How to use this application

1. Run `main.java`
2. Interact with the CLI with your command inputs

## Test cases: 

1. Admin test cases: [AdminCommandHandlerTest.java](https://github.com/johnnyleejy/showbookingsystem/blob/master/tests/AdminCommandHandlerTest.java)
2. Buyer test cases:  [BuyerCommandHandlerTest.java](https://github.com/johnnyleejy/showbookingsystem/blob/master/tests/BuyerCommandHandlerTest.java)

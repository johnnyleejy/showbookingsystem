package objects;

public class Seat {
    // A-Z
    // 1-10
    private String seatNumber;
    private boolean isOccupied;

    public Seat(String seatNumber, boolean isOccupied) {
        this.setSeatNumber(seatNumber);
        this.setOccupied(isOccupied);
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }
}

package Entities;

public class BookingResponse {

    private Integer bookingid;

    private Booking booking;

    public BookingResponse() {}

    public BookingResponse(Integer bookingid, Booking booking) {
        this.bookingid = bookingid;
        this.booking = booking;
    }

    public Integer getBookingid() { return bookingid; }

    public void setBookingid(Integer bookingid) { this.bookingid = bookingid; }

    public Booking getBooking() { return booking; }

    public void setBooking(Booking booking) { this.booking = booking; }

}


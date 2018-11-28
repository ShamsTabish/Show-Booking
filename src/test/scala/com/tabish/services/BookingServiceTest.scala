package com.tabish.services

import com.tabish.db.SeatBookingDAO
import com.tabish.models.{Auditorium, BookingBill, Seat, Tax}
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, Matchers}


class BookingServiceTest extends FunSpec with Matchers with MockitoSugar {
  describe("BookTickets") {
    val serviceTax = Tax("Service Tax", 14f)
    val swachhBharatTax = Tax("Swachh Bharat Cess", 0.5f)
    val krishiKalyanTax = Tax("Krishi Kalyan Cess", 0.5f)
    val taxes = List(serviceTax, swachhBharatTax, krishiKalyanTax)

    val seat1 = Seat("A1", "Platinum", 384,3)
    val seat2 = Seat("A2", "Platinum", 384,3)
    val seats = Set(seat1, seat2)

    val calculatedTaxes = Map(taxes(0) -> 212.80F, taxes(1) -> 7.60f, taxes(2) -> 7.60f)

    val bill = BookingBill(233, 400, calculatedTaxes)

    val auditorium = Auditorium("Audi 1", "Show 1", seats.toList)
    it("should return None for unavailable tickets") {
      SeatBookingDAO.bookTickets(auditorium, seats)
      val bookingService = new BookingService(new TextFormatter, taxes, new BookingCalculator)
      val maybeBill = bookingService.bookTickets(auditorium, seats)
      maybeBill shouldBe (Left(Set(seat1, seat2)))
      SeatBookingDAO.clearAll()
    }
    it("should return some bill when booking") {
      val mockCalculator = mock[BookingCalculator]
      when(mockCalculator.calculateBooking(seats, taxes)).thenReturn(bill)
      val bookingService = new BookingService(new TextFormatter, taxes, mockCalculator)
      val maybeString = bookingService.bookTickets(auditorium, seats)
      maybeString shouldBe (Right("Successfully Booked - Show 1\nSubTotal: Rs. 233\nService Tax @14.00: Rs. 212.80\nSwachh Bharat Cess @0.50: Rs. 7.60\nKrishi Kalyan Cess @0.50: Rs. 7.60\nTotal: Rs. 400"))
    }
  }
}

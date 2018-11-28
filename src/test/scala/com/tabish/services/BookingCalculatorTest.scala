package com.tabish.services

import com.tabish.models.{BookingBill, Seat, Tax}
import org.scalatest.{FunSpec, Matchers}

class BookingCalculatorTest extends FunSpec with Matchers {
  val injectedBookingCalculator = new BookingCalculator
  val seat1 = Seat("A1", "Platinum", 384, 3)
  val seat2 = Seat("A2", "Platinum", 384, 3)
  val seatsToBook = Set(seat1, seat2)
  val serviceTax = Tax("Service Tax", 14f)
  val swachhBharatTax = Tax("Swachh Bharat Cess", 0.5f)
  val krishiKalyanTax = Tax("Krishi Kalyan Cess", 0.5f)
  val taxes = List(serviceTax, swachhBharatTax, krishiKalyanTax)

  describe("Calculate Booking") {
    it("should calculate bill with taxes") {
      val expectedTax = Map(taxes(0) -> 107.52F, taxes(1) -> 3.84F, taxes(2) -> 3.84F)
      val expectedBill = BookingBill(768, 884, expectedTax)

      val actualBill = injectedBookingCalculator.calculateBooking(seatsToBook, taxes)

      actualBill shouldBe (expectedBill)
    }
  }

}

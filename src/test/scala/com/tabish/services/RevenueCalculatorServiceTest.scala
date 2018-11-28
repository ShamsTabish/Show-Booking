package com.tabish.services

import com.tabish.db.TaxesDAO
import com.tabish.models.{Auditorium, Earnings, Seat}
import org.scalatest.{FunSpec, Matchers}

class RevenueCalculatorServiceTest extends FunSpec with Matchers {
  val seat1 = Seat("C1", "Silver", 240, 1)
  val seat2 = Seat("C2", "Silver", 240, 1)
  val seat3 = Seat("A3", "Platinum", 320, 3)
  val seat4 = Seat("B4", "Gold", 280, 2)
  val seat5 = Seat("B5", "Gold", 280, 2)
  val allSeats = List(seat1, seat2, seat3, seat4, seat5)
  val audi = Auditorium("Audi 1", "Show 1", allSeats)

  val revenueCalculatorService = new RevenueCalculatorService

  describe("Revenue") {
    it("should calculate revenue as 0 when there is no booking") {
      val bookings: Set[(Auditorium, Seat)] = Set()
      val earnings = revenueCalculatorService.calculateEarnings(bookings)
      val expectedTaxes = TaxesDAO.getAllTaxes().map(tax => (tax, 0F)).toMap
      val expectedRevenue = Earnings(0f, expectedTaxes)
      earnings shouldBe (expectedRevenue)
    }
    it("should calculate revenue when there are multiple bookings") {
      val booking1 = (audi, seat1)
      val booking2 = (audi, seat2)
      val booking3 = (audi, seat3)

      val allBookings = Set(booking1, booking2, booking3)
      val earnings = revenueCalculatorService.calculateEarnings(allBookings)

      val taxes = TaxesDAO.getAllTaxes()
      val expectedTaxes = Map(taxes(0) -> 112f, taxes(1) -> 4f, taxes(2) -> 4f)
      val expectedRevenue = Earnings(800f, expectedTaxes)

      earnings shouldBe (expectedRevenue)

    }
  }

}

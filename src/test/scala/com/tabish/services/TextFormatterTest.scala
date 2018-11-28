package com.tabish.services

import com.tabish.models._
import org.scalatest.{FunSpec, Matchers}

class TextFormatterTest extends FunSpec with Matchers {
  val formatter: OutputFormatter = new TextFormatter
  val serviceTax = Tax("Service Tax", 14f)
  val swachhBharatTax = Tax("Swachh Bharat Cess", 0.5f)
  val krishiKalyanTax = Tax("Krishi Kalyan Cess", 0.5f)
  val taxes = List(serviceTax, swachhBharatTax, krishiKalyanTax)

  val calculatedTaxes = Map(taxes(0) -> 212.80F, taxes(1) -> 7.60f, taxes(2) -> 7.60f)

  describe("Earning Formatting") {
    it("should format Earnings in the given format with Taxes") {
      val earnings = Earnings(1520F, calculatedTaxes)
      val expectedOutput =
        """Total Sales:
          |Revenue: Rs. 1520.0
          |Service Tax: Rs. 212.80
          |Swachh Bharat Cess: Rs. 7.60
          |Krishi Kalyan Cess: Rs. 7.60""".stripMargin
      val formattedRevenue = formatter.formatRevenue(earnings)
      formattedRevenue shouldBe (expectedOutput)
    }
  }

  describe("Format Booking") {
    it("should format booking bill in given format. ") {
      val seat1 = Seat("A1", "Platinum", 384, 3)
      val seat2 = Seat("A2", "Platinum", 384, 3)
      val allSeats = Set(seat1, seat2)
      val bill = BookingBill(560, 644, calculatedTaxes)
      val audi = Auditorium("Audi 1", "Show 1", allSeats.toList)
      val expectedBill =
        """Successfully Booked - Show 1
          |SubTotal: Rs. 560
          |Service Tax @14.00: Rs. 212.80
          |Swachh Bharat Cess @0.50: Rs. 7.60
          |Krishi Kalyan Cess @0.50: Rs. 7.60
          |Total: Rs. 644""".stripMargin

      val actualFormattedBill = formatter.formatBooking(audi, bill, allSeats)

      actualFormattedBill shouldBe (expectedBill)
    }
  }
}

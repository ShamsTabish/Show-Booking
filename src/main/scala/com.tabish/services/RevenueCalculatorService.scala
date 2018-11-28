package com.tabish.services

import com.tabish.db.{SeatBookingDAO, TaxesDAO}
import com.tabish.models
import com.tabish.models.{Auditorium, Earnings, Seat, Tax}

class RevenueCalculatorService {
  def calculateEarnings(bookings: Set[(Auditorium, Seat)]): Earnings = {
    val revenue = calculateRevenue(bookings)
    val taxes = TaxesDAO.getAllTaxes()
    val calculatedTaxes = calculateTaxes(revenue, taxes)
    models.Earnings(revenue, calculatedTaxes)
  }

  private def calculateTaxes(revenue: Float, taxes: List[Tax]): Map[Tax, Float] = {
    taxes.foldLeft(Map[Tax, Float]()) {
      (calculatedTax, tax) =>
        calculatedTax.+((tax, revenue * tax.percentage / 100))
    }
  }

  private def calculateRevenue(bookings: Set[(Auditorium, Seat)]) = {
    bookings.foldLeft(0f) {
      (revenue, booking) =>
        val seat = booking._2
        revenue + seat.cost
    }
  }
}

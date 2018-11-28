package com.tabish.services

import com.tabish.models.{BookingBill, Seat, Tax}

class BookingCalculator {
  def calculateBooking(seats: Set[Seat], taxes: List[Tax]) = {
    val subTotal = seats.toList.map(_.cost).reduce(_ + _)
    val calculatedTaxes = taxes.map(tax => (tax, tax.percentage * subTotal / 100)).toMap
    val total = calculatedTaxes.foldLeft(subTotal)((partialTotal, nextTax) => partialTotal.toFloat + nextTax._2)
    BookingBill(subTotal.ceil.toInt, total.ceil.toInt, calculatedTaxes)
  }
}

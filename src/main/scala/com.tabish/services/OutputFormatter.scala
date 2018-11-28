package com.tabish.services

import com.tabish.models.{Auditorium, BookingBill, Earnings, Seat}

trait OutputFormatter {
  def formatRevenue(earnings: Earnings): String

  def formatBooking(auditorium: Auditorium, bill: BookingBill, seats: Set[Seat]): String
}

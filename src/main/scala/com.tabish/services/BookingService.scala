package com.tabish.services

import com.tabish.db.SeatBookingDAO
import com.tabish.models.{Auditorium, Seat, Tax}

class BookingService(outputFormatter: OutputFormatter, taxes: List[Tax], bookingCalculator: BookingCalculator) {
  def bookTickets(auditorium: Auditorium, seats: Set[Seat]): Either[Set[Seat], String] = {
    val injectedBookingInterface = SeatBookingDAO
    val wasBookingSuccessful = injectedBookingInterface.bookTickets(auditorium, seats)

    wasBookingSuccessful match {
      case Right(list) =>
        val bill = bookingCalculator.calculateBooking(list, taxes)
        Right(outputFormatter.formatBooking(auditorium, bill, seats))
      case Left(list) => Left(list)
    }

  }
}
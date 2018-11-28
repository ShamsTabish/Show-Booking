package com.tabish.db

import com.tabish.models.{Auditorium, Seat}

object SeatBookingDAO {
  private val bookings = scala.collection.mutable.Set[(Auditorium, Seat)]()

  def bookTickets(audi: Auditorium, seats: Set[Seat]): Either[Set[Seat], Set[Seat]] = {
    val alreadyBookedSeats = getAlreadyBookedSeats(audi, seats)
    val seatAvailability = alreadyBookedSeats.isEmpty
    bookIfAvailable(audi, seats, seatAvailability)
    seatAvailability match {
      case true => Right(seats)
      case false => Left(alreadyBookedSeats.toSet)
    }
  }

  private def bookIfAvailable(audi: Auditorium, seats: Set[Seat], seatAvailability: Boolean) = {
    seatAvailability match {
      case true =>
        bookings ++= (seats.map((audi, _)))
      case _ => bookings
    }
  }

  def clearAll(): Unit = {
    bookings.clear()
  }

  def getAllBookings(): Set[(Auditorium, Seat)] = bookings.toSet

  private def getAlreadyBookedSeats(audi: Auditorium, seats: Set[Seat]) = {
    val empty: List[Seat] = List.empty
    seats.foldLeft(empty) {
      (acc, seat) =>
        if (bookings.contains((audi, seat))) acc :+ seat else acc
    }
  }
}
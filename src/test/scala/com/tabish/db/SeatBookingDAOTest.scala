package com.tabish.db

import com.tabish.models.{Auditorium, Seat}
import org.scalatest.{FunSpec, Matchers}

class SeatBookingDAOTest extends FunSpec with Matchers {
  val platinum = Seat("A1", "Platinum", 320f, 3)
  val gold = Seat("B1", "Gold", 280f, 2)
  private val silver = Seat("C2", "Silver", 240f, 1)
  val seats = List(
    platinum,
    Seat("A2", "Platinum", 320f, 3),
    gold,
    silver
  )
  val auditorium = Auditorium("Audi 1", "2", seats)

  describe("ClearAll") {
    it("should clear all the bookings") {
      SeatBookingDAO.bookTickets(auditorium, Set(platinum, gold))

      SeatBookingDAO.getAllBookings().size shouldBe (2)

      SeatBookingDAO.clearAll()

      SeatBookingDAO.getAllBookings().size shouldBe (0)

    }
  }

  describe("bookTickets") {

    it("should return false when booking with pre booked seats") {
      SeatBookingDAO.clearAll()
      val seats = Set(platinum, gold)
      val booking = SeatBookingDAO.bookTickets(auditorium, seats)
      booking shouldBe (Right(seats))
      val isBooked = SeatBookingDAO.bookTickets(auditorium, Set(silver, gold))
      isBooked shouldBe (Left(Set(gold)))


      //      SeatDAO.bookTickets()
    }
    it("should return true and book tickets when seats not booked already") {

    }

  }
}

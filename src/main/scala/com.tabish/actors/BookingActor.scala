package com.tabish.actors

import akka.actor.{Actor, Props}
import com.tabish.db.TaxesDAO
import com.tabish.services.{BookingCalculator, BookingService}

object BookingActor {
  val props: Props = Props(new BookingActor())
}

private class BookingActor extends Actor {

  override def receive: Receive = {
    case bookingRequest: BookRequest =>
      bookTickets(bookingRequest)
  }

  private def bookTickets(bookRequest: BookRequest) = {
    val taxes = TaxesDAO.getAllTaxes()
    val calculator = new BookingCalculator
    val bookingService = new BookingService(bookRequest.outputFormatter, taxes, calculator)
    bookingService.bookTickets(bookRequest.auditorium, bookRequest.seats) match {
      case Right(bill) => bookRequest.notifyStatusFunction(bill)
      case Left(unavailableSeats) =>
        bookRequest.notifyStatusFunction(s"${unavailableSeats.map(_.id).mkString(", ")} Not available Please select different seats.!")
    }
  }
}

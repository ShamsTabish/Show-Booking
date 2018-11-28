package com.tabish

import akka.actor.{ActorRef, ActorSystem}
import com.tabish.actors.{BookRequest, BookingActor}
import com.tabish.db.{AuditoriumDAO, SeatBookingDAO}
import com.tabish.models.{Auditorium, Seat}
import com.tabish.services.TextFormatter

import scala.util.{Failure, Try}

object UserInterface extends App {
  val system: ActorSystem = ActorSystem("ShowBookingActorSystem")

  val seatBookingActor: ActorRef = system.actorOf(BookingActor.props, "TicketBookingActor")


  presentActions


  private def presentActions: Unit = {
    val presentOptions = Try {
      val menu = new Menu()
      Thread.sleep(1000)
      menu.printMainMenu()
      scala.io.StdIn.readInt() match {
        case 1 =>
          menu.printSeatStatusForAllAuditorium()
        case 2 =>
          menu.bookTicketsAudiMenu(seatBookingActor: ActorRef)
        case 3 => sys.exit()
        case _ => println("Invalid Input try again")
      }
    }
    presentOptions match {
      case Failure(e) => println("Something went wrong, please try again")
      case scala.util.Success(value) => println("Thank you! Enjoy the show.")
    }
    presentActions
  }


  class Menu {
    def printMainMenu(): Unit = {
      val horizontalRuleLine = "-" * 40
      val mainMenu =
        s"""\n$horizontalRuleLine
           |Menu:
           |1. Show Seats
           |2. Book Ticket
           |3. Exit
        """.stripMargin
      println(mainMenu)
    }

    def bookTicketsAudiMenu(seatBookingActor: ActorRef) = {
      val auditoriums = AuditoriumDAO.listAllAuditoriums()
      val listOfShows = auditoriums.map(_.showName).zipWithIndex.map(entry => (entry._2 + 1) + ". " + entry._1)
      listOfShows.map(println)
      scala.io.StdIn.readInt() match {
        case showNumber: Int if showNumber <= auditoriums.length && showNumber > 0 => seatBookingMenu(auditoriums(showNumber - 1), seatBookingActor)
        case _ => println("Invalid show Number.")
      }
    }

    def seatBookingMenu(audi: Auditorium, seatBookingActor: ActorRef) = {
      println("Enter Seats:")

      try {
        val seatsString = scala.io.StdIn.readLine()
        val audiSeats: List[Seat] = convertToAudiSeats(seatsString)
        seatBookingActor ! BookRequest(audi, audiSeats.toSet, new TextFormatter, notifyBookingStatus)
      } catch {
        case e: Exception => println("Invalid Seat Numbers, please try again...")
      }


      def convertToAudiSeats(seatsString: String) = {
        seatsString.split(",").map(_.trim.toUpperCase).map {
          seatId =>
            audi.seats.find(_.id.trim == seatId)
        }.filter(_ != None).map(_.get).toList
      }

      def notifyBookingStatus(message: String) = {
        println("Status: " + message)
      }
    }

    def printSeatStatusForAllAuditorium() = {
      val bookedSeats = getAuditoriumWiseBookedSeats
      AuditoriumDAO.listAllAuditoriums().map(printAuditorium)
    }

    private def printAuditorium(auditorium: Auditorium) = {
      println(s"${auditorium.showName} Running in ${auditorium.name}:")
      println("All Seats:")

      printSortedBySeatType(auditorium)
    }

    private def printSortedBySeatType(auditorium: Auditorium) = {
      val seatSeparator = "\t"
      val groupedSeats = auditorium.seats.groupBy(seat => seat.rowNumber).toList.sortBy(_._1).reverse
      groupedSeats.map {
        entry =>
          println(entry._2.map(seat => markSeats(auditorium, seat)).mkString("[ ", seatSeparator, " ]"))
      }
    }

    private def markSeats(auditorium: Auditorium, seat: Seat) = {
      isSeatBooked(auditorium, seat) match {
        case true =>
          "[x" + seat.id + "x]"
        case false =>
          "  " + seat.id + "  "
      }
    }

    private def isSeatBooked(auditorium: Auditorium, seat: Seat) = {
      SeatBookingDAO.getAllBookings().contains((auditorium, seat))
    }

    private def getAuditoriumWiseBookedSeats = {
      val tuples = SeatBookingDAO.getAllBookings()
      tuples.groupBy(booking => booking._1).map(entry => (entry._1, entry._2.map(_._2)))
    }
  }

}
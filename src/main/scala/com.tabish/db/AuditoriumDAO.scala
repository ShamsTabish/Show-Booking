package com.tabish.db

import com.tabish.models.Auditorium
import com.tabish.models.Seat

// Hard coded value to Mock the DB Layer..
//In real world scenario these values should be stored and fetched from DB

/*Todo: Move to DB.*/
object AuditoriumDAO {


  private val audi1 = Auditorium("Audi 1", "Show 1", createSeats(9, 6, 7))
  private val audi2 = Auditorium("Audi 2", "Show 2", createSeats(7, 6, 7))
  private val audi3 = Auditorium("Audi 3", "Show 3", createSeats(5, 8, 9))
  private val allAuditoriums = List(audi1, audi2, audi3)


  def listAllAuditoriums(): List[Auditorium] = {
    allAuditoriums
  }

  private def createSeats(countOfPlatinum: Int, countOfGold: Int, countOfSilver: Int): List[Seat] = {
    val platinumSeats = Range(1, countOfPlatinum + 1).map(id => Seat("A" + id, "Platinum", 320, 3)).toList
    val goldSeats = Range(1, countOfGold + 1).map(id => Seat("B" + id, "Gold", 280, 2)).toList
    val silverSeats = Range(1, countOfSilver + 1).map(id => Seat("C" + id, "Silver", 240, 1)).toList
    platinumSeats ++ goldSeats ++ silverSeats
  }

}
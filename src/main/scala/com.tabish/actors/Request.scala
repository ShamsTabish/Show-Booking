package com.tabish.actors

import com.tabish.models.{Auditorium, Seat}
import com.tabish.services.OutputFormatter

case class BookRequest(auditorium: Auditorium, seats: Set[Seat], outputFormatter: OutputFormatter, notifyStatusFunction: String => Unit)

case class StatusRequest()


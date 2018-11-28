package com.tabish.models

sealed trait Status

final case object Booked extends Status

final case object Available extends Status

final case class Seat(id: String, category: String, cost: Float, rowNumber: Int)


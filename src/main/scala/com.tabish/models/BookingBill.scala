package com.tabish.models

final case class BookingBill(subTotal: Int, total: Int, taxes: Map[Tax, Float])

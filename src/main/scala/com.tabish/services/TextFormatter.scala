package com.tabish.services

import com.tabish.models._

class TextFormatter extends OutputFormatter {
  override def formatRevenue(earnings: Earnings): String = {
    f"Total Sales:\nRevenue: Rs. ${earnings.revenue.abs}\n" + formatTaxesForRevenue(earnings)
  }

  private def formatTaxesForRevenue(earnings: Earnings) = {
    earnings.taxes.map(tax => f"${tax._1.name}: Rs. ${tax._2}%.2f").mkString("\n")
  }

  override def formatBooking(auditorium: Auditorium, bill: BookingBill, seats: Set[Seat]): String = {
    def format(tax: Tuple2[Tax, Float]) =f"""${tax._1.name} @${tax._1.percentage}%.2f: Rs. ${tax._2}%.2f"""

    val formattedTaxes = bill.taxes.map(format).mkString("\n")

    f"""Successfully Booked - ${auditorium.showName}
       |SubTotal: Rs. ${bill.subTotal}
       |$formattedTaxes
       |Total: Rs. ${bill.total}""".stripMargin
  }
}

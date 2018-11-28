package com.tabish.models

final case class Earnings(revenue: Float, taxes: Map[Tax, Float])
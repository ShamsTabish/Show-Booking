package com.tabish.db

import com.tabish.models.Tax

object TaxesDAO {
  def getAllTaxes(): List[Tax] = {
    val serviceTax = Tax("Service Tax", 14f)
    val swachhBharatTax = Tax("Swachh Bharat Cess", 0.5f)
    val krishiKalyanTax = Tax("Krishi Kalyan Cess", 0.5f)
    List(serviceTax, swachhBharatTax, krishiKalyanTax)
  }
}

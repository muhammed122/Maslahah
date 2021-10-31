package com.example.maslahah.data

data class UserData(
    val name: String? = null,
    val phone: String? = null,
    val image: String? = null,
    val email: String? = null,
    val password: String? = null,
    var tasks: Int? = 0,
    val balance: Double? = 0.0,
    val tax: Double? = 0.0,
    val userToken: String? = "",
    val available: Boolean? = true
)
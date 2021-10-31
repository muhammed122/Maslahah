package com.example.maslahah.data

data class ServiceData(
    val id: String? = null,
    val serviceOwnerPhone: String? = null,
    val title: String? = null,
    val details: String? = null,
    val duration: String? = null,
    val papers: String? = null,
    val date: String? = null,
    val time: String? = null,
    var selected: Boolean? = false,
    var status: Int? = 0,
)

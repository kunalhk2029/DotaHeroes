package com.app.core

sealed class FilterOrder {

    object Ascending: FilterOrder()

    object Descending: FilterOrder()
}
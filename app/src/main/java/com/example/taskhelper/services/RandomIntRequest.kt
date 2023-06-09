package com.example.taskhelper.services

import java.util.concurrent.atomic.AtomicInteger

object RandomIntRequest {

    private val seed = AtomicInteger()

    fun getRandomIntNumber() = seed.getAndIncrement() + System.currentTimeMillis().toInt()
}
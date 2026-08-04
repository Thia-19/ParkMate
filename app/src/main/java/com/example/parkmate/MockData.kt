package com.example.parkmate

import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.core.domain.models.ParkingType
import com.example.parkmate.core.domain.models.Position
import com.example.parkmate.core.domain.models.Vehicle
import java.util.concurrent.TimeUnit

object MockData {

    private val currentTime = System.currentTimeMillis()
    private val hourInMillis = TimeUnit.HOURS.toMillis(1)
    private val dayInMillis = TimeUnit.DAYS.toMillis(1)

    // --- LISTA MOCK VEICOLI (Utile per testare la modale dei filtri) ---
    val mockVehicles = listOf(
        Vehicle(id = 1, name = "Fiat Panda"),
        Vehicle(id = 2, name = "Tesla Model 3")
    )

    // --- LISTA MOCK SESSIONI DI PARCHEGGIO ---
    val mockSessions = listOf(
        // 1. Parcheggio a ORE Attivo (Iniziato 2 ore fa)
        ParkingSession(
            id = 1,
            isActive = true,
            vehicleId = 1,
            parkingType = ParkingType.HOURLY_PAID,
            startTime = currentTime - (2 * hourInMillis),
            position = Position(latitude = 45.4642, longitude = 9.1900), // Duomo di Milano
            hourlyRate = 2.50,
            note = "Stallo blu vicina alla farmacia",
            photoUri = null,
            endTime = null,
            finalCost = null
        ),

        // 2. Parcheggio GRATUITO Concluso (Ieri)
        ParkingSession(
            id = 2,
            isActive = false,
            vehicleId = 1,
            parkingType = ParkingType.FREE,
            startTime = currentTime - dayInMillis - (3 * hourInMillis),
            endTime = currentTime - dayInMillis - (1 * hourInMillis),
            position = Position(latitude = 45.4781, longitude = 9.2256), // Città Studi
            note = "Parcheggio strisce bianche libero",
            photoUri = null,
            finalCost = 0.0
        ),

        // 3. Parcheggio a TIFFA FISSA Concluso (3 giorni fa)
        ParkingSession(
            id = 3,
            isActive = false,
            vehicleId = 2,
            parkingType = ParkingType.FIXED,
            startTime = currentTime - (3 * dayInMillis),
            endTime = currentTime - (3 * dayInMillis) + (5 * hourInMillis),
            expiryTime = currentTime - (3 * dayInMillis) + (4 * hourInMillis), // Scaduto prima della fine
            position = Position(latitude = 45.4842, longitude = 9.2023), // Stazione Centrale
            fixedCost = 10.00,
            note = "Ticket giornaliero Stazione",
            photoUri = null,
            finalCost = 10.00
        ),

        // 4. Parcheggio a ORE Concluso con Costo Finale (5 giorni fa)
        ParkingSession(
            id = 4,
            isActive = false,
            vehicleId = 1,
            parkingType = ParkingType.HOURLY_PAID,
            startTime = currentTime - (5 * dayInMillis),
            endTime = currentTime - (5 * dayInMillis) + (3 * hourInMillis),
            position = Position(latitude = 45.4512, longitude = 9.1724), // Navigli
            hourlyRate = 2.00,
            note = "Cena sui Navigli",
            photoUri = null,
            finalCost = 6.00
        ),

        // 5. Parcheggio FISSO Attivo (Grattacielo Unicredit)
        ParkingSession(
            id = 5,
            isActive = true,
            vehicleId = 2,
            parkingType = ParkingType.FIXED,
            startTime = currentTime - (1 * hourInMillis),
            expiryTime = currentTime + (3 * hourInMillis), // Scade tra 3 ore
            position = Position(latitude = 45.4825, longitude = 9.1895), // Porta Nuova
            fixedCost = 5.00,
            note = "Piazza Gae Aulenti - Grattacielo",
            photoUri = null,
            endTime = null,
            finalCost = null
        )
    )
}
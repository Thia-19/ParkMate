package com.example.parkmate

import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.core.domain.models.ParkingType
import com.example.parkmate.core.domain.models.Position
import com.example.parkmate.core.domain.models.Vehicle
import java.util.concurrent.TimeUnit

import java.util.Calendar
import java.util.TimeZone

// Helper pulito e leggibile per generare i millisecondi senza usare API 26+
private fun millisOf(
    year: Int,
    month: Int, // 1 = Gennaio, 2 = Febbraio, ecc.
    day: Int,
    hour: Int = 0,
    minute: Int = 0
): Long = Calendar.getInstance(TimeZone.getDefault()).apply {
    set(year, month - 1, day, hour, minute, 0) // Calendar usa i mesi da 0 a 11
    set(Calendar.MILLISECOND, 0)
}.timeInMillis

val pastMockSessions = listOf(
    // --- LUGLIO ---
    ParkingSession(
        id = 6,
        isActive = false,
        vehicleId = 1,
        parkingType = ParkingType.HOURLY_PAID,
        startTime = millisOf(2026, 7, 28, 10, 0),
        endTime = millisOf(2026, 7, 28, 13, 30),
        position = Position(latitude = 45.4627, longitude = 9.1777), // Sant'Ambrogio
        hourlyRate = 2.00,
        note = "Shopping in centro",
        photoUri = null,
        finalCost = 7.00
    ),
    ParkingSession(
        id = 7,
        isActive = false,
        vehicleId = 2,
        parkingType = ParkingType.FIXED_TICKET,
        startTime = millisOf(2026, 7, 15, 14, 0),
        endTime = millisOf(2026, 7, 15, 18, 0),
        expiryTime = millisOf(2026, 7, 15, 18, 0),
        position = Position(latitude = 45.4705, longitude = 9.2052), // Porta Venezia
        fixedCost = 8.00,
        note = "Pomeriggio al parco Giardini Pubblici",
        photoUri = null,
        finalCost = 8.00
    ),
    ParkingSession(
        id = 8,
        isActive = false,
        vehicleId = 3,
        parkingType = ParkingType.FREE,
        startTime = millisOf(2026, 7, 3, 20, 0),
        endTime = millisOf(2026, 7, 3, 23, 30),
        position = Position(latitude = 45.4542, longitude = 9.1523), // Zona Saffi / Wagner
        note = "Cena da amici",
        photoUri = null,
        finalCost = 0.0
    ),

    // --- GIUGNO ---
    ParkingSession(
        id = 9,
        isActive = false,
        vehicleId = 3,
        parkingType = ParkingType.HOURLY_PAID,
        startTime = millisOf(2026, 6, 22, 9, 15),
        endTime = millisOf(2026, 6, 22, 12, 15),
        position = Position(latitude = 45.4781, longitude = 9.1240), // San Siro
        hourlyRate = 2.50,
        note = "Commissioni in zona",
        photoUri = null,
        finalCost = 7.50
    ),
    ParkingSession(
        id = 10,
        isActive = false,
        vehicleId = 5,
        parkingType = ParkingType.FIXED_TICKET,
        startTime = millisOf(2026, 6, 14, 8, 0),
        endTime = millisOf(2026, 6, 14, 20, 0),
        expiryTime = millisOf(2026, 6, 14, 20, 0),
        position = Position(latitude = 45.4485, longitude = 9.2102), // Porta Romana
        fixedCost = 12.00,
        note = "Giornata intera lavoro",
        photoUri = null,
        finalCost = 12.00
    ),
    ParkingSession(
        id = 11,
        isActive = false,
        vehicleId = 5,
        parkingType = ParkingType.FREE,
        startTime = millisOf(2026, 6, 2, 11, 0),
        endTime = millisOf(2026, 6, 2, 17, 30),
        position = Position(latitude = 45.4750, longitude = 9.2300), // Lambrate
        note = "Picnic festivo",
        photoUri = null,
        finalCost = 0.0
    ),

    // --- MAGGIO ---
    ParkingSession(
        id = 12,
        isActive = false,
        vehicleId = 2,
        parkingType = ParkingType.HOURLY_PAID,
        startTime = millisOf(2026, 5, 29, 15, 0),
        endTime = millisOf(2026, 5, 29, 17, 0),
        position = Position(latitude = 45.4668, longitude = 9.1905), // Quadrilatero della Moda
        hourlyRate = 3.00,
        note = "Appuntamento cliente",
        photoUri = null,
        finalCost = 6.00
    ),
    ParkingSession(
        id = 13,
        isActive = false,
        vehicleId = 3,
        parkingType = ParkingType.FIXED_TICKET,
        startTime = millisOf(2026, 5, 18, 19, 30),
        endTime = millisOf(2026, 5, 18, 23, 30),
        expiryTime = millisOf(2026, 5, 18, 23, 59),
        position = Position(latitude = 45.4800, longitude = 9.1600), // Sempione / Arco della Pace
        fixedCost = 6.00,
        note = "Aperitivo Arco della Pace",
        photoUri = null,
        finalCost = 6.00
    ),
    ParkingSession(
        id = 14,
        isActive = false,
        vehicleId = 3,
        parkingType = ParkingType.HOURLY_PAID,
        startTime = millisOf(2026, 5, 5, 10, 0),
        endTime = millisOf(2026, 5, 5, 14, 0),
        position = Position(latitude = 45.4611, longitude = 9.1601), // Corso Vercelli
        hourlyRate = 2.00,
        note = "Pranzo di lavoro",
        photoUri = null,
        finalCost = 8.00
    ),

    // --- APRILE ---
    ParkingSession(
        id = 15,
        isActive = false,
        vehicleId = 3,
        parkingType = ParkingType.FREE,
        startTime = millisOf(2026, 4, 25, 10, 30),
        endTime = millisOf(2026, 4, 25, 16, 0),
        position = Position(latitude = 45.4380, longitude = 9.1680), // Famagosta / Barona
        note = "Passeggiata fuori porta",
        photoUri = null,
        finalCost = 0.0
    ),
    ParkingSession(
        id = 16,
        isActive = false,
        vehicleId = 3,
        parkingType = ParkingType.HOURLY_PAID,
        startTime = millisOf(2026, 4, 16, 14, 0),
        endTime = millisOf(2026, 4, 16, 19, 0),
        position = Position(latitude = 45.4700, longitude = 9.1700), // Zona Tortona / Design Week
        hourlyRate = 2.50,
        note = "Fuorisalone Tortona",
        photoUri = null,
        finalCost = 12.50
    ),
    ParkingSession(
        id = 17,
        isActive = false,
        vehicleId = 2,
        parkingType = ParkingType.FIXED_TICKET,
        startTime = millisOf(2026, 4, 2, 8, 30),
        endTime = millisOf(2026, 4, 2, 13, 0),
        expiryTime = millisOf(2026, 4, 2, 12, 30),
        position = Position(latitude = 45.4850, longitude = 9.2000), // Sondrio / Centrale
        fixedCost = 5.00,
        note = "Commissioni veloci ufficio",
        photoUri = null,
        finalCost = 5.00
    ),

    // --- MARZO ---
    ParkingSession(
        id = 18,
        isActive = false,
        vehicleId = 1,
        parkingType = ParkingType.HOURLY_PAID,
        startTime = millisOf(2026, 3, 27, 16, 0),
        endTime = millisOf(2026, 3, 27, 18, 30),
        position = Position(latitude = 45.4600, longitude = 9.2100), // Dateo / Corso Plebisciti
        hourlyRate = 2.00,
        note = "Visita medica specialistica",
        photoUri = null,
        finalCost = 5.00
    ),
    ParkingSession(
        id = 19,
        isActive = false,
        vehicleId = 5,
        parkingType = ParkingType.FREE,
        startTime = millisOf(2026, 3, 15, 9, 0),
        endTime = millisOf(2026, 3, 15, 12, 0),
        position = Position(latitude = 45.5000, longitude = 9.1500), // Affori / Bovisa
        note = "Parcheggio stazione M3",
        photoUri = null,
        finalCost = 0.0
    ),
    ParkingSession(
        id = 20,
        isActive = false,
        vehicleId = 5,
        parkingType = ParkingType.FIXED_TICKET,
        startTime = millisOf(2026, 3, 5, 20, 0),
        endTime = millisOf(2026, 3, 5, 23, 45),
        expiryTime = millisOf(2026, 3, 6, 2, 0),
        position = Position(latitude = 45.4500, longitude = 9.1700), // Ticinese
        fixedCost = 7.00,
        note = "Teatro / Concerto",
        photoUri = null,
        finalCost = 7.00
    ),

    // --- FEBBRAIO ---
    ParkingSession(
        id = 21,
        isActive = false,
        vehicleId = 5,
        parkingType = ParkingType.HOURLY_PAID,
        startTime = millisOf(2026, 2, 21, 11, 0),
        endTime = millisOf(2026, 2, 21, 15, 0),
        position = Position(latitude = 45.4650, longitude = 9.1850), // Cordusio / Cairoli
        hourlyRate = 2.50,
        note = "Pranzo nel weekend",
        photoUri = null,
        finalCost = 10.00
    ),
    ParkingSession(
        id = 22,
        isActive = false,
        vehicleId = 5,
        parkingType = ParkingType.FIXED_TICKET,
        startTime = millisOf(2026, 2, 10, 8, 0),
        endTime = millisOf(2026, 2, 10, 18, 0),
        expiryTime = millisOf(2026, 2, 10, 18, 0),
        position = Position(latitude = 45.4720, longitude = 9.1920), // Turati / Repubblica
        fixedCost = 15.00,
        note = "Sosta lunga lavoro",
        photoUri = null,
        finalCost = 15.00
    ),

    // --- GENNAIO ---
    ParkingSession(
        id = 23,
        isActive = false,
        vehicleId = 1,
        parkingType = ParkingType.FREE,
        startTime = millisOf(2026, 1, 30, 15, 0),
        endTime = millisOf(2026, 1, 30, 18, 0),
        position = Position(latitude = 45.4880, longitude = 9.2150), // Loreto / Nolo
        note = "Spesa al centro commerciale",
        photoUri = null,
        finalCost = 0.0
    ),
    ParkingSession(
        id = 24,
        isActive = false,
        vehicleId = 2,
        parkingType = ParkingType.HOURLY_PAID,
        startTime = millisOf(2026, 1, 18, 10, 30),
        endTime = millisOf(2026, 1, 18, 13, 0),
        position = Position(latitude = 45.4580, longitude = 9.1950), // Crocetta / Missori
        hourlyRate = 2.00,
        note = "Saldi invernali",
        photoUri = null,
        finalCost = 5.00
    ),
    ParkingSession(
        id = 25,
        isActive = false,
        vehicleId = 1,
        parkingType = ParkingType.FIXED_TICKET,
        startTime = millisOf(2026, 1, 7, 9, 0),
        endTime = millisOf(2026, 1, 7, 13, 0),
        expiryTime = millisOf(2026, 1, 7, 13, 0),
        position = Position(latitude = 45.4790, longitude = 9.1750), // Moscova / Brera
        fixedCost = 6.00,
        note = "Rientro dalle vacanze - Ufficio",
        photoUri = null,
        finalCost = 6.00
    ),
    ParkingSession(
        id = 26,
        isActive = false,
        vehicleId = 8,
        parkingType = ParkingType.HOURLY_PAID,
        startTime = millisOf(2026, 7, 28, 10, 0),
        endTime = millisOf(2026, 7, 28, 13, 30),
        position = Position(latitude = 45.4627, longitude = 9.1777), // Sant'Ambrogio
        hourlyRate = 2.00,
        note = "Shopping in centro",
        photoUri = null,
        finalCost = 7.00
    ),
    ParkingSession(
        id = 27,
        isActive = false,
        vehicleId = 9,
        parkingType = ParkingType.HOURLY_PAID,
        startTime = millisOf(2026, 7, 28, 10, 0),
        endTime = millisOf(2026, 7, 28, 13, 30),
        position = Position(latitude = 45.4627, longitude = 9.1777), // Sant'Ambrogio
        hourlyRate = 2.00,
        note = "Shopping in centro",
        photoUri = null,
        finalCost = 7.00
    ),
    ParkingSession(
        id = 28,
        isActive = false,
        vehicleId = 7,
        parkingType = ParkingType.HOURLY_PAID,
        startTime = millisOf(2026, 7, 28, 10, 0),
        endTime = millisOf(2026, 7, 28, 13, 30),
        position = Position(latitude = 45.4627, longitude = 9.1777), // Sant'Ambrogio
        hourlyRate = 2.00,
        note = "Shopping in centro",
        photoUri = null,
        finalCost = 7.00
    ),
    ParkingSession(
        id = 6,
        isActive = false,
        vehicleId = 6,
        parkingType = ParkingType.HOURLY_PAID,
        startTime = millisOf(2026, 7, 28, 10, 0),
        endTime = millisOf(2026, 7, 28, 13, 30),
        position = Position(latitude = 45.4627, longitude = 9.1777), // Sant'Ambrogio
        hourlyRate = 2.00,
        note = "Shopping in centro",
        photoUri = null,
        finalCost = 7.00
    ),
)

object MockData {

    private val currentTime = System.currentTimeMillis()
    private val hourInMillis = TimeUnit.HOURS.toMillis(1)
    private val dayInMillis = TimeUnit.DAYS.toMillis(1)

    // --- LISTA MOCK VEICOLI (Utile per testare la modale dei filtri) ---
    val mockVehicles = listOf(
        Vehicle(id = 1, name = "Fiat Panda", licensePlate = "EX5681"),
        Vehicle(id = 2, name = "Tesla Model 3", licensePlate = "EZ5681"),
        Vehicle(id = 3, name = "Pegeot Partner TP", licensePlate = "SIGMASKSK"),
        Vehicle(id = 4, name = "Tesla Model 2", licensePlate = "EZ5681"),
        Vehicle(id = 5, name = "Carsan TPER", licensePlate = "E12346"),
        Vehicle(id = 6, name = "dih", licensePlate = "E12346"),
        Vehicle(id = 7, name = "luca", licensePlate = "E12346"),
        Vehicle(id = 8, name = "Umami", licensePlate = "E12346"),
        Vehicle(id = 9, name = "siR", licensePlate = "E12346"),


    )

    // --- LISTA MOCK SESSIONI DI PARCHEGGIO ---
    val mockSessions = pastMockSessions + listOf(
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
            parkingType = ParkingType.FIXED_TICKET,
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
            parkingType = ParkingType.FIXED_TICKET,
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
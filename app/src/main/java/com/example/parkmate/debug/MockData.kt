package com.example.parkmate.debug

import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.core.domain.models.ParkingType
import com.example.parkmate.core.domain.models.Position
import com.example.parkmate.core.domain.models.vehicle.Vehicle
import com.example.parkmate.core.domain.models.vehicle.VehicleType
import java.util.Calendar
import java.util.TimeZone

object MockData {

    private fun dateToMillis(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(year, month - 1, day, hour, minute, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    val vehicles = listOf(
        Vehicle(id = 1, name = "Fiat 500", type = VehicleType.CAR),
        Vehicle(id = 2, name = "Vespa GTS 300", type = VehicleType.MOTORCYCLE),
        Vehicle(id = 3, name = "Tesla Model 3", type = VehicleType.CAR)
    )

    val mockParkingSessions = listOf(
        // ==================== FEBBRAIO ====================
        ParkingSession(
            id = 1, isActive = false, vehicleId = 1, parkingType = ParkingType.HOURLY_PAID,
            startTime = dateToMillis(2026, 2, 3, 9, 0), endTime = dateToMillis(2026, 2, 3, 11, 30),
            hourlyRate = 2.00, finalCost = 5.00, position = Position(44.4949, 11.3426, 54.0),
            note = "Cliente Centro Historic", photoUri = null
        ),
        ParkingSession(
            id = 2, isActive = false, vehicleId = 2, parkingType = ParkingType.FREE,
            startTime = dateToMillis(2026, 2, 12, 14, 15), endTime = dateToMillis(2026, 2, 12, 16, 0),
            position = Position(44.4822, 11.3601, 72.0), note = "Posto moto giardini", photoUri = null
        ),
        ParkingSession(
            id = 3, isActive = false, vehicleId = 3, parkingType = ParkingType.FIXED_TICKET,
            startTime = dateToMillis(2026, 2, 20, 8, 30), endTime = dateToMillis(2026, 2, 20, 18, 0),
            fixedCost = 15.00, expiryTime = dateToMillis(2026, 2, 20, 20, 0), finalCost = 15.00,
            position = Position(44.5058, 11.3417, 48.0), note = "Stazione P2", photoUri = null
        ),
        ParkingSession(
            id = 4, isActive = false, vehicleId = 1, parkingType = ParkingType.HOURLY_PAID,
            startTime = dateToMillis(2026, 2, 26, 19, 0), endTime = dateToMillis(2026, 2, 26, 23, 0),
            hourlyRate = 1.50, finalCost = 6.00, position = Position(44.4931, 11.3468, 55.0),
            note = "Cena fuori", photoUri = null
        ),

        // ==================== MARZO ====================
        ParkingSession(
            id = 5, isActive = false, vehicleId = 3, parkingType = ParkingType.HOURLY_PAID,
            startTime = dateToMillis(2026, 3, 2, 10, 0), endTime = dateToMillis(2026, 3, 2, 13, 0),
            hourlyRate = 2.50, finalCost = 7.50, position = Position(44.4965, 11.3580, 51.0),
            note = "Visita medica", photoUri = null
        ),
        ParkingSession(
            id = 6, isActive = false, vehicleId = 1, parkingType = ParkingType.FIXED_TICKET,
            startTime = dateToMillis(2026, 3, 9, 9, 0), endTime = dateToMillis(2026, 3, 9, 14, 0),
            fixedCost = 5.00, expiryTime = dateToMillis(2026, 3, 9, 14, 0), finalCost = 5.00,
            position = Position(44.5123, 11.3200, 42.0), note = "Ticket fiera", photoUri = null
        ),
        ParkingSession(
            id = 7, isActive = false, vehicleId = 2, parkingType = ParkingType.FREE,
            startTime = dateToMillis(2026, 3, 15, 16, 30), endTime = dateToMillis(2026, 3, 15, 18, 0),
            position = Position(44.4880, 11.3310, 60.0), note = null, photoUri = null
        ),
        ParkingSession(
            id = 8, isActive = false, vehicleId = 3, parkingType = ParkingType.HOURLY_PAID,
            startTime = dateToMillis(2026, 3, 22, 11, 0), endTime = dateToMillis(2026, 3, 22, 12, 30),
            hourlyRate = 2.00, finalCost = 3.00, position = Position(44.4949, 11.3426, 54.0),
            note = "Commissioni veloci", photoUri = null
        ),
        ParkingSession(
            id = 9, isActive = false, vehicleId = 1, parkingType = ParkingType.HOURLY_PAID,
            startTime = dateToMillis(2026, 3, 29, 15, 0), endTime = dateToMillis(2026, 3, 29, 17, 45),
            hourlyRate = 1.80, finalCost = 4.95, position = Position(44.4989, 11.3542, 50.0),
            note = "Shopping", photoUri = null
        ),

        // ==================== APRILE ====================
        ParkingSession(
            id = 10, isActive = false, vehicleId = 2, parkingType = ParkingType.FREE,
            startTime = dateToMillis(2026, 4, 4, 11, 0), endTime = dateToMillis(2026, 4, 4, 15, 30),
            position = Position(44.4822, 11.3601, 72.0), note = "Pranzo al parco", photoUri = null
        ),
        ParkingSession(
            id = 11, isActive = false, vehicleId = 1, parkingType = ParkingType.HOURLY_PAID,
            startTime = dateToMillis(2026, 4, 11, 8, 30), endTime = dateToMillis(2026, 4, 11, 13, 0),
            hourlyRate = 2.00, finalCost = 9.00, position = Position(44.4931, 11.3468, 55.0),
            note = "Ufficio", photoUri = null
        ),
        ParkingSession(
            id = 12, isActive = false, vehicleId = 3, parkingType = ParkingType.FIXED_TICKET,
            startTime = dateToMillis(2026, 4, 18, 7, 0), endTime = dateToMillis(2026, 4, 18, 19, 0),
            fixedCost = 10.00, expiryTime = dateToMillis(2026, 4, 18, 20, 0), finalCost = 10.00,
            position = Position(44.5058, 11.3417, 48.0), note = "Viaggio in treno", photoUri = null
        ),
        ParkingSession(
            id = 13, isActive = false, vehicleId = 1, parkingType = ParkingType.HOURLY_PAID,
            startTime = dateToMillis(2026, 4, 25, 17, 0), endTime = dateToMillis(2026, 4, 25, 20, 0),
            hourlyRate = 1.50, finalCost = 4.50, position = Position(44.4989, 11.3542, 50.0),
            note = "Aperitivo", photoUri = null
        ),

        // ==================== MAGGIO ====================
        ParkingSession(
            id = 14, isActive = false, vehicleId = 3, parkingType = ParkingType.HOURLY_PAID,
            startTime = dateToMillis(2026, 5, 2, 9, 30), endTime = dateToMillis(2026, 5, 2, 12, 0),
            hourlyRate = 2.50, finalCost = 6.25, position = Position(44.4965, 11.3580, 51.0),
            note = null, photoUri = null
        ),
        ParkingSession(
            id = 15, isActive = false, vehicleId = 2, parkingType = ParkingType.FREE,
            startTime = dateToMillis(2026, 5, 8, 20, 30), endTime = dateToMillis(2026, 5, 8, 23, 30),
            position = Position(44.4880, 11.3310, 60.0), note = "Cinema", photoUri = null
        ),
        ParkingSession(
            id = 16, isActive = false, vehicleId = 1, parkingType = ParkingType.HOURLY_PAID,
            startTime = dateToMillis(2026, 5, 14, 14, 0), endTime = dateToMillis(2026, 5, 14, 18, 30),
            hourlyRate = 2.00, finalCost = 9.00, position = Position(44.4949, 11.3426, 54.0),
            note = "Lavoro cliente", photoUri = null
        ),
        ParkingSession(
            id = 17, isActive = false, vehicleId = 3, parkingType = ParkingType.FIXED_TICKET,
            startTime = dateToMillis(2026, 5, 21, 10, 0), endTime = dateToMillis(2026, 5, 21, 16, 0),
            fixedCost = 8.00, expiryTime = dateToMillis(2026, 5, 21, 18, 0), finalCost = 8.00,
            position = Position(44.5123, 11.3200, 42.0), note = "Shopping mall", photoUri = null
        ),
        ParkingSession(
            id = 18, isActive = false, vehicleId = 1, parkingType = ParkingType.HOURLY_PAID,
            startTime = dateToMillis(2026, 5, 29, 8, 0), endTime = dateToMillis(2026, 5, 29, 11, 0),
            hourlyRate = 1.80, finalCost = 5.40, position = Position(44.4931, 11.3468, 55.0),
            note = null, photoUri = null
        ),

        // ==================== GIUGNO ====================
        ParkingSession(
            id = 19, isActive = false, vehicleId = 2, parkingType = ParkingType.FREE,
            startTime = dateToMillis(2026, 6, 3, 15, 0), endTime = dateToMillis(2026, 6, 3, 19, 0),
            position = Position(44.0678, 12.5695, 5.0), note = "Pomeriggio al mare", photoUri = null
        ),
        ParkingSession(
            id = 20, isActive = false, vehicleId = 1, parkingType = ParkingType.HOURLY_PAID,
            startTime = dateToMillis(2026, 6, 10, 10, 0), endTime = dateToMillis(2026, 6, 10, 12, 0),
            hourlyRate = 2.00, finalCost = 4.00, position = Position(44.4949, 11.3426, 54.0),
            note = "Commissioni", photoUri = null
        ),
        ParkingSession(
            id = 21, isActive = false, vehicleId = 3, parkingType = ParkingType.HOURLY_PAID,
            startTime = dateToMillis(2026, 6, 17, 18, 0), endTime = dateToMillis(2026, 6, 17, 22, 30),
            hourlyRate = 2.50, finalCost = 11.25, position = Position(44.4965, 11.3580, 51.0),
            note = "Cena centro", photoUri = null
        ),
        ParkingSession(
            id = 22, isActive = false, vehicleId = 1, parkingType = ParkingType.FIXED_TICKET,
            startTime = dateToMillis(2026, 6, 25, 8, 0), endTime = dateToMillis(2026, 6, 25, 17, 0),
            fixedCost = 12.00, expiryTime = dateToMillis(2026, 6, 25, 20, 0), finalCost = 12.00,
            position = Position(44.5058, 11.3417, 48.0), note = "Giornata fuori porta", photoUri = null
        ),

        // ==================== LUGLIO ====================
        ParkingSession(
            id = 23, isActive = false, vehicleId = 1, parkingType = ParkingType.HOURLY_PAID,
            startTime = dateToMillis(2026, 7, 2, 9, 0), endTime = dateToMillis(2026, 7, 2, 13, 0),
            hourlyRate = 1.80, finalCost = 7.20, position = Position(44.4931, 11.3468, 55.0),
            note = "Ufficio", photoUri = null
        ),
        ParkingSession(
            id = 24, isActive = false, vehicleId = 2, parkingType = ParkingType.FREE,
            startTime = dateToMillis(2026, 7, 8, 18, 0), endTime = dateToMillis(2026, 7, 8, 21, 0),
            position = Position(44.4822, 11.3601, 72.0), note = "Aperitivo collinare", photoUri = null
        ),
        ParkingSession(
            id = 25, isActive = false, vehicleId = 3, parkingType = ParkingType.FIXED_TICKET,
            startTime = dateToMillis(2026, 7, 15, 10, 0), endTime = dateToMillis(2026, 7, 15, 20, 0),
            fixedCost = 15.00, expiryTime = dateToMillis(2026, 7, 15, 23, 59), finalCost = 15.00,
            position = Position(44.0678, 12.5695, 5.0), note = "Sosta giornata mare", photoUri = null
        ),
        ParkingSession(
            id = 26, isActive = false, vehicleId = 1, parkingType = ParkingType.HOURLY_PAID,
            startTime = dateToMillis(2026, 7, 22, 11, 30), endTime = dateToMillis(2026, 7, 22, 14, 0),
            hourlyRate = 2.00, finalCost = 5.00, position = Position(44.4989, 11.3542, 50.0),
            note = "Pranzo di lavoro", photoUri = null
        ),
        ParkingSession(
            id = 27, isActive = false, vehicleId = 3, parkingType = ParkingType.HOURLY_PAID,
            startTime = dateToMillis(2026, 7, 29, 16, 0), endTime = dateToMillis(2026, 7, 29, 19, 30),
            hourlyRate = 2.50, finalCost = 8.75, position = Position(44.4965, 11.3580, 51.0),
            note = null, photoUri = null
        ),

        // ==================== AGOSTO ====================
        ParkingSession(
            id = 28, isActive = false, vehicleId = 2, parkingType = ParkingType.FREE,
            startTime = dateToMillis(2026, 8, 3, 10, 0), endTime = dateToMillis(2026, 8, 3, 18, 0),
            position = Position(44.0678, 12.5695, 5.0), note = "Vacanze - Riviera", photoUri = null
        ),
        ParkingSession(
            id = 29, isActive = false, vehicleId = 3, parkingType = ParkingType.FIXED_TICKET,
            startTime = dateToMillis(2026, 8, 11, 8, 0), endTime = dateToMillis(2026, 8, 11, 22, 0),
            fixedCost = 18.00, expiryTime = dateToMillis(2026, 8, 11, 23, 59), finalCost = 18.00,
            position = Position(44.0678, 12.5695, 5.0), note = "Parcheggio Hotel", photoUri = null
        ),
        ParkingSession(
            id = 30, isActive = false, vehicleId = 1, parkingType = ParkingType.HOURLY_PAID,
            startTime = dateToMillis(2026, 8, 20, 17, 30), endTime = dateToMillis(2026, 8, 20, 20, 0),
            hourlyRate = 1.50, finalCost = 3.75, position = Position(44.4949, 11.3426, 54.0),
            note = "Rientro in città", photoUri = null
        ),
        ParkingSession(
            id = 31, isActive = false, vehicleId = 2, parkingType = ParkingType.FREE,
            startTime = dateToMillis(2026, 8, 27, 21, 0), endTime = dateToMillis(2026, 8, 28, 0, 30),
            position = Position(44.4880, 11.3310, 60.0), note = "Uscita serale", photoUri = null
        ),

        // ==================== SETTEMBRE (Corrente) ====================
        ParkingSession(
            id = 32, isActive = false, vehicleId = 1, parkingType = ParkingType.HOURLY_PAID,
            startTime = dateToMillis(2026, 9, 1, 8, 30), endTime = dateToMillis(2026, 9, 1, 12, 30),
            hourlyRate = 2.00, finalCost = 8.00, position = Position(44.4931, 11.3468, 55.0),
            note = "Riapertura ufficio", photoUri = null
        ),
        ParkingSession(
            id = 33, isActive = false, vehicleId = 3, parkingType = ParkingType.HOURLY_PAID,
            startTime = dateToMillis(2026, 9, 3, 14, 0), endTime = dateToMillis(2026, 9, 3, 16, 30),
            hourlyRate = 2.50, finalCost = 6.25, position = Position(44.4965, 11.3580, 51.0),
            note = "Appuntamento", photoUri = null
        ),
        // Sessione Attiva Oggi
        ParkingSession(
            id = 34, isActive = true, vehicleId = 1, parkingType = ParkingType.HOURLY_PAID,
            startTime = dateToMillis(2026, 9, 4, 13, 0), endTime = null,
            hourlyRate = 2.00, finalCost = null, position = Position(44.4949, 11.3426, 54.0),
            note = "Sosta in corso", photoUri = null
        )
    )
}
package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "ComplexoEsportivo.db"
        private const val DATABASE_VERSION = 4

        // Tabela de Usuários
        private const val TABLE_USERS = "users"
        private const val COLUMN_USER_ID = "id"
        private const val COLUMN_USER_CPF = "cpf"
        private const val COLUMN_USER_NAME = "name"
        private const val COLUMN_USER_PHONE = "phone"
        private const val COLUMN_USER_EMAIL = "email"
        private const val COLUMN_USER_BIRTH = "birth_date"
        private const val COLUMN_USER_PASSWORD = "password"
        private const val COLUMN_USER_TYPE = "userType"

        // Tabela de Atividades
        private const val TABLE_ACTIVITIES = "activities"
        private const val COLUMN_ACTIVITY_ID = "id"
        private const val COLUMN_ACTIVITY_NAME = "name"
        private const val COLUMN_ACTIVITY_DESCRIPTION = "description"
        private const val COLUMN_ACTIVITY_SCHEDULE = "schedule"
        private const val COLUMN_ACTIVITY_LOCATION = "location"

        // Tabela de Agendamentos
        private const val TABLE_BOOKINGS = "bookings"
        private const val COLUMN_BOOKING_ID = "id"
        private const val COLUMN_BOOKING_DATE = "date"
        private const val COLUMN_BOOKING_USER_NAME = "userName"
        private const val COLUMN_BOOKING_STATUS = "status"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Criação da tabela de usuários
        val createUserTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_CPF TEXT UNIQUE,
                $COLUMN_USER_NAME TEXT,
                $COLUMN_USER_PHONE TEXT,
                $COLUMN_USER_EMAIL TEXT,
                $COLUMN_USER_BIRTH TEXT,
                $COLUMN_USER_PASSWORD TEXT,
                $COLUMN_USER_TYPE TEXT CHECK($COLUMN_USER_TYPE IN ('admin', 'user')) NOT NULL DEFAULT 'user'
            )
        """
        db?.execSQL(createUserTable)

        // Criação da tabela de atividades
        val createActivityTable = """
            CREATE TABLE $TABLE_ACTIVITIES (
                $COLUMN_ACTIVITY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ACTIVITY_NAME TEXT,
                $COLUMN_ACTIVITY_DESCRIPTION TEXT,
                $COLUMN_ACTIVITY_SCHEDULE TEXT,
                $COLUMN_ACTIVITY_LOCATION TEXT
            )
        """
        db?.execSQL(createActivityTable)

        // Criação da tabela de agendamentos
        val createBookingsTable = """
            CREATE TABLE $TABLE_BOOKINGS (
                $COLUMN_BOOKING_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_BOOKING_DATE TEXT UNIQUE,
                $COLUMN_BOOKING_USER_NAME TEXT,
                $COLUMN_BOOKING_STATUS TEXT CHECK($COLUMN_BOOKING_STATUS IN ('available', 'booked')) NOT NULL DEFAULT 'available'
            )
        """
        db?.execSQL(createBookingsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < DATABASE_VERSION) {
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_ACTIVITIES")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_BOOKINGS")
            onCreate(db)
        }
    }

    // Adiciona um novo usuário
    fun addUser(
        cpf: String,
        name: String,
        phone: String,
        email: String,
        password: String,
        userType: String
    ): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_CPF, cpf)
            put(COLUMN_USER_NAME, name)
            put(COLUMN_USER_PHONE, phone)
            put(COLUMN_USER_EMAIL, email)
            put(COLUMN_USER_PASSWORD, password)
            put(COLUMN_USER_TYPE, userType)
        }
        return db.insert(TABLE_USERS, null, values)
    }

    // Autentica um usuário
    fun authenticateUser(cpf: String, password: String): User? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USER_CPF = ?"
        val cursor = db.rawQuery(query, arrayOf(cpf))

        if (cursor.moveToFirst()) {
            val storedPassword = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD))
            val userType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_TYPE))

            if (storedPassword == password) {
                cursor.close()
                return User(cpf, userType)
            }
        }

        cursor.close()
        return null
    }

    // Adiciona uma nova atividade
    fun addActivity(name: String, description: String, schedule: String, location: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ACTIVITY_NAME, name)
            put(COLUMN_ACTIVITY_DESCRIPTION, description)
            put(COLUMN_ACTIVITY_SCHEDULE, schedule)
            put(COLUMN_ACTIVITY_LOCATION, location)
        }
        return db.insert(TABLE_ACTIVITIES, null, values)
    }

    // Busca todas as atividades
    fun getAllActivities(): List<SportActivity> {
        val db = this.readableDatabase
        val activityList = mutableListOf<SportActivity>()
        val cursor = db.rawQuery("SELECT * FROM $TABLE_ACTIVITIES", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACTIVITY_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACTIVITY_NAME))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACTIVITY_DESCRIPTION))
                val schedule = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACTIVITY_SCHEDULE))
                val location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACTIVITY_LOCATION))

                activityList.add(SportActivity(id, name, description, schedule, location))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return activityList
    }

    // Adiciona um agendamento
    fun addBooking(date: String, userName: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_BOOKING_DATE, date)
            put(COLUMN_BOOKING_USER_NAME, userName)
            put(COLUMN_BOOKING_STATUS, "booked")
        }
        return db.insert(TABLE_BOOKINGS, null, values)
    }

    // Busca todos os agendamentos
    fun getAllBookings(): List<Pair<String, String>> {
        val db = this.readableDatabase
        val bookings = mutableListOf<Pair<String, String>>()
        val cursor = db.rawQuery("SELECT $COLUMN_BOOKING_DATE, $COLUMN_BOOKING_STATUS FROM $TABLE_BOOKINGS", null)

        if (cursor.moveToFirst()) {
            do {
                val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_DATE))
                val status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_STATUS))
                bookings.add(Pair(date, status))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return bookings
    }
}


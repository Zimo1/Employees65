package ru.yodata.employees65

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*
import ru.yodata.employees65.dto.EmployeeCard
import ru.yodata.employees65.dto.Specialty

//======= Свойства и методы верхнего уровня =======
const val DB_NAME = "EmployeesDB" //Название внутренней базы данных сотрудников
const val EMPLOYEES_TABLE_NAME = "EmployeesTable" //Название таблицы сотрудников
const val SPECIALTY_TABLE_NAME = "SpecialtyTable" //Название таблицы специальностей
//Поля таблицы EMPLOYEES_TABLE_NAME:
const val EMPLOYEES_ROWID_FIELD = "_id"
const val F_NAME_FIELD = "fName"
const val L_NAME_FIELD = "lName"
const val BIRTHDAY_FIELD = "birthday"
const val AVATAR_URL_FIELD = "avatarURL"
const val SPECIALTY_ID_FIELD = "specialtyId"
//Поля таблицы SPECIALTY_TABLE_NAME:
const val SPECIALTY_ROWID_FIELD = "_id"
const val SPECIALTY_NAME_FIELD = "specialtyName"
//Методы для получения различных данных из БД
fun getSpecialtyId(cursor: Cursor): Int = cursor.getInt(
    cursor.getColumnIndexOrThrow(SPECIALTY_ID_FIELD))
fun getSpecialtyName(cursor: Cursor): String = cursor.getString(
    cursor.getColumnIndexOrThrow(SPECIALTY_NAME_FIELD))
fun getSpecialty(cursor: Cursor): Specialty = Specialty(getSpecialtyId(cursor),
    getSpecialtyName(cursor))
fun getEmployeeCard(cursor: Cursor): EmployeeCard = EmployeeCard(
    cursor.getString(cursor.getColumnIndexOrThrow(F_NAME_FIELD)),
    cursor.getString(cursor.getColumnIndexOrThrow(L_NAME_FIELD)),
    cursor.getString(cursor.getColumnIndexOrThrow(BIRTHDAY_FIELD)).getBirhdayOrNot(),
    cursor.getString(cursor.getColumnIndexOrThrow(BIRTHDAY_FIELD)).getAgeOrNot(),
    cursor.getString(cursor.getColumnIndexOrThrow(AVATAR_URL_FIELD)),
    cursor.getString(cursor.getColumnIndexOrThrow(SPECIALTY_NAME_FIELD))
)
//=================================================

class MyDatabaseAnkoHelper private constructor(ctx: Context)
    : ManagedSQLiteOpenHelper(ctx, DB_NAME, null, 1) {
    init {
        instance = this
    }
    companion object { //Синглтон базы данных
        private var instance: MyDatabaseAnkoHelper? = null
        @Synchronized
        fun getInstance(ctx: Context) = instance ?: MyDatabaseAnkoHelper(ctx.applicationContext)
    }
    override fun onCreate(db: SQLiteDatabase) {
        // Создаем таблицы специальностей и сотрудников, связанных по полю specialtyId
        db.createTable(SPECIALTY_TABLE_NAME, true,
            SPECIALTY_ROWID_FIELD to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            SPECIALTY_ID_FIELD to INTEGER + UNIQUE,
            SPECIALTY_NAME_FIELD to TEXT)
        db.createTable(EMPLOYEES_TABLE_NAME, true,
            EMPLOYEES_ROWID_FIELD to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            F_NAME_FIELD to TEXT,
            L_NAME_FIELD to TEXT,
            BIRTHDAY_FIELD to TEXT,
            AVATAR_URL_FIELD to TEXT,
            SPECIALTY_ID_FIELD to INTEGER,
            FOREIGN_KEY(SPECIALTY_ID_FIELD,
                SPECIALTY_TABLE_NAME,
                SPECIALTY_ID_FIELD) )
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Обновление версии БД
        db.dropTable(EMPLOYEES_TABLE_NAME, true)
        db.dropTable(SPECIALTY_TABLE_NAME, true)
    }
}
// Создаем extention-свойство database для доступа к БД из приложения
val Context.database: MyDatabaseAnkoHelper
    get() = MyDatabaseAnkoHelper.getInstance(this)


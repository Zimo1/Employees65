package ru.yodata.employees65
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.jetbrains.anko.db.dropTable
import org.jetbrains.anko.db.insert
import ru.yodata.employees65.dto.*
import ru.yodata.employees65.fragments.SpecListFragment
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.time.Instant.now
import java.util.*
import kotlin.coroutines.CoroutineContext


//======= Свойства и методы верхнего уровня =======
const val SERVER_URL = "https://gitlab.65apps.com/65gb/static/raw/master/" //Сервер REST API
const val SERVER_RESPONSE_OK = 200 //HTTP ответ сервера "Успешный запрос"
const val BIRTHDAY_ABSENT_STRING = "-"
const val AGE_ABSENT_STRING = "Неизвестен"
//Функция correctNameWriting делает первую букву любой строки большой, остальные маленькими:
fun String.correctNameWriting(): String = this.toLowerCase().capitalize()
//Функция correctDateWriting конвертирует дату в нужный по условиям задачи формат.
fun String?.correctDateWriting(): String? = if (this?.indexOf("-") == 4)
    SimpleDateFormat("dd-MM-yyyy").format(SimpleDateFormat("yyyy-MM-dd").parse(this))
    else this ?: null
//Функция getAge вычисляет возраст (количество полных лет) по дате рождения, данной в строковом формате.
fun String.getAge(): Int = ((Date().time -
        SimpleDateFormat("dd-MM-yyyy").parse(this).time)/31536000000).toInt()
//Функция getAgeOrNot - аналог предыдущей функции. При отстствущей дате рождения выдает AGE_ABSENT_STRING.
fun String?.getAgeOrNot(): String = if (this.isNullOrBlank()) AGE_ABSENT_STRING else this.getAge().toString()
//Функция getBirhdayOrNot при отстствущей дате рождения выдает BIRTHDAY_ABSENT_STRING.
fun String?.getBirhdayOrNot(): String = if (this.isNullOrBlank()) BIRTHDAY_ABSENT_STRING else this
//=================================================

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        launch {
            progressBar.visibility = ProgressBar.VISIBLE
            //Прочитать и распарсить JSON данные о сотрудниках с удаленного сервера
            val resultData = withContext(Dispatchers.IO) {
                    Repository.getData()
            }
            //Записать полученные данные о сотрудниках в базу данных SQLite
            val employees: List<Employees>? = resultData.body()?.employees
            if (resultData.code() == SERVER_RESPONSE_OK && employees != null) {
                with (employees) {
                    val specialtySet = mutableSetOf<Int>() //Создать множество кодов специальностей
                    forEach { //Цикл по списку сотрудников employees
                        val curSpecId: Int = it.specialty.first().specialtyId //специальность сотрудника
                        if (curSpecId !in specialtySet) { //существует ли уже такой код специальности?
                            database.use { //если нет - добавить запись в таблицу специальностей
                                insert(
                                    SPECIALTY_TABLE_NAME,
                                    SPECIALTY_ID_FIELD to curSpecId,
                                    SPECIALTY_NAME_FIELD to it.specialty.first().specialtyName.
                                        correctNameWriting()
                                )
                            }
                            specialtySet.add(curSpecId) // а так же добавить значение в множество
                        }
                        //Внести в БД запись о текущем сотруднике
                        database.use {
                            insert(
                                EMPLOYEES_TABLE_NAME,
                                F_NAME_FIELD to it.fName.correctNameWriting(),
                                L_NAME_FIELD to it.lName.correctNameWriting(),
                                BIRTHDAY_FIELD to it.birthday.correctDateWriting(),
                                AVATAR_URL_FIELD to it.avatarURL,
                                SPECIALTY_ID_FIELD to curSpecId
                            )
                        }
                    }
                }
            } //if (resultData.code() == SERVER_RESPONSE_OK && employees != null)
            progressBar.visibility = ProgressBar.GONE

            //Выводим на экран первый фрагмент - список специальностей
            if (savedInstanceState == null) {
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.root_layout, SpecListFragment.newInstance(), "specList")
                    .commit()
            }
        } //launch {
    }
    override fun onDestroy() {
        super.onDestroy()
        database.use {
            //При выходе БД уничтожается и при запуске приложения создается каждый раз заново.
            //Это сделано в учебных целях, чтобы продемонстрировать загрузку данных из Интернета
            dropTable(EMPLOYEES_TABLE_NAME, true)
            dropTable(SPECIALTY_TABLE_NAME, true)
            deleteDatabase(DB_NAME)
        }
        cancel()
    }
}

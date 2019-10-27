package ru.yodata.employees65.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.person_fragment.*
import ru.yodata.employees65.*
import ru.yodata.employees65.dto.EmployeeCard

class PersonFragment: Fragment() {
    companion object {
        var curPerson = EmployeeCard("Инициализация", "Инициализация",
            "Инициализация","Инициализация","Инициализация","Инициализация")
        fun newInstance(employee: EmployeeCard): PersonFragment {
            curPerson = employee
            return PersonFragment()
        }
    }
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.person_fragment, container, false)
//        firstNameTV.text = curPerson.fName
//        lastNameTV.text = curPerson.lName
//        birthdayTV.text = curPerson.birthday
//        ageTV.text = curPerson.age
//        specialtyTV.text = curPerson.specialty
        return view
    }

    override fun onStart() {
        super.onStart()
        firstNameTV.text = curPerson.fName
        lastNameTV.text = curPerson.lName
        birthdayTV.text = curPerson.birthday
        ageTV.text = curPerson.age
        specialtyTV.text = curPerson.specialty
    }
}
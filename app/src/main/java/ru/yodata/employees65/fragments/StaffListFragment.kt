package ru.yodata.employees65.fragments

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.person_line.view.*
import kotlinx.android.synthetic.main.stafflist_fragment.view.*
import ru.yodata.employees65.*
import ru.yodata.employees65.dto.EmployeeCard
import ru.yodata.employees65.dto.Specialty

class StaffListFragment: Fragment() {
    companion object {
        var curSpecialty = Specialty(0, "Инициализация")
        fun newInstance(specialty: Specialty): StaffListFragment {
            curSpecialty = specialty
            return StaffListFragment()
        }
    }
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.stafflist_fragment, container, false)
        val db = context!!.database.readableDatabase
        val sqlQuery = "SELECT * FROM ${EMPLOYEES_TABLE_NAME} INNER JOIN ${SPECIALTY_TABLE_NAME} " +
                "ON ${EMPLOYEES_TABLE_NAME}.${SPECIALTY_ID_FIELD} = " +
                "${SPECIALTY_TABLE_NAME}.${SPECIALTY_ID_FIELD} " +
                "WHERE ${EMPLOYEES_TABLE_NAME}.${SPECIALTY_ID_FIELD} = ${curSpecialty.specialtyId}"
        val emplCursor: Cursor = db.rawQuery(sqlQuery,null)
        val activity = activity as Context
        val recyclerView = view.findViewById<RecyclerView>(R.id.staff_container)
        if (emplCursor != null && emplCursor.count > 0)
            with(recyclerView) {
                layoutManager = LinearLayoutManager(activity)
                adapter = StaffListAdapter(activity, emplCursor)
            }
        else
            Toast.makeText(context, "Работников с такой специальностью нет", Toast.LENGTH_LONG).show()
        view.curSpecialtyTV.text = curSpecialty.specialtyName
        return view
    }
    internal inner class StaffListAdapter(context: Context, cursor: Cursor)
        : CursorRecyclerViewAdapter<StaffViewHolder>(context,  cursor) {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): StaffViewHolder {
            return StaffViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.person_line, parent, false),
                cursor
            )
        }
        override fun onBindViewHolder(viewHolder: StaffViewHolder, cursor: Cursor) {
            with (viewHolder) {
                bind(
                    cursor.getString(cursor.getColumnIndexOrThrow(F_NAME_FIELD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(L_NAME_FIELD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(BIRTHDAY_FIELD)).getAgeOrNot()
                )
                itemView.setOnClickListener(onEmployeeSelected(getEmployeeCard(cursor)))
            }
        }
    }
    class StaffViewHolder constructor(view: View, cursor: Cursor): RecyclerView.ViewHolder(view) {
        fun bind(fName: String, lName: String, age: String) {
            itemView.nameTV.text = lName + " " + fName
            itemView.ageTV.text = age
        }
    }
    fun onEmployeeSelected(employee: EmployeeCard) = View.OnClickListener {
        fragmentManager!!
            .beginTransaction()
            .replace(R.id.root_layout, PersonFragment.newInstance(employee), "personCard")
            .addToBackStack(null)
            .commit()
        //Toast.makeText(context, "Работник: ${employee.fName} ${employee.specialty}", Toast.LENGTH_LONG).show()
    }
}
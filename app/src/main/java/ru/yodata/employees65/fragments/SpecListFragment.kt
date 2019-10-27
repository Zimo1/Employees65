package ru.yodata.employees65.fragments

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
//import android.support.v4.app.Fragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.spec_line.view.*
import org.jetbrains.anko.db.asSequence
import ru.yodata.employees65.*
//import ru.yodata.employees65.*
import ru.yodata.employees65.dto.Specialty

class SpecListFragment: Fragment() {
    companion object {
        fun newInstance(): SpecListFragment {
            return SpecListFragment()
        }
    }
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.speclist_fragment, container, false)
        val db = context!!.database.readableDatabase
        val specCursor: Cursor = db.query(SPECIALTY_TABLE_NAME, arrayOf("*"),
            null, null, null, null, null,null)
        val activity = activity as Context
        val recyclerView = view.findViewById<RecyclerView>(R.id.spec_container)
        if (specCursor != null && specCursor.count > 0)
            with(recyclerView) {
                layoutManager = LinearLayoutManager(activity)
                adapter = SpecListAdapter(activity, specCursor)
            }
        else
            Toast.makeText(context, "Таблица специальностей пуста", Toast.LENGTH_LONG).show()
        return view
    }
    internal inner class SpecListAdapter(context: Context, cursor: Cursor)
        : CursorRecyclerViewAdapter<SpecViewHolder>(context,  cursor) {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): SpecViewHolder {
            return SpecViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.spec_line, parent, false),
                cursor
            )
        }
        override fun onBindViewHolder(viewHolder: SpecViewHolder, cursor: Cursor) {
            viewHolder.bind(cursor.getString(cursor.getColumnIndexOrThrow(SPECIALTY_NAME_FIELD)))
            viewHolder.itemView.setOnClickListener(onSpecialtySelected(getSpecialty(cursor)))
        }
    }
    class SpecViewHolder constructor(view: View, cursor: Cursor): RecyclerView.ViewHolder(view) {
        fun bind(specialtyName: String) {
            itemView.specTV.text = specialtyName
        }
    }
    fun onSpecialtySelected(specialty: Specialty) = View.OnClickListener {
        fragmentManager!!
            .beginTransaction()
            .replace(R.id.root_layout, StaffListFragment.newInstance(specialty), "staffList")
            .addToBackStack(null)
            .commit()
        //Toast.makeText(context, "Cпециальность: {${specialty.specialtyName}}", Toast.LENGTH_LONG).show()
    }
}
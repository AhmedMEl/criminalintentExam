package com.example.criminalintentexam

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

private const val TAG = "CrimeListFragment"
class CrimeListFragment : Fragment() {
    private lateinit var emptyLayout:RelativeLayout
    private lateinit var emptyButton:Button
    private lateinit var layout:View
    private lateinit var addCrimeButton: Button
    private lateinit var crimeRecyclerView: RecyclerView
    private var adapter: CrimeAdapter? = CrimeAdapter(emptyList())
    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeListViewModel::class.java)
    }
    var callBacks:CallBacks?= null

    interface CallBacks{
        fun onItemSelected(crimeId:UUID)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        emptyLayout=view.findViewById(R.id.empty_view)
        emptyButton=view.findViewById(R.id.empty_button)
        emptyButton.setOnClickListener {
            val crime = Crime()
            crimeListViewModel.addCrime(crime)
            callBacks?.onItemSelected(crime.id)
        }
        crimeRecyclerView.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            Observer { crimes ->
                crimes?.let {
                    Log.i(TAG, "Got crimes ${crimes.size}")
                    updateUI(crimes)
                }
            })
    }

    private fun updateUI(crimes: List<Crime>) {
        adapter = CrimeAdapter(crimes)
        crimeRecyclerView.adapter = adapter
        val adapterItem=crimeRecyclerView.adapter as CrimeAdapter
        if (adapter!!.getItemCount() == 0) {
            emptyLayout.setVisibility(View.VISIBLE);
        } else {
            emptyLayout.setVisibility(View.GONE);
        }
        adapterItem.submitList(crimes)
    }

    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }

    private inner class CrimeHolder(view: View)
        : RecyclerView.ViewHolder(view),View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }
        private lateinit var crime: Crime
        val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        val solvedImageView: ImageView = itemView.findViewById(R.id.imageView)
        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = this.crime.date.toString()
            solvedImageView.visibility=if(crime.isSolved){
                View.VISIBLE
            }else{
               View.GONE
            }
        }
        override fun onClick(v: View) {
//            val fragment = CrimeFragment()
//            val fm = activity?.supportFragmentManager
//            fm?.beginTransaction()
//                ?.replace(R.id.fragment_container, fragment)
//                ?.commit()
            callBacks?.onItemSelected(crime.id)

        }
    }
    private inner class CrimeAdapter(var crimes: List<Crime>)
        :androidx.recyclerview.widget.ListAdapter<Crime, CrimeHolder>(CrimeDiffUtil()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                : CrimeHolder {
            val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
            return CrimeHolder(view)
        }
        override fun getItemCount() = crimes.size
        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes[position]
            holder.bind(crime)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callBacks=context as CallBacks?
    }

    override fun onDetach() {
        super.onDetach()
        callBacks=null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime -> {
                val crime = Crime()
                crimeListViewModel.addCrime(crime)
                callBacks?.onItemSelected(crime.id)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    class CrimeDiffUtil:DiffUtil.ItemCallback<Crime>(){
        override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem.id === newItem.id
        }

        override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem == newItem
        }

    }

}
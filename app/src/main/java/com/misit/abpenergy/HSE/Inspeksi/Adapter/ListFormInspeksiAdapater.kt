package com.misit.abpenergy.HSE.Inspeksi.Adapterimport android.content.Contextimport android.view.LayoutInflaterimport android.view.Viewimport android.view.ViewGroupimport android.widget.TextViewimport androidx.cardview.widget.CardViewimport androidx.recyclerview.widget.RecyclerViewimport com.misit.abpenergy.HSE.Inspeksi.Response.FormItemimport com.misit.abpenergy.Rimport java.text.SimpleDateFormatclass ListFormInspeksiAdapater (private val c: Context?,                                private val inspeksiList:MutableList<FormItem>):    RecyclerView.Adapter<ListFormInspeksiAdapater.MyViewHolder>() {    private var onItemClickListener: OnItemClickListener? = null    private val layoutInflater: LayoutInflater    private var simpleDateFormat: SimpleDateFormat? = null    lateinit var view:View    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {        view = layoutInflater.inflate(R.layout.form_inspeksi_list,parent,false)        return MyViewHolder(view)    }    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {        var inspeksiList = inspeksiList[position]        holder.tvFormName.text = inspeksiList.namaForm.toString()        holder.cvFormInspeksi.setOnClickListener{            onItemClickListener?.onItemClick(inspeksiList.idForm.toString(),inspeksiList.namaForm.toString())        }    }    override fun getItemCount(): Int {        return inspeksiList.size    }    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {        var cvFormInspeksi = itemView.findViewById<View>(R.id.cvFormInspeksi) as CardView        var tvFormName = itemView.findViewById<View>(R.id.tvFormName) as TextView    }    interface OnItemClickListener{        fun onItemClick(idForm:String,nameForm:String)    }    fun setListener (listener: OnItemClickListener){        onItemClickListener = listener    }    init {        layoutInflater = LayoutInflater.from(c)        simpleDateFormat= SimpleDateFormat("yyyy-MM-dd")    }}
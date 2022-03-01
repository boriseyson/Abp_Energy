package com.misit.abpenergy.HSE.HazardReport.Adapterimport android.content.Contextimport android.graphics.Colorimport android.view.LayoutInflaterimport android.view.Viewimport android.view.ViewGroupimport android.widget.Buttonimport android.widget.LinearLayoutimport androidx.recyclerview.widget.RecyclerViewimport com.misit.abpenergy.HSE.HazardReport.Response.LokasiItemimport com.misit.abpenergy.Rimport java.text.SimpleDateFormatclass LokasiAdapter(private val c: Context?,                    private var lokasiDipilih: String?,                    private val lokasiItem:MutableList<LokasiItem>):    RecyclerView.Adapter<LokasiAdapter.MyViewHolder>() {    private var onItemClickListener: OnItemClickListener? = null    private val layoutInflater: LayoutInflater    private var simpleDateFormat: SimpleDateFormat? = null    lateinit var view:View    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {        var lokasi = itemView.findViewById<View>(R.id.rbSumberBahaya) as Button        var lnPilih = itemView.findViewById<View>(R.id.lnPilih) as LinearLayout    }    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {        view = layoutInflater.inflate(R.layout.sumber_bahaya_list,parent,false)        return MyViewHolder(view)    }    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {        var itemLokasi =lokasiItem[position]        holder.lokasi.text = itemLokasi.lokasi        if(lokasiDipilih==itemLokasi.lokasi){            holder.lokasi.setTextColor(Color.parseColor("#FFFFFF"))            holder.lokasi.setBackgroundColor(Color.parseColor("#4F8E50"))            holder.lnPilih.setBackgroundColor(Color.parseColor("#4F8E50"))        }else{            holder.lokasi.setTextColor(Color.parseColor("#4F8E50"))            holder.lokasi.setBackgroundColor(Color.parseColor("#FFFFFF"))            holder.lnPilih.setBackgroundColor(Color.parseColor("#FFFFFF"))        }        holder.lokasi.setOnClickListener{            onItemClickListener?.onItemClick(itemLokasi.idLok.toString(),itemLokasi.lokasi)        }    }    override fun getItemCount(): Int {        return lokasiItem.size    }    interface OnItemClickListener{        fun onItemClick(idLokasi:String?,lokasi:String?)    }    fun setListener (listener: OnItemClickListener){        onItemClickListener = listener    }    init {        layoutInflater = LayoutInflater.from(c)        simpleDateFormat= SimpleDateFormat("yyyy-MM-dd")    }}
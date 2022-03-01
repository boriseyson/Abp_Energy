package com.misit.abpenergy.HSE.HazardReport.Adapterimport android.content.Contextimport android.graphics.Colorimport android.view.LayoutInflaterimport android.view.Viewimport android.view.ViewGroupimport android.widget.Buttonimport android.widget.LinearLayoutimport androidx.recyclerview.widget.RecyclerViewimport com.misit.abpenergy.HSE.HazardReport.Response.RiskItemimport com.misit.abpenergy.Rimport java.text.SimpleDateFormatclass RiskAdapter(private val c: Context?,                  private var riskDipilih: String?,                  private val riskItem:MutableList<RiskItem>):    RecyclerView.Adapter<RiskAdapter.MyViewHolder>() {    private var onItemClickListener: OnItemClickListener? = null    private val layoutInflater: LayoutInflater    private var simpleDateFormat: SimpleDateFormat? = null    lateinit var view: View    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {        var risk = itemView.findViewById<View>(R.id.rbSumberBahaya) as Button        var lnPilih = itemView.findViewById<View>(R.id.lnPilih) as LinearLayout    }    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {        view = layoutInflater.inflate(R.layout.sumber_bahaya_list,parent,false)        return MyViewHolder(view)    }    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {        var riskItem =riskItem[position]        holder.risk.text = riskItem.risk        if(riskDipilih==riskItem.risk){            holder.risk.setTextColor(Color.parseColor("#FFFFFF"))            holder.risk.setBackgroundColor(Color.parseColor("#4F8E50"))            holder.lnPilih.setBackgroundColor(Color.parseColor("#4F8E50"))        }else{            holder.risk.setTextColor(Color.parseColor("#4F8E50"))            holder.risk.setBackgroundColor(Color.parseColor("#FFFFFF"))            holder.lnPilih.setBackgroundColor(Color.parseColor("#FFFFFF"))        }        holder.risk.setOnClickListener{            onItemClickListener?.onItemClick(riskItem.idRisk.toString(),riskItem.risk)        }    }    override fun getItemCount(): Int {        return riskItem.size    }    interface OnItemClickListener{        fun onItemClick(idRisk:String?,risk:String?)    }    fun setListener (listener: OnItemClickListener){        onItemClickListener = listener    }    init {        layoutInflater = LayoutInflater.from(c)        simpleDateFormat= SimpleDateFormat("yyyy-MM-dd")    }}
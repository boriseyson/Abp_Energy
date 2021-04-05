package com.misit.abpenergy.Rkb.FragmentRKB

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.misit.abpenergy.Rkb.DetailRkbActivity
import com.misit.abpenergy.R
import com.misit.abpenergy.Adapter.UserRkbListAdapter
import com.misit.abpenergy.Api.ApiClient
import com.misit.abpenergy.Api.ApiEndPoint
import com.misit.abpenergy.Rkb.Response.*
import com.misit.abpenergy.Rkb.RkbActivity
import com.misit.abpenergy.Utils.ConnectivityUtil
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.cancel_kabag.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
/**
 * A simple [Fragment] subclass.
 */
class WaitingRkbFragment : Fragment() , UserRkbListAdapter.OnItemClickListener ,RefreshDataWaiting {
    private var adapter: UserRkbListAdapter? = null
    private var rkbList: MutableList<DataItem>? = null
    private var username : String?=null
    private var dept : String?=null
    private var sect : String?=null
    private var level : String?=null
    private var page : Int=1
    private var visibleItem : Int=0
    private var total : Int=0
    private var loading : Boolean=false
    private var pastVisibleItem : Int=0
    lateinit var recyclerView: RecyclerView
    lateinit var rvLoading: RelativeLayout
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var apiEndPoint : ApiEndPoint
    private var call: Call<RkbResponse>?=null
    private var _token: String?=null
    lateinit var viewCancel: View
    lateinit var alertDialog: AlertDialog

    var curentPosition: Int=0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_waiting_rkb, container, false)
        setHasOptionsMenu(true)
        val argument = arguments
        username = argument?.getString(RkbActivity.USERNAME)
        dept = argument?.getString(RkbActivity.DEPARTMENT)
        sect = argument?.getString(RkbActivity.SECTON)
        level = argument?.getString(RkbActivity.LEVEL)
        rkbList = ArrayList()
        adapter = UserRkbListAdapter(activity,username,rkbList!!,sect!!)
        recyclerView = view.findViewById(R.id.recycler_totalRkb)
        rvLoading = view.findViewById(R.id.relativeLoading)
        swipeRefreshLayout = view.findViewById(R.id.pullRefresh)
        swipeRefreshLayout.setOnRefreshListener(object :SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                recyclerView.adapter = adapter
                page=1
                rkbList?.clear()
                loadRkbTotal(page.toString())
//                swipeRefreshLayout.isRefreshing=false
                //PopupUtil.dismissDialog()

            }
        })
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
        adapter?.setListener(this)

        viewCancel = LayoutInflater.from(activity).inflate(R.layout.cancel_kabag,null)
        return view
    }

    override fun onResume() {
        if(ConnectivityUtil.isConnected(requireActivity())){
            page=1
            rkbList?.clear()
            loadRkbTotal(page.toString())
        }
        super.onResume()
    }

    private fun loadRkbTotal(curentPage:String) {
        swipeRefreshLayout.isRefreshing=true
        apiEndPoint = ApiClient.getClient(activity)!!.create(ApiEndPoint::class.java)
        if(level=="administrator"){
            call = apiEndPoint.getRkbAdmin(
                username!!,
                dept!!,
                null,
                null,
                null,
                null,
                "1",
                null,
                curentPage,
                level)

        }else{
            call = apiEndPoint.getRkbUser(
                username!!,
                dept!!,
                null,
                null,
                null,
                null,
                "1",
                null,
                curentPage,
                level)

        }

        call?.enqueue(object : Callback<RkbResponse?> {
            override fun onFailure(call: Call<RkbResponse?>, t: Throwable) {
                swipeRefreshLayout.isRefreshing=false
                Toast.makeText(activity, "Failed to Fetch Data\n" +
                        "e: $t", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<RkbResponse?>, response: Response<RkbResponse?>) {
                getToken()
                swipeRefreshLayout.isRefreshing=false
                val rkbResponse = response.body()
                if (rkbResponse != null) {
                    rvLoading.visibility=View.GONE
                    loading=true
                    if(rkbList?.size == 0){
                        rkbList?.addAll(rkbResponse.data!!)
                        activity?.runOnUiThread {
                            adapter?.notifyDataSetChanged()
                        }
                    }else{

                        curentPosition = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                        rkbList?.addAll(rkbResponse.data!!)
                        activity?.runOnUiThread {
                            adapter?.notifyDataSetChanged()
                        }
                    }
                    recyclerView.addOnScrollListener(object :RecyclerView.OnScrollListener(){
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            if (dy > 0) {
                                visibleItem = recyclerView.layoutManager!!.childCount
                                total = recyclerView.layoutManager!!.itemCount
                                pastVisibleItem =
                                    (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                                if (loading) {
                                    if ((visibleItem + pastVisibleItem) >= total) {
                                        rvLoading.visibility=View.VISIBLE
                                        loading = false
                                        page++
                                        loadRkbTotal(page.toString())
                                    }
                                }
                            }
                        }
                        override fun onScrollStateChanged(
                            recyclerView: RecyclerView,
                            newState: Int
                        ) {
                            super.onScrollStateChanged(recyclerView, newState)
                        }
                    })
                } else {
                    swipeRefreshLayout.isRefreshing=true
                    Toast.makeText(activity, "Failed to Fetch Data\n" +
                            "Response is null", Toast.LENGTH_SHORT).show()
                    refresh()
                }
            }
        })
    }
    override fun onItemClick(noRkb: String?) {
        val intent= Intent(activity, DetailRkbActivity::class.java)
        intent.putExtra(DetailRkbActivity.Companion.NO_RKB,noRkb)
        startActivity(intent)
    }
    override fun onApproveKabag(noRkb: String?) {
        approveKabag(username!!,noRkb!!,_token!!)
    }
    override fun onCancelKabag(noRkb: String?) {
        cancelKabag(username!!,noRkb!!,_token!!)
    }

    override fun onApproveKTT(noRkb: String?) {
        approveKTT(username!!,noRkb!!,_token!!)
    }

    override fun onCancelKTT(noRkb: String?) {
        cancelKTT(username!!,noRkb!!,_token!!)
    }

    override fun onCancelUser(noRkb: String?) {
        showDialogOption(sect!!,noRkb!!)
    }

    override fun refresh() {
//        page=1
//        rkbList?.clear()
//        loadRkbTotal(page.toString())
    }

    fun getToken(){
        val call = apiEndPoint.getToken("csrf_token")
        call?.enqueue(object : Callback<CsrfTokenResponse> {
            override fun onFailure(call: Call<CsrfTokenResponse>, t: Throwable) {
                Toast.makeText(context,"Error : $t", Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(
                call: Call<CsrfTokenResponse>,
                response: Response<CsrfTokenResponse>
            ) {
                _token = response.body()?.csrfToken
            }
        })
    }
    fun approveKabag(username:String,noRkb:String,_token:String){
        val call = apiEndPoint.approveKabag(username, noRkb, _token)
        call.enqueue(object : Callback<ApproveRkbResponse> {
            override fun onFailure(call: Call<ApproveRkbResponse>, t: Throwable) {
                Log.d("errrMessage", "$t")
            }
            override fun onResponse(
                call: Call<ApproveRkbResponse>,
                response: Response<ApproveRkbResponse>
            ) {
                val apprResponse = response.body()
                if (apprResponse != null) {
                    if(apprResponse.aprrove==true){
                            adapter?.notifyDataSetChanged()
                        refresh()
                        Toasty.info(activity!!,"Approved",Toasty.LENGTH_SHORT).show()
                    }else{
                        refresh()
                        adapter?.notifyDataSetChanged()
                        Toasty.error(activity!!,"Approve Failed",Toasty.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    fun approveKTT(username:String,noRkb:String,_token:String){
        val call = apiEndPoint.approveKTT(username, noRkb, _token)
        call.enqueue(object : Callback<ApproveRkbResponse> {
            override fun onFailure(call: Call<ApproveRkbResponse>, t: Throwable) {
                Log.d("errrMessage", "$t")
            }
            override fun onResponse(
                call: Call<ApproveRkbResponse>,
                response: Response<ApproveRkbResponse>
            ) {
                val apprResponse = response.body()
                if (apprResponse != null) {
                    if(apprResponse.aprrove==true){
                        adapter?.notifyDataSetChanged()
                        refresh()
                        Toasty.info(activity!!,"Approved",Toasty.LENGTH_SHORT).show()
                    }else{
                        refresh()
                        adapter?.notifyDataSetChanged()
                        Toasty.error(activity!!,"Approve Failed",Toasty.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
    fun cancelKabag(username:String,noRkb:String,_token:String){
        showDialogOption(sect!!,noRkb!!)
    }
    fun cancelKTT(username:String,noRkb:String,_token:String){
        showDialogOption(sect!!,noRkb!!)
    }

    private fun showDialogOption(sect: String, noRkb: String) {
        if(viewCancel.parent!=null){
            (viewCancel.parent as ViewGroup).removeView(viewCancel)
        }

        viewCancel.tvCancelNoRKB.text="Cancel RKB $noRkb"
        viewCancel.btnCancelForm.setOnClickListener {
            alertDialog.dismiss()
        }
        viewCancel.sendCancel.setOnClickListener {
            var remarks = viewCancel.InCancelKabag.text.toString()
            Toasty.info(requireActivity(),remarks,Toasty.LENGTH_SHORT).show()
            cancelRkb(noRkb,username,sect!!,remarks)

        }
        alertDialog = AlertDialog.Builder(requireActivity())
            .setView(viewCancel).create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        alertDialog.show()
    }

    private fun cancelRkb(noRkb: String, username: String?, section: String, remarks: String) {
        val apiEndPoint = ApiClient.getClient(activity)!!.create(ApiEndPoint::class.java)
        val call = apiEndPoint.cancelRkb(
            username!!,
            noRkb!!,
            section,
            remarks,
            _token)
        call?.enqueue(object : Callback<CancelRKBResponse?> {
            override fun onFailure(call: Call<CancelRKBResponse?>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<CancelRKBResponse?>,
                response: Response<CancelRKBResponse?>
            ) {
                var cancelRes = response.body()
                if (cancelRes!=null){
                    if(cancelRes.success!!){
                        Toasty.success(activity!!,"RKB Telah Di Cancel")
                        alertDialog.dismiss()
                    }else{
                        Toasty.error(activity!!,"Cancel RKB Gagal")
                        alertDialog.dismiss()
                    }
                }else{
                    Toasty.error(activity!!,"Cancel RKB Gagal")
                    alertDialog.dismiss()
                }
            }

        })
    }
}
interface RefreshDataWaiting {
    fun refresh()
}
package com.misit.abpenergy.Rkb.FragmentRKB


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.misit.abpenergy.Rkb.DetailRkbActivity

import com.misit.abpenergy.R
import com.misit.abpenergy.Main.Adapter.UserRkbListAdapter
import com.misit.abpenergy.Api.ApiClient
import com.misit.abpenergy.Api.ApiEndPoint
import com.misit.abpenergy.Rkb.Response.DataItem
import com.misit.abpenergy.Rkb.Response.RkbResponse
import com.misit.abpenergy.Rkb.RkbActivity
import com.misit.abpenergy.Utils.ConnectivityUtil
import com.misit.abpenergy.Utils.PopupUtil
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class CancelFragment : Fragment() , UserRkbListAdapter.OnItemClickListener ,RefreshDataCancel {
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
    private var call: Call<RkbResponse>?=null
    var curentPosition: Int=0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_cancel, container, false)
        setHasOptionsMenu(true)
        val argument = arguments
        username = argument?.getString(RkbActivity.Companion.USERNAME)
        dept = argument?.getString(RkbActivity.Companion.DEPARTMENT)
        sect = argument?.getString(RkbActivity.Companion.SECTON)
        level = argument?.getString(RkbActivity.Companion.LEVEL)
        rkbList = ArrayList()
        adapter = UserRkbListAdapter(activity,username,rkbList!!,sect!!)
        recyclerView = view.findViewById(R.id.recycler_totalRkb)
        rvLoading = view.findViewById(R.id.relativeLoading)
        swipeRefreshLayout = view.findViewById(R.id.pullRefresh)
        swipeRefreshLayout.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener{
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

        return view
    }

    override fun onResume() {
        if(ConnectivityUtil.isConnected(requireActivity())){
            page=1
            rkbList?.clear()
            loadRkbTotal(page.toString())
            //PopupUtil.dismissDialog()

        }
        else
        {
            PopupUtil.dismissDialog()
        }
        super.onResume()
    }

    private fun loadRkbTotal(curentPage:String) {
        swipeRefreshLayout.isRefreshing=true

        //PopupUtil.showLoading(activity!!,"Load Data","Please Wait")
        val apiEndPoint = ApiClient.getClient(activity)!!.create(ApiEndPoint::class.java)
        if(level=="administrator"){
            call = apiEndPoint.getRkbAdmin(
                username!!,
                dept!!,
                null,
                null,
                null,
                null,
                null,
                "1",
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
                null,
                "1",
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
                    recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
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
        Toasty.success(requireActivity(),"APPROVE", Toasty.LENGTH_SHORT).show()
    }
    override fun onApproveKTT(noRkb: String?) {

    }

    override fun onCancelKTT(noRkb: String?) {

    }
    override fun onCancelKabag(noRkb: String?) {
        Toasty.error(requireActivity(),"Cancel", Toasty.LENGTH_SHORT).show()
    }

    override fun onCancelUser(noRkb: String?) {

    }
    override fun refresh() {
//        page=1
//        rkbList?.clear()
//        loadRkbTotal(page.toString())
    }
}

interface RefreshDataCancel {
    fun refresh()
}
package com.misit.abpenergy.HGE.Rkb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.misit.abpenergy.HGE.Rkb.FragmentRKB.*
import com.misit.abpenergy.R
import com.misit.abpenergy.Utils.PrefsUtil
import kotlinx.android.synthetic.main.activity_rkb.*

class RkbActivity : AppCompatActivity() {
    //
    private var totalRKbFragment : TotalRKbFragment? = null
    private var approveRkbFragment : ApproveRkbFragment? = null
    private var waitingRkbFragment : WaitingRkbFragment? = null
    private var cancelRkbFragment : CancelFragment? = null
    private var closeRKbFragment : CloseRkbFragment? = null
    private var mSectionPagerAdapter : SectionPagerAdapter? = null
    private var mViewPager : ViewPager? = null
    private  var tabIndex :Int?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rkb)
        PrefsUtil.initInstance(this)
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title="Rencana Kebutuhan Barang"
        mSectionPagerAdapter= SectionPagerAdapter(supportFragmentManager)
        mViewPager = findViewById<View>(R.id.container) as ViewPager
        mViewPager?.adapter = mSectionPagerAdapter
        val tabLayout = findViewById<View>(R.id.tabs) as TabLayout
        mViewPager?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(mViewPager))
        mViewPager?.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                    //mViewPager!!.getAdapter()!!.notifyDataSetChanged()
                    mViewPager!!.currentItem = position
            }

        })
        tabLayout.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {

                //mViewPager!!.getAdapter()!!.notifyDataSetChanged()

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
            }
        })
        tabIndex=intent.getIntExtra(Tab_INDEX,0)
        mViewPager?.setCurrentItem(tabIndex!!.toInt())
        newRKB.setOnClickListener {
            var intent = Intent(this@RkbActivity, NewRkbActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
    class PlaceholderFragment : Fragment(){
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val rootView = inflater.inflate(R.layout.activity_rkb,container,false)
            return rootView
        }
        companion object{
            private const val ARG_SECTION_NUMBER = "section_number"
            fun  newInstance(sectionNumber : Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER,sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }
    inner class SectionPagerAdapter(fm: FragmentManager? ): FragmentPagerAdapter(fm!!,FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
        override fun getItem(position: Int): Fragment {
            when(position){
                0 ->    return totalRKbFragment!!
                1 ->    return approveRkbFragment!!
                2 ->    return waitingRkbFragment!!
                3 ->    return cancelRkbFragment!!
                4 ->    return closeRKbFragment!!
            }
            return PlaceholderFragment.newInstance(position + 1)
        }
        override fun getCount():Int{
            return 5
        }
        override fun getPageTitle(position: Int): CharSequence? {
            when(position){
                0->return "Total"
                1->return "Approve"
                2->return "Waiting"
                3->return "Cancel"
                4->return "Close"
            }
            return super.getPageTitle(position)
        }
        override fun getItemPosition(obj: Any): Int {
//            if (obj is RefreshDataTotal) {
//                obj.refresh()
//            }else if (obj is RefreshDataApprove) {
//                obj.refresh()
//            }else if(obj is RefreshDataWaiting){
//                obj.refresh()
//            }else if(obj is RefreshDataCancel){
//                obj.refresh()
//            }else if(obj is RefreshDataClose){
//                obj.refresh()
//            }
            return super.getItemPosition(obj)
        }
        init {
            val username = intent.getStringExtra(USERNAME)
            val dept = intent.getStringExtra(DEPARTMENT)
            val sect = intent.getStringExtra(SECTON)
            val level = intent.getStringExtra(LEVEL)
            val tipe = intent.getStringExtra(TIPE)
            val no_rkb = intent.getStringExtra(NO_RKB)

            val argument = Bundle()
            argument.putString(USERNAME,username)
            argument.putString(DEPARTMENT,dept)
            argument.putString(SECTON,sect)
            argument.putString(LEVEL,level)
            argument.putString(TIPE,tipe)
            argument.putString(NO_RKB,no_rkb)

            totalRKbFragment = TotalRKbFragment()
            totalRKbFragment?.arguments = argument

            approveRkbFragment = ApproveRkbFragment()
            approveRkbFragment?.arguments=argument

            waitingRkbFragment = WaitingRkbFragment()
            waitingRkbFragment?.arguments=argument

            cancelRkbFragment = CancelFragment()
            cancelRkbFragment?.arguments=argument

            closeRKbFragment = CloseRkbFragment()
            closeRKbFragment?.arguments=argument
        }
    }

    companion object{
        var Tab_INDEX = "tab_index"
        var USERNAME = "username"
        var DEPARTMENT="department"
        var SECTON="section"
        var LEVEL="level"
        var TIPE="TIPE"
        var NO_RKB="NO_RKB"
    }
}

package com.misit.abpenergy.Main.IntroFragmentimport android.os.Bundleimport androidx.fragment.app.Fragmentimport android.view.LayoutInflaterimport android.view.Viewimport android.view.ViewGroupimport com.misit.abpenergy.Rimport kotlinx.android.synthetic.main.fragment_slider.*class SliderFragment : Fragment() {    override fun onCreateView(        inflater: LayoutInflater, container: ViewGroup?,        savedInstanceState: Bundle?    ): View? {        return inflater.inflate(R.layout.fragment_slider, container, false)    }    fun frgamentSetText(titleText:String?,content:String,ask:String){        fragment_title.text = titleText.toString()//        fragment_content.text = content//        fragment_ask.text = ask    }}
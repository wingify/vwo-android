package com.vwo.sampleapp.fragments


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.vwo.sampleapp.R
import com.vwo.sampleapp.interfaces.ChangeFragment
import com.vwo.sampleapp.interfaces.NavigationToggleListener
import kotlinx.android.synthetic.main.toolbar_common.*
import kotlinx.android.synthetic.main.toolbar_common.view.*

/**
 * Created by aman on 08/08/17.
 */

class FragmentHousingMain : Fragment(), ChangeFragment {

    private var listener: NavigationToggleListener? = null

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(VARIATION_LOGIN_TYPE_NORMAL)
    internal annotation class LoginType

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is NavigationToggleListener) {
            listener = context
        } else {
            Log.e(LOG_TAG, "Interface NavigationToggleListener not implemented in Activity")
        }
    }

    @Nullable
    override fun onCreateView(@NonNull inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_housing_main, container, false)

        val navigation = view.campaign_navigation
        val refresh = view.refresh_campaign
        val toolbarTitle = view.toolbar_title

        navigation.setOnClickListener {
            if (listener != null) {
                listener!!.onToggle()
            }
        }

        refresh.visibility = View.GONE

        toolbarTitle.setText(R.string.title_on_boarding)
        loadDefaultFragments()
        return view
    }

    private fun loadDefaultFragments() {
        loadFragment(null, VARIATION_LOGIN_TYPE_NORMAL, null)
    }

    /**
     * ** This function is used to load a particular [android.app.Fragment] from the
     * controlling [Activity] or [android.app.Fragment] **
     *
     * @param bundle     is the data to be passed to [android.app.Fragment]
     * @param fragmentId is the id that identifies, which [android.app.Fragment] is to be loaded
     * @param tag        is the tag that is attached to [android.app.Fragment] which is to be loaded
     */
    override fun loadFragment(@Nullable bundle: Bundle?, fragmentId: Int, @Nullable tag: String?) {
        childFragmentManager.beginTransaction().replace(R.id.onboarding_variation_container,
                FragmentHousing.getInstance(fragmentId)).commit()
    }

    companion object {
        private val LOG_TAG = FragmentHousingMain::class.java.simpleName

        const val VARIATION_LOGIN_TYPE_NORMAL = 2
    }
}

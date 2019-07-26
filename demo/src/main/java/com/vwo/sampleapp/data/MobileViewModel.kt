package com.vwo.sampleapp.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vwo.sampleapp.R
import com.vwo.sampleapp.models.Mobile

/**
 * Created by aman on Fri 13/07/18 13:38.
 */
class MobileViewModel : ViewModel() {
    val mobiles = MutableLiveData<List<Mobile>>()
    private var mobilesList: List<Mobile>

    init {
        val apple = Mobile(1, "iPhone 6 (16GB, Black)", 399, "$", true,
                true, R.drawable.iphone, "Apple", "Also Available in Space Grey and Rose Gold", 4)
        val samsung = Mobile(2, "Samsung Galaxy S8 (64GB, Midnight Black)", 799, "$",
                true, false, R.drawable.s8, "Samsung", "Also available in Maple Gold and Orchid Grey", 4)
        val pixel = Mobile(3, "Google Pixel (32GB, Very Silver)", 699, "$", false,
                false, R.drawable.pixel, "Google", "Also Available in Quite black", 5)
        val ZTE = Mobile(4, "ZTE Max XL (16GB)", 695, "$", true, false,
                R.drawable.zte, "ZTE", "Available in 16GB", 3)
        val galaxy = Mobile(5, "Galaxy J250(16GB)", 400, "$", true, false,
                R.drawable.galaxy_j250, "Samsung", "Samsung Galaxy J250 16GB", 4)
        val honor = Mobile(6, "Honor 7X (Blue, 4GB RAM + 32GB Memory)", 159, "$", false, false,
                R.drawable.honor_7x, "Huawei", "Honor 7X 4GB RAM and 32GB ROM", 3)
        val miMix = Mobile(7, "Mi Max 2 (Black, 32GB)", 169, "$", true, false,
                R.drawable.mi_mix_2, "Mi", "Mi Max 2 Black, 4GB RAM and 32GB ROM", 4)
        val redmi = Mobile(8, "Redmi Y2 (Dark Grey, 32GB)", 129, "$", true, true,
                R.drawable.redmi_y2, "Mi", "Redmi Y2 Dark Grey, 3GB RAM and 32GB ROM", 5)
        val onePlus6 = Mobile(8, "OnePlus 6 (Mirror Black 6GB RAM + 64GB Memory)", 459, "$", true, false,
                R.drawable.one_plus_6, "OnePlus", "OnePlus 6 Mirror Black 6GB RAM and 64GB Memory", 5)

        mobilesList = listOf(apple, samsung, pixel, ZTE, galaxy, honor, miMix, redmi, onePlus6)
        sortByPrice()
    }

    @JvmOverloads
    fun sortByPrice(asc: Boolean = true) {
        if (asc) {
            mobiles.value = mobilesList.sortedBy { it.price }.toList()
        } else {
            mobiles.value = mobilesList.sortedByDescending { it.price }
        }
    }

    @JvmOverloads
    fun sortByName(asc: Boolean = true) {
        if (asc) {
            mobiles.value = mobilesList.sortedBy { it.name.toLowerCase() }
        } else {
            mobiles.value = mobilesList.sortedByDescending { it.name.toLowerCase() }
        }
    }

    @JvmOverloads
    fun sortById(asc: Boolean = true) {
        if (asc) {
            mobiles.value = mobilesList.sortedBy { it.id }.toList()
        } else {
            mobiles.value = mobilesList.sortedByDescending { it.id }.toList()
        }
    }

    fun sortRandom() {
        mobiles.value = mobilesList.shuffled()
    }
}
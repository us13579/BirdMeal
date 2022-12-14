package com.ssafy.birdmeal.binding

import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.ssafy.birdmeal.R
import com.ssafy.birdmeal.di.ApplicationClass.Companion.PACKAGE_NAME
import com.ssafy.birdmeal.utils.TAG

object ViewBindingAdapter {

    @BindingAdapter("categoryImg")
    @JvmStatic
    fun AppCompatImageView.setCategoryImg(imgUrl: String) {
        val url = "drawable/$imgUrl"
        val imageResource = resources.getIdentifier(url, null, PACKAGE_NAME)
        val image = resources.getDrawable(imageResource)

        this.setImageDrawable(image)
    }

    @BindingAdapter("productPrice")
    @JvmStatic
    fun TextView.setProductPrice(price: Int) {
        var t = String.format("%,2d", price)
        text = "$t ELN"
    }

    @BindingAdapter("cartThumbnail")
    @JvmStatic
    fun ImageView.setCartThumbnail(url: String?) {
        if (url != null) {
            Glide.with(this.context)
                .load("$url")
                .override(R.dimen.cartImg * 2, R.dimen.cartImg * 2)
                .placeholder(com.ssafy.birdmeal.R.drawable.meal)
                .into(this)
        }
    }

    @BindingAdapter("productThumbnail")
    @JvmStatic
    fun ImageView.setProductThumbnail(url: String?) {
        if (url != null) {
            Glide.with(this.context)
                .load("$url")
                .override(R.dimen.thumbnailImgWidth * 2, R.dimen.thumbnailImgHeight * 2)
                .placeholder(com.ssafy.birdmeal.R.drawable.meal)
                .into(this)
        }
    }

    @BindingAdapter("productDescription")
    @JvmStatic
    fun ImageView.setProductDescription(url: String?) {
        if (url != null) {
            Glide.with(this.context)
                .load("$url")
                .override(R.dimen.thumbnailImgWidth * 2, R.dimen.descriptionImgHeight * 2)
                .placeholder(R.drawable.meal)
                .into(this)
        }
    }


    @BindingAdapter("totalAmount", "userELN")
    @JvmStatic
    fun TextView.setRemainAmount(totalAmount: Int, userELN: Int) {
        var value = (userELN - totalAmount)
        this.text = String.format("%,2d", value) + " ELN"
    }

    @BindingAdapter("orderName", "orderSize")
    @JvmStatic
    fun TextView.setOrderName(name: String, size: Int) {
        var text = "$name"
        if (size > 2) {
            text += " ??? ${size - 1}???"
        }
        this.text = text
    }

    @BindingAdapter("orderHistoryName", "orderHistorySize")
    @JvmStatic
    fun TextView.setOrderHistoryName(name: String, size: Int) {
        var text = "$name"
        if (size > 0) {
            text += " ??? ${size}???"
        }
        this.text = text
    }

    // ????????????, ???????????? ????????? ??????
    @BindingAdapter("donationImg")
    @JvmStatic
    fun ImageView.setDonationImg(donationType: Boolean?) {
        if (donationType != null) {
            when (donationType) {
                true -> {
                    Glide.with(this.context)
                        .load(com.ssafy.birdmeal.R.drawable.ic_donation1)
                        .placeholder(com.ssafy.birdmeal.R.drawable.meal)
                        .into(this)
                }
                false -> {
                    Glide.with(this.context)
                        .load(com.ssafy.birdmeal.R.drawable.ic_donation2)
                        .placeholder(com.ssafy.birdmeal.R.drawable.meal)
                        .into(this)
                }
            }
        }
    }

    // ???????????? New ????????? ?????? or ??????
    @BindingAdapter("cartNewIcon")
    @JvmStatic
    fun ImageView.setNewIcon(productCnt: Int) {
        if (productCnt > 0) {
            this.visibility = View.VISIBLE
        } else {
            this.visibility = View.INVISIBLE
        }
    }

    // ???????????? ??????????????? ????????? ??????
    @BindingAdapter("textEmpty")
    @JvmStatic
    fun TextView.setTextEmpty(productCnt: Int) {
        if (productCnt > 0) {
            this.visibility = View.GONE
        } else {
            this.visibility = View.VISIBLE
        }
    }

    // NFT ?????????
    @BindingAdapter("nftImg")
    @JvmStatic
    fun ImageView.setNftImg(url: String?) {
        if (url != null) {
            Glide.with(this.context)
                .load(url)
                .placeholder(com.ssafy.birdmeal.R.drawable.loading)
                .into(this)
        }
    }

    // NFT ?????? ????????? ???
    @BindingAdapter("setGetNftButton")
    @JvmStatic
    fun ImageView.setGetNftButton(isMint: Boolean?) {
        if (isMint != null) {
            if (isMint) {
                this.background = resources.getDrawable(R.drawable.btn_get_nft)
            } else {
                this.background = resources.getDrawable(R.drawable.btn_get_nft_false)
            }
        }
    }

    // ????????? ??? ???????????? ?????????
    @BindingAdapter("myDonationAmount")
    @JvmStatic
    fun TextView.myDonationAmount(productCnt: Int) {
        if (productCnt > 0) {
            this.visibility = View.GONE
        } else {
            this.visibility = View.VISIBLE
        }
    }

    // ?????? ?????? ?????? ??? (??????)
    @BindingAdapter("setFillUpButton")
    @JvmStatic
    fun ImageView.setFillUpButton(userChargeState: Boolean?) {
        Log.d(TAG, "setFillUpButton: $userChargeState")
        if (userChargeState != null) {
            if (userChargeState) {
                this.setImageResource(R.drawable.btn_charge)
            } else {
                this.setImageResource(R.drawable.btn_charged)
            }
        }
    }

    // ?????? ?????? NULL ??????
    @BindingAdapter("myELN")
    @JvmStatic
    fun TextView.myELN(userELN: String?) {
        if (userELN == null) {
            this.text = "0 ELN"
        } else {
            this.text = "$userELN ELN"
        }
    }

    // ?????? ?????? NULL ??????
    @BindingAdapter("myNFT")
    @JvmStatic
    fun TextView.myNFT(count: String?) {
        if (count == null) {
            this.text = "?????? NFT : 0???"
        } else {
            this.text = "?????? NFT : ${count}???"
        }
    }
}
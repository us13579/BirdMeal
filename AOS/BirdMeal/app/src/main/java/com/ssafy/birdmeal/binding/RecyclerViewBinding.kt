package com.ssafy.birdmeal.binding

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.birdmeal.base.BaseResponse
import com.ssafy.birdmeal.model.dto.ProductDto
import com.ssafy.birdmeal.utils.TAG
import com.ssafy.birdmeal.utils.Result
import com.ssafy.birdmeal.view.market.product.ProductListAdapter

object RecyclerViewBinding {

    // 페이징 하지 않는 리사이클러뷰 데이터 바인딩
    @JvmStatic
    @BindingAdapter("submitList")
    fun bindSubmitList(view: RecyclerView, result: Result<*>){
        Log.d(TAG, "bindSubmitList: bindSubmitList : $result")
        if(result is Result.Success){
            if(result.data is BaseResponse<*>){
                when(view.adapter){
                    is ProductListAdapter -> {
                        (view.adapter as ListAdapter<Any, *>).submitList(result.data.data as List<ProductDto>)
                    }
                }
                // 같은 형태로 추가하면 됨
            }
        } else if(result is Result.Empty){
            (view.adapter as ListAdapter<Any, *>).submitList(emptyList())
        }
    }
}
package com.ssafy.birdmeal.view.donation

import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.birdmeal.di.ApplicationClass.Companion.elenaContract
import com.ssafy.birdmeal.di.ApplicationClass.Companion.fundingContract
import com.ssafy.birdmeal.model.dto.DonationHistoryDto
import com.ssafy.birdmeal.model.response.ChildHistoryResponse
import com.ssafy.birdmeal.repository.DonationRepository
import com.ssafy.birdmeal.repository.NftRepository
import com.ssafy.birdmeal.utils.*
import com.ssafy.birdmeal.utils.Converter.DecimalConverter.fromEtherToWei
import com.ssafy.birdmeal.utils.Converter.DecimalConverter.fromWeiToEther
import com.ssafy.birdmeal.utils.Converter.DecimalConverter.priceConvert
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DonationViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val donationRepository: DonationRepository,
    private val nftRepository: NftRepository
) : ViewModel() {

    private val _loadingMsgEvent = SingleLiveEvent<String>()
    val loadingMsgEvent get() = _loadingMsgEvent

    private val _contractErrMsgEvent = SingleLiveEvent<String>()
    val contractErrMsgEvent get() = _contractErrMsgEvent

    private val _errMsgEvent = SingleLiveEvent<String>()
    val errMsgEvent get() = _errMsgEvent

    private val _donationMsgEvent = SingleLiveEvent<String>()
    val donationMsgEvent get() = _donationMsgEvent

    private val _donateMsgEvent = SingleLiveEvent<String>()
    val donateMsgEvent get() = _donateMsgEvent

    private val _donationPrice = SingleLiveEvent<String>()
    val donationPrice get() = _donationPrice

    private val _donationAmount = SingleLiveEvent<Long>()
    val donationAmount get() = _donationAmount

    private val _animationMsgEvent = SingleLiveEvent<String>()
    val animationMsgEvent get() = _animationMsgEvent

    private val _donationAllHistoryList = SingleLiveEvent<List<DonationHistoryDto>>()
    val donationAllHistoryList get() = _donationAllHistoryList

    private val _donationMyHistoryList = SingleLiveEvent<List<DonationHistoryDto>>()
    val donationMyHistoryList get() = _donationMyHistoryList

    @RequiresApi(Build.VERSION_CODES.O)
    val myDonationAmount =
        Transformations.map(_donationMyHistoryList) { list ->
            var sum = 0L;
            val today = LocalDate.now()
            Log.d(TAG, "${today.year}: ")
            Log.d(TAG, "${today.monthValue}: ")
            list.forEach {
                if (it.donationDate.substring(0, 4) == today.year.toString() &&
                    it.donationDate.substring(4, 6) == today.monthValue.toString()
                ) {
                    sum += it.donationPrice
                }
            }
            sum.priceConvert()
        }

    private val _orderChildHistoryList = SingleLiveEvent<List<ChildHistoryResponse>>()
    val orderChildHistoryList get() = _orderChildHistoryList

    val childUseAmount = Transformations.map(_orderChildHistoryList) { list ->
        list.sumOf { it.productPrice * it.orderQuantity }
    }

    // ??? ????????? ???????????? (????????????)
    fun getDonationAmount() = viewModelScope.launch(IO) {
        try {
            val result = fundingContract.currentBalance().sendAsync().get()
            val text = result.fromWeiToEther().priceConvert() + " ELN"

            _donationMsgEvent.postValue(text)
            Log.d(TAG, "fundingContract.currentBalance: $result")
        } catch (e: Exception) {
            _contractErrMsgEvent.postValue(ERR_GET_DONATION_AMOUNT)
            Log.d(TAG, "getDonationAmount err: $e")
        }
    }

    // ?????? ???????????? ??????
    fun checkDonate(userBalance: Long, donationType: Boolean) {
        Log.d(TAG, "?????? ?????????: ${donationPrice.value}")
        Log.d(TAG, "?????? ??????: $userBalance")

        if (donationPrice.value.isNullOrBlank()) {
            _errMsgEvent.postValue("?????? ????????? ??????????????????")
            _loadingMsgEvent.postValue(DONATE_EMPTY)
            return
        }

        val amount = donationPrice.value?.replace(",", "")?.toLong()!!
        _donationAmount.postValue(amount)

        if (amount > userBalance) {
            _errMsgEvent.postValue("?????? ????????? ?????? ???????????? ????????????")
            _loadingMsgEvent.postValue(DONATE_BALANCE)
            return
        }

        doDonate(amount, donationType)
    }

    // ?????? ?????? ?????? ??????
    fun setDonateCompleted() {
        _loadingMsgEvent.postValue(DONATE_COMPLETED)
    }

    // ???????????? (????????????)
    fun doDonate(amount: Long, donationType: Boolean) = viewModelScope.launch(IO) {
        _loadingMsgEvent.postValue(DONATE_POSSIBLE)

        try {
            elenaContract.approve(CA_FUNDING, amount.fromEtherToWei().toBigInteger()).sendAsync()
                .get()
            val result = fundingContract.funding(BigInteger.valueOf(amount)).sendAsync().get()
            Log.d(TAG, "fundingContract.funding: $result")

            insertDonationHistory(amount, donationType)

            _loadingMsgEvent.postValue(DONATE_COMPLETED)
        } catch (e: Exception) {
            _loadingMsgEvent.postValue(DONATE_ERR)
            _contractErrMsgEvent.postValue(ERR_DO_DONATE)
            Log.d(TAG, "doDonate err: $e")
        }
    }

    // chip ????????? ?????? ?????? ??????
    fun plusDonationPrice(num: Int) {
        var current: Long? = null
        if (!_donationPrice.value.isNullOrBlank()) {
            current = _donationPrice.value?.replace(",", "")?.toLong()
        }
        val amount = (20000L * num - 10000L)
        if (current == null) {
            _donationPrice.postValue(amount.toString())
        } else {
            current = current?.plus(amount)
            _donationPrice.postValue(current.toString())
        }
    }

    // ?????? ???????????? ????????????
    fun getAllDonationHistory() = viewModelScope.launch(IO) {
        donationRepository.getAllDonationHistory().collectLatest {
            Log.d(TAG, "getAllDonationHistory response: $it")

            if (it is Result.Success) {
                Log.d(TAG, "getAllDonationHistory data: ${it.data}")

                // ???????????? ????????? ??????
                if (it.data.success) {
                    _donationAllHistoryList.postValue(it.data.data.reversed())
                    _donateMsgEvent.postValue("?????? ???????????? ???????????? ??????")
                }
            } else if (it is Result.Error) {
                _errMsgEvent.postValue("?????? ?????? ??????")
            }
        }
    }

    // ??? ???????????? ????????????
    fun getMyDonationHistory() = viewModelScope.launch(IO) {

        val userSeq = sharedPreferences.getInt(USER_SEQ, -1)
        Log.d(TAG, "getMyDonationHistory userSeq: $userSeq")

        donationRepository.getMyDonationHistory(userSeq).collectLatest {
            Log.d(TAG, "getMyDonationHistory response: $it")

            if (it is Result.Success) {
                Log.d(TAG, "getMyDonationHistory data: ${it.data}")

                // ???????????? ????????? ??????
                if (it.data.success) {
                    _donationMyHistoryList.postValue(it.data.data.reversed())
                    _donateMsgEvent.postValue("?????? ???????????? ???????????? ??????")
                }
            } else if (it is Result.Error) {
                _errMsgEvent.postValue("?????? ?????? ??????")
            }
        }
    }

    // ??? ???????????? ????????????
    fun insertDonationHistory(donationPrice: Long, donationType: Boolean) =
        viewModelScope.launch(IO) {

            val userSeq = sharedPreferences.getInt(USER_SEQ, -1)
            Log.d(TAG, "insertDonationHistory userSeq: $userSeq")

            val donationHistory = DonationHistoryDto(
                userSeq = userSeq,
                donationPrice = donationPrice,
                donationType = donationType
            )

            donationRepository.insertDonationHistory(donationHistory).collectLatest {
                Log.d(TAG, "insertDonationHistory response: $it")

                if (it is Result.Success) {
                    Log.d(TAG, "insertDonationHistory data: ${it.data}")

                    // ???????????? ????????? ??????
                    if (it.data.success) {
                        _donateMsgEvent.postValue("????????? ?????????????????????")
                    }
                } else if (it is Result.Error) {
                    _errMsgEvent.postValue("?????? ?????? ??????")
                }
            }
        }

    // ????????? ????????? ?????? ?????? ????????????
    fun getChildOrderHistory() = viewModelScope.launch(IO) {
        donationRepository.getChildOrderHistory().collectLatest {
            Log.d(TAG, "getChildOrderHistory response: $it")

            if (it is Result.Success) {
                Log.d(TAG, "getChildOrderHistory data: ${it.data}")

                // ???????????? ????????? ??????
                if (it.data.success) {
                    _orderChildHistoryList.postValue(it.data.data)
                    _donateMsgEvent.postValue("????????? ????????? ?????? ?????? ???????????? ??????")
                }
            } else if (it is Result.Error) {
                _errMsgEvent.postValue("?????? ?????? ??????")
            }
        }
    }

    // ????????? ??? ????????? ????????????
    fun getChildAmount() = viewModelScope.launch(IO) {
        val result = fundingContract.totalWithdrawal.sendAsync().get()
        Log.d(TAG, "getChildAmount: $result")
    }

    // ?????? ?????? ??????????????? ??????
    fun setAnimation() {
        _animationMsgEvent.postValue("????????????")
    }
}
package com.ssafy.birdmeal.di

import android.app.Application
import android.content.Context
import android.util.Log
import com.dttmm.web3test.wrapper.Elena
import com.dttmm.web3test.wrapper.Exchange
import com.dttmm.web3test.wrapper.Trade
import com.dttmm.web3test.wrapper.TradeManager
import com.ssafy.birdmeal.model.entity.CartEntity
import com.ssafy.birdmeal.utils.*
import com.ssafy.birdmeal.wrapper.Funding
import dagger.hilt.android.HiltAndroidApp
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.tx.FastRawTransactionManager
import org.web3j.tx.gas.StaticGasProvider
import org.web3j.tx.response.PollingTransactionReceiptProcessor
import javax.inject.Inject

@HiltAndroidApp
class ApplicationClass : Application() {

    @Inject
    lateinit var gasProvider: StaticGasProvider

    @Inject
    lateinit var web3j: Web3j

    @Inject
    lateinit var pollingProcessor: PollingTransactionReceiptProcessor

    companion object {
        var appContext: Context? = null

        lateinit var manager: FastRawTransactionManager
        private var contractList : MutableList<Trade> = mutableListOf()
        // 컨트랙트 객체들
        lateinit var fundingContract: Funding
        lateinit var elenaContract: Elena
        lateinit var exchangeContract: Exchange
        lateinit var tradeManagerContract: TradeManager
        lateinit var tradeContract: Trade
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }

    fun getTradeContract(product: List<CartEntity>): MutableList<Trade> {
        product.map {
            Log.d(TAG, "getTradeContract: ${it.productName}, ${it.productCount}")
            tradeContract = Trade.load(it.productCa, web3j, manager, gasProvider)
            contractList.add(tradeContract)
        }
        return contractList
    }

    fun initContract(credentials: Credentials) {
        manager = FastRawTransactionManager(
            web3j, credentials, BOLCKCHAIN_NETWORK_CHAINID,
            pollingProcessor
        )

        fundingContract = Funding.load(CA_FUNDING, web3j, manager, gasProvider)
        elenaContract = Elena.load(CA_ELENA, web3j, manager, gasProvider)
        exchangeContract = Exchange.load(CA_EXCHANGE, web3j, manager, gasProvider)
        tradeManagerContract = TradeManager.load(CA_TRADE_MANAGER, web3j, manager, gasProvider)
    }
}
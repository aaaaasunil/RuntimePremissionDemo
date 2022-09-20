package com.example.runtimepremission.callBack

interface PremissionCallBack {

    fun onPremissionAllow()
    fun onPremissionDeny(eventKey: String)
    fun onPremissionNeverAsk()
    fun onPremissionRequestAgain()
}
package com.binbard.geu.one.models

data class FeeDetails(
    val headdata: List<FeeHead>,
    val headdatahostel: List<FeeHead>,
    val totaldue: String,
    val totalreceive: String,
    val totalbalance: String,
    val adjust: String,
    val excessfee: String
)

data class FeeHead(
    val YS: String,
    val FeeHead: String,
    val DueAmount: String,
    val RefundAmount: String,
    val ReceivedAmount: String,
    val BalanceAmount: String
)
package com.example.railwayconcession.model

data class userConccessionDetails(
    var concession_class : String? = null,
    var voucherNo : String? = null,
    var concessionPeriod : String? = null,
    var appliedDate : String? = null,
    var source : String? = null,
    var destination : String? = null
)

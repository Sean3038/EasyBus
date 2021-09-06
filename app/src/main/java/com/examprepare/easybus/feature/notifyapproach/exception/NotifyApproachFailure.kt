package com.examprepare.easybus.feature.notifyapproach.exception

import com.examprepare.easybus.core.exception.Failure

abstract class NotifyApproachFailure : Failure.FeatureFailure(){
    /**
     * The Failure throw when response can't receive estimate time
     * */
    object NoEstimateArrivalFailure : NotifyApproachFailure()
}


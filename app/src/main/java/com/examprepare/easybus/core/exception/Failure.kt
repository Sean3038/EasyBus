package com.examprepare.easybus.core.exception

sealed class Failure {
    object None : Failure()
    object NetworkConnection : Failure()
    object ServerError : Failure()

    /** * Extend this class for feature specific failures.*/
    abstract class FeatureFailure : Failure()
}
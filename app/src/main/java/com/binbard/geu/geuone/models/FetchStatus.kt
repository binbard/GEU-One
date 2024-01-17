package com.binbard.geu.geuone.models

enum class FetchStatus {
    NA,
    SUCCESS,
    FAILED,
    NO_NEW_DATA_FOUND,
    NEW_DATA_FOUND,
    DONE,
}

enum class StatusCode{
    NA,
    SUCCESS,
    FAILED,
    DONE,
}

enum class LoginStatus {
    LOGIN_SUCCESS,
    LOGIN_FAILED,
    LOGOUT,
    PREV_LOGGED_IN,
    PREV_LOGGED_OUT,
    LOGGED_IN,
    NOT_LOGGED_IN,
    UNKNOWN,
}
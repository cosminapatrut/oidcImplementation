package com.orange.volunteers.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.util.Log
import io.reactivex.Observable
import io.reactivex.disposables.Disposables

object InternetConnectionManager {

    fun isOnline(context: Context): Boolean {
        val connectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo?.isConnected ?: false
    }

    fun monitorInternetConnection(context: Context): Observable<Boolean> {
        val action = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        return monitorInternetConnection(context, IntentFilter(action))
    }

    private fun monitorInternetConnection(context: Context, intentFilter: IntentFilter): Observable<Boolean> {

        return Observable.create {
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val isInternetOn = isOnline(context)
                    it.onNext(isInternetOn)
                }
            }

            context.registerReceiver(receiver, intentFilter)

            it.setDisposable(Disposables.fromRunnable {
                try {
                    context.unregisterReceiver(receiver)
                } catch (e: Exception) {
                    Log.e("Internet Manager:", "${e.message}")
                }
            })
        }
    }

}

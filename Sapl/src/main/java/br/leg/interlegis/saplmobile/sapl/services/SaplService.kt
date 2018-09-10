package br.leg.interlegis.saplmobile.sapl.services

import android.app.Service
import android.arch.lifecycle.LiveData
import android.content.Intent
import android.os.*
import br.leg.interlegis.saplmobile.sapl.SaplApplication
import br.leg.interlegis.saplmobile.sapl.db.AppDataBase
import br.leg.interlegis.saplmobile.sapl.db.entities.ChaveValor
import br.leg.interlegis.saplmobile.sapl.json.JsonApi
import br.leg.interlegis.saplmobile.sapl.support.Log
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

import br.leg.interlegis.saplmobile.R
import br.leg.interlegis.saplmobile.sapl.db.daos.DaoChaveValor

class SaplService : Service() {

    private var mServiceLooper: Looper? = null
    private var mServiceHandler: ServiceHandler? = null

    private var interval_update : Long = 5000

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private inner class ServiceHandler(looper: Looper) : Handler(looper) {
        private// get date time in custom format
        val dateTime: String
            get() {
                val sdf = SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss]")
                return sdf.format(Date())
            }

        override fun handleMessage(msg: Message) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.

            try {
                toast("teste")

            } catch (e: InterruptedException) {
                // Restore interrupt status.
                Thread.currentThread().interrupt()
            }
        }

    }

    override fun onCreate() {

        instance = this

        val thread = HandlerThread("SaplThreadService",
                Process.THREAD_PRIORITY_BACKGROUND)
        thread.start()

        mServiceLooper = thread.getLooper()
        mServiceHandler = ServiceHandler(mServiceLooper!!)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        toast("Serviço iniciado!")

        mServiceHandler!!.post(object : Runnable {

            private// get date time in custom format
            val dateTime: String
                get() {
                    val sdf = SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss]")
                    return sdf.format(Date())
                }

            override fun run() {
                //toast(dateTime)
                this@SaplService.execute()
                mServiceHandler!!.postDelayed(this, this@SaplService.interval_update)
            }
        })

        return START_STICKY
    }

    private fun isUpdated(): Boolean {
        try {
            val json = JsonApi(this@SaplService)
            val lgrt = json.get_last_global_refresh_time()

            var cv = DaoChaveValor?.get_or_create(this@SaplService,"last_global_refresh_time", lgrt)

            if (cv.valor == lgrt) {
                return true
            }
            else {
                cv.valor = lgrt
                val list = ArrayList<ChaveValor>()
                list.add(cv)
                AppDataBase.getInstance(this@SaplService).DaoChaveValor().insertAll(list)
                return false
            }

        }
        catch (e: Exception) {
            toast("Erro de Comunicação com o Servidor na Internet")
            return false
        }

    }

    private fun execute() {
        if (!isUpdated()) {
            toast("Servidor foi atualizado!")
        }
    }

    companion object {

        var instance: SaplService? = null
            private set

        fun sendMessage(message: Message) {
            instance!!.mServiceHandler!!.sendMessage(message)
        }
    }

}

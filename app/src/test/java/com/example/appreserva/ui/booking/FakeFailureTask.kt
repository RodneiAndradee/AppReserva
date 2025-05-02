package com.example.appreserva.util

import android.app.Activity
import com.google.android.gms.tasks.*
import java.lang.Exception
import java.util.concurrent.Executor

class FakeFailureTask : Task<Void>() {

    override fun addOnCompleteListener(listener: OnCompleteListener<Void>): Task<Void> {
        listener.onComplete(this)
        return this
    }

    override fun addOnFailureListener(listener: OnFailureListener): Task<Void> {
        listener.onFailure(Exception("Erro ao fazer a reserva."))
        return this
    }

    override fun addOnFailureListener(activity: Activity, listener: OnFailureListener): Task<Void> {
        listener.onFailure(Exception("Erro ao fazer a reserva."))
        return this
    }

    override fun addOnFailureListener(executor: Executor, listener: OnFailureListener): Task<Void> {
        listener.onFailure(Exception("Erro ao fazer a reserva."))
        return this
    }

    override fun addOnSuccessListener(listener: OnSuccessListener<in Void>): Task<Void> = this
    override fun addOnSuccessListener(activity: Activity, listener: OnSuccessListener<in Void>): Task<Void> = this
    override fun addOnSuccessListener(executor: Executor, listener: OnSuccessListener<in Void>): Task<Void> = this

    override fun isComplete(): Boolean = true
    override fun isSuccessful(): Boolean = false
    override fun isCanceled(): Boolean = false

    override fun getException(): Exception = Exception("Erro ao fazer a reserva.")
    override fun getResult(): Void? = null
    override fun <X : Throwable?> getResult(exceptionType: Class<X>): Void? = null
}

package com.example.appreserva.util

import android.app.Activity
import com.google.android.gms.tasks.*
import com.google.firebase.auth.AuthResult
import java.util.concurrent.Executor

class FakeFailureAuthTask(private val exception: Exception) : Task<AuthResult>() {

    override fun addOnCompleteListener(listener: OnCompleteListener<AuthResult>): Task<AuthResult> {
        listener.onComplete(this)
        return this
    }

    override fun addOnFailureListener(listener: OnFailureListener): Task<AuthResult> {
        listener.onFailure(exception)
        return this
    }

    override fun addOnFailureListener(activity: Activity, listener: OnFailureListener): Task<AuthResult> {
        listener.onFailure(exception)
        return this
    }

    override fun addOnFailureListener(executor: Executor, listener: OnFailureListener): Task<AuthResult> {
        listener.onFailure(exception)
        return this
    }

    override fun addOnSuccessListener(listener: OnSuccessListener<in AuthResult>): Task<AuthResult> {
        // Falha, não chama sucesso
        return this
    }

    override fun addOnSuccessListener(activity: Activity, listener: OnSuccessListener<in AuthResult>): Task<AuthResult> {
        // Falha, não chama sucesso
        return this
    }

    override fun addOnSuccessListener(executor: Executor, listener: OnSuccessListener<in AuthResult>): Task<AuthResult> {
        // Falha, não chama sucesso
        return this
    }

    override fun isComplete(): Boolean = true
    override fun isSuccessful(): Boolean = false
    override fun isCanceled(): Boolean = false
    override fun getResult(): AuthResult? = null
    override fun <X : Throwable?> getResult(exceptionType: Class<X>): AuthResult? = null
    override fun getException(): Exception? = exception
}

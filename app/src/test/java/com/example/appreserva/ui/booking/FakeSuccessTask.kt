package com.example.appreserva.util

import android.app.Activity
import com.google.android.gms.tasks.*
import java.util.concurrent.Executor

class FakeSuccessTask : Task<Void>() {
    override fun addOnCompleteListener(listener: OnCompleteListener<Void>): Task<Void> {
        listener.onComplete(this)
        return this
    }

    override fun addOnCompleteListener(activity: Activity, listener: OnCompleteListener<Void>): Task<Void> {
        listener.onComplete(this)
        return this
    }

    override fun addOnCompleteListener(executor: Executor, listener: OnCompleteListener<Void>): Task<Void> {
        executor.execute { listener.onComplete(this) }
        return this
    }

    override fun addOnFailureListener(listener: OnFailureListener): Task<Void> {
        return this
    }

    override fun addOnFailureListener(activity: Activity, listener: OnFailureListener): Task<Void> {
        return this
    }

    override fun addOnFailureListener(executor: Executor, listener: OnFailureListener): Task<Void> {
        return this
    }

    override fun addOnSuccessListener(listener: OnSuccessListener<in Void>): Task<Void> {
        listener.onSuccess(null)
        return this
    }

    override fun addOnSuccessListener(activity: Activity, listener: OnSuccessListener<in Void>): Task<Void> {
        listener.onSuccess(null)
        return this
    }

    override fun addOnSuccessListener(executor: Executor, listener: OnSuccessListener<in Void>): Task<Void> {
        executor.execute { listener.onSuccess(null) }
        return this
    }

    override fun isComplete(): Boolean = true

    override fun isSuccessful(): Boolean = true

    override fun isCanceled(): Boolean = false

    override fun getResult(): Void? = null

    override fun <X : Throwable?> getResult(exceptionType: Class<X>): Void {
        throw UnsupportedOperationException()
    }

    override fun getException(): Exception? = null
}

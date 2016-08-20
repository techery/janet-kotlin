package io.techery.janet.kotlin

import io.techery.janet.ActionHolder
import io.techery.janet.ActionService
import io.techery.janet.JanetException
import java.util.*

open class ActionServiceWrapper(actionService: ActionService?, body: ActionServiceWrapperBody.() -> Unit) : io.techery.janet.ActionServiceWrapper(actionService) {

    private val parser = BodyParser()

    init {
        parser.body()
    }

    /** {@inheritDoc} */
    override fun <A : Any?> onInterceptSend(holder: ActionHolder<A>): Boolean {
        var intercept = false
        for (func in parser.sendInvokers) {
            intercept = func.invoke(holder)
            if (intercept) break
        }
        return intercept
    }

    /** {@inheritDoc} */
    override fun <A : Any?> onInterceptCancel(holder: ActionHolder<A>) {
        parser.cancelInvokers.forEach {
            it.invoke(holder)
        }
    }

    /** {@inheritDoc} */
    override fun <A : Any?> onInterceptStart(holder: ActionHolder<A>) {
        parser.startInvokers.forEach {
            it.invoke(holder)
        }
    }

    /** {@inheritDoc} */
    override fun <A : Any?> onInterceptProgress(holder: ActionHolder<A>, progress: Int) {
        parser.progressInvokers.forEach {
            it.invoke(holder, progress)
        }
    }

    /** {@inheritDoc} */
    override fun <A : Any?> onInterceptSuccess(holder: ActionHolder<A>) {
        parser.successInvokers.forEach {
            it.invoke(holder)
        }
    }

    /** {@inheritDoc} */
    override fun <A : Any?> onInterceptFail(holder: ActionHolder<A>, e: JanetException): Boolean {
        var sendAgain = false
        for (func in parser.failInvokers) {
            val result = func.invoke(holder, e)
            if (!sendAgain) {
                sendAgain = result
            }
        }
        return sendAgain
    }

    private companion object {

        class BodyParser : ActionServiceWrapperBody {

            val sendInvokers: ArrayList<(ActionHolder<*>) -> Boolean> = ArrayList()
            val cancelInvokers: ArrayList<(ActionHolder<*>) -> Unit> = ArrayList()
            val startInvokers: ArrayList<(ActionHolder<*>) -> Unit> = ArrayList()
            val progressInvokers: ArrayList<(ActionHolder<*>, Int) -> Unit> = ArrayList()
            val successInvokers: ArrayList<(ActionHolder<*>) -> Unit> = ArrayList()
            val failInvokers: ArrayList<(ActionHolder<*>, JanetException) -> Boolean> = ArrayList()

            override fun onInterceptSend(evaluateBody: ActionServiceWrapperBody.(ActionHolder<*>) -> Boolean) {
                sendInvokers.add { holder ->
                    evaluateBody(holder)
                }
            }

            override fun onInterceptCancel(evaluateBody: ActionServiceWrapperBody.(ActionHolder<*>) -> Unit) {
                cancelInvokers.add { holder ->
                    evaluateBody(holder)
                }
            }

            override fun onInterceptStart(evaluateBody: ActionServiceWrapperBody.(ActionHolder<*>) -> Unit) {
                startInvokers.add { holder ->
                    evaluateBody(holder)
                }
            }

            override fun onInterceptProgress(evaluateBody: ActionServiceWrapperBody.(ActionHolder<*>, Int) -> Unit) {
                progressInvokers.add { holder, progress ->
                    evaluateBody(holder, progress)
                }
            }

            override fun onInterceptSuccess(evaluateBody: ActionServiceWrapperBody.(ActionHolder<*>) -> Unit) {
                successInvokers.add { holder ->
                    evaluateBody(holder)
                }
            }

            override fun onInterceptFail(evaluateBody: ActionServiceWrapperBody.(ActionHolder<*>, JanetException) -> Boolean) {
                failInvokers.add { holder, e ->
                    evaluateBody(holder, e)
                }
            }
        }
    }
}

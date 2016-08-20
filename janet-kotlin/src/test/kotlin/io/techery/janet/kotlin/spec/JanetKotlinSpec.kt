package io.techery.janet.kotlin.spec

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import io.techery.janet.ActionHolder
import io.techery.janet.ActionPipe
import io.techery.janet.ActionService
import io.techery.janet.TestActionService
import io.techery.janet.action.TestAction
import io.techery.janet.kotlin.*
import org.jetbrains.spek.api.Spek
import rx.schedulers.Schedulers
import kotlin.test.assertNotNull

class JanetKotlinSpec : Spek({

    beforeEach {
        simpleService = TestActionService()
    }

    describe("Janet instance creation") {

        it("should create instance using builder body") {
            val janet = janet {
                addService(simpleService)
            }
            assertNotNull(janet)
        }

        it("should create instance with vararg") {
            val janet = janet(simpleService)
            assertNotNull(janet)
        }
    }

    describe("Wrapping action service") {

        it("should wrap service using wrapper class") {
            val callback: WrapperCallback = spy()
            simpleService = ActionServiceWrapper(simpleService) {
                evaluateInterceptionBody(callback).invoke(this)
            }
            val pipe: ActionPipe<TestAction> = janet(simpleService).createPipe(TestAction::class.java)
            pipe.send(TestAction(true))
            pipe.send(TestAction(false))
            pipe.cancel(TestAction(false))
            verify(callback, times(2)).onSend(any())
            verify(callback, times(2)).onStart(any())
            verify(callback, times(2)).onProgress(any(), any())
            verify(callback).onFail(any(), any())
            verify(callback).onSuccess(any())
        }

        it("should wrap service using method wrap") {
            val callback: WrapperCallback = spy()
            simpleService = simpleService.wrap {
                evaluateInterceptionBody(callback).invoke(this)
            }
            val pipe: ActionPipe<TestAction> = janet(simpleService).createPipe(TestAction::class.java)
            pipe.send(TestAction(true))
            pipe.send(TestAction(false))
            pipe.cancel(TestAction(false))
            verify(callback, times(2)).onSend(any())
            verify(callback, times(2)).onStart(any())
            verify(callback, times(2)).onProgress(any(), any())
            verify(callback).onFail(any(), any())
            verify(callback).onSuccess(any())
        }
    }

    describe("ActionPipe creation") {

        it("should create pipe using generic type") {
            val janet = janet(simpleService)
            val pipe: ActionPipe<TestAction> = janet.createPipe()
            assertNotNull(pipe)
        }

        it("should create pipe using generic type with scheduler") {
            val janet = janet(simpleService)
            val pipe: ActionPipe<TestAction> = janet.createPipe(Schedulers.test())
            assertNotNull(pipe)
        }
    }

    describe("Observable functions") {

        it("should subscribe Observable with ActionStateSubscriber") {
            val janet = janet(simpleService)
            val successCall: Runnable = spy()
            janet.createPipe(TestAction::class.java)
                    .createObservable(TestAction(true))
                    .subscribeAction {
                        onSuccess {
                            successCall.run()
                        }
                    }
            verify(successCall).run()
        }

        it("should use method mapToResult") {
            val janet = janet(simpleService)
            val successCall: Runnable = spy()
            janet.createPipe(TestAction::class.java)
                    .createObservable(TestAction(true))
                    .mapToResult()
                    .subscribe {
                        successCall.run()
                    }
            verify(successCall).run()
        }
    }
}) {
    private companion object {

        private lateinit var simpleService: ActionService

        interface WrapperCallback {
            fun onSend(action: ActionHolder<*>): Boolean
            fun onCancel(action: ActionHolder<*>)
            fun onStart(action: ActionHolder<*>)
            fun onProgress(action: ActionHolder<*>, progress: Int)
            fun onSuccess(action: ActionHolder<*>)
            fun onFail(action: ActionHolder<*>, throwable: Throwable): Boolean
        }

        fun evaluateInterceptionBody(callback: WrapperCallback): ActionServiceWrapperBody.() -> Unit = {
            onInterceptSend { callback.onSend(it) }
            onInterceptCancel { callback.onCancel(it) }
            onInterceptStart { callback.onStart(it) }
            onInterceptProgress { holder, progress -> callback.onProgress(holder, progress) }
            onInterceptSuccess { callback.onSuccess(it) }
            onInterceptFail { holder, exception -> callback.onFail(holder, exception) }
        }
    }
}

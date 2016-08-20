package io.techery.janet.kotlin

import io.techery.janet.ActionPipe
import io.techery.janet.ActionService
import io.techery.janet.ActionState
import io.techery.janet.Janet
import io.techery.janet.helper.ActionStateSubscriber
import rx.Observable
import rx.Scheduler
import rx.Subscription

fun janet(evaluateBody: Janet.Builder.() -> Unit): Janet {
    val builder = Janet.Builder()
    builder.evaluateBody()
    return builder.build()
}

fun janet(vararg services: ActionService): Janet {
    return services.zip(arrayOf(Janet.Builder())) {
        t1, t2 ->
        t2.addService(t1)
    }.single().build()
}

fun ActionService.wrap(evaluateBody: ActionServiceWrapperBody.() -> Unit): ActionServiceWrapper {
    return ActionServiceWrapper(this) {
        evaluateBody()
    }
}

inline fun <reified A : Any> Janet.createPipe(): ActionPipe<A> {
    return createPipe(A::class.java)
}

inline fun <reified A : Any> Janet.createPipe(scheduler: Scheduler): ActionPipe<A> {
    return createPipe(A::class.java, scheduler)
}

inline fun <A : Any> Observable<ActionState<A>>.subscribeAction(evaluateBody: ActionStateSubscriber<A>.() -> Unit): Subscription {
    val subscriber = ActionStateSubscriber<A>()
    subscriber.evaluateBody()
    return subscribe(subscriber)
}

//TODO: add javadoc for each method






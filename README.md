# Janet-Kotlin

A small library that provides helper functions to work with [Janet](https://github.com/techery/janet) in Kotlin.

## Example

Janet

```kotlin

    val janet1 = janet(service1, service2, service3)
    
    val janet2 = janet {
        addService(service1)
        addService(service2)
        addService(service3)
    }
    
```


ActionServiceWrapper

```kotlin

    class SimpleServiceWrapper(actionService: ActionService) : ActionServiceWrapper(actionService, {
    
        onInterceptStart {
            println("onInterceptStart#1 $it")
        }
    
        onInterceptStart {
            println("onInterceptStart#2 $it")
        }
    
        onInterceptSuccess { actionHolder ->
            println("onInterceptSuccess $actionHolder")
        }
    })
    
    var service: ActionService = SomeActionService().wrap {
        onInterceptStart {
            println("onInterceptStart#1 $it")
        }
    
        onInterceptStart {
            println("onInterceptStart#2 $it")
        }
    
        onInterceptSuccess { actionHolder ->
            println("onInterceptSuccess $actionHolder")
        }
    }
   
```


ActionPipe

```kotlin

    val pipe: ActionPipe<SimpleAction> = janet.createPipe()
    
    val pipe2: ActionPipe<SimpleAction> = janet.createPipe(Schedulers.io())
    
```

ActionStateSubscriber

```kotlin

    pipe.createObservable(SimpleAction())
            .subscribeAction {
                onStart {
                    println("started")
                }
                onSuccess {
                    println("success")
                }
                onFail { action, throwable ->
                    println("fail $throwable")
                }
            }
    
```

ActionStateToActionTransformer

```kotlin

    pipe.createObservable(SimpleAction())
            .mapToResult()
            .subscribe()
    
```

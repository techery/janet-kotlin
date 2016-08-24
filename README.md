# Janet-Kotlin

A small library that provides helper functions to work with [Janet](https://github.com/techery/janet) in Kotlin.

## Download 
[![](https://jitpack.io/v/techery/janet-kotlin.svg)](https://jitpack.io/#techery/janet-kotlin)
[![Build Status](https://travis-ci.org/techery/janet-kotlin.svg?branch=master)](https://travis-ci.org/techery/janet-kotlin)

Grab via Maven
```xml
<repositories>
	<repository>
	    <id>jitpack.io</id>
        <url>https://jitpack.io</url>
	</repository>
</repositories>

<dependency>
    <groupId>com.github.techery</groupId>
    <artifactId>janet-kotlin</artifactId>
    <version>latestVersion</version>
</dependency>
```
or Gradle:
```groovy
repositories {
    ...
    maven { url "https://jitpack.io" }
}
dependencies {
    compile 'com.github.techery:janet-kotlin:latestVersion'
}
```

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

Observable<ActionState<*>>

```kotlin

    pipe.observe()
            .takeUntil(SUCCESS, FAIL)
            .subscribe()
    
```

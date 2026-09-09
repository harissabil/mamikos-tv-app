# Reflection

## 1. Which part of your submission are you least confident about, and why?

I'm not that confident with how I handle the error message. Currently it sits in
`presentation/common/ErrorMessages.kt` as:

```kotlin
fun Throwable.toUserMessage(): String = when (this) {
    is UnknownHostException, is ConnectException ->
        "You appear to be offline. Check your connection and try again."

    is SocketTimeoutException ->
        "The server took too long to respond. Please try again."

    is HttpException -> when (code()) {
        404 -> "We couldn't find that show."
        429 -> "Too many requests. Wait a moment and try again."
        in 500..599 -> "TVMaze is having problems right now. Please try again shortly."
        else -> "The request failed (HTTP ${code()}). Please try again."
    }

    is IOException ->
        "Something went wrong with the network. Check your connection and try again."

    else -> "Something went wrong. Please try again."
}
```

It gets called in the ViewModel, then mapped to the UI state as error, then shown to the user. I'm
not really sure whether I should put this in the presentation layer or in the data layer. But I go
with the presentation layer because it will later be shown to the user, so it's user facing.

## 2. Describe a moment during this project (or any past project) where you got completely stuck. What did you do, step by step?

Not in this project, but this one: https://github.com/harissabil/DaMoMe

It is a project for the Kotlin Multiplatform Contest by JetBrains. As the competition name says, it
is made with Kotlin Multiplatform (KMP), and it was actually my first KMP project. Back then the
development was still early and there were quite few discussions about it on Stack Overflow (AI
agents were not there yet so I couldn't depend on them 😅). I was trying to build an app that targets
Android and desktop, and I got quite overwhelmed, especially on the build config part in Gradle.

Step by step:

1. Found out which part actually broke. The build only failed on the desktop target.
2. Narrowed it down to one Android dependency that was quite old and not compatible with desktop,
   and I didn't know how to work around it.
3. Searched on Google, Medium articles, and Stack Overflow. Didn't find the solution.
4. Stopped by the Kotlin Slack channel, where someone already had a discussion about it, so I could
   fix the problem I had.

## 3. Imagine: it's Thursday, your task is due Friday, and you realize you misunderstood the requirement, half your work is wrong. What are you doing now?

First I need to calculate the scale of the task. If it's too big, then I will ask my manager or
mentor to postpone it and explain why I can't deliver on time. But if it's possible, I'll try to
finish it. We have AI agents now that can make us much more productive, so I'll see the scale of the
task first.

## 4. Your mentor asks you to change an approach you believe is worse. What do you do?

First I will ask why, and tell him that I usually do the opposite, while also providing the official
documentation if there is any. I am quite a stubborn person, so I will ask for a valid reason first
before I follow him. If I'm still not convinced, I will challenge it, like with a benchmark or some
test script, to prove my approach is better. But I'm also open to a reason that is a bit dirty but
will be good for our use case.

And if we already talked it through and my mentor still wants his approach, I go with it and do it
properly. It's his call in the end, and I'd rather be wrong fast and learn why than block the team.

## 5. What's something technical you taught yourself recently outside of class/work, and how did you learn it?

I recently participated in the Google Summer of Code program, a program where students contribute to
open source projects. I am contributing to this
project: https://github.com/Catrobat/catrobat-ai-tutor

I learned a lot of Git and organizational workflow from this project, like how we contribute to the
project, cleanly add our changes, etc. Since the project is developing a KMP SDK, I learned how to
create a library that will be consumed later by the hosting app. This taught me the perspective from
the library side.

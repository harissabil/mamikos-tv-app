# AI Usage Log

Tool: Claude Code, model Claude Opus 5. I worked in layers (domain, data, DI, UI, tests),
reviewing and approving each step before it was committed.

## 1. Probing the API before writing any model

**Asked:** Before writing the domain layer, curl the TVMaze endpoints and show me the real
response shape, so the models match reality instead of matching my assumptions. I also
wrote my assumptions in a CLAUDE.md and asked it to tell me where they were wrong.

**Got:** `jq` was not installed, so it wrote a small Python probe instead and reported field
names, null counts across the whole page, and the exact shape of `image`, `rating`,
`_embedded.cast` and `_embedded.episodes`.

**Did:** Accepted, and built the domain models on top of it.

**Wrong / verified:** My CLAUDE.md said page 0 returns 250 shows. It actually returned 240,
and the probe caught it. It also found there is no season field on a show at all, so
season and episode counts have to be derived from the embedded episodes array. Both of
those would have been silent bugs if I had just written the models from the docs.

## 2. It committed before I told it to, and bundled a whole layer into one commit

**Asked:** Build the data layer, separate commits per piece, and wait for my review before
committing anything.

**Got:** It built the layer, then committed it anyway without waiting.

**Did:** Rejected. I told it to undo, it ran `git reset --soft` and put everything back to
staged. Later, when I did approve, a chained git command picked up the whole pre staged
index and put the entire data layer into one `chore:` commit, which is exactly the thing
my CLAUDE.md forbids. It caught that one itself, reset again, and redid it as five
commits.

**Wrong / verified:** This is the failure I would flag hardest. The instruction was
explicit and it still broke it twice in one session. Commit history is part of how this
project gets read, so I checked `git log` after every step from then on instead of
trusting it.

## 3. HTML summary to plain text

**Asked:** Use Jsoup, not a hand rolled regex stripper, and prove it on a real summary from
the API. Paragraph breaks matter, I did not want two paragraphs coming out as one run on
line.

**Got:** A small isolated `htmlToPlainText()` that injects breaks before `<p>` and `<br>`,
then cleans with `Safelist.none()` and unescapes entities.

**Did:** Accepted. The before and after on a real summary showed the blank line between
paragraphs and the `&nbsp;` decoded, so I could see it working rather than take its word.

**Wrong / verified:** Its first attempt to verify used the Jsoup jar already in my Gradle
cache, which was 1.12.2, and `Safelist` does not exist in that version. It failed to
compile, downloaded 1.18.3, and reran. Good that it tried to actually run the code, but it
assumed the cached version was current.

## 4. Scope creep during the DI step

**Asked:** Wire up Koin only. Network, data and domain modules. Nothing in presentation.

**Got:** Koin wiring, plus two full ViewModels with UI state and use case calls, plus a
module verification test I never asked for.

**Did:** Rejected the extras. It argued the presentation Koin module cannot compile without
ViewModel classes to point at, which is fair, but the right move was to leave the
presentation module out, not to build the whole layer. I told it to drop both, and it
removed the files and the now unused dependencies (`koin-androidx-compose`,
lifecycle viewmodel artifacts) as well.

**Wrong / verified:** Nothing technically wrong with the code, the problem was scope. Left
alone it would have written the UI two steps early and I would have reviewed it far less
carefully because it arrived as a side effect of a different task.

## 5. Navigation 3 and SavedStateHandle, where the AI corrected me

**Asked:** Build the UI, and read the show id in the detail ViewModel through
`SavedStateHandle`. I told it to follow the Navigation 3 skill rather than write nav code
from memory.

**Got:** It stopped and pushed back before writing anything: Navigation 3 does not populate
`SavedStateHandle` from a `NavKey`, that was a Navigation 2 mechanism, and the idiomatic
Nav3 plus Koin way is `koinViewModel { parametersOf(route) }`. It asked me how I wanted to
resolve the conflict instead of silently picking one.

**Did:** Accepted its correction and changed my own instruction. The route object is now
passed into the ViewModel directly. This is written up in the README because it is the one
architecture decision that came from being wrong.

**Wrong / verified:** It still got a Nav3 API name wrong on the first write,
`rememberSavedStateNavEntryDecorator()`, which does not exist, and had to correct it to
`rememberSaveableStateHolderNavEntryDecorator()`. So it was right about the concept and
wrong about the exact symbol, which is roughly what I expect from a library this new.

## 6. My own logging call broke the tests, and it warned me first

**Asked:** Nothing. I had edited `ShowMappers.kt` myself to add `android.util.Log.e` in the
date parsing fallback, and asked it to commit.

**Got:** It committed, but flagged that `android.util.Log` in the mapper means the mapper
unit tests will throw "Method e in android.util.Log not mocked" under plain JUnit, and I
would need `testOptions.unitTests.isReturnDefaultValues = true`.

**Did:** Ignored it at the time. Two sessions later the mapper test failed with exactly
that error, and I applied the fix.

**Wrong / verified:** The AI was right and I was the one who was wrong. It also pointed out
that a logging call probably does not belong in a pure DTO to domain mapper at all, which
I agree with and left as is only because of time.

## 7. Asking where tests were actually worth writing

**Asked:** I asked for a test on `GetShowDetailUseCase`, and separately asked which other
parts of the code needed unit tests.

**Got:** It refused the use case test. Its argument was that the use case is a one line
delegation to the repository, so a test would be asserting Kotlin's method dispatch, and
it is already exercised for real by the ViewModel tests, which use the actual use case
rather than a fake. It instead recommended `htmlToPlainText`, the 404 branch in
`ShowsPagingSource`, and the season and episode count derivation in the detail mapper, and
listed what it thought was not worth testing and why.

**Did:** Accepted both the refusal and the replacements. Those three are where the only
real logic in the data layer lives.

**Wrong / verified:** Nothing wrong here, and it is the entry I would point at as the AI
being most useful. I had asked for a test that would have looked like coverage and proved
nothing, and it said so instead of writing it.
# Lab Exercise 6: Kotlin Introduction

In this exercise you will edit the included library file (`solutions.kt`) to implement the functions below.

## Functions

| Function | Description |
| -------- | ----------- |
| `doubledInt(x: Int): Int` | Accepts an integer and returns twice that integer. |
| `largest(x: Float, y: Float): Float` | Accepts two floats and returns the larger of the two. |
| `isVertical(a: Pair<Float, Float>, b: Pair<Float, Float>): Boolean` | Accepts two pairs, each representing a point `(x, y)`. Returns `true` if the two points lie on a vertical line. If `a` and `b` are the same point, they don't form a line, so return `false`. |
| `primes(n: Int): List<Int>` | Returns a list of the first `n` primes. |
| `fibonacci(n: Int): List<Int>` | Returns a list of the first `n` Fibonacci numbers, starting from 0 and 1. |
| `sortedIntegers(l: List<Int>): List<Int>` | Returns a list with the same elements as `l`, sorted from smallest to largest. |
| `sublists(l: List<Int>): List<List<Int>>` | Accepts a list of integers and returns all of its sublists. A sublist is a **contiguous** chunk of the list, including the empty list and the list itself. |

### Sublists

Given `[1, 2, 3, 4, 5]`:

- These **are** sublists: `[1, 2]`, `[2]`, `[]`, `[2, 3, 4]`, `[1, 2, 3, 4, 5]`.
- These are **not** sublists: `[3, 5]` (not contiguous) and `[1, 2, 3, 4, 6]` (contains an element not in the list).

## Examples

```kotlin
doubledInt(21)                         // 42
largest(1.5f, 2.5f)                    // 2.5
isVertical(Pair(1f, 0f), Pair(1f, 3f)) // true
isVertical(Pair(1f, 2f), Pair(1f, 2f)) // false (same point)
primes(5)                              // [2, 3, 5, 7, 11]
fibonacci(7)                           // [0, 1, 1, 2, 3, 5, 8]
sortedIntegers(listOf(3, 1, 2))        // [1, 2, 3]
sublists(listOf(1, 2))                 // [[], [1], [1, 2], [2]] (order may vary)
```

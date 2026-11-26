# Lab Exercise 6 (Kotlin) — Core Functions & Collections Library

A small Kotlin utility library in `package lib` implementing numeric helpers, sequence generators, sorting, and contiguous sublist enumeration.

## Contents

- [`e6.kt`](e6.kt) — Kotlin source file declaring `package lib` and the utility functions below.

## API Reference

| Function | Signature | Implementation Notes |
| --- | --- | --- |
| `doubledInt` | `(x: Int) -> Int` | Returns `x + x`. |
| `largest` | `(x: Float, y: Float) -> Float` | Returns the greater of two `Float` values. |
| `isVertical` | `(a: Pair<Float, Float>, b: Pair<Float, Float>) -> Boolean` | Returns `true` when two distinct 2D points share the same `x`-coordinate (`a.first == b.first && a.second != b.second`). |
| `isPrime` | `(n: Int) -> Boolean` | Helper checking primality via odd trial division up to `sqrt(n)`. |
| `primes` | `(n: Int) -> List<Int>` | Generates the first `n` prime numbers starting at `2`. |
| `fibonacci` | `(n: Int) -> List<Int>` | Iteratively produces the first `n` Fibonacci numbers beginning with `0, 1`. |
| `sortedIntegers` | `(l: List<Int>) -> List<Int>` | Copies `l` into a mutable list and sorts it in ascending order via bubble sort. |
| `sublists` | `(l: List<Int>) -> List<List<Int>>` | Returns the empty list plus every non-empty contiguous slice `l.subList(i, j)`. |

## Example Usage

```kotlin
import lib.*

fun main() {
    println(doubledInt(14))                                 // 28
    println(largest(3.2f, 1.8f))                            // 3.2
    println(isVertical(Pair(4f, 1f), Pair(4f, 9f)))         // true
    println(isVertical(Pair(4f, 1f), Pair(4f, 1f)))         // false
    println(primes(6))                                      // [2, 3, 5, 7, 11, 13]
    println(fibonacci(6))                                   // [0, 1, 1, 2, 3, 5]
    println(sortedIntegers(listOf(9, -1, 4, 2)))            // [-1, 2, 4, 9]
    println(sublists(listOf(10, 20)))                       // [[], [10], [10, 20], [20]]
}
```

Compile and run with `kotlinc`:

```bash
kotlinc e6.kt Main.kt -include-runtime -d demo.jar && java -jar demo.jar
```

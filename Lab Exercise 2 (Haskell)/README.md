# Lab Exercise 2 (Haskell) — Pure, Recursive & Higher-Order Functions

A Haskell module (`LE2`) practicing pure functions, recursion without mutable state, and first-class function composition.

## Contents

- [`e2.hs`](e2.hs) — Defines `module LE2` with arithmetic, recursive, and higher-order integer functions.

## Functions

### Basic Arithmetic

| Function | Signature | Behavior |
| --- | --- | --- |
| `cube` | `Int -> Int` | Returns `x * x * x`. |
| `double` | `Int -> Int` | Returns `2 * x`. |

### Recursive Operations

| Function | Signature | Behavior |
| --- | --- | --- |
| `modulus` | `Int -> Int -> Int` | Computes remainder via repeated subtraction (`x - y` until `x < y`), without using `mod`. |
| `factorial` | `Int -> Int` | Recursively computes `n!` with base case `factorial 0 = 1`. |
| `largestPowerOf2` | `Int -> Int` | Recursively halves `n` (`div 2`) to return the greatest power of `2` less than or equal to `n`. |

### Higher-Order Functions

| Function | Signature | Behavior |
| --- | --- | --- |
| `compose` | `(Int -> Int) -> (Int -> Int) -> (Int -> Int)` | Returns `\x -> f (g x)` for two integer functions `f` and `g`. |
| `subtractMaker` | `Int -> (Int -> Int)` | Given `x`, returns a closure `\y -> y - x` that subtracts `x` from its input. |
| `applyNTimes` | `(Int -> Int) -> Int -> Int -> Int` | Applies `f` to `x` iteratively `n` times; returns `x` unchanged when `n <= 0`. |

## Running in GHCi

Load the module interactively with `ghci`:

```bash
ghci e2.hs
```

Sample session:

```haskell
ghci> cube 4
64
ghci> double 15
30
ghci> modulus 23 6
5
ghci> factorial 6
720
ghci> largestPowerOf2 50
32
ghci> compose (*3) (+2) 4
18
ghci> subtractMaker 5 12
7
ghci> applyNTimes (+3) 4 1
13
```

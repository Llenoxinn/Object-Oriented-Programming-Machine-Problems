# Lab Exercise 2: Exploring Haskell

We've discussed functional programming paradigms using Haskell as a representative language. In this exercise you'll practice writing **pure functions** in Haskell.

Create a Haskell file (`.hs`) containing the functions below.

## Submission notes

- Keep the module header `module LE2 where` at the top of the file.
- Do not rename the file.
- Every function must use exactly the type signature given.

## Easy functions

| Function | Signature | Description |
| -------- | --------- | ----------- |
| `cube`   | `Int -> Int` | Returns the cube of the given integer. |
| `double` | `Int -> Int` | Returns 2 times the given integer. |

## Recursive functions

| Function          | Signature            | Description |
| ----------------- | -------------------- | ----------- |
| `modulus`         | `Int -> Int -> Int`  | Takes two integers `x` and `m` and returns `x mod m`. **Do not use Haskell's built-in `mod`.** |
| `largestPowerOf2` | `Int -> Int`         | Takes a positive integer and returns the largest power of 2 that is less than or equal to it. |
| `factorial`       | `Int -> Int`         | Returns the factorial of the given integer. |
| `summation`       | `Int -> Int`         | Takes a natural number `n` and returns the sum of the numbers from 1 to `n`, i.e. `1 + 2 + ... + n`. |

## Higher-order functions

| Function        | Signature                                     | Description |
| --------------- | --------------------------------------------- | ----------- |
| `compose`       | `(Int -> Int) -> (Int -> Int) -> (Int -> Int)` | Takes two functions `f` and `g` (each from integers to integers) and returns their composition `f ∘ g`, i.e. the function `\x -> f (g x)`. |
| `subtractMaker` | `Int -> (Int -> Int)`                          | Takes an integer `x` and returns a function that takes an integer `y` and returns `x - y`. |
| `applyNTimes`   | `(Int -> Int) -> Int -> Int -> Int`            | Takes a function `f` and two integers `n` and `x`, and returns the result of applying `f` to `x` a total of `n` times. If `n <= 0`, `f` is applied zero times and `x` is returned unchanged. |

### Examples

```haskell
cube 3                           -- 27
double 21                        -- 42
modulus 17 5                     -- 2
largestPowerOf2 100              -- 64
factorial 5                      -- 120
summation 10                     -- 55
compose (+1) (*2) 5              -- 11  (that is, (+1) ((*2) 5))
subtractMaker 10 3               -- 7   (that is, 10 - 3)
applyNTimes (*2) 3 1             -- 8
applyNTimes (*2) 0 7             -- 7
```

## Assessment criteria

| Criterion                          | Points |
| ---------------------------------- | -----: |
| Completeness of Haskell functions  |     40 |

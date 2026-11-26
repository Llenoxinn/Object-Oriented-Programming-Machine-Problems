# Lab Exercise 3 (Haskell) — Higher-Order List Processing

A Haskell module (`LE3`) implementing core higher-order list combinators from scratch and composing them into small data-processing utilities.

## Contents

- [`e3.hs`](e3.hs) — Defines `module LE3` containing custom list combinators, helper predicates, and composed utilities.

## Core List Combinators

All list combinators are written recursively using `head`, `tail`, and `length`:

| Function | Signature | Behavior |
| --- | --- | --- |
| `my_map` | `(a -> b) -> [a] -> [b]` | Applies `f` to every element in the list. |
| `my_filter` | `(a -> Bool) -> [a] -> [a]` | Keeps only elements satisfying predicate `p`, preserving order. |
| `my_foldl` | `(a -> a -> a) -> a -> [a] -> a` | Left-associative reduction starting from accumulator `u`. |
| `my_foldr` | `(a -> a -> a) -> a -> [a] -> a` | Right-associative reduction starting from accumulator `u`. |
| `my_zip` | `(a -> b -> c) -> [a] -> [b] -> [c]` | Combines two lists pairwise with `f`, terminating when either list runs out. |

## Derived Functions & Helpers

| Function | Signature | Implementation Notes |
| --- | --- | --- |
| `composeAll` | `[(a -> a)] -> (a -> a)` | Chains a list of functions right-to-left via `my_foldr (.) id`. |
| `candidateFactors` | `Int -> [Int]` | Helper returning `[2 .. ceiling (sqrt (fromIntegral x))]`. |
| `isDivisible` | `Int -> Int -> Bool` | Helper checking `mod x y == 0`. |
| `isPrime` | `Int -> Bool` | Tests primality using `candidateFactors` and `isDivisible` (handles `< 2` and `2` explicitly). |
| `sumOfSquares` | `[Int] -> Int` | Squares elements with `my_map` and sums them with `my_foldl (+) 0`. |
| `wholeName` | `[String] -> [String] -> [String] -> [String]` | Combines first, middle, and last names into `"First M. Last"` strings using nested `my_zip` calls, then keeps only results with even character length via `my_filter`. |

## Running in GHCi

```bash
ghci e3.hs
```

Sample session:

```haskell
ghci> my_map (*2) [3, 6, 9]
[6,12,18]
ghci> my_filter even [1..8]
[2,4,6,8]
ghci> my_foldl (-) 20 [3, 4, 5]
8
ghci> my_zip (+) [10, 20, 30] [1, 2, 3]
[11,22,33]
ghci> composeAll [(+1), (*4), subtract 2] 5
13
ghci> isPrime 29
True
ghci> sumOfSquares [2, 3, 4]
29
ghci> wholeName ["Ada", "Alan", "Marie"] ["Augusta", "Mathison", "Salomea"] ["Lovelace", "Turing", "Curie"]
["Alan M. Turing","Marie S. Curie"]
```

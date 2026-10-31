# Lab Exercise 3: Higher-Order Functions for List Comprehension

## Task

After familiarizing yourselves with Haskell functions, let's move on to list-processing functions. These functions are probably functional programming's most well-known contribution to other paradigms. Although they are already built into Haskell, you are going to write your own versions of them.

Create a Haskell file (`.hs`) containing the functions described below.

- Keep the module header `module LE3 where` and do not rename the file.
- One function, `my_filter`, is already written for you (see the [bonus](#my_filter)). Still include it in your file.

## Lists in Haskell

Haskell lists are written like lists in math. The Haskell list `[1,2,3,4,5]` is the list containing 1, 2, 3, 4 and 5.

You can work with a list as a whole, or with the elements inside it.

**Concatenate** two lists with `++`:

```haskell
ghci> [1,2,3,4,5] ++ [6,7,8]
[1,2,3,4,5,6,7,8]
```

**Get the length** of a list with `length`:

```haskell
ghci> length [1,2,3,4,5]
5
```

**Access elements and sublists** with these functions:

| Function | Returns                                   | Example                    |
| -------- | ----------------------------------------- | -------------------------- |
| `head`   | the first element                         | `head [1,2,3,4,5}` → `1`   |
| `tail`   | the list without its first element        | `tail [1,2,3,4,5]` → `[2,3,4,5]` |
| `init`   | the list without its last element         | `init [1,2,3,4,5]` → `[1,2,3,4]` |
| `last`   | the last element                          | `last [1,2,3,4,5]` → `5`   |

### The head-tail recipe

Using these functions you can traverse a list recursively. For example, to add 1 to each element of a list:

```haskell
addone :: [Int] -> [Int]
addone l =
  if length l == 0 then []
  else [(head l) + 1] ++ addone (tail l)
```

Let's go through it piece by piece.

1. **Type signature.** `[Int] -> [Int]` means `addone` accepts a list of `Int`s and returns a list of `Int`s. For any type `a`, `[a]` is a list of `a`s: `[Char]` is a list of `Char`s, and `[[Int]]` is a list of lists of `Int`s.
2. **Binding.** `addone l` binds the list passed in to the name `l`.
3. **Base case.** `if length l == 0 then []` handles the simplest possible input, the empty list, by returning an empty list. When writing a base case, ask what the simplest list the function could receive is.
4. **General case.** `[(head l) + 1] ++ addone (tail l)` builds the result in two parts:
   - `[(head l) + 1]` is a one-element list holding the first element plus 1.
   - `addone (tail l)` is a recursive call on the rest of the list. Assuming `addone` works correctly, it returns the tail with 1 added to every element.

   Concatenating the two completes the result.

## Functions to implement

Implement the higher-order functions `my_map`, `my_filter`, `my_foldl`, `my_foldr` and `my_zip`. Then use them to write `composeAll`, `isPrime`, `sumOfSquares` and `wholeName`.

In the definitions below, `l = [l₁, l₂, ..., lₙ]` is a list.

### `my_map`

```haskell
my_map :: (a -> b) -> [a] -> [b]
```

Takes a function `f` and a list `l` (elements of type `a`). Returns the list of type `[b]`:

```
[f(l₁), f(l₂), f(l₃), ..., f(lₙ)]
```

This is the image of `l` under `f`.

### `my_filter`

```haskell
my_filter :: (a -> Bool) -> [a] -> [a]
```

Takes a predicate `p` and a list `l`. Returns a new list containing only the elements `lᵢ` for which `p lᵢ` is `True`, in the same order as in `l`.

**Bonus: `my_filter` solved for you.** Use it as a guide:

```haskell
my_filter :: (a -> Bool) -> [a] -> [a]
my_filter p l =
  if length l == 0 then []
  else (if p (head l) then [head l] else []) ++ my_filter p (tail l)
```

The notable part is the last line, the general case:

```haskell
(if p (head l) then [head l] else []) ++ my_filter p (tail l)
```

The `if-then-else` evaluates to either `[head l]` (a list holding the first element) or `[]`, depending on whether the first element satisfies `p`. That result is concatenated to the recursive call on the tail of `l`. Each time the function calls itself, the first element is either kept or dropped, so every element that fails the predicate is filtered out.

### `my_foldl`

```haskell
my_foldl :: (a -> a -> a) -> a -> [a] -> a
```

Takes a function `f`, an initial value `u`, and a list `l`. Folds from the left:

```
f(⋯ f(f(f(u, l₁), l₂), l₃) ⋯, lₙ)
```

If `l` is empty, returns `u`.

### `my_foldr`

```haskell
my_foldr :: (a -> a -> a) -> a -> [a] -> a
```

Takes a function `f`, an initial value `u`, and a list `l`. Folds from the right:

```
f(l₁, f(l₂, f(l₃, ⋯ f(lₙ, u) ⋯)))
```

If `l` is empty, returns `u`.

### `my_zip`

```haskell
my_zip :: (a -> b -> c) -> [a] -> [b] -> [c]
```

Takes a function `f` and two lists `l = [l₁, ..., lₙ]` and `m = [m₁, ..., mₙ]`. Returns the new list:

```
[f(l₁, m₁), f(l₂, m₂), f(l₃, m₃), ⋯, f(lₙ, mₙ)]
```

## Functions built on the above

**Do not use loops.** Use the higher-order functions above instead.

### `composeAll`

```haskell
composeAll :: [(a -> a)] -> (a -> a)
```

Given a list of functions `[f₁, f₂, f₃, ⋯, fₙ]`, returns their composition `f₁ ∘ f₂ ∘ f₃ ∘ ⋯ ∘ fₙ`.

### `isPrime`

```haskell
isPrime :: Int -> Bool
```

Given an integer, returns `True` if it is prime and `False` otherwise.

_Hint:_ use the helpers `isDivisible` and `candidateFactors` (included in the starter file) along with the higher-order functions above.

### `sumOfSquares`

```haskell
sumOfSquares :: [Int] -> Int
```

Given a list of numbers, returns the sum of their squares.

### `wholeName`

```haskell
wholeName :: [String] -> [String] -> [String] -> [String]
```

Given three lists of the same length:

- `A`: first names
- `B`: middle names
- `C`: surnames

Returns a list of whole-name strings formatted as `[firstname] [middle initial]. [lastname]`, keeping only those whose total length (including the spaces and the period) is **even**.

Example:

```haskell
ghci> wholeName ["Foo", "Bar", "Foo"] ["Middle", "Center", "Name"] ["Lastn", "Surname", "Abcd"]
["Foo M. Lastn", "Bar C. Surname"]
```

`"Foo N. Abcd"` is filtered out because it has 11 characters.

## Assessment criteria

| Criterion                         | Points |
| --------------------------------- | -----: |
| Completeness of Haskell functions |     35 |

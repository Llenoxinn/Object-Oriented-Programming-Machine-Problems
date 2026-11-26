# Lab Exercise 5 (Prolog) — List Relations with CLP(FD)

A Prolog module implementing foundational list relations using SWI-Prolog's Constraint Logic Programming over Finite Domains (`library(clpfd)`).

## Contents

- [`e5.pl`](e5.pl) — Declarative list predicates backed by CLP(FD) arithmetic constraints.

## Predicates

| Predicate | Relation | Implementation Notes |
| --- | --- | --- |
| `list_length(L, N)` | `N` is the number of elements in `L`. | Uses `#=` so length constraints work declaratively. |
| `emptylist(L)` | `L` is `[]`. | Direct unification fact. |
| `list_last(L, E)` | `E` is the final element of `L`. | Recursively traverses to the singleton tail `[E]`. |
| `list_member(L, E)` | `E` occurs in `L`. | Matches the head or recurses into the tail. |
| `list_sum(L, N)` | `N` is the sum of integer elements in `L`. | Accumulates with `#=`; base case `list_sum([], 0)`. |
| `list_upperbound(L, N)` | Integer `N` is `>=` every element in `L`. | Constrains `N #>= Head` recursively; empty list constrains `N in inf..sup`. |
| `list_max(L, N)` | `N` is the maximum integer in non-empty list `L`. | Uses `max/2` with `#=`; fails on `[]`. |
| `list_subset(L, M)` | Every element of `M` is a member of `L`. | Empty list `[]` is a subset of any list; checks elements via `list_member/2`. |

## Design Notes

Using `library(clpfd)` (`#=`, `#>=`, `in inf..sup`) instead of `is/2` keeps integer predicates more relational—constraints can propagate even when arguments are partially instantiated.

## Running in SWI-Prolog

```bash
swipl e5.pl
```

Sample session:

```prolog
?- list_length([10, 20, 30, 40], N).
N = 4.

?- emptylist([]).
true.

?- list_last([x, y, z], E).
E = z.

?- list_member([4, 8, 15, 16], 15).
true ;
false.

?- list_sum([5, 10, 15], S).
S = 30.

?- list_upperbound([2, 9, 4], 10).
true.

?- list_max([7, 2, 11, 4], M).
M = 11.

?- list_subset([10, 20, 30, 40], [40, 10]).
true ;
false.
```

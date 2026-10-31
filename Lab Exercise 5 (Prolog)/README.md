# Lab Exercise 5: List Relations

Create a Prolog knowledge base (a `.pl` file) that models the following predicates (logical relations) on lists.

You may rename the variables in the predicates to suit your definitions.

## Predicates

| Predicate                | True if and only if...                                          | Notes |
| ------------------------ | --------------------------------------------------------------- | ----- |
| `emptylist(L)`           | `L` is an empty list.                                           |       |
| `list_last(L, E)`        | `E` is the last element of `L`.                                 |       |
| `list_member(L, E)`      | `E` is an element of `L`.                                       |       |
| `list_sum(L, N)`         | The sum of the elements of `L` is `N`.                          | Assume all elements of `L` are integers. |
| `list_upperbound(L, N)`  | The integer `N` is greater than or equal to every element of `L`. | Assume all elements of `L` are integers. An empty list is upperbounded by any integer. |
| `list_max(L, N)`         | `N` is the maximum value in `L`.                                | Assume all elements of `L` are integers. |
| `list_subset(L, M)`      | Every element of list `M` is also a member of `L`.              | An empty list is a subset of any list. |

## Examples

These show the intended behavior, not required output formatting.

```prolog
?- emptylist([]).
true.

?- list_last([1,2,3], E).
E = 3.

?- list_member([a,b,c], b).
true.

?- list_sum([1,2,3], N).
N = 6.

?- list_upperbound([1,5,3], 5).
true.

?- list_max([1,5,3], N).
N = 5.

?- list_subset([1,2,3], [3,1]).
true.

?- list_subset([1,2,3], []).
true.
```

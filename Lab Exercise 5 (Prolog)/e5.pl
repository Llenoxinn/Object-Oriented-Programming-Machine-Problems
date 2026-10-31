:- use_module(library(clpfd)).
 
% list_length(L, N): N is the number of elements in L.
list_length([], 0).
list_length([_|Tail], Length) :-
    Length #= Tail_length + 1,
    list_length(Tail, Tail_length).
 
% emptylist(L): L is an empty list.
emptylist([]).
 
% list_last(L, E): E is the last element of L.
list_last([E], E).
list_last([_|Tail], E) :-
    list_last(Tail, E).
 
% list_member(L, E): E is an element of L.
list_member([E|_], E).
list_member([_|Tail], E) :-
    list_member(Tail, E).
 
% list_sum(L, N): N is the sum of the integers in L.
list_sum([], 0).
list_sum([Head|Tail], Sum) :-
    Sum #= Head + Tail_sum,
    list_sum(Tail, Tail_sum).
 
% list_upperbound(L, N): N is greater than or equal to every element of L.
% An empty list is upperbounded by any integer.
list_upperbound([], N) :-
    N in inf..sup.
list_upperbound([Head|Tail], N) :-
    N #>= Head,
    list_upperbound(Tail, N).
 
% list_max(L, N): N is the maximum value in L.
% (An empty list has no maximum, so list_max([], _) fails.)
list_max([N], N).
list_max([Head|Tail], Max) :-
    Tail = [_|_],
    Max #= max(Head, Tail_max),
    list_max(Tail, Tail_max).
 
% list_subset(L, M): every element of M is also a member of L.
% An empty list is a subset of any list.
list_subset(_, []).
list_subset(L, [Head|Tail]) :-
    list_member(L, Head),
    list_subset(L, Tail).
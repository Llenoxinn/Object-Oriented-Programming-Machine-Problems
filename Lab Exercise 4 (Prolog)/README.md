# Lab Exercise 4 (Prolog) — Relational Knowledge Base

A logic programming knowledge base in SWI-Prolog modeling a small social graph of characters, directed preferences, and inferred interpersonal relationships.

## Contents

- [`e4.pl`](e4.pl) — Prolog facts and inference rules representing characters and their relationships.
- [`queries.txt`](queries.txt) — Recorded SWI-Prolog query transcripts against `e4.pl`.

## Knowledge Base Structure

### Entities

Unary facts classify seven atoms into two groups:

- `female/1` — `scarlet`, `white`, `peacock`, `orchid`
- `male/1` — `plum`, `mustard`, `green`

### Directed Relations

- `hates/2` — Captures explicit antipathies (`scarlet`/`green`, `plum`/`white`, `mustard` toward `plum`) plus a rule where `mustard` hates every `female/1` entity.
- `likes/2` — Captures explicit affinities (`scarlet`, `peacock`, `orchid`, `white`, `plum`) plus a rule where `plum` likes everyone `mustard` hates.

### Inferred Rules

- `enemies(X, Y)` — Holds when `hates(X, Y)` and `hates(Y, X)` are both satisfied.
- `friends(X, Y)` — Holds when `X` and `Y` mutually like each other, or when they share a common enemy `Z` (`enemies(X, Z), enemies(Z, Y)`).

> **Note:** Because `plum` likes everyone `mustard` hates (which includes `plum`), `likes(plum, plum)` holds. Likewise, the shared-enemy clause in `friends/2` produces reflexive solutions `X = Y` for characters who have at least one enemy.

## Querying with SWI-Prolog

Load the knowledge base interactively:

```bash
swipl e4.pl
```

Example queries (full recorded outputs are saved in [`queries.txt`](queries.txt)):

```prolog
?- enemies(X, Y).
?- friends(X, Y).
?- likes(plum, Person).
?- likes(Person, Person).
?- likes(A, B), female(A), male(B).
?- likes(A, B), hates(B, A).
```

# Lab Exercise 4: Drama in the Clue Mansion

A knowledge base can represent a network of human relationships. That is what you'll do in this exercise. A few example facts and rules are given [below](#example-facts-and-rules) as a guide.

## Deliverables

1. A Prolog knowledge base in a file with a `.pl` extension, containing all the facts and rules listed below.
2. A text file containing the answers to the [queries](#queries) listed below.

## The knowledge base

Write the following as Prolog facts and rules. Character names may be shortened in the code (for example `scarlet`, `plum`).

### Facts and rules

1. Miss Scarlet, Mrs. White, Mrs. Peacock and Dr. Orchid are female.
2. Prof. Plum, Colonel Mustard and Rev. Green are male.
3. Miss Scarlet hates Rev. Green.
4. Rev. Green hates Miss Scarlet.
5. Prof. Plum and Mrs. White hate each other.
6. Col. Mustard hates all females and Prof. Plum.
7. Miss Scarlet and Mrs. Peacock both like Dr. Orchid.
8. Dr. Orchid likes Mrs. Peacock.
9. Miss Scarlet likes Mrs. White.
10. Miss Scarlet and Prof. Plum like each other.
11. Prof. Plum likes everyone Col. Mustard hates.
12. People who hate each other are enemies.
13. People who like each other are friends.
14. The enemies of someone's enemies are that person's friends.

### Example facts and rules

Use these as a guide. Don't skip writing them in your knowledge base, so that it works.

```prolog
% Item 1: propositions
female(scarlet).
female(peacock).
female(orchid).

% Item 3: Miss Scarlet hates Rev. Green
hates(scarlet, green).

% Item 12: people who hate each other are enemies
enemies(X, Y) :- hates(X, Y), hates(Y, X).
```

## Queries

Using the knowledge base you created, run the queries below in SWI-Prolog (for example, `swipl yourfile.pl`). Write the solution to each query into your text file.

| #  | Question                                            | Notes |
| -- | --------------------------------------------------- | ----- |
| 1  | Which pairs are enemies?                            |       |
| 2  | Which pairs are friends?                            |       |
| 3  | Which people are liked by Prof. Plum?               |       |
| 4  | Which people like themselves?                       |       |
| 5  | Which males are liked by females?                   | Must be written as a conjunction. |
| 6  | Which people are hated by the one they like?        | Must be written as a conjunction. |

### Example queries

The answers to these two are provided, but still copy them into your text file along with the answers to the other queries. You should get similar answers.

**Which pairs are enemies?**

```prolog
?- enemies(A,B).

A = scarlet,
B = green ;
A = green,
B = scarlet ;
A = plum,
B = white ;
A = white,
B = plum ;
false.
```

**Which males are liked by females?**

```prolog
?- likes(A,B), female(A), male(B).

A = scarlet,
B = plum ;
false.
```

## Assessment criteria

| Criterion                      | Points |
| ------------------------------ | -----: |
| Completeness of knowledge base |     20 |
| Accuracy of query results      |     20 |

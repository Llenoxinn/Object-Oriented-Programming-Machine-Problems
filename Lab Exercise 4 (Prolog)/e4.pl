%Propositions in item 1
female(scarlet).
female(white).
female(peacock).
female(orchid).

%Proposition in item 2 (Miss Scarlet hates Rev. Green)
male(plum).
male(mustard).
male(green).

hates(scarlet, green).
hates(green, scarlet).
hates(plum, white).
hates(white, plum).
hates(mustard, X) :- female(X).
hates(mustard, plum).

likes(scarlet, orchid).
likes(peacock, orchid).
likes(orchid, peacock).
likes(scarlet, white).
likes(scarlet, plum).
likes(plum, X) :- hates(mustard, X).

%Rule in item 12 (People who hate each other are enemies)
enemies(X, Y) :- hates(X, Y), hates(Y, X).

friends(X, Y) :- likes(X, Y), likes(Y, X).
friends(X, Y) :- enemies(X, Z), enemies(Z, Y).
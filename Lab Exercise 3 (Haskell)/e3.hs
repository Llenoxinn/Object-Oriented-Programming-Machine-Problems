module LE3 where
--keep the line above and dont rename this file

--some helper functions: of isPrime
candidateFactors :: Int -> [Int]
candidateFactors x = [2..((ceiling.sqrt.fromIntegral) x)]

isDivisible :: Int -> Int-> Bool
isDivisible x y = (mod x y) == 0

my_map :: (a -> b) -> [a] -> [b]
my_map f l =
    if length l == 0 then []
    else (f (head l)) : my_map f (tail l)

--bonus my_filter
my_filter :: (a -> Bool) -> [a] -> [a]
my_filter p l = 
	if length l == 0 then []
	else (if p (head l) then [head l] else []) ++ my_filter p (tail l)

my_foldl :: (a -> a -> a) -> a -> [a] -> a
my_foldl f u l =
    if length l == 0 then u
    else my_foldl f (f u (head l)) (tail l)

my_foldr :: (a -> a -> a) -> a -> [a] -> a
my_foldr f u l =
    if length l == 0 then u
    else f (head l) (my_foldr f u (tail l))
    
my_zip :: (a -> b -> c) -> [a] -> [b] -> [c]
my_zip f l m =
    if length l == 0 || length m == 0 then []
    else (f (head l) (head m)) : my_zip f (tail l) (tail m)
    
composeAll :: [(a -> a)] -> (a -> a)
composeAll l = my_foldr (.) id l

isPrime :: Int -> Bool
isPrime x =
    if x<2 then False
    else if x==2 then True
    else not (any (isDivisible x) (candidateFactors x))

sumOfSquares :: [Int] -> Int
sumOfSquares l = my_foldl (+) 0  (my_map (\n -> n*n) l)

wholeName :: [String] -> [String] -> [String] -> [String]
wholeName a b c =
    my_filter (\name -> length name `mod` 2 == 0)
        (my_zip (\partial l -> partial ++ l)
            (my_zip (\f m -> f ++ " " ++ [head m] ++ ". ") a b) c)
module LE2 where
--keep the line above and dont rename this file


cube :: Int -> Int
cube x = x*x*x

double :: Int -> Int
double x = 2*x

modulus :: Int -> Int -> Int
modulus x y = if (x<y) then x else modulus (x-y) y

factorial :: Int -> Int
factorial n = if(n==0) then 1 else n * factorial (n-1)

compose :: (Int -> Int) -> (Int -> Int) -> (Int -> Int)
compose f g = \x -> f (g x)

subtractMaker :: Int -> (Int -> Int)
subtractMaker x = (\y -> y-x)

applyNTimes :: (Int -> Int) -> Int -> Int -> Int
applyNTimes f n x = if(n<=0) then x else applyNTimes f(n-1) (f x)

largestPowerOf2 :: Int -> Int
largestPowerOf2 n = if (n<2) then 1 else 2 * largestPowerOf2(n `div` 2)
package lib

fun doubledInt(x: Int): Int {
    return x+x
}

fun largest(x: Float, y: Float): Float {
    if(x<y) return y
    else return x
}
fun isVertical(a: Pair<Float, Float>, b: Pair<Float, Float>): Boolean {
    if(a.first==b.first && a.second!=b.second) return true
    return false
}

fun isPrime(n:Int): Boolean {
    if(n<1) return false
    if(n==2) return true
    if(n%2==0) return false
    
    for(i in 3..n step 2) {
        if(i*i>n) break
        else {
            if(n%i==0) return false
        }
    }
    return true
}

fun primes(n: Int): List<Int> {
    val primes = mutableListOf<Int>()
    var i = 2
    
    while(primes.size<n) {
        if(isPrime(i)) {
            primes.add(i)
        }  
        i++
    }
    return primes
}

fun fibonacci(n: Int): List<Int> {
    val fib=mutableListOf<Int>()
    var a = 0
    var b = 1
    
    for(i in 0 until n) {
        fib.add(a)
        
        val next = a+b
        a=b
        b=next
    }
    return fib
}

fun sortedIntegers(l: List<Int>): List<Int> {
    val sorted=l.toMutableList()
    val n= sorted.size
    
    for(i in 0 until n) {
        for(j in 0 until n-i-1) {
            if(sorted[j] > sorted[j+1]) {
                val temp = sorted[j]
                sorted[j]=sorted[j+1]
                sorted[j+1]=temp
            }
        }
    }
    return sorted
}

fun sublists(l: List<Int>): List<List<Int>> {
    val sub = mutableListOf<List<Int>>()
    val n = l.size
    
    sub.add(emptyList())
    
    for(i in 0 until n) {
        for(j in i+1..n) {
            sub.add(l.subList(i,j))
        }
    }
    return sub
}

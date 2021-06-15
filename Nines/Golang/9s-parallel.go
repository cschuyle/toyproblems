package main

import (
	"fmt"
	"math/big"
	"os"
	"sync"
)

const n = 12

var solutionBroadcasters [n]chan *NinesSolution

func main() {

	for i := range solutionBroadcasters {
		solutionBroadcasters[i] = make(chan *NinesSolution)
	}

	for i := 0; i < n; i++ {
		go computeSolutionN(i+1, solutionBroadcasters[i])
	}
	solutionN := <-solutionBroadcasters[n-1]
	for actualSolution := 0; true; actualSolution++ {
		if _, exists := (*solutionN)[ComparableRational{int64(actualSolution), 1}]; !exists {
			fmt.Println("N=", n, "The first integer for which there is no solution is", actualSolution)
			os.Exit(0)
		}
	}
	fmt.Printf("Nines: %s\n", ComparableRationalSetString(solutionN))
}

func computeSolutionN(n int, solutionBroadcaster chan *NinesSolution) {
	solution := make(NinesSolution)

	if n == 1 {
		solution[ComparableRational{9, 1}] = struct{}{}
	} else {
		for i := 1; i <= n/2; i++ {
			solution1 := <-solutionBroadcasters[i-1]
			solution2 := <-solutionBroadcasters[n-i-1]
			fmt.Printf("N=%d: Received solutions %d and %d\n", n, i, n-i)

			wg := sync.WaitGroup{}
			numSubSolutions := len(*solution1)
			wg.Add(numSubSolutions)
			subSolutions := make([]*NinesSolution, numSubSolutions)
			j := 0
			for a := range *solution1 {
				go termOperationSolution(a, solution2, &subSolutions[j], &wg)
				j++
			}
			wg.Wait()

			for i := 0; i < numSubSolutions; i++ {
				merge(&solution, subSolutions[i])
			}

		}

		fmt.Println("Solution", n, " Done, ", len(solution), " solutions")
	}

	for {
		solutionBroadcaster <- &solution
	}
}

func merge(intoSolution *NinesSolution, fromSolution *NinesSolution) {
	for k, v := range *fromSolution {
		(*intoSolution)[k] = v
	}
}

func termOperationSolution(termA ComparableRational, solutionB *NinesSolution, outputSolution **NinesSolution, wg *sync.WaitGroup) {

	solution := NinesSolution{}
	rat := new(big.Rat)

	produce := func(result *big.Rat) {
		rational := comparableRational(result)
		solution[rational] = struct{}{}
		//fmt.Printf("Produced [%d, %d]\n", rational.numerator, rational.denominator)
	}
	ratA := new(big.Rat).SetFrac64(termA.numerator, termA.denominator)
	for b := range *solutionB {
		ratB := new(big.Rat).SetFrac64(b.numerator, b.denominator)

		produce(rat.Add(ratA, ratB))
		produce(rat.Mul(ratA, ratB))
		produce(rat.Sub(ratA, ratB))
		produce(rat.Sub(ratB, ratA))
		if ratB.Num().Int64() != 0 {
			produce(rat.Quo(ratA, ratB))
		}
		if ratA.Num().Int64() != 0 {
			produce(rat.Quo(ratB, ratA))
		}
	}
	*outputSolution = &solution
	wg.Done()
}

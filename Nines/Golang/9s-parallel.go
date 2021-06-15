package main

import (
	"fmt"
	"math/big"
	"os"
	"sync"
)

const n = 13

var solutionBroadcasters [n]chan *NinesSolution

func main() {

	for i := range solutionBroadcasters {
		solutionBroadcasters[i] = make(chan *NinesSolution)
	}

	for i := 0; i < n; i++ {
		solution := computeSolutionN(i + 1)
		i := i
		go func() {
			for {
				solutionBroadcasters[i] <- solution
			}
		}()
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

func computeSolutionN(n int) *NinesSolution {
	solution := make(NinesSolution)
	mux := sync.Mutex{}

	if n == 1 {
		solution[ComparableRational{9, 1}] = struct{}{}
		fmt.Println("Solution 1 Done, 1 solutions")
	} else {
		wg := sync.WaitGroup{}
		// n == 2 => middleSolution = 1
		// n == 3 => middleSolution = 2
		middleSolution := (n + 1) / 2
		wg.Add(middleSolution)
		for i := 1; i <= middleSolution; i++ {
			i := i
			go func() {
				defer wg.Done()

				// n == 2 => [1,1]
				// n == 3 => [0,1], [1,0]  means if i==1, [i-1,n-i-1]
				solution1 := <-solutionBroadcasters[i-1]
				solution2 := <-solutionBroadcasters[n-i-1]
				fmt.Printf("N=%d: Received solutions %d and %d\n", n, i, n-i)

				numSubSolutions := len(*solution1)
				subSolutions := make([]*NinesSolution, numSubSolutions)

				wg2 := sync.WaitGroup{}
				wg2.Add(numSubSolutions)

				j := 0
				for a := range *solution1 {
					go termOperationSolution(a, solution2, &subSolutions[j], &wg2)
					j++
				}
				wg2.Wait()

				for x := 0; x < numSubSolutions; x++ {
					mux.Lock()
					merge(&solution, subSolutions[x])
					mux.Unlock()
				}
			}()
		}
		wg.Wait()

		fmt.Println("Solution", n, " Done, ", len(solution), " solutions")
	}
	return &solution
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

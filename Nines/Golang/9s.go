package main

import (
	"math/big"
)

type NinesSolution map[ComparableRational]struct{}

var allSolutions = make(map[int]*NinesSolution)

func Nines(howMany int) *NinesSolution {

	if howMany == 1 {
		solution := make(NinesSolution)
		solution[ComparableRational{9, 1}] = struct{}{}
		allSolutions[howMany] = &solution
		return &solution
	}

	if solution, exists := allSolutions[howMany]; exists {
		return solution
	}

	rat := new(big.Rat)
	newSolution := make(NinesSolution)
	for i := 1; i <= howMany/2; i++ {

		solution1 := Nines(i)
		solution2 := Nines(howMany - i)

		for a := range *solution1 {
			ratA := new(big.Rat).SetFrac64(a.numerator, a.denominator)
			for b := range *solution2 {
				ratB := new(big.Rat).SetFrac64(b.numerator, b.denominator)

				newSolution[comparableRational(rat.Add(ratA, ratB))] = struct{}{}
				newSolution[comparableRational(rat.Mul(ratA, ratB))] = struct{}{}
				newSolution[comparableRational(rat.Sub(ratA, ratB))] = struct{}{}
				newSolution[comparableRational(rat.Sub(ratB, ratA))] = struct{}{}
				if ratB.Num().Int64() != 0 {
					newSolution[comparableRational(rat.Quo(ratA, ratB))] = struct{}{}
				}
				if ratA.Num().Int64() != 0 {
					newSolution[comparableRational(rat.Quo(ratB, ratA))] = struct{}{}
				}
			}
		}
	}

	allSolutions[howMany] = &newSolution
	return &newSolution
}

func comparableRational(rat *big.Rat) ComparableRational {
	return ComparableRational{rat.Num().Int64(), rat.Denom().Int64()}
}

type ComparableRational struct {
	numerator   int64
	denominator int64
}

package main

import (
	"fmt"
	"os"
)

func main() {
	solution9 := Nines(9)
	for actualSolution := 0; true; actualSolution++ {
		if _, exists := (*solution9)[ComparableRational{int64(actualSolution), 1}]; !exists {
			fmt.Println("The first integer for which there is no solution is", actualSolution)
			os.Exit(0)
		}
	}
	fmt.Printf("Nines: %s\n", ComparableRationalSetString(solution9))
}

func ComparableRationalSetString(nines *NinesSolution) []string {
	return MapFractString(nines, func(n *ComparableRational) string {
		return fmt.Sprintf("%d/%d", n.numerator, n.denominator)
	})
}

func MapFractString(coll *NinesSolution, f func(n *ComparableRational) string) []string {
	ret := make([]string, 0)
	for n := range *coll {
		ret = append(ret, f(&n))
	}
	return ret
}

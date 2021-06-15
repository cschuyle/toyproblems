package main

import (
	"fmt"
	"os"
)

func main() {
	solution9 := Nines(12)
	for actualSolution := 0; true; actualSolution++ {
		if _, exists := (*solution9)[ComparableRational{int64(actualSolution), 1}]; !exists {
			fmt.Println("The first integer for which there is no solution is", actualSolution)
			os.Exit(0)
		}
	}
	fmt.Printf("Nines: %s\n", ComparableRationalSetString(solution9))
}

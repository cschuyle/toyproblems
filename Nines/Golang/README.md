# Nine Nines in Go

My first program in Go

Uses the Go big.Rat class (rational numbers)

Did it parallel too (probably terrible concurrent Go ... go easy on me)


```
go test
go run 9s.go 9s_main.go
go clean
```

Compare sequential and parallel
```
go build 9s.go 9s_main.go
time ./9s

go build 9s-parallel.go 9s.go 
time ./9s-parallel
```

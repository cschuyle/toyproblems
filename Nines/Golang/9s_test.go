package main

import "testing"
import "reflect"

func Test1(t *testing.T) {
	nines := Nines(1)

	expected := NinesSolution{
		ComparableRational{9, 1}: {},
	}

	if !reflect.DeepEqual(*nines, expected) {
		t.Errorf("expected %v, got %v", ComparableRationalSetString(&expected), ComparableRationalSetString(nines))
	}
}

func Test2(t *testing.T) {
	nines := Nines(2)
	/*
		18 = 9+9
		0 = 9-9
		81 = 9*9
		1 = 9/9
	*/
	expected := NinesSolution{
		ComparableRational{0, 1}:  {},
		ComparableRational{1, 1}:  {},
		ComparableRational{18, 1}: {},
		ComparableRational{81, 1}: {},
	}

	if !reflect.DeepEqual(*nines, expected) {
		t.Errorf("expected %v, got %v", ComparableRationalSetString(&expected), ComparableRationalSetString(nines))
	}
}

func Test3(t *testing.T) {
	nines := Nines(3)
	/*
		27 = 18+9
		9 = 18-9
		-9 = 9-18
		162 = 18*9
		2 = 18/9
		1/2 = 9/18

		9 = 0+9
		-9 = 0-9
		9 = 9-0
		0 = 0*9
		0 = 0/9
		NaN = 9/0

		90 = 81+9
		72 = 81-9
		-72 = 9-81
		729 = 81*9
		9 = 81/9
		1/9 = 9/81

		10 = 1+9
		-8 = 1-9
		8 = 9-1
		9 = 1*9
		1/9 = 1/9
		0 = 0/1
	*/
	expected := NinesSolution{}
	for _, x := range []ComparableRational{
		{-8, 1},
		{-72, 1},
		{-9, 1},
		{0, 1},
		{1, 2},
		{1, 9},
		{1, 9},
		{10, 1},
		{162, 1},
		{2, 1},
		{27, 1},
		{72, 1},
		{729, 1},
		{8, 1},
		{9, 1},
		{90, 1},
	} {
		expected.Set(x)
	}

	if !reflect.DeepEqual(*nines, expected) {
		t.Errorf("expected %v, got %v", ComparableRationalSetString(&expected), ComparableRationalSetString(nines))
	}
}

func (s *NinesSolution) Set(rational ComparableRational) {
	(*s)[ComparableRational{rational.numerator, rational.denominator}] = struct{}{}
}

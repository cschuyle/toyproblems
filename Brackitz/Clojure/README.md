# brackitz

A coding challenge I got for an interview.  It was administered by
Hackerrank (on behalf of the actual interviewing company), but I
didn't submit this Cllojure version (I submitted it in Java 7 instead, 
yes chicken me).

The challenge is: Validate properly-bracketed strings.  A valid string 
must contain only balanced brackets of the () [] {} variety (I elect to 
ignore any characters besides the brackets - the condition is, any of 
the brackets must be properly balanced, but the string can contain any 
other characters.)

This has a bit of extra credit in it: It gives relatively good
validation error messages.

## Usage

Run the tests, or `lein run` and then enter lines at a time to get the
YES/NO answer as to whether the string is valid. 

## License

Copyright © 2016 Carlton Schuyler

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version. 

# differences

Various types of collection differences

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

## Definitions

### "symmetric difference"

* When the inputs are sets, the elements that occur in only one of the input sets:
`symmetric-difference [a b c] [b c d] = [a d]`

* When the inputs are multisets (or plain old sequences), elements from different inputs "cancel" each other out:
`symmetric-difference [a a b c] [b b c] = [a a b]`

The above extends to more than two input collections: The output consists of the elements that remain after recursively cancelling across inputs:
`symmetric-difference [a a b c] [b b c] [a b] = [a]`

One way to look at the meaning of this operation is: The elements that are most unique among the individuals in the input collections.

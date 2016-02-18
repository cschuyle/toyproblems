generations = {1: {9.0}}

for iteration in range(2,10):
    solutions = set()
    for subtree_index in range(1, iteration/2+1):
        subtree = generations[subtree_index]
        other_subtree = generations[iteration-subtree_index]
        for subtree_value in subtree:
            for other_subtree_value in other_subtree:
                solutions.add(subtree_value * other_subtree_value)
                solutions.add(subtree_value + other_subtree_value)
                solutions.add(subtree_value - other_subtree_value)
                solutions.add(other_subtree_value - subtree_value)
                if (other_subtree_value != 0): solutions.add(subtree_value / other_subtree_value)
                if (subtree_value != 0): solutions.add(other_subtree_value / subtree_value)
    generations[iteration] = solutions
last_gen = generations[9]
print "{} elements in generation 9".format(len(last_gen))
nat = 0
while True:
    if( not(float(nat) in last_gen)):
        print "The lowest natural number you did not generate for an arithmetic expression of nine 9's is {}".format(nat)
        break
    nat = nat + 1

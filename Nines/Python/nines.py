generations = {1: {9.0}}

up_to = 11
for iteration in range(2, up_to+1):
    solutions = set()
    for subtree_index in range(1, iteration/2 + 1):
        for a in generations[subtree_index]:
            for b in generations[iteration - subtree_index]:
                solutions.add(a * b)
                solutions.add(a + b)
                solutions.add(a - b)
                if(a != b): solutions.add(b - a)
                if(a != 0): solutions.add(b / a)
                if(b != 0 and a != b and a != -b): solutions.add(a / b)
    generations[iteration] = solutions
    print "{} elements in generation {}".format(len(solutions), iteration)
    nat = 0
    while True:
        if( not(float(nat) in solutions)):
            print "The lowest natural number you did not generate for an arithmetic expression of {} 9's is {}".format(iteration, nat)
            break
        nat = nat + 1

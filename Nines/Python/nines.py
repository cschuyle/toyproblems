
pair_solutions = {}
def pair_solution(a,b):
    if((a,b) in pair_solutions): return pair_solutions[(a,b)]
    solutions = []
    if(a == 0 or b == 0):
        solutions.append(0)
    elif(a == 1):
        solutions.append(b)
    elif(b == 1):
        solutions.append(a)
    else:
        solutions.append(a * b)

    if(a == 0):
        solutions.append(b)
    elif(b == 0):
        solutions.append(a)
    else:
        solutions.append(a + b)

    solutions.append(a - b)

    if(a != b):
        if(a != 0):
            solutions.append(b - a)
        if(b != 0 and b != 1 and a != -b):
            solutions.append(a / b)

    if(a != 0 and a != 1):
        solutions.append(b / a)

    frozen = tuple(solutions)
    pair_solutions[(a,b)] = frozen
    return frozen

generations = {1: {9.0}}
up_to = 11
for iteration in range(2, up_to+1):
    solutions = set()
    for subtree_index in range(1, iteration/2 + 1):
        for a in generations[subtree_index]:
            for b in generations[iteration - subtree_index]:
                solutions.update(pair_solution(a, b))
    generations[iteration] = solutions
    print "{} elements in generation {}".format(len(solutions), iteration)
    nat = 0
    while True:
        if( not(float(nat) in solutions)):
            print "The lowest natural number you did not generate for an arithmetic expression of {} 9's is {}".format(iteration, nat)
            break
        nat = nat + 1

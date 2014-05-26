#include <assert.h>
#include <algorithm>
#include <cmath>
#include <iostream>
#include <iterator>
#include <list>
#include <vector>
#include <set>

/*
 * Toy Problem: Nines
 *
 * This code Copyright (C) Carl Schuyler 2007.  This is based on Dave
 * Schulenburg's solution to the "nines" problem, which he implemented
 * in Lisp (but which I never read; we just talked about the
 * solution).
 *
 * Permission is granted to copy all or part of this code as long as
 * this and other acknowledgements are included in all copies and
 * derivative works. 
 *
 * Statement:
 *
 * What is the smallest positive integer which one cannot generate
 * using any combinations of the operators + - * / (and parentheses),
 * and exactly nine instances of the number 9?
 *
 * Solution:
 *
 * Memoize the set of possible values for the problem statement using
 * 1..9 9's.  At each "level" (1-9), use the previously-memoized:
 * Level 12 is the single number 9.  Level 2 uses level 1 and 1.
 * Level 3 uses levels 1 and 2.  Level 4 uses levels 1 and 3; 2 and 2;
 * and 3 and 1 (BUT only for non-associative operators of / and -).
 * In other words:
 *
 *   1: 9
 *   2: 9+9 9-9 9*9 9/9 
 *   3: [1,2]
 *   4: [1,3] [2,2] non-assoc only: [3,1]
 *   5: [1,4] [2,3] non-assoc only: [4,1] [2,3]
 *   (etc)
 * 
 * At the final level, the first natural number not present in the
 * list is the solution.
 *
 * Optimization: For the final level (9), throw out non-positive and
 * non-natural numbers (leaving only positive integers).
 * 
 * TODO:
 *   Need to have a rational number class, use it instead of double.
 *   Instead we are currently using doubles and a "good enough"
 *   less-than predicate for our domain.
 * 
 *   The data struct for the last list would be better a sequence of
 *   contiguous natural number ranges (i.e. the solution for level ==9
 *   (execute the main() with "-levels 9 -verbose" to illustrate why
 *   this is:) would be (1..194) (196..200) (202..211) ..(etc).. -
 *   this saves space and (I think) time for the final and (much)
 *   hardest "level".
 *
 *   Parallelize (ok for a code sample maybe this is pushing it
 *   ... Maybe base it on Boost and use a Boost-based rational number impl)
 *
 *   (think more about:) Data structs for the non-final list might
 *   also be optimized ...
 *
 */

using namespace std;

namespace CarlsNines {

  ////////////////////////////////////////////////////////////////
  // Definitions for sequence of unique values for each "level"
  
  // This is a good enough number close to 0 which makes me trustmy
  // comparisons for this domain, with sufficiently low values for
  // "level".  NOTE that we REALLY SHOULD use a rational number impl
  // istead of doubles, which would obviate the need for this hack.
  const double THRESHOLD = 1e-100;
  
  // Double less-than predicate (using THRESHOLD, see notes above)
  struct dblless {
    bool operator () (double _a, double _b) const {
      return (_b-_a > THRESHOLD);
    }
  };
  
  typedef set<double,dblless> NinesList;
  
  ////////////////////////////////////////////////////////////////
  // The machinery which adds "levels" to a NinesList.

  template<class T>
  void Augment (NinesList & list, T val, bool is_last = false) {
    // The special is_last flag is an optimization:  On the last level we needn't consider negatives or non-naturals
    if (is_last) {
      if (val <= THRESHOLD) return; // no negatives
      if (val - (long)val > THRESHOLD) return;  // no non-naturals
    }
    list.insert (val);
  }
  
  void AugmentAssoc (NinesList & list, NinesList const & first, NinesList const & second, bool is_last = false) {
    for (NinesList::const_iterator i = first.begin () ; i != first.end () ; ++i) {
      for (NinesList::const_iterator j = second.begin () ; j != second.end () ; ++j) {
	Augment (list, *i + *j, is_last);
	Augment (list, *i * *j, is_last);
      }
    }
  }
  
  void AugmentNonAssoc (NinesList & list, NinesList const & first, NinesList const & second, bool is_last = false) {
    for (NinesList::const_iterator i = first.begin () ; i != first.end () ; ++i) {
      for (NinesList::const_iterator j = second.begin () ; j != second.end () ; ++j) {
	Augment (list, *i - *j, is_last);
	Augment (list, *i / *j, is_last);
      }
    }
  }
  
  void Augment (NinesList & list, NinesList const & first, NinesList const & second, bool is_last = false) {
    AugmentAssoc (list, first, second, is_last);
    AugmentNonAssoc (list, first, second, is_last);
  }
  
  ////////////////////////////////////////////////////////////////
  
  // The Problem Solver Implementation
  
  class Nines {
  public:
    friend ostream & operator<<(ostream & out, Nines const & impl);
  protected:
    typedef NinesList SET;
    typedef vector<SET *> IterList;
  private:
    IterList _iter_list;
    double _seed;
  public:
    Nines (double seed = 9.) {
      _seed = seed;
    }
    void Init (SET & init_list) {
      _iter_list.clear ();
      _iter_list.push_back (&init_list);
    }
  public:
    SET const & operator[] (unsigned int i) const {
      return *(_iter_list[i]);
    }
    int level (void) const {
      return _iter_list.size ();
    }
  public:
    void Iter (bool is_last = false) {
      const int which_level = level ();
      const int top = which_level/2;
      
      SET * new_iter = new SET ();
      _iter_list.push_back (new_iter);
      
      for (int i = 1 ; i <= top ; ++i) {
	SET const & first = *(_iter_list[i-1]);     // 0, 1 for which_level==4
	SET const & second = *(_iter_list[which_level-i]); // 3, 2 for which_level==4
	Augment (*new_iter, first, second, is_last);
	// Now do the non-associative ones in the other permutation ( 1+2, 1*2, 1-2, 1/2, [[ THESE: 2-1, 2/1 ]] )
	AugmentNonAssoc (*new_iter, second, first, is_last);
      }
      // If odd number of iters, do the middle one
      if (0 != which_level % 2) {
	SET const & mid = *(_iter_list[top]);
	Augment (*new_iter, mid, mid, is_last);
      }
    }
    void Solve (int iterations, ostream & out, bool do_is_last = true) {
      Nines & me = *this;
      NinesList init_list;
      init_list.insert (_seed);
      me.Init (init_list);
      for (;;) {
	const int i = me.level();
	if (iterations == i) break;
	bool is_last = (iterations == i+1 && do_is_last);
	char const * msg = "";
	if (is_last) {
	  msg =  " (Optimizing for last iteration)";
	}
	cout << i+1 << " " << _seed << "'s ..." << msg << endl;
	me.Iter (is_last);
      }
      NinesList const & soln = me[me.level()-1];
      //  cout << soln;
      
      // Find the solution in the last iter
      
      int counter = 1; 
      NinesList::const_iterator i_got = soln.begin ();
      for( ; i_got != soln.end () ; ) {
	// Consider only positive integers - in case the "IsLast
	// optimization" is off.
	const double got = *i_got;
	if (got <= 0) { 
	  ++i_got; 
	  continue; 
	}
	double int_part = 0;
	double frac_part = modf(got, &int_part);
	if (frac_part > THRESHOLD) {
	  ++i_got;
	  continue;
	}
	
	// If this elt is NOT the counter I'm keeping, this is the answer.
	if (abs(counter - got) > THRESHOLD) {
	  out << "SOLUTION " << counter << endl;
	  break;
	}
	++counter;
	++i_got;
      }
    }
    
  };
  
  // Outputters
  
  ostream & operator<< (ostream & out, NinesList const & list) {
    ostream_iterator<double> outter (out, ",");
    copy (list.begin(), list.end(), outter);
    out << "__END LIST__";
    return out;
  }

  ostream & operator<< (ostream & out, Nines const & impl) {
    int i = 1;
    for (Nines::IterList::const_iterator level = impl._iter_list.begin () ; level != impl._iter_list.end () ; ++level, ++i) {
      out << "Level #" << i << ": " << (**level) << endl;
    }
    out << "__END NINES__" << endl;
    return out;
  }
}

////////////////////////////////////////////////////////////////

using namespace CarlsNines;

// Tests

void TestEnsure (void) {
  NinesList list;
  list.insert(2);
  list.insert(1);
  list.insert(4);
  list.insert(5);
  list.insert(3);
  list.insert(1);
  list.insert(2);
  cout << "Should be 1,2,3,4,5 [" << list << ']' << endl;
}

void TestAugment (void) {
  NinesList one;
  one.insert (9.);
  NinesList list;
  Augment (list, one, one);
  cout << "Should be 0,1,18,81 [" << list << ']' << endl;
}

void Testdblless (void) {
  // Blecch, use C++ assert
  assert (dblless() (0.1, 0.2));
  assert (!dblless() (0.1, 0.1));
  assert (!dblless() (0.2, 0.1));
}

void TestAll (void)
{
  Testdblless ();
  TestEnsure ();
  TestAugment ();
}

////////////////////////////////////////////////////////////////
// Main

int main (int argc, char *argv[]) {
  bool do_is_last = true;
  int levels = 9;
  bool do_test = false;
  bool verbose = false;
  double seed = 9.;

  // USAGE: nines [options]
  //    -verbose Output lots of informational and diagnostic text.
  //    -noopt   Do NOT do IsLast optimization.
  //    -seed    "Seed" value (default: 9.0)
  //    -levels  Number of nines to find solution for (default 9).
  //    Unrecognized options cause test mode to run.

  for (int i = 1; i < argc ; ++i) {
    if (strncmp (argv[i], "-noopt", 6) == 0) {
      do_is_last = false;
    }
    else if (strncmp (argv[i], "-verbose", 8) == 0) {
      verbose = true;
    }
    else if (strncmp (argv[i], "-seed", 5) == 0) {
      ++i;
      if (i<argc) {
	seed = atof(argv[i]);
      }
    }
    else if (strncmp (argv[i], "-levels", 7) == 0) {
      ++i;
      if (i<argc) {
	levels = atoi(argv[i]);
      }
    }
    else {
      cout << "Unrecognized option " << argv[i] << endl;
      do_test = true;
    }
  }

  if (do_test) {
    cout << "TESTING ..." << endl;
    TestAll ();
    exit (0);
  }
  cout << "  SEED: " << seed 
       << ", LEVELS: " << levels 
       << ", OPTIMIZE: " << do_is_last
       << ", VERBOSE: " << verbose 
       << endl;
  Nines nines (seed);
  nines.Solve (levels, cout, do_is_last);
  if (verbose) {
    cout << "Final level list: " << nines[levels-1] << endl;
  }
}

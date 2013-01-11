# The Nines Problem

require './btree.rb'
require 'mathn'  # Auto-rationalize when divide

class Nines

  attr_reader :stashed_nines, :opers

  def initialize
    @stashed_nines = {}
    @opers = 0
  end

  # add *what to the list of results for expression containing n 9's
  def stash(n,*what)
  warn "Creating Stash for Expression Order #{n}" if !@stashed_nines.has_key? n
    @stashed_nines[n] ||= Btree.new
    what.each { |i| @stashed_nines[n] << i if !@stashed_nines[n].contains?(i) }
  end

  def stash_count
    ret=0
    @stashed_nines.values.each do |arr|
      ret += arr.count
    end
    ret
  end

  def stashed?(n)
    return true if @stashed_nines[n]
    return false
  end

  # Compute full-product using operation <m> of two lists
  def oper(m, l1, l2)
    l1.each do |i1|
      l2.each do |i2|
	      @opers += 1
	      yield(i1.send m, i2)
      end
    end
  end

  def oper_with_catch(m, l1, l2)
    l1.each do |i1|
      l2.each do |i2|
	      @opers += 1
	      yield(i1.send m.to_sym, i2) unless 0 == i2
      end
    end
  end

PLUS = '+'.to_sym
MINUS = '-'.to_sym
TIMES = '*'.to_sym
DIVIDED_BY = '/'.to_sym

  def add(l1,l2,&block);      oper(PLUS,l1,l2,&block); end
  def multiply(l1,l2,&block); oper(TIMES,l1,l2,&block); end
  def subtract(l1,l2,&block); oper(MINUS,l1,l2,&block); end
  def divide(l1,l2,&block);   oper_with_catch(DIVIDED_BY,l1,l2,&block); end

  # Compute the nines for expression order n.  Requires stashed_nines to have been computed for 1..n-1.  Must pass block.  There will be duplicates yielded, both rational and integer, both negative and positive, yielded values will not be in order.
  def compute_nines(n)
    raise "Must pass block" if !block_given?
    # end-recursion case
    if n==1
      yield 9
    else
      top=(n/2).to_i
      for i in 1..top
#	warn "#{i} OPER #{n-i}"

        add(       stashed_nines[i]   , stashed_nines[n-i] ) { |x| yield x }
        multiply(  stashed_nines[i]   , stashed_nines[n-i] ) { |x| yield x }
        subtract(  stashed_nines[i]   , stashed_nines[n-i] ) { |x| yield x }
        divide(    stashed_nines[i]   , stashed_nines[n-i] ) { |x| yield x }
        subtract(  stashed_nines[n-i] , stashed_nines[i] )   { |x| yield x } if n-i != i
        divide(    stashed_nines[n-i] , stashed_nines[i] )   { |x| yield x } if n-i != i
      end
    end # if
  end # def

  # Top-level method.  Compute solution for expression order n.
  def nines(n)
    # compute the smaller cases
    for i in 1..n-1
      if !stashed_nines[i]
        compute_nines(i) { |x| stash(i,x) }
      end
    end

    warn "Computing final solution for Expression Order #{n}"
    arr=Btree.new
    compute_nines(n) do |x|
      next if x < 0
      next if (!x.kind_of?(Fixnum)) && x.denominator != 1
      next if arr.contains? x
      arr << x
    end
    return arr
  end

end

if __FILE__ == $0
  $stdout.sync=$stderr.sync=true
  nines=Nines.new
  n = ARGV[0].to_i
  n = 9 if n == 0
  nines.nines(n).inorder.each_with_index do |a,i| 
    if i != a
      puts "solution: #{a-1}"
      puts "#{nines.opers} operations performed, #{nines.stash_count} values stashed"
      break
    end
  end
end


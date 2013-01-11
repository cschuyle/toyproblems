class Btree
  attr_reader :v
  def initialize(v=nil)
    @left = @right = nil
    @v = v
  end

  def insert(v)
    if @v.nil?
      @v=v; return
    end
    return if @v==v
    if v<@v
      if @left.nil?
        @left = Btree.new(v)
      else
        @left.insert(v)
      end
    else
      if @right.nil?
        @right = Btree.new(v)
      else
        @right.insert(v)
      end
    end
  end

  def <<(*arr)
    arr.each { |i| insert(i) }
  end

  def contains?(v)
    return false if @v.nil?
    return true if @v==v
    return @left.contains?(v) if !@left.nil? && v < @v
    return @right.contains?(v) if !@right.nil? && v > @v
    return false
  end


  def inorder(&block)
    if block_given?
      @left.inorder &block if @left
      yield @v if @v
      @right.inorder &block if @right
    else
      arr = []
      if @left
        @left.inorder.each { |i| arr << i }
      end
      arr << @v if @v
      if @right
        @right.inorder.each { |i| arr << i }
      end
      return arr
    end
  end

  def each(&block)
    if block_given?
      inorder.each { |i| yield i }
    end
  end

  def to_arr; inorder; end

  def count
    c=0
    inorder { c += 1 }
    c
  end

end


if __FILE__ == $0
	t=Btree.new
	for i in 1..3 
	  t.insert(i)
	end
	warn t.inorder.collect.join(',')# |i|

	t2=Btree.new
	for i in [3, 2, 1, 3]
	  t2.insert(i)
	end
	warn t2.inorder.collect.join(',')# |i|
	warn "t2 contains 2 = #{t2.contains?(2)}"
	warn "t2 contains 5 = #{t2.contains?(5)}"
	warn "t2 contains 0 = #{t2.contains?(0)}"

end

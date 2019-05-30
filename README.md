# 2PhaseMultiWayMergeSort
Jumbo Files Sorting Algorithm

The sorting algorithm is the Two-Phase Multiway Merge-Sort with possibly several rounds of
merging in Phase 2 and based on the given input information the algorithm determines the
best buffer size before it starts the actual sorting.

The input file is given as a text-file in which 
each line of the file is a positive integer (with possibly duplicated values), where each integer is a
tuple. 
The algorithm compare the tuples in the list and outputs to a file the same list where its
tuples are sorted in ascending order. 
The first line of the input file will indicate the number of tuples
in it and the amount of allowed maximum main memory. 
The second line of the input file is blank
and from the third line the tuples start.


Example
1000000 5mb
111111 234566 22 ... ...

Another src file should be run prior to the main algorithm to generate the required input file which is then given as input to the tpmms algorithm.

The Main memory limitation can be set in Arguments section of Projects run configurations as Example:- "-Xmx5m  for 5 mb main memory limitation"

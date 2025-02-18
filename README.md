# ForkJoinTest
repository for a Fork&amp;Join lab session of concurrent programming 
This is the parallel version of a sequential search algorithm, using the fork/join model. The goal of the search is finding a goal inside a maze taking advantage of parallelism.

#Compile your solution:

make compile

#Run sequentialism on map small

make sequential_small

#Run parallelism on map small, with forkAfter equal to 3 or to 9:

make parallel_small_step3

make parallel_small_step9

#Run parallelism on map medium, with forkAfter equal to 3 or to 9:

make parallel_medium_step3

make parallel_medium_step9

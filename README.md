# ForkJoinTest
repository for a Fork&amp;Join lab session of concurrent programming 
This is the parallel version of a sequential search algorithm, using the fork/join model. The goal of the search is finding a goal inside a maze taking advantage of parallelism.

#Authors: 

Zhuangzhuang Gong, Ergi Senja

#Description:

We designed and implemented a parallel version of a sequential search algorithm using the Fork/Join. The goal of the search is to find a goal inside a maze taking advantage of parallelism.

#How we implement:

To implement the method, we have to acknowledge that the compute() function is part of the Fork/Join framework, and it recursively invokes fork() and join() to execute subtasks in parallel.

#The key logic in  parallelSearch() of compute():

We start with an initial note and explore the maze by moving the player to the top node in the frontier list. 

With each move, the player will check the neighboring nodes:

If the player is not reaching the goal, we let the current thread keep exploring via the first neighbor (save the cost by avoiding extra forks), and then we fork the other neighbors. 

Once the player reaches the goal, we terminate and return the path of the recursion. At the same time, all parallel forks will be terminated by the hasFoundGoal flag and traced back to their child fork to merge the path. 


#Shared information:

To ensure the plays only explore new nodes in the maze instead of the visited path, we used a thread-safe data structure, ConcurrentSkipListSet, to store all visited notes.

To store and trace all threads, we used a CopyOnWriteArrayList, a thread-safe data structure, to store all forks.

Once a thread finds the goal, all threads need to terminate and start to join other threads. So we used an AtomicBoolean to record this state.



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



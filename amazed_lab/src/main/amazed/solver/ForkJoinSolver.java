package amazed.solver;

import amazed.maze.Maze;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * <code>ForkJoinSolver</code> implements a solver for
 * <code>Maze</code> objects using a fork/join multi-thread
 * depth-first search.
 * <p>
 * Instances of <code>ForkJoinSolver</code> should be run by a
 * <code>ForkJoinPool</code> object.
 */


public class ForkJoinSolver
    extends SequentialSolver
{
    private AtomicBoolean hasFoundGoal = new AtomicBoolean(false);
    //public ArrayList<ForkJoinSolver> forklist = new ArrayList<>();
    public List<ForkJoinSolver> forklist = new CopyOnWriteArrayList<>();
    
    /**
     * Creates a solver that searches in <code>maze</code> from the
     * start node to a goal.
     *
     * @param maze   the maze to be searched
     */
    public ForkJoinSolver(Maze maze)
    {
        super(maze);
        this.visited = new ConcurrentSkipListSet<>();
    }

    /**
     * Creates a solver that searches in <code>maze</code> from the
     * start node to a goal, forking after a given number of visited
     * nodes.
     *
     * @param maze        the maze to be searched
     * @param forkAfter   the number of steps (visited nodes) after
     *                    which a parallel task is forked; if
     *                    <code>forkAfter &lt;= 0</code> the solver never
     *                    forks new tasks
     */
    public ForkJoinSolver(Maze maze, int forkAfter)
    {
        this(maze);
        this.forkAfter = forkAfter;
    }

   /**
   * Creates a solver that searches in <code>maze</code> from the
   * start node to a goal.
   * initialises the start node, visited set and hasFoundGoal
   * @param maze   the maze to be searched
   * @param start    start node
   * @param visited   visited set of nodes
   * @param hasFound  hasFoundGoal the termination condition
   */
    public ForkJoinSolver(Maze maze, int start, Set<Integer> visited, AtomicBoolean hasFound)
    {
        this(maze);
        this.start = start;
        this.visited = visited;
        this.hasFoundGoal = hasFound;
    }

    /**
     * Searches for and returns the path, as a list of node
     * identifiers, that goes from the start node to a goal node in
     * the maze. If such a path cannot be found (because there are no
     * goals, or all goals are unreacheable), the method returns
     * <code>null</code>.
     *
     * @return   the list of node identifiers from the start node to a
     *           goal node in the maze; <code>null</code> if such a path cannot
     *           be found.
     */
    @Override
    public List<Integer> compute()
    {
        return parallelSearch();
    }

    private List<Integer> parallelSearch()
    {
       // one player active on the maze at start
       frontier.push(start);
       // terminate when goal is found or frontier is empty (i.e., no more nodes to visit)
       while (!hasFoundGoal.get() && !frontier.isEmpty()) {
           int player = maze.newPlayer(start);
           int current = frontier.pop();
           //if node is visited, go to next 
           if (visited.contains(current)) {
               continue;
           }
           //move the player to the current node
           maze.move(player, current);
           //mark the current node as visited
           visited.add(current);
           // if current node is a goal, return the path from start to goal
           if (maze.hasGoal(current)) {
               hasFoundGoal.set(true);
               return pathFromTo(start, current);
           }
           boolean firstnode = true;
           for (int nb : maze.neighbors(current)) {
               if (!visited.contains(nb)) {
                   //if it is the first node in the list, we add it to the frontier and continue to moving, instead of forking
                   if (firstnode) {
                       frontier.push(nb);
                       firstnode = false;
                   } else {
                       ForkJoinSolver fjs = new ForkJoinSolver(maze, nb, visited, hasFoundGoal);
                       forklist.add(fjs);
                       fjs.fork();
                   }
                   //record path between current and next ppsition
                   predecessor.put(nb, current);
               }
           }
       }
       //once stepout from the 'while' join all threads
       List<Integer> pathToGoal = null;
       List<Integer> pathToJoin = null;
       for (ForkJoinSolver fjs : forklist) {
           List<Integer> tmp = fjs.join();
           if (tmp != null) {
               pathToGoal = tmp;
           }
       }
       //if a path to goal is found, we join the path from start to the goal
       //check the pathToGoal, excluside the empty node
       if (pathToGoal != null && !pathToGoal.isEmpty()) {
           //join current "start node" to the first node in the path to goal. notice remove the first node otherwise it would be joined twice
           pathToJoin = pathFromTo(start, pathToGoal.remove(0));
           pathToJoin.addAll(pathToGoal);
       }
       return pathToJoin;
    }
}

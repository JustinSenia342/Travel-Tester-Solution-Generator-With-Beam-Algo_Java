/*
 * Name: Justin Senia
 * E-Number: E00851822
 * Date: 10/20/2017
 * Class: COSC 461
 * Project: #2
 */
import java.util.LinkedList;
import java.io.*;
import java.util.*;

//this program solves travelling salesman problem with Beam algorithm
public class TravelBeam
{
  private int BEAMSIZE = 0;
	
  //Path class (inner class)
  private class Path
  {
    private LinkedList<Integer> list;	//vertices in a path
    private int cost;					//cost of a path

    //constructor of path class
    private Path()
    {
      list = new LinkedList<Integer>();	//empty list of vertices
      cost = 0;							//cost is 0
    }

    //Copy constructor
    private Path(Path other)			
    {
      list = new LinkedList<Integer>();				//empty list of vertices

      for (int i = 0; i < other.list.size(); i++)	//copy vertices to list
        list.addLast(other.list.get(i));			

      cost = other.cost;							//copy cost
    }

    //method adds vertex to path
    private void add(int vertex, int weight)
    {
      list.addLast(vertex);							//add vertex at the end
      cost += weight;								//increment cost
    }

    //Method finds last vertex of path
    private int last()
    {
      return list.getLast();						//returns last vertex
    }

    //Method finds cost of path
    private int cost()								
    {
      return cost;									//return cost
    }

    //Method finds length of path
    private int size()
    {
      return list.size();							//return length
    }

    //Method decides whether path contains a given vertex
    private boolean contains(int vertex)
    {
      for (int i = 0; i < list.size(); i++)			//compare vertex with
        if (list.get(i) == vertex)					//vertices of path
          return true;

      return false;
    }

    //Method displays path and it's cost
    private void display(long timeElapsed)
    {
	  System.out.println("Beam Size: " + BEAMSIZE);
	  pW.println("Beam Size: " + BEAMSIZE);
	  
	  System.out.println("\n");
		
	  System.out.print("Cycle: ");
	  pW.print("Cycle: ");
      for (int i = 0; i < list.size(); i++)				//print path in file & output
	  {
		//re-incrementing data so each vert has it's original "name"
		//done because vert names were decremented for ease of use in arrays
        System.out.print((list.get(i)+1) + " ");		
		pW.print((list.get(i)+1) + " ");
      }
	  System.out.println("\n");
	  
	  System.out.println("Miles: " + cost);				//print cost in output
	  pW.println("Miles: " + cost);						//print cost in file
	  
	  System.out.println("");
	  
	  //print time taken in output and file
	  System.out.println("Time: " + timeElapsed + " Milliseconds");
	  pW.println("Time: " + timeElapsed + " Milliseconds");
	  
	  System.out.println("\n");
	  pW.println("\n");
    }
  }

  private int size;
  private int[][] matrix;
  private int initial;
  private PrintWriter pW;
  

  //Constructor of TravelBeam class
  public TravelBeam(int vertices, int[][] edges, PrintWriter PWOut)
  {
    size = vertices;								//assign vertices
    this.pW = PWOut;

    matrix = new int[size][size];					//initialize adjacency matrix
    for (int i = 0; i < size; i++)
      for (int j = 0; j < size; j++)
        matrix[i][j] = 0;

    for (int i = 0; i < edges.length; i++)
    {
      int u = edges[i][0];							//place weights in adjacency
      int v = edges[i][1];							//matrix using edges
      int weight = edges[i][2];
      matrix[u][v] = weight;
      matrix[v][u] = weight;
    }
	
    initial = edges[0][0];							//pick a starting/ending vertex
  }

  //Method finds shortest cycle
  public void timedSolve(int bSize)
  {
	BEAMSIZE = bSize;
	  
	//declares and initializes temp variable to store total time taken
	long totTime = 0;
	  
	//makes note of initial time before algorithm begins
    long startTime = System.currentTimeMillis();
	  
    LinkedList<Path> openList = new LinkedList<Path>(); 	//creation of new openlist
    LinkedList<Path> closedList = new LinkedList<Path>();	//creation of new closedlist

    Path shortestPath = null;						//initialize shortest cycle
    int minimumCost = Integer.MAX_VALUE;			//and minimum cost

    Path path = new Path();
    path.add(initial, 0);							//creating initial path and
    openList.addFirst(path);						//adding to openlist

    while (!openList.isEmpty())						//while openList still has paths
    {
      path = openList.removeFirst(); 				//remove first from openlist
	  
	  closedList.addLast(path);						//add to closedList to mark as used

      if (complete(path))							//if path is cycle
      {
        if (path.cost() < minimumCost)				//current path is less than previously
        {											//found most efficient path (cost wise)
          minimumCost = path.cost();				//update new minimum cost to current path
          shortestPath = path;						//update new shortest path to current path
        }
      }
      else											//else if path is not complete
      {
		  LinkedList<Path> children = generate(path);	//generate children of path
		  
			for (int i = 0; i < children.size() && i < BEAMSIZE; i++)		
				openList.addFirst(children.get(i));	 		//add at most Beamsize children 
															//to beginning of list
      }
    }

	//makes note of end time after solve has completed
    long endTime = System.currentTimeMillis();
	
	//calculates total time taken
	totTime = endTime - startTime;
	
    if (shortestPath == null)								//if there is no cycle
	{
      System.out.println("no solution");					//there is no solution
	  pW.println("no solution");
	}
    else
      shortestPath.display(totTime);						//otherwise display shortest cycle
															//and associated info (& write to file)
  }

  //Method generates children of path
  private LinkedList<Path> generate(Path path)
  {
    LinkedList<Path> children = new LinkedList<Path>();		//children list

    int lastVertex = path.last();							//find last vertex of path

    for (int i = 0; i < size; i++)							//go thru all verticies
    {
      if (matrix[lastVertex][i] != 0)						//if vertex has neighbor
      {
        if (i == initial)									//if vertex is initial vertex
        {
          if (path.size() == size)							//if path has size vertices
          {
            Path child = new Path(path);					//create copy of path

            child.add(i, matrix[lastVertex][i]);			//add vertex to path

            children.addLast(child);						//add extended path to children list
          }
        }
        else												//if vertex is not initial vertex
        {
          if (!path.contains(i))							//if vertex is not in path
          {
            Path child = new Path(path);					//create a copy of path

            child.add(i, matrix[lastVertex][i]);			//add extended path to

            children.addLast(child);						//children list
          }
        }
      }
    }

    return children;										//return children list
  }

  //Method decides whether a path is complete
  boolean complete(Path path)
  {
    return path.size() == size + 1;							//check if path has size+1
  }															//number of vertices
  
}

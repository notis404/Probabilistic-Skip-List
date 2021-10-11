// Nathan Otis 
// NID: na287158
// COP 3503, Spring 2021
// SkipList Implementation

import java.util.ArrayList;

// Class implementation of Skip List nodes
class Node<AnyNodeType>
{
	
	private int height;
	private AnyNodeType data;

	// ArrayList to store the pointers for a given node
	private ArrayList<Node<AnyNodeType>> links = new ArrayList<Node<AnyNodeType>>(); 
	
	// Node constructor given only a height parameter, useful for created the head node
	Node(int height)
	{
		this.height = height;
		for (int i = 0; i < height; i++)
		{
			links.add(null);
		}
	}
	
	// Node constuctor given data and a node height
	Node(AnyNodeType data, int height)
	{
		this.height = height;
		this.data = data;
		for (int i = 0; i < height; i++)
		{
			links.add(null);
		}
	}
	
	// Method to return the value stored in the node
	public AnyNodeType value()
	{
		return data;
	}
	
	// Method to return the current height of the node
	public int height()
	{
		return height;
	}
	
	// Method that returns the node that the pointer at some integer 
	// "level" in the ArrayList is pointing to
	public Node<AnyNodeType> next(int level)
	{
		// Enter if the level provided is valid given the height of this node
		if (level >= 0 && level <= (height - 1))
		{
			// Checks if the ArrayList size needs to be incremented before accessing
			if (level < links.size())
			{
				return links.get(level);
			}
			else
			{
				// If the ArrayList size needs to be incremented,
				// add null pointers until the size is appropriate
				while (level > links.size() - 1)
				{
					links.add(null);
				}
				return links.get(level);
			}
			
		}
		
	// Return null if the level provided is invalid
	return null;	
	}
	
	// Method to set the pointer at a specified level to a specified node
	public void setNext(int level, Node<AnyNodeType> node)
	{
		// Checks if the ArrayList size needs to be incremented before accessing
		if (level < links.size())
		{
			links.set(level, node);
		}
		else 
		{
			// If the ArrayList size needs to be incremented,
			// add null pointers until the size is appropriate
			while (level > links.size() - 1)
			{
				links.add(null);
			}
			links.set(level, node);
		}
	}
	
	// Method to increment the height of a node by 1
	public void grow()
	{
		links.add(null);
		height++;
	}
	
	// Method to potentially (50% chance) increment the height of a node by 1
	public int maybeGrow()
	{
		// Only enters 50% of the time
		if ((int)(Math.random() * 2) == 1)
		{
			links.add(null);
			height++;
			return 1;
		}
		return 0;
	}
	
	// Method to trim the node down to the given height
	public void trim(int height)
	{
		// Loop through the Node's ArrayList, removing the pointers between
		// the previous node height and the new height 
		for (int i = this.height - 1; i >= height; i--)
		{
			links.remove(i);
		}
		this.height = height;
	}
}

// Public class implementation of a SkipList
public class SkipList<AnyType extends Comparable<AnyType>>
{
	private int height;
	private int size;
	private int maxHeight;
	private Node<AnyType> head;
	
	// ArrayList used to store "breadcrumbs" of pointers when inserting or deleting nodes
	private ArrayList<Node<AnyType>> breadCrumbs = new ArrayList<Node<AnyType>>();
	
	
	// Default Construtor 
	SkipList()
	{
		this.height = 1;
		head = new Node<AnyType>(1);
		this.size = 0;
	}
	
	// Constructor that takes creates a SkipList with a given height
	SkipList(int height)
	{
		
		if (height > 0)
		{		
			this.height = height;
			head = new Node<AnyType>(height);
		}
		else
		{
			this.height = 1;
			head = new Node<AnyType>(1);
		}
		this.size = 0;
	}
	
	// Method to return the size of the SkipList
	public int size()
	{
		return size;
	}
	
	// Method to return the height of the SkipList
	public int height()
	{
		return height;
	}
	
	// Method to return the head of the SkipList
	public Node<AnyType> head()
	{
		return head;
	}
	
	// Method to insert a node with specific data into the SkipList
	public void insert(AnyType data)
	{
		int level = height - 1;
		Node<AnyType> cur, next;
		cur = head;
		
		// Loop while the current level is positive
		while (level >= 0)
		{
			next = cur.next(level);
			
			// If the next value is null or is greater than or equal to data, decrement the level
			if (next == null || next.value().compareTo(data) >= 0)
			{
				// Checks if ArrayList breadCrumbs' size needs to be incremented before accessing
				if (level < breadCrumbs.size())
				{
					breadCrumbs.set(level, cur);
				}
				else
				{	
					// If the ArrayList size does need to be incremented
					// insert null values until the value can be set
					while (level > breadCrumbs.size() - 1)
					{
						breadCrumbs.add(null);
					}
					breadCrumbs.set(level, cur);
				}
				level--;
				
				// If decrementing the level caused it to become negative, place the node
				if (level < 0)
				{
					// Check if either the current height or the calculated maxHeight is
					// greater and use that value as the maximum height
					if (height < getMaxHeight(size))
					{
						maxHeight = getMaxHeight(size);
						height = maxHeight;
					}
					else 
					{
						maxHeight = height;
					}
					
					// Generate a random height for the new node
					int newHeight = generateRandomHeight(maxHeight);
					Node<AnyType> newNode = new Node<AnyType>(data, newHeight);
					
					// Use the stored pointers in the breadCrumbs ArrayList to hook
					// the new node up
					for (int i = 0; i < newHeight; i++)
					{
						newNode.setNext(i, breadCrumbs.get(i).next(i));
						breadCrumbs.get(i).setNext(i, newNode);
					}
					
					// Increment the SkipList size
					size++;
					
					// Check if incrementing the SkipList size causes a need to
					// grow the height of the SkipList
					if (height < getMaxHeight(size))
					{
						growSkipList();
						height = getMaxHeight(size);
					}
				}
			}
			// If the next value is less than data, move the current node to the next node
			else if (next.value().compareTo(data) < 0)
			{
				cur = next;
			}
		}
	}
	
	// Method to insert a node with specific data and a specific height into the SkipList
	public void insert(AnyType data, int height)
	{
		int level = this.height - 1;
		Node<AnyType> cur, next;
		cur = head;
		
		// Loop while the current level is positive
		while (level >= 0)
		{
			next = cur.next(level);
			
			// If the next value is null or is greater than or equal to zero, decrement the level
			if (next == null || next.value().compareTo(data) >= 0)
			{ 
				// Checks if ArrayList breadCrumbs' size needs to be incremented before accessing
				if (level < breadCrumbs.size())
				{
					breadCrumbs.set(level, cur);
				}
				else
				{
					// If the ArrayList size does need to be incremented
					// insert null values until the value can be set
					while (level > breadCrumbs.size() - 1)
					{
						breadCrumbs.add(null);
					}
					breadCrumbs.set(level, cur);
				}
				
				// Decrement the current level
				level -= 1;
				
				// If decrementing the level caused it to become negative, place the node at this location
				if (level < 0)
				{
					// Create a new node with the given data and height
					Node<AnyType> newNode = new Node<AnyType>(data, height);
					size++;
					
					// Check if incrementing the SkipList size causes a need to
					// grow the height of the SkipList 
					if (this.height < getMaxHeight(size))
					{
						growSkipList();
						this.height = getMaxHeight(size);
					}
					
					// Use the stored pointers in the breadCrumbs ArrayList to hook
					// the new node up
					for (int i = 0; i < height; i++)
					{
						newNode.setNext(i, breadCrumbs.get(i).next(i));
						breadCrumbs.get(i).setNext(i, newNode);
					}
				}
			}
			
			// If the next value is less than data, move the current node to the next node 
			else if (next.value().compareTo(data) < 0)
			{
				cur = next;
			}
		}
	}
	
	// Method to delete the first instance of a node with the given data
	public void delete(AnyType data)
	{
		int level = height - 1;
		int foundFlag = 0;
		Node<AnyType> cur, next;
		cur = head;
		
		// Loop while the current node is non-null and the current level is positive
		while (cur != null && level >= 0)
		{
			next = cur.next(level);
			
			// If next is non-null and the next value is equal to data, decrement the level
			// and store a breadCrumb
			if (next != null && next.value().compareTo(data) == 0)
			{
				// Checks if ArrayList breadCrumbs' size needs to be incremented before accessing
				if (level < breadCrumbs.size())
				{
					breadCrumbs.set(level, cur);
				}
				else
				{
					// If the ArrayList size does need to be incremented
					// insert null values until the value can be set
					while (level > breadCrumbs.size() - 1)
					{
						breadCrumbs.add(null);
					}
					breadCrumbs.set(level, cur);
				}
				
				// Decrement the current level
				level -= 1;
				
				// If decrementing the current level caused it to become negative
				// delete the next node
				if (level < 0)
				{
					// Use the pointers stored in the breadCrumbs ArrayList to 
					// disconnect the next node from the SkipList
					for (int i = 0; i < next.height(); i++)
					{
							breadCrumbs.get(i).setNext(i, next.next(i));			
					}
					
					// Mark that a node was deleted 
					foundFlag = 1;
				}
			}
			
			// If the next node is null or if the next value is greater than the data value,
			// decrement the level
			else if (next == null || next.value().compareTo(data) > 0)
			{
				level--;
			}
			// If the next node is non-null and the next value is less than the data value,
			// set the current node equal to the next node
			else if (next != null && next.value().compareTo(data) < 0)
			{
				cur = next;
			}
		}
		
		// If a node was deleted, decrement the SkipList size and check if the 
		// new size forces the SkipList to be trimmed
		if (foundFlag == 1)
		{
			size--;
			if (height > 1 && height > getMaxHeight(size))
			{
				height = getMaxHeight(size);
				trimSkipList();
			}
			
		}
	}
	
	// Method that checks if the SkipList contains a specific value and returns a 
	// boolean value
	public boolean contains(AnyType data)
	{
		int level = height - 1;
		Node<AnyType> cur, next;
		cur = head;
		
		// Loop while the cur node is non-null and the level is positive,
		while (cur != null && level >= 0)
		{
			next = cur.next(level);
			// If the next node is non-null and the next value is equal to the data value
			// return true
			if (next != null && next.value().compareTo(data) == 0)
			{
				return true;
			}
			
			// If the next node is null or if the next value is greater
			// than the data value, decrement the level
			else if (next == null || next.value().compareTo(data) > 0)
			{
				level--;
			}
			
			// If the next node is non-null and the next value is less
			// than the data value, follow the pointer to the next node
			else if (next != null && next.value().compareTo(data) < 0)
			{
				cur = next;
			}
		}
		
		return false;
	}
	
	// Method that checks if the SkipList contains a specific value and returns the 
	// node that contains that value
	public Node<AnyType> get(AnyType data)
	{
		int level = height - 1;
		Node<AnyType> cur, next;
		cur = head;
		
		// Loop while the current node is non-null and the level is positive
		while (cur != null && level >= 0)
		{
			next = cur.next(level);
			
			// If the next node is non-null and the next value is equal to the data value
			// return the next node  
			if (next != null && next.value().compareTo(data) == 0)
			{
				return next;
			}
			
			// If the next node is null or if the next value is greater
			// than the data value, decrement the level
			else if (next == null || next.value().compareTo(data) > 0)
			{
				level--;
			}
			
			// If the next node is non-null and the next value is less
			// than the data value, follow the pointer to the next node
			else if (next != null && next.value().compareTo(data) < 0)
			{
				cur = next;
			}
		}
		return null;
	}
	
	// Method that generates the maximum possible height of the SkipList
	// given the size of the SkipList (ceil)(log_2(n)) = h
	private static int getMaxHeight(int n)
	{
		int h = 0;
		int temp = 1;
		while (n > temp)
		{
			temp *= 2;
			h++;
		}
		return h;
	}
	
	// Method the generates a random height for a node given the maximum
	// possible height
	private static int generateRandomHeight(int maxHeight)
	{
		int h = 1;
		while ((int)(Math.random() * 2) == 1 && h < maxHeight)
		{
			h++;
		}
		
		return h;
	}
	
	// Method that grows the SkipList by one and connects the tops
	// of all of the grown nodes
	private void growSkipList()
	{
		int level = height - 1;
		head.grow();
		
		// Node that stores the last node that was grown
		Node<AnyType> prevGrowth = head;
		Node<AnyType> cur = head.next(level);
		
		// While current is non-null grow of the nodes.
		// If a node is grown, connect it to the rest
		// of the grown nodes
		while (cur != null)
		{
			if (cur.maybeGrow() == 1)
			{
				prevGrowth.setNext(level + 1, cur);
				prevGrowth = cur;
			}
			cur = cur.next(level);
		}
	
			
	}
	
	// Method that trims the top of the SkipList
	private void trimSkipList()
	{
		int level = height - 1;
		Node<AnyType> cur, next;
		cur = head;
		
		while (cur != null && level >= 0)
		{
			next = cur.next(level);
			cur.trim(height);
			cur = next;
		}
	}
	
	// Method that returns the difficulty level of this assignment 
	public static double difficultyRating()
	{
		return 3.0;
	}
	
	// Methid that returns the amount of hours spent on this assignment
	public static double hoursSpent()
	{
		return 12.5;
	}
}

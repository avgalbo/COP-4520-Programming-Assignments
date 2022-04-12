// Anthony Galbo
// COP 4520
// 4/7/22

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Node
{
  int data;
  Node next;
  Node(int data){this.data = data;}
}

class GiftList
{
  Node head;
  int num = 0;
  Random r = new Random();
  Lock lock = new ReentrantLock();
  public AtomicInteger action = new AtomicInteger();
	public AtomicInteger counter = new AtomicInteger(0);
	public AtomicBoolean finished = new AtomicBoolean(false);

  // Utility method that returns a created node.
  public Node createNode(int data)
  {
    return new Node(data);
  }

  GiftList(AtomicBoolean finished)
  {
    this.finished = finished;
  }

  // Run method executes adding and removing presents.
  public void run(GiftList list, GiftQueue queue, AtomicInteger guest) throws InterruptedException
  {
    int tag;

    action.set(r.nextInt(10));

    if (!queue.isEmpty() && action.get() < 4)
    {
      tag = queue.dequeue();
      list.insert(tag);
    }
    else if (!list.isEmpty() && action.get() < 8)
      list.delete();
    else
      if (!list.isEmpty() && action.get() < 10)
        list.contains(r.nextInt(500000));

    if (counter.get() == guest.get())
      finished.set(true);
  }

  // Insert method inserts the data into the list.
  public void insert(int data)
  {
    Node newNode = createNode(data);

    lock.lock();

    try
    {
      // Case where list is empty and
      if (isEmpty())
      {
        newNode.next = head;
        head = newNode;
        System.out.println("Tag " + data + " Present is added");
        return;
      }

      // Otherwise list is not empty and append data as long as its less than
      // current data then append at the end of the list.
      Node iter = head;

      while (iter.next != null && iter.next.data < data)
        iter = iter.next;

      newNode.next = iter.next;
      iter.next = newNode;
      System.out.println("Tag " + data + " Present is added");
    }
    finally
    {
      lock.unlock();
    }
  }

  // Delete method
  public void delete()
  {
    lock.lock();

    try
    {
      if (!isEmpty())
      {
        Node iter = head;

        // Case where there is only one node in the list.
        if (iter.next == null)
        {
          System.out.println("Thank you for present " + iter.data);
          head = iter.next;
          head = null;

        }

        System.out.println("Thank you for present " + iter.data);
        head = iter.next;

        counter.getAndIncrement();
      }
    }
    finally
    {
      lock.unlock();
    }
  }

  // Contains method which finds which iterates through the list and finds the gift
  // prints out not present if not in the list.
  public void contains(int data)
  {
    lock.lock();

    try
    {
      if (isEmpty())
        System.out.println("Gift " + data + " is not in the chain");
      else
      {
        Node iter = head;

        if (iter.data == data)
          System.out.println("Gift " + data + " is in the chain");

        else
        {
          while (iter.next != null && iter.next.data != data)
            iter = iter.next;

          if (iter.data == data)
            System.out.println("Gift " + data + " is in the chain");
          else
            System.out.println("Gift " + data + " is not in the chain");
        }
      }
    }
    finally
    {
      lock.unlock();
    }
  }

  public boolean isEmpty()
  {
    return (head == null);
  }
}

class GiftQueue
{
  Lock lock = new ReentrantLock();
  Queue<Integer> queue = new LinkedList<>();
  List<Integer> list;
  int giftNum;

  GiftQueue(int giftNum, List<Integer> list)
  {
    this.giftNum = giftNum;
    this.list = list;
  }

  // Add all method which adds the gifts in the list to the queue
  public void addAll()
  {
    for (int i = 0; i < giftNum; i++)
      queue.add((int)list.remove(0));
  }

  // Enqueue method
  public void enqueue(int data)
  {
    lock.lock();

    try
    {
      if (!queue.contains(data))
        queue.add(data);
    }
    finally
    {
      lock.unlock();
    }
  }

  public int dequeue()
  {
    lock.lock();

    try
    {
      return queue.remove();
    }
    finally
    {
      lock.unlock();
    }
  }

  public boolean isEmpty()
  {
    return (queue.size() == 0);
  }

}

class Servant implements Runnable
{
  AtomicBoolean finished;
  private AtomicInteger guestNum;
  private GiftList list;
  private GiftQueue queue;

  Servant(GiftList list, GiftQueue queue, AtomicBoolean finished, int guestNum)
  {
    this.list = list;
    this.queue = queue;
    this.guestNum = new AtomicInteger(guestNum);
    this.finished = finished;
  }

  public void run()
  {
    while (!finished.get())
    {
      try
      {
        list.run(list, queue, guestNum);
      }
      catch (InterruptedException e)
      {
        e.printStackTrace();
      }
    }
  }
}

public class BirthdayPresentsParty
{

  public static void main(String [] args) throws InterruptedException
  {
    int servants = 4, totalPresents = 500000;
    AtomicBoolean finished = new AtomicBoolean(false);
    int [] array = IntStream.rangeClosed(1, totalPresents).toArray();
    List<Integer> list = Arrays.stream(array).boxed().collect(Collectors.toList());
    Collections.shuffle(list);
    Thread [] thread = new Thread[servants];

    // Create objects for gift list and giftQueue.
    GiftList giftList = new GiftList(finished);
    GiftQueue queue = new GiftQueue(totalPresents, list);
    queue.addAll();

    long startTime = System.currentTimeMillis();

    for (int i = 0; i < servants; i++)
      thread[i] = new Thread(new Servant(giftList, queue, finished, totalPresents));

    for (int i = 0; i < servants; i++)
      thread[i].start();

    for (int i = 0; i < servants; i++)
      thread[i].join();

    if (finished.get() == true)
      System.out.println("\nThank you for all of the presents!!!\n");

    long endTime = System.currentTimeMillis();
    System.out.println("Party Took " + (endTime - startTime) + "ms to finish\n");
  }

}

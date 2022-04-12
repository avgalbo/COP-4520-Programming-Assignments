// Anthony Galbo
// COP 4520
// 4/10/22

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Node
{
  int temp, start, end;
  Node next;

  Node(int temp, int start, int end)
  {
    this.temp = temp;
    this.start = start;
    this.end = end;
  }
}

class TemperatureModule
{
  int num = 0;
  Node head;
  Random r = new Random();
  Lock lock = new ReentrantLock();
  AtomicInteger temp = new AtomicInteger();
  AtomicInteger start = new AtomicInteger(-1);
  AtomicInteger end = new AtomicInteger(0);
  AtomicInteger hour = new AtomicInteger(0);
  AtomicInteger counter = new AtomicInteger(0);
  AtomicBoolean finished = new AtomicBoolean(false);

  TemperatureModule(AtomicBoolean finished)
  {
    this.finished = finished;
  }

  public Node createNode(int temp, int start, int end)
  {
    return new Node(temp, start, end);
  }

  // Method that executes the given tasks in the TempertureModule class.
  public void run(TemperatureModule module, AtomicInteger duration) throws InterruptedException
  {
    lock.lock();

    try
    {
      int newHour = hour.get() + 1;

      temp.set(r.nextInt(170) - 100);
      start.getAndIncrement();
      end.getAndIncrement();

      module.insert(temp.get(), start.get(), end.get());

      // Print the hour
      if (counter.get() % 60 == 0)
      {
        if (newHour == 1 || newHour == 21)
          System.out.println(newHour +  "st hour:");
        else if (newHour == 2 || newHour == 22)
          System.out.println(newHour + "nd hour:");
        else if (newHour == 3 || newHour == 23)
            System.out.println(newHour + "rd hour:");
        else
            System.out.println(newHour + "th hour:");

        System.out.println("-------------");

        if (hour.get() == 0)
          module.printData(0, 60);
        else
          module.printData(60*hour.get(), 120*hour.get());

        System.out.println();
        System.out.println("Lowest Tempertures: ");
        module.lowestTemperture();
        System.out.println();

        System.out.println();
        System.out.println("Higest Tempertures: ");
        module.highestTemperture();
        System.out.println();

        System.out.println();
        System.out.print("Temperature Difference: ");
        module.temperatureDifference();
        System.out.println();

        head = null;
        hour.getAndIncrement();
      }

      if (hour.get() == duration.get())
        finished.set(true);
    }
    finally
    {
      lock.unlock();
    }
  }

  // Insert node method
  public void insert(int temp, int start, int end)
  {
    // Create new node
    Node newNode = createNode(temp, start, end);
    lock.lock();

    try
    {
      // Insert in the front
      if (isEmpty() || head.start >= start)
      {
        newNode.next = head;
        head = newNode;
        counter.incrementAndGet();
        return;
      }

      // Otherwise traverse to the end and insert.
      Node iter = head;

      while (iter.next != null && iter.next.start < start)
        iter = iter.next;

      newNode.next = iter.next;
      iter.next = newNode;
      counter.incrementAndGet();
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

  // Method to display the lowest tempertures.
  public void lowestTemperture()
  {
    lock.lock();
    int i = 0;

    try
    {
      if (!isEmpty())
      {
        List<Integer> list = new ArrayList<>();

        Node iter = head;

        // Call temp iter node and point it at head, then add the temperture
        // to the arraylist, then traverse through the rest of the list.
        while (iter.next != null)
        {
          list.add(iter.temp);
          iter = iter.next;
        }

        // Sort the list to grab lowest tempertures.
        Collections.sort(list);

        // Way to print out the 5 lowest tempertures.
        for (Integer temp: list)
        {
          if (i < 5)
            System.out.println((i + 1) + ": " + temp + "F");
          i++;
        }
      }
    }
    finally
    {
      lock.unlock();
    }
  }

  // Method to display the lowest tempertures.
  public void highestTemperture()
  {
    lock.lock();
    int i = 0;

    try
    {
      if (!isEmpty())
      {
        List<Integer> list = new ArrayList<>();

        Node iter = head;

        // Call temp iter node and point it at head, then add the temperture
        // to the arraylist, then traverse through the rest of the list.
        while (iter.next != null)
        {
          list.add(iter.temp);
          iter = iter.next;
        }

        // Sort the list to grab highest tempertures.
        Collections.sort(list);

        // Display the Highest Tempertures.
        for (Integer temp: list)
        {
          if (i >= (list.size() - 5) && i < list.size())
            System.out.println((i - 53) + ": " + temp + "F");
          i++;
        }
      }
    }
    finally
    {
      lock.unlock();
    }
  }

  // Method to display the temperture difference.
  public void temperatureDifference()
  {
    int i = 0, j = 0;
    int start = 0, end = 0, diff;
    int intervalTime = 10;
    Node iter = head;
    Node nextNode = head.next;

    int [] array = new int[50];

    lock.lock();

    try
    {
      if (!isEmpty())
      {
        while (i < 50 && nextNode != null)
        {
          // Assign iter temp to start variable
          start = iter.temp;

          while (j < intervalTime - 1 && nextNode.next != null)
          {
            // Traverse through the list
            nextNode = nextNode.next;
            j++;
          }

          // Assign next node
          end = nextNode.temp;
          diff = start - end;
          array[i] = diff;
          i++;

          iter = iter.next;
          nextNode = nextNode.next;
        }

        i = 0;

        int max = array[i];

        // Loop through and set the max.
        for (i = 1; i < 50; i++)
          if (array[i] > max)
            max = array[i];

        System.out.println(max + "F");
        System.out.println();

      }
    }
    finally
    {
      lock.unlock();
    }
  }

  // Utility method that prints the intervals and tempertures
  public void printData(int start, int end)
  {
    lock.lock();

    try
    {
      if (!isEmpty())
      {
        Node iter = head;

        while (iter.next != null && iter.next.start >= start && iter.next.start <= end)
        {
          if (hour.get() == 0)
            System.out.println("Intervals: " + iter.start + " - " + iter.end + " Temperature: " + iter.temp + "F");
          else
            System.out.println("Intervals: " + iter.start%60 + " - " + iter.end%60 + " Temperature: " + iter.temp + "F");

        iter = iter.next;
        }
      }
    }
    finally
    {
      lock.unlock();
    }
  }
}

class Sensor implements Runnable
{
  AtomicBoolean finished;
  private AtomicInteger duration;
  private TemperatureModule module;

  Sensor(TemperatureModule module, AtomicBoolean finished, int duration)
  {
    this.module = module;
    this.finished = finished;
    this.duration = new AtomicInteger(duration);
  }

  public void run()
  {
    while (!finished.get())
    {
      try
      {
        module.run(module, duration);
      }
      catch (InterruptedException e)
      {
        e.printStackTrace();
      }
    }
  }
}

public class AtmosphericTemperatureReadingModule
{
  public static void main(String[] args) throws InterruptedException
  {
		int duration = 24, tempSensors = 8;
		AtomicBoolean finished = new AtomicBoolean(false);
		TemperatureModule record = new TemperatureModule(finished);
		Thread [] thread = new Thread[tempSensors];

    long startTime = System.currentTimeMillis();

		for(int i = 0; i < tempSensors; i++) {
			thread[i] = new Thread(new Sensor(record, finished, duration));
		}

		for(int i = 0; i < tempSensors; i++) {
			thread[i].start();
		}

		for(int i = 0; i < tempSensors; i++) {
			thread[i].join();
		}

    long endTime = System.currentTimeMillis();
    System.out.println("Module Took " + (endTime - startTime) + "ms to finish\n");
  }
}

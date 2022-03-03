// Anthony Galbo
// COP 4520
// 2/24/22

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;

class Guest implements Runnable, Comparable<Guest>
{
  public enum State
  {
    ATPARTY,
    INSHOWROOM,
    TERMINATED,
  };

  private CrystalVase vase;
  private Guest getGuest;
  public State state;
  public boolean viewedVase;
  public int guestNum;
  public int numGuests;
  public int numGuestsViewedVase;

  public Guest(CrystalVase vase, int guestNum, int numGuests)
  {
    this.vase = vase;
    this.guestNum = guestNum;
    this.numGuests = numGuests;
    this.numGuestsViewedVase = 0;
    this.state = State.ATPARTY;
    this.viewedVase = false;
  }

  public int compareTo(Guest guest)
  {
    return 1;
  }

  public State getState()
  {
    return state;
  }

  public void setState(State state)
  {
    this.state = state;
  }

  public void setNumGuestsViewedVase(int numGuestsViewedVase)
  {
    this.numGuestsViewedVase = numGuestsViewedVase;
  }

  public void run()
  {
    while(true)
    {
      try
      {
        Thread.sleep(vase.MAX_VALUE);
      }
      catch (InterruptedException e)
      {
        //System.out.println("Guest " + guestNum + " was interrupted.\n");
        setState(State.TERMINATED);
        Thread.currentThread().interrupt();
        return;
      }

      switch (state)
      {
        case ATPARTY:
        {
          // Guests have less than 1% chance to view the vase
          Random r = new Random();
          if (r.nextInt() < 1)
          {
            try
            {
              vase.available.acquire();
              vase.myQueue.add(this);
              vase.available.release();
            }
            catch (InterruptedException e)
            {
              setState(State.TERMINATED);
              Thread.currentThread().interrupt();
              return;
            }
          }
          break;
        }
        case INSHOWROOM:
        {
          try
          {
            // Acquire Permit
            vase.available.acquire();

            System.out.println("Guest Number: " + guestNum + " has entered the showroom");

            // If a guest did not view the vase yet, mark visited vase to true
            // Increment the number of guests that visited the vase.
            if (!viewedVase)
            {
              viewedVase = true;
              numGuestsViewedVase++;
            }

            // Leave showroom
            state = State.ATPARTY;
            System.out.println("Guest Number: " + guestNum + " left the showroom");
            vase.available.release();

            if (numGuests == numGuestsViewedVase)
            {
              vase.allGuestsViewedVase = true;
              System.out.println("All of the guests have visited the showroom!");
            }
            else
            {
              getGuest = vase.myQueue.poll();

              while (getGuest == null)
              {
                try
                {
                  Thread.sleep(vase.MAX_VALUE);
                  getGuest = vase.myQueue.poll();
                }
                catch (InterruptedException e)
                {
                  state = State.TERMINATED;
                  return;
                }
              }

              getGuest.setNumGuestsViewedVase(numGuestsViewedVase);

              getGuest.setState(State.INSHOWROOM);
              System.out.println("Guest Number: " + guestNum + " called guest " + getGuest.guestNum + " to enter the showroom");
            }
          }
          catch (InterruptedException e)
          {
            state = State.TERMINATED;
            return;
          }
          break;
        }
        case TERMINATED:
        {
          return;
        }
      }
    }
  }
}

public class CrystalVase
{
  public static final int MAX_VALUE = 1;
  public final Semaphore available = new Semaphore(MAX_VALUE, true);
  public static boolean allGuestsViewedVase = false;
  public static PriorityBlockingQueue<Guest> myQueue;
  private static int n;

  public static void main(String [] args) throws InterruptedException
  {
    CrystalVase vase = new CrystalVase();
    Guest guest, getGuest;
    Thread myThread;
    List<Thread> threadList = new ArrayList<>();
    boolean inParty = true;
    boolean guestsInShowroom = false;
    boolean firstGuestInShowroom = false;
    myQueue = new PriorityBlockingQueue<>();

    // Allow user to creat custom number of guests.
    if (args.length < 1)
    {
      System.out.println("\nProgram has defaulted to 10 guests\n");
      n = 10;
    }
    else
    {
      n = Integer.parseInt(args[0]);
      checkUserInput();
    }

    // Create array of guestList
    Guest [] guestArray = new Guest[n];

    for (int i = 0; i < n; i++)
    {
      guest = new Guest(vase, i, n);
      myThread = new Thread(guest);
      guestArray[i] = guest;
      threadList.add(myThread);
      threadList.get(i).start();
    }

    // Start the clock.
    long startTime = System.currentTimeMillis();

    while (inParty)
    {
      // Case where the peek of the queue is not null and the first guest in the showroom is false.
      // If so, dequeue a guest from the queue, set the number of guests that
      // visited the vase to 0. set guest status to in the showroom and mark
      // first guest in showroom to true.
      if (!firstGuestInShowroom && myQueue.peek() != null)
      {
        getGuest = myQueue.remove();
        getGuest.setState(Guest.State.INSHOWROOM);
        getGuest.setNumGuestsViewedVase(0);
        System.out.println("Guest Number: " + getGuest.guestNum + " has been called to the showroom");
        firstGuestInShowroom = true;
      }

      // Case where all the guestList have visited the showroom, if so mark inParty
      // to false and break out of the loop.
      if (allGuestsViewedVase)
      {
        inParty = false;
        break;
      }

      // Fast forward
      Thread.sleep(MAX_VALUE);
    }

    // Party is over so interrupt every thread in the threadList
    for (Thread i : threadList)
      i.interrupt();

    // Stop the clock.
    long endTime = System.currentTimeMillis();

    System.out.println("Party Took " + (endTime - startTime) + "ms to finish");
  }

  public static void checkUserInput()
  {
    if (n <= 2)
    {
      System.out.println("\nGuest number too low, program has defaulted to 10 guests\n");
      n = 10;
    }
    else if (n > 20)
    {
      System.out.println("\nGuest number too high, program has defaulted to 10 guests\n");
      n = 10;
    }
    else
    {
      System.out.println("\nYou assigned " + n + " guests to the party\n");
    }
  }
}

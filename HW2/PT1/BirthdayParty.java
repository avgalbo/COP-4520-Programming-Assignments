// Anthony Galbo
// COP 4520
// 2/24/22

import java.util.*;
import java.util.concurrent.Semaphore;

class Guest implements Runnable
{
  public enum State
  {
    ATPARTY,
    INLABRINTH,
    TERMINATED,
  };

  private BirthdayParty party;
  public State state;
  public boolean visitedLabyrinth;
  public boolean ateCupcake;
  public int guestNum;
  public int numGuests;
  public int numGuestsVisited;

  public Guest(BirthdayParty party, int guestNum, int numGuests)
  {
    this.party = party;
    this.guestNum = guestNum;
    this.numGuests = numGuests;
    this.state = State.ATPARTY;
    this.visitedLabyrinth = false;
    this.ateCupcake = false;
  }

  public void setState(State state)
  {
    this.state = state;
  }

  public State getState()
  {
    return state;
  }

  public void run()
  {
    while (true)
    {
      try
      {
        Thread.sleep(party.MAX_VALUE);
      }
      catch(InterruptedException e)
      {
        Thread.currentThread().interrupt();
        setState(State.TERMINATED);
        return;
      }

      switch(state)
      {
        case ATPARTY:
        {
          // Case where guest number is 0, if so check to see if the number of
          // guests is equal to the number of guests visited the labyrinth.
          if (guestNum == 0)
          {
            if (numGuests == numGuestsVisited)
            {
              party.allGuestsVisited();
              System.out.println("All of the guests has visited the Minotaur’s labyrinth!");
            }
          }
          break;
        }
        case INLABRINTH:
        {
          try
          {
            // Acquire Permit
            party.available.acquire();

            // Case where guest number is 0
            if (guestNum == 0)
            {

              // Case where a guest has eaten the cupcake, if so, increment the
              // number of guests visited and replace the cupcake.
              if (!party.cupcake)
              {
                party.replaceCupcake();
                numGuestsVisited++;
                System.out.println("Guest Number: " + guestNum + " replaced the cupcake. " + numGuestsVisited + " guests visited the labyrinth.");
              }

              // Case where this is the guest 0's first time completing the labyrinth.
              // If mark visited true and incrmenet the number of guests visited.
              if (visitedLabyrinth)
              {
                visitedLabyrinth = true;
                numGuestsVisited++;
              }
            }
            else
            {
              // Case where if the cupcake is present
              if (party.cupcake)
              {
                // Case where the guest has not eaten the cupcake.
                if (!ateCupcake)
                {
                  party.eatCupcake();
                  System.out.println("Guest Number: " + guestNum + " has eaten the cupcake.");
                }
              }
            }

            // Release permit.
            party.available.release();
          }

          // Catch InterruptedException
          catch(InterruptedException e)
          {
            state = State.TERMINATED;
            return;
          }

          state = State.ATPARTY;
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

public class BirthdayParty
{
  public static final int MAX_VALUE = 1;
  public final Semaphore available = new Semaphore(MAX_VALUE, true);
  public boolean cupcake = true;
  public static boolean allGuestsVisited = false;
  private static int n;

  public void eatCupcake()
  {
    this.cupcake = false;
  }

  public void replaceCupcake()
  {
    this.cupcake = true;
  }

  public static void allGuestsVisited()
  {
    allGuestsVisited = true;
  }

  public static void main(String [] args)
  {
    BirthdayParty party = new BirthdayParty();
    Guest guest, getGuest;
    Thread myThread;
    List<Thread> threadList = new ArrayList<>();
    boolean inParty = true;
    boolean guestInLabyrinth = false;

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

    Guest [] guestArray = new Guest[n];

    for (int i = 0; i < n; i++)
    {
      guest = new Guest(party, i, n);
      myThread = new Thread(guest);
      guestArray[i] = guest;
      threadList.add(myThread);
      threadList.get(i).start();
    }

    getGuest = guestArray[0];

    // Start the clock.
    long startTime = System.currentTimeMillis();

    while (inParty)
    {
      while (guestInLabyrinth)
      {
        // Case where guest is in labyrinth, if so mark guest in labyrinth to true.
        if (getGuest.getState() == Guest.State.INLABRINTH)
          guestInLabyrinth = true;

        // Otherwise mark guest in labyrinth to false and print out which guest
        // number finished labyrinth.
        else
        {
          guestInLabyrinth = false;
          System.out.println("Guest Number: " + getGuest.guestNum + " finished labyrinth");
        }
      }

      // Case where the party is still going on, if so pick a random guest to
      // send in labyrinth and mark guest in labyrinth to true.
      if (inParty)
      {
        Random r = new Random();
        int nextGuest = r.nextInt(n);
        getGuest = guestArray[nextGuest];
        getGuest.setState(Guest.State.INLABRINTH);
        guestInLabyrinth = true;
      }

      // Case where all the guestList have visited the labyrinth, if so mark inParty
      // to false and break out of the loop.
      if (allGuestsVisited)
      {
        inParty = false;
        break;
      }
    }

    // Party is over so interrupt every thread in the threadList
    for (Thread i : threadList)
      i.interrupt();

    // Stop the clock
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

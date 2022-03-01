// Anthony Galbo
// COP 4520
// 2/24/22

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.Random;

public class BirthdayParty
{
  public static final int MAX_VALUE = 1;
  public final Semaphore available = new Semaphore(MAX_VALUE, true);
  public boolean cupcake = true;
  public static boolean allGuestsVisited = false;

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

    int n = (args.length < 1) ? 10 : Integer.parseInt(args[0]);

    Guest [] guestList = new Guest[n];

    for (int i = 0; i < n; i++)
    {
      guest = new Guest(party, i, n);
      myThread = new Thread(guest);
      guestList[i] = guest;
      threadList.add(myThread);
      threadList.get(i).start();
    }

    getGuest = guestList[0];

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
        getGuest = guestList[nextGuest];
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
}

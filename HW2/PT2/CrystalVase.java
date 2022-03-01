// Anthony Galbo
// COP 4520
// 2/24/22

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;

public class CrystalVase
{
  public static final int MAX_VALUE = 1;
  public final Semaphore available = new Semaphore(MAX_VALUE, true);
  public static boolean allGuestsVisited = false;
  public static PriorityBlockingQueue<Guest> myQueue;

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
    int n = (args.length < 1) ? 10 : Integer.parseInt(args[0]);

    Guest [] guestList = new Guest[n];

    for (int i = 0; i < n; i++)
    {
      guest = new Guest(vase, i, n);
      myThread = new Thread(guest);
      guestList[i] = guest;
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
        getGuest.setNumGuestsVisitedVase(0);
        System.out.println("Guest Number: " + getGuest.guestNum + " has been called to the showroom");
        firstGuestInShowroom = true;
      }

      // Case where all the guestList have visited the showroom, if so mark inParty
      // to false and break out of the loop.
      if (allGuestsVisited)
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
}

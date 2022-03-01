// Anthony Galbo
// COP 4520
// 2/23/2022

import java.util.*;

public class Guest implements Runnable, Comparable<Guest>
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
  public boolean visitedVase;
  public int guestNum;
  public int numGuests;
  public int numGuestsVisitedVase;

  public Guest(CrystalVase vase, int guestNum, int numGuests)
  {
    this.vase = vase;
    this.guestNum = guestNum;
    this.numGuests = numGuests;
    this.numGuestsVisitedVase = 0;
    this.state = State.ATPARTY;
    this.visitedVase = false;
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

  public void setNumGuestsVisitedVase(int numGuestsVisitedVase)
  {
    this.numGuestsVisitedVase = numGuestsVisitedVase;
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
            if (!visitedVase)
            {
              visitedVase = true;
              numGuestsVisitedVase++;
            }

            // Leave showroom
            state = State.ATPARTY;
            System.out.println("Guest Number: " + guestNum + " left the showroom");
            vase.available.release();

            if (numGuests == numGuestsVisitedVase)
            {
              vase.allGuestsVisited = true;
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

              getGuest.setNumGuestsVisitedVase(numGuestsVisitedVase);

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

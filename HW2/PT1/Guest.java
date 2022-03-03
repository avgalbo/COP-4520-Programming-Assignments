// Anthony Galbo
// COP 4520
// 2/23/2022

public class Guest implements Runnable
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


Anthony Galbo
COP 4520 Spring 2022

Programming Assignment 3 Part 1

Source code name: BirthdayPresentsParty.java

## Compile and Run Instructions

1. In a terminal, navigate to the location of BirthdayPresentsParty.java using the command:
```bash
    cd
```
2. To compile the program, run the following command:
```bash
    javac BirthdayPresentsParty.java
```
3. To run the program, use the following command:
```bash
    java BirthdayPresentsParty
```

## Output

The output will display to the console in the following format:

If a present is added to the list, the console will display:
```txt
Tag n present has been added.
```

If a present is removed from the list, the console will display:
```txt
Thank you for present n
```

If a present is found in the list, the console will display:
```txt
Gift n is in the chain
```

If a present is not found in the list, the console will display:
```txt
Gift n is not in the chain
```

When the party is over, the console will display:
```txt
Thank you for all the presents!
```

For time efficiency, the console will display:
```txt
Party Took <Time in ms>ms to finish
```

## Proof of Correctness
This program utilizes the ReentrantLock where it is owned by the thread last successfully locking, but not yet unlocking it. Each method called involving lock() will return, successfully acquiring the lock. This only allows one servant to write "thank you" cards at a time.


## Efficiency

Since there is no standard runtime for this algorithm. I am going to analyze this by a glance.

We take a present from the unordered bag and adds it to the chain in the correct location by hooking it up to the predecessors link. this is similar to an insertInOrder linked list method which runs in linear ```O(n)``` time.

Write a “Thank you” card to a guest and remove the present from the chain. To do so, a servant had to unlink the gift from its predecessor and make sure to connect the predecessor’s link with the next gift in the chain. This remove operation runs in ```O(n)``` time where we can remove from the head or somewhere in the middle or the last.

Per the Minotaur’s request, check whether a gift with a particular tag was present in the chain or not; without adding or removing a new gift, a servant would scan through the chain and check whether a gift with a particular tag is already added to the ordered chain of gifts or not. This runs in ```O(n)``` time.

When we take a present from the unordered bag, we are preforming a dequeue operation which both enqueue and dequeue run in ```O(n)``` time.

Since there are no nested loops, this program runs in ```O(n)``` time.

## Experimental evaluation

Before I display the results, This experiment was tested on an Macbook pro intel i9 processor with 16gb of ram.

Results may differ on machines with different cores.

I tested ten different test runs.

```txt
Trial 1 Run recorded at 5826ms
Trial 2 Run recorded at 7018ms
Trial 3 Run recorded at 4742ms
Trial 4 Run recorded at 5106ms
Trial 5 Run recorded at 4696ms
Trial 6 Run recorded at 6296ms
Trial 7 Run recorded at 4691ms
Trial 8 Run recorded at 4536ms
Trial 9 Run recorded at 5360ms
Trial 10 Run recorded at 4947ms
```

On average at ```5321.8ms``` is a reasonable time for each servant on one thread and only allowing one servant to write a thank you card.  

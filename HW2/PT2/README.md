Anthony Galbo
Team 24
COP 4520 Spring 2022

Programming Assignment 2 Part 2

Source code name: BirthdayParty.java

## Compile and Run Instructions

1. In a terminal, navigate to the location of BirthdayParty.java using the command:
```bash
    cd
```
2. To compile the program, run the following command:
```bash
    javac CrystalVase.java
```
3. To run the program, use the following command:
```bash
    java CrystalVase <n>
```

Where ```<n>``` is a custom number of guests the user can provide in the command line.

If a user does not provide a number in the command line, the code will default the number of guests to 10.

## Output

The output will display to the console in the following format:

If a guest gets called to the showroom, the console will display:
```txt
Guest Number: <GuestNum> has been called to the showroom.
```

If a guest enters the showroom, the console will display:
```txt
Guest Number: <GuestNum> has entered the showroom
```

If a guest leaves the showroom, the console will display:
```txt
Guest Number: <GuestNum> left the showroom.
```

If all the guests visited the showroom, the console will display:
```txt
All of the guests have visited the showroom!
```

To time efficiency, the console will display:
```txt
Party Took 166ms to finish
```

## Proof of Correctness
This program utilizes the Java Semaphore lock class which will only allow 1 of the 10 guests to enter Minotaurs showroom at a time. When a guest enters the showroom, the Semaphore will acquire a permit and will release it only when a guest completes the cycle in the showroom.

## Efficiency

Since there is no standard runtime for this algorithm. I am going to analyze this by a glance.

In the guest class each guest is in a loop until a guest breaks out of it.

So that runs in: ```O(n)``` time.

In the CrystalVase class, each guest is called in a loop under the condition while each guest is in the party.

So that runs in: ```O(n)``` time.

With that said, it is my estimation that this algorithm runs in: ```O(n^2)``` time.

## Efficiency pt2- Why was a queue strategy the best strategy?

The queue was the best strategy because the runtime for enqueue and dequeue operations happen at O(1) complexity as suppose to strategies 1 and 2 which causes a lot of expensive operations to view the vase.


## Experimental evaluation

Before I display the results, This experiment was tested on an Macbook pro intel i9 processor with 16gb of ram.

Results may differ on machines with different cores.

I tested ten different test runs.

Trial 1 Run recorded at ```31ms```
Trial 2 Run recorded at ```25ms```
Trial 3 Run recorded at ```31ms```
Trial 4 Run recorded at ```44ms```
Trial 5 Run recorded at ```32s```
Trial 6 Run recorded at ```32ms```
Trial 7 Run recorded at ```35ms```
Trial 8 Run recorded at ```26ms```
Trial 9 Run recorded at ```39ms```
Trial 10 Run recorded at ```42ms```

On avg at ```33.7ms``` is a reasonable time for each guest on one thread and only allowing one guest to enter the labyrinth on each permit.  

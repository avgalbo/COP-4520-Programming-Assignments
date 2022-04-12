Anthony Galbo
COP 4520 Spring 2022

Programming Assignment 3 Part 2

Source code name: AtmosphericTemperatureReadingModule.java

## Compile and Run Instructions

1. In a terminal, navigate to the location of AtmosphericTemperatureReadingModule.java using the command:
```bash
    cd
```
2. To compile the program, run the following command:
```bash
    javac AtmosphericTemperatureReadingModule.java
```
3. To run the program, use the following command:
```bash
    java AtmosphericTemperatureReadingModule
```

## Output

The output will display to the console in the following format:

First the console will display the hour such as:
```txt
nth hour:
```

Next, the console will display the intervals and temperature such as:
```txt
Intervals: n - n+1 Temperature: <n>F
```

Next, the console will display Lowest 5 temperature intervals such as:
```txt
Lowest Temperatures:
1: nF
2: nF
3: nF
4: nF
5: nF
```

Next, the console will display Lowest 5 temperature intervals such as:
```txt
Highest Tempertures:
1: nF
2: nF
3: nF
4: nF
5: nF
```

Next, the console will display Temperature difference such as:
```txt
Temperature Difference: nF
```

For time efficiency, the console will display:
```txt
Party Took <Time in ms>ms to finish
```

## Proof of Correctness
This program utilizes the ReentrantLock where it is owned by the thread last successfully locking, but not yet unlocking it. Each method called involving lock() will return, successfully acquiring the lock. This only allows one temperature reading to execute at a time.

## Efficiency

Since there is no standard runtime for this algorithm. I am going to analyze this by a glance.

In the TemperatureModule class there are methods that run in ```O(n)``` time, such as the insert method, the lowest temperature method and highest temperature method. Where the insert method at worst iterates to the end of the list at the worst case. As well as insert temperatures into an array list, sorting them and displaying the top 5 or bottom 5 results of the array list which utilizes one loop.

However in the temperatureDifference class it seems at worst case runs at ```O(n^2)``` because of the nested while loops.

It seems that at worst case this runs in ```O(n^2)```

## Experimental evaluation

Before I display the results, This experiment was tested on an Macbook pro intel i9 processor with 16gb of ram.

Results may differ on machines with different cores.

I tested ten different test runs on the default 10 guests.

```txt
Trial 1 Run recorded at 51ms
Trial 2 Run recorded at 44ms
Trial 3 Run recorded at 56ms
Trial 4 Run recorded at 44ms
Trial 5 Run recorded at 45ms
Trial 6 Run recorded at 43ms
Trial 7 Run recorded at 45ms
Trial 8 Run recorded at 44ms
Trial 9 Run recorded at 53ms
Trial 10 Run recorded at 42ms
```

On average at ```43.7ms``` is a reasonable time to allow one temperature reading at a time, however do not be fooled by the ms difference between this and BirthdayPresentsParty, where BirthdayPresentsParty had an very large test size at 500,000 presents which can take a tremendous amount of execution time even if the algorithm is running in linear time, however here we are only testing just a days worth of test cases which can run much faster even at exponential time.

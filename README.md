# Hitquoter1.1
Program for estimating the independence of local fluctuations in stock quotes, version 1.1

The study of fluctuations in exchange quotations remains an extremely topical task. An extensive software and technological apparatus was created to predict the market situation. However, its use is often ineffective in the specific practice of exchange trading. 

Therefore, determination of a dependent (or independent) local randomness of an increase (or decrease) in the exchange price may be in demand. A nominal quote chart is a kind of broken line with points of highs and lows. We can copy it (or part of it) from the monitor screen using my application Hitquoter 1.0 in Java 8 and turn it into a discrete array. Offered demo version of the program analyzes a chart, which consists of red and green columns. Red columns mean decrease, green – increase.

We will compare the resulting array with independent random oscillations based on fractal constructions. 

The “Chaos game" method is known for the Sierpinski triangle. In its construction, we choose three random static points on the plane. 

Let’s find the midpoint of the segment from any of these points (also chosen at random) to the fourth, random dynamic point. Now this midpoint becomes a new nominal fourth dynamic point. The procedure is repeated.

We can simplify this construction and remove one vertex of the triangle. This has not been encountered in the topic of fractals in the available literature. But the principle of randomness remains unchanged. The nominal third dynamic point will be defined as the midpoint of the segment between the previous point and one randomly taken from the first two fixed points. If we take a pair of adjacent dynamic points consecutively in pairs, they become segments of randomly varying length. 

Let’s take the arithmetic mean of the lengths of these segments. At large number of iterations the ratio of this mean to the length of the segment between the first two static points will be approximately equal to ¼. We propose to use this fact to analyze quotes for local randomness.

My application Hitquoter 1.0 performs calculations of the change in value from the minimum one pixel step along the x-axis to the step between the extremes. The program calculates the ratio of the amplitudes to the maximum difference of values in the selected fragment of the chart and displays the result that is closest to ¼ in the window. In the result line, the first place is the absolute value of the ratio, the second place takes that value in percent relative to ¼.

Approximation of the found values to ¼ allows us to consider changes in the chosen quote as locally independent (random).

The Hitquoter 1.1 program is written in Java 8 in the IntelliJ IDEA 2020.2.3 IDE using Git.

Huge thanks to my father for advice on the concept.

08 March 2022, Anna Zoikina

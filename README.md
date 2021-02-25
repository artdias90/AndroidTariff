# AndroidTariff_IE6

* Add a new user and return the new id of the user:
http://r1482a-02.etech.haw-hamburg.de/~s16tarif/cgi-bin/newUser.pl

* Add data usage for a user.
http://r1482a-02.etech.haw-hamburg.de/~s16tarif/cgi-bin/addDataUsage.pl?userId=.......&date=......&data=.......

* Explain: 
 - userId: is the id of the user, get from the http://....../newUser.pl url
 - date: The month. Must be in format MM-yyyy. (ex: 05-2016)
 - cost: Integer representing the monthly price of the package in euros ( ex: 15)
 - data: The total amount of data used by user in one month, in integer format.
 - example (adding usage): http://r1482a-02.etech.haw-hamburg.de/~s16tarif/cgi-bin/addDataUsage.pl?userId=<idHash>&date=MM-yyyy&data=<integer>
 - example2 (adding cost): http://r1482a-02.etech.haw-hamburg.de/~s16tarif/cgi-bin/addCost.pl?userId=<idHash>&date=MM-yyyy&cost=<integer>

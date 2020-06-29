# PayTM
TestDemo.java is the entry point that contains the main function from where execution starts.

Scenario:

Below is a GET API to fetch upcoming movies in Paytm.
https://apiproxy.paytm.com/v2/movies/upcoming
Automate the above API using an automation framework like Rest Assured etc. Make
all the following assertions to Pass/Fail your test case:
1. Status code
2. Movie release date: should not be before today’s date
3. Movie Poster URL: should only have .jpg format
4. Paytm movie code: is unique
5. No movie code should have more than 1 language format
Then write down the name of all the movies (in an excel file) whose content
available is 0.

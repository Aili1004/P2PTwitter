# P2P Twitter
#### The goal of this project is to implement a basic Peer-to-Peer (P2P) Twitter application. This P2PTwitter program will communicate with other P2PTwitter programs running remotely without using any centralised server.

-----------------------------------------

### Functionalities and Technologies used
- As the program is P2P, it comprises a client thread and a server thread. The client will send requests to the server of remote P2P programs whereas the server will serve the requests sent by remote P2P programs. As all servers must use the same port (7014), testing is only possible if at least two P2P programs are running concurrently on remote machines.

- It is using DatagramSocket for communication as messages get sent repeatedly.

- The program output should respect the following format for the program to be tested automatically.
- The user starts his/her own local program by giving his/her unikey, then the program asks his/her current status and lists the status of each participant. More precisely, the user must first start the program by giving his unikey (e.g., bob1234) as a parameter ($ java P2PTwitter bob1234).

- Then the program must immediately ask the user for his/her status by outputting Status: on the next line. After the user type some status (e.g., "Not too bad today.") and ‘Enter’, the program should output the information about the status of other machines, where status lines start with ‘#’ and are followed by the pseudo of the user and his/her unikey (or myself in case of the current user) in parentheses separated by single spaces, then a colon and a space followed by the corresponding status.

1. Status: Not too bad today.
2. ###P2P tweets###
3. #bob (myself): Not too bad today.
4. #alice (alic0123): just voted...
5. ###End tweets###

-  The program will be terminated manually by typing Ctrl-C (from a terminal).

- We test application by running it on two or more machines that we can control. The information regarding the participants should be stored in a properties file copied on each participant machine.









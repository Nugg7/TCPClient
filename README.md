# Auction
This program is only part of the Auction project and it consists of the client side which contains a graphical interface to be able to connect and operate on the auction.
*[SERVER](https://github.com/Nugg7/TCPServer.git) - to get server side.*
# Dependencies (from maven)
- JSON-simple ver. 1.1.1 (`com.googlecode.json-simple:json-simple:1.1.1`)
- formsfx-core (`com.dlsc.formsfx:formsfx-core:11.6.0`)

> [!important]
> These libraries might not be imported automatically when opening the IDE, if so import them manually by going on the File>Project Structure>Libraries and click the '+' button to either import them from maven or from the "libraries to import" folder provided by the project.
# Requirements
- JDK 8+
- IDE to run program (like Intellij)
# Installation
## Linux/Mac/Windows (git bash)
```java
git clone https://github.com/Nugg7/TCPClient.git
```
## Windows (without git bash)
- Download the zip
- extract
# Usage
Open the IDE and open the project downloaded/cloned, change the IP address and the port in the `ClientController.java` class `src/main/java/com/example/clienttcp` (if needed - default is : localhost:1234), then run the `ClientMain.java` file. Once ran the project, make sure the server is running too, insert a username, sign in, and when on the main GUI, click on the connect button on the lower right of the window.
# Functionalities
## User:
- Connection to the server through TCP with a socket chosen
- Send messages to other clients connected
- Send bids to the Auction
- Visualize the bids and messages on display with separated panels
- Visualize the products currently on auction
- Visualize how the current auction is going with the highest bid
- Quit from the Auction

> [!Tip]
> Connect to the server before the Admin starts the auction or you won't be able to connect
## Admin:
- Connection to the server through TCP with a socket chosen
- Set the products put up for auction
- Start the auction
- Refuse connections by the socket when auction is started
- Send messages to other clients connected
- Ability to choose when to switch to the next product
- Visualize the bids and messages on display with separated panels
- Visualize the products currently on auction
- Visualize how the current auction is going with the highest bid
- End the Auction
- Quit from the Auction

> [!Tip]
> Credentials of Admin - Username: `ADMIN`, Password: `admin1234`
> To change password go into `ClientController.java` and change the password in String variable `pass="admin1234"`

> [!warning]
> the current project works fine with Linux based operating systems but on windows operating systems has quite some bugs

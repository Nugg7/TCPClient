# Auction from: Steven Guevarra
This program is only part of the Auction project and it consists of the client side which contains a graphical interface to be able to connect and operate on the auction.
*[SERVER](https://github.com/Nugg7/TCPServer.git) - to get server side.*
# Dependencies (from maven)
- JSON-simple ver. 1.1.1 (`com.googlecode.json-simple:json-simple:1.1.1`)
- formsfx-core (`com.dlsc.formsfx:formsfx-core:11.6.0`)

# Requirements
- JDK 22
# Installation
## Linux/Mac/Windows
install the release or
```java
git clone https://github.com/Nugg7/TCPClient.git
```
# Usage
just double click on the jar file
or if that doesn't work open terminal or cmd in folder and use:
```java
java -jar ClientTCP.jar
```
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
> the current project executable (JAR) works fine with windows but breaks on Linux and Mac based OS

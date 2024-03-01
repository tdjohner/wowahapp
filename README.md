# wowahapp
Crafting Recipe fullfilment arbitrage calculator.

Turn a profit by buying materials off the Auction House
and re-selling them as a crafted item.

The sum of materials (+AH listing cut) must be less than the sale price of the finished item.

The user selects crafted items to monitor on the AH.
When the sum cost of the reagents required to craft the item is
less than the sale cost of the item on the AH, the item
is highlighted as profitable.

The application is aware of crafting materials listed at different prices,
and can tell you how many of each item can be crafted at a profit.


This document is to provide instructions for deploying our API code on a server. You’ll probably need to sudo a few of these commands.


For the service:

First navigate to /wowahapp
$> git pull [whatever release branch]
$> systemctl kill wowahapp
$> systemctl daemon-reload
$> cd /wowahapp/backend/src/wowahappAPI
$> go build -o wowahapp
$> setcap CAP_NET_BIND_SERVICE+eip /wowahapp/backend/src/wowahappAPI/wowahapp
$> systemctl start wowahapp

NOTE!!: It is VERY important that the executable be named ‘wowahapp’ because that’s what the startup service is set to look for.


For the scraper:

You just need to compile the executable with the proper name.
$> go build /wowahapp/backend/src/scraper -o getAHData

#If you need to edit the timing of the cron job or something
#This line should allow you to edit the file where the command
$> sudo crontab -e johnert2 

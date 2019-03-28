# pocket-picker
Displays some Pocket stats.

Simple web app consisting of two components:
* *gcp-proxy* which is simple proxy written in Golang and running as Cloud Function in Google Cloud Platform. It's only purpose is to add CORS headers to responses to brower requests as getpocket.com API doesn't do this.
* *pocketpicker* itself which is simple html/css/kotlin.js app fetching data from getpocket.com and displaying them so you know now, that you have to spend several days of non-stop reading all your saved articles to get inbox-zero.

Currently you can get running version here: https://pocketpicker.bluesloth.net/

Please notice, that it fetches *all your pocket data* everytime you open it, so it isn't blazingly fast...

Work still in progress, so hopefully some fancy optimizations would be added in the future.

## Version history
### 0.2 - 03/27/2019 
displays more interesting stats, now in color (table)!
### 0.1 - 03/26/2019
displays the number of unread items in your Pocket. Yay!

Youtube video: http://www.youtube.com/watch?v=MO-ooycZ_5E&feature=plcp

Have not decided on name yet, currently working with PsyberNet. Other considerations are Psyborg and Telepath. Is inconsistently used in project currently.

PsyberNet uses the location service in a phone to find regions, defined as polygons, that map to chatrooms. The chatrooms are displayed as a listview with diffent types of possible conversation items that influence sort behavior.




This was done as a learning project, as well as a demo for an idea that I think is cool. The currently implementation uses google app engine because it was free for small sizes. I learned half the things about both server and client sides of this project as I was going, and consequently I learned to not implement things until I have thought about and researched how things work more. I previously would implement straight ahead to practice.

The current database structure reflects putting more stress on the bandwidth, to save database operations. This is not a design that I would use again, but I naively implemented things before I understood them well enough. 

In the android app, there are some similar problems that were handled very differently at different places. I was learning and practicing new techniques for handling these issues, and thus the inconsistencies. I also didn't plan out the naming conventions I was going to use for different types of things enough, which is reflected again in some inconsistencies in names.




Currently defined regions

Santana Row
-121.950325,-121.95012,-121.94507,-121.94526
37.318417,37.323376,37.323334,37.318726

San Jose // may want to update this, doesn't seem to include south san jose
-121.970901,-121.843872,-121.758728,-121.957169
37.378342,37.370703,37.285526,37.303552


lock-icon url
http://www.iconarchive.com/show/aesthetica-2-icons-by-dryicons/lock-icon.html





For proper implementation
	Move off google app engine
	Use web sockets and push notifications (non-trivial without browser in google app engine currently)
	Restructure database to reflect different server architecture and push capabilities

For potential monetization
	create way to buy special, unfilterable conversation items, that are randomly (or something) put into the space
	
For for user created infrastructure
	release modified server software so that users can create regions, and register them with the service. Potentially
	a monetization option here.



Things that need to be done, some assuming push server is implemented

 • push pmConversations notification 
	add way to reply right there in conversation listview

 • use push notifications for most communications

 • Refactor packages on webserver, see if anything can be done on app

 • handle request timeout

 • create bulletins sections (nearly identical to conversation section, less frequently cleaned, not completely thought out)

 • better thread some http requests

 • sanitize input for server

 • limit input sizes for conversations, particularly topic field

 • time based location revalidation on client, using service bound to overview and conversations/bulletins

 • change createRegionsGoats.jsp (an armature obfuscation attempt) to a get request for /secure/createRegions (platform dependent)
                                                               
 • implement sounds settings page, (make a noise for lock and filter?)

 • refactor multiple servlets into one, directed by a mode parameter, to save instance time (platform dependent)

 • could lock people out of room by hashing date, salting partially with region name, sending both, and making sure that the date is within 30 seconds old or so

 
 JSON CONVERSATIONS DATA STRUCTURE{   //should be changed in real implementation

ArrayList Conversations[
	ArrayList Converation_Row[
		String parent, String time, String topic, String Content, String idNum, ArrayList Replies [
			ArrayList Reply [
				String Commenter, String replyContent, String Time
			]
			...Reply
		]
	]
	...Conversation_Row 
]
}




PM Conversation DB Structure{

Entity(
	String sender,
	String Message, (limit 500)
	Date date,
	String pmKey;
}



PM Conversation Json Structure{
					//for single conversation
ArrayList statements [
	ArrayList statement[
		String Sender,
		String Message,
		String dateString
		]
	...statement	
	]
}
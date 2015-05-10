package com.ar.twitter.harvester;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import twitter4j.AccountSettings;
import twitter4j.IDs;
import twitter4j.Location;
import twitter4j.PagableResponseList;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;

/**
 * <font color="#000000">The class that handles current logged and authenticated
 * user.</font> <font color="#000000"> </font><font color="#000000">-Must
 * include retrieval of friends and followers. </font> <font
 * color="#000000">-Must include retrieval of friends of friends and followers
 * of friends and followers.</font> <font color="#000000">-Shall handle only one
 * account.</font>
 * 
 * @author hmartinez
 * @version 1.0
 * @updated 02-may-2013 10:49:19 p.m.
 */
public class Account {

	private static final int MINIMUM_ALLOWABLE_RATELIMIT_4_OPERATION = 5;
	private Twitter twitter = null;
	private ArrayList<RateLimitStatus> ratelimit = null;

	public Account() {
		this.twitter = new TwitterFactory().getInstance();
	}

	/**
	 * Tweets a string chain of barely 140 chars.
	 * 
	 * @param msg
	 */
	public void tweet(String msg) {
		// The factory instance is re-useable and thread safe.
		// Twitter twitter = TwitterFactory.getSingleton();
		Status status;
		try {
			status = twitter.updateStatus(msg);
			System.out.println("Successfully updated the status to ["
					+ status.getText() + "].");
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets rate limit status remaining
	 */
	public Integer getRateLimitStatusSecondsToReset(String endpoint) {

		String family = endpoint.split("/", 3)[1];
		RateLimitStatus status;
		try {
			status = twitter.getRateLimitStatus(family).get(
					endpoint);
			return new Integer(status.getResetTimeInSeconds());
		} catch (TwitterException e) {

			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets rate limit status remaining
	 * NOTA: OJO, el limite para pedir el Rate Limit Status es de 180; esto es hasta antes de que te quedes sin poder pedir el estado.
	 */
	public Integer getRateLimitStatus(String endpoint) {
		String family = endpoint.split("/", 3)[1];
		RateLimitStatus status;
		try {
			status = twitter.getRateLimitStatus(family).get(endpoint);
			System.out.println(" ---- RATE LIMIT STATUS ----- TRACE ----");
			//traceRateLimitStatus(); 
			return new Integer(status.getRemaining());
		} catch (TwitterException e) {
			
			//traceRateLimitStatus(); 
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Manages when and how to stop the processing of any program executing Twitter API requests.
	 * @param string
	 */
	public void manageExceededRateLimit(String endpoint) {
		String family = endpoint.split("/", 3)[1];
		RateLimitStatus essential_status;
		RateLimitStatus action_status;
		
		try {
			
			essential_status = twitter.getRateLimitStatus("application").get("/application/rate_limit_status");
			action_status = twitter.getRateLimitStatus(family).get(endpoint);
			
			//if the current rate being used, or the rate limit are dangerously low...we put the process to sleep.			
			try {
				
				if ((essential_status.getRemaining() <= MINIMUM_ALLOWABLE_RATELIMIT_4_OPERATION) || (action_status.getRemaining() <= MINIMUM_ALLOWABLE_RATELIMIT_4_OPERATION)) {
					int timespan = (essential_status.getSecondsUntilReset() > action_status.getSecondsUntilReset()) ? essential_status.getSecondsUntilReset() : action_status.getSecondsUntilReset();
					
					System.out.println(" ---- STOPPING PROCESS DUE TO RATE LIMIT BY " + timespan + MINIMUM_ALLOWABLE_RATELIMIT_4_OPERATION+ " seconds ----");
					TimeUnit.SECONDS.sleep(timespan + MINIMUM_ALLOWABLE_RATELIMIT_4_OPERATION); 
				}
				 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
			
			//traceRateLimitStatus(); 
			
		} catch (TwitterException e) {
			
			//traceRateLimitStatus(); 
			e.printStackTrace();
			
		}
		
	}
	
	/**
	 * Prints rate limit status.
	 */
	public void traceRateLimitStatus() {
		try {
			// Twitter twitter = new TwitterFactory().getInstance();
			Map<String, RateLimitStatus> rateLimitStatus = twitter
					.getRateLimitStatus();
			for (String endpoint : rateLimitStatus.keySet()) {
				RateLimitStatus status = rateLimitStatus.get(endpoint);
				System.out.println("Endpoint: " + endpoint);
				System.out.println(" -> Limit: " + status.getLimit());
				System.out.println(" -> Remaining: " + status.getRemaining());
				System.out.println(" -> ResetTimeInSeconds: "
						+ status.getResetTimeInSeconds());
				System.out.println(" -> SecondsUntilReset: "
						+ status.getSecondsUntilReset());
			}
			
		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to get rate limit status: "
					+ te.getMessage());
			System.exit(-1);
		}
	}

	/**
	 * Obtains current account settings for the default account.
	 */
	public void getSettings() {
		try {
			// Twitter twitter = new TwitterFactory().getInstance();
			AccountSettings settings = twitter.getAccountSettings();
			System.out.println("Sleep time enabled: "
					+ settings.isSleepTimeEnabled());
			System.out.println("Sleep end time: " + settings.getSleepEndTime());
			System.out.println("Sleep start time: "
					+ settings.getSleepStartTime());
			System.out.println("Geo enabled: " + settings.isGeoEnabled());
			System.out.println("Listing trend locations:");
			Location[] locations = settings.getTrendLocations();
			for (Location location : locations) {
				System.out.println(" " + location.getName());
			}
			System.exit(0);
		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to get account settings: "
					+ te.getMessage());
			System.exit(-1);
		}
	}

	/**
	 * Returns a list of all the followers, by every account ID passed by
	 * parameter. It's a list of ALL followers ID, without discrimination.
	 * 
	 * @param useraccountid
	 */
	public long[] getFollowersIDbyUserID(long[] useraccountid) {
		try {

			long cursor = -1;
			long[] salida = new long[0];
			long[] aux;
			IDs ids;

			System.out.println("Getting followers's ids.");

			// for every single user
			for (int i = 0; i <= useraccountid.length; i++) {
				ids = twitter.getFollowersIDs(useraccountid[i], cursor);

				// destroys pagination
				do {
					// copies the ID to an array of long
					aux = new long[salida.length + ids.getIDs().length];

					System.arraycopy(ids.getIDs(), 0, aux, 0,
							ids.getIDs().length);
					System.arraycopy(aux, 0, salida, salida.length, aux.length);

				} while ((cursor = ids.getNextCursor()) != 0);

			}

			return salida;

		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to get followers' ids: "
					+ te.getMessage());
			return null;
		}
	}

	/**
	 * Returns a list of all your followers, by account name.
	 * 
	 * @param useraccount
	 */
	public void getFollowersIDbyUser(String[] useraccount) {
		try {
			// Twitter twitter = new TwitterFactory().getInstance();
			long cursor = -1;
			IDs ids;
			System.out.println("Listing followers's ids.");
			do {
				// por cada usuario que le pido
				if (0 < useraccount.length) {
					ids = twitter.getFollowersIDs(useraccount[0], cursor);
				} else {
					ids = twitter.getFollowersIDs(cursor);
				}
				// me devuelve el ID de seguidor
				for (long id : ids.getIDs()) {

					System.out.println(id);
				}
			} while ((cursor = ids.getNextCursor()) != 0);
			System.exit(0);
		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to get followers' ids: "
					+ te.getMessage());
			System.exit(-1);
		}
	}

	/**
	 * For any userID, friends IDs.
	 * 
	 * @return
	 */
	public long[] getFriendsID(long id) {

		List<User> listusers = this.getFriendsUsers();
		long[] ids = new long[listusers.size()];
		int i = 0;

		try {

			// just build up an array of longs with the full size of IDs.
			for (User usr : listusers) {
				ids[i] = usr.getId();
				i++;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return ids;
	}

	/**
	 * Connected user, friends IDs.
	 * 
	 * @return
	 */
	public long[] getFriendsID() {

		List<User> listusers = this.getFriendsUsers();
		long[] ids = new long[listusers.size()];
		int i = 0;

		try {

			// just build up an array of longs with the full size of IDs.
			for (User usr : listusers) {
				ids[i] = usr.getId();
				i++;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return ids;
	}

	/**
	 * Connected user, followers IDs.
	 * 
	 * @return
	 */
	public ArrayList<Long> getFollowersID() {

		List<User> listusers = this.getFollowersUsers();
		ArrayList<Long> listidusers = new ArrayList<Long>();

		// just build up an array of longs with the full size of IDs.
		for (User usr : listusers) {
			listidusers.add(usr.getId());
		}

		return listidusers;
	}

	/**
	 * Obtains all the users that follow you.
	 */
	public List<User> getFollowersUsers() {

		ArrayList<User> salida = new ArrayList<User>();

		try {

			long cursor = -1;

			PagableResponseList<User> userlist;

			do {
				// for the user already connected.
				userlist = twitter.getFollowersList(twitter.getScreenName(),
						cursor);

				// destroys the pagination
				for (User usr : userlist) {
					salida.add(usr);
				}

				// check ratelimitstatus and set timer.
				if (getRateLimitStatus("/followers/list").intValue() <= 1){
					//TODO: guarda aca, porque falta completar los parametros de endpoint.
					TimeUnit.SECONDS.sleep(getRateLimitStatusSecondsToReset("/followers/list"));
				}
					

				// iterates page by page
			} while ((cursor = userlist.getNextCursor()) != 0);

			return salida;

		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to get followers' ids: "
					+ te.getMessage());
			return salida;
		} catch (InterruptedException e) {
			System.out.println("Failed to set timer for rate reset wait: "
					+ e.getMessage());
			e.printStackTrace();
			return salida;
		}
	}

	/**
	 * Obtains all the users you're following.
	 */
	public List<User> getFriendsUsers() {

		ArrayList<User> salida = new ArrayList<User>();

		try {

			long cursor = -1;

			PagableResponseList<User> userlist;

			do {
				// for the user already connected.
				userlist = twitter.getFriendsList(twitter.getScreenName(),
						cursor);

				// destroys the pagination
				for (User usr : userlist) {
					salida.add(usr);
				}

				// iterates page by page
			} while ((cursor = userlist.getNextCursor()) != 0);

			return salida;

		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to get followers' ids: "
					+ te.getMessage());
			return salida;
		}
	}

	/**
	 * It gives you a list of Users (objects) that are the ones you're
	 * following.
	 * 
	 * @param accountname
	 */
	public void getFriendsUsersList(String accountname) {
		try {
			long cursor = -1;
			PagableResponseList<User> userlist;

			System.out.println("Listing friends ScreenNames.");

			do {
				// para el usuario que llega por parametro
				userlist = twitter.getFriendsList(accountname, cursor);

				// me devuelve el ID de seguidor
				for (User usr : userlist) {
					System.out.println(usr.getScreenName());
				}

				// itera pagina por pagina.
			} while ((cursor = userlist.getNextCursor()) != 0);
			System.exit(0);

		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to get followers' ids: "
					+ te.getMessage());
			System.exit(-1);
		}
	}

	/**
	 * Returns a list of all the followers of a given account name. (no names,
	 * just identifications)
	 * 
	 * @param useraccount
	 */
	public ArrayList<Long> getFollowersIDbyUser(String useraccount) {
		try {

			ArrayList<Long> idUsers = new ArrayList<Long>();
			long cursor = -1;
			IDs ids = null;

			System.out.println("Listing followers's ids.");

			do {

				// por cada usuario que le pido
				if (useraccount != null) {
					ids = twitter.getFollowersIDs(useraccount, cursor);

				} else {
					System.out.println("There is no user defined.");
					return null;
				}

				// me devuelve el ID de seguidor
				for (long id : ids.getIDs()) {
					idUsers.add(new Long(id));

				}

			} while ((cursor = ids.getNextCursor()) != 0);

			System.out.println("Total number of IDs elevated: "
					+ idUsers.size());
			return idUsers;

		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to get followers' ids: "
					+ te.getMessage());
			System.exit(-1);
		}

		return null;

	}

	/**
	 * This method follows a new userid.
	 * 
	 * @param id
	 * @throws TwitterException
	 */
	public User follow(Long id) throws TwitterException {

		return twitter.createFriendship(id.longValue());

	}

	/**
	 * Returns user data from the given id
	 * 
	 * @param id
	 * @return
	 */
	public User getUserData(Long id) {
		User usuario = null;

		try {
			usuario = this.twitter.showUser(id.longValue());

		} catch (TwitterException e) {

			e.printStackTrace();
		}

		return usuario;
	}




}

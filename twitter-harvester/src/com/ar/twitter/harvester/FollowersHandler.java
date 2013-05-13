package com.ar.twitter.harvester;

import java.util.List;
import java.util.Map;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;


/**
 * <font color="#000000">This will be the one that will process the data obtained
 * from the list of twitter accounts obtained.</font>
 * <font color="#000000">
 * </font><font color="#000000">-Propose the accounts to follow.</font>
 * <font color="#000000">-Set timer for every account.</font>
 * <font color="#000000">-Keep record of past followed accounts </font>
 * <font color="#000000">-Unfollow accounts that do not follow back in 24hs.
 * </font>
 * @author hmartinez
 * @version 1.0
 * @updated 02-may-2013 10:49:19 p.m.
 */
public class FollowersHandler {

	private Map AccountTimer;
	private List AlreadyFollowedPast;
	private List PossibleFollowersAccounts;
	public OldFollowersDAO m_OldFollowersDAO;

	private Twitter twitter = null;

	/**
	 * Just obtains the singleton that is used to interoperate with twiter. 
	 */
	public FollowersHandler() {
		this.twitter = new TwitterFactory().getInstance();
	}

//	Gives back a list of followers for a given user.
	public List getFollowersList(String tuser){
		
		return null;
	}

	public void finalize() throws Throwable {

	}

	public FollowersHandler FollowersHandler(){
		return null;
	}

	/**
	 * Performs the following of the IDs passed as parameter, it doen't mind WHO is followed.
	 * @param amount - The amount of the given users to be followed.
	 * @param accounts - a long array with all the accounts to follow.
	 */
	public int followAccount(long amount, long[] accounts){
		
		try {
			
			for (int i=0;i<=accounts.length;i++){
				twitter.createFriendship(accounts[i]);
				
			}			
			
		} catch (TwitterException e) {
			e.printStackTrace();
		} 
		
		return 1;
	}

	public List getPossibleFollowersAccounts(){
		return PossibleFollowersAccounts;
	}

	/**
	 * 
	 * @param accounts
	 * @param conditions
	 */
	public int identifyViableAccounts(List accounts, Constraints conditions){
		return 0;
	}

	/**
	 * 
	 * @param account
	 */
	private int initTimer(IDs account){
		return 0;
	}

	private int loadAlreadyFollowedPast(){
		return 0;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setPossibleFollowersAccounts(List newVal){
		PossibleFollowersAccounts = newVal;
	}

	/**
	 * 
	 * @param accounts
	 */
	public int unfollowAccount(List accounts){
		return 0;
	}

}

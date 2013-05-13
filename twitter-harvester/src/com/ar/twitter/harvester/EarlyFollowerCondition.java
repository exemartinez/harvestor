package com.ar.twitter.harvester;

/**
 * <font color="#000000">It's a kind of structure, it could end to be a sub class
 * of harvestor, but I choose to set it separated for scalability reasons.</font>
 * <font color="#000000">
 * </font><font color="#000000">This contains the conditions that must use other
 * classes for deciding if a given Account is suitable for being followed.</font>
 * <font color="#000000">
 * </font><font color="#000000">The default attributes will be (by account):
 * </font>
 * <font color="#000000">
 * </font><font color="#000000">-Number of friends index / Number of followers (
 * rule could be: always below 1, or not follow)</font>
 * <font color="#000000">-Maximun number of followers.</font>
 * <font color="#000000">-Maximum number of friends</font>
 * <font color="#000000">-Number of Twits/ Number of followers </font>
 * @author hmartinez
 * @version 1.0
 * @created 02-may-2013 10:54:49 p.m.
 */
public class EarlyFollowerCondition implements Constraints {

	private int MAX_FOLLOWER = 5000;
	private int MAX_FRIENDS = -1;
	private int MIN_NUMBER_TWEET = 1200;
	/**
	 * Must be lower than this
	 */
	private int RATIO_FRIEND_FOLLOWER = 1;
	/**
	 * MUST BE GREATER THAN THIS
	 */
	private int RATIO_TWEET_FOLLOWER = 20;
	private long MaxFollowers;
	private long MaxFriends;
	private long MinNumberTweet;
	private Double RatioFriendFollower;
	private Double RatioTweetFollower;



	public void finalize() throws Throwable {

	}

	public EarlyFollowerCondition EarlyFollowerCondition(){
		return null;
	}

	public long getMaxFollowers(){
		return MaxFollowers;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setMaxFollowers(long newVal){
		MaxFollowers = newVal;
	}

	public long getMaxFriends(){
		return MaxFriends;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setMaxFriends(long newVal){
		MaxFriends = newVal;
	}

	public long getMinNumberTweet(){
		return MinNumberTweet;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setMinNumberTweet(long newVal){
		MinNumberTweet = newVal;
	}

	public Double getRatioFriendFollower(){
		return RatioFriendFollower;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setRatioFriendFollower(Double newVal){
		RatioFriendFollower = newVal;
	}

	public Double getRatioTweetFollower(){
		return RatioTweetFollower;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setRatioTweetFollower(Double newVal){
		RatioTweetFollower = newVal;
	}

}
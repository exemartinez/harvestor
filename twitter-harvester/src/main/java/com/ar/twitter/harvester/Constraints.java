package com.ar.twitter.harvester;

/**
 * <font color="#000000">It's a kind of structure, it could end to be a sub
 * class of harvestor, but I choose to set it separated for scalability
 * reasons.</font> <font color="#000000"> </font><font color="#000000">This
 * contains the conditions that must use other classes for deciding if a given
 * Account is suitable for being followed.</font> <font color="#000000">
 * </font><font color="#000000">The default attributes will be (by account):
 * </font> <font color="#000000"> </font><font color="#000000">-Number of
 * friends index / Number of followers ( rule could be: always below 1, or not
 * follow)</font> <font color="#000000">-Maximun number of followers.</font>
 * <font color="#000000">-Maximum number of friends</font> <font
 * color="#000000">-Number of Twits/ Number of followers </font>
 * 
 * @author hmartinez
 * @version 1.0
 * @created 02-may-2013 10:54:49 p.m.
 */
public interface Constraints {

	long MaxFollowers = -1;
	long MaxFriends = -1;
	long MinNumberTweet = -1;
	double RatioFriendFollower = -1;
	double RatioTweetFollower = -1;

	public long getMaxFollowers();

	public long getMaxFriends();

	public long getMinNumberTweet();

	public Double getRatioFriendFollower();

	public Double getRatioTweetFollower();

	/**
	 * 
	 * @param newVal
	 */
	public void setMaxFollowers(long newVal);

	/**
	 * 
	 * @param newVal
	 */
	public void setMaxFriends(long newVal);

	/**
	 * 
	 * @param newVal
	 */
	public void setMinNumberTweet(long newVal);

	/**
	 * 
	 * @param newVal
	 */
	public void setRatioFriendFollower(Double newVal);

	/**
	 * 
	 * @param newVal
	 */
	public void setRatioTweetFollower(Double newVal);

}
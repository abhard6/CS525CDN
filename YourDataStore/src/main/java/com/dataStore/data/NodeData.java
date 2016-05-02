package com.dataStore.data;
import java.io.Serializable;

public class NodeData implements Serializable
{

	private static final long serialVersionUID = 1L;
	
	
	
	private String nodeId = "";
	private long heartBeat = 0l;
	private long lastRecordedTime = 0l;
	private boolean isActive = true;
	//TODO may re-consider this later 
	// code block for the election 
	private int pid = 99;
	private int electionCounts = 0;
	private int okMessageCounts = 0;
	private boolean isLeader = false;
	private boolean isSuperNode = false;
	private long space = 0l; //space should be in MB
	private int bandwidth = 0; //it should be in KB per second
	
	public boolean isSuperNode() {
		return isSuperNode;
	}

	public void setSuperNode(boolean isSuperNode) {
		this.isSuperNode = isSuperNode;
	}

	public long getSpace() {
		return space;
	}

	public void setSpace(long space) {
		this.space = space;
	}
	
	
	public int getBandwidth() {
		return bandwidth;
	}

	public void setBandwidth(int bandwidth) {
		this.bandwidth = bandwidth;
	}

	public NodeData() 
	{
		super();
	}
	
	public NodeData(String nodeId, long heartBeat, long lastRecordedTime) 
	{
		//super();
		this.nodeId = nodeId;
		this.heartBeat = heartBeat;
		this.lastRecordedTime = lastRecordedTime;
	}

	public NodeData(String nodeId, long heartBeat, long lastRecordedTime,
			boolean isActive) 
	{
		this.nodeId = nodeId;
		this.heartBeat = heartBeat;
		this.lastRecordedTime = lastRecordedTime;
		this.isActive = isActive;
	}
	
	public NodeData(String nodeId, long heartBeat, long lastRecordedTime,
			boolean isActive, int pid) 
	{
		this.nodeId = nodeId;
		this.heartBeat = heartBeat;
		this.lastRecordedTime = lastRecordedTime;
		this.isActive = isActive;
		this.pid = pid;
	}
	
	
	public NodeData(String nodeId, long heartBeat, long lastRecordedTime,
			boolean isActive, long space, int bandwidth,boolean isSuperNode) 
	{
		this.nodeId = nodeId;
		this.heartBeat = heartBeat;
		this.lastRecordedTime = lastRecordedTime;
		this.isActive = isActive;
		this.space = space;
		this.bandwidth = bandwidth;
		this.isSuperNode = isSuperNode;
	}
	
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public long getHeartBeat() {
		return heartBeat;
	}
	public void setHeartBeat(long heartBeat) {
		this.heartBeat = heartBeat;
	}
	public void increaseHeartBeat()
	{
		this.heartBeat += 1;
	}
	public long getLastRecordedTime() {
		return lastRecordedTime;
	}
	public void setLastRecordedTime(long lastRecordedTime) {
		this.lastRecordedTime = lastRecordedTime;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	
	public int getPid()
	{
		return pid;
	}
	public void setPId(int id)
	{	
		this.pid = id;
	}
	public int getElectionCounts()
	{
		return electionCounts;
	}
	public void setElectionCounts(int count)
	{	
		this.electionCounts = count;
	}
	public void increaseElectionCounts()
	{
		this.electionCounts +=1;
	}
	public int getOkMessageCounts()
	{
		return okMessageCounts;
	}
	public void setOkMessageCounts(int count)
	{	
		this.okMessageCounts = count;
	}
	public void increaseOkMessageCounts()
	{
		this.okMessageCounts +=1;
	}
	public boolean isLeader()
	{
		return isLeader;
	}
	public void setIsLeader(boolean l)
	{
		this.isLeader = l;
	}
	
}

package org.mgnl.nicki.scheduler;

public class JobConfig {
	private boolean active;
	private String name;
	private String group;
	private String JobClassName;
	private String cronSchedule;
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getCronSchedule() {
		return cronSchedule;
	}
	public void setCronSchedule(String cronSchedule) {
		this.cronSchedule = cronSchedule;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getJobClassName() {
		return JobClassName;
	}
	public void setJobClassName(String jobClassName) {
		JobClassName = jobClassName;
	}

}

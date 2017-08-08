package org.mgnl.nicki.vaadin.base.components;

import com.vaadin.ui.UI;

public abstract class ProgressRunner extends Thread implements Runnable {
	private String title;
	private int count;
	private int current;
	private String details;
	private NickiProgress progress;

	public ProgressRunner(NickiProgress progress, String title, int count) {
		super();
		this.title = title;
		this.count = count;
		this.progress = progress;
	}

	public String getTitle() {
		return this.title;
	}


    @Override
    public void run() {
    	doWork();
        
        // Show the "all done" for a while
        try {
            sleep(2000); // Sleep for 2 seconds
        } catch (InterruptedException e) {}

        progress.finish();
    }

	public abstract void doWork();

	public void progressed(int newCurrent, String newDetails) {
		this.current = newCurrent;
		this.details = newDetails;
		
        UI.getCurrent().access(new Runnable() {
            @Override
            public void run() {
            	progress.progressed(current, details);
            }
        });		
	}

	public int getCount() {
		return count;
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}
}

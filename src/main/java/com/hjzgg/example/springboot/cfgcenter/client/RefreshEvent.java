package com.hjzgg.example.springboot.cfgcenter.client;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.context.ApplicationEvent;

public class RefreshEvent extends ApplicationEvent {

	private Object event;
	private String eventDesc;

	public RefreshEvent(Object source, Object event, String eventDesc) {
		super(source);
		this.event = event;
		this.eventDesc = eventDesc;
	}

	public Object getEvent() {
		return this.event;
	}

	public String getEventDesc() {
		return this.eventDesc;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("event", event)
				.append("eventDesc", eventDesc)
				.toString();
	}
}
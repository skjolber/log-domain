package com.github.skjolber.log.domain.utils;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Marker;

import net.logstash.logback.marker.LogstashMarker;

public abstract class DomainMdc<T extends DomainMarker> {

	private static volatile List<DomainMdc<? extends DomainMarker>> mdcs = new ArrayList<>();

	public static void register(DomainMdc<? extends DomainMarker> mdc) {
		// defensive copy operation
		List<DomainMdc<? extends DomainMarker>> next = new ArrayList<>(DomainMdc.mdcs);
		
		next.add(mdc);
		
		DomainMdc.mdcs = next;
	}

	public static void unregister(DomainMdc<? extends DomainMarker> mdc) {
		// defensive copy operation
		List<DomainMdc<? extends DomainMarker>> next = new ArrayList<>(DomainMdc.mdcs);

		next.remove(mdc);

		DomainMdc.mdcs = next;
	}

	public static List<DomainMdc<? extends DomainMarker>> getMdcs() {
		return mdcs;
	}
	
	public static Closeable mdc(LogstashMarker marker) {
		if(marker instanceof DomainMarker) {
			DomainMarker domainMarker = (DomainMarker)marker;
			domainMarker.pushContext();
			
			if(marker.hasReferences()) {
				Iterator<Marker> iterator = marker.iterator();
				while(iterator.hasNext()) {
					Marker next = iterator.next();
					
					if(next instanceof DomainMarker) {
						mdc((DomainMarker)next);
					}
				}
			}
			return domainMarker;
		}
		throw new IllegalArgumentException("Expected instance of " + DomainMarker.class.getName());
	}
	
	protected ThreadLocal<T> inheritableThreadLocal = new InheritableThreadLocal<T>() {
		
		@Override
		protected T childValue(T parentValue) {
            if (parentValue == null) {
                return null;
            }
            return parentValue;
		}
    };

	protected String qualifier;

	public DomainMdc(String qualifier) {
		this.qualifier = qualifier;
	}
	
	public String getQualifier() {
		return qualifier;
	}

	public T get() {
		return inheritableThreadLocal.get();
	}
    
    public void push(T child) {
		inheritableThreadLocal.set(child);
    }
    
    public void pop(T item) {
    	T tail = inheritableThreadLocal.get();
    	if(tail == null) {
    		throw new IllegalArgumentException("Cannot pop MDC stack, already empty");
    	} else if(item != tail) {
    		throw new IllegalArgumentException("MDC entries must be removed in the reverse order as they were added");
    	}
    	
		T parent = (T) item.getParent();
		if(parent != null) {
			inheritableThreadLocal.set(parent);
		} else {
	    	inheritableThreadLocal.remove();
		}
    }
	
    public void clear() {
    	inheritableThreadLocal.remove();
    }
    
	public void register() {
		DomainMdc.register(this);
	}
	
	public void unregister() {
		DomainMdc.unregister(this);
	}
	
}
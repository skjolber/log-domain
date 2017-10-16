package com.github.skjolber.log.domain.utils;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;

import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.marker.LogstashMarker;

public class DeferredMdcMarker extends LogstashMarker implements StructuredArgument {

	private static final long serialVersionUID = 1L;

	public static final String MARKER_NAME = LogstashMarker.MARKER_NAME_PREFIX + "DEFERRED_MAP_FIELDS";

	private final List<DomainMarker> deferredMarkers;
	
    public DeferredMdcMarker(List<DomainMarker> deferredMarkers) {
        super(MARKER_NAME);
        
        this.deferredMarkers = deferredMarkers;
    }
    
    public List<DomainMarker> getMarkers() {
		return deferredMarkers;
	}
    
    @Override
    public void writeTo(JsonGenerator generator) throws IOException {
		for (DomainMarker domainMarker : deferredMarkers) {
			domainMarker.writeTo(generator);
		}
    }

}
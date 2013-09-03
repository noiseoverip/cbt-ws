package com.cbt.ws.services;

import javax.ws.rs.ext.Provider;

import com.cbt.ws.WorkerManager;
import com.google.inject.Inject;
import com.sun.jersey.api.model.AbstractResourceModelContext;
import com.sun.jersey.api.model.AbstractResourceModelListener;

@Provider
public class ResourceLoaderListener implements AbstractResourceModelListener {
	
	private WorkerManager mWorkerManager;

	@Inject
	public ResourceLoaderListener(WorkerManager workerManager) {
		mWorkerManager = workerManager;
	}
	
    @Override
    public void onLoaded(AbstractResourceModelContext modelContext) {
    	mWorkerManager.start();
    }	
}

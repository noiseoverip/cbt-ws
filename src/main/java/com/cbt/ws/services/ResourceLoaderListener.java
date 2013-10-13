package com.cbt.ws.services;

import com.cbt.ws.WorkerManager;
import com.google.inject.Inject;
import com.sun.jersey.api.model.AbstractResourceModelContext;
import com.sun.jersey.api.model.AbstractResourceModelListener;

import javax.ws.rs.ext.Provider;

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

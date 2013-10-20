package com.cbt.ws.testtools;

import com.cbt.ws.entity.TestRun;

public class GetTestRunResponse {
   private TestRun[] results;
   private int totalRecords;

   public TestRun[] getResults() {
      return results;
   }

   public int getTotalRecords() {
      return totalRecords;
   }
}

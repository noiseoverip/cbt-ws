package com.cbt.ws.entity;

import com.cbt.core.entity.Device;
import com.cbt.core.entity.TestConfig;
import com.cbt.jooq.enums.TestrunTestrunStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

/**
 * Entity representing single test run information (configuration id, status...)
 *
 * @author Saulius Alisauskas 2013-03-24 Initial version
 */
public class TestRun {
   public enum Status {FINISHED, RUNNING, WAITING}

   private Date created;
   /**
    * Devices to be used for this test run, calculated and assigned by system
    */
   private List<Device> devices;
   private Long id;
   private String name;
   private TestrunTestrunStatus status;
   @JsonIgnore
   private TestConfig testconfig;
   private Long testconfigId;
   private Date updated;
   private Long userId;

   @Override
   public boolean equals(Object object) {
      if (null != object && object instanceof TestRun) {
         TestRun other = (TestRun) object;
         if (getId().equals(other.getId()) && getId().equals(other.getId())
               && getStatus().equals(other.getStatus())) {
            return true;
         }
      }
      return false;
   }

   public Date getCreated() {
      return created;
   }

   public List<Device> getDevices() {
      return devices;
   }

   public Long getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public TestrunTestrunStatus getStatus() {
      return status;
   }

   public TestConfig getTestconfig() {
      return testconfig;
   }

   @Column(name = "testrun_testconfig_id")
   public Long getTestconfigId() {
      return testconfigId;
   }

   public Date getUpdated() {
      return updated;
   }

   @Column(name = "testrun_user_id")
   public Long getUserId() {
      return userId;
   }

   @Column(name = "testrun_created")
   public void setCreated(Date created) {
      this.created = created;
   }

   public void setDevices(List<Device> devices) {
      this.devices = devices;
   }

   @Column(name = "testrun_id")
   public void setId(Long id) {
      this.id = id;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Column(name = "testrun_status")
   public void setStatus(TestrunTestrunStatus status) {
      this.status = status;
   }

   public void setTestconfig(TestConfig testconfig) {
      this.testconfig = testconfig;
   }

   @Column(name = "testrun_testconfig_id")
   public void setTestconfigId(Long testconfigId) {
      this.testconfigId = testconfigId;
   }

   @Column(name = "testrun_updated")
   public void setUpdated(Date updated) {
      this.updated = updated;
   }

   public void setUserId(Long userId) {
      this.userId = userId;
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this.getClass())
            .add("id", getId())
            .add("userId", getUserId())
            .add("devices", getDevices())
            .add("status", getStatus())
            .add("created", getCreated())
            .add("updated", getUpdated())
            .add("testconfigId", getTestconfigId())
            .add("testconfig", getTestconfig()).toString();
   }
}

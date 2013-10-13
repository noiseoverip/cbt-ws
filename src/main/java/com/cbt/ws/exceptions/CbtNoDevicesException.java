package com.cbt.ws.exceptions;

public class CbtNoDevicesException extends CbtWsException {

   /**
    * Auto-generated value
    */
   private static final long serialVersionUID = -1619107002691721326L;
   private static final String MESSAGE = "Could not find any devices to run tests on";

   public CbtNoDevicesException() {
      this(MESSAGE);
   }

   public CbtNoDevicesException(String message) {
      super(message);
   }

   public CbtNoDevicesException(String message, Throwable cause) {
      super(message, cause);
   }

}

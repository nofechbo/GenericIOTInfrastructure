package org.connection.connectionserviceutils;

public enum Status {
        SUCCESS(200), //message: "request fulfilled"
        BAD_REQUEST(400), //message: "invalid request"
        REQUEST_NOT_FOUND(404), //message: "no such request"
        EXECUTION_FAILURE(418); //messsage: "unable to fulfill request"

        private final int statusCode;
        private Status(int statusCode) {
                this.statusCode = statusCode;
        }

        public int getStatusCode() {
                return statusCode;
        }
}

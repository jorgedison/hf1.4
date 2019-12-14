package io.fledger.fabric.chaincode.Models;

public class ChaincodeResponse {
    private String message;
    private String code;
    private boolean OK;

    public ChaincodeResponse(String message, String code, boolean OK) {
        this.setCode(code);
        this.setMessage(message);
        this.setOK(OK);
    }

    /**
     * @return the oK
     */
    public boolean isOK() {
        return OK;
    }

    /**
     * @param oK the oK to set
     */
    public void setOK(boolean oK) {
        this.OK = oK;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
    }

package io.fledger.fabric.chaincode;
import java.util.List;

import io.fledger.fabric.chaincode.Models.Wallet;
import io.fledger.fabric.chaincode.Models.ChaincodeResponse;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Wallet manager model is one of the implementations of the transaction system, in which, generally speaking, 
 * there are wallets and each wallet contains some values or tokens that could be used to transfer. 
 * Of course, there are lots of variations on the details of the implementations, such as the token exchange and multi-tokens features
 */
public class WalletManager extends ChaincodeBase {
    

    private String responseError(String errorMessage, String code) {
        try {
            return (new ObjectMapper()).writeValueAsString(new ChaincodeResponse(errorMessage, code, false));
        } catch (Throwable e) {
            return "{\"code\":'" + code + "', \"message\":'" + e.getMessage() + " AND " + errorMessage + "', \"OK\":" + false + "}";
        }
    }

    private String responseSuccess(String successMessage) {
        try {
            return (new ObjectMapper()).writeValueAsString(new ChaincodeResponse(successMessage, "", true));
        } catch (Throwable e) {
            return "{\"message\":'" + e.getMessage() + " BUT " + successMessage + " (NO COMMIT)', \"OK\":" + false + "}";
        }
    }

    private String responseSuccessObject(String object) {
        return "{\"message\":" + object + ", \"OK\":" + true + "}";
    }

    private boolean checkString(String str) {
        if (str.trim().length() <= 0 || str == null)
            return false;
        return true;
    }
    
    
    /**
     * The Init function must be implemented in Chaincode and will be called automatically whenever we instantiate or upgrade the Chaincode. 
     * Usually, it is used to initialize some data in the Blockchain.
     * @param stub
     */
    @Override
    public Response init(ChaincodeStub stub) {
        return newSuccessResponse(responseSuccess("Init"));
    }

    /**
     * The Invoke function is used to receive all user function calls, then based on the call, 
     * it invokes (calls) the corresponding function — (1), (2), or (3). It is like a router — routing incoming requests to a different path.
     */
    @Override
    public Response invoke(ChaincodeStub stub) {
        String func = stub.getFunction();
        List<String> params = stub.getParameters();
        if (func.equals("createWallet"))
            return createWallet(stub, params);
        else if (func.equals("getWallet"))
            return getWallet(stub, params);
        else if (func.equals("transfer"))
            return transfer(stub, params);
        return newErrorResponse(responseError("Unsupported method", ""));
    }

    /**
     * User wallet creation, command to invoke '{"Args":["createWallet","alice","100"]}''
     * @param stub
     * @param args
     * @return
     */
    private Response createWallet(ChaincodeStub stub, List<String> args) {
        if (args.size() != 2)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 2", ""));
        String walletId = args.get(0);
        String tokenAmount = args.get(1);
        if (!checkString(walletId) || !checkString(tokenAmount))
            return newErrorResponse(responseError("Invalid argument(s)", ""));

        double tokenAmountDouble = 0.0;
        try {
            tokenAmountDouble = Double.parseDouble(tokenAmount);
            if(tokenAmountDouble < 0.0)
                return newErrorResponse(responseError("Invalid token amount", ""));
        } catch (NumberFormatException e) {
            return newErrorResponse(responseError("parseInt error", ""));
        }

        Wallet wallet = new Wallet(walletId, tokenAmountDouble);
        try {
            if(checkString(stub.getStringState(walletId)))
                return newErrorResponse(responseError("Existent wallet", ""));
            stub.putState(walletId, (new ObjectMapper()).writeValueAsBytes(wallet));
            return newSuccessResponse(responseSuccess("Wallet created"));
        } catch (Throwable e) {
            return newErrorResponse(responseError(e.getMessage(), ""));
        }
    }
    /**
     * User wallet query
     * @param stub
     * @param args
     * @return
     */
    private Response getWallet(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 1", ""));
        String walletId = args.get(0);
        if (!checkString(walletId))
            return newErrorResponse(responseError("Invalid argument", ""));
        try {
            String walletString = stub.getStringState(walletId);
            if(!checkString(walletString))
                return newErrorResponse(responseError("Nonexistent wallet", ""));
            return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject(walletString)));
        } catch(Throwable e){
            return newErrorResponse(responseError(e.getMessage(), ""));
        }
    }

    /**
     * Wallet token transfer (i.e. to send money or tokens to from one wallet another wallet)
     * @param stub
     * @param args
     * @return
     */
    private Response transfer(ChaincodeStub stub, List<String> args) {
        if (args.size() != 3)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 3", ""));
        String fromWalletId = args.get(0);
        String toWalletId = args.get(1);
        String tokenAmount = args.get(2);
        if (!checkString(fromWalletId) || !checkString(toWalletId) || !checkString(tokenAmount))
            return newErrorResponse(responseError("Invalid argument(s)", ""));
        if(fromWalletId.equals(toWalletId))
            return newErrorResponse(responseError("From-wallet is same as to-wallet", ""));

        double tokenAmountDouble = 0.0;
        try {
            tokenAmountDouble = Double.parseDouble(tokenAmount);
            if(tokenAmountDouble < 0.0)
                return newErrorResponse(responseError("Invalid token amount", ""));
        } catch (NumberFormatException e) {
            return newErrorResponse(responseError("parseDouble error", ""));
        }

        try {
            String fromWalletString = stub.getStringState(fromWalletId);
            if(!checkString(fromWalletString))
                return newErrorResponse(responseError("Nonexistent from-wallet", ""));
            String toWalletString = stub.getStringState(toWalletId);
            if(!checkString(toWalletString))
                return newErrorResponse(responseError("Nonexistent to-wallet", ""));

            ObjectMapper objectMapper = new ObjectMapper();
            Wallet fromWallet = objectMapper.readValue(fromWalletString, Wallet.class);
            Wallet toWallet = objectMapper.readValue(toWalletString, Wallet.class);

            if(fromWallet.getTokenAmount() < tokenAmountDouble)
                return newErrorResponse(responseError("Token amount not enough", ""));

            fromWallet.setTokenAmount(fromWallet.getTokenAmount() - tokenAmountDouble);
            toWallet.setTokenAmount(toWallet.getTokenAmount() + tokenAmountDouble);
            stub.putState(fromWalletId, objectMapper.writeValueAsBytes(fromWallet));
            stub.putState(toWalletId, objectMapper.writeValueAsBytes(toWallet));

            return newSuccessResponse(responseSuccess("Transferred"));
        } catch(Throwable e){
            return newErrorResponse(responseError(e.getMessage(), ""));
        }
    }

    public static void main(String[] args) {
        new WalletManager().start(args);
    }
}

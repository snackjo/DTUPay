package dtupay.service.customer;

import dtupay.service.DtuPayException;

// @author Carl
public class ResponseObject<T> {
    private T successContent;
    private String exceptionMessage;

    public ResponseObject(T successContent){
        this.successContent = successContent;
    }

    public ResponseObject(String exceptionMessage){
        this.exceptionMessage = exceptionMessage;
    }

    public T getSuccessContentOrThrow() throws DtuPayException {
        if(this.successContent != null){
            return successContent;
        }else{
            throw new DtuPayException(exceptionMessage);
        }
    }

}

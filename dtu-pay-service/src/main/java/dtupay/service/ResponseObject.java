package dtupay.service;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ResponseObject<T> {
    private T successContent;
    private String exceptionMessage;

    public ResponseObject(T successContent){
        this.successContent = successContent;
    }

    public ResponseObject(String exceptionMessage){
        this.exceptionMessage = exceptionMessage;
    }

    public T getSuccessContentOrThrow() throws DTUPayException {
        if(this.successContent != null){
            return successContent;
        }else{
            throw new DTUPayException(exceptionMessage);
        }
    }

}

package apptive.devlog.error;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter //@Getter가 없으면 HttpMediaTypeNotAcceptableException터짐 -> Jackson이 Json으로 변환할때 get이 필요
public class ErrorMessage {

    public Map<String,String> messages;



    public ErrorMessage(Map<String, String> messages) {
        this.messages = messages;
    }
}

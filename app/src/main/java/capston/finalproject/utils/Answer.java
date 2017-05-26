package capston.finalproject.utils;

public class Answer {
    private String ansNo;
    private String answerAnsNo;
    private String ansMem;
    private String ansContent;
    private String ansDate;
    private String arcName;
    private String arcRoute;
    private String ansAnswerCount;

    public Answer(String ansNo, String ansMem, String ansContent, String ansDate, String arcName, String arcRoute, String ansAnswerCount){
        this.ansNo=ansNo;
        this.ansMem=ansMem;
        this.ansContent=ansContent;
        this.ansDate=ansDate;
        this.arcName=arcName;
        this.arcRoute=arcRoute;
        this.ansAnswerCount=ansAnswerCount;
    }

    public Answer(String ansNo, String ansMem, String ansContent, String ansDate){
        this.ansNo=ansNo;
        this.ansMem=ansMem;
        this.ansContent=ansContent;
        this.ansDate=ansDate;
    }
    public String getAnsNo() {
        return ansNo;
    }

    public String getAnswerAnsNo() {
        return answerAnsNo;
    }

    public String getAnsMem() {
        return ansMem;
    }

    public String getAnsContent() {
        return ansContent;
    }

    public String getAnsDate() {
        return ansDate;
    }

    public String getArcName() {
        return arcName;
    }


    public String getArcRoute() {
        return arcRoute;
    }


    public String getAnsAnswerCount() {
        return ansAnswerCount;
    }
}

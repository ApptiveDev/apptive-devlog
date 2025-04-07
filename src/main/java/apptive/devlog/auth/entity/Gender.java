package apptive.devlog.auth.entity;

public enum Gender {
    MALE("남"),
    FEMALE("여");

    private final String label;

    Gender(String label){
        this.label = label;
    }

    public String getLabel(){
        return label;
    }
}

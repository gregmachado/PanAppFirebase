package gregmachado.com.panappfirebase.domain;

/**
 * Created by gregmachado on 24/11/16.
 */
public class Historic {
    private String historicID;
    private String date;
    private String userName, bakeryName;
    private String msg;

    public Historic() {
    }

    public Historic(String historicID, String date, String userName, String bakeryName, String msg) {
        this.historicID = historicID;
        this.date = date;
        this.userName = userName;
        this.bakeryName = bakeryName;
        this.msg = msg;
    }

    public String getHistoricID() {
        return historicID;
    }

    public void setHistoricID(String historicID) {
        this.historicID = historicID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBakeryName() {
        return bakeryName;
    }

    public void setBakeryName(String bakeryName) {
        this.bakeryName = bakeryName;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

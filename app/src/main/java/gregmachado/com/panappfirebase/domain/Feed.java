package gregmachado.com.panappfirebase.domain;

/**
 * Created by gregmachado on 18/11/16.
 */
public class Feed {
    private String feedID;
    private String bakeryID, userID;
    private String date, hour;
    private String userName, bakeryName;
    private String msg;
    private boolean open;
    private int action;

    public Feed() {
    }

    public Feed(String feedID, String bakeryID, String userID, String date, String hour,
                String userName, String bakeryName, String msg, boolean open, int action) {
        this.feedID = feedID;
        this.bakeryID = bakeryID;
        this.userID = userID;
        this.date = date;
        this.hour = hour;
        this.userName = userName;
        this.bakeryName = bakeryName;
        this.msg = msg;
        this.open = open;
        this.action = action;
    }

    public String getFeedID() {
        return feedID;
    }

    public void setFeedID(String feedID) {
        this.feedID = feedID;
    }

    public String getBakeryID() {
        return bakeryID;
    }

    public void setBakeryID(String bakeryID) {
        this.bakeryID = bakeryID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
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

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }
}

package gregmachado.com.panappfirebase.domain;

/**
 * Created by gregmachado on 18/11/16.
 */
public class Feed {
    private String feedID;
    private String bakeryID, userID;
    private String date;
    private String userName, bakeryName;
    private String msg;
    private int action;
    private String image;

    public Feed() {
    }

    public Feed(String feedID, String bakeryID, String userID, String date,
                String userName, String bakeryName, String msg, int action, String image) {
        this.feedID = feedID;
        this.bakeryID = bakeryID;
        this.userID = userID;
        this.date = date;
        this.userName = userName;
        this.bakeryName = bakeryName;
        this.msg = msg;
        this.action = action;
        this.image = image;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }
}

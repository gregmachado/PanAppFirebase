package gregmachado.com.panappfirebase.domain;

/**
 * Created by gregmachado on 01/12/16.
 */
public class SocialMedia {
    private int icon;
    private String name;

    public SocialMedia() {
    }

    public SocialMedia(int icon, String name) {
        this.icon = icon;
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

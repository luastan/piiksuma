package piiksuma;

public class Statistics {
    private User owner;
    private int maxLikeIt;
    private int maxLoveIt;
    private int maxHateIt;
    private int maxMakesMeAngry;
    private int following;
    private int followers;
    private int followBack;

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public int getMaxLikeIt() {
        return maxLikeIt;
    }

    public void setMaxLikeIt(int maxLikeIt) {
        this.maxLikeIt = maxLikeIt;
    }

    public int getMaxLoveIt() {
        return maxLoveIt;
    }

    public void setMaxLoveIt(int maxLoveIt) {
        this.maxLoveIt = maxLoveIt;
    }

    public int getMaxHateIt() {
        return maxHateIt;
    }

    public void setMaxHateIt(int maxHateIt) {
        this.maxHateIt = maxHateIt;
    }

    public int getMaxMakesMeAngry() {
        return maxMakesMeAngry;
    }

    public void setMaxMakesMeAngry(int maxMakesMeAngry) {
        this.maxMakesMeAngry = maxMakesMeAngry;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowBack() {
        return followBack;
    }

    public void setFollowBack(int followBack) {
        this.followBack = followBack;
    }
}

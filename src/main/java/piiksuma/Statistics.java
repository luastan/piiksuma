package piiksuma;

public class Statistics {
    private User owner;
    private long maxLikeIt;
    private long maxLoveIt;
    private long maxHateIt;
    private long maxMakesMeAngry;
    private long following;
    private long followers;
    private long followBack;

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public long getMaxLikeIt() {
        return maxLikeIt;
    }

    public void setMaxLikeIt(long maxLikeIt) {
        this.maxLikeIt = maxLikeIt;
    }

    public long getMaxLoveIt() {
        return maxLoveIt;
    }

    public void setMaxLoveIt(long maxLoveIt) {
        this.maxLoveIt = maxLoveIt;
    }

    public long getMaxHateIt() {
        return maxHateIt;
    }

    public void setMaxHateIt(long maxHateIt) {
        this.maxHateIt = maxHateIt;
    }

    public long getMaxMakesMeAngry() {
        return maxMakesMeAngry;
    }

    public void setMaxMakesMeAngry(long maxMakesMeAngry) {
        this.maxMakesMeAngry = maxMakesMeAngry;
    }

    public long getFollowing() {
        return following;
    }

    public void setFollowing(long following) {
        this.following = following;
    }

    public long getFollowers() {
        return followers;
    }

    public void setFollowers(long followers) {
        this.followers = followers;
    }

    public long getFollowBack() {
        return followBack;
    }

    public void setFollowBack(long followBack) {
        this.followBack = followBack;
    }
}

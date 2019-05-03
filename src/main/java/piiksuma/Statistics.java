package piiksuma;

public class Statistics {
    private User owner;
    private long countLikeIt;
    private long countLoveIt;
    private long countHateIt;
    private long countMakesMeAngry;
    private long following;
    private long followers;
    private long followBack;

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public long getCountLikeIt() {
        return countLikeIt;
    }

    public void setCountLikeIt(long countLikeIt) {
        this.countLikeIt = countLikeIt;
    }

    public long getCountLoveIt() {
        return countLoveIt;
    }

    public void setCountLoveIt(long countLoveIt) {
        this.countLoveIt = countLoveIt;
    }

    public long getCountHateIt() {
        return countHateIt;
    }

    public void setCountHateIt(long countHateIt) {
        this.countHateIt = countHateIt;
    }

    public long getCountMakesMeAngry() {
        return countMakesMeAngry;
    }

    public void setCountMakesMeAngry(long countMakesMeAngry) {
        this.countMakesMeAngry = countMakesMeAngry;
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

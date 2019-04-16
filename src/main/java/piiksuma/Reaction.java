package piiksuma;

import piiksuma.database.MapperColumn;
import piiksuma.database.MapperTable;

import java.util.Objects;

@MapperTable
public class Reaction {
    @MapperColumn(columna = "usr", pkey = true, fKeys = "usr", targetClass = User.class)
    private User user;
    @MapperColumn(pkey = true, fKeys = "post,author", targetClass = Post.class)
    private Post post;

    private ReactionType reactionType;

    @MapperColumn(columna = "reactionType")
    private String strReactionType;

    public Reaction() {
    }

    public Reaction(User user, Post post, ReactionType reactionType) {
        this.user = user;
        this.post = post;
        this.reactionType = reactionType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public ReactionType getReactionType() {
        this.reactionType = ReactionType.valueOf(strReactionType);
        return reactionType;
    }

    public void setReactionType(ReactionType reactionType) {
        this.reactionType = reactionType;
        this.strReactionType = reactionType.toString();
    }

    /**
     * Function to check that the attributes with restriction 'not null' are not null
     *
     * @return the function return "true" if the attributes are not null, otherwise return "false"
     */
    public boolean checkNotNull() {
        // Check that the primary keys are not null
        if (!checkPrimaryKey()) {
            return false;
        }

        return true;
    }

    /**
     * Function to check that the primary keys are not null
     *
     * @return the function return "true" if the primary keys are not null, otherwise return "false"
     */
    public boolean checkPrimaryKey() {
        // Check that the primary keys are not null
        return getUser() != null && getPost() != null && getPost().getPostAuthor() != null;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reaction reaction = (Reaction) o;
        User owner = reaction.getPost().getPostAuthor();
        return user.equals(reaction.user) &&
                owner.equals(reaction.getPost().getPostAuthor()) &&
                post.equals(reaction.post);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, post);
    }
}

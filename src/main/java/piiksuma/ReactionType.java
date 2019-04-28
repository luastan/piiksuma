package piiksuma;

public enum ReactionType {
    LikeIt, LoveIt, HateIt, MakesMeAngry;

    public static ReactionType stringToReactionType(String string) {

        switch(string.toLowerCase()) {

            case "likeit":
                return ReactionType.LikeIt;

            case "loveit":
                return ReactionType.LoveIt;

            case "hateit":
                return ReactionType.HateIt;

            case "makesmeangry":
                return ReactionType.MakesMeAngry;

            default:
                return null;
        }
    }
}

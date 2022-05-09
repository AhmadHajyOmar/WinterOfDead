package de.unisaarland.cs.se.sopra.commands;

import de.unisaarland.cs.se.sopra.ConnectionWrapper;
import de.unisaarland.cs.se.sopra.model.Model;

public class VoteCommand extends BallotBoxCommand {

    boolean vote;

    public VoteCommand(final int commId, final boolean vote) {
        super(commId);
        this.vote = vote;
    }


    @Override
    public void run(final Model model, final ConnectionWrapper connection) {



        if (getVote()) {
            model.addVoteForTheFirstConsequence();
            model.getPlayerByCommId(getCommId()).setVoted(true);
            return;
        }
        model.addVoteForTheSecondConsequence();
        model.getPlayerByCommId(getCommId()).setVoted(true);

    }

    private boolean getVote() {
        return this.vote;
    }
}

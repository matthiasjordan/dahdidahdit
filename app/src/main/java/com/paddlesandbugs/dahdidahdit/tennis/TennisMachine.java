/****************************************************************************
    Dahdidahdit - an Android Morse trainer
    Copyright (C) 2021-2024 Matthias Jordan <matthias@paddlesandbugs.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
****************************************************************************/

package com.paddlesandbugs.dahdidahdit.tennis;

import androidx.annotation.NonNull;

import com.paddlesandbugs.dahdidahdit.R;

/**
 * The basic Morse Tennis state machine.
 * <p>
 * The API is based on just strings. The question this class deals with is "does the message look like the one required to proceed to the next
 * state"?
 */
public class TennisMachine {

    private static final String MESSAGE_END_OF_GAME = "<sk>";

    private static final String MESSAGE_RESTART = "<ka>";

    private final StateInitial stateInitial = new StateInitial(this);

    private final StateEnd stateEnd = new StateEnd(this);

    private final StateInviteReceived stateInviteReceived = new StateInviteReceived(this);

    private final StateInviteAnswered stateInviteAnswered = new StateInviteAnswered(this);

    private final StateInviteSent stateInviteSent = new StateInviteSent(this);

    private final StateInviteAccepted stateInviteAccepted = new StateInviteAccepted(this);

    private final StateStartRoundSender stateStartRoundSender = new StateStartRoundSender(this);

    private final StateWaitForAnswer stateWaitForAnswer = new StateWaitForAnswer(this);

    private final StateStartRoundReceiver stateStartRoundReceiver = new StateStartRoundReceiver(this);

    private final StateChallengeReceived stateChallengeReceived = new StateChallengeReceived(this);

    private final Client client;

    private final GameConfig config;

    private final WordBuffer sendBuffer = new WordBuffer();

    private final WordBuffer receiveBuffer = new WordBuffer();

    private String lastMessageReceived;

    private volatile GameState gameState;

    private volatile State currentState;


    /**
     * Create a new instance with the given message set and scoring rules.
     *
     * @param client  the client implementation for interfacing with the outside world
     * @param msgSet  the message set to use
     * @param scoring the scoring rules to use
     */
    public TennisMachine(Client client, short msgSet, short scoring) {
        this.config = new GameConfig();
        this.client = client;
        configure(msgSet, scoring);
    }


    private void configure(short msgSet, short scoring) {
        setMsgSet(msgSet);
        updateScoringRules(scoring);
    }


    /**
     * Starts the state machine.
     *
     * @return the instance for chaining
     */
    public TennisMachine start() {
        switchToState(stateInitial);
        return this;
    }


    /**
     * Stops the state machine.
     *
     * @return the instance for chaining
     */
    public TennisMachine stop() {
        switchToState(stateEnd);
        return this;
    }


    private void MORSELOGLN(String msg) {
        client.log(msg);
    }


    /**
     * Called with the next word to transmit.
     * <p>
     * The word is added to the internal word buffer and if the tail of the buffer looks like the message expected for the given state, it is
     * processed.
     *
     * @param message the message (a word)
     */
    public void onMessageTransmit(String message) {
        MORSELOGLN("TM::oMT " + message);
        sendBuffer.addWord(message);
        if (sendBuffer.matches(MESSAGE_END_OF_GAME)) {
            MORSELOGLN("TM::oMT got end of game " + MESSAGE_END_OF_GAME);
            client.send(MESSAGE_END_OF_GAME);
            client.printInlineText("usendedgame");
            switchToState(stateEnd);
        } else {
            currentState.onMessageTransmit(sendBuffer);
        }
    }


    /**
     * Called with the next word that was received.
     * <p>
     * The word is added to the internal word buffer and if the tail of the buffer looks like the message expected for the given state, it is
     * processed.
     *
     * @param message the message (a word)
     */
    public void onMessageReceive(String message) {
        MORSELOGLN("TM::oMR " + message);
        lastMessageReceived = message;
        receiveBuffer.addWord(message);
        if (receiveBuffer.matches(MESSAGE_END_OF_GAME)) {
            client.printInlineText("dxendedgame");
            switchToState(stateEnd);
        } else {
            currentState.onMessageReceive(receiveBuffer);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //
    // For testing.
    //


    private void switchToState(State newState) {
        if (currentState != null) {
            currentState.onLeave();
        }

        sendBuffer.getAndClear();
        receiveBuffer.getAndClear();

        newState.onEnter();
        currentState = newState;

        gameState.statusTextId = newState.getStatusTextId();
        client.printScore(gameState);
    }


    private String renderPatternOld(String pattern) {
        String out = pattern;
        out = outReplace(out, "$dx", gameState.dx.call);
        out = outReplace(out, "$us", gameState.us.call);
        return out;
    }


    private String outReplace(String str, String pattern, String replacement) {
        if (pattern != null && replacement != null) {
            return str.replace(pattern, replacement);
        }
        return str;
    }


    GameConfig getGameConfig() {
        return config;
    }


    String getLastMessageReceived() {
        return lastMessageReceived;
    }

    //
    // End testing
    //
    /////////////////////////////////////////////////////////////////////////////////////////////


    String getState() {
        return currentState.getName();
    }


    GameState getGameState() {
        return gameState;
    }


    private void setMsgSet(short msgSetNo) {
        GameConfig gameConfig = getGameConfig();
        gameConfig.msgSet = new MessageSet();
        gameConfig.msgSetNo = msgSetNo;
        System.out.println("MMT::updateMsgSet " + msgSetNo);
        switch (msgSetNo) {
            case 0: {
                gameConfig.msgSet.name = "Basic";
                gameConfig.msgSet.cqCall = "cq mt de #us";
                gameConfig.msgSet.dxdepat = "#dx de #us";
                gameConfig.msgSet.dxdeus = "#dx de #us";
                gameConfig.msgSet.usdedx = "#us de #dx";
                gameConfig.msgSet.usdepat = "#us de #dx";
                gameConfig.msgSet.sendChallenge = "# #";
                gameConfig.msgSet.answerChallenge = "#";
                break;
            }
            case 1: {
                gameConfig.msgSet.name = "Advanced";
                gameConfig.msgSet.cqCall = "cq mt cq mt cq mt de # # # +";
                gameConfig.msgSet.dxdepat = "$dx de # k";
                gameConfig.msgSet.dxdeus = "$dx de $us k";
                gameConfig.msgSet.usdedx = "$us de $dx k";
                gameConfig.msgSet.usdepat = "$us de # k";
                gameConfig.msgSet.sendChallenge = "# #";
                gameConfig.msgSet.answerChallenge = "#";
                break;
            }
            case 2: {
                gameConfig.msgSet.name = "Top notch";
                gameConfig.msgSet.cqCall = "cq mt cq mt cq mt de # # # +";
                gameConfig.msgSet.dxdepat = "$dx de # k";
                gameConfig.msgSet.dxdeus = "$dx de $us k";
                gameConfig.msgSet.usdedx = "$us de $dx k";
                gameConfig.msgSet.usdepat = "$us de # k";
                gameConfig.msgSet.sendChallenge = "$dx de $us # # k";
                gameConfig.msgSet.answerChallenge = "$dx de $us # k";
                break;
            }
        }
    }


    private void updateScoringRules(short scoringRules) {
        GameConfig gameConfig = getGameConfig();
        gameConfig.scoringNo = scoringRules;
        switch (scoringRules) {
            case 0: {
                // Sender gets point for receiver error
                gameConfig.ifCopyOk.senderPoints = 0;
                gameConfig.ifCopyOk.receiverPoints = 0;
                gameConfig.ifCopyNotOk.senderPoints = 1;
                gameConfig.ifCopyNotOk.receiverPoints = 0;
                break;
            }
            case 1: {
                // Receiver gets point for correct copy
                gameConfig.ifCopyOk.senderPoints = 0;
                gameConfig.ifCopyOk.receiverPoints = 1;
                gameConfig.ifCopyNotOk.senderPoints = 0;
                gameConfig.ifCopyNotOk.receiverPoints = 0;
                break;
            }
        }
    }


    private interface State {
        String getName();

        void onMessageReceive(WordBuffer message);

        void onMessageTransmit(WordBuffer message);

        void onEnter();

        void onLeave();

        int getStatusTextId();
    }


    public interface Client {
        void log(String msg);

        void send(String s);

        void printInlineText(String msgId);

        void printScore(GameState g);

        void challengeSound(boolean ok);

        void onChallengeReceived();
    }

    public static class MessageSet {
        String name;

        String cqCall;

        String dxdepat;

        String dxdeus;

        String usdedx;

        String usdepat;

        String sendChallenge;

        String answerChallenge;
    }

    public static class GameConfig {

        short msgSetNo;

        MessageSet msgSet;

        short scoringNo;

        PointDelta ifCopyOk = new PointDelta();

        PointDelta ifCopyNotOk = new PointDelta();

        public static class PointDelta {
            short receiverPoints;

            short senderPoints;
        }
    }

    public static class Station {
        public String call;

        public short points = 0;


        @NonNull
        @Override
        public String toString() {
            return "[" + call + "-" + points + "]";
        }
    }

    /*****************************************************************************
     *
     *  State: INITIAL
     */

    private class StateInitial implements State {

        private final TennisMachine machine;


        public StateInitial(TennisMachine instance) {
            this.machine = instance;
        }


        @Override
        public String getName() {
            return "StateInitial";
        }


        @Override
        public int getStatusTextId() {
            return R.string.action_mopp_morsetennis_status_initial;
        }


        @Override
        public void onEnter() {
            MORSELOGLN("StateInitial entered");
            machine.gameState = new GameState();
            machine.client.printInlineText("intro");
        }


        @Override
        public void onLeave() {
            MORSELOGLN("StateInitial left");
        }


        public void onMessageReceive(WordBuffer rawMessage) {
            MORSELOGLN("StateInitial checking " + rawMessage + " for cq call");

            if (!rawMessage.matches(getGameConfig().msgSet.cqCall)) {
                MORSELOGLN("StateInitial received " + rawMessage + " but that was no cq call");
                return;
            }

            //            machine.gameState.dx.call = rawMessage.getMatch();
            machine.switchToState(machine.stateInviteReceived);
        }


        public void onMessageTransmit(WordBuffer message) {
            String pattern = getGameConfig().msgSet.cqCall;
            if (message.matches(pattern)) {
                String us = message.getMatch("us");
                MORSELOGLN("StateInitial sent cq - off to invite sent - our call: '" + us + "'");
                String text = message.getFullPatternMatch();
                machine.client.send(text);
                machine.gameState.us.call = us;
                machine.switchToState(machine.stateInviteSent);
            } else {
                //        machine.client.print("Send cq to continue!");
            }
        }


    }

    /*****************************************************************************
     *
     *  State: INVITE RECEIVED
     */
    private class StateInviteReceived implements State {

        private final TennisMachine machine;


        public StateInviteReceived(TennisMachine instance) {
            this.machine = instance;
        }


        public String getName() {
            return "StateInviteReceived";
        }


        @Override
        public int getStatusTextId() {
            return R.string.action_mopp_morsetennis_status_invite_received;
        }


        public void onEnter() {
            MORSELOGLN("StateInviteReceived entered - invited by '" + machine.gameState.dx.call + "'");

        }


        public void onLeave() {
            MORSELOGLN("StateInviteReceived left");

        }


        public void onMessageReceive(WordBuffer message) {
            MORSELOGLN("StateInviteReceived received " + message);
        }


        public void onMessageTransmit(WordBuffer message) {
            if (message.matches(getGameConfig().msgSet.dxdepat)) {
                String us = message.getMatch("us");
                MORSELOGLN("ACK sent - off to answered");
                final String fullPatternMatch = message.getFullPatternMatch();
                machine.client.send(fullPatternMatch);
                machine.gameState.us.call = us;
                machine.switchToState(machine.stateInviteAnswered);
            } else if (message.matches(getGameConfig().msgSet.cqCall)) {
                String us = message.getMatch("us");
                MORSELOGLN("CQ sent - off to invite sent");
                final String fullPatternMatch = message.getFullPatternMatch();
                machine.client.send(fullPatternMatch);
                machine.gameState.us.call = us;
                machine.switchToState(machine.stateInviteSent);
            } else {
                //        machine.client.print("Answer call to continue!\n");
            }
        }
    }

    /*****************************************************************************
     *
     *  State: INVITE ANSWERED
     */
    private class StateInviteAnswered implements State {
        private final TennisMachine machine;


        public StateInviteAnswered(TennisMachine instance) {
            this.machine = instance;
        }


        public String getName() {
            return "StateInviteAnswered";
        }


        @Override
        public int getStatusTextId() {
            return R.string.action_mopp_morsetennis_status_invite_answered;
        }


        public void onEnter() {
            MORSELOGLN("StateInviteAnswered entered - dx: '" + machine.gameState.dx.call + "' us: '" + machine.gameState.us.call + "'\n");

        }


        public void onLeave() {
            MORSELOGLN("StateInviteAnswered left");

        }


        public void onMessageReceive(WordBuffer message) {
            MORSELOGLN("StateInviteAnswered received '" + message + "'");
            if (message.matches(getGameConfig().msgSet.usdedx)) {
                String us = message.getMatch("us");
                if (us.equals(machine.gameState.us.call)) {
                    machine.gameState.dx.call = message.getMatch("dx");
                    MORSELOGLN("Game between " + machine.gameState.dx.call + " and " + machine.gameState.us.call);
                    machine.client.printInlineText("gameStarts");
                    machine.switchToState(machine.stateStartRoundReceiver);
                }
            }
        }


        public void onMessageTransmit(WordBuffer message) {
            if (message.matches(getGameConfig().msgSet.cqCall)) {
                String us = message.getMatch();
                MORSELOGLN("CQ sent - off to invite sent");
                final String fullPatternMatch = message.getFullPatternMatch();
                machine.client.send(fullPatternMatch);
                machine.gameState.us.call = us;
                machine.switchToState(machine.stateInviteSent);
            }
        }
    }

    /*****************************************************************************
     *
     *  State: INVITE SENT
     */
    private class StateInviteSent implements State {
        private final TennisMachine machine;


        public StateInviteSent(TennisMachine instance) {
            this.machine = instance;
        }


        public String getName() {
            return "StateInviteSent";
        }


        @Override
        public int getStatusTextId() {
            return R.string.action_mopp_morsetennis_status_invite_sent;
        }


        public void onEnter() {
            MORSELOGLN("StateInviteSent entered by '" + machine.gameState.us.call + "'");
        }


        public void onLeave() {
            MORSELOGLN("StateInviteSent left");

        }


        public void onMessageReceive(WordBuffer message) {
            MORSELOGLN("StateInviteSent received '" + message + "'");
            if (message.matches(getGameConfig().msgSet.usdepat)) {
                String dxCall = message.getMatch();
                MORSELOGLN("Received ACK from " + dxCall + " - off to state invite accepted");
                //                machine.gameState.dx.call = dxCall;
                machine.switchToState(machine.stateInviteAccepted);
            }
        }


        public void onMessageTransmit(WordBuffer message) {
            if (message.matches(getGameConfig().msgSet.dxdeus)) {
                String us = message.getMatch();
                MORSELOGLN("ACK sent - off to answered");
                final String fullPatternMatch = message.getFullPatternMatch();
                machine.client.send(fullPatternMatch);
                machine.gameState.us.call = us;
                machine.switchToState(machine.stateInviteAnswered);
            }
        }
    }

    /*****************************************************************************
     *
     *  State: INVITE ACCEPTED
     */
    private class StateInviteAccepted implements State {

        private final TennisMachine machine;


        public StateInviteAccepted(TennisMachine instance) {
            this.machine = instance;
        }


        public String getName() {
            return "StateInviteAccepted";
        }


        @Override
        public int getStatusTextId() {
            return R.string.action_mopp_morsetennis_status_invite_accepted;
        }


        public void onEnter() {
            MORSELOGLN("StateInviteAccepted entered dx: '" + machine.gameState.dx.call + "' us: '" + machine.gameState.us.call + "'");
        }


        public void onLeave() {
            MORSELOGLN("StateInviteAccepted left");

        }


        public void onMessageReceive(WordBuffer message) {
            MORSELOGLN("StateInviteAccepted received " + message + "");
        }


        public void onMessageTransmit(WordBuffer message) {
            String pattern = getGameConfig().msgSet.dxdeus;
            if (message.matches(pattern)) {
                machine.gameState.dx.call = message.getMatch("dx");
                machine.gameState.us.call = message.getMatch("us");
                machine.client.send(message.getFullPatternMatch());
                machine.switchToState(machine.stateStartRoundSender);
            }
        }
    }

    /*****************************************************************************
     *
     *  State: START ROUND SENDER
     */
    private class StateStartRoundSender implements State {

        private final TennisMachine machine;

        private boolean firstAttempt;


        public StateStartRoundSender(TennisMachine instance) {
            this.machine = instance;
        }


        public String getName() {
            return "StateStartRoundSender";
        }


        @Override
        public int getStatusTextId() {
            return R.string.action_mopp_morsetennis_status_start_round_sender;
        }


        public void onEnter() {
            MORSELOGLN("StateStartRoundSender entered");
            machine.client.printInlineText("giveWordTwice");
            firstAttempt = true;
        }


        public void onLeave() {
            MORSELOGLN("StateStartRoundSender left");
        }


        public void onMessageReceive(WordBuffer message) {
            MORSELOGLN("StateStartRoundSender received " + message + "");
        }


        public void onMessageTransmit(WordBuffer message) {
            if (message.matches(getGameConfig().msgSet.sendChallenge)) {
                // Send test passed
                String challenge = message.getMatch();
                machine.gameState.challenge = challenge;
                machine.client.send(challenge);
                machine.client.challengeSound(true);
                machine.switchToState(machine.stateWaitForAnswer);
            } else {
                if (!firstAttempt) {
                    // Send test failed
                    machine.client.challengeSound(false);
                }
            }

            firstAttempt = false;
        }
    }

    /*****************************************************************************
     *
     *  State: WAIT FOR ANSWER
     */
    private class StateWaitForAnswer implements State {

        private final TennisMachine machine;


        public StateWaitForAnswer(TennisMachine instance) {
            this.machine = instance;
        }


        public String getName() {
            return "StateWaitForAnswer";
        }


        @Override
        public int getStatusTextId() {
            return R.string.action_mopp_morsetennis_status_wait_for_answer;
        }


        public void onEnter() {
            MORSELOGLN("StateWaitForAnswer entered");
        }


        public void onLeave() {
            MORSELOGLN("StateWaitForAnswer left");
        }


        public void onMessageReceive(WordBuffer message) {
            MORSELOGLN("StateWaitForAnswer received " + message);
            if (message.matches(machine.gameState.challenge)) {
                machine.gameState.us.points += machine.config.ifCopyOk.senderPoints;
                machine.gameState.dx.points += machine.config.ifCopyOk.receiverPoints;
            } else {
                machine.gameState.us.points += machine.config.ifCopyNotOk.senderPoints;
                machine.gameState.dx.points += machine.config.ifCopyNotOk.receiverPoints;
            }
            machine.client.printScore(machine.gameState);
            machine.switchToState(machine.stateStartRoundReceiver);
        }


        public void onMessageTransmit(WordBuffer message) {
            machine.client.printInlineText("waitForDx");
        }
    }

    /*****************************************************************************
     *
     *  State: START ROUND RECEIVER
     */
    private class StateStartRoundReceiver implements State {

        private final TennisMachine machine;


        public StateStartRoundReceiver(TennisMachine instance) {
            this.machine = instance;
        }


        public String getName() {
            return "StateStartRoundReceiver";
        }


        @Override
        public int getStatusTextId() {
            return R.string.action_mopp_morsetennis_status_start_round_receiver;
        }


        public void onEnter() {
            MORSELOGLN("StateStartRoundReceiver entered");

        }


        public void onLeave() {
            MORSELOGLN("StateStartRoundReceiver left");
        }


        public void onMessageReceive(WordBuffer message) {
            MORSELOGLN("StateStartRoundReceiver received " + message);
            String pattern = "#";
            if (message.matches(pattern)) {
                machine.gameState.challenge = message.getMatch();
                machine.switchToState(machine.stateChallengeReceived);
            }
        }


        public void onMessageTransmit(WordBuffer message) {
            machine.client.printInlineText("waitForDx");
        }
    }

    /*****************************************************************************
     *
     *  State: CHALLENGE RECEIVED
     */
    private class StateChallengeReceived implements State {

        private final TennisMachine machine;


        public StateChallengeReceived(TennisMachine instance) {
            this.machine = instance;
        }


        public String getName() {
            return "StateChallengeReceived";
        }


        @Override
        public int getStatusTextId() {
            return R.string.action_mopp_morsetennis_status_challenge_received;
        }


        public void onEnter() {
            MORSELOGLN("StateChallengeReceived entered");
            machine.client.onChallengeReceived();
        }


        public void onLeave() {
            MORSELOGLN("StateChallengeReceived left\n");
        }


        public void onMessageReceive(WordBuffer message) {
            MORSELOGLN("StateChallengeReceived received " + message);
        }


        public void onMessageTransmit(WordBuffer message) {
            if (message.matches(machine.gameState.challenge)) {
                // Challenge passed
                machine.client.printInlineText("challengePassed");
                machine.client.challengeSound(true);
                machine.gameState.us.points += machine.config.ifCopyOk.receiverPoints;
                machine.gameState.dx.points += machine.config.ifCopyOk.senderPoints;
            } else {
                machine.gameState.us.points += machine.config.ifCopyNotOk.receiverPoints;
                machine.gameState.dx.points += machine.config.ifCopyNotOk.senderPoints;
                machine.client.printInlineText("challengeFailed");
                machine.client.challengeSound(false);
            }
            machine.client.printScore(machine.gameState);
            machine.client.send(message.get());
            machine.switchToState(machine.stateStartRoundSender);
        }
    }

    /*****************************************************************************
     *
     *  State: END
     */

    private class StateEnd implements State {

        private final TennisMachine machine;


        public StateEnd(TennisMachine instance) {
            this.machine = instance;
        }


        public String getName() {
            return "StateEnd";
        }


        @Override
        public int getStatusTextId() {
            return R.string.action_mopp_morsetennis_status_end;
        }


        public void onEnter() {
            //            machine.client.print("\nGame ended\n");
            machine.client.printScore(machine.gameState);
            machine.client.printInlineText("outro");
            machine.gameState.dx.call = null;
            machine.gameState.dx.points = 0;
        }


        public void onLeave() {
            MORSELOGLN("StateEnd left\n");
        }


        public void onMessageReceive(WordBuffer message) {
            MORSELOGLN("StateEnd received " + message + "\n");
        }


        public void onMessageTransmit(WordBuffer message) {
            if (message.matches(MESSAGE_RESTART)) {
                machine.switchToState(machine.stateInitial);
            }
        }
    }

    public class GameState {
        public final Station dx = new Station();

        public final Station us = new Station();

        public int statusTextId;

        public String challenge = null;


        @NonNull
        @Override
        public String toString() {
            return us + " : " + dx + " (" + challenge + ")";
        }
    }
}

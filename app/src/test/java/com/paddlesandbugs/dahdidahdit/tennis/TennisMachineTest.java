package com.paddlesandbugs.dahdidahdit.tennis;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TennisMachineTest {

    private final PubSub ps = new PubSub();

    private TennisMachine sutA;

    private TennisMachine sutB;

    private TennisMachine sutC;


    private static void send(TennisMachine sutA, String cqDeAa1a) {
        sutA.onMessageTransmit(cqDeAa1a);
    }


    private static void assertStates(TennisMachine sutA, String aState, TennisMachine sutB, String bState) {
        Assert.assertEquals(aState, sutA.getState());
        Assert.assertEquals(bState, sutB.getState());
    }


    private static void assertStates(TennisMachine sutA, String aState, TennisMachine sutB, String bState, TennisMachine sutC, String cState) {
        Assert.assertEquals(aState, sutA.getState());
        Assert.assertEquals(bState, sutB.getState());
        Assert.assertEquals(cState, sutC.getState());
    }


    private static void assertGameState(TennisMachine sut, String dxcall, int dxp, String uscall, int usp, String challenge) {
        Assert.assertEquals(dxcall, sut.getGameState().dx.call);
        Assert.assertEquals((short) dxp, sut.getGameState().dx.points);
        Assert.assertEquals(uscall, sut.getGameState().us.call);
        Assert.assertEquals((short) usp, sut.getGameState().us.points);
        Assert.assertEquals(challenge, sut.getGameState().challenge);
    }


    private static void assertGameState(TennisMachine sut, String state, String dxcall, int dxp, String uscall, int usp, String challenge) {
        Assert.assertEquals(state, sut.getState());
        Assert.assertEquals(dxcall, sut.getGameState().dx.call);
        Assert.assertEquals((short) dxp, sut.getGameState().dx.points);
        Assert.assertEquals(uscall, sut.getGameState().us.call);
        Assert.assertEquals((short) usp, sut.getGameState().us.points);
        Assert.assertEquals(challenge, sut.getGameState().challenge);
    }


    @Before
    public void setup() {
        sutA = create("A");
        sutB = create("B");
        sutC = create("C");

        assertStates(sutA, "StateInitial", sutB, "StateInitial", sutC, "StateInitial");
        assertGameState(sutA, "StateInitial", null, 0, null, 0, null);
        assertGameState(sutB, "StateInitial", null, 0, null, 0, null);
        assertGameState(sutC, "StateInitial", null, 0, null, 0, null);
    }


    private TennisMachine create(String name) {
        final TestClient clientA = new TestClient(name);
        TennisMachine sut = new TennisMachine(clientA, (short) 0, (short) 0);
        ps.add(name, sut::onMessageReceive);
        sut.start();

        return sut;
    }


    @Test
    public void testFullGame() {
        // A sends
        send(sutA, "cq mt de aa1a");

        // B receives
        Assert.assertEquals("cq mt de aa1a", sutB.getLastMessageReceived());

        // Test state
        assertStates(sutA, "StateInviteSent", sutB, "StateInviteReceived");
        assertGameState(sutA, null, 0, "aa1a", 0, null);
        assertGameState(sutB, null, 0, null, 0, null);

        /////////////////////////////////////////////////////
        //
        // Next pass - B answers call.

        // B sends
        send(sutB, "aa1a de bb1b");

        // A receives
        Assert.assertEquals("aa1a de bb1b", sutA.getLastMessageReceived());

        // Test state
        assertStates(sutA, "StateInviteAccepted", sutB, "StateInviteAnswered");
        assertGameState(sutA, null, 0, "aa1a", 0, null);
        assertGameState(sutB, null, 0, "bb1b", 0, null);

        /////////////////////////////////////////////////////
        //
        // Next pass - A accepts B.

        // A sends
        send(sutA, "bb1b de aa1a");

        // B receives
        Assert.assertEquals("bb1b de aa1a", sutB.getLastMessageReceived());

        // Test state
        assertStates(sutA, "StateStartRoundSender", sutB, "StateStartRoundReceiver");
        assertGameState(sutA, "bb1b", 0, "aa1a", 0, null);
        assertGameState(sutB, "aa1a", 0, "bb1b", 0, null);

        /////////////////////////////////////////////////////
        //
        // Next pass - A sends.

        // A sends - word successful.
        send(sutA, "a1 a1");

        // B receives
        Assert.assertEquals("a1", sutB.getLastMessageReceived());

        // Test state
        assertStates(sutA, "StateWaitForAnswer", sutB, "StateChallengeReceived");
        assertGameState(sutA, "bb1b", 0, "aa1a", 0, "a1");
        assertGameState(sutB, "aa1a", 0, "bb1b", 0, "a1");

        /////////////////////////////////////////////////////
        //
        // B tries to copy.

        // B sends - word incorrect, A scores.
        send(sutB, "ax");

        // A receives
        Assert.assertEquals("ax", sutA.getLastMessageReceived());

        // Test state
        assertStates(sutA, "StateStartRoundReceiver", sutB, "StateStartRoundSender");
        assertGameState(sutA, "bb1b", 0, "aa1a", 1, "a1");
        assertGameState(sutB, "aa1a", 1, "bb1b", 0, "a1");

        /////////////////////////////////////////////////////
        //
        // Next pass - B's turn to send.

        // B sends - word correct
        send(sutB, "a2 a2");

        // A receives - challenge
        Assert.assertEquals("a2", sutA.getLastMessageReceived());

        // Test state
        assertStates(sutA, "StateChallengeReceived", sutB, "StateWaitForAnswer");
        assertGameState(sutA, "bb1b", 0, "aa1a", 1, "a2");
        assertGameState(sutB, "aa1a", 1, "bb1b", 0, "a2");

        /////////////////////////////////////////////////////
        //
        // A tries to copy.

        // A sends - word correct.
        send(sutA, "a2");

        // B receives -
        Assert.assertEquals("a2", sutB.getLastMessageReceived());

        // Test state
        assertStates(sutA, "StateStartRoundSender", sutB, "StateStartRoundReceiver");
        assertGameState(sutA, "bb1b", 0, "aa1a", 1, "a2");
        assertGameState(sutB, "aa1a", 1, "bb1b", 0, "a2");

        /////////////////////////////////////////////////////
        //
        // Next pass - A's turn to send.

        // A sends - word correct.
        send(sutA, "a3 a3");

        // B receives - nothing
        Assert.assertEquals("a3", sutB.getLastMessageReceived());

        // Test state
        assertStates(sutA, "StateWaitForAnswer", sutB, "StateChallengeReceived");
        assertGameState(sutA, "bb1b", 0, "aa1a", 1, "a3");
        assertGameState(sutB, "aa1a", 1, "bb1b", 0, "a3");

        /////////////////////////////////////////////////////
        //
        // B tries to copy.

        // B sends - word correct.
        send(sutB, "a3");

        // A receives
        Assert.assertEquals("a3", sutA.getLastMessageReceived());

        // Test state
        assertStates(sutA, "StateStartRoundReceiver", sutB, "StateStartRoundSender");
        assertGameState(sutA, "bb1b", 0, "aa1a", 1, "a3");
        assertGameState(sutB, "aa1a", 1, "bb1b", 0, "a3");

        /////////////////////////////////////////////////////
        //
        // Next pass - B's turn to send.

        // B sends - word correct.
        send(sutB, "a4 a4");

        // A receives
        Assert.assertEquals("a4", sutA.getLastMessageReceived());

        // Test state
        assertStates(sutA, "StateChallengeReceived", sutB, "StateWaitForAnswer");
        assertGameState(sutA, "bb1b", 0, "aa1a", 1, "a4");
        assertGameState(sutB, "aa1a", 1, "bb1b", 0, "a4");

        /////////////////////////////////////////////////////
        //
        // A tries to copy.

        // A sends - word wrong - B scores.
        send(sutA, "ax");

        // B receives
        Assert.assertEquals("ax", sutB.getLastMessageReceived());

        // Test state
        assertStates(sutA, "StateStartRoundSender", sutB, "StateStartRoundReceiver");
        assertGameState(sutA, "bb1b", 1, "aa1a", 1, "a4");
        assertGameState(sutB, "aa1a", 1, "bb1b", 1, "a4");

        /////////////////////////////////////////////////////
        //
        // Next pass - A's turn to send.

        // A sends - word wrong
        send(sutA, "a5 aa");

        // B receives - nothing
        Assert.assertEquals("ax", sutB.getLastMessageReceived());

        // Test state
        assertStates(sutA, "StateStartRoundSender", sutB, "StateStartRoundReceiver");
        assertGameState(sutA, "bb1b", 1, "aa1a", 1, "a4");
        assertGameState(sutB, "aa1a", 1, "bb1b", 1, "a4");

        /////////////////////////////////////////////////////
        //
        // Next pass - A's next turn to send.

        // A sends - word wrong
        send(sutA, "a5 a5");

        // B receives - nothing
        Assert.assertEquals("a5", sutB.getLastMessageReceived());

        // Test state
        assertStates(sutA, "StateWaitForAnswer", sutB, "StateChallengeReceived");
        assertGameState(sutA, "bb1b", 1, "aa1a", 1, "a5");
        assertGameState(sutB, "aa1a", 1, "bb1b", 1, "a5");

        /////////////////////////////////////////////////////
        //
        // B tries to copy.

        // B sends - word incorrect - A scores.
        send(sutB, "ax");

        // A receives
        Assert.assertEquals("ax", sutA.getLastMessageReceived());

        // Test state
        assertStates(sutA, "StateStartRoundReceiver", sutB, "StateStartRoundSender");
        assertGameState(sutA, "bb1b", 1, "aa1a", 2, "a5");
        assertGameState(sutB, "aa1a", 2, "bb1b", 1, "a5");
    }


    @Test
    public void testNegotiation1() {

        send(sutA, "cq mt de aa1a");

        assertGameState(sutA, "StateInviteSent", null, 0, "aa1a", 0, null);
        assertGameState(sutB, "StateInviteReceived", null, 0, null, 0, null);
        assertGameState(sutC, "StateInviteReceived", null, 0, null, 0, null);

        /////////////////////////////////////////////////////
        //
        // Next pass - B answers call.

        send(sutB, "aa1a de bb1b");

        // Test state
        assertGameState(sutA, "StateInviteAccepted", null, 0, "aa1a", 0, null);
        assertGameState(sutB, "StateInviteAnswered", null, 0, "bb1b", 0, null);
        assertGameState(sutC, "StateInviteReceived", null, 0, null, 0, null);

        /////////////////////////////////////////////////////
        //
        // Next pass - C answers call, too.

        send(sutC, "aa1a de cc1c");

        // Test state
        assertGameState(sutA, "StateInviteAccepted", null, 0, "aa1a", 0, null);
        assertGameState(sutB, "StateInviteAnswered", null, 0, "bb1b", 0, null);
        assertGameState(sutC, "StateInviteAnswered", null, 0, "cc1c", 0, null);

        /////////////////////////////////////////////////////
        //
        // Next pass - A answers B.

        send(sutA, "bb1b de aa1a");

        // Test state
        assertGameState(sutA, "StateStartRoundSender", "bb1b", 0, "aa1a", 0, null);
        assertGameState(sutB, "StateStartRoundReceiver", "aa1a", 0, "bb1b", 0, null);
        assertGameState(sutC, "StateInviteAnswered", null, 0, "cc1c", 0, null);

        /////////////////////////////////////////////////////
        //
        // Next pass - A answers B, but B is already gone.

        send(sutA, "bb1b de aa1a");

        // Test state
        assertGameState(sutA, "StateStartRoundSender", "bb1b", 0, "aa1a", 0, null);
        assertGameState(sutB, "StateStartRoundReceiver", "aa1a", 0, "bb1b", 0, null);
        assertGameState(sutC, "StateInviteAnswered", null, 0, "cc1c", 0, null);

        /////////////////////////////////////////////////////
        //
        // Next pass - A answers C but the game has already started so they must quit using <sk>.

        send(sutA, "cc1c de aa1a");

        // Test state
        assertGameState(sutA, "StateStartRoundSender", "bb1b", 0, "aa1a", 0, null);
        assertGameState(sutB, "StateStartRoundReceiver", "aa1a", 0, "bb1b", 0, null);
        assertGameState(sutC, "StateInviteAnswered", null, 0, "cc1c", 0, null);

        /////////////////////////////////////////////////////
        //
        // Next pass - A quit using <sk>.

        send(sutA, "<sk>");

        // Test state
        assertGameState(sutA, "StateEnd", null, 0, "aa1a", 0, null);
        assertGameState(sutB, "StateEnd", null, 0, "bb1b", 0, null);
        assertGameState(sutC, "StateEnd", null, 0, "cc1c", 0, null);
    }


    private static class PubSub {

        private final Map<String, Consumer<String>> consumers = new HashMap<>();


        void add(String name, Consumer<String> c) {
            consumers.put(name, c);
        }


        void publish(String senderName, String msg) {
            for (Map.Entry<String, Consumer<String>> consumer : consumers.entrySet()) {
                if (!consumer.getKey().equals(senderName)) {
                    consumer.getValue().accept(msg);
                }
            }
        }
    }

    private class TestClient implements TennisMachine.Client {


        private final String name;


        private TestClient(String name) {
            this.name = name;
        }


        @Override
        public void log(String msg) {
            System.out.println("[" + name + "] " + msg);
        }


        @Override
        public void send(String s) {
            System.out.println("[" + name + "]> " + s);
            ps.publish(name, s);
        }


        @Override
        public void printInlineText(String s) {
            System.out.println("[" + name + "]" + s);
        }


        @Override
        public void printScore(TennisMachine.GameState g) {
            System.out.println("[" + name + "]" + g);
        }


        @Override
        public void challengeSound(boolean ok) {
            System.out.println("[" + name + "] Sound played: " + (ok ? "OK" : "ERR"));
        }


        @Override
        public void onChallengeReceived() {

        }
    }
}

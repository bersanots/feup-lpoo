package Controller;

import Model.Arena;
import View.*;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.io.IOException;

import static java.lang.Thread.sleep;

public class Game {

    private int interface_op;
    private Interface interf;
    private Arena arena;
    int level;

    public Game(int interface_op) throws IOException {
        this.interface_op = interface_op;
        if (interface_op == 1)
            this.interf = new Lanterna();
        else
            this.interf = new Swing();
        this.level = 1;
        this.arena = new Arena(100, 25, level);
    }

    public Arena getArena() {
        return arena;
    }

    public Interface getInterface() {
        return interf;
    }

    private void draw() throws IOException {
        if (interface_op == 1) {
            interf.getScreen().clear();
            arena.draw(interf.getScreen().newTextGraphics());
            interf.getScreen().refresh();
        } else {
            arena.draw(interf.getGameWindow(), interf.getGraphics());
        }
    }

    private void processKey(KeyStroke key, int i) {
        arena.processKey(key, i);
    }

    private void endingMessage(boolean hasWon) throws IOException, InterruptedException {
        if (interface_op == 1) {
            interf.getScreen().clear();
            if (hasWon) {
                interf.getScreen().setCharacter(10, 10, new TextCharacter('G'));
                interf.getScreen().setCharacter(11, 10, new TextCharacter('A'));
                interf.getScreen().setCharacter(12, 10, new TextCharacter('M'));
                interf.getScreen().setCharacter(13, 10, new TextCharacter('E'));
                interf.getScreen().setCharacter(14, 10, new TextCharacter(' '));
                interf.getScreen().setCharacter(15, 10, new TextCharacter('W'));
                interf.getScreen().setCharacter(16, 10, new TextCharacter('O'));
                interf.getScreen().setCharacter(17, 10, new TextCharacter('N'));
            } else {
                interf.getScreen().setCharacter(10, 10, new TextCharacter('G'));
                interf.getScreen().setCharacter(11, 10, new TextCharacter('A'));
                interf.getScreen().setCharacter(12, 10, new TextCharacter('M'));
                interf.getScreen().setCharacter(13, 10, new TextCharacter('E'));
                interf.getScreen().setCharacter(14, 10, new TextCharacter(' '));
                interf.getScreen().setCharacter(15, 10, new TextCharacter('L'));
                interf.getScreen().setCharacter(16, 10, new TextCharacter('O'));
                interf.getScreen().setCharacter(17, 10, new TextCharacter('S'));
                interf.getScreen().setCharacter(18, 10, new TextCharacter('T'));
            }
            interf.getScreen().refresh();
            sleep(2000);
            interf.getScreen().close();
        } else {
            interf.gameEndingMessage(hasWon);
        }
    }

    public void run() throws IOException, InterruptedException {
        while (level < 3) {
            this.arena = new Arena(100, 25, level);
            if (!run_level()) {
                endingMessage(false);
                return;
            }
            level++;
        }
        endingMessage(true);
    }

    public boolean run_level() throws IOException, InterruptedException {
        if (interface_op == 1) {
            KeyStroke key;
            int i = 0;
            do {
                sleep(33);
                draw();
                key = interf.getScreen().pollInput();
                if (key != null)
                    if (key.getKeyType() == KeyType.Character && key.getCharacter() == 'q') {
                        interf.getScreen().close();
                        return false;
                    }
                processKey(key, i);
                i++;
            } while ((key == null || key.getKeyType() != KeyType.EOF) && !arena.isGameOver());

        } else {
            int i = 0;
            do {
                sleep(330);
                interf.setLives(arena.getHero().getLives());
                interf.setKeys(arena.getHero().getKeys());
                draw();
                processKey(interf.getDirection(), i);
                interf.resetDirection();
                i++;
            } while (!arena.isGameOver() && !interf.isWindowClosed());
        }

        return arena.hasWon();
    }
}

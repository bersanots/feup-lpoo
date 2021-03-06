package Model;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.googlecode.lanterna.input.KeyType.*;


public class Wizard extends Character {
    KeyType key;
    int speed;
    int spell_speed;
    Spell spell;
    boolean spellFired;
    int spell_cooldown;

    public Wizard(int x, int y, boolean vertical, int speed, int spell_speed) {
        super(x, y);
        if (vertical)
            key = ArrowDown;
        else
            key = ArrowRight;
        this.speed = speed;

        spell = null;

        spellFired = false;

        this.spell_speed = spell_speed;

        spell_cooldown = 0;

        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        img_path = s + "\\src\\main\\resources\\wizard.jpg";
    }

    public Spell getSpell(){return spell;}

    public void move(int time){
        if (time % speed != 0)
            return;
        switch (key) {
            case ArrowUp:
                moveUp();
                break;
            case ArrowRight:
                moveRight();
                break;
            case ArrowDown:
                moveDown();
                break;
            case ArrowLeft:
                moveLeft();
                break;
        }
    }

    public void moveSpell(int time,List<Wall> walls) {
        if (spellFired){
            if (spell.testCollisions(walls,new KeyStroke(KeyType.ArrowRight))){
                spell.move(time);
            }
            else eraseSpell(time);
        }
    }

    public void eraseSpell(int time){
        if (time % spell.getSpeed() != 0) return;
        spell = null;
        spellFired = false;
    }

    public void newSpell(List<Wall> walls, Hero hero){
        if (spell_cooldown > 0){
            spell_cooldown--;
            return;
        }
        if (spellFired) return;
        if (test_spell_path(walls, hero)){
            spellFired = true;
            spell_cooldown = 60;
            if(pos.getX() == hero.getPos().getX()){
                if (pos.getY() < hero.getPos().getY())
                    spell = new Spell(pos.getX(),pos.getY(),ArrowDown,spell_speed);
                else
                    spell = new Spell(pos.getX(),pos.getY(),ArrowUp,spell_speed);
            }
            else{
                if (pos.getX() < hero.getPos().getX())
                    spell = new Spell(pos.getX(),pos.getY(),ArrowRight,spell_speed);
                else
                    spell = new Spell(pos.getX(),pos.getY(),ArrowLeft,spell_speed);
            }

        }

    }

    public boolean test_spell_path(List<Wall> walls, Hero hero){
        int i = 0;
        while (i < walls.size()){
            if (!walls.get(i).testPath(pos, hero.getPos()))
                return false;
            i++;
        }
        return true;

    }



    public void turnAround() {
        switch (key) {
            case ArrowUp:
                key = ArrowDown;
                break;
            case ArrowRight:
                key = ArrowLeft;
                break;
            case ArrowDown:
                key = ArrowUp;
                break;
            case ArrowLeft:
                key = ArrowRight;
                break;
        }
    }

    @Override
    public void draw(TextGraphics graphics) {
        if (spellFired)
            spell.draw(graphics);
        graphics.setForegroundColor(TextColor.Factory.fromString("#FF00FF"));
        graphics.enableModifiers(SGR.BOLD);
        graphics.putString(new TerminalPosition(pos.getX()*2, pos.getY()), "W");
    }

    @Override
    public void draw(MyComponent component, Graphics graphics) {
        if (spellFired)
            spell.draw(component, graphics);
        component.paintComponent(graphics, pos.getX() * 20, pos.getY() * 20, img_path);
    }

    @Override
    public boolean testCollisions(List<Wall> walls, KeyStroke k) {
        k = new KeyStroke(key);
        int i = 0;
        while (i < walls.size()){
            if (!walls.get(i).testCollisions(pos, k))
                return false;
            i++;
        }
        return true;
    }

}

package net.runelite.client.plugins.iutils.bot;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.inject.Inject;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.plugins.iutils.api.Interactable;
import net.runelite.client.plugins.iutils.iUtils;
import net.runelite.client.plugins.iutils.scene.Locatable;
import net.runelite.client.plugins.iutils.scene.ObjectCategory;
import net.runelite.client.plugins.iutils.scene.ObjectType;
import net.runelite.client.plugins.iutils.scene.Position;

public class iObject implements Locatable, Interactable {

    private Client client;
    private Bot bot;

    public TileObject tileObject;
    public ObjectCategory type;
    public ObjectComposition definition;

    public iObject(Bot bot, Client client, TileObject tileObject, ObjectCategory type, ObjectComposition definition) {
        this.client = client;
        this.bot = bot;
        this.tileObject = tileObject;
        this.type = type;
        this.definition = definition;
    }

    //	@Override
    public Bot bot() {
        return bot;
    }

    @Override
    public Client client() {
        return client;
    }


    @Override
    public Position position() {
        return new Position(tileObject.getWorldLocation());
    }

    public LocalPoint localPoint() {
        return tileObject.getLocalLocation();
    }

    /**
     * The {@link ObjectType} of the object.
     */
    public ObjectCategory type() {
        return type;
    }

    public int id() {
        return tileObject.getId();
    }

    public String name() {
        return definition.getName();
    }

    public List<String> actions() {
        return Arrays.stream(definition().getActions())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public ObjectComposition definition() {
//        return client().getObjectDefinition(id());
        return definition;
    }

    @Override
    public void interact(String action) {
        for (int i = 0; i < actions().size(); i++) {
            if (action.equalsIgnoreCase(actions().get(i))) {
                interact(i);
                return;
            }
        }

        throw new IllegalArgumentException("no action \"" + action + "\" on object " + id());
    }

    public void interact(int action) {
        bot().clientThread.invoke(() -> {
            int menuAction;
            Point point;

            switch (action) {
                case 0:
                    menuAction = MenuAction.GAME_OBJECT_FIRST_OPTION.getId();
                    break;
                case 1:
                    menuAction = MenuAction.GAME_OBJECT_SECOND_OPTION.getId();
                    break;
                case 2:
                    menuAction = MenuAction.GAME_OBJECT_THIRD_OPTION.getId();
                    break;
                case 3:
                    menuAction = MenuAction.GAME_OBJECT_FOURTH_OPTION.getId();
                    break;
                case 4:
                    menuAction = MenuAction.GAME_OBJECT_FIFTH_OPTION.getId();
                    break;
                default:
                    throw new IllegalArgumentException("action = " + action);
            }
            ;
            if (type() == ObjectCategory.REGULAR) {
                GameObject temp = (GameObject) tileObject;
                point = temp.getSceneMinLocation();
            } else {
                point = new Point(localPoint().getSceneX(), localPoint().getSceneY());
            }

            client().invokeMenuAction("",
                    "",
                    id(),
                    menuAction,
                    point.getX(),
                    point.getY()
            );
        });
    }
}

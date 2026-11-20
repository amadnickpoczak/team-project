package entity;

import java.util.ArrayList;
import java.util.List;

/**
 * A document: its name, who created it, who it is shared with
 * its text, and the styling of that text
 *
*/

public class Document {
    private final String name;
    private final String owner;
    private final List<String>sharedWith = new ArrayList<>();
    private final String text;
    private final List<TextRun> runs;

    public Document(String name, String owner, String text, List<TextRun> runs){
        this.name = name;
        this.owner = owner;
        this.text = text;
        this.runs = new ArrayList<>(runs);
    }

    public Document(String text, List<TextRun> runs){
        this("Untitled", "", text, runs);
    }

    public String getName(){
        return name;
    }
    public String getOwner(){
        return owner;
    }
    public String getText(){
        return text;
    }
    public List<TextRun> getRuns(){
        return runs;
    }
    public List<String> getSharedWith(){
        return sharedWith;
    }

    public void shareWith(String username){
        if(!canAccess(username)){
            sharedWith.add(username);
        }
    }

    public boolean canAccess(String username){
        return owner.equals(username) || sharedWith.contains(username);
    }
}

package week2assignment.codepath.gridimagesearch.Models;

import java.io.Serializable;

/**
 * Created by vincetulit on 2/14/15.
 */
public class SearchOptions implements Serializable {

    //Store only position, and grab actual string from the strings.xml
    private int posImageSize;
    private int posColorFilter;
    private int posType;
    private String site;

    public SearchOptions() {
        this.posImageSize = 0;
        this.posColorFilter = 0;
        this.posType = 0;
        this.site = "";
    }

    public int getPosImageSize() {
        return posImageSize;
    }

    public void setPosImageSize(int posImageSize) {
        this.posImageSize = posImageSize;
    }

    public int getPosColorFilter() {
        return posColorFilter;
    }

    public void setPosColorFilter(int posColorFilter) {
        this.posColorFilter = posColorFilter;
    }

    public int getPosType() {
        return posType;
    }

    public void setPosType(int posType) {
        this.posType = posType;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
}

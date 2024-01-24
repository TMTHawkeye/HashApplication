package hashtag.generator.hashapplication.ModelClasses;


import java.util.ArrayList;
import java.util.Random;


/**
 * Created by Cuneyt on 21.8.2015.
 *
 */
public class TagClass {

    private String code;
    private String name;
    private String color;
    private Boolean bolee;

    public TagClass() {

    }

    public TagClass(String sinif, String name) {
        this.code = sinif;
        this.name = name;
        this.color = getRandomColor();

    }

    public TagClass(String name,boolean bole){
        this.name=name;
        this.bolee = bole;
    }

    public String getRandomColor() {
        ArrayList<String> colors = new ArrayList<>();
        colors.add("#ED7D31");
        colors.add("#00B0F0");
        colors.add("#FF0000");
        colors.add("#D0CECE");
        colors.add("#00B050");
        colors.add("#9999FF");
        colors.add("#FF5FC6");
        colors.add("#FFC000");
        colors.add("#7F7F7F");
        colors.add("#4800FF");

        return colors.get(new Random().nextInt(colors.size()));
    }

    public Boolean getBolee() {
        return bolee;
    }

    public void setBolee(Boolean bolee) {
        this.bolee = bolee;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSinif() {
        return code;
    }

    public void setSinif(String code) {
        this.code = code;
    }


}

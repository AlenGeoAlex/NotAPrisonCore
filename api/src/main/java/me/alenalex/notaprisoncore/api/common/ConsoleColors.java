package me.alenalex.notaprisoncore.api.common;


import com.diogonunes.jcolor.AnsiFormat;
import com.diogonunes.jcolor.Attribute;

public class ConsoleColors {

    public static AnsiFormat WHITE = new AnsiFormat(Attribute.WHITE_TEXT());
    public static AnsiFormat GRAY = new AnsiFormat(Attribute.TEXT_COLOR(244));
    public static AnsiFormat YELLOW = new AnsiFormat(Attribute.YELLOW_TEXT());
    public static AnsiFormat RED = new AnsiFormat(Attribute.RED_TEXT());
    public static AnsiFormat CYAN = new AnsiFormat(Attribute.CYAN_TEXT());
    public static AnsiFormat RESET = new AnsiFormat(Attribute.CLEAR());

}

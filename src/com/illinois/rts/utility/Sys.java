package com.illinois.rts.utility;

/**
 * Created by dogs0 on 10/25/2016.
 */
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Sys {

    /**
     * returns true if running on windows.
     * https://github.com/arduino/Arduino/blob/8385aedc642d6cd76b50ac5167307121007e5045/arduino-core/src/processing/app/helpers/OSUtils.java
     */
    static public boolean isWindows() {
        //return PApplet.platform == PConstants.WINDOWS;
        return System.getProperty("os.name").contains("Windows");
    }

    /**
     * true if running on linux.
     * https://github.com/arduino/Arduino/blob/8385aedc642d6cd76b50ac5167307121007e5045/arduino-core/src/processing/app/helpers/OSUtils.java
     */
    static public boolean isLinux() {
        //return PApplet.platform == PConstants.LINUX;
        return System.getProperty("os.name").contains("Linux");
    }

    /**
     * returns true if Processing is running on a Mac OS X machine.
     * https://github.com/arduino/Arduino/blob/8385aedc642d6cd76b50ac5167307121007e5045/arduino-core/src/processing/app/helpers/OSUtils.java
     */
    static public boolean isMacOS() {
        //return PApplet.platform == PConstants.MACOSX;
        return System.getProperty("os.name").contains("Mac");
    }

    static public String getOSName() {
        return System.getProperty("os.name");
    }

    static public String getCPUArch() {
        return System.getProperty("os.arch");
    }


    /**
     * Get current program's root path.
     * @return The folder folder path without the last "\".
     */
    public static String getProgramRootPath() {
        return System.getProperty("user.dir");
    }

    public static String getFolderName(String inRootFolderPath) {
        File folder = new File(inRootFolderPath);
        return folder.getName();
    }


    public static Boolean isFileExisted(String inFilePath) {
        File file = new File(inFilePath);
        return file.exists() && !file.isDirectory();
    }

    public static Boolean isFolderExisted(String inFolderPath) {
        File file = new File(inFolderPath);
        return file.exists() && file.isDirectory();
    }

    public static void createFolder(String inFolderPath) {
        File folder = new File(inFolderPath);
        if(!folder.exists()){
            folder.mkdir();
        }
    }

    public static String currentTimeString() {
        Double time = new Double(System.currentTimeMillis() / 1000.0);

        DecimalFormat formatter = new DecimalFormat("#.##");
        return formatter.format(time);
    }

//    public static byte[] charToBytes(char[] chars) {
//        CharBuffer charBuffer = CharBuffer.wrap(chars);
//        ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
//        byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
//                byteBuffer.position(), byteBuffer.limit());
//        Arrays.fill(charBuffer.array(), '\u0000'); // clear sensitive data
//        Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
//        return bytes;
//    }

    public static byte[] charToBytes(char[] inChars) {
        byte[] b = new byte[inChars.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) inChars[i];
        }
        return b;
    }

}
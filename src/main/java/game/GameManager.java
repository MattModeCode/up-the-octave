package game;

/** A manager holding static settings and in-game values that may be modified and accessed at any time */
public class GameManager {
    /** Volume at which music will be played, range from 0.0 to 1.0 */
    private static double musicVolume = 1.0;

    /** Volume at which sound effects will be played, range from 0.0 to 1.0 */
    private static double sfxVolume = 1.0;

    /** Current number of deaths */
    private static int deaths = 0;

    /** Current time of timer in seconds */
    private static int currentTime = 0;

    /** Lowest time of timer in seconds */
    private static int bestTime = 0;

    public static double getMusicVolume() {
        return musicVolume;
    }

    public static double getSfxVolume() {
        return sfxVolume;
    }

    public static int getDeaths() {
        return deaths;
    }

    public static int getCurrentTime() {
        return currentTime;
    }

    public static int getBestTime() {
        return bestTime;
    }

    public static void setMusicVolume(double musicVolume) {
        GameManager.musicVolume = musicVolume;
    }

    public static void setSfxVolume(double sfxVolume) {
        GameManager.sfxVolume = sfxVolume;
    }

    public static void setDeaths(int deaths) {
        GameManager.deaths = deaths;
    }

    public static void setCurrentTime(int currentTime) {
        GameManager.currentTime = currentTime;
    }

    public static void setBestTime(int bestTime) {
        GameManager.bestTime = bestTime;
    }
}

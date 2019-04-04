package graphics;

public class Geometry {

    private int canvasWidth;
    private int canvasHeight;

    public Geometry(int canvasWidth, int canvasHeight) {
        this.canvasHeight = canvasHeight;
        this.canvasWidth = canvasWidth;
    }

    public int getCanvasWidth() {
        return canvasWidth;
    }

    public void setCanvasWidth(int canvasWidth) {
        this.canvasWidth = canvasWidth;
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    public void setCanvasHeight(int canvasHeight) {
        this.canvasHeight = canvasHeight;
    }

    // Fraction of the canvas width game panel at the right size occupies
    public int panelSizeFraction() {
        return 5;
    }

    public int boardWidth() {
        return getCanvasWidth() / panelSizeFraction() * (panelSizeFraction() - 1);
    }

    public int borderWidth() {
        return boardWidth() / 64;
    }

    public int boardHeight() {
        return getCanvasHeight();
    }

    public int centralBarWidth() {
        return borderWidth() * 2;
    }

    public int panelWidth() {
        return (getCanvasWidth() / panelSizeFraction()) - borderWidth();
    }

    public int spikeWidth() {
        return (boardWidth() - (borderWidth() * 4)) / 12;
    }

    public int spikeHeight() {
        int halfBoard = (boardHeight() - borderWidth() * 2) / 2;
        return halfBoard - halfBoard / 10;
    }
}

package Myth2D;

public class LevelScene extends Scene {

    public LevelScene() {
        System.out.println("Você está dentro do Level");
        Window.get().r = 1;
        Window.get().g = 1;
        Window.get().b = 1;
    }

    @Override
    public void update(float dt) {
    }
}
